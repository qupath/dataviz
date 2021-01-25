package net.mahdilamb.charts.series;

import java.util.Iterator;

/**
 * A series of strings
 */
public interface StringSeries extends Series<String> {
    @Override
    default String val(int index) {
        return get(index);
    }

    /**
     * Get the string at the specified index
     *
     * @param index the index of interest
     * @return the value of the series at the index
     */
    String get(int index);

    @Override
    default SeriesType getType() {
        return SeriesType.STRING;
    }

    @Override
    default Iterator<String> iterator() {
        return new Iterator<>() {
            private int i = 0;

            @Override
            public boolean hasNext() {
                return i < size();
            }

            @Override
            public String next() {
                return get(i++);
            }
        };
    }

}
