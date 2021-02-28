package net.mahdilamb.charts;

import net.mahdilamb.charts.graphics.ChartCanvas;
import net.mahdilamb.charts.graphics.Orientation;
import net.mahdilamb.charts.graphics.Side;

class KeyAreaNode extends Node {
    Legend legend;
    ColorScales colorScale;
    final Orientation orientation;
    boolean drawLegendFirst;

    KeyAreaNode(final Side side) {
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

    KeyAreaNode() {
        orientation = null;
        drawLegendFirst = false;
    }

    @Override
    protected void layoutComponent(Renderer<?> source, double minX, double minY, double maxX, double maxY) {
        sizeX = 0;
        sizeY = 0;
        if (orientation == null) {
            return;
        }
        KeyArea first = drawLegendFirst ? legend : colorScale;
        KeyArea last = drawLegendFirst ? colorScale : legend;

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

    @Override
    protected void drawComponent(Renderer<?> source, ChartCanvas<?> canvas) {

    }

    @Override
    protected boolean remove(Component component) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void add(Component component) {
        throw new UnsupportedOperationException();
    }
}
