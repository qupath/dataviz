package net.mahdilamb.dataviz.utils.rtree;

import java.util.Collection;
import java.util.List;
import java.util.Stack;
import java.util.function.Predicate;

/**
 * Traversal mode for RTree
 */
public abstract class TreeTraversal {
    /**
     * Use recursion to traverse the RTree
     */
    public static final TreeTraversal RECURSIVE = new RecursiveRTreeTraversal();
    /**
     * Use a stack data structure to traverse an RTree. Useful for very deep trees
     */
    public static final TreeTraversal NON_RECURSIVE = new NonRecursiveRTreeTraversal();

    TreeTraversal() {

    }

    private static final class RecursiveRTreeTraversal extends TreeTraversal {
        @SuppressWarnings("unchecked")
        private <T extends Node2D> List<? extends T> getLeaves0(Node2D node, List<T> result) {
            if (node == null) {
                return result;
            }
            if (node.leaf) {
                result.add((T) node);
                return result;
            }
            assert node.children != null;
            for (final Node2D n : node.children) {
                getLeaves0(n, result);
            }
            return result;
        }

        @Override
        <T extends Node2D> List<? extends T> getLeaves(Node2D root, List<T> result) {
            return getLeaves0(root, result);

        }

        private boolean collides0(double minX, double minY, double maxX, double maxY, Node2D node) {
            if (node == null) {
                return false;
            }
            boolean intersects = false;
            if (node.intersects(minX, minY, maxX, maxY)) {
                if (node.leaf) {
                    intersects = true;
                } else {
                    int i = 0;
                    while (!intersects && i < node.children.size()) {
                        intersects |= collides0(minX, minY, maxX, maxY, node.children.get(i));
                        ++i;
                    }
                }
            }
            return intersects;
        }

        @Override
        boolean collides(Node2D root, double minX, double minY, double maxX, double maxY) {
            if (!root.intersects(minX, minY, maxX, maxY)) {
                return false;
            }
            return collides0(minX, minY, maxX, maxY, root);
        }

        @SuppressWarnings("unchecked")
        private <T extends Node2D> void traverse0(Predicate<Node2D> nodePredicate, Predicate<T> leafFunction, Node2D node) {
            if (node == null) {
                return;
            }
            if (nodePredicate.test(node)) {
                if (node.leaf) {
                    leafFunction.test((T) node);
                } else {
                    for (int i = 0; i < node.children.size(); i++) {
                        traverse0(nodePredicate, leafFunction, node.children.get(i));
                    }
                }
            }
        }

        @Override
        <T extends Node2D> void traverse(Node2D root, Predicate<Node2D> nodePredicate, Predicate<T> leafFunction) {
            traverse0(nodePredicate, leafFunction, root);

        }


        @SuppressWarnings("unchecked")
        private <T extends Node2D, S extends Collection<T>> S search0(double minX, double minY, double maxX, double maxY, Node2D node, S results) {
            if (node == null) {
                return results;
            }
            if (node.intersects(minX, minY, maxX, maxY)) {
                if (node.leaf) {
                    results.add((T) node);
                } else {
                    for (int i = 0; i < node.children.size(); i++) {
                        search0(minX, minY, maxX, maxY, node.children.get(i), results);
                    }
                }
            }
            return results;
        }

        @Override
        <T extends Node2D, S extends Collection<T>> S search(Node2D root, S out, double minX, double minY, double maxX, double maxY) {
            if (!root.intersects(minX, minY, maxX, maxY)) {
                return out;
            }
            return search0(minX, minY, maxX, maxY, root, out);
        }


    }

    private static final class NonRecursiveRTreeTraversal extends TreeTraversal {

        @Override
        @SuppressWarnings("unchecked")
        <T extends Node2D> List<? extends T> getLeaves(Node2D root, List<T> result) {
            final Stack<Node2D> nodesToSearch = new Stack<>();
            while (root != null) {
                if (root.leaf) {
                    result.add((T) root);
                } else {
                    nodesToSearch.addAll(root.children);
                }

                root = nodesToSearch.isEmpty() ? null : nodesToSearch.pop();
            }
            return result;
        }

        @Override
        boolean collides(Node2D root, double minX, double minY, double maxX, double maxY) {
            Node2D node = root;
            if (!node.intersects(minX, minY, maxX, maxY)) {
                return false;
            }

            final Stack<Node2D> nodesToSearch = new Stack<>();
            while (node != null) {
                if (node.intersects(minX, minY, maxX, maxY)) {
                    if (node.leaf) {
                        return true;
                    } else {
                        nodesToSearch.addAll(node.children);
                    }
                }
                node = nodesToSearch.pop();
            }
            return false;
        }

        @Override
        @SuppressWarnings("unchecked")
        <T extends Node2D> void traverse(Node2D root, Predicate<Node2D> nodePredicate, Predicate<T> leafPredicate) {
            final Stack<Node2D> nodesToSearch = new Stack<>();
            nodesToSearch.add(root);
            while (!nodesToSearch.isEmpty()) {
                Node2D node = nodesToSearch.pop();
                if (node.leaf) {
                    if (leafPredicate.test((T) node)) {
                        return;
                    }
                } else {
                    if (nodePredicate.test(node)) {
                        nodesToSearch.addAll(node.children);
                    }
                }
            }
        }

        @Override
        @SuppressWarnings("unchecked")
        <T extends Node2D, S extends Collection<T>> S search(Node2D root, S out, double minX, double minY, double maxX, double maxY) {
            if (!root.intersects(minX, minY, maxX, maxY)) {
                return out;
            }
            final Stack<Node2D> nodesToSearch = new Stack<>();
            nodesToSearch.add(root);
            while (!nodesToSearch.isEmpty()) {
                Node2D node = nodesToSearch.pop();
                if (node.intersects(minX, minY, maxX, maxY)) {
                    if (node.leaf) {
                        out.add((T) node);
                    } else {
                        nodesToSearch.addAll(node.children);
                    }
                }
            }
            return out;
        }


    }

    abstract <T extends Node2D> List<? extends T> getLeaves(Node2D root, List<T> result);

    abstract boolean collides(Node2D root, double minX, double minY, double maxX, double maxY);

    abstract <T extends Node2D> void traverse(Node2D root, Predicate<Node2D> nodePredicate, Predicate<T> leafPredicate);

    abstract <T extends Node2D, S extends Collection<T>> S search(Node2D root, S out, double minX, double minY, double maxX, double maxY);

}
