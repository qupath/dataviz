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
         * @param ax the x component of a
         * @param ay the y component of a
         * @param bx the x component of b
         * @param by the y component of b
         * @return the result of applying the four parameters
         */
        double apply(double ax, double ay, double bx, double by);

        /**
         * Apply a function to two two-component arrays
         *
         * @param a the left xy array
         * @param b the right xy array
         * @return the result of applying the function to the arrays
         */
        default double apply(double[] a, double[] b) {
            return apply(a[0], a[1], b[0], b[1]);
        }
    }
}
