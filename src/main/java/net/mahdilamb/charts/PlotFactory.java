package net.mahdilamb.charts;

import net.mahdilamb.charts.plots.Scatter;
import net.mahdilamb.charts.series.DataSeries;
import net.mahdilamb.charts.series.DataType;
import net.mahdilamb.charts.series.Dataset;
import net.mahdilamb.charts.series.NumericSeries;

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
        return new PlotSeriesImpl.AbstractScatter.FromArray(x, y);
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
    public static Scatter scatter(Dataset dataset, String x, String y) {
        final DataSeries<?> xSeries = dataset.get(x);
        final DataSeries<?> ySeries = dataset.get(y);
        if (DataType.isNumeric(xSeries.getType()) && DataType.isNumeric(ySeries.getType())) {
            return new PlotSeriesImpl.AbstractScatter.FromIterable((NumericSeries<?>) xSeries, (NumericSeries<?>) ySeries);
        }
        throw new UnsupportedOperationException("Both series must be numeric");
    }
}
