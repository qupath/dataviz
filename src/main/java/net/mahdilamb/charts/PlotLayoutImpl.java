package net.mahdilamb.charts;

import net.mahdilamb.charts.axes.LabeledAxis;
import net.mahdilamb.charts.graphics.ChartCanvas;
import net.mahdilamb.charts.graphics.ClipShape;
import net.mahdilamb.charts.graphics.Fill;
import net.mahdilamb.charts.graphics.Stroke;
import net.mahdilamb.charts.layouts.PlotLayout;
import net.mahdilamb.charts.layouts.XYPlot;
import net.mahdilamb.charts.utils.StringUtils;
import net.mahdilamb.colormap.Color;

import java.util.Objects;

//TODO make package protected
public abstract class PlotLayoutImpl<S> extends ChartComponent<Object,S> implements PlotLayout<S> {

    static void showBounds(ChartCanvas<?> canvas, ChartComponent<?,?> component) {
        canvas.strokeRect(component.boundsX, component.boundsY, component.boundsWidth, component.boundsHeight);

    }

    protected double x, y, width, height;

    final S[] series;
    Fill backgroundColor = new Fill(Color.lightgrey);
    Stroke border = new Stroke(Color.darkgray, 1.5);

    @Override
    protected void calculateBounds(ChartCanvas<?> canvas, Chart<?, ? extends S> source, double minX, double minY, double maxX, double maxY) {

    }

    @SafeVarargs
    protected PlotLayoutImpl(S... series) {
        this.series = series;
    }


    public static class Circular<S> extends PlotLayoutImpl<S> {
        Axis radialAxis;
        Axis angularAxis;

        public Axis getRadialAxis() {
            return radialAxis;
        }

        public Axis getAngularAxis() {
            return angularAxis;
        }


        @Override
        protected void layout(ChartCanvas<?> canvas, Chart<?, ? extends S> source, double minX, double minY, double maxX, double maxY) {

        }
    }

    /**
     * A plot containing multiple rectangular plots
     */
    public static class Grid<S> extends XYPlotImpl<S> {

        @SafeVarargs
        Grid(Axis xAxis, Axis yAxis, S... series) {
            super(xAxis, yAxis, series);
        }

        @Override
        protected void layout(ChartCanvas<?> canvas, Chart<?, ? extends S> source, double minX, double minY, double maxX, double maxY) {

        }
    }

    public static class Pie<S> extends PlotLayoutImpl<S> {
        LabeledAxis axis;

        public LabeledAxis getAxis() {
            return axis;
        }

        @Override
        protected void layout(ChartCanvas<?> canvas, Chart<?, ? extends S> source, double minX, double minY, double maxX, double maxY) {

        }
    }

    public static class Table<S> extends PlotLayoutImpl<S> {
        private Axis columnHeading, rowHeading;

        /**
         * @return the column heading
         */
        public Axis getColumnHeading() {
            return columnHeading;
        }

        /**
         * @return the row heading
         */
        public Axis getRowHeading() {
            return rowHeading;
        }

        @Override
        protected void layout(ChartCanvas<?> canvas, Chart<?, ? extends S> source, double minX, double minY, double maxX, double maxY) {

        }
    }

    /**
     * An XY plot layout
     */
    public static class XYPlotImpl<S> extends PlotLayoutImpl<S> implements XYPlot<S> {

        @Override
        public Axis getXAxis() {
            return xAxis;
        }

        @Override
        public Axis getYAxis() {
            return yAxis;
        }

        private final Axis xAxis, yAxis;

        @SafeVarargs
        XYPlotImpl(Axis xAxis, Axis yAxis, S... series) {
            super(series);
            this.xAxis = Objects.requireNonNull(xAxis);
            this.yAxis = Objects.requireNonNull(yAxis);

        }

