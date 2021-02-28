package net.mahdilamb.charts.utils;

public final class Numbers {
    private Numbers() {

    }
    public static final double MORTENSEN_CONSTANT = 0.551915024494;

    /**
     * A modulo operator that gives the remainder of positive and negative number correctly
     *
     * @param numerator   the left hand operand
     * @param denominator the right hand operand
     * @return the modulo of numerator by denominator
     */
    public static double mod(double numerator, double denominator) {
        return (numerator < 0 ? ((numerator % denominator) + denominator) : (numerator)) % denominator;
    }

    /**
     * A modulo operator that gives the remainder of positive and negative number correctly
     *
     * @param numerator   the left hand operand
     * @param denominator the right hand operand
     * @return the modulo of numerator by denominator
     */
    public static int mod(int numerator, int denominator) {
        return (numerator < 0 ? ((numerator % denominator) + denominator) : (numerator)) % denominator;
    }

    /**
     * Find the distance between two 2D points
     *
     * @param ax x of point a
     * @param ay y of point a
     * @param bx x of point b
     * @param by y of point b
     * @return distance between a and b
     */
    public static double distance(final double ax, final double ay, final double bx, final double by) {
        final double deltaX = ax - bx;
        final double deltaY = ay - by;
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }
}
