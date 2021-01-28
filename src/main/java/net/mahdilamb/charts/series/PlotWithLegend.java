package net.mahdilamb.charts.series;

public interface PlotWithLegend<S extends PlotSeries<S>> {
    /**
     * Set whether to show this scatter series in the legend
     *
     * @param showLegend whether to show this scatter series in the legend
     * @return this scatter series
     */
    S showLegend(boolean showLegend);
}
