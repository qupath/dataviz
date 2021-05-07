package net.mahdilamb.dataviz.utils.functions;

/**
 * Functional interface for a method that accepts two doubles
 */
@FunctionalInterface
public interface BiDoubleConsumer {
    /**
     * An empty consumer
     */
    BiDoubleConsumer IDENTITY = (c, d) -> {

    };

    /**
     * Accept the given commands
     *
     * @param c first double
     * @param d second double
     */
    void accept(double c, double d);
}
