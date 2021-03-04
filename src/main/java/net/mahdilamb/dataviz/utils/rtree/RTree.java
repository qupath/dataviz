package net.mahdilamb.dataviz.utils.rtree;

import java.util.*;
import java.util.function.Predicate;

/**
 * Rtree implementation, heavily based on <a href="https://github.com/mourner/rbush">rbush</a>, with support for generics
 * and functional programming
 *
 * @param <T> the type of the data in leaf nodes
 */
public class RTree<T> extends AbstractRTree<T> {
    private static final class UnmodifiableRTee<T> extends RTree<T> {
        @Override
        public void put(Node2D<T> item) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void put(double minX, double minY, double maxX, double maxY, T data) {
            throw new UnsupportedOperationException();
        }
    }

    private static final RTree<?> EMPTY_TREE = new UnmodifiableRTee<>();

    /**
     * @param <T> the "type" of the tree
     * @return an empty, unmodifiable tree
     */
    @SuppressWarnings("unchecked")
    public static <T> RTree<T> emptyTree() {
        return (RTree<T>) EMPTY_TREE;
    }

    /**
     * Create an Rtree that performs the core functions using recursion rather than {@link java.util.Stack}
     *
     * @param maxEntriesPerNode the maximum number of items per node
     * @param <T>               the type of data in the tree
     * @return an Rtree
     */
    public static <T> AbstractRTree<T> createRecursiveSupporting(int maxEntriesPerNode) {
        return new RecursiveRTree<>(maxEntriesPerNode);
    }

    /**
     * Create an Rtree that performs the core functions using recursion rather than {@link java.util.Stack}
     *
     * @param <T> the type of data in the tree
     * @return an Rtree
     */
    public static <T> AbstractRTree<T> createRecursiveSupporting() {
        return createRecursiveSupporting(DEFAULT_MAX_ENTRIES);
    }

    static final class RecursiveRTree<T> extends AbstractRTree<T> {
        RecursiveRTree(int maxEntriesPerNode) {
            super(maxEntriesPerNode);
        }

        @Override
        public Collection<? extends Node2D<T>> getLeaves() {
            return getLeavesRecursive(root, new LinkedList<>());
        }

        @Override
        public boolean collides(double minX, double minY, double maxX, double maxY) {
            return collidesRecursive(minX, minY, maxX, maxY);
        }

        @Override
        public List<? extends Node2D<T>> search(double minX, double minY, double maxX, double maxY) {
            return searchRecursive(minX, minY, maxX, maxY);
        }

        @Override
        public void traverse(Predicate<Node2D<T>> nodePredicate, Predicate<Node2D<T>> leafFunction) {
            traverseRecursive(nodePredicate, leafFunction);
        }
    }

    /**
     * Create an Rtree
     *
     * @param maxEntriesPerNode the maximum entries per node
     */
    public RTree(int maxEntriesPerNode) {
        super(maxEntriesPerNode);
    }

    /**
     * Create an Rtree with a maximum of 9 items per node
     */
    public RTree() {
        this(DEFAULT_MAX_ENTRIES);
    }

    @Override
    public Collection<? extends Node2D<T>> getLeaves() {
        return super.getLeavesNonRecursive(root, new ArrayList<>(size()));
    }

    @Override
    public boolean collides(double minX, double minY, double maxX, double maxY) {
        return super.collidesNonRecursive(minX, minY, maxX, maxY);
    }

    @Override
    public List<? extends Node2D<T>> search(double minX, double minY, double maxX, double maxY) {
        return super.searchNonRecursive(minX, minY, maxX, maxY);
    }

    /**
     * Return a set of matches in the given range. This can be useful if, for example, the matches need to be in a
     * specific order
     *
     * @param minX the minimum x component of the data
     * @param minY the maximum x component of the data
     * @param maxX the minimum y component of the data
     * @param maxY the maximum y component of the data
     * @return the leaf nodes in the given range
     */
    public Set<? extends Node2D<T>> search(final Set<Node2D<T>> out, double minX, double minY, double maxX, double maxY) {
        return super.searchRecursive(out, minX, minY, maxX, maxY);
    }

    @Override
    public void traverse(Predicate<Node2D<T>> nodePredicate, Predicate<Node2D<T>> leafFunction) {
        super.traverseNonRecursive(nodePredicate, leafFunction);
    }

}
