package net.mahdilamb.charts;

import net.mahdilamb.charts.graphics.ChartCanvas;
import net.mahdilamb.charts.graphics.Orientation;

/**
 * A chart node containing a maximum of two nodes
 */
public class PairedChartNode extends ChartNode<ChartComponent> {
    private ChartComponent a, b;
    final Orientation orientation;

    PairedChartNode(final Orientation orientation) {
        this.orientation = orientation;
    }

    protected boolean contains(ChartComponent component) {
        if (component == null) {
            return false;
        }
        return a == component || b == component;
    }


    @Override
    protected void layout(Figure<?, ?> source, ChartCanvas<?> canvas, double minX, double minY, double maxX, double maxY) {
        if (!layoutNeedsRefresh) {
            return;
        }
        sizeX = 0;
        sizeY = 0;

        switch (orientation) {
            case HORIZONTAL:
                if (a != null) {
                    a.layout(source, canvas, minX, minY, maxX, maxY);
                    sizeY = Math.max(sizeY, a.sizeY);
                    sizeX += a.sizeX;
                }
                if (b != null) {
                    b.layout(source, canvas, minX, minY, maxX, maxY);
                    sizeY = Math.max(sizeY, b.sizeY);
                    sizeX += b.sizeX;
                }
                break;
            case VERTICAL:
                if (a != null) {
                    a.layout(source, canvas, minX, minY, maxX, maxY);
                    sizeX = Math.max(sizeX, a.sizeX);
                    sizeY += a.sizeY;
                }
                if (b != null) {
                    b.layout(source, canvas, minX, minY, maxX, maxY);
                    sizeX = Math.max(sizeX, b.sizeX);
                    sizeY += b.sizeY;
                }
                break;
        }

        layoutNeedsRefresh = false;
    }

    @Override
    protected void draw(Figure<?, ?> source, ChartCanvas<?> canvas, double minX, double minY, double maxX, double maxY) {
        //TODO
    }

    @Override
    protected boolean remove(ChartComponent component) {
        if (component == null || a == null || b == null) {
            return false;
        }
        if (a == component) {
            component.parentNode = null;
            component.figure = null;
            a = b;
            b = null;
            return true;
        }
        if (b == component) {
            component.parentNode = null;
            component.figure = null;
            b = null;
            return true;
        }
        return false;
    }

    @Override
    protected void add(ChartComponent keyArea) {
        if (keyArea != null){
            keyArea.parentNode = this;
            keyArea.figure = figure;
        }
        if (a == null) {
            a = keyArea;
            return;
        }
        if (b == null) {
            b = keyArea;
            return;
        }
        throw new IndexOutOfBoundsException("Key area can only have two items");
    }
}
