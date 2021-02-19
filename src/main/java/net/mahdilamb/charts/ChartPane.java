package net.mahdilamb.charts;

import net.mahdilamb.charts.graphics.ChartCanvas;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A chart node contains multiple chart components.
 */
public class ChartPane extends ChartNode implements Iterable<ChartComponent> {
    private List<ChartComponent> children;

    /**
     * Add a child node
     *
     * @param child the child node to add
     */
    @Override
    protected void add(final ChartComponent child) {
        if (child != null) {
            child.parent = this;
            child.figure = figure;
            markLayoutAsOld();
        } else {
            return;
        }
        if (children == null) {
            children = new ArrayList<>();
        }
        this.children.add(child);
    }

    /**
     * Remove a component from the node
     *
     * @param component the component to remove
     * @return whether the component was removed
     */
    @Override
    protected boolean remove(ChartComponent component) {
        if (component != null) {
            component.parent = null;
            component.figure = null;
            markLayoutAsOld();
        } else {
            return false;
        }
        if (children == null) {
            return false;
        }
        return children.remove(component);
    }

    /**
     * @param component the component
     * @return whether the pane contains a component
     */
    protected boolean contains(ChartComponent component) {
        if (children == null) {
            return false;
        }
        return children.contains(component);
    }

    @Override
    protected void layoutComponent(Figure<?, ?> source, double minX, double minY, double maxX, double maxY) {
        if (children == null) {
            return;
        }
        double remainingWidth = maxX - minX;
        sizeY = 0;
        sizeX = 0;
        double rowHeight = 0;
        double rowWidth = 0;
        for (final ChartComponent child : this) {
            child.layoutComponent(source, minX, minY, maxX, maxY);

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

    }

    @Override
    protected void drawComponent(Figure<?, ?> source, ChartCanvas<?> canvas) {
        if (children != null) {
            for (final ChartComponent c : children) {
                c.drawComponent(source, canvas);
            }
        }
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

    /**
     * @param index the index
     * @return the item at the index
     */
    protected ChartComponent get(int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("index cannot be less than 0");
        }
        if (children == null) {
            return null;
        }
        return children.get(index);
    }

    /**
     * @return the size of the chart node
     */
    protected int size() {
        if (children == null) {
            return 0;
        }
        return this.children.size();
    }

    @Override
    protected void markLayoutAsOld() {
        if (children != null) {
            for (final ChartComponent component : children) {
                component.markLayoutAsOld();
            }
        }
        super.markLayoutAsOld();
    }

    @Override
    protected void markDrawAsOld() {
        if (children != null) {
            for (final ChartComponent component : children) {
                component.markDrawAsOld();
            }
        }
        super.markDrawAsOld();
    }
}
