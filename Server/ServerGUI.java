package Server;

import Common.FallingInRiver;
import Server.CommandPattern.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ServerGUI extends JFrame {

    private String savePath = "C:/Users/Daniil/iCloudDrive/ИТМО/1 курс/2 семестр/Лабы/Програмированние/Lab6/src/d.json";
    private final String[] collectionColumnNames = {"ID", "Имя", "Брызги",
            "Глубина", "Цвет", "Координата X", "Координата Y",}, usersColumnNames = {"IP Адресс", "Порт", "Бан"};
    private JMenuBar menuBar;
    private JMenu controlMenu;
    private JMenuItem menuItemImport, menuItemSave, menuUsers;
    private JTable collectionTable, usersTable;
    private JLabel idLabel, nameLabel, splashLabel, depthLabel,
            colorLabel, xLabel, yLabel;
    private JTextField idField, nameField, splashField, depthField,
            colorField, xField, yField;
    private JButton addButton, removeButton, removeLowerButton, helpButton, undoButton, redoButton;
    private DefaultTableModel collectionModel, usersModel;
    private ArrayList<User> usersList = new ArrayList<>();
    private Stack<Undoable> undoStack = new Stack<>();
    private Stack<Undoable> redoStack = new Stack<>();
    private Executor executor = new Executor();
    private DatagramSocket socket;

    public ServerGUI(String windowName, DatagramSocket socket) {
        super(windowName);
        this.socket = socket;
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        setSize(750, 495);
        setResizable(false);
        collectionModel = new DefaultTableModel();
        collectionModel.setColumnIdentifiers(collectionColumnNames);
        updateTable(SingletonCollection.getCollection());
        usersModel = new DefaultTableModel();
        usersModel.setColumnIdentifiers(usersColumnNames);
    }

    public void init() {
        setVisible(false);
        getContentPane().removeAll();

        //Верхняя менюшка
        menuBar = new JMenuBar();
        controlMenu = new JMenu("Управление");
        menuItemImport = new JMenuItem("Импорт");
        menuItemImport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new JFrame("Импорт") {
                    {
                        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                        int width = 750;
                        setLocationRelativeTo(null);
                        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
                        setSize(width, 135);
                        setResizable(false);
                        JLabel msg = new JLabel("Введите путь к файлу импорта:");
                        JTextField pathField = new JTextField();
                        pathField.setPreferredSize(new Dimension(width - 10, 20));
                        JButton innerImportButton = new JButton("Импорт");
                        innerImportButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                String path;
                                if (pathField.getText().equals("default") || pathField.getText().equals("Default"))
                                    path = "\"/Users/daniil/Library/Mobile Documents/com~apple~CloudDocs/ИТМО/1 курс/2 семестр/Лабы/Програмированние/Lab6/out/production/Lab6/Server/d.json\"   "; // Это нужно менять на другой платформе
                                else path = "\"" + pathField.getText() + "\"";

                                setVisible(false);
                                executor.configure(new ImportCommand(), path);
                                executor.execute();
                                dispose();

                            }
                        });
                        add(msg);
                        add(pathField);
                        add(innerImportButton);
                        setVisible(true);
                    }

                };
            }
        });
        menuItemSave = new JMenuItem("Сохранить");
        menuItemSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                executor.configure(new SaveCommand(), savePath);
                executor.execute();
            }
        });
        menuUsers = new JMenuItem("Подключения");
        menuUsers.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new UsersFrame();
            }
        });
        menuBar.add(controlMenu);
        controlMenu.add(menuItemImport);
        controlMenu.add(menuItemSave);
        controlMenu.add(menuUsers);
        setJMenuBar(menuBar);


        //Таблица
        collectionTable = new JTable(collectionModel); // После чего заполнить таблицу
        collectionTable.setMinimumSize(getSize());
        collectionTable.getTableHeader().setReorderingAllowed(false);
        collectionTable.getTableHeader().setResizingAllowed(false);
        collectionTable.setEnabled(false);
        collectionTable.setBackground(Color.white);
        collectionTable.getColumnModel().getColumn(0).setPreferredWidth(20);
        collectionTable.getColumnModel().getColumn(1).setPreferredWidth(60);
        collectionTable.getColumnModel().getColumn(2).setPreferredWidth(20);
        collectionTable.getColumnModel().getColumn(3).setPreferredWidth(17);
        collectionTable.getColumnModel().getColumn(4).setPreferredWidth(50);
        collectionTable.getColumnModel().getColumn(5).setPreferredWidth(50);
        collectionTable.getColumnModel().getColumn(6).setPreferredWidth(50);
        JScrollPane scrollPane = new JScrollPane(collectionTable);
        scrollPane.getViewport().setBackground(Color.white);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(getSize().width - 10, 300));
