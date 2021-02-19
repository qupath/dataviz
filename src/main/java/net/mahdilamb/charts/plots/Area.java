package net.mahdilamb.charts.plots;

import net.mahdilamb.charts.Figure;
import net.mahdilamb.charts.Plot;
import net.mahdilamb.charts.graphics.ChartCanvas;

public final class Area extends AbstractScatter<Area> {
    boolean showEdges = true;
    boolean stacked = false;

    public Area(String name, double[] x, double[] y) {
        super(name, x, y);
    }

    public Area showEdges(final boolean edgeVisible) {
        showEdges = edgeVisible;
        return redraw();
    }

    public Area setStacked(boolean stacked) {
        this.stacked = stacked;
        // todo recalculate
        return redraw();
    }

    @Override
    protected void drawSeries(Figure<?, ?> source, ChartCanvas<?> canvas, Plot<? extends Area> plot) {

    }

    @Override
    public int size() {
        //TODO
        return 0;
    }
}
