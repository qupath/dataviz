package net.mahdilamb.dataviz.utils;

/**
 * Utility methods for working with numbers
 */
public final class Numbers {
    private Numbers() {

    }

    /**
     * Constant used to make ellipses from beziers
     */
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
     * Find the square of the distance distance between two 2D points
     *
     * @param ax x of point a
     * @param ay y of point a
     * @param bx x of point b
     * @param by y of point b
     * @return square of the distance between a and b
     */
    public static double distanceSq(final double ax, final double ay, final double bx, final double by) {
        final double deltaX = ax - bx;
        final double deltaY = ay - by;
        return deltaX * deltaX + deltaY * deltaY;
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
        return Math.sqrt(distanceSq(ax, ay, bx, by));
    }

    /**
     * Round a number to the nearest decimal representation, using the <a href="https://github.com/ulfjack/ryu">Ryu</a> algorithm to calculate string length
     *
     * @param v             the value to round
     * @param minDifference the minimum difference in the length of strings from the current to the query
     * @return the nearest decimal representation (or the original if incrementing bits doesn't lead to a better representation)
     * @implNote This finds the length of a double and compares it to adding a single bit to the significand (+/- 5) and if the difference in length compared
     * to the original value is greater than the minDifference, then it returns the shortened double, otherwise returns the source value
     */
    public static double approximateDouble(double v, int minDifference) {
        if (!Double.isFinite(v) || v == 0) {
            return v;
        }
        int startLength = Ryu.lengthOfDouble(v);
        long bits = Double.doubleToRawLongBits(v);
        long signBit = bits & 0x8000000000000000L;
        long exponentBits = (bits & 0x7FF0000000000000L);
        long significandBits = bits & 0x000FFFFFFFFFFFFFL;
        for (int i = -1; i > -5; --i) {
            double w = Double.longBitsToDouble(signBit | exponentBits | (significandBits + i));
            if ((startLength - Ryu.lengthOfDouble(w)) > minDifference) {
                return w;
            }
        }
        for (int i = 1; i < 5; ++i) {
            double w = Double.longBitsToDouble(signBit | exponentBits | (significandBits + i));
            if ((startLength - Ryu.lengthOfDouble(w)) > minDifference) {
                return w;
            }
        }
        return v;
    }

    /**
     * Get an approximate representation of a value
     *
     * @param v the value
     * @return the a close approximate of the value
     */
    public static double approximateDouble(double v) {
        return approximateDouble(v, 5);
    }


}
