package net.mahdilamb.charts.datasets;

import java.util.function.DoubleBinaryOperator;
import java.util.function.DoublePredicate;

/**
 * A dataseries that contains
 *
 * @param <T> the type of the data in the series
 */
public interface NumericSeries<T extends Number> extends DataSeries<T> {
    /**
     * Returns whether the value at the index is NaN
     *
     * @param index the index to search
     * @return whether the value is NaN
     */
    boolean isNaN(int index);

    /**
     * Reduce the series to a single double value
     *
     * @param func the function to apply
     * @return the reduced value
     */
    default double reduce(DoubleBinaryOperator func) {
        double out = isNaN(0) ? Double.NaN : get(0).doubleValue();
        if (size() > 1) {
            for (int i = 1; i < size(); ++i) {
                out = func.applyAsDouble(out, isNaN(i) ? Double.NaN : get(i).doubleValue());
            }
        }
        return out;
    }

    /**
     * Reduce the series to a single double
     *
     * @param func            the function to apply to each element
     * @param earlyTerminator the value of a sentinel. I.e. once this test passes, then the loop is terminated early
     * @return the reduced value
     */
    default double reduce(DoubleBinaryOperator func, DoublePredicate earlyTerminator) {
        double out = isNaN(0) ? Double.NaN : get(0).doubleValue();
        if (size() > 1) {
            for (int i = 1; i < size(); ++i) {
                out = func.applyAsDouble(out, isNaN(i) ? Double.NaN : get(i).doubleValue());
                if (earlyTerminator.test(out)) {
                    return out;
                }
            }
        }
        return out;
    }

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
