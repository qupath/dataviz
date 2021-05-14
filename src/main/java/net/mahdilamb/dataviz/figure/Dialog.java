package net.mahdilamb.dataviz.figure;

import net.mahdilamb.dataviz.graphics.GraphicsBuffer;
import net.mahdilamb.dataviz.graphics.Side;

import java.awt.*;

public class Dialog extends AbstractPopout<Form> {


    public Dialog(double x, double y, Color background, Color outline, Side side, Form content, boolean showArrow) {
        super(x, y, background, outline, side, content, showArrow);
    }

    @Override
    protected void layoutContent(Renderer renderer, double minX, double minY, double maxX, double maxY) {
        content.layoutComponent(renderer, minX, minY, maxX, maxY);
    }

    @Override
    protected double getContentWidth(Renderer renderer) {
        return content.getWidth();
    }

    @Override
    protected double getContentHeight(Renderer renderer) {
        return content.getHeight();
    }

    @Override
    protected void drawContent(Renderer renderer, GraphicsBuffer canvas, double x, double y) {
        content.drawComponent(renderer, canvas);
    }

    @Override
    protected void onFocus() {
        content.onFocus();
        super.onFocus();
    }

    @Override
    protected void onBlur() {
        content.onBlur();
        super.onBlur();
    }

    @Override
    protected Component getComponentAt(double x, double y) {
        if (content.containsPoint(x, y)) {
            return content;
        }
        return super.getComponentAt(x, y);
    }
}
