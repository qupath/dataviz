package net.mahdilamb.dataviz.plots;

import net.mahdilamb.colormap.Color;
import net.mahdilamb.colormap.Colors;
import net.mahdilamb.dataframe.DataFrame;
import net.mahdilamb.dataframe.DataType;
import net.mahdilamb.dataframe.Series;
import net.mahdilamb.dataframe.utils.DoubleArrayList;
import net.mahdilamb.dataviz.PlotData;
import net.mahdilamb.dataviz.PlotLayout;
import net.mahdilamb.dataviz.PlotTrace;
import net.mahdilamb.dataviz.graphics.FillMode;
import net.mahdilamb.dataviz.graphics.shapes.MarkerShape;
import net.mahdilamb.dataviz.graphics.Stroke;
import net.mahdilamb.dataviz.utils.StringUtils;
import net.mahdilamb.dataviz.utils.VarArgsUtils;

import java.util.function.DoubleUnaryOperator;

import static net.mahdilamb.stats.ArrayUtils.map;

/**
 * Scatter series
 */
public final class Scatter extends PlotData.RelationalData<Scatter> {

    /**
     * Default marker size
     */
    public final static double DEFAULT_MARKER_SIZE = 10;
    /**
     * Default largest marker size (for numerical traces)
     */
    public final static double DEFAULT_MAX_MARKER_SIZE = 30;

    Color markerColor;
    protected PlotTrace markerSizes;
    PlotTrace.Categorical markerShapes;
    MarkerShape shape = MarkerShape.CIRCLE;
    protected double markerSize = DEFAULT_MARKER_SIZE;

    boolean showEdge = false;
    Color edgeColor;
    Stroke edgeStroke;
    private double edgeWidth = .5;
    /**
     * The default x label if the source is a raw array
     */
    public static final String defaultXLabel = "x";
    /**
     * The default y label if the source is a raw array
     */
    public static final String defaultYLabel = "y";

    DoubleArrayList errorXLower, errorXUpper, errorYLower, errorYUpper;

    /**
     * Create a scatter series from a data frame and two series
     *
     * @param dataFrame the data frame
     * @param x         the x values
     * @param y         the y values
     */
    public Scatter(DataFrame dataFrame, String x, String y) {
        super(dataFrame, x, y);
    }

    /**
     * Create a scatter series from two arrays of doubles
     *
     * @param x the x values
     * @param y the y values
     */
    public Scatter(double[] x, double[] y) {
        super(x, y);
        xLab = defaultXLabel;
        yLab = defaultYLabel;
    }

    /**
     * Create a named scatter series
     *
     * @param name the name of the series
     * @param x    the x values
     * @param y    the y values
     */
    public Scatter(final String name, double[] x, double[] y) {
        super(x, y);
        this.name = name;
    }

    /**
     * Create a scatter series from labeled points
     *
     * @param x the x labels
     * @param y the y labels
     */
    public Scatter(String[] x, double[] y) {
        super(x, y);
    }

    /**
     * Create a scatter series from y values
     *
     * @param y the y values
     */
    public Scatter(double[] y) {
        super(y);
    }

    /**
     * Create a scatter plot using an array and a unary operator.
     * This will change the mode to markers + lines
     *
     * @param x    the x values
     * @param func the function to apply
     */
    public Scatter(double[] x, DoubleUnaryOperator func) {
        super(x, map(x, func));
        setMarkerMode(ScatterMode.MARKER_AND_LINE);
    }

    public Scatter setColors(final String seriesName) throws DataFrameOnlyOperationException {
        opacity = 0.8;
        showEdge = true;

        final Series<?> s = getSeries(seriesName);
        if (s.getType() != DataType.DOUBLE) {
            setColorsQualitative(s.asString());
        } else {
            setColorsSequential(s.asDouble());
        }
        return this;

    }

    public Scatter setSizes(final String seriesName) throws DataFrameOnlyOperationException {
        return setSizes(seriesName, DEFAULT_MARKER_SIZE, DEFAULT_MAX_MARKER_SIZE);
    }

