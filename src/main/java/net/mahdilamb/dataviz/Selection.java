package net.mahdilamb.dataviz;

import net.mahdilamb.dataframe.utils.BooleanArrayList;
import net.mahdilamb.dataframe.utils.DoubleArrayList;
import net.mahdilamb.dataviz.utils.rtree.RTree;

import java.util.List;

/**
 * Selections for use with plot layouts
 */
public abstract class Selection {
    /**
     * A polygon selection
     */
    public static class Polygon extends Selection {
        private final boolean useNonZero;
        private final DoubleArrayList x = new DoubleArrayList(), y = new DoubleArrayList();
        private double minX, minY, maxX, maxY;
        private boolean isClosed;

        /**
         * Create a polygon with the given Winding number rule
         *
         * @param useNonZero whether to consider a point in the polygon using the NON_ZERO rule or EVEN_ODD rule
         */
        public Polygon(boolean useNonZero) {
            this.useNonZero = useNonZero;
            reset();
        }

        /**
         * Add a vertex
         *
         * @param x the x of the vertex
         * @param y the y of the vertex
         * @return this polygon
         */
        public Polygon add(double x, double y) {
            if (isClosed) {
                reset();
            }
            this.x.add(x);
            this.y.add(y);
            minX = Math.min(minX, x);
            minY = Math.min(minY, y);
            maxX = Math.max(maxX, x);
            maxY = Math.max(maxY, y);
            return this;
        }

        /**
         * @param i the index of the vertex
         * @return the x of the vertex
         */
        public double getX(int i) {
            return x.get(i);
        }

        /**
         * @param i the index of the vertex
         * @return the y of the vertex
         */
        public double getY(int i) {
            return y.get(i);
        }

        /**
         * @return the current number of points in the polyline (if not closed) or polygon (if closed)
         */
        public int size() {
            return x.size();
        }

        /**
         * Close the polygon
         *
         * @return this polygon
         */
        public Polygon close() {
            //add a segment if the start and don't match
            if (x.get(0) != x.get(size() - 1) || y.get(0) != y.get(size() - 1)) {
                add(x.get(0), y.get(0));
            }
            isClosed = true;
            return this;
        }

        //reset the polygon
        private void reset() {
            minX = Double.POSITIVE_INFINITY;
            minY = Double.POSITIVE_INFINITY;
            maxX = Double.NEGATIVE_INFINITY;
            maxY = Double.NEGATIVE_INFINITY;
            x.clear();
            y.clear();
            isClosed = false;

        }

        /**
         * Calculate whether a point is on the left or right of a line
         *
         * @param P0x start x of line
         * @param P0y start y of line
         * @param P1x end x of line
         * @param P1y end y of line
         * @param P2x point x
         * @param P2y point y
         * @return {@literal >}0 if point is left of line, 0 if point on line, {@literal <}0 if point is right of line
         */
        static double isLeft(double P0x, double P0y, double P1x, double P1y, double P2x, double P2y) {
            return ((P1x - P0x) * (P2y - P0y)
                    - (P2x - P0x) * (P1y - P0y));
        }

        /**
         * Calculate the winding number of a point in a polygon. Based on <a href="http://geomalgorithms.com/a03-_inclusion.html">GeomAlgorithms.com</a>
         *
         * @param xs       the x points
         * @param ys       the y points
         * @param x        the x point
         * @param y        the y point
         * @param isClosed whether the polygon is closed (i.e. whether to "add" an additional segment to close the polyline)
         * @return the winding number of the point in polygon
         */
        static int windingNumber(DoubleArrayList xs, DoubleArrayList ys, double x, double y, boolean isClosed) {
            int wn = 0;
            int n = xs.size() - 1;
            for (int i = 0; i < n; ++i) {
                wn += segmentWN(xs.get(i), ys.get(i), xs.get(i + 1), ys.get(i + 1), x, y);
            }
            if (!isClosed) {
                wn += segmentWN(xs.get(n), ys.get(n), xs.get(0), ys.get(0), x, y);
            }
            return wn;
        }

        /**
         * Calculate the crossing number of a segment
         *
         * @param thisX the start x
         * @param thisY the start y
         * @param nextX the end x
         * @param nextY the end y
         * @param x     the query x point
         * @param y     the query y point
         * @return whether the direction of the crossing (to be adding to the accumulating winding number)
         */
        private static int segmentWN(double thisX, double thisY, double nextX, double nextY, double x, double y) {
            if (thisY <= y) {          // start y <= P.y
                if (nextY > y)      // an upward crossing
                    if (isLeft(thisX, thisY, nextX, nextY, x, y) > 0)  // P left of  edge
                        return 1;           // have  a valid up intersect
            } else {                        // start y > P.y (no test needed)
                if (nextY <= y)     // a downward crossing
                    if (isLeft(thisX, thisY, nextX, nextY, x, y) < 0)  // P right of  edge
                        return -1;            // have  a valid down intersect
            }
            return 0;
        }

        @Override
        public void apply(PlotLayout layout) {
            if (size() <= 1) {
                clear(layout);
                return;
            }
            for (final PlotData<?> t : layout.data) {
                t.selected = (t.selected == null) ? new BooleanArrayList(t.size()) : t.selected;
                t.selected.fill(false, t.size());
                t.anySelected = false;
                @SuppressWarnings("unchecked") final List<PlotData.PlotMarker> n = (((List<PlotData.PlotMarker>) layout.markers.getOrDefault(t, RTree.emptyTree()).search(minX, minY, maxX, maxY)));
                for (final PlotData.PlotMarker m : n) {
                    int windingNumber = windingNumber(x, y, m.getMidX(), m.getMidY(), isClosed);
                    if (useNonZero ? (windingNumber != 0) : ((windingNumber & 1) == 1)) {
                        t.anySelected = true;
                        t.selected.set(m.i(), true);
                    }
                }
            }
        }

        @Override
        public String toString() {
            final StringBuilder out = new StringBuilder();
            for (int i = 0; i < x.size(); ++i) {
                out.append(i == 0 ? '[' : ", ").append(String.format("(%s %s)", x.get(i), y.get(i)));
            }
            return out.append(']').toString();
        }


    }

    /**
     * Apply this selection to a layout
     *
     * @param layout the layout
     */
    public abstract void apply(PlotLayout layout);

    /**
     * Clear the selection from a layout
     *
     * @param layout the layout
     */
    public void clear(PlotLayout layout) {
        for (final PlotData<?> d : layout.data) {
            d.anySelected = false;
        }
    }

}
