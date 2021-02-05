package net.mahdilamb.charts;

import net.mahdilamb.charts.graphics.ChartCanvas;
import net.mahdilamb.charts.graphics.Font;
import net.mahdilamb.charts.graphics.HAlign;

/**
 * A title is text that takes up space in a layout
 */
public class Title extends ChartComponent {
    double paddingX = 20, paddingY = 20;
    boolean isVisible = true;

    HAlign textAlign = HAlign.LEFT;
    String text;
    Font font;

    boolean metricsSet = false;
    double baselineOffset;


    /**
     * Create a title with the following details
     *
     * @param text the text
     * @param font the font to use
     */
    public Title(String text, Font font) {
        this.text = text;
        this.font = font;
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
        //TODO either change in chart, or queue change, and clear metrics
    }

    /**
     * @return the alignment of the label
     */
    public HAlign getAlignment() {
        return textAlign;
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

    private double getAlignFrac() {
        switch (textAlign) {
            case CENTER:
                return 0.5;
            case RIGHT:
                return 1;
            case LEFT:
                return 0;
            default:
                throw new UnsupportedOperationException();
        }
    }


    @Override
    protected void layout(ChartCanvas<?> canvas, Chart<?> source, double minX, double minY, double maxX, double maxY) {
        if (!isVisible()) {
            return;
        }
        canvas.setFont(font);
        if (!metricsSet) {
            calculateBounds(canvas, source, minX, minY, maxX, maxY);
            this.boundsX = minX;
            this.boundsY = minY;

        }
        canvas.fillText(text, minX + paddingX * 0.5, minY + baselineOffset + paddingX * 0.5);
        drawBounds(canvas);

    }

    @Override
    protected void calculateBounds(ChartCanvas<?> canvas, Chart<?> source, double minX, double minY, double maxX, double maxY) {
        baselineOffset = source.getTextBaselineOffset(font);
        int i = 0;
        boundsWidth = paddingX;
        while (i < text.length()) {
            boundsWidth += source.getCharWidth(font, text.charAt(i++));
        }
        boundsHeight = source.getTextLineHeight(font) + paddingY;
    }


}
