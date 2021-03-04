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
import net.mahdilamb.dataviz.graphics.MarkerShape;
import net.mahdilamb.dataviz.graphics.Stroke;
import net.mahdilamb.dataviz.utils.StringUtils;
import net.mahdilamb.dataviz.utils.VarArgsUtils;

import java.util.function.DoubleUnaryOperator;

import static net.mahdilamb.statistics.ArrayUtils.map;

/**
 * Scatter series
 */
public final class Scatter extends PlotData.XYData<Scatter> {

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
        xLab = "x";
        yLab = "y";
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

    public Scatter setColors(final String seriesName) {
        opacity = 0.8;
        showEdge = true;
        return PlotData.ifSeriesCategorical(
                dataFrame,
                seriesName,
                this::setColorsQualitative,
                this::setColorsSequential,
                () -> this);

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
        colors = addAttribute(new PlotTrace.Numeric(this, Attribute.COLOR, null, values, 0, 1));
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
        colors = addAttribute(new PlotTrace.Categorical(this, PlotData.Attribute.COLOR, s));
        addToHoverText(colors, "%s=%{color:s}", () -> colors.getName(), "color", ((PlotTrace.Categorical) colors)::get);

    }

    private void setColorsSequential(final Series<?> s) {
        colors = addAttribute(new PlotTrace.Numeric(this, PlotData.Attribute.COLOR, s));
        addToHoverText(colors, "%s=%{color:.1f}", () -> colors.getName(), "color", i -> getRaw(((PlotTrace.Numeric) colors), i));
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
        return setLineStyle(StringUtils.convertToStroke(lineStyle));
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
    public Scatter showEdges(boolean showEdge) {
        this.showEdge = showEdge;//TODO update
        return this;
    }

    @Override
    protected void init(PlotLayout plotLayout) {
        if (lineStroke == null) {
            lineStroke = new Stroke(lineWidth, lineDashes);
            lineDashes = null;
        }

        double xBuff = (xMax - xMin) * .01;
        double yBuff = (yMax - yMin) * .01;
        updateXYBounds(plotLayout, xMin - xBuff, xMax + xBuff, yMin - yBuff, yMax + yBuff);

        if (fillMode != FillMode.NONE) {
            putPolygons(plotLayout, this, createPolygons(plotLayout));
            /*for (final Node2D<Runnable> p : createPolygons().search(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY)) {
                PlotPolygon polygon = (PlotPolygon) p;
                int i = polygon.i();
                if (!(isVisible(i))) {
                    continue;
                }
                System.out.printf("%s color=%s stroke=%s%s%n",
                        polygon,
                        fillColor.toHex(),
                        lineStroke,
                        lineColor == null ? EMPTY_STRING : String.format(" strokeColor=%s", lineColor.toHex())
                );
            }*/
        }
        if (markerMode != ScatterMode.LINE_ONLY) {
            putMarkers(plotLayout, this, createMarkers(plotLayout));
            /*for (final Node2D<Runnable> m : createMarkers().search(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY)) {
                PlotMarker marker = (PlotMarker) m;
                int i = marker.i();
                if (!(isVisible(i))) {
                    continue;
                }
                System.out.printf("x=%s y=%s color=%s size=%s%s hover=%s%n", x.get(i), y.get(i), colorSetter.apply(i).toHex(), sizeSetter.applyAsDouble(i), showEdge ? " edge=" + (edgeWidth) : EMPTY_STRING, getHoverText(i));
            }*/
        }
        if (markerMode != ScatterMode.MARKER_ONLY) {
            putLines(plotLayout, this, createLines(plotLayout));
           /* for (final Node2D<Runnable> l : createLines().search(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY)) {
                PlotPolyLine line = (PlotPolyLine) l;
                System.out.printf("start=%s,%s end=%s,%s stroke=%s%s%s%s%n",
                        line.getStartX(),
                        line.getStartY(),
                        line.getEndX(),
                        line.getEndY(),
                        lineStroke,
                        lineColor == null ? EMPTY_STRING : String.format(" strokeColor=%s", lineColor),
                        colors == null ? EMPTY_STRING : (String.format(" color=%s (%s)", ((Trace.Categorical) colors).get(line.i()), colors.get(qualitativeColormap, line.i()).toHex())),
                        group == null ? EMPTY_STRING : (String.format(" group=%s", group.get(line.i())))
                );
            }*/
        }
    }

}
