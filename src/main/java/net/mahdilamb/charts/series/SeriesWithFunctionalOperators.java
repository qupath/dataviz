package net.mahdilamb.charts.series;

import net.mahdilamb.charts.utils.Comparables;

import java.util.function.BinaryOperator;
import java.util.function.Predicate;

/**
 * Functional operators to be used with data series. These have been abstracted away as getting a series via dataset will
 * not work for {@link Dataset#get} as the comparator cannot be retrieved for an unknown type
 *
 * @param <T> the type of the elements in the series
 */
interface SeriesWithFunctionalOperators<T extends Comparable<T>> extends DataSeries<T> {

    /**
     * Reduce the series to a single double value
     *
     * @param func the function to apply
     * @return the reduced value
     */
    default T reduce(BinaryOperator<T> func) {
        T out = get(0);
        if (size() > 1) {
            for (int i = 1; i < size(); ++i) {
                if (get(i) == null) {
                    continue;
                }
                out = out == null ? get(i) : func.apply(out, get(i));
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
    default T reduce(BinaryOperator<T> func, Predicate<T> earlyTerminator) {
        T out = get(0);
        if (earlyTerminator.test(out)) {
            return out;
        }
        if (size() > 1) {
            for (int i = 1; i < size(); ++i) {
                if (get(i) == null) {
                    continue;
                }
                out = out == null ? get(i) : func.apply(out, get(i));
                if (earlyTerminator.test(out)) {
                    return out;
                }
            }
        }
        return out;
    }

    /**
     * @return the minimum element in the series
     */
    default T min() {
        return reduce(Comparables::min);
    }

    /**
     * @return the maximum element in the series
     */
    default T max() {
        return reduce(Comparables::max);
    }

}
