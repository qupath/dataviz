package net.mahdilamb.charts;

import net.mahdilamb.charts.plots.Plot;
import net.mahdilamb.charts.series.PlotSeries;
import net.mahdilamb.charts.styles.Orientation;
import net.mahdilamb.charts.styles.Text;

abstract class PlotImpl<S extends PlotSeries<S>> implements Plot<S> {
    double minorTickSpacing, majorTickSpacing;
    boolean showMinorTicks = true, showMajorTicks = true;

    protected double x, y, width, height;

    Chart<?, ?> chart;
    private final S[] series;
    @SafeVarargs
    PlotImpl(S... series){
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

    protected abstract Iterable<Legend.LegendItem> getLegendItems();

    protected abstract void layoutAxes();

    protected double getTextHeight(Text text) {
        return chart.getTextHeight(text);
    }

    protected double getTextWidth(Text text) {
        return chart.getTextWidth(text);
    }

    @Override
    public S get(int series) {
        return this.series[series];
    }

    @Override
    public int numSeries() {
        return series.length;
    }
}
