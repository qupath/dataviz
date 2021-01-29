package net.mahdilamb.charts.series;

import net.mahdilamb.utils.functions.PrimitiveIterators;

/**
 * A boolean series
 */
public interface BooleanSeries extends DataSeries<Boolean>, SeriesWithFunctionalOperators<Boolean> {

    /**
     * @param index the index
     * @return the value at the index
     */
    boolean getBoolean(int index);

    @Override
    default Boolean get(int index) {
        return getBoolean(index);
    }

    @Override
    default DataType getType() {
        return DataType.BOOLEAN;
    }

    @Override
    default PrimitiveIterators.OfBoolean iterator() {
        return new PrimitiveIterators.OfBoolean() {
            private int i = 0;

            @Override
            public boolean nextBoolean() {
                return i < size();
            }

            @Override
            public boolean hasNext() {
                return getBoolean(i++);
            }
        };
    }

}
