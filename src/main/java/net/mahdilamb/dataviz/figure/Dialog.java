package net.mahdilamb.dataviz.figure;

import net.mahdilamb.dataviz.graphics.GraphicsBuffer;
import net.mahdilamb.dataviz.graphics.Side;

import java.awt.*;

public class Dialog extends AbstractPopout<Form> {

boolean blocking;
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
    protected void onMouseClick(boolean ctrlDown, boolean shiftDown, double x, double y) {
        AbstractComponent.print(content.containsPoint(x, y), getComponentAt(x, y));

        super.onMouseClick(ctrlDown, shiftDown, x, y);
    }


    @Override
    public boolean containsPoint(double x, double y) {
        return content.containsPoint(x, y);
    }

    @Override
    void setBoundsFromMinAndMax(double contentX, double contentY, double contentWidth, double contentHeight, double triHeight) {
        setBoundsFromExtent(0, 0, getContext().getRenderer().getFigure().getWidth(), getContext().getRenderer().getFigure().getHeight());
    }

    @Override
    void drawOverlay(Renderer renderer, GraphicsBuffer canvas) {
        if (blocking){
            canvas.setFill(new Color(0, 0, 0, 24));
            canvas.fillRect(getX(), getY(), getWidth(), getHeight());
        }
    }

}
