package net.mahdilamb.charts;

import net.mahdilamb.charts.graphics.ChartCanvas;

import java.util.ArrayList;
import java.util.List;

/**
 * A chart node contains multiple chart components.
 *
 * @implNote Uses a small list optimisation (only uses a list when there are
 * more than one child). The list remains once created
 */
public class ChartNode extends ChartComponent {
    private ChartComponent child;
    private List<ChartComponent> children;

    /**
     * Add a child node
     * @param child the child node to add
     */
    public void add(final ChartComponent child) {

        if (children == null) {
            if (this.child == null) {
                this.child = child;
                return;
            }
            this.children = new ArrayList<>();
            this.children.add(this.child);
            this.children.add(child);
            this.child = null;
            return;
        }
        this.children.add(child);
    }

    /**
     *
     * @return the size of the chart node
     */
    public int size() {
        if (children == null) {
            return this.child == null ? 0 : 1;
        }
        return this.children.size();
    }

    /**
     * @param index the index
     * @return the item at the index
     */
    public ChartComponent get(int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("index cannot be less than 0");
        }
        if (children == null) {
            if (child == null) {
                throw new IndexOutOfBoundsException("The node is currently empty");
            }
            return child;
        }
        return children.get(index);
    }

    /**
     * Remove a component from the node
     * @param component the component to remove
     * @return whether the component was removed
     */
    public boolean remove(ChartComponent component) {
        if (children == null) {
            if (child != null && child == component) {
                child = null;
                return true;
            }
            return false;
        }
        return children.remove(component);
    }

    @Override
    protected void calculateBounds(ChartCanvas<?> canvas, Chart<?, ?> source, double minX, double minY, double maxX, double maxY) {
        //TODO
    }

    @Override
    protected void layout(ChartCanvas<?> canvas, Chart<?, ?> source, double minX, double minY, double maxX, double maxY) {
        //TODO
    }
}
