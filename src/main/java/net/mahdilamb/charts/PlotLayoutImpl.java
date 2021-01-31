package net.mahdilamb.charts;

import net.mahdilamb.charts.layouts.PlotLayout;

abstract class PlotLayoutImpl<S > implements PlotLayout<S> {
    double minorTickSpacing, majorTickSpacing;
    boolean showMinorTicks = true, showMajorTicks = true;

    protected double x, y, width, height;

    Chart<?, ?> chart;
    private final S[] series;
    @SafeVarargs
    PlotLayoutImpl(S... series){
        this.series = series;
    }


    /**
     * Store the last parameters for chart exporter
     *
     * @param x      the x position of the plot area
     * @param y      the y position of the plot area
     * @param width  the width of the plot area
     * @param height the height of the plot area
     */
    final void layout(double x, double y, double width, double height) {

        //TODO get axis dims and update
        layoutPlot(x, y, width, height);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    protected abstract void layoutPlot(double x, double y, double width, double height);

    protected abstract Iterable<LegendImpl.LegendItem> getLegendItems();

    protected abstract void layoutAxes();

    @Override
    public S get(int series) {
        return this.series[series];
    }

    @Override
    public int numSeries() {
        return series.length;
    }
}
