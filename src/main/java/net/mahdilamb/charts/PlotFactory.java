package net.mahdilamb.charts;

import net.mahdilamb.charts.dataframe.DataFrame;
import net.mahdilamb.charts.plots.Scatter;
import net.mahdilamb.charts.plots.dataframe.DataframeScatter;

//TODO make series using Pattern matching in datasets e.g. for doing replicates
public final class PlotFactory {
    private PlotFactory() {

    }

    /**
     * Create a scatter series from an array of x and y data
     *
     * @param x the x data
     * @param y the y data
     * @return the scatter series
     */
    public static Scatter scatter(double[] x, double[] y) {
        return new Scatter(null, x, y);
    }

    /**
     * Create a scatter series from a dataset
     *
     * @param dataset the dataset
     * @param x       the name of the series containing the x data
     * @param y       the name of the series containing the y data
     * @return the scatter series
     * @throws UnsupportedOperationException of either series is not numeric
     * @throws NullPointerException          if the series cannot be found
     */
    public static DataframeScatter scatter(DataFrame dataset, String x, String y) {
        return new DataframeScatter(dataset, x, y);
    }

    public static DataframeScatter scatter(DataFrame dataset, String x, String y, String colorBy) {
        return scatter(dataset, x, y).setColors(colorBy);
    }


}
