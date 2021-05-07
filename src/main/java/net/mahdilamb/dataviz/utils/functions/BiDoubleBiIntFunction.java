package net.mahdilamb.dataviz.utils.functions;

/**
 * Functional interface for a method that accepts two doubles and two integers and returns an object
 *
 * @param <T> the type of the object
 */
@FunctionalInterface
public interface BiDoubleBiIntFunction<T> {
    /**
     * @param a first double
     * @param b second double
     * @param c first int
     * @param d second int
     * @return the object
     */
    T apply(double a, double b, int c, int d);
}