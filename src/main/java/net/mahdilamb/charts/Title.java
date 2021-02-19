package net.mahdilamb.charts;

import net.mahdilamb.charts.graphics.ChartCanvas;
import net.mahdilamb.charts.graphics.Font;
import net.mahdilamb.charts.graphics.HAlign;
import net.mahdilamb.colormap.Color;

/**
 * A title is text that takes up space in a layout
 */
public class Title extends ChartComponent {
    double paddingX = 20, paddingY = 20;
    private boolean isVisible = true;

    HAlign textAlign = HAlign.CENTER;
    String text;
    Font font;
    Color color = Color.BLACK;

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
        redraw();
    }

    public void setFont(final Font font) {
        this.font = font;
        redraw();
    }

    public void setVisible(boolean visible) {
        this.isVisible = visible;
        redraw();
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

    protected static double getAlignFrac(HAlign textAlign) {
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
    protected void drawComponent(Figure<?, ?> source, ChartCanvas<?> canvas) {
        if (!isVisible()) {
            return;
        }
        canvas.setFill(color);
        canvas.setFont(font);
        canvas.fillText(text, posX + paddingX * 0.5, posY + baselineOffset + paddingX * 0.5);

    }

    @Override
    protected void layoutComponent(Figure<?, ?> source, double minX, double minY, double maxX, double maxY) {
        if (!isVisible()) {
            sizeX = 0;
            sizeY = 0;
            posX = minX;
            posY = minY;
            return;
        }
        baselineOffset = source.getTextBaselineOffset(font);
        int i = 0;
        sizeX = paddingX;
        while (i < text.length()) {
            sizeX += source.getCharWidth(font, text.charAt(i++));
        }
        sizeY = source.getTextLineHeight(font) + paddingY;
        this.posY = minY;
        this.posX = minX;
        if (textAlign != HAlign.LEFT) {
            posX += (textAlign == HAlign.RIGHT ? 1 : 0.5) * (maxX - minX - sizeX);
        }

    }

    @Override
    public String toString() {
        return String.format("Title {%s, %s}", text, font);
    }
}
