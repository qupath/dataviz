package net.mahdilamb.dataviz.utils.functions;

/**
 * Functional interface for a method that accepts two booleans, an int and a char
 */
@FunctionalInterface
public interface ObjBiBooleanIntCharConsumer<T> {
    /**
     * An empty consumer
     */
    ObjBiBooleanIntCharConsumer<?> IDENTITY = (e, a, b, c, d) -> {

    };

    @SuppressWarnings("unchecked")
    static <T> ObjBiBooleanIntCharConsumer<T> getIdentity() {
        return (ObjBiBooleanIntCharConsumer<T>) IDENTITY;
    }

    /**
     * Accept the given commands
     *  @param a first boolean
     * @param b second boolean
     * @param c the int
     * @param d the char
     */
    void accept(T e, boolean a, boolean b, int c, char d);
}
