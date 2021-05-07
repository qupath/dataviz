package net.mahdilamb.dataviz.utils.functions;

/**
 * Functional interface for a method that accepts four doubles and returns an object
 *
 * @param <T> the type of the object
 */
@FunctionalInterface
public interface QuadDoubleFunction<T> {

    /**
     * Apply the given parameters and return an object
     *
     * @param a first double
     * @param b second double
     * @param c third double
     * @param d fourth double
     * @return the object from this method
     */
    T apply(double a, double b, double c, double d);
}
