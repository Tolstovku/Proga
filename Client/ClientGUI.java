package Client;

import Common.FallingInRiver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class ClientGUI extends JFrame {
    private ReentrantLock lock = new ReentrantLock();
    private int count;
    private ArrayList<FallCircle> allCircles = new ArrayList<>();
    private JLayeredPane lpane;
    private ConcurrentHashMap<Integer, FallingInRiver> collection;
    private final int frameWidth = 850, frameHeight = 700;
    private Graph g;
    private JButton startButton, stopButton, updateButton, languageButton;
    private JRadioButton orangeRadio, blueRadio, redRadio, yellowRadio;
    private ButtonGroup buttonGroup;
    private JFormattedTextField nameField;
    private JLabel minXLabel, maxXLabel, minYLabel, maxYLabel,
            minSplashLabel, maxSplashLabel, minDepthLabel, maxDepthLabel;
    private JSlider minXSlider, maxXSlider, minYSlider, maxYSlider;
    private JSpinner minSplash, maxSplash, minDepth, maxDepth;

    //Все для эффекта исчезания
    private int opacityCount = 255;
    private boolean fadeInOver;
    private boolean fadeAllOver;
    private ArrayList<FallCircle> filteredCircles = new ArrayList<>();
    //Очень хитро... таймер для эффекта исчезания
    private Timer fadeTimer = new Timer(10, new TimerListener());

    private ResourceBundle bundle = ResourceBundle.getBundle("Common.Resources.Resource");

    public ClientGUI() { // добавить сюда мапу
        setTitle(bundle.getString("client"));
            ConnectionHandler.getMap();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        setSize(frameWidth, frameHeight);
        setResizable(false);
        g = new Graph(40);
        g.setPreferredSize(new Dimension(801, 400));
        g.setMinimumSize(g.getPreferredSize());
    }


    public void init() {
        setVisible(false);
        getContentPane().removeAll();


        //Кнопки старт | стоп | обновить | помощь [?]
        startButton = new JButton(bundle.getString("start"));
        startButton.setPreferredSize(new Dimension(80, 30));
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startButton.setEnabled(false);
                stopButton.setEnabled(true);
                lock.lock();
                fadeEffect();

            }
        });
        stopButton = new JButton(bundle.getString("stop"));
        stopButton.setPreferredSize(new Dimension(80, 30));
        stopButton.setEnabled(false);
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopButton.setEnabled(false);
                startButton.setEnabled(true);
                fadeTimer.stop();
                for (FallCircle circle : filteredCircles) {
                    circle.setOpacity(255);
                    circle.repaint();
                    g.revalidate();
                    g.repaint();
                }
                lock.unlock();
            }
        });
        updateButton = new JButton(bundle.getString("update"));
        updateButton.setPreferredSize(new Dimension(90, 30));
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ((int) maxSplash.getValue() == 13377331) {
                         ConnectionHandler.serverShutdown();
                        JOptionPane.showMessageDialog(g, "Введена секретная комбинация. Сервер остановлен", "( ͡° ͜ʖ ͡°)", JOptionPane.WARNING_MESSAGE); // на случай, если нужно будет запускать на гелиосе и останавливать сервер с клиента.
                }
                else {
                        ConnectionHandler.getMap();
                }
            }
        });


        languageButton = new JButton(bundle.getString("language"));
        languageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new JFrame() {
                    {
                        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                        setSize(180, 230);
                        setLocationRelativeTo(null);
                        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
                        setResizable(false);
                        JLabel msg = new JLabel(bundle.getString("chooseLanguage"));

                        JButton russian = new JButton("Русский");
                        russian.setPreferredSize(new Dimension(150, 25));
                        russian.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                changeLanguage("ru");
                                setVisible(false);
                                dispose();
                            }
                        });
                        JButton netherlands = new JButton("Nederlandse");
                        netherlands.setPreferredSize(new Dimension(150, 25));
                        netherlands.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                changeLanguage("nl");
                                setVisible(false);
                                dispose();
                            }
                        });
                        JButton cathalonic = new JButton("Catalaanse");
                        cathalonic.setPreferredSize(new Dimension(150, 25));
                        cathalonic.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                changeLanguage("ca");
                                setVisible(false);
                                dispose();
                            }
                        });
                        JButton spanish = new JButton("Español");
                        spanish.setPreferredSize(new Dimension(150, 25));
                        spanish.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                changeLanguage("es", "PR");
                                setVisible(false);
                                dispose();
                            }
                        });


                        add(msg);
                        add(russian);
                        add(netherlands);
                        add(cathalonic);
                        add(spanish);
                        setVisible(true);

                    }
                };
            }
        });
        //***ФИЛЬТРЫ***
        //Радио кнопки
        orangeRadio = new JRadioButton(bundle.getString("orange"));
        orangeRadio.setActionCommand(orangeRadio.getText()); // это нужно чтобы потом вычислить нажатую
        blueRadio = new JRadioButton(bundle.getString("blue"));
        blueRadio.setActionCommand(blueRadio.getText());
        redRadio = new JRadioButton(bundle.getString("red"));
        redRadio.setActionCommand(redRadio.getText());
        yellowRadio = new JRadioButton(bundle.getString("yellow"));
        yellowRadio.setActionCommand(yellowRadio.getText());
        buttonGroup = new ButtonGroup();
        buttonGroup.add(orangeRadio);
        buttonGroup.add(blueRadio);
        buttonGroup.add(redRadio);
        buttonGroup.add(yellowRadio);

        //Подписи к слайдерам
        minXLabel = new JLabel(bundle.getString("minX"), SwingConstants.CENTER);
        minXLabel.setPreferredSize(new Dimension((int) (frameWidth / 2 - frameWidth * 0.15 - 100), 25));
        maxXLabel = new JLabel(bundle.getString("maxX"), SwingConstants.CENTER);
        maxXLabel.setPreferredSize(new Dimension((int) (frameWidth / 2 - frameWidth * 0.15 - 100), 25));
        minYLabel = new JLabel(bundle.getString("minY"), SwingConstants.CENTER);
        minYLabel.setPreferredSize(new Dimension((int) (frameWidth / 2 - frameWidth * 0.15 - 100), 25));
        maxYLabel = new JLabel(bundle.getString("maxY"), SwingConstants.CENTER);
        maxYLabel.setPreferredSize(new Dimension((int) (frameWidth / 2 - frameWidth * 0.15) - 100, 25));

        //Слайдеры
        minXSlider = new JSlider(0, (int) g.getMinimumSize().getWidth(), 0); // Все значения тут добыты экспериментальным путем
        minXSlider.setMinorTickSpacing(40);
        minXSlider.setMajorTickSpacing(160);
        minXSlider.setPaintLabels(true);
        minXSlider.setPaintTicks(true);
        maxXSlider = new JSlider(0, (int) g.getMinimumSize().getWidth(), 20*g.getCellSize());
        maxXSlider.setMinorTickSpacing(40);
        maxXSlider.setMajorTickSpacing(160);
        maxXSlider.setPaintLabels(true);
        maxXSlider.setPaintTicks(true);
        minYSlider = new JSlider(0, (int) g.getMinimumSize().getHeight(), 0);
        minYSlider.setMinorTickSpacing(20);
        minYSlider.setMajorTickSpacing(80);
        minYSlider.setPaintLabels(true);
        minYSlider.setPaintTicks(true);
        maxYSlider = new JSlider(0, (int) g.getMinimumSize().getHeight(), 10*g.getCellSize());
        maxYSlider.setMinorTickSpacing(20);
        maxYSlider.setMajorTickSpacing(80);
        maxYSlider.setPaintLabels(true);
        maxYSlider.setPaintTicks(true);

        //Подписи к спиннерам
        minSplashLabel = new JLabel(bundle.getString("minSplash"));
        maxSplashLabel = new JLabel(bundle.getString("maxSplash"));
        minDepthLabel = new JLabel(bundle.getString("minDepth"));
        maxDepthLabel = new JLabel(bundle.getString("maxDepth"));


        //Спиннеры xDDDDDDDDDD
        minSplash = new JSpinner();
        minSplash.setPreferredSize(new Dimension(70, 30));
        maxSplash = new JSpinner();
        maxSplash.setPreferredSize(new Dimension(70, 30));
        minDepth = new JSpinner();
        minDepth.setPreferredSize(new Dimension(70, 30));
        maxDepth = new JSpinner();
        maxDepth.setPreferredSize(new Dimension(70, 30));


        //Добавляем обьекты
        g.setLayout(null);
        add(g);
        drawCircles();

        add(Box.createRigidArea(new Dimension((int) (frameWidth / 2 - frameWidth * 0.3), 30))); // Значения все так же вычислены экспериметально
        add(startButton);
        add(Box.createRigidArea(new Dimension(45, 30)));
        add(stopButton);
        add(Box.createRigidArea(new Dimension(45, 30)));
        add(updateButton);
        add(Box.createRigidArea(new Dimension((int) (frameWidth / 2 - frameWidth * 0.3), 30)));
        add(languageButton);

        add(Box.createRigidArea(new Dimension((int) (frameWidth / 2 - frameWidth * 0.235), 30)));
        add(orangeRadio);
        add(blueRadio);
        add(redRadio);
        add(yellowRadio);
        add(Box.createRigidArea(new Dimension((int) (frameWidth / 2 - frameWidth * 0.235), 30)));

        add(minXLabel);
        add(maxXLabel);
        add(minYLabel);
        add(maxYLabel);
        add(minXSlider);
        add(maxXSlider);
        add(minYSlider);
        add(maxYSlider);
        add((Box.createRigidArea(new Dimension((frameWidth), 3))));
        add(minSplashLabel);
        add(minSplash);
        add(maxSplashLabel);
        add(maxSplash);
        add((Box.createRigidArea(new Dimension(50, 3))));
        add(minDepthLabel);
        add(minDepth);
        add(maxDepthLabel);
        add(maxDepth);
        setVisible(true);
    }

    private void drawCircles() {
        try {
            //Удаляем старые круги
            for (FallCircle circle : allCircles) {
                g.remove(circle);
            }
            allCircles.clear();
            //Рисуем новые
            for (FallingInRiver fall : collection.values()) {
                FallCircle circle = new FallCircle(fall);
                g.add(circle);
                allCircles.add(circle); // Лист нам необходим для того, чтобы потом находить круги по фильтрам.
            }
            g.revalidate();
            g.repaint();
        } catch (Exception e) {
        }
    }

    public void updateCollection(ConcurrentHashMap<Integer, FallingInRiver> newCollection) {
        lock.lock();
        collection = newCollection;
        drawCircles();
        lock.unlock();
    }

    //Эффект исчезания.
    private void fadeEffect() {
        opacityCount = 255;
        filteredCircles.clear();
        //Проверка на радиокнопки
        if (buttonGroup.getSelection() == null) {
            stopButton.setEnabled(false);
            startButton.setEnabled(true);
            JOptionPane.showMessageDialog(g, bundle.getString("noColor"), bundle.getString("error"), JOptionPane.ERROR_MESSAGE);
            lock.unlock();
            return;
        }
        for (FallCircle circle : allCircles) {

            FallingInRiver fall = circle.element;
            if (fall.getColor().toString().equals(buttonGroup.getSelection().getActionCommand()) && fall.getX() >= minXSlider.getValue() && fall.getX() <= maxXSlider.getValue() &&
                    fall.getY() >= minYSlider.getValue() && fall.getY() <= maxYSlider.getValue() && fall.getSplashLvl() >= (int) minSplash.getValue() &&
                    fall.getSplashLvl() <= (int) maxSplash.getValue() && fall.getDepth() >= (int) minDepth.getValue() && fall.getDepth() <= (int) maxDepth.getValue())
                filteredCircles.add(circle);
        }
        if (!filteredCircles.isEmpty())
            fadeTimer.start();
        else {
            stopButton.setEnabled(false);
            startButton.setEnabled(true);
            JOptionPane.showMessageDialog(g, bundle.getString("noFilteredObjects"));
            lock.unlock();
        }
        fadeInOver = false;
        fadeAllOver = false;
    }


    class TimerListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            //Сначала Исчезает
            if ((opacityCount > 0) && !fadeInOver) {
                for (FallCircle circle : filteredCircles) {
                    circle.decrementOpacity();
                    circle.repaint();
                    g.revalidate();
                    g.repaint();
                    if (opacityCount == 1) fadeInOver = true;
                }
                opacityCount--;
            }
            //Потом появляется
            if ((opacityCount < 255) && (fadeInOver)) {
                for (FallCircle circle : filteredCircles) {
                    circle.incrementOpacity();
                    circle.repaint();
                    g.revalidate();
                    g.repaint();

                    if (opacityCount == 254) fadeAllOver = true;
                }
                opacityCount++;
            }
            //Таймер останавливается
            if (fadeAllOver) {
                fadeTimer.stop();
                stopButton.setEnabled(false);
                startButton.setEnabled(true);
                lock.unlock();
            }


        }
    }


    public void showMessage(String message){
        JOptionPane.showMessageDialog(g, bundle.getString(message), bundle.getString("message"), JOptionPane.INFORMATION_MESSAGE);
    }


    private void changeLanguage(String language, String country) {
        bundle=ResourceBundle.getBundle("Common.Resources.Resource", new Locale(language, country));
        startButton.setText(bundle.getString("start"));
        stopButton.setText(bundle.getString("stop"));
        updateButton.setText(bundle.getString("update"));
        orangeRadio.setText(bundle.getString("orange"));
        blueRadio.setText(bundle.getString("blue"));
        redRadio.setText(bundle.getString("red"));
        yellowRadio.setText(bundle.getString("yellow"));
        minXLabel.setText(bundle.getString("minX"));
        maxXLabel.setText(bundle.getString("maxX"));
        minYLabel.setText(bundle.getString("minY"));
        maxYLabel.setText(bundle.getString("maxY"));
        minSplashLabel.setText(bundle.getString("minSplash"));
        maxSplashLabel.setText(bundle.getString("maxSplash"));
        minDepthLabel.setText(bundle.getString("minDepth"));
        maxDepthLabel.setText(bundle.getString("maxDepth"));
        setTitle(bundle.getString("client"));



        setTitle(bundle.getString("client"));
    }

    private void changeLanguage(String language) {
        bundle = ResourceBundle.getBundle("Common.Resources.Resource", new Locale(language));
        startButton.setText(bundle.getString("start"));
        stopButton.setText(bundle.getString("stop"));
        updateButton.setText(bundle.getString("update"));
        orangeRadio.setText(bundle.getString("orange"));
        blueRadio.setText(bundle.getString("blue"));
        redRadio.setText(bundle.getString("red"));
        yellowRadio.setText(bundle.getString("yellow"));
        minXLabel.setText(bundle.getString("minX"));
        maxXLabel.setText(bundle.getString("maxX"));
        minYLabel.setText(bundle.getString("minY"));
        maxYLabel.setText(bundle.getString("maxY"));
        minSplashLabel.setText(bundle.getString("minSplash"));
        maxSplashLabel.setText(bundle.getString("maxSplash"));
        minDepthLabel.setText(bundle.getString("minDepth"));
        maxDepthLabel.setText(bundle.getString("maxDepth"));
        languageButton.setText(bundle.getString("language"));
        setTitle(bundle.getString("client"));



        setTitle(bundle.getString("client"));
    }
}


// Что нужно сделать
// Сделать постоянный слушатель чтобы сообщение о бане выскакивало сразу с таймаутом


