package net.mahdilamb.dataviz.ui;

import net.mahdilamb.dataviz.figure.BufferingStrategy;
import net.mahdilamb.dataviz.figure.Component;
import net.mahdilamb.dataviz.figure.Renderer;
import net.mahdilamb.dataviz.graphics.GraphicsBuffer;

import java.awt.*;

/**
 * An interactive icon
 */
public final class Icon extends Component {
    private final IconStore.MaterialIconKey iconKey;
    private double x = 0, y = 0;
    private Color backgroundColor = null;

    /**
     * Create an icon from the key
     *
     * @param iconKey the icon key
     */
    public Icon(IconStore.MaterialIconKey iconKey) {
        super(BufferingStrategy.NO_BUFFERING);
        this.iconKey = iconKey;
    }

    /**
     * Create an icon at a position
     *
     * @param x       the min x
     * @param y       the min y
     * @param iconKey the icon key
     */
    public Icon(double x, double y, IconStore.MaterialIconKey iconKey) {
        this.iconKey = iconKey;
        this.x = x;
        this.y = y;
    }

    public Icon(double x, double y, IconStore.MaterialIconKey iconKey, Color backgroundColor) {
        this.iconKey = iconKey;
        this.x = x;
        this.y = y;
        this.backgroundColor = backgroundColor;
    }

    @Override
    protected void layoutComponent(Renderer renderer, double minX, double minY, double maxX, double maxY) {
        setBoundsFromRect(minX + x, minY + y, getMaterialIconWidth(), getMaterialIconHeight());
    }

    @Override
    protected void drawComponent(Renderer renderer, GraphicsBuffer canvas) {
        canvas.drawImage(getMaterialIcon(renderer, iconKey, backgroundColor), getX(), getY());
    }

}
