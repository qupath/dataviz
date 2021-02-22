package net.mahdilamb.charts;

import net.mahdilamb.charts.graphics.ChartCanvas;
import net.mahdilamb.charts.graphics.ClipShape;

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
    static class RectangularPlot<S extends PlotSeries<S>> extends PlotImpl<S> {


        private final Axis xAxis, yAxis;
        private PlotSeries<?> xMarginal, yMarginal;

        @SafeVarargs
        public RectangularPlot(Axis xAxis, Axis yAxis, S... series) {
            super(series);
            this.xAxis = Objects.requireNonNull(xAxis);
            this.yAxis = Objects.requireNonNull(yAxis);

        }

        @SafeVarargs
        public RectangularPlot(Axis xAxis, Axis yAxis, PlotSeries<?> xMarginal, PlotSeries<?> yMarginal, S... series) {
            this(xAxis, yAxis, series);
            this.xMarginal = xMarginal;
            this.yMarginal = yMarginal;
        }

        @Override
        protected void layoutComponent(Figure<?, ?> source, double minX, double minY, double maxX, double maxY) {
            setBoundsFromExtent(minX, minY, maxX, maxY);
            //todo check for y marginal
            yAxis.partialLayoutYAxis(source, minX, minY, maxX, maxY);
            xAxis.posX = yAxis.posX + yAxis.sizeX;

            //todo check for x marginal
            xAxis.partialLayoutXAxis(source, minX, minY, maxX, maxY);
            yAxis.sizeY = xAxis.posY - yAxis.posY;
            xAxis.updateXAxisScale();
            yAxis.updateYAxisScale();

        }

        @Override
        protected void drawComponent(Figure<?, ?> source, ChartCanvas<?> canvas) {
            if (backgroundColor != null) {
                canvas.setFill(backgroundColor);
                canvas.fillRect(xAxis.posX, yAxis.posY, xAxis.sizeX, yAxis.sizeY);
            }
            xAxis.drawXGrid(source, canvas, yAxis);
            yAxis.drawYGrid(source, canvas, xAxis);

            xAxis.drawXAxis(source, canvas, yAxis);
            yAxis.drawYAxis(source, canvas, xAxis);
            canvas.setClip(ClipShape.RECTANGLE, xAxis.posX, yAxis.posY, xAxis.sizeX, yAxis.sizeY);

            for (final S s : series) {
                s.drawSeries(source, canvas, this);
            }
            canvas.clearClip();
        }


        public Axis getXAxis() {
            return xAxis;
        }

        public Axis getYAxis() {
            return yAxis;
        }

    }

    static class FacetPlot<S extends PlotSeries<S>> extends PlotImpl<S> {
        FacetPlot(final S series) {
            super(series);
        }

        @Override
        protected void layoutComponent(Figure<?, ?> source, double minX, double minY, double maxX, double maxY) {

        }

        @Override
        protected void drawComponent(Figure<?, ?> source, ChartCanvas<?> canvas) {

        }

    }

}