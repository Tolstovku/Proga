package Common;

import java.awt.*;
import java.io.Serializable;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.awt.color.*;

import static java.awt.Color.*;

public class FallingInRiver implements Comparable<FallingInRiver>, Serializable {

    public enum COLOR{
            ORANGE("Оранжевый", Color.ORANGE),
            BLUE("Синий", Color.BLUE),
            RED("Красный", Color.RED),
            YELLOW("Желтый", Color.YELLOW);

        String colorName;
        Color color;

            COLOR(String s, Color c) {
                colorName=s;
                color=c;
            }

        @Override
        public String toString() {
            return colorName;
        }

    }



    private final String charName;
    private final int splashLvl;
    private final double depth;
    private final String timeStamp = new SimpleDateFormat("dd.mm.yyyy hh:mm").format(Calendar.getInstance().getTime());
    private final COLOR color;
    private final int x, y;
    private final int id;
    private int timer;

    public FallingInRiver(int id, String charName, int splashLvl, double depth, String color, int x, int y) {
        this.charName = charName;
        if (splashLvl==0)
            this.splashLvl=1;
        else if (splashLvl>10)
            this.splashLvl=10;
        else
            this.splashLvl = splashLvl;
        this.depth = depth;
        this.id = id;
        this.timer = (int) (Math.random() * 11);
        if (timer < 3) timer *= 2;
        this.x=x;
        this.y=y;
        switch(color){
            case "Оранжевый":
            case "оранжевый":
                this.color=COLOR.ORANGE;
                break;
            case "Синий":
            case "синий":
                this.color=COLOR.BLUE;
                break;
            case "Красный":
            case "красный":
                this.color=COLOR.RED;
                break;
            case "Желтый":
            case "желтый":
                this.color=COLOR.YELLOW;
                break;
            default:
                this.color=null;
                //System.out.println("Недопустимый цвет у объекта " + charName + ", id:" + id + ". Установлен желтый цвет.");
                //this.color=COLOR.YELLOW;
        }

    }

    public int getId() {
        return id;
    }

    public String getCharName() {
        return charName;
    }

    public int getSplashLvl() {
        return splashLvl;
    }

    public double getDepth() {
        return depth;
    }

    public int getX() { return x; }

    public int getY() { return y; }

    public COLOR getColor() {return color;}

    public void tick() {
        timer -= 1;
        if (timer <= 0) {
            action();
            timer = (int) (Math.random() * 11);
        }
    }



    void action() {
        System.out.print(charName);
        switch (splashLvl) {
            case 1:
            case 2:
            case 3:
                System.out.print(" шлепнулся");
                break;
            case 4:
            case 5:
            case 6:
                System.out.print(" свалился");
                break;
            case 7:
            case 8:
            case 9:
                System.out.print(" плюхнулся");
                break;
            case 10:
                System.out.print(" громоподобно грохнулся");
                break;
            default:
                System.out.print(" упал");
        }
        System.out.println(" в реку на глубину " + depth + "м" + ", после чего благополучно выплыл на берег.");
    }

    @Override
    public int compareTo(FallingInRiver obj) {
        return id-obj.id;
    }
}
