package net.mahdilamb.charts.plots;

import net.mahdilamb.charts.graphics.Marker;
import net.mahdilamb.charts.graphics.MarkerShape;
import net.mahdilamb.charts.graphics.Stroke;
import net.mahdilamb.charts.statistics.StatUtils;
import net.mahdilamb.colormap.Color;
import net.mahdilamb.colormap.Colormap;

import java.util.function.DoubleUnaryOperator;

import static net.mahdilamb.charts.utils.ArrayUtils.fill;

@PlotType(name = "Scatter", compatibleSeries = {PlotType.DataType.NUMERIC, PlotType.DataType.NUMERIC})
public class Scatter extends AbstractScatter<Scatter> {
    public static double DEFAULT_MARKER_SIZE = 10;
    public static double DEFAULT_MAX_MARKER_SIZE = 20;

    /**
     * Enum for markers where joining lines are supported
     */
    public enum Mode {
        /**
         * Draw only markers
         */
        MARKER_ONLY,
        /**
         * Draw markers and lines
         */
        MARKER_AND_LINE,
        /**
         * Draw only lines
         */
        LINE_ONLY
    }

    boolean showEdges = true;

    /*
    Marker fields
     */
    Mode markerMode = Mode.MARKER_ONLY;
    Marker marker;
    Marker[] markers;
    double markerSize = DEFAULT_MARKER_SIZE;
    double[] markerSizes;
    Color[] markerColor;
    MarkerShape[] markerShapes;
    Stroke edge = Stroke.BLACK_STROKE;


    /*
    Colormap fields
     */
    Colormap colormap = DEFAULT_QUALITATIVE_COLORMAP;
    double[] colors;
    double minCol, maxCol;

    /*
    Errors
     */
    double[] errorsXUpper;
    double[] errorsYUpper;
    double[] errorsXLower;
    double[] errorsYLower;
    /*
    Marginal modes
     */
    MarginalMode marginalModeX = MarginalMode.NONE;
    MarginalMode marginalModeY = MarginalMode.NONE;


    public Scatter(double[] x, DoubleUnaryOperator toYFunction) {
        super(x, toYFunction);
    }

    public Scatter(String name, double[] x, double[] y) {
        super(name, x, y);
    }

    public Scatter(double[] x, double[] y) {
        this(null, x, y);
    }

    //todo do relative error and constant errors
    public Scatter setAbsoluteErrorX(Iterable<Double> errorX) {
        this.errorsXLower = fill(new double[x.length], errorX, Double.NaN);
        this.errorsXUpper = errorsXLower;
        return requestDataUpdate();
    }

    public Scatter setAbsoluteErrorX(Iterable<Double> lower, Iterable<Double> upper) {
        this.errorsXLower = fill(new double[x.length], lower, Double.NaN);
        this.errorsXUpper = fill(new double[x.length], upper, Double.NaN);
        return requestDataUpdate();
    }

    public Scatter setAbsoluteErrorY(Iterable<Double> lower, Iterable<Double> upper) {
        this.errorsYLower = fill(new double[y.length], lower, Double.NaN);
        this.errorsYUpper = fill(new double[y.length], upper, Double.NaN);
        return requestDataUpdate();
    }

    public Scatter setAbsoluteErrorY(Iterable<Double> errorY) {
        this.errorsYUpper = fill(new double[x.length], errorY, Double.NaN);
        this.errorsYLower = errorsYUpper;
        return requestDataUpdate();
    }

    public Scatter setMode(Mode markerMode) {
        this.markerMode = markerMode;
        return requestDataUpdate();
    }

    public Scatter setMarginalX(MarginalMode mode) {
        this.marginalModeX = mode;
        return requestDataUpdate();
    }

    public Scatter setMarginalY(MarginalMode mode) {
        this.marginalModeY = mode;
        return requestDataUpdate();
    }

    public Scatter setMarkerSize(Iterable<Double> size) {
        this.markerSizes = fill(new double[x.length], size, DEFAULT_MARKER_SIZE);
        return requestDataUpdate();
    }

    public Scatter setMarkerSize(Iterable<Double> sizes, double minSize, double maxSize) {
        final double minSizes = StatUtils.min(sizes, Double::doubleValue);
        final double maxSizes = StatUtils.max(sizes, Double::doubleValue);
        final double range = maxSizes - minSizes;
        minSize = (!Double.isFinite(minSize)) ? DEFAULT_MARKER_SIZE : minSize;
        maxSize = (!Double.isFinite(maxSize)) ? DEFAULT_MAX_MARKER_SIZE : maxSize;
        final double sizeRange = maxSize - minSize;
        int i = 0;
        this.markerSizes = new double[x.length];
        for (final double size : sizes) {
            markerSizes[i] = (((size - minSizes) / range) * sizeRange) + minSize;
        }
        return requestDataUpdate();
    }

    public Scatter setMarker(char marker) {
        markers = null;
        this.marker = new MarkerImpl(MarkerShape.get(marker), markerSize, null, edge);
        return requestLayout();
    }

    public Scatter setColor(String colorName) {
        Color c = Color.get(colorName);
        if (c == null) {
            throw new IllegalArgumentException("Color name is not valid");
        }
        return setColor(c);
    }

    public Scatter setColor(Color color) {
        this.color = color;
        return requestLayout();
    }

    public Scatter setColors(Iterable<Color> colors) {
        this.markerColor = new Color[x.length];
        int i = 0;
        for (Color d : colors) {
            this.markerColor[i++] = d;
        }
        return requestLayout();
    }

    public Scatter setShapes(Iterable<MarkerShape> shape) {
        this.markerShapes = fill(new MarkerShape[x.length], shape, marker.getShape());
        return requestLayout();
    }

    public Scatter setColors(Colormap colormap, double[] colors) {
        return setColormap(colormap).setColors(colors);
    }

    public Scatter setColormap(final Colormap colormap) {
        this.colormap = colormap;
        return requestLayout();
    }

    public Scatter setColors(double[] colors) {
        this.colors = colors;
        this.minCol = StatUtils.min(colors);
        this.maxCol = StatUtils.max(colors);
        return requestLayout();
    }

    public Scatter setEdgeVisible(final boolean edgeVisible) {
        showEdges = edgeVisible;
        return requestLayout();
    }

    @Override
    public Scatter setGroupName(int group, String name) {
        return super.setGroupName(group, name);
    }

    @Override
    public Scatter setGroupColor(int group, Color color) {
        return super.setGroupColor(group, color);
    }

    @Override
    public Scatter setGroupStroke(int group, Stroke color) {
        return super.setGroupStroke(group, color);
    }

    @Override
    public Scatter setGroupName(String group, String name) {
        return super.setGroupName(group, name);
    }

    @Override
    public Scatter setGroupColor(String group, Color color) {
        return super.setGroupColor(group, color);
    }

    @Override
    public Scatter setGroupStroke(String group, Stroke color) {
        return super.setGroupStroke(group, color);
    }

    @Override
    public Scatter setGroupLine(String group, Stroke line) {
        return super.setGroupLine(group, line);
    }

    @Override
    public Scatter setGroupLine(int group, Stroke line) {
        return super.setGroupLine(group, line);
    }

    public MarginalMode getMarginalX() {
        return marginalModeX;
    }

    public MarginalMode getMarginalY() {
        return marginalModeY;
    }

    @Override
    protected Scatter requestDataUpdate() {
        //todo - recalc lines, marginals, etc. - need to add booleans to find which things need updates
        return super.requestDataUpdate();
    }
}
