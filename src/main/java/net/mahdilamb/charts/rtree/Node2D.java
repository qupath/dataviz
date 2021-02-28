package net.mahdilamb.charts.rtree;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A 2D node that can be inserted into a 2D spatial tree
 *
 * @param <T> the type of data in the leaf nodes
 */
public abstract class Node2D<T> {
    int height;
    final boolean leaf;

    final List<Node2D<T>> children;
    private final T data;

    /**
     * Create a new 2D Node
     *
     * @param data the data to add to the node
     */
    protected Node2D(T data) {
        this(data, true);
    }

    Node2D(T data, boolean isLeaf) {
        this.leaf = isLeaf;
        this.data = data;
        this.children = null;
    }

    Node2D(List<Node2D<T>> children, boolean isLeaf) {
        this.leaf = isLeaf;
        this.children = children;
        this.data = null;
    }

    /**
     * Create a node either with children, or an empty leaf
     *
     * @param children the children to initialize with (if {@code null}, assumed to be an orphaned node)
     */
    Node2D(List<Node2D<T>> children) {
        this(children, children == null);
    }

    @SafeVarargs
    Node2D(Node2D<T>... nodes) {
        this.children = new ArrayList<>(nodes.length);
        for (final Node2D<T> n : nodes) {
            children.add(n);
        }
        leaf = false;
        data = null;
    }

    /**
     * @return get the data if this is a leaf node. Or {@code null} if no data is set or this is not a leaf node.
     */
    public final T get() {
        return data;
    }

    /**
     * Set the bounds of this
     *
     * @param minX the minimum x component of the data
     * @param minY the maximum x component of the data
     * @param maxX the minimum y component of the data
     * @param maxY the maximum y component of the data
     * @implNote this must be implemented to allow for unions with other nodes
     */
    void set(double minX, double minY, double maxX, double maxY){

    }

    /**
     * @return the minimum x component
     */
    public abstract double getMinX();

    /**
     * @return the minimum y component
     */
    public abstract double getMinY();

    /**
     * @return the center x component
     * @implNote note final so that point data can avoid having to do unnecessary calculations
     */
    public double getMidX() {
        return (getMaxX() + getMinX()) * .5;
    }

    /**
     * @return the center y component
     * @implNote note final so that point data can avoid having to do unnecessary calculations
     */
    public double getMidY() {
        return (getMaxY() + getMinY()) * .5;
    }

    /**
     * @return the maximum x component
     */
    public abstract double getMaxX();

    /**
     * @return the maximum y component
     */
    public abstract double getMaxY();


    public boolean contains(double minX, double minY, double maxX, double maxY) {
        return RectangularNode.contains(this, minX, minY, maxX, maxY);
    }

    public boolean isContainedIn(double minX, double minY, double maxX, double maxY) {
        return RectangularNode.contains(minX, minY, maxX, maxY, this);
    }

    public boolean intersects(double minX, double minY, double maxX, double maxY) {
        return RectangularNode.intersects(minX, minY, maxX, maxY, this);
    }

    void recalculateBBox() {
        distBBox(this, 0, children.size(), this);
    }

    protected double calculateWidth() {
        return getMaxX() - getMinX();
    }

    protected double calculateHeight() {
        return getMaxY() - getMinY();
    }

    protected double calculateArea() {
        return calculateWidth() * calculateHeight();
    }

    protected double calculateHalfPerimeter() {
        return calculateWidth() + calculateHeight();
    }

    @Override
    public String toString() {
        return "Node{" +
                "minX=" + getMinX() +
                ", minY=" + getMinY() +
                ", maxX=" + getMaxX() +
                ", maxY=" + getMaxY() +
                (leaf ? ", data=" + data : ", numChildren=" + children.size()) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Node2D)) return false;
        @SuppressWarnings("unchecked")
        Node2D<T> node = (Node2D<T>) o;
        return Double.compare(node.getMinX(), getMinX()) == 0 && Double.compare(node.getMinY(), getMinY()) == 0 && Double.compare(node.getMaxX(), getMaxX()) == 0 && Double.compare(node.getMaxY(), getMaxY()) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMinX(),
                getMinY(),
                getMaxX(),
                getMaxY());
    }

    // min bounding rectangle of node children from k to p-1
    static <T> Node2D<T> distBBox(Node2D<T> node, int k, int p, Node2D<T> destNode) {
        if (destNode == null) {
            destNode = new RectangularNode<>((List<Node2D<T>>) null);
        }
        destNode.set(
                Double.POSITIVE_INFINITY,
                Double.POSITIVE_INFINITY,
                Double.NEGATIVE_INFINITY,
                Double.NEGATIVE_INFINITY
        );

        for (int i = k; i < p; i++) {
            Node2D<T> child = node.children.get(i);
            union(destNode, child);
        }

        return destNode;
    }

    /**
     * Set one node to the size of the union with another node
     *
     * @param a the node to set
     * @param b the node to union with
     */
    static void union(Node2D<?> a, Node2D<?> b) {
        a.set(
                Math.min(a.getMinX(), b.getMinX()),
                Math.min(a.getMinY(), b.getMinY()),
                Math.max(a.getMaxX(), b.getMaxX()),
                Math.max(a.getMaxY(), b.getMaxY())
        );
    }


}
