package net.mahdilamb.dataviz.ui.layouts;

import net.mahdilamb.dataviz.figure.AbstractComponent;
import net.mahdilamb.dataviz.figure.LayoutManager;
import net.mahdilamb.dataviz.figure.Group;
import net.mahdilamb.dataviz.figure.Renderer;

/**
 * A layout that positions components vertical.
 */
public class VBoxLayout extends LayoutManager {
    /**
     * Get the instance of a VBoxLayout
     */
    public static final LayoutManager INSTANCE = new VBoxLayout();

    @Override
    protected <T>void layoutChildren(Group node, Renderer<T> renderer, double minX, double minY, double maxX, double maxY) {
        double start = minY;
        double width = 0;
        for (final AbstractComponent c : getChildren(node)) {
            layout(c, renderer, minX, minY, maxX, maxY);
            minY += c.getHeight();
            width = Math.max(width, c.getWidth());
        }
        setBoundsFromRect(node, minX, start, width, minY - start);
    }

}
