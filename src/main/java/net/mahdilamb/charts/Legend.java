package net.mahdilamb.charts;

import net.mahdilamb.charts.graphics.ChartCanvas;
import net.mahdilamb.charts.graphics.Orientation;
import net.mahdilamb.charts.graphics.VAlign;
import net.mahdilamb.charts.graphics.shapes.Marker;

public class Legend extends KeyArea {

    Legend(Figure figure) {
        super(figure);
    }

    @Override
    protected void layoutComponent(Renderer<?> source, double minX, double minY, double maxX, double maxY) {
        if (orientation == Orientation.VERTICAL) {
            //TODO deal with title
            double lineHeight = source.getTextLineHeight(itemFont);
            sizeY = 0;
            sizeX = 0;
            for (final PlotLayout plot : figure.plots) {
                if (plot.traces.size() != 0) {
                    for (final PlotData<?> trace : plot.traces) {
                        trace.layoutLegendItems(this, source, lineHeight);
                    }
                }
            }
            //todo add title height
            switch (side) {
                case RIGHT:
                    posX = maxX - sizeX;
                    posY = minY;
                    if (vAlign != VAlign.TOP) {
                        double height = maxY - minY;
                        if (vAlign == VAlign.MIDDLE) {
                            posY += .5 * height - .5 * sizeY;
                        } else {
                            posY += height - sizeY;
                        }
                    }
                    break;
                default:
                    throw new UnsupportedOperationException();
            }

        } else {
            throw new UnsupportedOperationException();
        }

    }

    @Override
    protected void drawComponent(Renderer<?> source, ChartCanvas<?> canvas) {
        double lineHeight = source.getTextLineHeight(itemFont);
        double baseOffset = source.getTextBaselineOffset(itemFont);
        canvas.setFont(itemFont);
        for (final PlotLayout plot : figure.plots) {
            if (plot.traces.size() != 0) {
                for (final PlotData<?> trace : plot.traces) {
                    trace.drawLegendItems(this, source, canvas, lineHeight, baseOffset);
                }
            }
        }
    }
}
