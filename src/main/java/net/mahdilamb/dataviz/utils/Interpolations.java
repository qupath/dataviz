package net.mahdilamb.dataviz.utils;

import java.util.function.DoubleUnaryOperator;

import static java.lang.Math.*;

/**
 * Interpolation functions. The unary operators are adapted from <a href="https://easings.net/">Easing cheatsheet</a>
 */
public final class Interpolations {
    private Interpolations() {
    }

    private static final double c1 = 1.70158;
    private static final double c2 = c1 * 1.525;
    private static final double c3 = c1 + 1;
    private static final double c4 = (2 * Math.PI) / 3;
    private static final double c5 = (2 * Math.PI) / 4.5;

    /**
     * Linearly interpolate between two values
     *
     * @param a value a
     * @param b value b
     * @param t parametric value
     * @return the interpolated value
     */
    public static double lerp(double a, double b, double t) {
        return a + t * (b - a);
    }

    /**
     * Interpolate after transforming the parametric value
     *
     * @param a    value a
     * @param b    value b
     * @param t    parametric value
     * @param ease easing function
     * @return the interpolated value
     */
    public static double lerp(double a, double b, double t, DoubleUnaryOperator ease) {
        return a + ease.applyAsDouble(t) * (b - a);
    }

    /**
     * Exponential ease out function
     *
     * @param t the parametric value
     * @return the value transformed to exponential ease out
     */
    public static double easeOutExpo(double t) {
        return t == 1 ? 1 : 1 - pow(2, -10 * t);
    }

    /**
     * @param x the value to interpolate
     * @return the linearly interpolated value
     */
    public static double linear(double x) {
        return x;
    }

    /**
     * @param x the value to interpolate
     * @return the easeInQuad interpolated value
     */
    public static double easeInQuad(double x) {
        return x * x;
    }

    /**
     * @param x the value to interpolate
     * @return the easeOutQuad interpolated value
     */
    public static double easeOutQuad(double x) {
        return 1 - (1 - x) * (1 - x);
    }

    /**
     * @param x the value to interpolate
     * @return the easeInOutQuad interpolated value
     */
    public static double easeInOutQuad(double x) {
        return x < 0.5 ? 2 * x * x : 1 - pow(-2 * x + 2, 2) / 2;
    }

    /**
     * @param x the value to interpolate
     * @return the easeInCubic interpolated value
     */
    public static double easeInCubic(double x) {
        return x * x * x;
    }

    /**
     * @param x the value to interpolate
     * @return the easeOutCubic interpolated value
     */
    public static double easeOutCubic(double x) {
        return 1 - pow(1 - x, 3);
    }

    /**
     * @param x the value to interpolate
     * @return the easeInOutCubic interpolated value
     */
    public static double easeInOutCubic(double x) {
        return x < 0.5 ? 4 * x * x * x : 1 - pow(-2 * x + 2, 3) / 2;
    }

    /**
     * @param x the value to interpolate
     * @return the easeInQuart interpolated value
     */
    public static double easeInQuart(double x) {
        return x * x * x * x;
    }

    /**
     * @param x the value to interpolate
     * @return the easeOutQuart interpolated value
     */
    public static double easeOutQuart(double x) {
        return 1 - pow(1 - x, 4);
    }

    /**
     * @param x the value to interpolate
     * @return the easeInOutQuart interpolated value
     */
    public static double easeInOutQuart(double x) {
        return x < 0.5 ? 8 * x * x * x * x : 1 - pow(-2 * x + 2, 4) / 2;
    }

    /**
     * @param x the value to interpolate
     * @return the easeInQuint interpolated value
     */
    public static double easeInQuint(double x) {
        return x * x * x * x * x;
    }

    /**
     * @param x the value to interpolate
     * @return the easeOutQuint interpolated value
     */
    public static double easeOutQuint(double x) {
        return 1 - pow(1 - x, 5);
    }

    /**
     * @param x the value to interpolate
     * @return the easeInOutQuint interpolated value
     */
    public static double easeInOutQuint(double x) {
        return x < 0.5 ? 16 * x * x * x * x * x : 1 - pow(-2 * x + 2, 5) / 2;
    }

    /**
     * @param x the value to interpolate
     * @return the easeInSine interpolated value
     */
    public static double easeInSine(double x) {
        return 1 - cos((x * PI) / 2);
    }

    /**
     * @param x the value to interpolate
     * @return the easeOutSine interpolated value
     */
    public static double easeOutSine(double x) {
        return sin((x * PI) / 2);
    }

