package net.mahdilamb.dataviz.figure;

import java.util.List;

/**
 * A layout manager controls the positions of child nodes
 */
public abstract class LayoutManager {
    /**
     * Layout the given children according to the rules in the layout manager
     *
     * @param node     the node to layout
     * @param renderer the renderer
     * @param minX     the minimum x position
     * @param minY     the minimum y position
     * @param maxX     the maximum x position
     * @param maxY     the maximum y position
     */
    protected abstract <T> void layoutChildren(Group node, Renderer renderer, double minX, double minY, double maxX, double maxY);

    /**
     * Layout a component using the given renderer on a canvas
     *
     * @param component the component
     * @param renderer  the renderer
     * @param minX      the requested minX
     * @param minY      the requested minY
     * @param maxX      the requested maxX
     * @param maxY      the requested maxY
     */
    protected static <T> void layoutComponent(AbstractComponent component, Renderer renderer, double minX, double minY, double maxX, double maxY) {
        component.layoutComponent(renderer, minX, minY, maxX, maxY);
    }

    /**
     * Layout a component using the given renderer on a canvas
     *
     * @param component the component
     * @param renderer  the renderer
     * @param minX      the requested minX
     * @param minY      the requested minY
     * @param maxX      the requested maxX
     * @param maxY      the requested maxY
     */
    protected static <T> void layout(AbstractComponent component, Renderer renderer, double minX, double minY, double maxX, double maxY) {
        component.layout(renderer, minX, minY, maxX, maxY);
    }

    protected static List<AbstractComponent> getChildren(final Group node) {
        return node.getChildren();
    }

    protected void setBoundsFromRect(final AbstractComponent component, double x, double y, double width, double height) {
        component.setBoundsFromRect(x, y, width, height);
    }

}
    
