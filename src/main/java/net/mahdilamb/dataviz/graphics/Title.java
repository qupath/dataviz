package net.mahdilamb.dataviz.graphics;

import net.mahdilamb.colormap.Color;
import net.mahdilamb.dataviz.Component;
import net.mahdilamb.dataviz.Renderer;

/**
 * A title is text that takes up space in a layout
 */
public class Title extends Component {
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
    public void setText(String text) {
        this.text = text;
        refresh();
    }

    /**
     * Set the font of this title
     *
     * @param font the font of this text
     */
    public void setFont(final Font font) {
        this.font = font;
        refresh();
    }

    /**
     * Set the visibility of this title
     *
     * @param visible whether this title should be visible
     */
    public void setVisible(boolean visible) {
        this.isVisible = visible;
        refresh();
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
    protected void layoutComponent(Renderer<?> source, double minX, double minY, double maxX, double maxY) {
        if (!isVisible()) {
            setBoundsFromRect(minX, minY, 0, 0);
            return;
        }
        baselineOffset = getTextBaselineOffset(source, font);
        int i = 0;
        double sizeX = paddingX;
        while (i < text.length()) {
            sizeX += getTextCharWidth(source, font, text.charAt(i++));
        }
        double sizeY = getTextLineHeight(source, font) + paddingY;
        if (textAlign != HAlign.LEFT) {
            minX += (textAlign == HAlign.RIGHT ? 1 : 0.5) * (maxX - minX - sizeX);
        }
        setBoundsFromRect(minX, minY, sizeX, sizeY);

    }

    @Override
    protected void drawComponent(Renderer<?> source, ChartCanvas<?> canvas) {
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
