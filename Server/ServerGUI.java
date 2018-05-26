package Server;

import Client.UDPClient;
import Common.FallingInRiver;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.List;
import java.awt.event.*;
import java.security.Key;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ServerGUI extends JFrame {
    private ConcurrentHashMap<Integer, FallingInRiver> map;
    private final String[] columnNames = {"ID", "Имя", "Брызги",
            "Глубина", "Цвет", "Координата X", "Координата Y",};
    private JMenuBar menuBar;
    private JMenu menu;
    private JMenuItem menuItemImport, menuItemSave;
    private JTable collectionTable;
    private JLabel idLabel, nameLabel, splashLabel, depthLabel,
            colorLabel, xLabel, yLabel;
    private JTextField idField, nameField, splashField, depthField,
            colorField, xField, yField;
    private JButton addButton, removeButton, removeLowerButton;
    private DefaultTableModel model;


    public ServerGUI(String windowName, ConcurrentHashMap<Integer, FallingInRiver> collection) {
        super(windowName);
        map = collection;
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        setSize(750, 495);
        setResizable(false);
        model = new DefaultTableModel();
        model.setColumnIdentifiers(columnNames);
        updateTable(collection);
    }

    public void init() {
        setVisible(false);
        getContentPane().removeAll();

        //Верхняя менюшка
        menuBar = new JMenuBar();
        menu = new JMenu("Коллекция");
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
                        pathField.setPreferredSize(new Dimension(width-10, 20));
                        JButton innerImportButton = new JButton("Импорт");
                        innerImportButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                String path;
                                if (pathField.getText().equals("default") || pathField.getText().equals("Default"))
                                     path = "C:/Users/Daniil/iCloudDrive/ИТМО/1 курс/2 семестр/Лабы/Програмированние/Lab6/src/d.json"; // Это нужно менять на другой платформе
                                else path = "\"" + pathField.getText() + "\"";
                                Commands.importCHM(map, path);
                                updateTable(map);
                                setVisible(false);
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
                Commands.save(map, "C:/Users/Daniil/iCloudDrive/ИТМО/1 курс/2 семестр/Лабы/Програмированние/Lab6/src/d.json");
            }
        });
        menuBar.add(menu);
        menu.add(menuItemImport);
        menu.add(menuItemSave);
        setJMenuBar(menuBar);


        //Таблица
        collectionTable = new JTable(model); // После чего заполнить таблицу
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
                        color = "YELLOW";
                }
                Commands.addFall(map, "\"id\":" + idField.getText() + ",\"charName\":\"" + nameField.getText() + "\",\"splashLvl\":" + splashField.getText() +
                        ",\"depth\":" + depthField.getText() + ",\"color\":\"" + color + "\",\"x\":" + xField.getText() + ",\"y\":" + yField.getText());
                updateTable(map);
            }
        });
        removeButton = new JButton("Удалить элемент");
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Commands.remove(map, idField.getText());
                updateTable(map);
            }
        });
        removeButton.setEnabled(false);
        removeLowerButton = new JButton("Удалить все с ключом меньше");
        removeLowerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Commands.remove_lower(map, idField.getText());
                updateTable(map);
            }
        });

        //ненужная кнопка обновить"
       /* removeLowerButton.setEnabled(false);
        updateButton = new JButton("Обновить");
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        }); */


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
        setVisible(true);
    }

    //Обновитель таблицы. Делаем массив из элементов коллекции и изменяем модель таблицы. Таблица меняется сама
    public void updateTable(ConcurrentHashMap<Integer, FallingInRiver> collection) {
        Object[] row = new Object[7];
        model.setRowCount(0);
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
            model.addRow(row);
            for (int i = 0; i < 7; i++)
                row[i] = null;
        }
    }
}
/* Что нужно сделать
   Проверить таблицу на много элементов
   ексепшены
   Добавить контроль за людьми

   Имеет смысл брызги и прочее сделать слайдерами а не текстовыми полями ну хз
 */