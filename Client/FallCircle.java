package Client;

import Common.FallingInRiver;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;

public class FallCircle extends JButton {
    FallingInRiver element;

    FallCircle(FallingInRiver element) {
        this.element = element;
    }




    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        Ellipse2D.Double o = new Ellipse2D.Double(element.getX(), element.getY(), element.getSplashLvl() * 10, element.getSplashLvl() * 10);
        g2.setColor(element.COLORtoAWTColor());
        g2.fill(o);
    }
}
