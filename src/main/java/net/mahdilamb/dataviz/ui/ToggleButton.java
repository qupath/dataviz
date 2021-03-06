package net.mahdilamb.dataviz.ui;

import net.mahdilamb.colormap.Colors;
import net.mahdilamb.dataviz.figure.Renderer;
import net.mahdilamb.dataviz.graphics.GraphicsBuffer;

import java.awt.image.BufferedImage;

public class ToggleButton extends Button implements InputComponent<Boolean> {
    boolean selected = false;

    public ToggleButton(BufferedImage icon, String text) {
        super(icon, text);
    }

    public ToggleButton(BufferedImage icon, String text, boolean selected) {
        super(icon, text);
        this.selected = selected;
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
    protected void drawComponent(Renderer renderer, GraphicsBuffer canvas) {
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
        //todo color on mouse over after click
    }

}
