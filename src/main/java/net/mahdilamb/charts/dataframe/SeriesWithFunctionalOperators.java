package net.mahdilamb.charts.dataframe;

import net.mahdilamb.charts.statistics.utils.GroupBy;
import net.mahdilamb.charts.utils.Comparables;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Functional operators to be used with data series.
 *
 * @param <T> the type of the elements in the series
 * @implNote These have been abstracted away as getting a series via dataset will
 * not work for {@link DataFrame#get} as the compiler will not be able to get the type
 */
interface SeriesWithFunctionalOperators<T extends Comparable<T>> extends DataSeries<T> {
    /**
     * Convert each element to a boolean based on the test
     *
     * @param converter the test
     * @return a new boolean series produced from using the predicate on every element
     */
    default BooleanSeries asBoolean(Predicate<T> converter) {
        return new DataSeriesImpl.OfBooleanArray(this, converter);
    }

    /**
     * Element-by-element equality operation
     * <p>
     * Return a new boolean series containing the results of checking if any of the elements in this series equal
     * that of the other
     *
     * @param other the value to compare with
     * @return a boolean series containing the results of the equality operation
     */
    default BooleanSeries eq(T other) {
        return asBoolean(el -> Objects.equals(other, el));
    }

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
    /* todo
    default T reduce(ToDoubleFunction<T> test) {
        T current = get(0);
        double score = test.applyAsDouble(current);
        T best = current;
        if (size() > 1) {
            for (int i = 1; i < size(); ++i) {
                if (get(i) == null) {
                    continue;
                }
                double t = test.applyAsDouble(get(i));
                if (t >= score) {
                    best = get(i);
                    score = t;
                }
            }
        }
        return best;
    }*/

    /**
     * Reduce the series down to a external type
     *
     * @param initialize the supplier of the external type (e.g. constructor for an array list {@code <i>()->new ArrayList<>()</i>}
     * @param func       the function to apply to each element (e.g. add method <i>(arr,el) -> arr.add(el)</i>)
     * @param <S>        the type of the external object
     * @return the external object
     */
    default <S> S reduce(Supplier<S> initialize, BiConsumer<S, T> func) {
        S out = initialize.get();
        for (int i = 0; i < size(); ++i) {
            func.accept(out, get(i));
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

    default GroupBy<T> groups() {
        return new GroupBy<>(this);
    }

}
