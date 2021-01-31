package net.mahdilamb.charts;

import net.mahdilamb.charts.graphics.*;
import net.mahdilamb.charts.layouts.PlotLayout;
import net.mahdilamb.charts.plots.*;
import net.mahdilamb.charts.utils.StringUtils;
import net.mahdilamb.colormap.Color;
import net.mahdilamb.colormap.Colormap;
import net.mahdilamb.colormap.reference.sequential.Viridis;

import java.util.*;
//TODO deal with grouping and colorscale clash - alpha by iterable

/**
 * Implementations of series
 *
 * @param <P> the type of the plot
 * @param <S> the type of the series
 */
abstract class PlotSeries<P extends PlotLayout<S>, S> implements PlotWithColorBar<S>, PlotWithLegend<S> {
    public static final Colormap DEFAULT_SEQUENTIAL_COLORMAP = new Viridis();


    private enum ColorMode {
        SINGLETON,
        ITERABLE,
        COLORMAP
    }

    protected boolean needsUpdating = true;

    /**
     * Series display
     */
    boolean showInLegend = true, showInColorBars = false;
    protected LegendImpl.LegendItem legendItem;
    protected ColorBarsImpl.ColorBarItem colorBarItem;
    String name;

    /**
     * Edge options
     */
    Color edgeColor = Color.BLACK;
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

    protected abstract LegendImpl.LegendItem getLegendItem();
    protected abstract ColorBarsImpl.ColorBarItem getColorBarItem();

    /**
     * Called before the series is added to a plot
     *
     * @return the series
     */
    protected abstract S prepare();

    @SuppressWarnings("unchecked")
    public S setName(String name) {
        this.name = name;
        return (S) this;
    }

    @SuppressWarnings("unchecked")
    public S setEdgeSize(double size) {
        if (edgeSize != size) {
            needsUpdating = true;
        }
        edgeSize = size;
        return (S) this;
    }

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


    @SuppressWarnings("unchecked")
    public S setColors(String... colorNames) {
        faceColorArray = null;
        final List<Color> colors = prepareColorList(colorNames.length);
        for (final String string : colorNames) {
            colors.add(Color.get(string));
        }
        return (S) this;
    }

