package Client;

import Common.FallingInRiver;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ClientGUI extends JFrame {
    private int count;

    private ArrayList<FallCircle> list = new ArrayList<>();
    private JLayeredPane lpane;
    private ConcurrentHashMap<Integer, FallingInRiver> map;
    private final int frameWidth = 850, frameHeight = 700;
    private Graph g;
    private JButton startButton, stopButton;
    private JRadioButton orangeRadio, blueRadio, redRadio, yellowRadio;
    private ButtonGroup buttonGroup;
    private JFormattedTextField nameField;
    private JLabel minXLabel, maxXLabel, minYLabel, maxYLabel,
            minSplashLabel, maxSplashLabel, minDepthLabel, maxDepthLabel;
    private JSlider minXSlider, maxXSlider, minYSlider, maxYSlider;
    private JSpinner minSplash, maxSplash, minDepth, maxDepth;


    public ClientGUI(String windowName) {
        super(windowName);
        //this.map = map;
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


        //Кнопки старт | стоп
        startButton = new JButton("Старт");
        startButton.setPreferredSize(new Dimension(80, 30));
        stopButton = new JButton("Стоп");
        stopButton.setPreferredSize(new Dimension(80, 30));

        //***ФИЛЬТРЫ***
        //Радио кнопки
        orangeRadio = new JRadioButton("Оранжевый");
        blueRadio = new JRadioButton("Синий");
        redRadio = new JRadioButton("Красный");
        yellowRadio = new JRadioButton("Желтый");
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
        minXSlider = new JSlider(0, (int) g.getMinimumSize().getWidth(), 8 * g.getCellSize()); // Все значения тут добыты экспериментальным путем
        minXSlider.setMinorTickSpacing(40);
        minXSlider.setMajorTickSpacing(160);
        minXSlider.setPaintLabels(true);
        minXSlider.setPaintTicks(true);
        maxXSlider = new JSlider(0, (int) g.getMinimumSize().getWidth(), 8 * g.getCellSize());
        maxXSlider.setMinorTickSpacing(40);
        maxXSlider.setMajorTickSpacing(160);
        maxXSlider.setPaintLabels(true);
        maxXSlider.setPaintTicks(true);
        minYSlider = new JSlider(0, (int) g.getMinimumSize().getHeight(), 5 * g.getCellSize());
        minYSlider.setMinorTickSpacing(20);
        minYSlider.setMajorTickSpacing(80);
        minYSlider.setPaintLabels(true);
        minYSlider.setPaintTicks(true);
        maxYSlider = new JSlider(0, (int) g.getMinimumSize().getHeight(), 5 * g.getCellSize());
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


        GraphAndCircles graphPane = new GraphAndCircles();

        add(graphPane);

        add(Box.createRigidArea(new Dimension((int) (frameWidth / 2 - frameWidth * 0.235), 30))); // Значения все так же вычислены экспериметально
        add(startButton);
        add(Box.createRigidArea(new Dimension(45, 30)));
        add(stopButton);
        add(Box.createRigidArea(new Dimension((int) (frameWidth / 2 - frameWidth * 0.235), 30)));

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

    //JPanel с нулевым LayoutManager, чтобы в ней спокойно разместить график и кружочки
    class GraphAndCircles extends JPanel {
        public GraphAndCircles() {
            setLayout(null);
            setPreferredSize(new Dimension(801, 400));
            setMinimumSize(getPreferredSize());
            add(g);
            //Добавляем координатную сетку
            g.setBounds(0, 0, 801, 400);
            FallingInRiver fuckMyLife = new FallingInRiver(4, "lala", 10, 10, "Оранжевый", 139, 200);
            Circle o = new Circle(fuckMyLife);
            add(o);
            //o.setForeground(Color.BLUE);
            o.reBounds();
            RoundButton r = new RoundButton("lala");
            add(r);
            r.setBounds(100,100, 10, 10);
            o.repaint();

            JButton button = new JButton() {
                @Override
                public void paintComponent(Graphics g) {

                    g.fillOval(0,0, 10, 10);
                    setFocusPainted(false);
                }
            };
            add(button);
            button.setBounds(100, 100, 40, 40);
            button.repaint();
            repaint();
        }
    }


    //Кружочки
    class Circle extends JButton {
        FallingInRiver element; //Каждому кружочку соответствует элемент коллекции
        Ellipse2D.Double o;
        int circleRadius;

        Circle(FallingInRiver element) {
            this.element = element;
            circleRadius = element.getSplashLvl() * 10;
            setBackground(Color.WHITE);
            o = new Ellipse2D.Double(0, 0, element.getSplashLvl() * 10, element.getSplashLvl() * 10);
            setVisible(true);

        }

        //Делаем форму круга
        @Override
        public void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;

            g2.setColor(element.COLORtoAWTColor());
            g2.fill(o); //Необходимо, так как в GraphAndCircles LayoutManager = null

        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(element.COLORtoAWTColor());
            g2.fill(o);
        }

        public void reBounds() {
            setBounds(element.getX(), element.getY(), (int) 300, (int) 300);
        }
    }
    class RoundButton extends JButton {

        public RoundButton(String label) {
            super(label);

            setBackground(Color.lightGray);
            setFocusable(false);

    /*
     These statements enlarge the button so that it
     becomes a circle rather than an oval.
    */
            Dimension size = getPreferredSize();
            size.width = size.height = Math.max(size.width, size.height);
            setPreferredSize(size);

    /*
     This call causes the JButton not to paint the background.
     This allows us to paint a round background.
    */
            setContentAreaFilled(false);
        }

        protected void paintComponent(Graphics g) {
            if (getModel().isArmed()) {
                g.setColor(Color.gray);
            } else {
                g.setColor(getBackground());
            }
            g.fillOval(0, 0, getSize().width - 1, getSize().height - 1);

            super.paintComponent(g);
        }

        protected void paintBorder(Graphics g) {
            g.setColor(Color.darkGray);
            g.drawOval(0, 0, getSize().width - 1, getSize().height - 1);
        }

        // Hit detection.
        Shape shape;

        public boolean contains(int x, int y) {
            // If the button has changed size,  make a new shape object.
            if (shape == null || !shape.getBounds().equals(getBounds())) {
                shape = new Ellipse2D.Float(0, 0, getWidth(), getHeight());
            }
            return shape.contains(x, y);
        }
    }
}





// Что нужно сделать



