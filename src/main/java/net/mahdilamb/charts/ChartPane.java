package net.mahdilamb.charts;

import net.mahdilamb.charts.graphics.ChartCanvas;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A chart node contains multiple chart components.
 *
 * @implNote Uses small list optimisation (only uses a list when there are
 * more than one child). The list remains once created
 */
public class ChartPane extends ChartNode<ChartComponent> implements Iterable<ChartComponent> {
    private ChartComponent child;
    private List<ChartComponent> children;

    /**
     * Add a child node
     *
     * @param child the child node to add
     */
    protected void add(final ChartComponent child) {
        if (child != null) {
            child.parentNode = this;
            child.figure = figure;
            layoutNeedsRefresh = true;
        } else {
            return;
        }
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
        if (component != null) {
            component.parentNode = null;
            component.figure = null;
            layoutNeedsRefresh = true;
        } else {
            return false;
        }
        if (children == null) {
            if (child != null && child == component) {
                child = null;
                return true;
            }
            return false;
        }
        return children.remove(component);
    }

    protected boolean contains(ChartComponent component) {
        if (children == null) {
            return child != null && child == component;
        }
        return children.contains(component);
    }


    @Override
    protected void layout(Figure<?, ?> source, ChartCanvas<?> canvas, double minX, double minY, double maxX, double maxY) {
        if (!layoutNeedsRefresh) {
            return;
        }
        double remainingWidth = maxX - minX;
        sizeY = 0;
        sizeX = 0;
        double rowHeight = 0;
        double rowWidth = 0;
        for (final ChartComponent child : this) {
            if (child.layoutNeedsRefresh) {
                child.layout(source, canvas, minX, minY, maxX, maxY);
            }
            if (child.inline) {
                rowWidth += child.sizeX;

                if (child.sizeX > remainingWidth) {
                    sizeX = Math.max(maxX - minX - remainingWidth, child.sizeX);
                    sizeY += child.sizeY;
                    rowHeight = child.sizeY;
                    remainingWidth = maxX - minX;
                } else {
                    rowHeight = Math.max(rowHeight, child.sizeY);

                }
                remainingWidth -= child.sizeX;
            } else {
                sizeX = Math.max(sizeX, child.sizeX);
                sizeY += rowHeight;
                rowHeight = child.sizeY;
                rowWidth = child.sizeX;
                remainingWidth = maxX - minX;

            }
        }

        sizeY += rowHeight;
        sizeX = rowWidth;
        layoutNeedsRefresh = false;
    }

    @Override
    protected void draw(Figure<?, ?> source, ChartCanvas<?> canvas, double minX, double minY, double maxX, double maxY) {
        //TODO
    }

    @Override
    public Iterator<ChartComponent> iterator() {
        return new Iterator<>() {
            private int i = 0;

            @Override
            public boolean hasNext() {
                return i < size();
            }

            @Override
            public ChartComponent next() {
                return get(i++);
            }
        };
    }
}
