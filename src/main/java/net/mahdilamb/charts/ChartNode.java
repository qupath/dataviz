package net.mahdilamb.charts;

/**
 * A hierarchical component in the figure
 *
 */
public abstract class ChartNode extends ChartComponent {
    /**
     * Remove a component from this node
     *
     * @param component the component
     * @return whether the component was removed
     */
    protected abstract boolean remove(ChartComponent component);

    /**
     * Add a component
     *
     * @param component the component to add
     */
    protected abstract void add(ChartComponent component);
}
