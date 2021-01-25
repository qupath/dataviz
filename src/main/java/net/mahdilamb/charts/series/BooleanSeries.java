package net.mahdilamb.charts.series;

import net.mahdilamb.utils.functions.PrimitiveIterators;

import java.util.Iterator;

/**
 * A boolean series
 */
public interface BooleanSeries extends Series<Boolean> {

    /**
     * @param index the index
     * @return the value at the index
     */
    boolean get(int index);

    @Override
    default Boolean val(int index) {
        return get(index);
    }

    @Override
    default SeriesType getType() {
        return SeriesType.BOOLEAN;
    }

    @Override
    default Iterator<Boolean> iterator() {
        return new PrimitiveIterators.OfBoolean() {
            private int i = 0;

            @Override
            public boolean nextBoolean() {
                return i < size();
            }

            @Override
            public boolean hasNext() {
                return get(i++);
            }
        };
    }

}
