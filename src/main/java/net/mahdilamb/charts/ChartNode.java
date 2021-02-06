package net.mahdilamb.charts;

import java.util.ArrayList;
import java.util.List;

/**
 * A chart node contains multiple chart components.
 *
 * @implNote Uses a small list optimisation (only uses a list when there are
 * more than one child). The list remains once created
 */
public abstract class ChartNode extends ChartComponent {
    private ChartComponent child;
    private List<ChartComponent> children;

    /**
     * Add a child node
     *
     * @param child the child node to add
     */
    protected void add(final ChartComponent child) {

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
     * @return the size of the chart node
     */
    protected int size() {
        if (children == null) {
            return this.child == null ? 0 : 1;
        }
        return this.children.size();
    }

    /**
     * @param index the index
     * @return the item at the index
     */
    protected ChartComponent get(int index) {
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
     *
     * @param component the component to remove
     * @return whether the component was removed
     */
    protected boolean remove(ChartComponent component) {
        if (children == null) {
            if (child != null && child == component) {
                child = null;
                return true;
            }
            return false;
        }
        return children.remove(component);
    }


}
