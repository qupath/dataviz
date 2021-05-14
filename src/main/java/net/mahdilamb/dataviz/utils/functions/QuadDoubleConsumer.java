package net.mahdilamb.dataviz.utils.functions;

/**
 * Functional interface for a method that accepts four doubles
 */
@FunctionalInterface
public interface QuadDoubleConsumer {
    /**
     * An empty consumer
     */
    QuadDoubleConsumer IDENTITY = (a, b, c, d) -> {

    };

    /**
     * Accept the given commands
     *
     * @param a first double
     * @param b second double
     * @param c third double
     * @param d fourth double
     */
    void accept(double a, double b, double c, double d);
}