    /**
     * @param x the value to interpolate
     * @return the easeInOutSine interpolated value
     */
    public static double easeInOutSine(double x) {
        return -(cos(PI * x) - 1) / 2;
    }

    /**
     * @param x the value to interpolate
     * @return the easeInExpo interpolated value
     */
    public static double easeInExpo(double x) {
        return x == 0 ? 0 : pow(2, 10 * x - 10);
    }

    /**
     * @param x the value to interpolate
     * @return the easeInOutExpo interpolated value
     */
    public static double easeInOutExpo(double x) {
        return x == 0
                ? 0
                : x == 1
                ? 1
                : x < 0.5
                ? pow(2, 20 * x - 10) / 2
                : (2 - pow(2, -20 * x + 10)) / 2;
    }

    /**
     * @param x the value to interpolate
     * @return the easeInCirc interpolated value
     */
    public static double easeInCirc(double x) {
        return 1 - sqrt(1 - pow(x, 2));
    }

    /**
     * @param x the value to interpolate
     * @return the easeOutCirc interpolated value
     */
    public static double easeOutCirc(double x) {
        return sqrt(1 - pow(x - 1, 2));
    }

    /**
     * @param x the value to interpolate
     * @return the easeInOutCirc interpolated value
     */
    public static double easeInOutCirc(double x) {
        return x < 0.5
                ? (1 - sqrt(1 - pow(2 * x, 2))) / 2
                : (sqrt(1 - pow(-2 * x + 2, 2)) + 1) / 2;
    }

    /**
     * @param x the value to interpolate
     * @return the easeInBack interpolated value
     */
    public static double easeInBack(double x) {
        return c3 * x * x * x - c1 * x * x;
    }

    /**
     * @param x the value to interpolate
     * @return the easeOutBack interpolated value
     */
    public static double easeOutBack(double x) {
        return 1 + c3 * pow(x - 1, 3) + c1 * pow(x - 1, 2);
    }

    /**
     * @param x the value to interpolate
     * @return the easeInOutBack interpolated value
     */
    public static double easeInOutBack(double x) {
        return x < 0.5
                ? (pow(2 * x, 2) * ((c2 + 1) * 2 * x - c2)) / 2
                : (pow(2 * x - 2, 2) * ((c2 + 1) * (x * 2 - 2) + c2) + 2) / 2;
    }

    /**
     * @param x the value to interpolate
     * @return the easeInElastic interpolated value
     */
    public static double easeInElastic(double x) {
        return x == 0
                ? 0
                : x == 1
                ? 1
                : -pow(2, 10 * x - 10) * sin((x * 10 - 10.75) * c4);
    }

    /**
     * @param x the value to interpolate
     * @return the easeOutElastic interpolated value
     */
    public static double easeOutElastic(double x) {
        return x == 0
                ? 0
                : x == 1
                ? 1
                : pow(2, -10 * x) * sin((x * 10 - 0.75) * c4) + 1;
    }

    /**
     * @param x the value to interpolate
     * @return the easeInOutElastic interpolated value
     */
    public static double easeInOutElastic(double x) {
        return x == 0
                ? 0
                : x == 1
                ? 1
                : x < 0.5
                ? -(pow(2, 20 * x - 10) * sin((20 * x - 11.125) * c5)) / 2
                : (pow(2, -20 * x + 10) * sin((20 * x - 11.125) * c5)) / 2 + 1;
    }

    private static double bounceOut(double x) {
        double n1 = 7.5625;
        double d1 = 2.75;

        if (x < 1 / d1) {
            return n1 * x * x;
        } else if (x < 2 / d1) {
            return n1 * (x -= 1.5 / d1) * x + 0.75;
        } else if (x < 2.5 / d1) {
            return n1 * (x -= 2.25 / d1) * x + 0.9375;
        } else {
            return n1 * (x -= 2.625 / d1) * x + 0.984375;
        }
    }

    /**
     * @param x the value to interpolate
     * @return the easeOutBounce interpolated value
     */
    public static double easeOutBounce(double x) {
        return 1 - bounceOut(1 - x);
    }

    /**
     * @param x the value to interpolate
     * @return the easeInOutBounce interpolated value
     */
    public static double easeInOutBounce(double x) {
        return x < 0.5
                ? (1 - bounceOut(1 - 2 * x)) / 2
                : (1 + bounceOut(2 * x - 1)) / 2;
    }

}
