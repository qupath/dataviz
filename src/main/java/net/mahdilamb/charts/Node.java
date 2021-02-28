package net.mahdilamb.charts;

/**
 * A hierarchical component in the figure
 *
 */
abstract class Node extends Component {
    /**
     * Remove a component from this node
     *
     * @param component the component
     * @return whether the component was removed
     */
    protected abstract boolean remove(Component component);

    /**
     * Add a component
     *
     * @param component the component to add
     */
    protected abstract void add(Component component);
}
