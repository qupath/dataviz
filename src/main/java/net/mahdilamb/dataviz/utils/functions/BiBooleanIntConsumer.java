package net.mahdilamb.dataviz.utils.functions;

/**
 * Functional interface for a method that accepts two booleans and an int
 */
@FunctionalInterface
public interface BiBooleanIntConsumer {
    /**
     * An empty consumer
     */
    BiBooleanIntConsumer IDENTITY = (a, b, c) -> {

    };
    /**
     * Accept the given commands
     *
     * @param a first boolean
     * @param b second boolean
     * @param c the int
     */
    void accept(boolean a, boolean b, int c);
}
