package net.mahdilamb.dataviz;

import net.mahdilamb.dataviz.figure.Renderer;
import net.mahdilamb.dataviz.graphics.GraphicsBuffer;
import net.mahdilamb.dataviz.graphics.shapes.Marker;
import net.mahdilamb.dataviz.graphics.shapes.MarkerShape;
import net.mahdilamb.dataviz.layouts.XYLayout;
import net.mahdilamb.dataviz.utils.rtree.Node2D;

import java.awt.*;

public abstract class PlotShape<PL extends PlotLayout<PL>> extends Node2D {

    static final class Rectangle extends PlotShape<XYLayout> {

        double x, y, w, h;

        Rectangle(final PlotData<XYLayout> parent, int i, double x, double y, double w, double h) {
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
        <T> void fill(XYLayout plotLayout, Renderer<T> renderer, GraphicsBuffer<T> canvas) {
            canvas.setStroke(Color.DARK_GRAY);
            //canvas.strokeRect(plotLayout.plotArea.getX() + x, plotLayout.plotArea.getY() + y, w, h);
            //TODO

        }

        @Override
        <T> void stroke(XYLayout plotLayout, Renderer<T> renderer, GraphicsBuffer<T> canvas) {

        }
    }

    static final class PlotMarker extends PlotShape<XYLayout> {
        final double size;
        double x, y;

        PlotMarker(PlotData<XYLayout> parent, int i, double x, double y, double size) {
            super(parent, i);
            this.size = size;
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

        private static void updateMarker(double x, double y, double size, MarkerShape shape) {
            Marker.MARKER.x = x;
            Marker.MARKER.y = y;
            Marker.MARKER.shape = shape;
            Marker.MARKER.size = size;
        }


        @Override
        <T> void fill(XYLayout plotLayout, Renderer<T> renderer, GraphicsBuffer<T> canvas) {
            plotLayout.transformValueToPosition(x, y, (x, y) -> {
                updateMarker(x, y, size, MarkerShape.POINT);
                Marker.MARKER.fill(canvas);
            });
        }

        @Override
        <T> void stroke(XYLayout plotLayout, Renderer<T> renderer, GraphicsBuffer<T> canvas) {
            plotLayout.transformValueToPosition(x, y, (x, y) -> {
                updateMarker(x, y, size, MarkerShape.POINT);
                Marker.MARKER.stroke(canvas);
            });
        }
    }

    /**
     * The index of the shape that corresponds to the source data
     */
    protected int i;
    /**
     * The source data
     */
    protected PlotData<PL> parent;

    /**
     * Create a shape
     *
     * @param parent the source data
     * @param i      the index in the source data
     */
    PlotShape(PlotData<PL> parent, int i) {
        this.parent = parent;
        this.i = i;
    }

    abstract <T> void fill(PL plotLayout, Renderer<T> renderer, GraphicsBuffer<T> canvas);

    abstract <T> void stroke(PL plotLayout, Renderer<T> renderer, GraphicsBuffer<T> canvas);

}
