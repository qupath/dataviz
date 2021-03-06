package net.mahdilamb.dataviz;

import net.mahdilamb.colormap.Color;
import net.mahdilamb.dataframe.utils.IntArrayList;
import net.mahdilamb.dataviz.graphics.MarkerShape;
import net.mahdilamb.dataviz.utils.rtree.Node2D;
import net.mahdilamb.dataviz.utils.rtree.PointNode;
import net.mahdilamb.dataviz.utils.rtree.RectangularNode;

import java.util.Comparator;

import static net.mahdilamb.dataviz.utils.Functions.EMPTY_RUNNABLE;

public abstract class PlotShape extends Node2D<Runnable> {
    static Comparator<Node2D<Runnable>> ORDER_COMPARATOR = new Comparator<Node2D<Runnable>>() {
        @Override
        public int compare(Node2D<Runnable> o1, Node2D<Runnable> o2) {
            return Integer.compare(((PlotShape) o1).i, ((PlotShape) o2).i);
        }
    };
    protected int i;
    protected PlotData<?> parent;

    public PlotShape(PlotData<?> parent, int i, Runnable data) {
        super(data);
        this.parent = parent;
        this.i = i;
    }

    public Color getColor() {
        return parent.getColor(i);
    }

    public double getSize() {
        return parent.getSize(i);
    }

    public MarkerShape getShape() {
        return parent.getShape(i);
    }

    public String getHoverText() {
        return parent.getHoverText(i);
    }

    public boolean isVisible() {
        return parent.isVisible(i);
    }

    protected static final class PlotPolygon extends PlotShape {
        protected static final class PlotPoint extends PointNode<Runnable> {

            public PlotPoint(double x, double y, Runnable data) {
                super(x, y, data);
            }

            public PlotPoint(double x, double y) {
                super(x, y, EMPTY_RUNNABLE);
            }

            @Override
            public String toString() {
                return String.format("Point {x: %s, y: %s}", getMidX(), getMidY());
            }
        }

        double minX = Double.POSITIVE_INFINITY, minY = Double.POSITIVE_INFINITY, maxX = Double.NEGATIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY;
        IntArrayList ids;
        PlotPoint[] points;

        protected PlotPolygon(PlotData.XYData<?> parent, IntArrayList ids, Runnable data) {
            super(parent, ids.size() == 0 ? -1 : ids.get(0), data);
            this.ids = ids;
        }

        protected PlotPolygon(PlotData.XYData<?> parent, IntArrayList ids) {
            this(parent, ids, EMPTY_RUNNABLE);
        }

        protected PlotPoint[] getPoints() {
            if (points == null) {
                minX = Double.POSITIVE_INFINITY;
                minY = Double.POSITIVE_INFINITY;
                maxX = Double.NEGATIVE_INFINITY;
                maxY = Double.NEGATIVE_INFINITY;

                switch (((PlotData.XYData<?>) parent).fillMode) {
                    case NONE:
                        points = new PlotPoint[0];
                        break;
                    case TO_SELF:
                        points = new PlotPoint[ids.size() + 1];
                        for (int i : ids) {
                            points[i] = new PlotPoint(((PlotData.XYData<?>) parent).getX(ids.get(i)), ((PlotData.XYData<?>) parent).getY(ids.get(i)));
                            minX = Math.min(minX, points[i].getMidX());
                            minY = Math.min(minY, points[i].getMidY());
                            maxX = Math.max(maxX, points[i].getMidX());
                            maxY = Math.max(maxY, points[i].getMidY());
                        }
                        points[ids.size()] = points[0];
                        break;
                    default:
                        throw new UnsupportedOperationException();
                }
            }
            return points;
        }

        @Override
        public double getMinX() {
            getPoints();
            return minX;
        }

        @Override
        public double getMinY() {
            getPoints();
            return minY;
        }

        @Override
        public double getMaxX() {
            getPoints();
            return maxX;
        }

        @Override
        public double getMaxY() {
            getPoints();
            return maxY;
        }

        @Override
        public String toString() {
            return String.format("Polygon {min=%s,%s, min=%s,%s, n=%s}", getMinX(), getMinY(), getMaxX(), getMaxY(), ids.size() + 1);
        }
    }

    protected static final class PlotPolyLine extends PlotShape {

        protected static final class Segment extends RectangularNode<Runnable> {
            PlotPolyLine line;
            double startX, startY, endX, endY;

            private Segment(double startX, double startY, double endX, double endY) {
                super(Math.min(startX, endX), Math.min(startY, endY), Math.max(startX, endX), Math.max(startY, endY));
                this.startX = startX;
                this.startY = startY;
                this.endX = endX;
                this.endY = endY;
            }
        }

        double minX = Double.POSITIVE_INFINITY, minY = Double.POSITIVE_INFINITY, maxX = Double.NEGATIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY;
        IntArrayList ids;
        private Segment[] segs;

