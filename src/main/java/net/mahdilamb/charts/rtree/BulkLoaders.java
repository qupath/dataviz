package net.mahdilamb.charts.rtree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.IntBinaryOperator;

/**
 * The actual implementation of bulk loaders
 */
final class BulkLoaders {
    private BulkLoaders() {

    }

    /**
     * Overlap minimizing top-down bulk loader. Adapted from <a href="https://github.com/mourner/rbush">rbush</a>.
     *
     * @param minEntries the min entries of the tree
     * @param maxEntries the max entries of the tree
     * @param items      the items to add
     * @param <T>        the type of the data in the nodes
     * @return the root node of this subtree
     */
    static <T> RectangularNode<T> OMT(int minEntries, int maxEntries, Node2D<T>[] items) {
        return OMT(minEntries, maxEntries, items, 0, items.length - 1, -1);
    }

    private static <T> RectangularNode<T> OMT(int minEntries, int maxEntries, Node2D<T>[] items, int left, int right, int height) {

        int N = right - left + 1;
        int M = maxEntries;
        RectangularNode<T> node;

        if (N <= M) {
            // reached leaf level; return leaf
            node = new RectangularNode<T>(Arrays.copyOfRange(items, left, right + 1));
            node.height = 1;
            node.recalculateBBox();
            return node;
        }
        if (height == -1) {
            // target height of the bulk-loaded tree
            height = (int) Math.ceil(Math.log(N) / Math.log(M));

            // target number of root entries to maximize storage utilization
            M = (int) Math.ceil(N / Math.pow(M, height - 1));

        }

        node = new RectangularNode<>(new ArrayList<>(minEntries));
        node.height = height;

        // split the items into M mostly square tiles

        int N2 = (int) Math.ceil((double) N / M);
        int N1 = (int) (N2 * Math.ceil(Math.sqrt(M)));

        QuickSelect.multiSelect(items, left, right, N1, RectangularNode::compareMinX);

        for (int i = left; i <= right; i += N1) {

            int right2 = Math.min(i + N1 - 1, right);
            QuickSelect.multiSelect(items, i, right2, N2, RectangularNode::compareMinY);
            for (int j = i; j <= right2; j += N2) {
                int right3 = Math.min(j + N2 - 1, right2);
                // pack each entry recursively
                node.children.add(OMT(minEntries, maxEntries, items, j, right3, height - 1));
            }
        }

        node.recalculateBBox();
        return node;

    }

    private static <T> RectangularNode<T> SpaceFillingCurveSorted(int maxEntries, Node2D<T>[] items, IntBinaryOperator curveFunction) {

        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;

        for (Node2D<T> item : items) {
            minX = Math.min(minX, item.getMinX());
            minY = Math.min(minY, item.getMinY());
            maxX = Math.max(maxX, item.getMaxX());
            maxY = Math.max(maxY, item.getMaxY());
        }
        int[] indexValues = new int[items.length];
        int MAX_16_BIT = (1 << 16) - 1;

        double width = maxX - minX;
        double height = maxY - minY;
        for (int i = 0; i < items.length; ++i) {
            Node2D<T> b = items[i];
            int x = (int) (MAX_16_BIT * ((b.getMinX() + b.getMaxX()) * .5 - minX) / width);
            int y = (int) (MAX_16_BIT * ((b.getMinY() + b.getMaxY()) * .5 - minY) / height);
            indexValues[i] = curveFunction.applyAsInt(x, y);
        }
        sort(indexValues, items, 0, items.length - 1, maxEntries);
        return mergeUpwards(items, maxEntries, 2);
    }

    /**
     * Hilbert sorted bulk loader. The nodes are sorted by their Hilbert value relative to the center of the
     * items.
     *
     * @param maxEntries the max entries of the tree     * @param items the items to add
     * @param <T>        the type of the data in the nodes
     * @return the root node of this subtree
     */
    static <T> RectangularNode<T> HilbertSorted(int maxEntries, Node2D<T>[] items) {
        return SpaceFillingCurveSorted(maxEntries, items, SpaceFillingCurves::encodeHilbert);
    }

    /**
     * Morton curve sorted bulk loaded.
     *
     * @param maxEntries the max entries of the tree
     * @param items      the items to add
     * @param <T>        the type of the data in the nodes
     * @return the root node of this subtree
     */
    static <T> RectangularNode<T> ZOrderSorted(int maxEntries, Node2D<T>[] items) {
        return SpaceFillingCurveSorted(maxEntries, items, SpaceFillingCurves::encodeMorton);

    }

