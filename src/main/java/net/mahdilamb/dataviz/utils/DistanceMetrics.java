package net.mahdilamb.dataviz.utils;

import java.util.List;

/**
 * 2D distance spatial metrics
 */
public final class DistanceMetrics {
    public static final List<String> AVAILABLE_METRICS = List.of("euclidean", "manhattan", "chebyshev", "cosine", "canberra", "braycurtis");

    private DistanceMetrics() {
    }

    /**
     * @param name the name of distance metric
     * @return a distance metric by its name
     */
    public static Functions.DoubleQuaternaryFunction getDistanceMetric2D(final String name) {
        switch (name.toLowerCase()) {
            case "euclid":
            case "euclidean":
                return DistanceMetrics::euclidean;
            case "taxicab":
            case "cityblock":
            case "manhattan":
                return DistanceMetrics::manhattan;
            case "chebyshev":
                return DistanceMetrics::chebyshev;
            case "cosine":
            case "cos":
                return DistanceMetrics::cosine;
            case "canberra":
                return DistanceMetrics::canberra;
            case "braycurtis":
            case "bray-curtis":
                return DistanceMetrics::braycurtis;
            default:
                throw new UnsupportedOperationException("Could not find distance metric with the name of " + name);
        }
    }

    /**
     * Calculate the 2D distance between two points
     *
     * @param ax x of point a
     * @param ay y of point a
     * @param bx x of point b
     * @param by y of point b
     * @return the Euclidean distance between a and b
     */
    public static double euclidean(double ax, double ay, double bx, double by) {
        double dx = ax - bx;
        double dy = ay - by;
        return Math.sqrt((dx * dx) + (dy * dy));
    }

    /**
     * @param ax x of point a
     * @param ay y of point a
     * @param bx x of point b
     * @param by y of point b
     * @return the Manhattan distance between two points
     */
    public static double manhattan(double ax, double ay, double bx, double by) {
        return Math.abs(ax - bx) + Math.abs(ay - by);
    }

    /**
     * @param ax x of point a
     * @param ay y of point a
     * @param bx x of point b
     * @param by y of point b
     * @return the Chebyshev distance between two points
     */
    public static double chebyshev(double ax, double ay, double bx, double by) {
        return Math.max(Math.abs(ax - bx), Math.abs(ay - by));
    }

    /**
     * @param ax x of point a
     * @param ay y of point a
     * @param bx x of point b
     * @param by y of point b
     * @return the Bray-Curtis distance between two points
     */
    public static double braycurtis(double ax, double ay, double bx, double by) {
        double dx = Math.abs(ax - bx);
        double dy = Math.abs(ay - by);
        double sx = Math.abs(ax + bx);
        double sy = Math.abs(ay + by);
        return (dx + dy) / (sx + sy);
    }

    /**
     * @param ax x of point a
     * @param ay y of point a
     * @param bx x of point b
     * @param by y of point b
     * @return the Canberra distance between two points
     */
    public static double canberra(double ax, double ay, double bx, double by) {
        double _x = (Math.abs(ax - bx)) / (Math.abs(ax) + Math.abs(bx));
        double _y = (Math.abs(ay - by)) / (Math.abs(ay) + Math.abs(by));
        return _x + _y;
    }

    /**
     * @param ax x of point a
     * @param ay y of point a
     * @param bx x of point b
     * @param by y of point b
     * @return the Cosine distance between two points
     */
    public static double cosine(double ax, double ay, double bx, double by) {
        return 1 - (dot(ax, ay, bx, by) / (Math.sqrt(dot(ax, ay, ax, ay)) * Math.sqrt(dot(bx, by, bx, by))));
    }

    private static double dot(double ax, double ay, double bx, double by) {
        return ax * bx + ay * by;
    }

}