//
        //Подписи к полям ввода
        idLabel = new JLabel("ID", SwingConstants.CENTER);
        idLabel.setPreferredSize(new Dimension(50, 20));
        //idLabel.setBorder(new LineBorder(Color.black));

        nameLabel = new JLabel("Имя", SwingConstants.CENTER);
        nameLabel.setPreferredSize(new Dimension(150, 20));
        //nameLabel.setBorder(new LineBorder(Color.black));

        splashLabel = new JLabel("Брызги", SwingConstants.CENTER);
        splashLabel.setPreferredSize(new Dimension(60, 20));
        //splashLabel.setBorder(new LineBorder(Color.black));

        depthLabel = new JLabel("Глубина", SwingConstants.CENTER);
        depthLabel.setPreferredSize(new Dimension(60, 20));
        //depthLabel.setBorder(new LineBorder(Color.black));

        colorLabel = new JLabel("Цвет", SwingConstants.CENTER);
        colorLabel.setPreferredSize(new Dimension(150, 20));
        //colorLabel.setBorder(new LineBorder(Color.black));

        xLabel = new JLabel("Координата X", SwingConstants.CENTER);
        xLabel.setPreferredSize(new Dimension(100, 20));
        //xLabel.setBorder(new LineBorder(Color.black));

        yLabel = new JLabel("Координата Y", SwingConstants.CENTER);
        yLabel.setPreferredSize(new Dimension(100, 20));
        //yLabel.setBorder(new LineBorder(Color.black));


        //Поля ввода
        //Адаптер для того, чтобы, пока все нужные поля не были заполнены, кнопки не работали
        KeyAdapter textCheck = new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                if (idField.getText().length() > 0) {
                    removeButton.setEnabled(true);
                    removeLowerButton.setEnabled(true);
                    if (nameField.getText().length() > 0 && splashField.getText().length() > 0 &&
                            depthField.getText().length() > 0 && colorField.getText().length() > 0 && xField.getText().length() > 0 && yField.getText().length() > 0) {
                        addButton.setEnabled(true);
                    } else
                        addButton.setEnabled(false);
                } else {
                    removeButton.setEnabled(false);
                    removeLowerButton.setEnabled(false);
                }
            }
        };
        idField = new JTextField();
        idField.setPreferredSize(new Dimension(50, 20));
        idField.addKeyListener(textCheck);

        nameField = new JTextField();
        nameField.setPreferredSize(new Dimension(150, 20));
        nameField.addKeyListener(textCheck);

        splashField = new JTextField();
        splashField.setPreferredSize(new Dimension(60, 20));
        splashField.addKeyListener(textCheck);

        depthField = new JTextField();
        depthField.setPreferredSize(new Dimension(60, 20));
        depthField.addKeyListener(textCheck);

        colorField = new JTextField();
        colorField.setPreferredSize(new Dimension(150, 20));
        colorField.addKeyListener(textCheck);

        xField = new JTextField();
        xField.setPreferredSize(new Dimension(100, 20));
        xField.addKeyListener(textCheck);

        yField = new JTextField();
        yField.setPreferredSize(new Dimension(100, 20));
        yField.addKeyListener(textCheck);

        //Кнопки
        addButton = new JButton("Добавить элемент");
        addButton.setEnabled(false);
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String color;
                switch (colorField.getText()) {
                    case "Оранжевый":
                    case "оранжевый":
                        color = "ORANGE";
                        break;
                    case "Синий":
                    case "синий":
                        color = "BLUE";
                        break;
                    case "Красный":
                    case "красный":
                        color = "RED";
                        break;
                    case "Желтый":
                    case "желтый":
                        color = "YELLOW";
                        break;
                    default:
                        color = "UNDEFINED";
                }
                AddCommand command = new AddCommand();
                String params = "\"id\":" + idField.getText() + ",\"charName\":\"" + nameField.getText() + "\",\"splashLvl\":" + splashField.getText() +
                        ",\"depth\":" + depthField.getText() + ",\"color\":\"" + color + "\",\"x\":" + xField.getText() + ",\"y\":" + yField.getText();
                executor.configure(new AddCommand(), params);
                executor.execute();
            }
        });
        removeButton = new JButton("Удалить элемент");
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String params = idField.getText();
                executor.configure(new RemoveCommand(), params);
                executor.execute();
            }
        });
        removeButton.setEnabled(false);
        removeLowerButton = new JButton("Удалить все с ключом меньше");
        removeLowerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String params = idField.getText();
                executor.configure(new RemoveLowerCommand(), params);
                executor.execute();
            }
        });
        removeLowerButton.setEnabled(false);

        helpButton = new JButton();
        helpButton.setIcon(UIManager.getIcon("OptionPane.questionIcon"));
        helpButton.setIconTextGap(0);
        helpButton.setHorizontalAlignment(SwingConstants.LEADING);
        helpButton.setBorder(BorderFactory.createEmptyBorder(4, 4, 2, 20));
        helpButton.setPreferredSize(new Dimension(36, 30));
        helpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPopupMenu infoMenu = new JPopupMenu();
                JLabel info = new JLabel("<html>Формат добавления объекта:<br/><br/>" +
                        "ID - число<br/>" +
                        "Брызги - число 1 .. 10<br/>" +
                        "Глубина - число 1 .. 10<br/>" +
                        "Цвет - Синий / Желтый / Красный / Оранжевый<br/>" +
                        "Координата X - число 0 .. 800<br/>" +
                        "Координата Y - число 0 .. 400</html>");
                // info.setPreferredSize(new Dimension(500,500));
                info.setFont(new Font("Arial", Font.PLAIN, 18));
                Border paddingBorder = BorderFactory.createEmptyBorder(10, 10, 10, 10);
                Border border = BorderFactory.createLineBorder(Color.BLACK, 1);
                info.setBorder(BorderFactory.createCompoundBorder(border, paddingBorder));
                infoMenu.setBorder(BorderFactory.createLineBorder(Color.black, 1, true));
                infoMenu.add(info);
                infoMenu.show(collectionTable, collectionTable.getWidth() / 4 - 30, 20);
            }
        });

        undoButton = new JButton();
        undoButton.setIcon(new ImageIcon("C:/Users/Daniil/iCloudDrive/ИТМО/1 курс/2 семестр/Лабы/Програмированние/Lab6/src/Server/Icons/undo.png"));
        undoButton.setIconTextGap(0);
        undoButton.setHorizontalAlignment(SwingConstants.LEADING);
        undoButton.setBorder(BorderFactory.createEmptyBorder(4, 4, 2, 20));
        undoButton.setPreferredSize(new Dimension(40, 30));
        undoButton.setFocusPainted(false);
        undoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!undoStack.empty()) {
                    Undoable command = undoStack.pop();
                    SingletonCollection.setCollection(command.undo());
                    updateTable(SingletonCollection.getCollection());
                    redoStack.push(command);
                    sendCollectionToAll();
                }
            }
        });

        redoButton = new JButton();
        redoButton.setIcon(new ImageIcon("C:/Users/Daniil/iCloudDrive/ИТМО/1 курс/2 семестр/Лабы/Програмированние/Lab6/src/Server/Icons/redo.png"));
        redoButton.setIconTextGap(0);
        redoButton.setHorizontalAlignment(SwingConstants.LEADING);
        redoButton.setBorder(BorderFactory.createEmptyBorder(4, 4, 2, 20));
        redoButton.setPreferredSize(new Dimension(40, 30));
        redoButton.setFocusPainted(false);
        redoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!redoStack.empty()) {
                    Command command = (Command) redoStack.pop();
                    executor.configure(command, command.getParams());
                    executor.setToClearUndoStack(false);
                    executor.execute();
                    updateTable(SingletonCollection.getCollection());
                    undoStack.push((Undoable) command);
                    sendCollectionToAll();
                }
            }
        });


        //Добавление элементов
        add(scrollPane);

        add(idLabel);
        add(nameLabel);
        add(splashLabel);
        add(depthLabel);
        add(colorLabel);
        add(xLabel);
        add(yLabel);

        add(idField);
        add(nameField);
        add(splashField);
        add(depthField);
        add(colorField);
        add(xField);
        add(yField);
        add(Box.createRigidArea(new Dimension(710, 5))); //Просто пустое место
        add(addButton);
        add(Box.createRigidArea(new Dimension(7, 1)));
        add(removeButton);
        add(Box.createRigidArea(new Dimension(7, 1)));
        add(removeLowerButton);
        add(helpButton);
        add(undoButton);
        add(redoButton);
        setVisible(true);
    }

    //Обновитель таблицы. Делаем массив из элементов коллекции и изменяем модель таблицы. Таблица меняется сама
    public void updateTable(ConcurrentHashMap<Integer, FallingInRiver> collection) {
        Object[] row = new Object[7];
        collectionModel.setRowCount(0);
        Iterator<Map.Entry<Integer, FallingInRiver>> iter = collection.entrySet().iterator();
        while (iter.hasNext()) {
            FallingInRiver fall = iter.next().getValue();
            row[0] = Integer.toString(fall.getId());
            row[1] = fall.getCharName();
            row[2] = fall.getSplashLvl();
            row[3] = fall.getDepth();
            row[4] = fall.getColor().toString();
            row[5] = fall.getX();
            row[6] = fall.getY();
            collectionModel.addRow(row);
            for (int i = 0; i < 7; i++)
                row[i] = null;
        }
    }


    //Таблице юзеров
    public class UsersFrame extends JFrame {
        UsersFrame() {
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            int width = 650;
            setLocationRelativeTo(collectionTable);
            setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
            setSize(width, 400);
            setResizable(false);

            //Таблица юзеров
            usersTable = new JTable(usersModel);
            usersTable.setMinimumSize(new Dimension(200, 276));
            usersTable.setPreferredSize(new Dimension(200, 276));
            usersTable.getTableHeader().setReorderingAllowed(false);
            usersTable.getTableHeader().setResizingAllowed(false);
            usersTable.setBackground(Color.white);
            usersTable.setEnabled(false);
            usersTable.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent event) {
                    if (event.getButton() == MouseEvent.BUTTON3) {
                        super.mousePressed(event);
                        Point point = event.getPoint();
                        int column = usersTable.columnAtPoint(point);
                        int row = usersTable.rowAtPoint(point);
                        try {
                            usersTable.setColumnSelectionInterval(column, column);
                            usersTable.setRowSelectionInterval(row, row);
                        } catch (Exception e) {
                            return;
                        }
                        new PopUp(row, usersTable).show((Component) event.getSource(), event.getX(), event.getY());
                    } else {
                        Point point = event.getPoint();
                        int column = usersTable.columnAtPoint(point);
                        int row = usersTable.rowAtPoint(point);
                        try {
                            usersTable.setColumnSelectionInterval(column, column);
                            usersTable.setRowSelectionInterval(row, row);
                        } catch (Exception e) {
                        }
                    }
                }

            });
            usersTable.getColumnModel().getColumn(0).setPreferredWidth(30);
            usersTable.setVisible(true);

            JScrollPane usersScrollPane = new JScrollPane(usersTable);
            usersScrollPane.getViewport().setBackground(Color.white);
            usersScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            usersScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
            usersScrollPane.setPreferredSize(new Dimension(getSize().width - 10, 300));
            add(usersScrollPane);

            setVisible(true);
        }
    }


    //Методы для работы с таблицей юзеров
    public void updateUsersTable() {
        Object[] row = new Object[3];
        usersModel.setRowCount(0);
        for (User user : usersList) {
            row[0] = user.getAddress().toString().substring(1);
            row[1] = user.getPort();
            row[2] = user.isBanned();
            usersModel.addRow(row);
            for (int i = 0; i < 3; i++)
                row[i] = null;
        }
    }

    public void addUser(User user) {
        if (!usersList.contains(user))
            usersList.add(user);
    }

    public ArrayList<User> getUsersList() {
        return usersList;
    }


    //Менюшка при нажатии правой кнопкой по строчке в таблице
    class PopUp extends JPopupMenu {
        JMenuItem ban, unban;

        public PopUp(int row, JTable table) {
            ban = new JMenuItem("Забанить");
            ban.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        boolean isBannedBefore, isBannedAfter;
                        InetAddress inetAddress = InetAddress.getByName(table.getValueAt(row, 0).toString());
                        int port = (int) table.getValueAt(row, 1);
                        User user = new User(inetAddress, port, true);
                        if (!usersList.get(usersList.indexOf(user)).isBanned()==user.isBanned())
                            sendBanMessage(user, true);
                        usersList.remove(user);
                        usersList.add(user);
                        updateUsersTable();
                    } catch (Exception excep) {
                        excep.printStackTrace();
                    }
                }
            });
            unban = new JMenuItem("Разбанить");
            unban.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        InetAddress inetAddress = InetAddress.getByName(table.getValueAt(row, 0).toString());
                        int port = (int) table.getValueAt(row, 1);
                        User user = new User(inetAddress, port, false);
                        if (!usersList.get(usersList.indexOf(user)).isBanned()==user.isBanned())
                            sendBanMessage(user, false);
                        usersList.remove(user);
                        usersList.add(user);
                        updateUsersTable();
                    } catch (Exception excep) {
                        excep.printStackTrace();
                    }
                }
            });

            add(ban);
            add(unban);
        }
    }

    /* Паттерн стратегия для выполнения команд.
    *  Executor настраивается добавлением обьекта исполняемой команды и какого-либо String параметра, после чего запускается через execute.
    */
    private class Executor {
        private Command command;
        private String params;
        private boolean toClearUndoStack;

        public void configure(Command command, String params) {
            this.command = command;
            this.params = params;
        }

        public void setToClearUndoStack(boolean bool) {
            toClearUndoStack = bool;
        }

        public void execute() {
            if (command != null && params != null) {
                command.setParams(params);
                Command.Feedback feedback = command.execute(SingletonCollection.getCollection(), params);
                String resultMessage = feedback.message;
                pushToHistory(command, feedback);
                JOptionPane.showMessageDialog(collectionTable, resultMessage, "Сообщение", JOptionPane.INFORMATION_MESSAGE);
                if (feedback.wasExecuted) {
                    updateTable(SingletonCollection.getCollection());
                    if(command instanceof Undoable)
                    sendCollectionToAll();
                }
            } else throw new RuntimeException("Executor is not configured");
            if (toClearUndoStack) redoStack.clear();
            setToClearUndoStack(true);
        }
    }

    private void pushToHistory(Command command, Command.Feedback feedback) {
        if (feedback.wasExecuted && command instanceof Undoable) undoStack.push((Undoable) command);
    }

    //Мне впадлу делать нормальную отправку сообщение поэтому тут как будто принимается сообщение юзером
    private void sendCollectionToAll() {
        for (User user : usersList) {
            byte[] message = "start".getBytes();
            if (!user.isBanned()) {
                DatagramPacket fakeReceivedPacket = new DatagramPacket(message, message.length, user.getAddress(), user.getPort());
                new CommandExecutor(fakeReceivedPacket, socket, SingletonCollection.getCollection(), false).start();
            }

        }
    }

    private void sendBanMessage(User user, boolean bannedOrUnbanned) {
        byte[] message;
        if (bannedOrUnbanned)
            message = "start".getBytes();
        else
            message = "unban".getBytes();
        DatagramPacket fakeReceivedPacket = new DatagramPacket(message, message.length, user.getAddress(), user.getPort());
        new CommandExecutor(fakeReceivedPacket, socket, SingletonCollection.getCollection(), user.isBanned()).start();
    }
}


/* Много юзеров нет скролбара
 Пишет заблокировано если не смог приконектиться
 */