package net.mahdilamb.dataviz.utils;

/**
 * Utility methods for working with Java functional interfaces
 */
public final class Functions {
    private Functions() {

    }

    /**
     * An empty runnable
     */
    public static final Runnable EMPTY_RUNNABLE = new Runnable() {
        @Override
        public void run() {

        }
    };

    /**
     * Functional interface for a method that accepts 4 doubles and returns a double
     */
    @FunctionalInterface
    public interface DoubleQuaternaryFunction {
        /**
         * @param a the first param
         * @param b second param
         * @param c third param
         * @param d last param
         * @return the result of applying the four parameters
         */
        double apply(double a, double b, double c, double d);
    }
}
