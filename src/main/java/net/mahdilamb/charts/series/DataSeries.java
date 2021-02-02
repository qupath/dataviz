package net.mahdilamb.charts.series;


import net.mahdilamb.charts.PlotSeries;

import java.util.*;
import java.util.function.IntPredicate;
import java.util.function.IntToDoubleFunction;
import java.util.function.Predicate;

/**
 * A series of values
 */
public interface DataSeries<T extends Comparable<T>> extends Iterable<T> {
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
     * @return a version of this series as a string series
     */
    @SuppressWarnings("unchecked")
    default StringSeries asString() {
        switch (getType()) {
            case STRING:
                return (StringSeries) this;
            case BOOLEAN:
                return new DataSeriesImpl.OfStringArray(this, el -> DataType.toString((Boolean) el));
            case LONG:
                return new DataSeriesImpl.OfStringArray(this, el -> DataType.toString((Long) el));
            case DOUBLE:
                return new DataSeriesImpl.OfStringArray(this, el -> DataType.toString((Double) el));
            default:
                throw new DataSeriesCastException();
        }
    }

    @SuppressWarnings("unchecked")
    default NumericSeries<Double> asDouble() {
        switch (getType()) {
            case DOUBLE:
                return (NumericSeries<Double>) this;
            case BOOLEAN:
                return new DataSeriesImpl.OfDoubleArray(this, el -> DataType.toDouble((Boolean) el));
            case LONG:
                return new DataSeriesImpl.OfDoubleArray((NumericSeries<Long>) this, DataType::toDouble);
            case STRING:
                return new DataSeriesImpl.OfDoubleArray(this, el -> DataType.toDouble((String) el));
            default:
                throw new DataSeriesCastException();
        }
    }

    @SuppressWarnings("unchecked")
    default NumericSeries<Long> asLong() {
        switch (getType()) {
            case LONG:
                return (NumericSeries<Long>) this;
            case DOUBLE:
                return new DataSeriesImpl.OfLongArray(this, el -> DataType.toLong((Double) el), v -> !Double.isNaN((Double) v));
            case BOOLEAN:
                return new DataSeriesImpl.OfNonNaNLongArray(this, el -> DataType.toLong((Boolean) el));
            case STRING:
                return new DataSeriesImpl.OfLongArray(this, el -> DataType.toLong((String) el), v -> DataType.LONG.matches((String) v));
            default:
                throw new DataSeriesCastException();
        }
    }

    @SuppressWarnings("unchecked")
    default BooleanSeries asBoolean() {
        switch (getType()) {
            case BOOLEAN:
                return (BooleanSeries) this;
            case STRING:
                return new DataSeriesImpl.OfBooleanArray(this, el -> DataType.toBoolean((String) el));
            case LONG:
                return new DataSeriesImpl.OfBooleanArray(this, el -> DataType.toBoolean((Long) el));
            case DOUBLE:
                return new DataSeriesImpl.OfBooleanArray(this, el -> DataType.toBoolean((Double) el));
            default:
                throw new DataSeriesCastException();
        }
    }

    default BooleanSeries asBoolean(Predicate<T> converter) {
        return new DataSeriesImpl.OfBooleanArray(this, converter);
    }

    /**
     * Element-by-element equality operation
     * <p>
     * Return a new boolean series containing the results of checking if any of the elements in this series equal
     * that of the other
     *
     * @param other the value to compare with
     * @return a boolean series containing the results of the equality operation
     */
    default BooleanSeries eq(T other) {
        return asBoolean(el -> Objects.equals(other, el));
    }

    default T[] toArray(T[] output) {
        if (output.length != size()) {
            output = Arrays.copyOf(output, size());
        }
        for (int i = 0; i < size(); ++i) {
            output[i] = get(i);
        }
        return output;
    }

    /**
     * @return the counts of each of the elements in the series
     */
    default Map<T, Integer> valueCounts() {
        //todo return data series
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
    default Map<T, Double> frequencies() {
        //todo return data series
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

    /*todo
    default LongSeries factorize() {
        final Map<T, Integer> factors = new TreeMap<>();
        int i = 0;
        for (final T s : this) {
            if (!factors.containsKey(s)) {
                factors.put(s, i++);
            }
        }
        final long[] out = new long[size()];
        for (int j = 0; j < size(); ++j) {
            out[j] = factors.get(get(j));
        }
        return new DataSeriesImpl.OfNonNaNLongArray(getName() + " [factorized]", out);
    }*/

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
    static NumericSeries<Double> of(final String name, double... data) {
        return new DataSeriesImpl.OfDoubleArray(name, data);
    }

    /**
     * Factory method to create a series from long data
     *
     * @param name the name of the series
     * @param data the data to use in the series
     * @return the default series wrapping the data
     */
    static NumericSeries<Long> of(final String name, long... data) {
        return new DataSeriesImpl.OfNonNaNLongArray(name, data);
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
    static NumericSeries<Double> of(final String name, int size, IntToDoubleFunction dataGetter) {
        return new DataSeriesImpl.OfFunctionalDouble(name, size, dataGetter);
    }

    /**
     * Get an iterable over the possible plots that can be created from a list of series
     *
     * @param series the series of interest
     * @return an iterable (set) of the possible plots from the given combinations of series
     */
    static Iterable<PlotSeries<?>> getPlotsForSeries(DataSeries<?>... series) {
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

    /**
     * Get a subset of this series
     *
     * @param start the start index (inclusive)
     * @param end   the end index (exclusive)
     * @return a sliced view into this data series
     */
    DataSeries<T> subset(int start, int end);

    /**
     * Get the first n elements
     *
     * @param n the number of elements
     * @return a sliced view into this data series
     */
    default DataSeries<T> head(int n) {
        return subset(0, n);
    }

    /**
     * @return a sliced view of the first 5 elements in the series
     */
    default DataSeries<T> head() {
        return head(5);
    }

    /**
     * Get the last n elements
     *
     * @param n the number of elements
     * @return a sliced view into this data series
     */
    default DataSeries<T> tail(int n) {
        return subset(size() - n, size());
    }

    /**
     * @return a sliced view of the last 5 elements in the series
     */
    default DataSeries<T> tail() {
        return tail(5);
    }

    /**
     * Get a subset of this series by doing a test on the indices of the series
     *
     * @param test the test on the indices
     * @return a sliced view of this series based on the test
     */
    DataSeries<T> subset(IntPredicate test);
}
