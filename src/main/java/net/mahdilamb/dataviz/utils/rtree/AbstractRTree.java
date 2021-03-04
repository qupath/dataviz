package net.mahdilamb.dataviz.utils.rtree;


import java.util.*;
import java.util.function.Predicate;

import static net.mahdilamb.dataviz.utils.StringUtils.EMPTY_STRING;
import static net.mahdilamb.dataviz.utils.rtree.Node2D.distBBox;
import static net.mahdilamb.dataviz.utils.rtree.Node2D.union;
import static net.mahdilamb.dataviz.utils.rtree.RectangularNode.enlargedArea;
import static net.mahdilamb.dataviz.utils.rtree.RectangularNode.intersectionArea;


/**
 * Abstract R tree implementation which includes both recursive and non-recursive versions of the core methods (marked as abstract)
 *
 * @param <T> the type of data in teh tree
 */
abstract class AbstractRTree<T> {
    /**
     * Default bulk loader
     */
    public static BulkLoader DEFAULT_BULK_LOADER = BulkLoader.OVERLAP_MINIMIZING_TOPDOWN;
    /**
     * The default number of maximum entries per node
     */
    static final int DEFAULT_MAX_ENTRIES = 9;
    /**
     * The minimum entries per node
     */
    final int minEntries;

    /**
     * The maximum entries per node
     */
    final int maxEntries;
    /**
     * The root of the tree
     */
    RectangularNode<T> root;

    private int numData = 0;

    /**
     * Create an Rtree with the specified maximum entries per node. A number of bulk loaders use square root to
     * determine how best to pack data, so it can help to have this as a square number
     *
     * @param maxEntriesPerNode the maximum entries per node
     */
    protected AbstractRTree(int maxEntriesPerNode) {
        this.maxEntries = Math.max(4, maxEntriesPerNode);
        // min node fill is 40% for best performance
        this.minEntries = (int) Math.max(2, Math.ceil(this.maxEntries * 0.4));
        this.clear();
    }

    /**
     * Create an Rtree with a maximum of 9 entries per node
     */
    AbstractRTree() {
        this(DEFAULT_MAX_ENTRIES);
    }

    /**
     * @return a collection of all of the leaves in this tree
     * @implNote the default implementation, using streams, is most likely overridden
     */
    public abstract Collection<? extends Node2D<T>> getLeaves();

    /**
     * Returns if there is a given match in the given range
     *
     * @param minX the minimum x component of the data
     * @param minY the maximum x component of the data
     * @param maxX the minimum y component of the data
     * @param maxY the maximum y component of the data
     * @return if there is a leaf node in the given range
     */
    public abstract boolean collides(double minX, double minY, double maxX, double maxY);

    /**
     * Return a list of matches in the given range
     *
     * @param minX the minimum x component of the data
     * @param minY the maximum x component of the data
     * @param maxX the minimum y component of the data
     * @param maxY the maximum y component of the data
     * @return the leaf nodes in the given range
     * @implNote this implementation is here to provide an example of using one of the functional approaches
     */
    public abstract List<? extends Node2D<T>> search(double minX, double minY, double maxX, double maxY);

    /**
     * Traverse through the tree. If leafFunction is true, then the traversal terminates
     *
     * @param nodePredicate the function to test that the node is acceptable to continue traversal
     * @param leafFunction  the function to test that the leaf has been found
     */
    public abstract void traverse(Predicate<Node2D<T>> nodePredicate, Predicate<Node2D<T>> leafFunction);

    /**
     * Add a node into the tree
     *
     * @param item the item to add
     */
    public void put(Node2D<T> item) {
        if (item == null) {
            return;
        }
        put(item, this.root.height - 1);
    }

