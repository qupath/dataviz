package net.mahdilamb.charts;

import net.mahdilamb.charts.graphics.ChartCanvas;
import net.mahdilamb.charts.graphics.Orientation;
import net.mahdilamb.charts.graphics.Side;

/**
 * A chart node containing a maximum of two nodes
 */
final class ChartKeyNode extends ChartNode {
    private LegendImpl<?> legend;
    private ColorScaleImpl<?> colorScale;
    final Orientation orientation;
    boolean drawLegendFirst;

    ChartKeyNode(final Side side) {
        switch (side) {
            case LEFT:
                drawLegendFirst = false;
                orientation = Orientation.VERTICAL;
                break;
            case RIGHT:
                drawLegendFirst = true;
                orientation = Orientation.VERTICAL;
                break;
            case TOP:
                drawLegendFirst = false;
                orientation = Orientation.HORIZONTAL;
                break;
            case BOTTOM:
                drawLegendFirst = true;
                orientation = Orientation.HORIZONTAL;
                break;
            default:
                throw new UnsupportedOperationException();

        }
    }

    ChartKeyNode() {
        orientation = null;
        drawLegendFirst = false;
    }

    @Override
    protected void layoutComponent(Figure<?, ?> source, double minX, double minY, double maxX, double maxY) {
        sizeX = 0;
        sizeY = 0;
        if (orientation == null) {
            return;
        }
        KeyAreaImpl<?> first = drawLegendFirst ? legend : colorScale;
        KeyAreaImpl<?> last = drawLegendFirst ? colorScale : legend;

        switch (orientation) {
            case HORIZONTAL:
                if (first != null) {
                    first.layoutComponent(source, minX, minY, maxX, maxY);
                    sizeY = Math.max(sizeY, first.sizeY + first.getOffsetY());
                    sizeX += first.sizeX;
                }
                if (last != null) {
                    last.layoutComponent(source, minX, minY, maxX, maxY);
                    sizeY = Math.max(sizeY, last.sizeY + last.getOffsetY());
                    sizeX += last.sizeX;
                }
                break;
            case VERTICAL:
                if (first != null) {
                    first.layoutComponent(source, minX, minY, maxX, maxY);
                    sizeX = Math.max(sizeX, first.sizeX + first.getOffsetX());
                    sizeY += first.sizeY;
                }
                if (last != null) {
                    last.layoutComponent(source, minX, minY, maxX, maxY);
                    sizeX = Math.max(sizeX, last.sizeX + last.getOffsetX());
                    sizeY += last.sizeY;
                }
                break;
        }

    }

    /**
     * Performed after initial layout
     */
    void alignChildren(Figure<?, ?> source, double height, double width) {
        if (colorScale != null) {
            colorScale.align(source, height, width);
        }
        if (legend != null) {
            legend.align(source, height, width);
        }
    }

    @Override
    protected void drawComponent(Figure<?, ?> source, ChartCanvas<?> canvas) {
        if (colorScale != null) {
            colorScale.drawComponent(source, canvas);

        }
        if (legend != null) {
            legend.drawComponent(source, canvas);
        }

    }

    /**
     * Add a key
     *
     * @param key the key
     */
    public void add(KeyAreaImpl<?> key) {
        if (key == null) {
            return;
        }
        key.parent = this;
        if (key.getClass() == LegendImpl.class) {
            add((LegendImpl<?>) key);
        } else {
            add((ColorScaleImpl<?>) key);
        }
    }

    /**
     * Remove a key
     *
     * @param key the key
     */
    public void remove(KeyAreaImpl<?> key) {
        if (key == null) {
            return;
        }
        key.parent = null;
        if (key.getClass() == LegendImpl.class) {
            remove((LegendImpl<?>) key);
        } else {
            remove((ColorScaleImpl<?>) key);
        }
    }

    @Override
    protected void markLayoutAsOld() {
        if (colorScale != null) {
            colorScale.markLayoutAsOld();
        }
        if (legend != null) {
            legend.markLayoutAsOld();
        }
        super.markLayoutAsOld();
    }

    @Override
    protected void markDrawAsOld() {
        if (colorScale != null) {
            colorScale.markDrawAsOld();
        }
        if (legend != null) {
            legend.markDrawAsOld();
        }
        super.markDrawAsOld();
    }

    private void add(ColorScaleImpl<?> colorScale) {
        this.colorScale = colorScale;
        markLayoutAsOld();
    }

    private void remove(ColorScaleImpl<?> colorScale) {
        if (this.colorScale != colorScale) {
            return;
        }
        this.colorScale = null;
        markLayoutAsOld();
    }

    private void add(LegendImpl<?> legend) {
        this.legend = legend;
        markLayoutAsOld();
    }

    private void remove(LegendImpl<?> legend) {
        if (this.legend != legend) {
            return;
        }
        this.legend = null;
        markLayoutAsOld();
    }

    @Override
    protected boolean remove(ChartComponent component) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void add(ChartComponent component) {
        throw new UnsupportedOperationException();
    }
}
