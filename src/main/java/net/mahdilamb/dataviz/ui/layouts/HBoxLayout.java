package net.mahdilamb.dataviz.ui.layouts;

import net.mahdilamb.dataviz.figure.AbstractComponent;
import net.mahdilamb.dataviz.figure.LayoutManager;
import net.mahdilamb.dataviz.figure.Group;
import net.mahdilamb.dataviz.figure.Renderer;

/**
 * A layout that positions components horizontally.
 */
public class HBoxLayout extends LayoutManager {
    /**
     * Get the instance of an HBoxLayout
     */
    public static final LayoutManager INSTANCE = new HBoxLayout();

    HBoxLayout() {
    }

    @Override
    protected <T>void layoutChildren(Group node, Renderer renderer, double minX, double minY, double maxX, double maxY) {
        double start = minX;
        double height = 0;
        for (final AbstractComponent c : getChildren(node)) {
            layout(c, renderer, minX, minY, maxX, maxY);
            minX += c.getWidth();
            height = Math.max(height, c.getHeight());
        }
        setBoundsFromRect(node, start, minY, minX - start, height);

    }

}
