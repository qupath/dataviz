package net.mahdilamb.dataviz;

import net.mahdilamb.dataframe.utils.IntArrayList;
import net.mahdilamb.dataviz.data.RelationalData;
import net.mahdilamb.dataviz.figure.Renderer;
import net.mahdilamb.dataviz.figure.Tooltip;
import net.mahdilamb.dataviz.graphics.GraphicsBuffer;
import net.mahdilamb.dataviz.graphics.Side;
import net.mahdilamb.dataviz.layouts.XYLayout;
import net.mahdilamb.dataviz.utils.rtree.Node2D;
import net.mahdilamb.dataviz.utils.rtree.RectangularNode;

import java.awt.*;

public abstract class PlotShape<PL extends PlotLayout<PL>> extends Node2D {
    static final class PolyLine extends PlotShape<XYLayout> {

        protected static final class Segment extends Node2D {
            PolyLine line;
            double startX, startY, endX, endY;

            private Segment(double startX, double startY, double endX, double endY) {
                this.startX = startX;
                this.startY = startY;
                this.endX = endX;
                this.endY = endY;
            }

            @Override
            public double getMinX() {
                return Math.min(startX, endX);
            }

            @Override
            public double getMinY() {
                return Math.min(startY, endY);
            }

            @Override
            public double getMaxX() {
                return Math.max(startX, endX);
            }

            @Override
            public double getMaxY() {
                return Math.max(startY, endY);
            }
        }

        double minX = Double.POSITIVE_INFINITY, minY = Double.POSITIVE_INFINITY, maxX = Double.NEGATIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY;
        IntArrayList ids;
        private Segment[] segs;
        double[] xs;
        double[] ys;

        public PolyLine(RelationalData<?> parent, IntArrayList ids) {
            this(parent, ids.size() == 0 ? -1 : ids.get(0), ids);

        }

        public PolyLine(RelationalData<?> parent, double[] xs, double[] ys) {
            super(parent, -1);
            this.xs = xs;
            this.ys = ys;
        }

        public PolyLine(RelationalData<?> parent, int i, IntArrayList ids) {
            super(parent, i);
            this.ids = ids;
        }

        public Color getColor() {
            if (parent.lineColor != null) {
                return parent.lineColor;
            }
            return parent.getColor(i);
        }

        Segment[] getSegments() {
            if (segs == null) {
                minX = Double.POSITIVE_INFINITY;
                minY = Double.POSITIVE_INFINITY;
                maxX = Double.NEGATIVE_INFINITY;
                maxY = Double.NEGATIVE_INFINITY;
                if (xs != null) {
                    segs = new Segment[xs.length - 1];
                    for (int i = 1, h = 0; i < xs.length; h = i++) {
                        segs[h] = new Segment(xs[h], ys[h], xs[i], ys[i]);
                        segs[h].line = this;
                        minX = Math.min(minX, segs[h].getMinX());
                        minY = Math.min(minY, segs[h].getMinY());
                        maxX = Math.max(maxX, segs[h].getMaxX());
                        maxY = Math.max(maxY, segs[h].getMaxY());
                    }
                } else {
                    if (ids.size() < 2) {
                        segs = new Segment[0];
                    } else {
                        segs = new Segment[ids.size() - 1];
                        for (int i = 1, h = 0; i < ids.size(); h = i++) {
                            segs[h] = new Segment(((RelationalData<?>) parent).getX(ids.get(h)), ((RelationalData<?>) parent).getY(ids.get(h)), ((RelationalData<?>) parent).getX(ids.get(i)), ((RelationalData<?>) parent).getY(ids.get(i)));
                            segs[h].line = this;
                            minX = Math.min(minX, segs[h].getMinX());
                            minY = Math.min(minY, segs[h].getMinY());
                            maxX = Math.max(maxX, segs[h].getMaxX());
                            maxY = Math.max(maxY, segs[h].getMaxY());
                        }
                    }
                }
            }
            return segs;
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


        @Override
        <T> void draw(XYLayout plotLayout, Renderer<T> renderer, GraphicsBuffer<T> canvas) {
            canvas.setStroke(getColor());
            if (getSegments().length == 1) {
                canvas.strokeLine(plotLayout.getXAxis().getPositionFromValue(getSegments()[0].startX), plotLayout.getYAxis().getPositionFromValue(getSegments()[0].startY), plotLayout.getXAxis().getPositionFromValue(getSegments()[0].endX), plotLayout.getYAxis().getPositionFromValue(getSegments()[0].endY));
                return;
            }
            canvas.beginPath();
            canvas.moveTo(plotLayout.getXAxis().getPositionFromValue(getSegments()[0].startX), plotLayout.getYAxis().getPositionFromValue(getSegments()[0].startY));
            for (int i = 1; i < getSegments().length; ++i) {
                canvas.lineTo(plotLayout.getXAxis().getPositionFromValue(getSegments()[i].endX), plotLayout.getYAxis().getPositionFromValue(getSegments()[i].endY));
            }
            canvas.stroke();
        }

        @Override
        Tooltip createTooltip() {
            //TODO
            return null;
        }
    }

