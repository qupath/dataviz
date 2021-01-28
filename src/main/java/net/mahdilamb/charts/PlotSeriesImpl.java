package net.mahdilamb.charts;

import net.mahdilamb.charts.plots.Plot;
import net.mahdilamb.charts.series.*;
import net.mahdilamb.charts.styles.MarkerMode;
import net.mahdilamb.charts.styles.MarkerShape;
import net.mahdilamb.charts.utils.StringUtils;
import net.mahdilamb.colormap.Color;
import net.mahdilamb.colormap.Colormap;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Implementations of series
 *
 * @param <P> the type of the plot
 * @param <S> the type of the series
 */
abstract class PlotSeriesImpl<P extends Plot<S>, S extends PlotSeries<S>> implements PlotSeries<S>, PlotWithColorBar<S>, PlotWithLegend<S> {
    private enum ColorMode {
        SINGLETON,
        ITERABLE,
        COLORMAP
    }

    boolean needsUpdating = true;

    /**
     * Series display
     */
    boolean showInLegend = true, showInColorBars = false;
    /**
     * Edge options
     */
    Color edgeColor = (Color) Color.BLACK;
    boolean showEdges = false;
    double edgeSize = 2;

    /**
     * Face options
     */
    ColorMode colorMode = ColorMode.SINGLETON;

    Color faceColor;
    List<Color> faceColors;
    Colormap colormap;
    double[] faceColorArray;
    double minScale, maxScale;

    protected abstract void layoutData(P plot);

