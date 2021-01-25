package net.mahdilamb.charts.series;

import java.util.PrimitiveIterator;
import java.util.function.IntToDoubleFunction;

/**
 * A series of ordered double data
 */
public interface DoubleSeries extends NumericSeries<Double> {
    @Override
    default Double val(int index) {
        return get(index);
    }

    /**
     * Get the data element at the index
     *
     * @param index the index to get the data
     * @return the data at the index
     */
    double get(int index);

    @Override
    default boolean isNaN(int index) {
        return Double.isNaN(get(index));
    }

    @Override
    default SeriesType getType() {
        return SeriesType.DOUBLE;
    }

    @Override
    default PrimitiveIterator.OfDouble iterator() {
        return new PrimitiveIterator.OfDouble() {
            private int i = 0;

            @Override
            public double nextDouble() {
                return get(i++);
            }

            @Override
            public boolean hasNext() {
                return i < size();
            }
        };
    }

    /**
     * Factory method to create a series from double data
     *
     * @param name the name of the series
     * @param data the data to use in the series
     * @return the default series wrapping the data
     */
    static DoubleSeries of(final String name, double... data) {
        return new SeriesImpl.OfDoubleArray(name, data);
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
        return new SeriesImpl.OfFunctionalDouble(name, size, dataGetter);
    }

}
