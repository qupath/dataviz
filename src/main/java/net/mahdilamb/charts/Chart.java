package net.mahdilamb.charts;

import net.mahdilamb.charts.graphics.ChartCanvas;
import net.mahdilamb.geom2d.geometries.Point;

import java.util.Objects;

public abstract class Chart<S extends PlotSeries<S>, IMG> extends Figure<S, IMG> {


    /**
     * Create a chart figure
     *
     * @param title  title of the chart
     * @param width  width of the chart
     * @param height height of the chart
     * @param layout the layout, including series, of the chart
     */
    protected Chart(String title, double width, double height, PlotImpl<S> layout) {
        super(title, width, height, layout);
    }

    /**
     * @return the title of the chart
     */
    public final Title getTitle() {
        return title;
    }

    /**
     * Set the title of the chart
     *
     * @param text the title of the chart
     */
    public final void setTitle(String text) {
        this.title.setTitle(text);
        redraw();
    }

    /**
     * @return the plot area
     */
    public final Plot<S> getPlot() {
        return plotArea;
    }

    /**
     * @return the colorbars
     */
    public final ColorScale getColorScale() {
        return colorScales;
    }

    /**
     * @return the legend
     */
    public final Legend getLegend() {
        return legend;
    }

    /**
     * Set an axis title
     *
     * @param oldTitle the current title name
     * @param newTitle the new title name
     * @return whether the title was able to be updated
     */
    public final boolean setAxisTitle(final String oldTitle, final String newTitle) {
        final Axis axis = plotArea.getAxis(oldTitle);
        if (axis == null) {
            return false;
        }
        axis.setTitle(newTitle);
        return true;
    }

    /**
     * An XY plot layout
     */
    public static class RectangularPlot<S extends PlotSeries<S>> extends PlotImpl<S> {


        private final Axis xAxis, yAxis;

        @SafeVarargs
        public RectangularPlot(Axis xAxis, Axis yAxis, S... series) {
            super(series);
            this.xAxis = Objects.requireNonNull(xAxis);
            this.yAxis = Objects.requireNonNull(yAxis);

        }

        @Override
        protected void draw(Figure<?, ?> source, ChartCanvas<?> canvas, double minX, double minY, double maxX, double maxY) {
        }

        public Axis getXAxis() {
            return xAxis;
        }

        public Axis getYAxis() {
            return yAxis;
        }

        @Override
        public Point getPositionFromValue(double x, double y) {
            //TODO
            return null;
        }

        @Override
        public Point getValueFromPosition(double x, double y) {
            //TODO
            return null;
        }
    }

    public static class FacetPlot<S extends PlotSeries<S>> extends PlotImpl<S> {
        FacetPlot(final S series) {
            super(series);
        }

        @Override
        protected void draw(Figure<?, ?> source, ChartCanvas<?> canvas, double minX, double minY, double maxX, double maxY) {

        }

        @Override
        public Point getPositionFromValue(double x, double y) {
            //TODO
            return null;
        }

        @Override
        public Point getValueFromPosition(double x, double y) {
            //TODO
            return null;
        }
    }

}