    @Override
    @SuppressWarnings("unchecked")
    public S setEdgeSize(double size) {
        if (edgeSize != size) {
            needsUpdating = true;
        }
        edgeSize = size;
        return (S) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public S setEdgeColor(Color color) {
        if (!edgeColor.equals(color)) {
            needsUpdating = true;
        }
        edgeColor = color;
        if (!showEdges && color.alpha() != 0) {
            showEdges = true;
        }
        return (S) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public S showLegend(boolean showLegend) {
        if (showLegend != showInLegend) {
            needsUpdating = true;
        }
        showInLegend = showLegend;
        return (S) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public S showColorBar(boolean showColorBar) {
        if (showColorBar != showInColorBars) {
            needsUpdating = true;
        }
        showInColorBars = showColorBar;
        return (S) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public S setColor(Color color) {
        if (!color.equals(faceColor) || colorMode != ColorMode.SINGLETON) {
            needsUpdating = true;
        }
        colorMode = ColorMode.SINGLETON;
        faceColor = color;
        return (S) this;
    }

    List<Color> prepareColorList(int size) {
        needsUpdating = true;

        if (faceColors == null) {
            faceColors = new ArrayList<>(size);
        }
        faceColors.clear();
        if (size > 0) {
            ((ArrayList<Color>) faceColors).ensureCapacity(size);
        }
        colorMode = ColorMode.ITERABLE;
        return faceColors;
    }


    @Override
    @SuppressWarnings("unchecked")
    public S setColors(String... colorNames) {
        final List<Color> colors = prepareColorList(colorNames.length);
        for (final String string : colorNames) {
            colors.add((Color) Color.get(string));
        }
        return (S) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public S setColors(Iterable<String> colorNames) {
        final List<Color> colors = prepareColorList(-1);
        for (final String string : colorNames) {
            colors.add((Color) Color.get(string));
        }
        return (S) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public S setColors(Colormap colormap, Iterable<? extends Number> scalars) {
        needsUpdating = true;
        colorMode = ColorMode.COLORMAP;
        this.colormap = colormap;
        int size = 0;
        double min = Double.NaN, max = Double.NaN;
        for (final Number n : scalars) {
            if (n != null && (Double.isNaN(min) || n.doubleValue() < min)) {
                min = n.doubleValue();
            }
            if (n != null && (Double.isNaN(max) || n.doubleValue() > max)) {
                max = n.doubleValue();
            }
            ++size;
        }
        faceColorArray = new double[size];
        this.minScale = min;
        this.maxScale = max;
        int i = 0;
        for (final Number n : scalars) {
            faceColorArray[i++] = n == null ? Double.NaN : n.doubleValue();
        }
        return (S) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public S setColors(Colormap colormap, double... scalars) {
        needsUpdating = true;
        colorMode = ColorMode.COLORMAP;
        this.colormap = colormap;
        double min = Double.NaN, max = Double.NaN;
        for (final Number n : scalars) {
            if (n != null && (Double.isNaN(min) || n.doubleValue() < min)) {
                min = n.doubleValue();
            }
            if (n != null && (Double.isNaN(max) || n.doubleValue() > max)) {
                max = n.doubleValue();
            }
        }
        faceColorArray = scalars;
        this.minScale = min;
        this.maxScale = max;
        return (S) this;
    }

    static abstract class AbstractScatter extends PlotSeriesImpl<Layouts.RectangularPlot<Scatter>, Scatter> implements Scatter {
        double markerSize = 10;
        MarkerShape shape = MarkerShape.POINT;
        MarkerMode mode = MarkerMode.MARKER_ONLY;
        MarginalMode xMarginal = MarginalMode.NONE, yMarginal = MarginalMode.NONE;

        @Override
        public Scatter setXMarginal(MarginalMode marginal) {
            if (this.xMarginal != marginal){
                needsUpdating = true;
            }
            this.xMarginal = marginal;
            return this;
        }

        @Override
        public Scatter setYMarginal(MarginalMode marginal) {
            if (this.yMarginal != marginal){
                needsUpdating = true;
            }
            this.yMarginal = marginal;
            return this;
        }

        @Override
        public Scatter setMarkerMode(MarkerMode mode) {
            if (mode != this.mode) {
                needsUpdating = true;
            }
            this.mode = mode;
            return this;
        }

        static final class FromArray extends AbstractScatter {
            private final double[] x;
            private final double[] y;

            FromArray(double[] x, double[] y) {
                this.x = x;
                this.y = y;
            }


        }

        static final class FromIterable extends AbstractScatter {
            private final Iterable<? extends Number> x;
            private final Iterable<? extends Number> y;

            FromIterable(Iterable<? extends Number> x, Iterable<? extends Number> y) {
                this.x = x;
                this.y = y;
            }
        }

        @Override
        public String toString() {
            //TODO
            return "Scatter series {" +
                    "marker: " + StringUtils.snakeToTitleCase(shape.name()) +
                    ", markerSize: " + markerSize +
                    '}';
        }

        @Override
        public Scatter setMarkerSize(double size) {
            if (size != markerSize) {
                needsUpdating = true;
            }
            markerSize = size;
            return this;
        }

        @Override
        protected void layoutData(Layouts.RectangularPlot<Scatter> plot) {
//TODO
        }

        @Override
        public Scatter setMarker(MarkerShape marker) {
            if (marker != shape) {
                needsUpdating = true;
            }
            shape = Objects.requireNonNull(marker);
            return this;
        }
    }

    static abstract class BarImpl extends PlotSeriesImpl<Layouts.RectangularPlot<Bar>, Bar> {


        @Override
        protected void layoutData(Layouts.RectangularPlot<Bar> plot) {
            //TODO
        }
    }

    static abstract class ViolinImpl extends PlotSeriesImpl<Layouts.RectangularPlot<Violin>, Violin> {


        @Override
        protected void layoutData(Layouts.RectangularPlot<Violin> plot) {
            //TODO
        }
    }

    static abstract class RugImpl extends PlotSeriesImpl<Layouts.RectangularPlot<Rug>, Rug> {


        @Override
        protected void layoutData(Layouts.RectangularPlot<Rug> plot) {
            //TODO
        }
    }

    static abstract class AreaImpl extends PlotSeriesImpl<Layouts.RectangularPlot<Area>, Area> {


        @Override
        protected void layoutData(Layouts.RectangularPlot<Area> plot) {
            //TODO
        }
    }

    static abstract class BoxImpl extends PlotSeriesImpl<Layouts.RectangularPlot<BoxAndWhisker>, BoxAndWhisker> {


        @Override
        protected void layoutData(Layouts.RectangularPlot<BoxAndWhisker> plot) {
            //TODO
        }
    }

    static abstract class ContourImpl extends PlotSeriesImpl<Layouts.RectangularPlot<Contour>, Contour> {


        @Override
        protected void layoutData(Layouts.RectangularPlot<Contour> plot) {
            //TODO
        }
    }

    static abstract class DendrogramImpl extends PlotSeriesImpl<Layouts.RectangularPlot<Dendrogram>, Dendrogram> {


        @Override
        protected void layoutData(Layouts.RectangularPlot<Dendrogram> plot) {
            //TODO
        }
    }

    static abstract class DensityImpl extends PlotSeriesImpl<Layouts.RectangularPlot<Density>, Density> {


        @Override
        protected void layoutData(Layouts.RectangularPlot<Density> plot) {
            //TODO
        }
    }

    static abstract class KDEImpl extends PlotSeriesImpl<Layouts.RectangularPlot<KDE>, KDE> {


        @Override
        protected void layoutData(Layouts.RectangularPlot<KDE> plot) {
            //TODO
        }
    }

    static abstract class DotImpl extends PlotSeriesImpl<Layouts.RectangularPlot<Dot>, Dot> {


        @Override
        protected void layoutData(Layouts.RectangularPlot<Dot> plot) {
            //TODO
        }
    }

    static abstract class HeatmapImpl extends PlotSeriesImpl<Layouts.RectangularPlot<Heatmap>, Heatmap> {


        @Override
        protected void layoutData(Layouts.RectangularPlot<Heatmap> plot) {
            //TODO
        }
    }

    static abstract class Histogram2DImpl extends PlotSeriesImpl<Layouts.RectangularPlot<Histogram2D>, Histogram2D> {


        @Override
        protected void layoutData(Layouts.RectangularPlot<Histogram2D> plot) {
            //TODO
        }
    }

    static abstract class HistogramImpl extends PlotSeriesImpl<Layouts.RectangularPlot<Histogram>, Histogram> {


        @Override
        protected void layoutData(Layouts.RectangularPlot<Histogram> plot) {
            //TODO
        }
    }

    static abstract class LineImpl extends PlotSeriesImpl<Layouts.RectangularPlot<Line>, Line> {


        @Override
        protected void layoutData(Layouts.RectangularPlot<Line> plot) {
            //TODO
        }
    }

    static abstract class TableImpl extends PlotSeriesImpl<Layouts.RectangularPlot<TableColumn>, TableColumn> {


        @Override
        protected void layoutData(Layouts.RectangularPlot<TableColumn> plot) {
            //TODO
        }
    }

    static abstract class PolarImpl extends PlotSeriesImpl<Layouts.CircularPlot<Polar>, Polar> {


        @Override
        protected void layoutData(Layouts.CircularPlot<Polar> plot) {
            //TODO
        }
    }

    static abstract class PieImpl extends PlotSeriesImpl<Layouts.PiePlot<Pie>, Pie> {


        @Override
        protected void layoutData(Layouts.PiePlot<Pie> plot) {
            //TODO
        }
    }
}
