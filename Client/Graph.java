package Client;

import Common.FallingInRiver;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

import static java.awt.Color.*;

public class Graph extends JComponent {


    private int cellSize; // Размер клеток графа

    public int getCellSize() {
        return cellSize;
    }
    Graph(int cellSize) {
        this.cellSize = cellSize;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        //Заливаем белым фоном и обводим сверху и слева
        Rectangle rect = new Rectangle(0,0, getWidth(), getHeight());
        g2.setColor(white);
        g2.fill(rect);
        g2.setColor(black);
        g2.draw(rect);
        //Делаем сетку
        g2.setColor(lightGray);
        for (int y = 0; y <= this.getWidth(); y += cellSize) {
            g2.draw(new Line2D.Double(0, y, this.getWidth(), y));
            g2.draw(new Line2D.Double(y * 1, 0, y * 1, this.getHeight()));
        }
        // Нумеруем "штрихи" сетки
        g2.setPaint(Color.BLACK);
        for (int x = 0; x < this.getWidth(); x += cellSize * 1)
            g2.drawString(Integer.toString(x), x, 10);
        g2.drawString("X", this.getWidth() - 10, 10);
        for (int y = 0; y < this.getHeight(); y += cellSize)
            g2.drawString(Integer.toString(y), 0, y);
        g2.drawString("Y", 0, this.getHeight() - 10);
        //Чтобы сначала залилась коллекция а потом слушать изменение коллекции и
        // в случае изменения сразу апдейтить
    }
}
