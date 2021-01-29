package net.mahdilamb.charts.layouts;

import net.mahdilamb.charts.plots.PlotSeries;

/**
 * A plot used in the chart
 */
public interface Plot<S extends PlotSeries<S>> {
    /**
     * @param series the series to get
     * @return the series at the given index
     */
    S get(int series);

    /**
     * @return the number of series in the plot
     */
    int numSeries();
}
