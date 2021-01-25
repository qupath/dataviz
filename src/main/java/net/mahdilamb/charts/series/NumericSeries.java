package net.mahdilamb.charts.series;

public interface NumericSeries<T extends Number> extends Series<T> {
    /**
     * Returns whether the value at the index is NaN
     *
     * @param index the index to search
     * @return whether the value is NaN
     */
    boolean isNaN(int index);
}
