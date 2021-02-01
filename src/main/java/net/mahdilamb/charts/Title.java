package net.mahdilamb.charts;

import net.mahdilamb.charts.graphics.HAlign;
import net.mahdilamb.charts.graphics.ChartCanvas;
import net.mahdilamb.charts.graphics.Font;

import java.util.function.Consumer;

/**
 * A title is text that takes up space in a layout
 */
//TODO auto-wrapping
public class Title extends ChartComponent{
    double paddingX = 0, paddingY = 5;
    boolean isVisible = true;

    HAlign alignment;
    String text;
    Font font;

    boolean metricsSet = false;
    double width;
    double height;
    double baselineOffset;
    double lineHeight;
    double[] lineOffsets;
    double lineSpacing = 1;

    /**
     * Create a title with the following details
     *
     * @param text      the text
     * @param font      the font to use
     * @param alignment the alignment of the text
     */
    public Title(String text, Font font, HAlign alignment) {
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
    public HAlign getAlignment() {
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
    void setMetrics(double width, double height, double lineHeight, double baselineOffset, double[] lineOffsets) {
        if (metricsSet) {
            return;
        }
        this.width = width;
        this.height = height;
        this.baselineOffset = baselineOffset;
        this.lineOffsets = lineOffsets;
        metricsSet = true;
        this.lineHeight = lineHeight;
    }

    void setMetrics(Consumer<Title> setter) {
        if (metricsSet) {
            return;
        }
        setter.accept(this);
    }

    double adjustedX(double x) {
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


    @Override
    protected void layout(ChartCanvas<?> canvas, Chart<?, ?> source, double minX, double minY, double maxX, double maxY) {

    }
}