        protected void calculateBounds(Chart<?, ?> chart, ChartCanvas<?> canvas, double minX, double minY, double maxX, double maxY) {
            //get height of xAxis
            final double availableHeight = maxY - minY;
            final double availableWidth = maxX - minX;
            double xAxisHeight = 0;
            if (xAxis.title.isVisible()) {
                double titleHeight = chart.getTextLineHeight(xAxis.title.font);
                xAxisHeight += titleHeight;
            }
            if (xAxis.showMajorTicks) {
                xAxisHeight += xAxis.majorTickLength;
            } else if (xAxis.showMinorTicks) {
                xAxisHeight += xAxis.minorTickLength;
            }
            if (xAxis.showLabels) {
                xAxisHeight += chart.getTextLineHeight(xAxis.labelFont) + xAxis.labelPadding;
            }
            //yAxis
            double yAxisWidth = 0;
            final double yTitleWidth;
            final String longerLabel = StringUtils.longerString(yAxis.getLabel(yAxis.lowerBound), yAxis.getLabel(yAxis.upperBound));
            if (xAxis.showLabels) {
                yAxisWidth += chart.getTextWidth(xAxis.labelFont, longerLabel);
            }
            if (yAxis.title.isVisible()) {
                yTitleWidth = chart.getTextLineHeight(xAxis.title.font);
            } else {
                yTitleWidth = 0;
            }
            yAxisWidth += yTitleWidth;
            if (yAxis.showMajorTicks) {
                yAxisWidth += yAxis.majorTickLength + yAxis.labelPadding;
            } else if (yAxis.showMinorTicks) {
                yAxisWidth += yAxis.minorTickLength + yAxis.labelPadding;
            }
            xAxis.boundsX = minX + yAxisWidth;
            xAxis.boundsY = minY + availableHeight - xAxisHeight;
            xAxis.boundsWidth = availableWidth - yAxisWidth;
            xAxis.boundsHeight = xAxisHeight;
            xAxis.type = Axis.AxisType.X;

            yAxis.boundsX = minX;
            yAxis.boundsY = minY;
            yAxis.boundsWidth = yAxisWidth;
            yAxis.boundsHeight = availableHeight - xAxisHeight;
            yAxis.type = Axis.AxisType.Y;

            this.boundsX = xAxis.boundsX;
            this.boundsY = yAxis.boundsY;
            this.boundsWidth = xAxis.boundsWidth;
            this.boundsHeight = yAxis.boundsHeight;
        }


        @Override
        @SuppressWarnings("unchecked")
        protected void layout(ChartCanvas<?> canvas, Chart<?, ? extends S> source, double minX, double minY, double maxX, double maxY) {
            canvas.setFill(backgroundColor);
            calculateBounds(source, canvas, minX, minY, maxX, maxY);
            canvas.fillRect(boundsX, boundsY, boundsWidth, boundsHeight);
            xAxis.layout(canvas, source, minX, minY, maxX, maxY);
            yAxis.layout(canvas, source, minX, minY, maxX, maxY);
            canvas.setClip(ClipShape.RECTANGLE, boundsX, boundsY, boundsWidth, boundsHeight);
            for (final S s : series) {
                ((PlotSeries<S>) s).layout(canvas, source, minX, minY, maxX, maxY);
            }
            canvas.clearClip();
            //draw border
            canvas.setStroke(border);
            canvas.strokeRect(boundsX, boundsY, boundsWidth, boundsHeight);
        }

    }

    /**
     * A scatter plot that contains an extra two regions to show distribution data
     */
    public static class XYMarginal<S> extends XYPlotImpl<S> {

        /**
         * @return the marginal associated with the x axis
         */
        public XYPlotImpl<S> getXMarginal() {
            return xMarginal;
        }

        /**
         * @return the marginal associated with the y axis
         */
        public XYPlotImpl<S> getYMarginal() {
            return yMarginal;
        }

        @Override
        public int numSeries() {
            return 1;
        }

        XYMarginal<S> xMarginal, yMarginal;

        @SafeVarargs
        public XYMarginal(Axis xAxis, Axis yAxis, S... series) {
            super(xAxis, yAxis, series);
        }

        @Override
        protected void layout(ChartCanvas<?> canvas, Chart<?, ? extends S> source, double minX, double minY, double maxX, double maxY) {

        }

    }

    @Override
    public S get(int series) {
        return this.series[series];
    }


    @Override
    public int numSeries() {
        return series.length;
    }


}
