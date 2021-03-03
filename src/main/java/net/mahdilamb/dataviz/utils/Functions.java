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
}
