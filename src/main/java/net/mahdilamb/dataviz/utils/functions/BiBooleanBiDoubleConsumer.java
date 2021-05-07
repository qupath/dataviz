package net.mahdilamb.dataviz.utils.functions;

/**
 * Functional interface for a method that accepts two booleans and two doubles
 */
@FunctionalInterface
public interface BiBooleanBiDoubleConsumer {
    /**
     * An empty consumer
     */
    BiBooleanBiDoubleConsumer IDENTITY = (a, b, c, d) -> {

    };
    /**
     * Accept the given commands
     *
     * @param a first boolean
     * @param b second boolean
     * @param c first double
     * @param d second double
     */
    void accept(boolean a, boolean b, double c, double d);
}