    static final class Rectangle extends PlotShape<XYLayout> {

        double x, y, w, h;

        Rectangle(final PlotData<?, XYLayout> parent, int i, double x, double y, double w, double h) {
            super(parent, i);
            this.parent = parent;
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
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

        @Override
        <T> void draw(XYLayout plotLayout, Renderer<T> renderer, GraphicsBuffer<T> canvas) {
            canvas.setStroke(Color.DARK_GRAY);
            //canvas.strokeRect(plotLayout.plotArea.getX() + x, plotLayout.plotArea.getY() + y, w, h);
            //TODO

        }

        @Override
        Tooltip createTooltip() {
            //TODO
            return null;
        }

    }

    static final class PlotMarker extends PlotShape<XYLayout> {

        final double x, y;

        PlotMarker(PlotData<?, XYLayout> parent, int i, double x, double y) {
            super(parent, i);
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
        public double getMaxX() {
            return x;
        }

        @Override
        public double getMaxY() {
            return y;
        }

        @Override
        public String toString() {
            return String.format("PlotMarker {%s, %s}", x, y);
        }

        @Override
        <T> void draw(XYLayout plotLayout, Renderer<T> renderer, GraphicsBuffer<T> canvas) {

            plotLayout.transformValueToPosition(x, y, (x, y) -> MarkerShape.CIRCLE.fill.paint(canvas, x, y, parent.getSize(i)));
            //todo
            plotLayout.transformValueToPosition(x, y, (x, y) -> MarkerShape.CIRCLE.stroke.paint(canvas, x, y, parent.getSize(i)));

        }

        @Override
        Tooltip createTooltip() {
            Color color = parent.getColor(i);
            if (color.getAlpha() != 255) {
                color = new Color(color.getRGB());
            }
            return Tooltip.createWithOutline(
                    parent.getLayout().getXAxis().getPositionFromValue(x),
                    parent.getLayout().getYAxis().getPositionFromValue(y),
                    Side.LEFT,
                    color,
                    parent.hoverFormatter.get(i),
                    true
            );

        }

        @Override
        public boolean contains(double minX, double minY, double maxX, double maxY) {
            double w = parent.getSize(i) / parent.getLayout().getXAxis().scale;
            double h = parent.getSize(i) / parent.getLayout().getYAxis().scale;
            return RectangularNode.intersects(x, y, x, y, x - w, y - h, x + w, y + h);
        }


    }

    /**
     * The index of the shape that corresponds to the source data
     */
    protected int i;
    /**
     * The source data
     */
    protected PlotData<?, PL> parent;

    /**
     * Create a shape
     *
     * @param parent the source data
     * @param i      the index in the source data
     */
    PlotShape(PlotData<?, PL> parent, int i) {
        this.parent = parent;
        this.i = i;
    }

    abstract <T> void draw(PL plotLayout, Renderer<T> renderer, GraphicsBuffer<T> canvas);

    abstract Tooltip createTooltip();

}
