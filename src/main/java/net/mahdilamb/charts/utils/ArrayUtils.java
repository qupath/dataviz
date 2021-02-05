package net.mahdilamb.charts.utils;

import java.util.Iterator;

public final class ArrayUtils {
    private ArrayUtils() {
    }

    public static double[] fill(double[] dest, Iterable<Double> source, double defaultValue) {
        final Iterator<Double> it = source.iterator();
        for (int i = 0; i < dest.length; ++i) {
            dest[i] = it.hasNext() ? it.next() : defaultValue;
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
}
