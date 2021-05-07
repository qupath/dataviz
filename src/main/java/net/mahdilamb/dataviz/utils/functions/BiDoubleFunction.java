package net.mahdilamb.dataviz.utils.functions;

/**
 * Functional interface for a method that accepts two doubles and returns and object
 *
 * @param <T> the type of the object
 */
@FunctionalInterface
public interface BiDoubleFunction<T> {

    /**
     * Apply the given parameters and return an object
     *
     * @param c first double
     * @param d second double
     * @return the object from this method
     */
    T apply(double c, double d);
}