    public Scatter setSizes(final String seriesName, double minSize, double maxSize) throws DataFrameOnlyOperationException {
        final Series<?> series = getSeries(seriesName);
        if (DataType.isNumeric(series.getType())) {
            setMarkerSizes(series, minSize, maxSize);
        } else {
            throw new UnsupportedOperationException("Can only use numeric series");
        }
        return this;
    }

    public Scatter setShape(final String s) {
        this.shape = MarkerShape.get(s);
        return this;
    }

    public Scatter setSizes(double[] values) {
        markerSizes = addAttribute(new PlotTrace.Numeric(this, Attribute.SIZE, null, values));
        return this;
    }

    public Scatter setSizes(double first, double... rest) {
        return setSizes(VarArgsUtils.full(new double[size()], first, rest));
    }

    public Scatter setColors(final double[] values) {
        setColors(new PlotTrace.Numeric(this, Attribute.COLOR, null, values, 0, 1));
        return this;
    }

    public Scatter setColors(double first, double... rest) {
        return setColors(VarArgsUtils.full(new double[size()], first, rest));
    }

    public Scatter setMarkerMode(final ScatterMode mode) {
        this.markerMode = mode;
        clear();
        return this;
    }

    public Scatter setMarkerMode(final String mode) {
        return setMarkerMode(ScatterMode.from(mode));
    }

    public Scatter setSize(final double size) {
        removeAttribute(PlotData.Attribute.SIZE);
        markerSize = size;
        return this;
    }

    public Scatter setEdgeWidth(final double size) {
        edgeWidth = size;
        showEdge = Double.isFinite(size) && size > 0;
        edgeStroke = null;
        return this;
    }

    @Override
    protected Color getEdgeColor() {
        if (edgeColor == null && markerColor != null) {
            edgeColor = Colors.calculateLuminance(markerColor.red() * getBackgroundColor(layout).red(), markerColor.green() * getBackgroundColor(layout).green(), markerColor.blue() * getBackgroundColor(layout).blue()) > 0.1791 ? Color.BLACK : Color.WHITE;
        }
        if (edgeColor != null) {
            return edgeColor;
        }
        return super.getEdgeColor();
    }

    @Override
    protected Stroke getEdgeStroke() {
        if (edgeStroke == null) {
            edgeStroke = new Stroke(edgeWidth);
        }
        return edgeStroke;
    }

    public Scatter setColor(String s) {
        this.markerColor = StringUtils.convertToColor(s);
        return this;
    }

    @Override
    protected boolean isVisible(int index) {
        return super.isVisible(index) && PlotData.isVisible(markerSizes, index) && PlotData.isVisible(markerShapes, index);
    }

    private void setMarkerSizes(final Series<?> s) {
        markerSizes = addAttribute(new PlotTrace.Categorical(this, PlotData.Attribute.SIZE, s));
        addToHoverText(markerSizes, "%s=%{size:s}", () -> markerSizes.getName(), "size", ((PlotTrace.Categorical) markerSizes)::get);
    }

    private void setMarkerSizes(final Series<?> s, double minSize, double maxSize) {
        markerSizes = addAttribute(new PlotTrace.Numeric(this, PlotData.Attribute.SIZE, s, minSize, maxSize));
        addToHoverText(markerSizes, "%s=%{size:.1f}", () -> markerSizes.getName(), "size", i -> getRaw(((PlotTrace.Numeric) markerSizes), i));
    }

    private void setColorsQualitative(final Series<?> s) {
        setColors(new PlotTrace.Categorical(this, PlotData.Attribute.COLOR, s));
    }

    private void setColorsSequential(final Series<?> s) {
        setColors(new PlotTrace.Numeric(this, PlotData.Attribute.COLOR, s));
    }

    @Override
    protected Color getColor(int i) {
        if (colors != null || i == -1) {
            return super.getColor(i);
        }
        return getDefaultColor();

    }

    @Override
    protected Color getDefaultColor() {
        if (markerColor != null) {
            return markerColor;
        }
        return super.getDefaultColor();
    }

