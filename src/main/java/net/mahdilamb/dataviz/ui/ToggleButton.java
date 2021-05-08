package net.mahdilamb.dataviz.ui;

import net.mahdilamb.colormap.Colors;
import net.mahdilamb.dataviz.figure.Renderer;
import net.mahdilamb.dataviz.graphics.GraphicsBuffer;

public class ToggleButton extends Button implements InputComponent<Boolean> {
    boolean selected = false;

    public <IMG> ToggleButton(IMG icon, String text) {
        super(icon, text);
    }
    public <IMG> ToggleButton(IMG icon, String text, boolean selected) {
        super(icon, text);
        this.selected=selected;
    }
    @Override
    public void setValue(Boolean value) {
        if (this.selected != (this.selected = value)) {
            redraw();
        }
    }

    @Override
    public Boolean getValue() {
        return selected;
    }

    @Override
    protected <T> void drawComponent(Renderer<T> renderer, GraphicsBuffer<T> canvas) {
        if (selected) {
            canvas.setFill(Colors.lightgrey);
            canvas.fillRoundRect(getX(), getY(), getWidth(), getHeight(), 4, 4);
        }
        super.drawComponent(renderer, canvas);
    }

    @Override
    protected void onMouseClick(boolean ctrlDown, boolean shiftDown, double x, double y) {
        if (group == null) {
            selected = !selected;
        } else {
            ((ToggleButtonGroup) group).setSelected(this);
        }
        super.onMouseClick(ctrlDown, shiftDown, x, y);
    }

}
