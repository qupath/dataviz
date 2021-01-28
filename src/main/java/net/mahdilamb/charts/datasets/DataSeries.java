package net.mahdilamb.charts.datasets;


import net.mahdilamb.charts.plots.Plot;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.function.IntToDoubleFunction;

/**
 * A series of values
 */
public interface DataSeries<T> extends Iterable<T> {
    /**
     * Get the value at a specific index
     *
     * @param index the index
     * @return the value of interest. Note for numeric types, this will result in boxing of the primitive type
     */
    T get(int index);

    /**
     * @return the type of the series
     */
    DataType getType();

    /**
     * @return the size of the series
     */
    int size();

    /**
     * @return the name of the series
     */
    String getName();

    @Override
    default Iterator<T> iterator() {
        return new Iterator<>() {
            private int i = 0;

            @Override
            public boolean hasNext() {
                return i < size();
            }

            @Override
            public T next() {
                return get(i++);
            }
        };
    }

    /**
     * @return the counts of each of the elements in the series
     */
    default Map<T, Integer> counts() {
        final Map<T, Integer> map = new Hashtable<>();
        for (int i = 0; i < size(); ++i) {
            final Integer key = map.get(get(i));
            if (key == null) {
                map.put(get(i), 1);
                continue;
            }
            map.put(get(i), key + 1);
        }
        return map;
    }

    /**
     * @return the frequency of each of the elements in the series
     */
    default Map<T, Double> frequency() {
        final Map<T, Double> map = new Hashtable<>();
        for (int i = 0; i < size(); ++i) {
            final Double key = map.get(get(i));
            if (key == null) {
                map.put(get(i), 1. / size());
                continue;
            }
            map.put(get(i), key + (1. / size()));
        }
        return map;
    }

    /**
     * Create a string series from an array of strings
     *
     * @param name   the name of the series
     * @param values the values of the series
     * @return a string series of the values
     */
    static StringSeries of(final String name, final String... values) {
        return new DataSeriesImpl.OfStringArray(name, values);
    }

    /**
     * Factory method to create a series from double data
     *
     * @param name the name of the series
     * @param data the data to use in the series
     * @return the default series wrapping the data
     */
    static DoubleSeries of(final String name, double... data) {
        return new DataSeriesImpl.OfDoubleArray(name, data);
    }

    /**
     * Factory method to create a series from a "collection" of objects.
     * <p>
     * This enables the generation of a series from a collection of objects without having to implement the interface
     *
     * @param name       the name of the series
     * @param size       the size of the series
     * @param dataGetter a function which gets a double element at a position in the series
     * @return a series from a collection of objects
     */
    static DoubleSeries of(final String name, int size, IntToDoubleFunction dataGetter) {
        return new DataSeriesImpl.OfFunctionalDouble(name, size, dataGetter);
    }

    /**
     * Get an iterable over the possible plots that can be created from a list of series
     *
     * @param series the series of interest
     * @return an iterable (set) of the possible plots from the given combinations of series
     */
    static Iterable<Plot<?>> getPlotsForSeries(DataSeries<?>... series) {
        if (DataSeriesImpl.seriesToPlotMap.isEmpty()) {
            //TODO look at the arguments in the constructors in plots and match to interfaces and add to map
            //TODO Ignore Series.java. Only use interfaces
        }
        final DataType[] arr = new DataType[series.length];
        for (int i = 0; i < series.length; ++i) {
            arr[i] = series[i].getType();
        }
        return DataSeriesImpl.seriesToPlotMap.get(new DataSeriesImpl.CompatibleSeries(arr));
    }

}
