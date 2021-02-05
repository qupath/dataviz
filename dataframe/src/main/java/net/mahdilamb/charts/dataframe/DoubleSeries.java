package net.mahdilamb.charts.dataframe;

import java.util.PrimitiveIterator;

/**
 * A series of ordered double data
 */
public interface DoubleSeries extends NumericSeries<Double> {
    /**
     * Get the data element at the index
     *
     * @param index the index to get the data
     * @return the data at the index
     */
    double getDouble(int index);

    @Override
    default Double get(int index) {
        return getDouble(index);
    }

    @Override
    default boolean isNaN(int index) {
        return Double.isNaN(getDouble(index));
    }

    @Override
    default DataType getType() {
        return DataType.DOUBLE;
    }

    @Override
    default PrimitiveIterator.OfDouble iterator() {
        return new PrimitiveIterator.OfDouble() {
            private int i = 0;

            @Override
            public double nextDouble() {
                return getDouble(i++);
            }

            @Override
            public boolean hasNext() {
                return i < size();
            }
        };
    }

    default double[] toArray(double[] output) {
        if (output.length < size()) {
            output = new double[size()];
        }
        for (int i = 0; i < size(); ++i) {
            output[i] = getDouble(i);
        }
        return output;
    }

}
