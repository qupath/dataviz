package net.mahdilamb.charts.plots;

import net.mahdilamb.charts.LegendItem;
import net.mahdilamb.charts.axes.NumericAxis;
import net.mahdilamb.charts.series.DoubleSeries;
import net.mahdilamb.charts.series.LongSeries;
import net.mahdilamb.charts.series.SeriesType;
import net.mahdilamb.charts.styles.Marker;
import net.mahdilamb.geom2d.trees.PointNode;
import net.mahdilamb.geom2d.trees.RTree;

import java.util.Collections;

public class ScatterPlot<X extends NumericAxis, Y extends NumericAxis> extends AbstractRectangularPlot<X, Y> {
    private static final class ScatterPoint extends PointNode<Marker> {
        private static final Runnable EMPTY = () -> {
        };
        private final Runnable onSelect;

        public ScatterPoint(double x, double y, final Marker marker, Runnable onSelect) {
            super(x, y, marker);
            this.onSelect = onSelect;
        }

        public ScatterPoint(double x, double y, final Marker marker) {
            this(x, y, marker, EMPTY);
        }
    }

    private final RTree<Marker> tree = new RTree<>();
    private LegendItem legendItem;

    @PlotType(name = "Scatter plot", compatibleSeries = {SeriesType.DOUBLE, SeriesType.DOUBLE})
    public ScatterPlot(X xAxis, Y yAxis, DoubleSeries seriesX, DoubleSeries seriesY) {
        super(xAxis, yAxis);
        int n = Math.min(seriesX.size(), seriesY.size());
        for (int i = 0; i < n; ++i) {
            tree.put(new ScatterPoint(seriesX.get(i), seriesY.get(i), null));//TODO add marker
        }
    }

    public ScatterPlot(X xAxis, Y yAxis, double[] seriesX, double[] seriesY) {
        super(xAxis, yAxis);
        int n = Math.min(seriesX.length, seriesY.length);
        for (int i = 0; i < n; ++i) {
            tree.put(new ScatterPoint(seriesX[i], seriesY[i], null));//TODO add marker
        }
    }

    public ScatterPlot(X xAxis, Y yAxis, long[] seriesX, long[] seriesY) {
        super(xAxis, yAxis);
        int n = Math.min(seriesX.length, seriesY.length);
        for (int i = 0; i < n; ++i) {
            tree.put(new ScatterPoint(seriesX[i], seriesY[i], null));//TODO add marker
        }
    }

    @PlotType(name = "Scatter plot", compatibleSeries = {SeriesType.INTEGER, SeriesType.INTEGER})
    public ScatterPlot(X xAxis, Y yAxis, LongSeries seriesX, LongSeries seriesY) {
        super(xAxis, yAxis);
        int n = Math.min(seriesX.size(), seriesY.size());
        for (int i = 0; i < n; ++i) {
            tree.put(new ScatterPoint(seriesX.get(i), seriesY.get(i), null));//TODO add marker
        }
    }

    @Override
    protected Iterable<LegendItem> getLegendItems() {
        return Collections.singleton(legendItem);
    }

    @Override
    protected void layoutSeries(double x, double y, double width, double height) {

    }
}
