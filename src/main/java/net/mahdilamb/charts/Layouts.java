package net.mahdilamb.charts;

import net.mahdilamb.charts.axes.LabeledAxis;
import net.mahdilamb.charts.plots.*;
import net.mahdilamb.charts.series.PlotSeries;

import java.util.Objects;


final class Layouts {
    private Layouts() {

    }

    abstract static class PiePlot<S extends PlotSeries<S>> extends PlotImpl<S> implements Circular1DPlot<S> {
        LabeledAxis axis;

        @Override
        public LabeledAxis getAxis() {
            return axis;
        }

        @Override
        protected void layoutPlot(double x, double y, double width, double height) {
            //TODO
        }

        @Override
        protected Iterable<Legend.LegendItem> getLegendItems() {
            //TODO
            return null;
        }

        @Override
        protected void layoutAxes() {
        }
    }

    abstract static class CircularPlot<S extends PlotSeries<S>> extends PlotImpl<S> implements Circular2DPlot<S> {
        Axis radialAxis;
        Axis angularAxis;

        @Override
        public Axis getRadialAxis() {
            return radialAxis;
        }

        @Override
        public Axis getAngularAxis() {
            return angularAxis;
        }


        @Override
        protected void layoutPlot(double x, double y, double width, double height) {
            //TODO
        }

        @Override
        protected Iterable<Legend.LegendItem> getLegendItems() {
            //TODO
            return null;
        }

        @Override
        protected void layoutAxes() {

        }
    }

    /**
     * A plot containing multiple rectangular plots
     */
    abstract static class GridPlot<S extends PlotSeries<S>> extends PlotImpl<S> implements XYPlot<S> {
        private Axis xAxis;
        private Axis yAxis;

        @Override
        public final Axis getXAxis() {
            return xAxis;
        }

        @Override
        public final Axis getYAxis() {
            return yAxis;
        }


        @Override
        protected void layoutPlot(double x, double y, double width, double height) {
            //TODO
        }

        @Override
        protected Iterable<Legend.LegendItem> getLegendItems() {
            //TODO
            return null;
        }

        @Override
        protected void layoutAxes() {
            //TODO
        }
    }

    /**
     * A scatter plot that contains an extra two regions to show distribution data
     */
    static class RectangularPlot<S extends PlotSeries<S>> extends PlotImpl<S> implements XYMarginalPlot<S> {
        private final Axis xAxis, yAxis;
        PlotImpl<S> xMarginal, yMarginal;

        @SafeVarargs
        RectangularPlot(Axis xAxis, Axis yAxis, S... series) {
            super(series);
            this.xAxis = Objects.requireNonNull(xAxis);
            this.yAxis = Objects.requireNonNull(yAxis);
        }


        @Override
        protected void layoutPlot(double x, double y, double width, double height) {
            //TODO
        }

        @Override
        protected Iterable<Legend.LegendItem> getLegendItems() {
            //TODO
            return null;
        }

        @Override
        protected void layoutAxes() {

            //todo layout marginals

        }


        @Override
        public Plot<?> getXMarginal() {
            return xMarginal;
        }

        @Override
        public Plot<?> getYMarginal() {
            return yMarginal;
        }


        @Override
        public Axis getXAxis() {
            return xAxis;
        }

        @Override
        public Axis getYAxis() {
            return yAxis;
        }
    }
}
