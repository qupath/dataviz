package net.mahdilamb.charts.dataframe;

/**
 * A dataseries that contains
 *
 * @param <T> the type of the data in the series
 */
public interface NumericSeries<T extends Number & Comparable<T>> extends DataSeries<T>, SeriesWithFunctionalOperators<T> {
    /**
     * Returns whether the value at the index is NaN
     *
     * @param index the index to search
     * @return whether the value is NaN
     */
    boolean isNaN(int index);

    /**
     * @return the sum of all the values in the series
     */
    default double sum() {
        int sum = 0;
        for (int i = 0; i < size(); ++i) {
            final T val = get(i);
            if (val == null) {
                return Double.NaN;
            }
            sum += val.doubleValue();
        }
        return sum;
    }

    /**
     * @return the mean of all the values in the series
     */
    default double mean() {
        return sum() / size();
    }

}
