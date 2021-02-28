package net.mahdilamb.charts.rtree;

/**
 * A Rectangular node
 *
 * @param <T> the type of data in the leaf node
 */
public class PointNode<T> extends Node2D<T> {

    final double x;
    final double y;

    /**
     * Create a rectangular data node
     *
     * @param x the x component of the data
     * @param y the y component of the data
     */
    public PointNode(double x, double y) {
        this(x, y, null);
    }

    /**
     * Create a rectangular data node
     *
     * @param x    the x component of the data
     * @param y    the y component of the data
     * @param data the data
     */
    public PointNode(double x, double y, T data) {
        super(data, true);
        this.x = x;
        this.y = y;
    }

    @Override
    public double getMinX() {
        return x;
    }

    @Override
    public double getMinY() {
        return y;
    }

    @Override
    public final double getMaxX() {
        return getMinX();
    }

    @Override
    public final double getMaxY() {
        return getMinY();
    }

    @Override
    public final double getMidX() {
        return getMinX();
    }

    @Override
    public final double getMidY() {
        return getMinY();
    }
}
