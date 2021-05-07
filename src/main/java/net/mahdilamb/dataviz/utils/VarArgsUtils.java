package net.mahdilamb.dataviz.utils;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Utility methods for working with variable arguents
 */
public final class VarArgsUtils {

    private VarArgsUtils() {

    }

    public static <T> void process(T[] args, Consumer<T[]> empty, Consumer<T[]> single, Consumer<T[]> multi) {
        switch (args.length) {
            case 0:
                empty.accept(args);
                break;
            case 1:
                single.accept(args);
                break;
            default:
                multi.accept(args);
                break;

        }
    }

    public static <T> void process(T[] args, Consumer<T[]> empty, Consumer<T[]> multi) {
        process(args, empty, multi, multi);
    }

    public static <T, S> S process(T[] args, Function<T[], S> empty, Function<T[], S> single, Function<T[], S> multi) {
        switch (args.length) {
            case 0:
                return empty.apply(args);
            case 1:
                return single.apply(args);
            default:
                return multi.apply(args);
        }
    }

    public static <T, S> S process(T[] args, Function<T[], S> empty, Function<T[], S> multi) {
        return process(args, empty, multi, multi);
    }

    /**
     * Fill an array with var args. If the length of the array is longer than the
     * input arguments, the arguments repeat
     *
     * @param out   the output array
     * @param first the first element
     * @param rest  the rest of the varargs
     * @return the output array
     */
    public static double[] full(double[] out, double first, double... rest) {
        int l = rest.length + 1;
        for (int b = 0; b < out.length; b += l) {
            for (int i = -1, k = b; i < rest.length && k < out.length; ++i, ++k) {
                out[k] = i == -1 ? first : rest[i];
            }
        }
        return out;
    }

}