    /**
     * Remove an element from the tree
     *
     * @param minX     the minimum x component of the data
     * @param minY     the maximum x component of the data
     * @param maxX     the minimum y component of the data
     * @param maxY     the maximum y component of the data
     * @param equalsFn the test on the node to check that it is the correct value
     * @return whether the node was correctly removed
     */
    @SuppressWarnings("unchecked")
    public boolean remove(double minX, double minY, double maxX, double maxY, Predicate<Node2D<T>> equalsFn) {
        final Node2D<T>[] path = new Node2D[root.height];
        Node2D<T> node = this.root, parent = null;
        final int[] indices = new int[node.height];
        int i = 0, idx = 0;
        int level = 0;
        boolean goingUp = false;
        while (node != null || level > 0) {
            if (node == null) { // go up
                node = path[--level];
                parent = path[level];
                i = indices[--idx];
                goingUp = true;
            }

            // check current node
            if (node.leaf && equalsFn.test(node)) {
                assert parent != null;
                if (parent.children.remove(node)) {
                    --numData; //decrement here as otherwise calling clear later on will result in size being -1
                    // item found, remove the item and condense tree upwards
                    while (level > 0) {
                        if (path[--level].children.size() == 0) {
                            if (level > 0) {
                                path[level - 1].children.remove(path[level]);
                            } else {
                                this.clear();
                            }
                        } else {
                            path[level].recalculateBBox();
                        }
                        path[level] = null;
                    }
                    return true;
                }
            }

            if (!goingUp && !node.leaf && node.contains(minX, minY, maxX, maxY)) { // go down
                path[level++] = node;
                indices[idx++] = i;
                i = 0;
                parent = node;
                node = node.children.get(0);

            } else if (parent != null) { // go right
                node = parent.children.get(++i);
                goingUp = false;

            } else {
                node = null; // nothing found
            }
        }

        return false;
    }

    private List<? extends Node2D<T>> searchRecursive0(double minX, double minY, double maxX, double maxY, Node2D<T> node, List<Node2D<T>> results) {
        if (node == null) {
            return results;
        }
        if (node.intersects(minX, minY, maxX, maxY)) {
            if (node.leaf) {
                results.add(node);
            } else {
                for (int i = 0; i < node.children.size(); i++) {
                    searchRecursive0(minX, minY, maxX, maxY, node.children.get(i), results);
                }
            }
        }
        return results;
    }

    private Set<? extends Node2D<T>> searchRecursive0(double minX, double minY, double maxX, double maxY, Node2D<T> node, Set<Node2D<T>> results) {
        if (node == null) {
            return results;
        }
        if (node.intersects(minX, minY, maxX, maxY)) {
            if (node.leaf) {
                results.add(node);
            } else {
                for (int i = 0; i < node.children.size(); i++) {
                    searchRecursive0(minX, minY, maxX, maxY, node.children.get(i), results);
                }
            }
        }
        return results;
    }

    List<? extends Node2D<T>> searchNonRecursive(double minX, double minY, double maxX, double maxY) {
        if (!root.intersects(minX, minY, maxX, maxY)) {
            return Collections.emptyList();
        }
        final List<Node2D<T>> result = new LinkedList<>();
        final Stack<Node2D<T>> nodesToSearch = getStack(this.root);
        while (!nodesToSearch.isEmpty()) {
            Node2D<T> node = nodesToSearch.pop();
            if (node.intersects(minX, minY, maxX, maxY)) {
                if (node.leaf) {
                    result.add(node);
                } else {
                    nodesToSearch.addAll(node.children);
                }
            }
        }
        return result;
    }

    List<? extends Node2D<T>> searchRecursive(double minX, double minY, double maxX, double maxY) {
        if (!this.root.intersects(minX, minY, maxX, maxY)) {
            return Collections.emptyList();
        }
        return searchRecursive0(minX, minY, maxX, maxY, this.root, new LinkedList<>());
    }

    Set<? extends Node2D<T>> searchRecursive(Set<Node2D<T>> out, double minX, double minY, double maxX, double maxY) {
        if (!this.root.intersects(minX, minY, maxX, maxY)) {
            return Collections.emptySet();
        }
        return searchRecursive0(minX, minY, maxX, maxY, this.root, out);
    }

    private void traverseRecursive0(Predicate<Node2D<T>> nodePredicate, Predicate<Node2D<T>> leafFunction, Node2D<T> node) {
        if (node == null) {
            return;
        }
        if (nodePredicate.test(node)) {
            if (node.leaf) {
                leafFunction.test(node);
            } else {
                for (int i = 0; i < node.children.size(); i++) {
                    traverseRecursive0(nodePredicate, leafFunction, node.children.get(i));
                }
            }
        }
    }

    void traverseRecursive(Predicate<Node2D<T>> nodePredicate, Predicate<Node2D<T>> leafFunction) {
        traverseRecursive0(nodePredicate, leafFunction, root);
    }

    void traverseNonRecursive(Predicate<Node2D<T>> nodePredicate, Predicate<Node2D<T>> leafPredicate) {
        final Stack<Node2D<T>> nodesToSearch = getStack(this.root);
        while (!nodesToSearch.isEmpty()) {
            Node2D<T> node = nodesToSearch.pop();
            if (node.leaf) {
                if (leafPredicate.test(node)) {
                    return;
                }
            } else {
                if (nodePredicate.test(node)) {
                    nodesToSearch.addAll(node.children);
                }
            }

        }
    }