    @Override
    protected MarkerShape getShape(int i) {
        if (markerShapes != null) {
            return MarkerShape.get(getRaw(markerShapes, i));
        }
        return shape;
    }

    @Override
    protected double getSize(int i) {
        if (markerSizes == null || i == -1) {
            return markerSize;
        }
        return ((PlotTrace.Numeric) markerSizes).get(i);
    }

    @Override
    public Scatter setLineWidth(double width) {
        if (markerMode == ScatterMode.MARKER_ONLY) {
            markerMode = ScatterMode.MARKER_AND_LINE;
        }
        return super.setLineWidth(width);
    }

    public Scatter setLineStyle(double... dashes) {
        if (markerMode == ScatterMode.MARKER_ONLY) {
            markerMode = ScatterMode.MARKER_AND_LINE;
        }
        this.lineDashes = dashes;
        this.lineStroke = null;
        return this;
    }

    public Scatter setLineStyle(Stroke lineStyle) {
        if (markerMode == ScatterMode.MARKER_ONLY) {
            markerMode = ScatterMode.MARKER_AND_LINE;
        }
        this.lineDashes = lineStyle.getDashes();
        lineWidth = lineStyle.getWidth();
        this.lineStroke = null;
        return this;
    }

    public Scatter setLineStyle(String lineStyle) {
        return setLineStyle(StringUtils.convertToStroke(lineStyle).getDashes());
    }

    @Override
    public Scatter setLineColor(Color color) {
        if (markerMode == ScatterMode.MARKER_ONLY) {
            markerMode = ScatterMode.MARKER_AND_LINE;
        }
        return super.setLineColor(color);
    }

    public Scatter setShapes(final String seriesName) throws DataFrameOnlyOperationException {
        final Series<?> series = getSeries(seriesName);
        if (series.getType() == DataType.DOUBLE) {
            throw new UnsupportedOperationException("Cannot use a double series to set shapes");
        }
        clear();
        markerShapes = addAttribute(new PlotTrace.Categorical(this, PlotData.Attribute.SHAPE, series.asString()));
        addToHoverText(markerShapes, "%s=%{shape:s}", () -> markerShapes.getName(), "shape", ((PlotTrace.Categorical) markerShapes)::get);
        return this;
    }

    @Override
    protected boolean showEdge() {
        return showEdge;
    }

    /**
     * @param showEdge whether to show marker edges or not
     * @return this scatter series
     */
    public Scatter showEdge(boolean showEdge) {
        this.showEdge = showEdge;//TODO update
        return this;
    }

    @Override
    protected void init(PlotLayout plotLayout) {
        if (numXLabels(plotLayout.getXAxis()) != -1) {
            double yBuff = (yMax - yMin) * .01;
            updateXYBounds(plotLayout, xMin, xMax, yMin - yBuff, yMax + yBuff, true, true);
            double major = 1;
            double b = xMax * .05;
            plotLayout.getXAxis().setRange(-b, xMax + b);
            setTicks(plotLayout.getXAxis(), major);
        } else {
            if (markerMode != ScatterMode.MARKER_ONLY) {
                double yBuff = (yMax - yMin) * .01;
                updateXYBounds(plotLayout, xMin, xMax, yMin - yBuff, yMax + yBuff, false, false);
            } else {
                double xBuff = (xMax - xMin) * .01;
                double yBuff = (yMax - yMin) * .01;
                updateXYBounds(plotLayout, xMin - xBuff, xMax + xBuff, yMin - yBuff, yMax + yBuff, true, true);
            }

        }
        if (fillMode != FillMode.NONE) {
            putPolygons(plotLayout, this, createPolygons(plotLayout));
        }
        if (markerMode != ScatterMode.LINE_ONLY) {
            putMarkers(plotLayout, this, createMarkers(plotLayout));
        }
        if (markerMode != ScatterMode.MARKER_ONLY) {
            putLines(plotLayout, this, createLines(plotLayout));
        }
    }

}
