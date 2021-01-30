package net.mahdilamb.charts.layouts;


/**
 * A plot used in the chart
 */
public interface PlotLayout<S> {
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
