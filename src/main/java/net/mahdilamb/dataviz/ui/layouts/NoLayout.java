package net.mahdilamb.dataviz.ui.layouts;

import net.mahdilamb.dataviz.figure.AbstractComponent;
import net.mahdilamb.dataviz.figure.LayoutManager;
import net.mahdilamb.dataviz.figure.Group;
import net.mahdilamb.dataviz.figure.Renderer;

/**
 * A layout that let's children layout by themselves
 */
public class NoLayout extends LayoutManager {
    /**
     * Get the instance of an HBoxLayout
     */
    public static final LayoutManager INSTANCE = new NoLayout();

    NoLayout() {
    }

    @Override
    protected <T>void layoutChildren(Group node, Renderer renderer, double minX, double minY, double maxX, double maxY) {
        for (final AbstractComponent c : getChildren(node)) {
            layout(c, renderer, minX, minY, maxX, maxY);
        }
        setBoundsFromRect(node, minX, minY, maxX - minX, maxY - minY);

    }

}
