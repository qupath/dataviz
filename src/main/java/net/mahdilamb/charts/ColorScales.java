package net.mahdilamb.charts;

import net.mahdilamb.charts.graphics.ChartCanvas;
import net.mahdilamb.charts.graphics.Side;

public class ColorScales extends KeyArea{
    ColorScales(Figure figure) {
        super(figure);
    }

    @Override
    protected void layoutComponent(Renderer<?> source, double minX, double minY, double maxX, double maxY) {
        for (final PlotLayout plot : figure.plots) {
            if (plot.traces.size() != 0) {
                System.out.println(">> ColorScale <<");
                for (final PlotData<?> trace : plot.traces) {
                    trace.drawColorScales();
                }
            }
        }
    }

    @Override
    protected void drawComponent(Renderer<?> source, ChartCanvas<?> canvas) {

    }
}
