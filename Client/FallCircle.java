package Client;

import Common.FallingInRiver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;

class FallCircle extends JButton {
    private int opacity=255; //Непрозрачность
    FallingInRiver element; //Каждому кружочку соответствует элемент коллекции
    Ellipse2D.Double o;
    int circleRadius;

    FallCircle(FallingInRiver element) {
        this.element = element;
        setBorder(null);
        setEnabled(false);
        setVisible(true);
        setBounds(element.getX(), element.getY(), element.getSplashLvl()*10, element.getSplashLvl()*10);
        addMouseListener(new showNameOnHover());
    }

    //Делаем форму круга
    @Override
    public void paintComponent(Graphics g) {
        AWTColorAdapter adapter = new AWTColorAdapter(element.getColor());
        g.setColor(new Color(adapter.getRed(), adapter.getGreen(), adapter.getBlue(), opacity)); //Тупо достаю RGB из цвета элемента коллекции и ставлю непрозрачность. Потом используется для эффекта
        g.fillOval(0, 0, element.getSplashLvl() * 10, element.getSplashLvl() * 10);

    }

    public void rebounds() {
        setBounds(element.getX(), element.getY(), element.getSplashLvl()*10, element.getSplashLvl()*10);
    }
    public int getOpacity() {return opacity;}

    public void decrementOpacity(){opacity--;}

    public void incrementOpacity() {opacity++;}

    public void setOpacity(int opacity) {this.opacity=opacity;}


    class showNameOnHover extends MouseAdapter {

        @Override
        public void mouseEntered(MouseEvent e) {
            super.mouseEntered(e);
            setToolTipText(element.getCharName());
        }

        @Override
        public void mouseExited(MouseEvent e) {
            super.mouseExited(e);
            setToolTipText(null);
        }
    }
}