    /**
     * Nearest X bulk loader. The nodes are sorted by the center of their bounds
     *
     * @param maxEntries the max entries of the tree
     * @param items      the items to add
     * @param <T>        the type of the data in the nodes
     * @return the root node of this subtree
     */
    static <T> RectangularNode<T> NearestXSorted(int maxEntries, Node2D<T>[] items) {
        QuickSelect.multiSelect(items, 0, items.length - 1, maxEntries, Comparator.comparingDouble(Node2D::getMidX));
        return mergeUpwards(items, maxEntries, 2);
    }

    /**
     * Sort-tile recursive bulk loader. The nodes are first sorted by their center X value
     * and then sorted by their y axis
     *
     * @param items the items to add
     * @param <T>   the type of the data in the nodes
     * @return the root node of this subtree
     */
    static <T> RectangularNode<T> STR(int maxEntries, Node2D<T>[] items) {

        final int N = items.length - 1;

        final int N2 = (int) Math.ceil((double) N / maxEntries);
        final int m = (int) Math.ceil(Math.sqrt(maxEntries));
        final int N1 = (N2 * m);

        QuickSelect.multiSelect(items, 0, N, N1, Comparator.comparingDouble(Node2D::getMidX));

        for (int i = 0; i <= N; i += N1) {

            final int right2 = Math.min(i + N1 - 1, N);
            QuickSelect.multiSelect(items, i, right2, m, Comparator.comparingDouble(Node2D::getMidY));
        }
        return mergeUpwards(items, maxEntries, 1);
    }

    /**
     * Utility method to merge nodes into a single root (bottom-up)
     *
     * @param items      the items to merge
     * @param maxEntries the maximum children in each leaf
     * @param height     the current height of the tree
     * @param <T>        the type of the data in the nodes
     * @return a new root node for the generates subtree
     */
    static <T> RectangularNode<T> mergeUpwards(Node2D<T>[] items, int maxEntries, int height) {
        final int N = items.length;
        if (N <= maxEntries) {
            final RectangularNode<T> node = new RectangularNode<T>(items);
            node.height = height;
            node.recalculateBBox();
            return node;
        }

        @SuppressWarnings("unchecked") final Node2D<T>[] merged = new Node2D[(int) Math.ceil((double) items.length / maxEntries)];
        for (int i = 0, j = 0; i < items.length; i += maxEntries) {
            final List<Node2D<T>> children = new ArrayList<>(maxEntries);
            for (int k = 0; k < maxEntries; ++k) {
                int l = i + k;
                if (l >= items.length) {
                    break;
                }

                children.add(items[l]);

            }

            final Node2D<T> n = new RectangularNode<>(children);
            n.height = height;
            n.recalculateBBox();
            merged[j++] = n;
        }
        return mergeUpwards(merged, maxEntries, height + 1);
    }


    /**
     * Swap two arrays
     *
     * @param indices the int array
     * @param values  the value arrays
     * @param i       the i index to swap
     * @param j       the j index to swap
     * @param <T>     the type of the values in the value array
     */
    private static <T> void swapWith(int[] indices, T[] values, int i, int j) {
        int tmp = indices[i];
        indices[i] = indices[j];
        indices[j] = tmp;
        T v = values[i];
        values[i] = values[j];
        values[j] = v;
    }

    /**
     * Modified quick sort
     *
     * @param sortIndices the indices to sort by
     * @param values      the values to sort
     * @param left        the left index to sort by
     * @param right       the right index to sort by
     * @param nodeSize    the size of the nodes
     * @param <T>         the type of the data in the values
     */
    private static <T> void sort(int[] sortIndices, T[] values, int left, int right, int nodeSize) {
        if (Math.floor((double) left / nodeSize) >= Math.floor((double) right / nodeSize)) {
            return;
        }
        final int pivot = sortIndices[(left + right) >> 1];
        int i = left - 1,
                j = right + 1;
        while (true) {
            do {
                ++i;
            } while (sortIndices[i] < pivot);
            do {
                --j;
            } while (sortIndices[j] > pivot);
            if (i >= j) {
                break;
            }
            swapWith(sortIndices, values, i, j);
        }
        sort(sortIndices, values, left, j, nodeSize);
        sort(sortIndices, values, j + 1, right, nodeSize);
    }

}
