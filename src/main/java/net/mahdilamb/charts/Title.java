package net.mahdilamb.charts;

import net.mahdilamb.charts.graphics.Alignment;
import net.mahdilamb.charts.graphics.Font;

/**
 * A title is text that takes up space in a layout
 */
public class Title {
    double paddingX = 0, paddingY = 2;
    boolean isVisible;
    Alignment alignment;
    String text;
    Font font;
    boolean metricsSet = false;
    double width;
    double height;
    double baselineOffset;

    /**
     * Create a title wi
     * @param text
     * @param font
     * @param alignment
     */
    public Title(String text, Font font, Alignment alignment) {
        this.text = text;
        this.font = font;
        this.alignment = alignment;
    }

    /**
     * @return whether the text is visible (i.e. should take up space in the layout)
     */
    public boolean isVisible() {
        return isVisible && text != null && text.length() > 0;
    }

    /**
     * @return the padding in the x axis
     */
    public double getPaddingX() {
        return paddingX;
    }

    /**
     * @return the padding in the y axis
     */
    public double getPaddingY() {
        return paddingY;
    }

    /**
     * Set the text of this title
     *
     * @param text the text of the title
     */
    public void setTitle(String text) {
        this.text = text;
        metricsSet = false;
    }

    /**
     * @return the alignment of the label
     */
    public Alignment getAlignment() {
        return alignment;
    }

    /**
     * @return the text of this title. May be {@code null}
     */
    public String getText() {
        return text;
    }

    /**
     * @return the font of this title
     */
    public Font getFont() {
        return font;
    }

    /**
     * Mark this title as requiring the metrics to be set on the next layout pass
     */
    void markDirty() {
        metricsSet = false;
    }

    /**
     * Set the metrics of this title
     *
     * @param width          the width of the title
     * @param height         the height of the title
     * @param baselineOffset the baseline offset of the title
     */
    void setMetrics(double width, double height, double baselineOffset) {
        if (metricsSet) {
            return;
        }
        this.width = width;
        this.height = height;
        this.baselineOffset = baselineOffset;
        metricsSet = true;
    }

}
