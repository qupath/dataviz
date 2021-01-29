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

    double getBaselineOffset() {
        return baselineOffset;
    }

    double getHeight() {
        return height;
    }

    synchronized double getWidth() {
        return width;
    }
}
