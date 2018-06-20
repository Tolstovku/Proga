package Client;

import Common.FallingInRiver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
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
    private JButton startButton, stopButton, updateButton;
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


    public ClientGUI(String windowName) { // добавить сюда мапу
        super(windowName);
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
        startButton = new JButton("Старт");
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
        stopButton = new JButton("Стоп");
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
        updateButton = new JButton("Обновить");
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

        //***ФИЛЬТРЫ***
        //Радио кнопки
        orangeRadio = new JRadioButton("Оранжевый");
        orangeRadio.setActionCommand(orangeRadio.getText()); // это нужно чтобы потом вычислить нажатую
        blueRadio = new JRadioButton("Синий");
        blueRadio.setActionCommand(blueRadio.getText());
        redRadio = new JRadioButton("Красный");
        redRadio.setActionCommand(redRadio.getText());
        yellowRadio = new JRadioButton("Желтый");
        yellowRadio.setActionCommand(yellowRadio.getText());
        buttonGroup = new ButtonGroup();
        buttonGroup.add(orangeRadio);
        buttonGroup.add(blueRadio);
        buttonGroup.add(redRadio);
        buttonGroup.add(yellowRadio);

        //Подписи к слайдерам
        minXLabel = new JLabel("Минимальный X", SwingConstants.CENTER);
        minXLabel.setPreferredSize(new Dimension((int) (frameWidth / 2 - frameWidth * 0.15 - 100), 25));
        maxXLabel = new JLabel("Максимальный X", SwingConstants.CENTER);
        maxXLabel.setPreferredSize(new Dimension((int) (frameWidth / 2 - frameWidth * 0.15 - 100), 25));
        minYLabel = new JLabel("Минимальный Y", SwingConstants.CENTER);
        minYLabel.setPreferredSize(new Dimension((int) (frameWidth / 2 - frameWidth * 0.15 - 100), 25));
        maxYLabel = new JLabel("Максимальный Y", SwingConstants.CENTER);
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
        minSplashLabel = new JLabel("Мин. брызги");
        maxSplashLabel = new JLabel("Макс. брызги");
        minDepthLabel = new JLabel("Мин. глубина");
        maxDepthLabel = new JLabel("Макс. глубина");


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
            JOptionPane.showMessageDialog(g, "Не выбран цвет", "Ошибка", JOptionPane.ERROR_MESSAGE);
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
            JOptionPane.showMessageDialog(g, "Нет объектов, удовлетворяющих фильтрам.");
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

    private void showYouAreBannedMessage() {
        JOptionPane.showMessageDialog(g, "Доступ к серверу заблокирован", "Ошибка", JOptionPane.ERROR_MESSAGE);
    }

    public void showMessage(String message){
        JOptionPane.showMessageDialog(g, message);
    }
}


// Что нужно сделать
// Сделать постоянный слушатель чтобы сообщение о бане выскакивало сразу с таймаутом