    private boolean collidesRecursive0(double minX, double minY, double maxX, double maxY, Node2D<T> node) {
        if (node == null) {
            return false;
        }
        boolean intersects = false;
        if (node.intersects(minX, minY, maxX, maxY)) {
            if (node.leaf) {
                intersects = true;
            } else {
                int i = 0;
                while (!intersects || i < node.children.size()) {
                    intersects |= collidesRecursive0(minX, minY, maxX, maxY, node.children.get(i++));
                }
            }
        }
        return intersects;
    }

    boolean collidesRecursive(double minX, double minY, double maxX, double maxY) {
        if (!root.intersects(minX, minY, maxX, maxY)) {
            return false;
        }
        return collidesRecursive0(minX, minY, maxX, maxY, root);
    }

    boolean collidesNonRecursive(double minX, double minY, double maxX, double maxY) {
        Node2D<T> node = this.root;
        if (!node.intersects(minX, minY, maxX, maxY)) {
            return false;
        }

        Stack<Node2D<T>> nodesToSearch = getStack();
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

    @SafeVarargs
    public final void putAll(Node2D<T>... data) {
        putAll(DEFAULT_BULK_LOADER, data);
    }

    @SafeVarargs
    public final void putAll(BulkLoader loader, Node2D<T>... data) {
        if (data == null || data.length == 0) {
            return;
        }
        if (data.length < minEntries) {
            for (Node2D<T> datum : data) {
                this.put(datum);
            }
            return;
        }

        // recursively build the tree with the given data from scratch using OMT algorithm
        @SuppressWarnings("unchecked")
        RectangularNode<T> node = (RectangularNode<T>) loader.bulkLoad((AbstractRTree<Object>) this, (Node2D<Object>[]) data);
        mergeSubtree(this, node, data.length);
    }

    /**
     * Remove all the elements from the tree
     */
    public void clear() {
        this.root = new RectangularNode<>(new ArrayList<>(minEntries));
        root.height = 0;
        numData = 0;
    }

    /**
     * @return the number of data points in the tree
     */
    public int size() {
        return numData;
    }

    /**
     * @return an iterable over the leaves in the tree
     */
    public Iterable<Node2D<T>> leaves() {
        return () -> new Iterator<>() {
            private final Stack<Node2D<T>> stack = getStack(root);
            private int i = 0;

            @Override
            public boolean hasNext() {
                return i < size();
            }

            @Override
            public Node2D<T> next() {
                while (!stack.isEmpty()) {
                    final Node2D<T> node = stack.pop();
                    if (node.leaf) {
                        ++i;
                        return node;
                    } else {
                        stack.addAll(node.children);
                    }
                }
                throw new IndexOutOfBoundsException();
            }
        };
    }

    @Override
    public String toString() {
        return String.format("RTree containing %d node%s {min: {x: %s, y: %s}, max: {x: %s, y: %s}}", size(), size() == 1 ? EMPTY_STRING : 's', root.minX, root.minY, root.maxX, root.maxY);
    }

    /**
     * Gets the stack associated with this object. Initializes it if it hasn't been used yet.
     * Clears it if has already been used
     *
     * @param initialValues the initial values in the stack
     * @param <E>           the type of elements in the stack
     * @return the stack with the initial values
     */
    @SafeVarargs
    protected static <E> Stack<E> getStack(E... initialValues) {
        final Stack<E> stack = new Stack<>();
        for (E v : initialValues) {
            stack.push(v);
        }
        return stack;
    }

    private List<? extends Node2D<T>> getLeavesRecursive0(Node2D<T> node, List<Node2D<T>> result) {
        if (node == null) {
            return result;
        }
        if (node.leaf) {
            result.add(node);
            return result;
        }
        for (final Node2D<T> n : node.children) {
            getLeavesRecursive0(n, result);
        }
        return result;
    }

    /**
     * Driver function for recursive all
     *
     * @param start  the node to start at
     * @param result the list to add to
     * @return a list of all the leaf nodes
     */
    protected List<? extends Node2D<T>> getLeavesRecursive(Node2D<T> start, List<Node2D<T>> result) {
        return getLeavesRecursive0(start, result);
    }

    protected List<? extends Node2D<T>> getLeavesNonRecursive(Node2D<T> node, List<Node2D<T>> result) {
        Stack<Node2D<T>> nodesToSearch = new Stack<>();
        while (node != null) {
            if (node.leaf) {
                result.add(node);
            } else {
                nodesToSearch.addAll(node.children);
            }

            node = nodesToSearch.isEmpty() ? null : nodesToSearch.pop();
        }
        return result;
    }

    private Node2D<T> chooseSubtree(Node2D<T> bbox, Node2D<T> node, int level, List<Node2D<T>> path) {
        while (true) {
            path.add(node);
            if (node.leaf || path.size() - 1 == level) {
                break;
            }
            double minArea = Double.POSITIVE_INFINITY;
            double minEnlargement = Double.POSITIVE_INFINITY;
            Node2D<T> targetNode = null;

            for (int i = 0; i < node.children.size(); i++) {
                Node2D<T> child = node.children.get(i);
                double area = child.calculateArea();
                double enlargement = enlargedArea(bbox, child) - area;

                // choose entry with the least area enlargement
                if (enlargement < minEnlargement) {
                    minEnlargement = enlargement;
                    minArea = Math.min(area, minArea);
                    targetNode = child;
                    // otherwise choose one with the smallest area
                } else if (enlargement == minEnlargement) {
                    if (area < minArea) {
                        minArea = area;
                        targetNode = child;
                    }
                }
            }


            node = targetNode != null ? targetNode : node.children.get(0);

        }

        return node;
    }

    private void put(Node2D<T> item, int level) {
        final List<Node2D<T>> insertPath = new LinkedList<>();
        final Node2D<T> node;
        // find the best node for accommodating the item, saving all nodes along the path too
        if (level == -1) {
            node = root;
            root.height = 1;
            level = 0;
            insertPath.add(root);
        } else {
            node = chooseSubtree(item, root, level, insertPath);
        }

        // put the item into the node
        node.children.add(item);
        union(node, item);

        // split on node overflow; propagate upwards if necessary
        while (level >= 0) {
            if (insertPath.get(level).children.size() > maxEntries) {
                split(insertPath, level--);
            } else {
                break;
            }
        }

        // adjust bboxes along the insertion path
        for (int i = level - 1; i >= 0; --i) {
            union(insertPath.get(i), item);
        }
        ++numData;
    }

    // split overflowed node into two
    private void split(List<Node2D<T>> insertPath, int level) {
        final Node2D<T> node = insertPath.get(level);
        int M = node.children.size();
        int m = this.minEntries;

        chooseSplitAxis(node, m, M);

        int splitIndex = chooseSplitIndex(node, m, M);

        final Node2D<T> newNode = new RectangularNode<>(splice(new ArrayList<>(minEntries), node.children, splitIndex, node.children.size() - splitIndex));
        newNode.height = node.height;

        node.recalculateBBox();
        newNode.recalculateBBox();

        if (level != 0) {
            insertPath.get(level - 1).children.add(newNode);
        } else {
            splitRoot(node, newNode);
        }
    }

    /**
     * Split root node
     *
     * @param node    the new child node of the root
     * @param newNode the other new child of the root
     */
    private void splitRoot(Node2D<T> node, Node2D<T> newNode) {
        final List<Node2D<T>> children = new ArrayList<>(minEntries);
        children.add(node);
        children.add(newNode);
        this.root = new RectangularNode<>(children);
        this.root.height = node.height + 1;
        this.root.recalculateBBox();
    }

    private int chooseSplitIndex(Node2D<T> node, int m, int M) {
        int index = -1;
        boolean foundIndex = false;
        double minOverlap = Double.POSITIVE_INFINITY;
        double minArea = Double.POSITIVE_INFINITY;

        for (int i = m; i <= M - m; ++i) {
            final Node2D<T> bbox1 = distBBox(node, 0, i, null);
            final Node2D<T> bbox2 = distBBox(node, i, M, null);

            double overlap = intersectionArea(bbox1, bbox2);
            double area = bbox1.calculateArea() + bbox2.calculateArea();

            // choose distribution with minimum overlap
            if (overlap < minOverlap) {
                minOverlap = overlap;
                index = i;
                foundIndex = true;

                minArea = Math.min(area, minArea);

            } else if (overlap == minOverlap) {
                // otherwise choose distribution with minimum area
                if (area < minArea) {
                    minArea = area;
                    index = i;
                    foundIndex = true;
                }
            }
        }

        return !foundIndex ? M - m : index;
    }

    // sorts node children by the best axis for split
    private void chooseSplitAxis(Node2D<T> node, int m, int M) {
        double xMargin = this.allDistMargin(node, m, M, RectangularNode::compareMinX);
        double yMargin = this.allDistMargin(node, m, M, RectangularNode::compareMinY);

        // if total distributions margin value is minimal for x, sort by minX,
        // otherwise it's already sorted by minY
        if (xMargin < yMargin) {
            node.children.sort(RectangularNode::compareMinX);
        }
    }

    // total margin of all possible split distributions where each node is at least m full
    private double allDistMargin(Node2D<T> node, int m, int M, Comparator<Node2D<T>> compare) {

        node.children.sort(compare);

        final Node2D<T> leftBBox = distBBox(node, 0, m, null);
        final Node2D<T> rightBBox = distBBox(node, M - m, M, null);
        double margin = leftBBox.calculateHalfPerimeter() + rightBBox.calculateHalfPerimeter();

        for (int i = m; i < M - m; i++) {
            Node2D<T> child = node.children.get(i);
            union(leftBBox, child);
            margin += leftBBox.calculateHalfPerimeter();
        }

        for (int i = M - m - 1; i >= m; i--) {
            Node2D<T> child = node.children.get(i);
            union(rightBBox, child);
            margin += rightBBox.calculateHalfPerimeter();
        }

        return margin;
    }


    /**
     * Merge a subtree into an Rtree
     *
     * @param tree        the tree to merge into
     * @param subtreeRoot the root of the subtree
     * @param numNewData  the number of data nodes
     * @param <T>         the type of the data
     */
    private static <T> void mergeSubtree(AbstractRTree<T> tree, RectangularNode<T> subtreeRoot, int numNewData) {

        tree.numData += numNewData;

        if (tree.root.children.size() == 0) {
            // save as is if tree is empty
            tree.root = subtreeRoot;
        } else if (subtreeRoot.height == 1) {
            for (final Node2D<T> datum : subtreeRoot.children) {
                tree.put(datum);
                --tree.numData;
            }
        } else if (tree.root.height == subtreeRoot.height) {
            // split root if trees have the same height
            tree.splitRoot(tree.root, subtreeRoot);

        } else {
            if (tree.root.height < subtreeRoot.height) {
                // swap trees if inserted one is bigger
                final RectangularNode<T> tmpNode = tree.root;
                tree.root = subtreeRoot;
                subtreeRoot = tmpNode;
            }
            // insert the small tree into the large tree at appropriate level
            tree.put(subtreeRoot, tree.root.height - subtreeRoot.height - 1);
            --tree.numData;//correct for put operation
        }
    }

    /**
     * Utility method to splice a list
     *
     * @param out         the output list
     * @param collection  the list to splice
     * @param from        the index to splice from
     * @param numElements the number of elements to splice
     * @param <E>         the type of the elements in the lists
     * @param <C>         the type of the collection
     * @return the output collection
     */
    static <E, C extends List<E>> C splice(C out, List<E> collection, int from, int numElements) {
        for (int i = 0; i < numElements; ++i) {
            out.add(collection.remove(from));
        }
        return out;
    }

    /**
     * @return an iterable over the data in the tree. This is likely to be a slow operation as it does not take
     * advantage of the tree structure. The order of the data may not be the same as the insertion order
     */
    public Iterable<T> data() {
        return () -> new Iterator<T>() {
            private final Iterator<Node2D<T>> nodes = leaves().iterator();

            @Override
            public boolean hasNext() {
                return nodes.hasNext();
            }

            @Override
            public T next() {
                return nodes.next().get();
            }
        };
    }

    /**
     * @return whether the tree is empty
     */
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Add a data element into the tree
     *
     * @param minX the minimum x component of the data
     * @param minY the maximum x component of the data
     * @param maxX the minimum y component of the data
     * @param maxY the maximum y component of the data
     * @param data the data
     */
    public void put(double minX, double minY, double maxX, double maxY, T data) {
        put(new RectangularNode<>(minX, minY, maxX, maxY, data));
    }

    /**
     * Remove an element from the tree
     *
     * @param range          the range to search in
     * @param equalsFunction the test on the node to check that it is the correct value
     * @return whether the node was correctly removed
     */
    public boolean remove(Node2D<T> range, Predicate<Node2D<T>> equalsFunction) {
        return remove(range.getMinX(), range.getMinY(), range.getMaxX(), range.getMaxY(), equalsFunction);
    }

    /**
     * Test to see if any nodes collide with the given rectangle
     *
     * @param range the range of the rectangle
     * @return whether any of the nodes collide with the given rectangle
     */
    public boolean collides(RectangularNode<T> range) {
        return collides(range.getMinX(), range.getMinY(), range.getMaxX(), range.getMaxY());
    }

    /**
     * Return a list of nodes that can be found in the given range
     *
     * @param range the range to search
     * @return a list of nodes that are found within the given range
     */
    public Collection<? extends Node2D<T>> search(RectangularNode<T> range) {
        return search(range.getMinX(), range.getMinY(), range.getMaxX(), range.getMaxY());
    }

    /**
     * Traverse through the tree until a match is found
     *
     * @param nodePredicate the function to apply to internal nodes
     * @param leafFunction  the function to apply to leaf nodes
     * @return the matching leaf node
     */

    public Node2D<T> findAny(Predicate<Node2D<T>> nodePredicate, Predicate<Node2D<T>> leafFunction) {
        final Object[] out = new Object[1];
        traverse(nodePredicate, l -> {
            if (leafFunction.test(l)) {
                out[0] = l;
                //return true to terminate
                return true;
            }
            return false;
        });
        @SuppressWarnings("unchecked") final Node2D<T> cast = (Node2D<T>) out[0];
        return cast;
    }

    /**
     * Traverse through the tree until all matching leaves are found
     *
     * @param nodePredicate the function to apply to internal nodes
     * @param leafFunction  the function to apply to leaf nodes
     * @return an iterable of the matching data (these will most likely be {@link LinkedList})
     */
    public Collection<Node2D<T>> findAll(Predicate<Node2D<T>> nodePredicate, Predicate<Node2D<T>> leafFunction) {
        final Collection<Node2D<T>> out = new LinkedList<>();
        traverse(nodePredicate, l -> {
            if (leafFunction.test(l)) {
                out.add(l);
            }
            //return false to continue traversal
            return false;
        });
        return out;
    }

    /**
     * Traverse through the tree until all matching leaves are found. The iterable is sorted by the supplied comparator
     *
     * @param nodePredicate the function to apply to internal nodes
     * @param leafFunction  the function to apply to leaf nodes
     * @param sort          the comparator used to sort the data
     * @return an iterable of the matching data (these will most likely be {@link SortedSet})
     */
    public Collection<Node2D<T>> findAll(Predicate<Node2D<T>> nodePredicate, Predicate<Node2D<T>> leafFunction, Comparator<Node2D<T>> sort) {
        final SortedSet<Node2D<T>> out = new TreeSet<>(sort);
        traverse(nodePredicate, l -> {
            if (leafFunction.test(l)) {
                out.add(l);
            }
            //return false to continue traversal
            return false;
        });
        return out;
    }

    /**
     * Traverse through the tree until the "minimum" match is found (as defined by a comparator)
     *
     * @param nodePredicate the function to apply to internal nodes
     * @param leafFunction  the function to apply to leaf nodes
     * @param sort          the comparator used to sort the data
     * @return an iterable of the matching data (these will most likely be {@link SortedSet})
     */
    @SuppressWarnings("unchecked")
    public Node2D<T> findNearest(Predicate<Node2D<T>> nodePredicate, Predicate<Node2D<T>> leafFunction, Comparator<Node2D<T>> sort) {
        final Object[] out = new Object[1];
        traverse(nodePredicate, l -> {
            if (leafFunction.test(l)) {
                if (out[0] == null || sort.compare((Node2D<T>) out[0], l) < 0) {
                    out[0] = l;
                }
            }
            //return false to continue traversal
            return false;
        });
        return (Node2D<T>) out[0];
    }

    /**
     * Traverse through the tree until the "maximum" match is found (as defined be a comparator)
     *
     * @param nodePredicate the function to apply to internal nodes
     * @param leafFunction  the function to apply to leaf nodes
     * @param sort          the comparator used to sort the data
     * @return an iterable of the matching data (these will most likely be {@link SortedSet})
     */
    @SuppressWarnings("unchecked")
    public Node2D<T> findFurthest(Predicate<Node2D<T>> nodePredicate, Predicate<Node2D<T>> leafFunction, Comparator<Node2D<T>> sort) {
        final Object[] out = new Object[1];
        traverse(nodePredicate, l -> {
            if (leafFunction.test(l)) {
                if (out[0] == null || sort.compare((Node2D<T>) out[0], l) > 0) {
                    out[0] = l;
                }
            }
            //return false to continue traversal
            return false;
        });
        return (Node2D<T>) out[0];
    }
}
