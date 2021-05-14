package net.mahdilamb.dataviz.utils;

import java.util.function.DoubleConsumer;

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
        for (int i = -1; i >= -5; --i) {
            double w = Double.longBitsToDouble(signBit | exponentBits | (significandBits + i));
            if (!Double.isFinite(w)) {
                continue;
            }
            if ((startLength - Ryu.lengthOfDouble(w)) > minDifference) {
                return w;
            }
        }
        for (int i = 1; i <= 5; ++i) {
            double w = Double.longBitsToDouble(signBit | exponentBits | (significandBits + i));
            if (!Double.isFinite(w)) {
                continue;
            }
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

    /**
     * Perform an integer ceiling division. The operation is modified from {@link java.lang.Math#floorDiv(int, int)}
     *
     * @param a the numerator
     * @param b the denominator
     * @return the ceiling division of a over b
     */
    public static int ceilDiv(int a, int b) {
        int r = a / b;
        if ((a ^ b) > 0 && (r * b != a)) {
            ++r;
        }
        return r;
    }

    /**
     * Perform an integer ceiling division. The operation is modified from {@link java.lang.Math#floorDiv(long, long)}
     *
     * @param a the numerator
     * @param b the denominator
     * @return the ceiling division of a over b
     */
    public static long ceilDiv(long a, long b) {
        long r = a / b;
        if ((a ^ b) > 0 && (r * b != a)) {
            ++r;
        }
        return r;
    }

    public static double requireFinitePositive(double x) {
        if (!Double.isFinite(x)) {
            throw new IllegalArgumentException("Number is not finite");
        }
        if (x <= 0) {
            throw new IllegalArgumentException("Number must be greater than 0");
        }
        return x;
    }

    public static void iterateKleinSum(final double start, final double end, double spacing, DoubleConsumer func) {
        double s = start, c, cc, cs = 0, ccs = 0, t;
        func.accept(s);
        while (s <= end) {
            t = s + spacing;
            if (Math.abs(s) >= Math.abs(spacing)) {
                c = (s - t) + spacing;
            } else {
                c = (spacing - t) + s;
            }
            s = t;
            t = cs + c;
            if (Math.abs(cs) >= Math.abs(c)) {
                cc = (cs - t) + c;
            } else {
                cc = (c - t) + cs;
            }
            cs = t;
            ccs = ccs + cc;
            func.accept(s + cs + ccs);
        }

    }

    public static void main(String[] args) {
        iterateKleinSum(0, 2, .2, System.out::println);
    }

}
