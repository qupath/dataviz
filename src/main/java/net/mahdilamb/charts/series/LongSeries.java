package net.mahdilamb.charts.series;

import java.util.PrimitiveIterator;

/**
 * A series of ordered long data
 */
public interface LongSeries extends NumericSeries<Long> {

    /**
     * Get the data element at the index
     *
     * @param index the index to get the data
     * @return the data at the index
     */
    long getLong(int index);

    @Override
    default Long get(int index) {
        if (isNaN(index)) {
            return null;
        }
        return getLong(index);
    }

    @Override
    default DataType getType() {
        return DataType.LONG;
    }

    @Override
    default PrimitiveIterator.OfLong iterator() {
        return new PrimitiveIterator.OfLong() {
            private int i = 0;

            @Override
            public long nextLong() {
                return getLong(i++);
            }

            @Override
            public boolean hasNext() {
                return i < size();
            }
        };
    }

}
