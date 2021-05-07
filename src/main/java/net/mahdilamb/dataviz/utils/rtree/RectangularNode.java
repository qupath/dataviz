package net.mahdilamb.dataviz.utils.rtree;

/**
 * A rectangular node
 *
 * @param <T> the toe of the data in the node
 */
public class RectangularNode<T> extends Node2DImpl {
    /**
     * The data in the node
     */
    public T data;

    /**
     * Create a rectangular node
     *
     * @param minX the minimum x position of the rectangle
     * @param minY the minimum y position of the rectangle
     * @param maxX the maximum x position of the rectangle
     * @param maxY the maximum y position of the rectangle
     * @param data the data to put in the rectangle
     */
    public RectangularNode(double minX, double minY, double maxX, double maxY, T data) {
        super(minX, minY, maxX, maxY);
        this.data = data;
    }

    /**
     * @return the data in the node
     */
    public T get() {
        return data;
    }
}
