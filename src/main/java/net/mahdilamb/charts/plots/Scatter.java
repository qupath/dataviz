package net.mahdilamb.charts.plots;

import net.mahdilamb.charts.graphics.MarkerShape;
import net.mahdilamb.charts.graphics.Stroke;
import net.mahdilamb.charts.statistics.StatUtils;
import net.mahdilamb.colormap.Color;
import net.mahdilamb.colormap.Colormap;

import java.util.function.DoubleUnaryOperator;

import static net.mahdilamb.charts.utils.ArrayUtils.toArray;

@PlotType(name = "Scatter", compatibleSeries = {PlotType.DataType.NUMERIC, PlotType.DataType.NUMERIC})
public class Scatter extends AbstractScatter<Scatter>implements RectangularPlot {
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
    MarkerShape marker;
    double markerSize = DEFAULT_MARKER_SIZE;
    Stroke edge = Stroke.BLACK_STROKE;

    Colormap colormap = DEFAULT_QUALITATIVE_COLORMAP;

    /*
    Colormap fields
     */
    ColorScaleAttributes colorScale;
    double[] colors;

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

    public Scatter(Iterable<Double> x, Iterable<Double> y, int size) {
        super(toArray(x, size), toArray(y, size));
    }

    public Scatter setConstantErrorX(Iterable<Double> constantErrorX) {
        return setConstantErrorX(constantErrorX, constantErrorX);
    }

    public Scatter setConstantErrorX(Iterable<Double> constantLowerX, Iterable<Double> constantUpperX) {
        fillDoubles((p, it) -> p.errorXLower = p.getMidX() - it, constantLowerX);
        fillDoubles((p, it) -> p.errorXUpper = p.getMidX() + it, constantUpperX);
        return requestDataUpdate();
    }

    public Scatter setConstantErrorY(Iterable<Double> constantErrorY) {
        return setConstantErrorY(constantErrorY, constantErrorY);
    }

    public Scatter setConstantErrorY(Iterable<Double> constantLowerY, Iterable<Double> constantUpperY) {
        fillDoubles((p, it) -> p.errorYLower = p.getMidY() - it, constantLowerY);
        fillDoubles((p, it) -> p.errorYUpper = p.getMidY() + it, constantUpperY);
        return requestDataUpdate();
    }

    public Scatter setErrorX(Iterable<Double> errorX) {
        return setErrorX(errorX, errorX);
    }

    public Scatter setErrorX(Iterable<Double> lower, Iterable<Double> upper) {
        fillDoubles((p, x) -> p.errorXLower = x, lower);
        fillDoubles((p, x) -> p.errorXUpper = x, upper);
        return requestDataUpdate();
    }

    public Scatter setErrorY(Iterable<Double> lower, Iterable<Double> upper) {
        fillDoubles((p, it) -> p.errorYLower = it, lower);
        fillDoubles((p, it) -> p.errorYUpper = it, upper);
        return requestDataUpdate();
    }

    public Scatter setErrorY(Iterable<Double> errorY) {
        return setErrorY(errorY, errorY);
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
        fillDoubles((p, it) -> p.size = it, size);
        return requestDataUpdate();
    }

    public Scatter setMarkerSize(Iterable<Double> sizes, double minSize, double maxSize) {
        final double minSizes = StatUtils.min(sizes, Double::doubleValue);
        final double maxSizes = StatUtils.max(sizes, Double::doubleValue);
        final double range = maxSizes - minSizes;
        minSize = (!Double.isFinite(minSize)) ? DEFAULT_MARKER_SIZE : minSize;
        maxSize = (!Double.isFinite(maxSize)) ? DEFAULT_MAX_MARKER_SIZE : maxSize;
        final double sizeRange = maxSize - minSize;
        double finalMinSize = minSize;
        fillDoubles((p, it) -> p.size = (((it - minSizes) / range) * sizeRange) + finalMinSize, sizes);

        return requestDataUpdate();
    }

    public Scatter setMarker(char marker) {
        this.marker = MarkerShape.get(marker);
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
        fill((p, it) -> p.markerColor = it, colors);
        return requestLayout();
    }

    public Scatter setShapes(Iterable<MarkerShape> shape) {
        fill((p, it) -> p.markerShape = it, shape);
        return requestLayout();
    }

    public Scatter setColors(Colormap colormap, double[] colors) {
        return setColormap(colormap).setColors(colors);
    }

    public Scatter setColormap(final Colormap colormap) {
        if (colorScale == null) {
            colorScale = new ColorScaleAttributes();
        }
        colorScale.colormap = colormap;
        return requestLayout();
    }

    public Scatter setColors(double[] colors) {
        this.colors = colors;
        return requestLayout();
    }

    public Scatter showEdges(final boolean edgeVisible) {
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
