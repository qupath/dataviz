package net.mahdilamb.charts.dataframe.utils;

public final class Comparables {
    private Comparables() {

    }

    /**
     * Generic min method. Not null-safe
     *
     * @param a   the left hand operand
     * @param b   the right hand operand
     * @param <T> the type of the data
     * @return a or b, whichever is smaller
     */
    public static <T extends Comparable<T>> T min(T a, T b) {
        return a.compareTo(b) <= 0 ? a : b;
    }

    /**
     * Generic max method. Not null-safe
     *
     * @param a   the left hand operand
     * @param b   the right hand operand
     * @param <T> the type of the data
     * @return a or b, whichever is bigger
     */
    public static <T extends Comparable<T>> T max(T a, T b) {
        return a.compareTo(b) >= 0 ? a : b;
    }
}
