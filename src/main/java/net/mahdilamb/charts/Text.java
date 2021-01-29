package net.mahdilamb.charts;

import net.mahdilamb.charts.graphics.Alignment;
import net.mahdilamb.charts.graphics.Font;

/**
 * Formatting for an axis label
 */
public class Text {

    Alignment alignment;
    String text;
    Font font;
    double width, height, baselineOffset;
    boolean set = false;
    static int i = 0;
    int j = ++i;

    public Text(String text, Font font, Alignment alignment) {
        this.text = text;
        this.font = font;
        this.alignment = alignment;
    }

    /**
     * @return the alignment of the label
     */
    public Alignment getAlignment() {
        return alignment;
    }

    public String getText() {
        return text;
    }

    public Font getFont() {
        return font;
    }

    synchronized void setMetrics(double width, double height, double baselineOffset) {
        this.height = height;
        this.width = width;
        this.baselineOffset = baselineOffset;
        this.set = true;


    }

    double getBaselineOffset() {
        return baselineOffset;
    }

    double getHeight() {
        return height;
    }

    synchronized double getAdjustedY(double y) {
        return y + baselineOffset;
    }

    synchronized double getAdjustedX(double x) {
        switch (alignment) {
            case LEFT:
                return x;
            case CENTER:
                return x - (width * .5);
            case RIGHT:
                return x - width;
            default:
                throw new UnsupportedOperationException();
        }
    }

    synchronized double getWidth() {
        return width;
    }
}
