package net.mahdilamb.dataviz.figure;

import net.mahdilamb.dataviz.graphics.GraphicsBuffer;
import net.mahdilamb.dataviz.ui.IconStore;
import net.mahdilamb.dataviz.ui.InputComponent;
import net.mahdilamb.dataviz.ui.ToggleButton;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class Form extends Component {
    final Map<String, InputComponent<?>> inputs = new HashMap<>();
    Color color;

    public Form() {
        super(BufferingStrategy.NO_BUFFERING);
        add("hi",new ToggleButton(IconStore.get(IconStore.MATERIAL_ICONS, IconStore.MaterialIconKey.class).getDark(8),null));
        setOnBlur(() -> {
            color = Color.RED;
            redraw();
        });
        setOnFocus(() -> {
            color = Color.GREEN;
            redraw();
        });
    }

    public void add(final String name, final InputComponent<?> input) {
        inputs.put(name, input);
        relayout();
    }

    @Override
    protected void layoutComponent(Renderer renderer, double minX, double minY, double maxX, double maxY) {
        setBoundsFromExtent(minX, minY, maxX, maxY);
    }

    @Override
    protected void drawComponent(Renderer renderer, GraphicsBuffer canvas) {
        canvas.setFill(color);
        canvas.fillRect(getX(), getY(), getWidth(), getHeight());
    }

    @Override
    public double getWidth() {
        return 360;
    }

    @Override
    public double getHeight() {
        return 240;
    }
}
