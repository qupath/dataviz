package net.mahdilamb.charts.utils;

import java.util.Iterator;
import java.util.function.*;

/**
 * Utility class to work with arrays and iterables
 */
public final class ArrayUtils {
    private ArrayUtils() {
    }

    /**
     * Fill a double array using an iterable as a source. If the length of the iterable is shorter than the array,
     * the rest of the values are filled with a default value
     *
     * @param dest         the destination array
     * @param source       the source iterable
     * @param defaultValue the default value
     * @return the destination array
     */
    public static double[] fill(double[] dest, Iterable<Double> source, double defaultValue) {
        final Iterator<Double> it = source.iterator();
        for (int i = 0; i < dest.length; ++i) {
            dest[i] = it.hasNext() ? it.next() : defaultValue;
        }
        return dest;
    }

    public static double[] fill(double[] dest, Iterable<Double> source, DoubleUnaryOperator func, double defaultValue) {
        final Iterator<Double> it = source.iterator();
        for (int i = 0; i < dest.length; ++i) {
            dest[i] = it.hasNext() ? func.applyAsDouble(it.next()) : defaultValue;
        }
        return dest;
    }

    public static double[] fill(double[] dest, Iterable<Double> lhs, Iterable<Double> rhs, DoubleBinaryOperator func, double defaultValue) {
        final Iterator<Double> l = lhs.iterator(),
                r = rhs.iterator();
        for (int i = 0; i < dest.length; ++i) {
            dest[i] = l.hasNext() && r.hasNext() ? func.applyAsDouble(l.next(), r.next()) : defaultValue;
        }
        return dest;
    }

    public static double[] fill(double[] dest, IntToDoubleFunction func) {
        for (int i = 0; i < dest.length; ++i) {
            dest[i] = func.applyAsDouble(i);
        }
        return dest;
    }

    public static <T> double[] fill(double[] dest, Iterable<T> lhs, ToDoubleFunction<T> lhsExtractor, Iterable<Double> rhs, DoubleBinaryOperator func, double defaultValue) {
        final Iterator<T> l = lhs.iterator();
        final Iterator<Double> r = rhs.iterator();
        for (int i = 0; i < dest.length; ++i) {
            dest[i] = l.hasNext() && r.hasNext() ? func.applyAsDouble(lhsExtractor.applyAsDouble(l.next()), r.next()) : defaultValue;
        }
        return dest;
    }

    public static <T> T[] fill(T[] dest, Iterable<T> source, T defaultValue) {
        final Iterator<T> it = source.iterator();
        for (int i = 0; i < dest.length; ++i) {
            dest[i] = it.hasNext() ? it.next() : defaultValue;
        }
        return dest;
    }

    public static <T> T[] fill(T[] dest, Iterable<T> source, UnaryOperator<T> func, T defaultValue) {
        final Iterator<T> it = source.iterator();
        for (int i = 0; i < dest.length; ++i) {
            dest[i] = it.hasNext() ? func.apply(it.next()) : defaultValue;
        }
        return dest;
    }

    public static <S, T> S[] fill(S[] dest, T[] source, Function<T, S> func, S defaultValue) {
        for (int i = 0; i < dest.length; ++i) {
            dest[i] = i < source.length ? func.apply(source[i]) : defaultValue;
        }
        return dest;
    }

    public static <S, T> T[] fill(T[] dest, Iterable<S> source, Function<S, T> func, T defaultValue) {
        final Iterator<S> it = source.iterator();
        for (int i = 0; i < dest.length; ++i) {
            dest[i] = it.hasNext() ? func.apply(it.next()) : defaultValue;
        }
        return dest;
    }

    public static double[] toArray(Iterable<Double> data, int size) {
        final double[] out = new double[size];
        final Iterator<Double> it = data.iterator();
        for (int i = 0; i < size && it.hasNext(); ++i) {
            out[i] = it.next();
        }
        return out;
    }


}
