package net.mahdilamb.dataviz.utils.functions;

/**
 * Functional interface for a method that accepts two booleans, an int and a char
 */
@FunctionalInterface
public interface BiBooleanIntCharConsumer {
    /**
     * An empty consumer
     */
    BiBooleanIntCharConsumer IDENTITY = (a, b, c, d) -> {

    };

    /**
     * Accept the given commands
     *
     * @param a first boolean
     * @param b second boolean
     * @param c the int
     * @param d the char
     */
    void accept(boolean a, boolean b, int c, char d);
}
