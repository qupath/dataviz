package net.mahdilamb.charts.series;

import net.mahdilamb.charts.plots.Plot;

/**
 * A series of values
 */
public interface Series<T> extends Iterable<T> {
    /**
     * Get the value at a specific index
     *
     * @param index the index
     * @return the value of interest. Note for numeric types, this will result in boxing of the primitive type
     */
    T val(int index);

    /**
     * @return the type of the series
     */
    SeriesType getType();

    /**
     * @return the size of the series
     */
    int size();

    /**
     * @return the name of the series
     */
    String getName();

    /**
     * Create a string series from an array of strings
     *
     * @param name   the name of the series
     * @param values the values of the series
     * @return a string series of the values
     */
    static StringSeries of(final String name, final String... values) {
        return new SeriesImpl.OfStringArray(name, values);
    }

    /**
     * Create a double series from an array of doubles
     *
     * @param name   the name of the series
     * @param values the values of the series
     * @return a double series
     */
    static DoubleSeries of(final String name, final double... values) {
        return DoubleSeries.of(name, values);
    }

    /**
     * Get an iterable over the possible plots that can be created from a list of series
     *
     * @param series the series of interest
     * @return an iterable (set) of the possible plots from the given combinations of series
     */
    static Iterable<Plot> getPlotsForSeries(Series<?>... series) {
        if (SeriesImpl.seriesToPlotMap.isEmpty()) {
            //TODO look at the arguments in the constructors in plots and match to interfaces and add to map
            //TODO Ignore Series.java. Only use interfaces
        }
        final SeriesType[] arr = new SeriesType[series.length];
        for (int i = 0; i < series.length; ++i) {
            arr[i] = series[i].getType();
        }

        return SeriesImpl.seriesToPlotMap.get(new SeriesImpl.CompatibleSeries(arr));
    }

}
