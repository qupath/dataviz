package net.mahdilamb.charts;

import net.mahdilamb.charts.graphics.Alignment;
import net.mahdilamb.charts.graphics.Font;

public class Title extends Text {
    double paddingX = 0, paddingY = 2;
    boolean isVisible;

    public Title(String text, Font font, Alignment alignment) {
        super(text, font, alignment);
    }


    public boolean isVisible() {
        return isVisible && text != null && text.length() > 0;
    }

    public double getPaddingX() {
        return paddingX;
    }

    public double getPaddingY() {
        return paddingY;
    }

    public void setTitle(String title) {
        this.text = title;
    }
}