        public PlotPolyLine(PlotData.XYData<?> parent, IntArrayList ids, Runnable data) {
            this(parent, ids.size() == 0 ? -1 : ids.get(0), ids, data);

        }

        public PlotPolyLine(PlotData.XYData<?> parent, IntArrayList ids) {
            this(parent, ids.size() == 0 ? -1 : ids.get(0), ids, EMPTY_RUNNABLE);

        }

        public PlotPolyLine(PlotData.XYData<?> parent, int i, IntArrayList ids) {
            this(parent, i, ids, EMPTY_RUNNABLE);

        }

        public PlotPolyLine(PlotData.XYData<?> parent, int i, IntArrayList ids, Runnable data) {
            super(parent, i, data);
            this.ids = ids;
        }

        @Override
        public Color getColor() {
            if (((PlotData.XYData<?>) parent).lineColor != null) {
                return ((PlotData.XYData<?>) parent).lineColor;
            }
            return parent.getColor(i);
        }

        Segment[] getSegments() {
            if (segs == null) {
                minX = Double.POSITIVE_INFINITY;
                minY = Double.POSITIVE_INFINITY;
                maxX = Double.NEGATIVE_INFINITY;
                maxY = Double.NEGATIVE_INFINITY;
                if (ids.size() < 2) {
                    segs = new Segment[0];
                } else {
                    segs = new Segment[ids.size() - 1];
                    for (int i = 1, h = 0; i < ids.size(); h = i++) {
                        segs[h] = new Segment(((PlotData.XYData<?>) parent).getX(ids.get(h)), ((PlotData.XYData<?>) parent).getY(ids.get(h)), ((PlotData.XYData<?>) parent).getX(ids.get(i)), ((PlotData.XYData<?>) parent).getY(ids.get(i)));
                        segs[h].line = this;
                        minX = Math.min(minX, segs[h].getMinX());
                        minY = Math.min(minY, segs[h].getMinY());
                        maxX = Math.max(maxX, segs[h].getMaxX());
                        maxY = Math.max(maxY, segs[h].getMaxY());
                    }
                }
            }
            return segs;
        }

        public double getStartX() {
            if (getSegments().length == 0) {
                return Double.NaN;
            }
            return getSegments()[0].startX;
        }

        public double getStartY() {
            if (getSegments().length == 0) {
                return Double.NaN;
            }
            return getSegments()[0].startY;
        }

        public double getEndX() {
            if (getSegments().length == 0) {
                return Double.NaN;
            }
            return getSegments()[getSegments().length - 1].endX;
        }

        public double getEndY() {
            if (getSegments().length == 0) {
                return Double.NaN;
            }
            return getSegments()[getSegments().length - 1].endY;
        }

        @Override
        public double getMinX() {
            getSegments();
            return minX;
        }

        @Override
        public double getMinY() {
            getSegments();
            return minY;
        }

        @Override
        public double getMaxX() {
            getSegments();
            return maxX;
        }

        @Override
        public double getMaxY() {
            getSegments();
            return maxY;
        }
    }

    protected static final class PlotRectangle extends PlotShape {

        double x, y, w, h;

        protected PlotRectangle(final PlotData<?> parent, int i, double x, double y, double w, double h, Runnable data) {
            super(parent, i, data);
            this.parent = parent;
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }

        protected PlotRectangle(final PlotData<?> parent, int i, double x, double y, double w, double h) {
            this(parent, i, x, y, w, h, EMPTY_RUNNABLE);
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
        public double getMaxX() {
            return x + w;
        }

        @Override
        public double getMaxY() {
            return y + h;
        }
    }

    protected static final class PlotMarker extends PlotShape {
        private final double size;
        double xMin, xMax, yMin, yMax;

        @Override
        public boolean intersects(double minX, double minY, double maxX, double maxY) {
            if (minX == maxX && minY == maxY) {
                double w = size * .5 / parent.layout.getXAxis().scale;
                double h = size * .5 / parent.layout.getYAxis().scale;
                return RectangularNode.intersects(minX, minY, maxX, maxY, this.xMin - w, this.yMin - h, this.xMax + w, this.yMax + h);
            }

            return super.intersects(minX, minY, maxX, maxY);
        }

        public PlotMarker(PlotData<?> parent, int i, double x, double y, double size, Runnable data) {
            super(parent, i, data);
            this.size = size;
            xMin = x;
            xMax = x;
            yMin = y;
            yMax = y;

        }

        public PlotMarker(PlotData<?> parent, int i, double x, double y, double size) {
            this(parent, i, x, y, size, EMPTY_RUNNABLE);
        }

        @Override
        public double getMinX() {
            return xMin;
        }

        @Override
        public double getMinY() {
            return yMin;
        }

        @Override
        public double getMaxX() {
            return xMax;
        }

        @Override
        public double getMaxY() {
            return yMax;
        }

        @Override
        public String toString() {
            return String.format("PlotMarker {%s, %s}", ((PlotData.XYData<?>) parent).getX(i), ((PlotData.XYData<?>) parent).getY(i));
        }
    }
}