    @SuppressWarnings("unchecked")
    public S setColors(Iterable<String> colorNames) {
        faceColorArray = null;
        final List<Color> colors = prepareColorList(-1);
        for (final String string : colorNames) {
            colors.add(Color.get(string));
        }
        return (S) this;
    }

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
        faceColors = null;
        faceColorArray = new double[size];
        this.minScale = min;
        this.maxScale = max;
        int i = 0;
        for (final Number n : scalars) {
            faceColorArray[i++] = n == null ? Double.NaN : n.doubleValue();
        }
        return (S) this;
    }

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
        faceColors = null;
        faceColorArray = scalars;
        this.minScale = min;
        this.maxScale = max;
        return (S) this;
    }

    static abstract class AbstractScatter extends PlotSeries<Layouts.RectangularPlot<Scatter>, Scatter> implements Scatter {
        MarkerMode mode = MarkerMode.MARKER_ONLY;
        MarginalMode xMarginal = MarginalMode.NONE, yMarginal = MarginalMode.NONE;
        Iterable<String> groups;
        Set<String> groupNames;
        LegendImpl.LegendItem legendItem;
        MarkerShape markerShape;
        double markerSize = 10;


        @Override
        public Scatter setGroups(Iterable<String> groupings) {
            this.groups = groupings;
            return this;
        }

        @Override
        public Scatter setXMarginal(MarginalMode marginal) {
            if (this.xMarginal != marginal) {
                needsUpdating = true;
            }
            this.xMarginal = marginal;
            return this;
        }

        @Override
        public Scatter setYMarginal(MarginalMode marginal) {
            if (this.yMarginal != marginal) {
                needsUpdating = true;
            }
            this.yMarginal = marginal;
            return this;
        }

        @Override
        protected ColorBarsImpl.ColorBarItem getColorBarItem() {
            return colorBarItem;
        }

        @Override
        protected Scatter prepare() {
            //Prepare legend items

            if (groups == null) {//todo also check if colors are iterable as these are also groups
                final MarkerImpl defaultMarker;
                if (colorMode == ColorMode.SINGLETON) {
                    defaultMarker = new MarkerImpl(markerShape, 10, faceColor, edgeSize, edgeColor);
                } else {
                    if (colorMode == ColorMode.COLORMAP) {
                        defaultMarker = new MarkerImpl(markerShape, 10, Color.BLACK, edgeSize, edgeColor);
                    } else {
                        //go with the chart default
                        defaultMarker = new MarkerImpl(markerShape, 10, null, edgeSize, edgeColor);
                    }
                }
                legendItem = new LegendImpl.LegendItem(name, defaultMarker);
            } else {
                groupNames = new LinkedHashSet<>();
                for (final String s : groups) {
                    groupNames.add(s);
                }

                final LegendImpl.LegendItem[] legendItems = new LegendImpl.LegendItem[groupNames.size()];
                int i = 0;
                for (final String g : groupNames) {
                    legendItems[i++] = new LegendImpl.LegendItem(g, new MarkerImpl(markerShape, 10, null, edgeSize, edgeColor));
                }
                legendItem = new LegendImpl.GroupedLegendItem(name, legendItems);
            }
            //prepare colorbar items
            if (colorMode == ColorMode.COLORMAP) {
                colorBarItem = new ColorBarsImpl.ColorBarItem(colormap, minScale, maxScale);
            }
            //prepare data items
            //TODO
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

        @Override
        protected LegendImpl.LegendItem getLegendItem() {
            return legendItem;
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
                this.x = Objects.requireNonNull(x);
                this.y = Objects.requireNonNull(y);
            }
        }

        @Override
        public String toString() {
            //TODO
            return "Scatter series {" +
                    "marker: " + StringUtils.snakeToTitleCase(markerShape.name()) +
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
            if (marker != this.markerShape) {
                needsUpdating = true;
            }
            this.markerShape = Objects.requireNonNull(marker);
            return this;
        }
    }

    static abstract class BarImpl extends PlotSeries<Layouts.RectangularPlot<Bar>, Bar> {


        @Override
        protected void layoutData(Layouts.RectangularPlot<Bar> plot) {
            //TODO
        }
    }

    static abstract class ViolinImpl extends PlotSeries<Layouts.RectangularPlot<Violin>, Violin> {


        @Override
        protected void layoutData(Layouts.RectangularPlot<Violin> plot) {
            //TODO
        }
    }

    static abstract class RugImpl extends PlotSeries<Layouts.RectangularPlot<Rug>, Rug> {


        @Override
        protected void layoutData(Layouts.RectangularPlot<Rug> plot) {
            //TODO
        }
    }

    static abstract class AreaImpl extends PlotSeries<Layouts.RectangularPlot<Area>, Area> {


        @Override
        protected void layoutData(Layouts.RectangularPlot<Area> plot) {
            //TODO
        }
    }

    static abstract class BoxImpl extends PlotSeries<Layouts.RectangularPlot<BoxAndWhisker>, BoxAndWhisker> {


        @Override
        protected void layoutData(Layouts.RectangularPlot<BoxAndWhisker> plot) {
            //TODO
        }
    }

    static abstract class ContourImpl extends PlotSeries<Layouts.RectangularPlot<Contour>, Contour> {


        @Override
        protected void layoutData(Layouts.RectangularPlot<Contour> plot) {
            //TODO
        }
    }

    static abstract class DendrogramImpl extends PlotSeries<Layouts.RectangularPlot<Dendrogram>, Dendrogram> {


        @Override
        protected void layoutData(Layouts.RectangularPlot<Dendrogram> plot) {
            //TODO
        }
    }

    static abstract class DensityImpl extends PlotSeries<Layouts.RectangularPlot<Density>, Density> {


        @Override
        protected void layoutData(Layouts.RectangularPlot<Density> plot) {
            //TODO
        }
    }

    static abstract class KDEImpl extends PlotSeries<Layouts.RectangularPlot<KDE>, KDE> {


        @Override
        protected void layoutData(Layouts.RectangularPlot<KDE> plot) {
            //TODO
        }
    }

    static abstract class DotImpl extends PlotSeries<Layouts.RectangularPlot<Dot>, Dot> {


        @Override
        protected void layoutData(Layouts.RectangularPlot<Dot> plot) {
            //TODO
        }
    }

    static abstract class HeatmapImpl extends PlotSeries<Layouts.RectangularPlot<Heatmap>, Heatmap> {


        @Override
        protected void layoutData(Layouts.RectangularPlot<Heatmap> plot) {
            //TODO
        }
    }

    static abstract class Histogram2DImpl extends PlotSeries<Layouts.RectangularPlot<Histogram2D>, Histogram2D> {


        @Override
        protected void layoutData(Layouts.RectangularPlot<Histogram2D> plot) {
            //TODO
        }
    }

    static abstract class HistogramImpl extends PlotSeries<Layouts.RectangularPlot<Histogram>, Histogram> {


        @Override
        protected void layoutData(Layouts.RectangularPlot<Histogram> plot) {
            //TODO
        }
    }

    static abstract class LineImpl extends PlotSeries<Layouts.RectangularPlot<Line>, Line> {


        @Override
        protected void layoutData(Layouts.RectangularPlot<Line> plot) {
            //TODO
        }
    }

    static abstract class TableImpl extends PlotSeries<Layouts.RectangularPlot<TableColumn>, TableColumn> {


        @Override
        protected void layoutData(Layouts.RectangularPlot<TableColumn> plot) {
            //TODO
        }
    }

    static abstract class PolarImpl extends PlotSeries<Layouts.CircularPlot<Polar>, Polar> {


        @Override
        protected void layoutData(Layouts.CircularPlot<Polar> plot) {
            //TODO
        }
    }

    static abstract class PieImpl extends PlotSeries<Layouts.PiePlot<Pie>, Pie> {


        @Override
        protected void layoutData(Layouts.PiePlot<Pie> plot) {
            //TODO
        }
    }

    static final class MarkerImpl implements Marker {
        Fill face;
        Stroke edge;
        double size, edgeWidth;
        MarkerShape markerShape;

        MarkerImpl(MarkerShape markerShape, double size, Color face, double edgeWidth, Color edge) {
            this.edge = new Stroke(edge, edgeWidth);
            this.edgeWidth = edgeWidth;
            this.markerShape = markerShape;
            this.face = new Fill(face);
            this.size = size;
        }


        @Override
        public Fill getFill() {
            return face;
        }

        @Override
        public Stroke getStroke() {
            return edge;
        }

        @Override
        public double getSize() {
            return size;
        }

        @Override
        public MarkerShape getShape() {
            return markerShape;
        }
    }
}
