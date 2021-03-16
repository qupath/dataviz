package net.mahdilamb.dataviz.utils;

import java.util.List;
import java.util.function.DoubleUnaryOperator;

public final class Kernels {
    private Kernels() {
    }

    public static final List<String> AVAILABLE_KERNELS = List.of("gaussian", "tophat", "triangular", "exponential",
            "trigonometric", "epanechnikov", "quartic", "triweight", "tricube", "cosine", "logistic", "sigmoid", "silverman");

    private static final double GAUSS_DENOM = Math.sqrt(2 * Math.PI);

    /**
     * Gaussian kernel
     *
     * @param x the input value
     * @return the value with a gaussian kernel applied
     */
    public static double gaussian(double x) {
        return Math.exp(-(x * x) * .5) / GAUSS_DENOM;
    }

    /**
     * Box/tophat kernel
     *
     * @param x the value at x
     * @return the value with the box kernel applied
     */
    public static double box(double x) {
        return Math.abs(x) <= 1 ? .5 : 0;
    }

    /**
     * Apply the triangular kernel to a value
     *
     * @param x the x value
     * @return the triangular kernel applied to x
     */
    public static double triangular(double x) {
        return Math.abs(x) <= 1 ? (1 - Math.abs(x)) : 0;
    }

    /**
     * Apply the exponential kernel to a value
     *
     * @param x the x value
     * @return the exponential kernel applied to x
     */
    public static double exponential(double x) {
        return 0.5 * Math.exp(-Math.abs(x));
    }

    /**
     * Apply the trigonometric kernel to a value
     *
     * @param x the x value
     * @return the trigonometric kernel applied to x
     */
    public static double trigonometric(double x) {
        return Math.abs(x) <= .5 ? (1 + Math.cos(2 * Math.PI * x)) : 0;
    }

    /**
     * Apply the Epanechnikov kernel to a value
     *
     * @param x the x value
     * @return the Epanechnikov kernel applied to x
     */
    public static double epanechnikov(double x) {
        return Math.abs(x) <= 1 ? (.75 * (1 - (x * x))) : 0;
    }

    /**
     * Apply the quartic kernel to a normalized value
     *
     * @param x the value
     * @return the value with the quartic kernel applied
     */
    public static double quartic(double x) {
        if (Math.abs(x) > 1) {
            return 0;
        }
        double w = 1 - x * x;
        return (15. / 16) * w * w;
    }

    /**
     * Apply the triweight kernel to a normalized value
     *
     * @param x the value
     * @return the value with the triweight kernel applied
     */
    public static double triweight(double x) {
        if (Math.abs(x) > 1) {
            return 0;
        }
        double w = 1 - x * x;
        return (35. / 32) * w * w * w;
    }

    /**
     * Apply the tricube kernel to a normalized value
     *
     * @param x the value
     * @return the value with the tricube kernel applied
     */
    public static double tricube(double x) {
        x = Math.abs(x);
        if (x > 1) {
            return 0;
        }
        double w = 1 - x * x * x;
        return (70. / 81) * w * w * w;
    }

    private static final double HALF_PI = Math.PI * .5;
    private static final double QUART_PI = Math.PI * .25;

    /**
     * Apply the cosine kernel to a normalized value
     *
     * @param x the value
     * @return the value with the cosine kernel applied
     */
    public static double cosine(double x) {
        return Math.abs(x) <= 1 ? (QUART_PI * Math.cos(HALF_PI * x)) : 0;

    }

    /**
     * Apply the logistic kernel to a normalized value
     *
     * @param x the value
     * @return the value with the logistic kernel applied
     */
    public static double logistic(double x) {
        return 1. / (Math.exp(x) + 2 + Math.exp(-x));
    }

    private static final double TWO_OVER_PI = 2. / Math.PI;

    /**
     * Apply the sigmoid kernel to a normalized value
     *
     * @param x the value
     * @return the value with the sigmoid kernel applied
     */
    public static double sigmoid(double x) {
        return TWO_OVER_PI * (1. / (Math.exp(x) + Math.exp(-x)));
    }

    private static final double ROOT_2 = Math.sqrt(2.);

    /**
     * Apply the Silverman  kernel to a normalized value
     *
     * @param x the value
     * @return the value with the Silverman  kernel applied
     */
    public static double silverman(double x) {
        x = Math.abs(x);
        return .5 * Math.exp(-(x / ROOT_2)) * Math.sin((x / ROOT_2) + QUART_PI);
    }

    /**
     * Get a kernel by its name
     *
     * @param name the name of the kernel
     * @return the kernel
     */
    public static DoubleUnaryOperator getKernel(final String name) {
        switch (name.toLowerCase()) {
            case "gauss":
            case "gaussian":
            case "normal":
                return Kernels::gaussian;
            case "tophat":
            case "box":
            case "boxcar":
                return Kernels::box;
            case "trigonometric":
                return Kernels::trigonometric;
            case "epanechnikov":
            case "parabolic":
                return Kernels::epanechnikov;
            case "triangular":
                return Kernels::triangular;
            case "exp":
            case "exponential":
                return Kernels::exponential;
            case "quartic":
            case "biweight":
                return Kernels::quartic;
            case "triweight":
                return Kernels::triweight;
            case "tricube":
            case "tricubic":
                return Kernels::tricube;
            case "cosine":
            case "cos":
                return Kernels::cosine;
            case "logistic":
                return Kernels::logistic;
            case "sigmoid":
                return Kernels::sigmoid;
            case "silverman":
                return Kernels::silverman;
            default:
                throw new UnsupportedOperationException("Could not find a kernel called " + name);
        }
    }

}
