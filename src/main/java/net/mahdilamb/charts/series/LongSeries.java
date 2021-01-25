package net.mahdilamb.charts.series;

import java.util.PrimitiveIterator;

/**
 * A series of ordered long data
 */
public interface LongSeries extends NumericSeries<Long> {
    @Override
    default Long val(int index) {
        if (isNaN(index)) {
            return null;
        }
        return get(index);
    }

    /**
     * Get the data element at the index
     *
     * @param index the index to get the data
     * @return the data at the index
     */
    long get(int index);

    @Override
    default SeriesType getType() {
        return SeriesType.INTEGER;
    }

    @Override
    default PrimitiveIterator.OfLong iterator() {
        return new PrimitiveIterator.OfLong() {
            private int i = 0;

            @Override
            public long nextLong() {
                return get(i++);
            }

            @Override
            public boolean hasNext() {
                return i < size();
            }
        };
    }

}
