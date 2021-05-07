package net.mahdilamb.dataviz.figure;

import net.mahdilamb.dataviz.graphics.*;
import net.mahdilamb.dataviz.graphics.Font;

import java.awt.*;

public final class Tooltip extends AbstractPopout<String> {

    private final Font font;

    Tooltip(Side side, double relativePosition, VAlign vAlign, HAlign hAlign, double radius, Color background, Color outline, String content, Font font, boolean showArrow) {
        super(vAlign, hAlign, relativePosition, background, outline, side, content, showArrow);
        this.font = font;
        this.radius = radius;
    }

    public void setBackground(final Color color) {
        this.background = color;
    }

    /**
     * @return the text of the tool tip
     */
    public String getText() {
        return content;
    }

    @Override
    protected <IMG> double getContentWidth(Renderer<IMG> renderer) {
        return getTextWidth(renderer, font, content);
    }

    @Override
    protected <IMG> double getContentHeight(Renderer<IMG> renderer) {
        return getTextLineHeight(renderer, font);
    }

    @Override
    protected <IMG> void drawContent(Renderer<IMG> renderer, GraphicsBuffer<IMG> canvas, double x, double y) {
        canvas.setFill(getForeground());
        canvas.setFont(font);
        canvas.fillText(content, x, y + getTextBaselineOffset(renderer, font));
    }

    public static Tooltip create(Side side, double relativePosition, VAlign vAlign, HAlign hAlign, double radius, Color background, Color outline, String content, Font font, boolean showArrow) {
        return new Tooltip(side, relativePosition, vAlign, hAlign, radius, background, outline, content, font, showArrow);
    }

    public static Tooltip create(Side side, double relativePosition, VAlign vAlign, HAlign hAlign, double radius, Color background, Color outline, String content, Font font) {
        return new Tooltip(side, relativePosition, vAlign, hAlign, radius, background, outline, content, font, DEFAULT_SHOW_ARROW);
    }

    public static Tooltip create(Side side, double relativePosition, VAlign vAlign, HAlign hAlign, double radius, Color background, Color outline, String content) {
        return new Tooltip(side, relativePosition, vAlign, hAlign, radius, background, outline, content, Font.DEFAULT_FONT, DEFAULT_SHOW_ARROW);
    }

    public static Tooltip create(Side side, double relativePosition, VAlign vAlign, HAlign hAlign, Color background, Color outline, String content) {
        return new Tooltip(side, relativePosition, vAlign, hAlign, DEFAULT_TOOLTIP_RADIUS, background, outline, content, Font.DEFAULT_FONT, DEFAULT_SHOW_ARROW);
    }

    public static Tooltip create(Side side, double relativePosition, VAlign vAlign, HAlign hAlign, Color background, String content) {
        return new Tooltip(side, relativePosition, vAlign, hAlign, DEFAULT_TOOLTIP_RADIUS, background, DEFAULT_OUTLINE, content, Font.DEFAULT_FONT, DEFAULT_SHOW_ARROW);
    }

    public static Tooltip create(Side side, VAlign vAlign, HAlign hAlign, Color background, String content) {
        return new Tooltip(side, DEFAULT_RELATIVE_POSITION, vAlign, hAlign, DEFAULT_TOOLTIP_RADIUS, background, DEFAULT_OUTLINE, content, Font.DEFAULT_FONT, DEFAULT_SHOW_ARROW);
    }

    public static Tooltip create(Side side, Color background, String content) {
        return new Tooltip(side, DEFAULT_RELATIVE_POSITION, DEFAULT_VALIGN, DEFAULT_HALIGN, DEFAULT_TOOLTIP_RADIUS, background, DEFAULT_OUTLINE, content, Font.DEFAULT_FONT, DEFAULT_SHOW_ARROW);
    }

    public static Tooltip create(Color background, String content) {
        return new Tooltip(DEFAULT_SIDE, DEFAULT_RELATIVE_POSITION, DEFAULT_VALIGN, DEFAULT_HALIGN, DEFAULT_TOOLTIP_RADIUS, background, DEFAULT_OUTLINE, content, Font.DEFAULT_FONT, DEFAULT_SHOW_ARROW);
    }
}
