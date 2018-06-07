package Client;

import Common.FallingInRiver;

import java.awt.*;

import static java.awt.Color.*;

public class AWTColorAdapter extends Color{
    private FallingInRiver.COLOR fallColor;
    private Color AWTAnalog;

    public AWTColorAdapter(FallingInRiver.COLOR fallColor) {
        super(fromCOLOR(fallColor).getRed(), fromCOLOR(fallColor).getGreen(), fromCOLOR(fallColor).getBlue());
        this.fallColor=fallColor;
        AWTAnalog = fromCOLOR(fallColor);
    }

    @Override
    public int getRed() {
        return AWTAnalog.getRed();
    }

    @Override
    public int getGreen() {
        return AWTAnalog.getGreen();
    }

    @Override
    public int getBlue() {
        return AWTAnalog.getBlue();
    }

    @Override
    public int getAlpha() {
        return AWTAnalog.getAlpha();
    }

    private static Color fromCOLOR(FallingInRiver.COLOR fallColor) {
        switch (fallColor.toString()) {
            case "Оранжевый":
                return ORANGE;
            case "Синий":
                return BLUE;
            case "Красный":
                return RED;
            case "Желтый":
                return YELLOW;
            default:
                return YELLOW;
        }
    }


}






























































//Да это бессмысленный адаптер я знаю