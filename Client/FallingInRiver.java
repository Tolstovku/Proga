package Client;

import java.io.Serializable;
import java.util.Calendar;
import java.text.SimpleDateFormat;
public class FallingInRiver implements Comparable<FallingInRiver>, Serializable {
    private final int id;
    private final String charName;
    private final int splashLvl;
    private final double depth;
    private final String timeStamp = new SimpleDateFormat("dd.mm.yyyy hh:mm").format(Calendar.getInstance().getTime());
    private int timer;

    public FallingInRiver(int id, String charName, int splashLvl, double depth) {
        this.charName = charName;
        this.splashLvl = splashLvl;
        this.depth = depth;
        this.id = id;
        this.timer = (int) (Math.random() * 11);
        if (timer < 3) timer *= 2;
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

    void tick() {
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
