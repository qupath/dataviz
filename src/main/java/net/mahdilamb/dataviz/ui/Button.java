package net.mahdilamb.dataviz.ui;

import net.mahdilamb.dataviz.figure.Component;
import net.mahdilamb.dataviz.figure.Renderer;
import net.mahdilamb.dataviz.graphics.Font;
import net.mahdilamb.dataviz.graphics.GraphicsBuffer;
import net.mahdilamb.dataviz.graphics.Stroke;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Button component
 */
public class Button extends Component {

    private static final Color DISABLED = new Color(.8f, 0f, .8f, 0);
    private static final Color HOVER = new Color(.8f, .8f, .8f, .5f);
    private static final Color PRESSED = new Color(.8f, .8f, .8f, .75f);
    private final String text;
    private final BufferedImage icon;
    double padding = 2;
    double spacing = 5;
    ButtonGroup<?> group;
    Color color;
    double alpha = 1;

    /**
     * Create a button with an icon and text
     *
     * @param icon the icon
     * @param text the text
     */
    public Button(final BufferedImage icon, final String text) {
        this.text = text;
        this.icon = icon;
    }

    /**
     * Create an icon button
     *
     * @param icon the icon
     */
    public Button(final BufferedImage icon) {
        this.icon = icon;
        this.text = null;
    }

    /**
     * Create a text button
     *
     * @param text the text on the button
     */
    public Button(final String text) {
        this.text = text;
        this.icon = null;
    }

    @Override
    protected void layoutComponent(Renderer renderer, double minX, double minY, double maxX, double maxY) {
        double width = 0;
        double height = 0;
        if (icon != null) {
            width = getMaterialIconWidth();
            height = getMaterialIconHeight();
        }
        if (text != null && text.length() > 0) {
            if (icon != null) {
                width += padding;
            }
            width += getTextWidth(renderer, Font.DEFAULT_FONT, text);
            height = Math.max(getTextLineHeight(renderer, Font.DEFAULT_FONT, text), height);
        }
        if (text != null && icon != null) {
            width += spacing;
        }
        setBoundsFromRect(minX, minY, width + padding * 2, height + padding * 2);

    }

    @Override
    protected void onMouseEnter(boolean ctrlDown, boolean shiftDown, double x, double y) {
        color = HOVER;
        redraw();

    }

    @Override
    protected void onMouseExit(boolean ctrlDown, boolean shiftDown, double x, double y) {
        color = null;
        redraw();
    }

    @Override
    protected void onMouseDown(boolean ctrlDown, boolean shiftDown, double x, double y) {
        color = PRESSED;
        redraw();
    }

    @Override
    protected void onMouseUp(boolean ctrlDown, boolean shiftDown, double x, double y) {
        if (containsPoint(x, y)) {
            color = HOVER;
        } else {
            color = null;
        }
        redraw();
    }

    private void drawButton(Renderer renderer, GraphicsBuffer canvas) {
        if (alpha != 1) {
            canvas.setGlobalAlpha(alpha);
        }
        if (icon != null) {
            canvas.drawImage( icon, getX() + padding, getY() + padding);
        }
        if (text != null) {
            double left = 0;
            if (icon != null) {
                left += spacing + getMaterialIconWidth();
            }
            canvas.setFont(Font.DEFAULT_FONT);
            canvas.setFill(Color.BLACK);
            final double vAlign = (getMaterialIconHeight() - getTextLineHeight(renderer, Font.DEFAULT_FONT, text)) / 2;
            canvas.fillText(text, getX() + left, getY() + padding + getTextBaselineOffset(renderer, Font.DEFAULT_FONT) + vAlign);
        }
        if (alpha != 1) {
            canvas.resetGlobalAlpha();
        }
        if (group != null && !isFirst()) {
            canvas.setStroke(new Stroke(.5));//TODO
            canvas.setStroke(Color.DARK_GRAY);
            canvas.strokeLine(getX(), getY(), getX(), getY() + getHeight());
        }
    }

    @Override
    protected void drawComponent(Renderer renderer, GraphicsBuffer canvas) {
        alpha = isEnabled() ? 1 : .2;
        if (color != null) {
            canvas.setFill(color);
            canvas.fillRoundRect(getX(), getY(), getWidth(), getHeight(), 4, 4);
        } else {
            alpha = .5;
        }
        drawButton(renderer, canvas);
        drawFocusRing(canvas);
    }

    protected boolean isFirst() {
        return group != null && group.indexOf(this) == 0;
    }

    protected boolean isLast() {
        return group != null && group.lastIndexOf(this) == group.size() - 1;
    }
}
