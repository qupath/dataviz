package net.mahdilamb.dataviz.ui;

import net.mahdilamb.dataviz.figure.Component;
import net.mahdilamb.dataviz.figure.Renderer;
import net.mahdilamb.dataviz.graphics.Font;
import net.mahdilamb.dataviz.graphics.GraphicsBuffer;
import net.mahdilamb.dataviz.graphics.HAlign;

import java.awt.*;

/**
 * A title is text that takes up space in a layout
 */
public class Label extends Component {
    double paddingX = 5, paddingY = 5;
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
    public Label(String text, Font font) {
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
    public void setText(String text) {
        this.text = text;
        relayout();
    }

    /**
     * Set the font of this title
     *
     * @param font the font of this text
     */
    public void setFont(final Font font) {
        this.font = font;
        relayout();
    }

    /**
     * Set the visibility of this title
     *
     * @param visible whether this title should be visible
     */
    public void setVisible(boolean visible) {
        this.isVisible = visible;
        relayout();
    }

    public void setColor(final Color color) {
        this.color = color;
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

    /**
     * @return the title color
     */
    public Color getColor() {
        return color;
    }

    /**
     * Set the alignment of the title
     *
     * @param alignment the alignment
     */
    public void setAlignment(HAlign alignment) {
        textAlign = alignment;
        relayout();
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
    protected <T> void layoutComponent(Renderer<T> renderer, double minX, double minY, double maxX, double maxY) {
        if (!isVisible()) {
            setBoundsFromRect(minX, minY, 0, 0);
            return;
        }
        baselineOffset = getTextBaselineOffset(renderer, font);
        int i = 0;
        double sizeX = paddingX;
        while (i < text.length()) {
            sizeX += getTextCharWidth(renderer, font, text.charAt(i++));
        }
        double sizeY = getTextLineHeight(renderer, font, text) + paddingY;
        if (textAlign != HAlign.LEFT) {
            minX += (textAlign == HAlign.RIGHT ? 1 : 0.5) * (maxX - minX - sizeX);
        }
        setBoundsFromRect(minX, minY, sizeX, sizeY);

    }

    @Override
    protected <T> void drawComponent(Renderer<T> renderer, GraphicsBuffer<T> canvas) {
        if (!isVisible()) {
            return;
        }
        canvas.setFill(color);
        canvas.setFont(font);
        canvas.fillText(text, getX() + paddingX * 0.5, getY() + baselineOffset + paddingX * 0.5);

    }

    @Override
    public String toString() {
        return String.format("Title {%s, %s}", text, font);
    }

}
