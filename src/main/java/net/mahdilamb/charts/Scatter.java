package net.mahdilamb.charts;

import net.mahdilamb.charts.graphics.FillMode;
import net.mahdilamb.charts.graphics.MarkerShape;
import net.mahdilamb.charts.graphics.Stroke;
import net.mahdilamb.charts.utils.StringUtils;
import net.mahdilamb.charts.utils.VarArgsUtils;
import net.mahdilamb.colormap.Color;
import net.mahdilamb.dataframe.DataFrame;
import net.mahdilamb.dataframe.DataType;
import net.mahdilamb.dataframe.Series;
import net.mahdilamb.dataframe.utils.DoubleArrayList;
import net.mahdilamb.dataframe.utils.StringParseException;

import java.util.function.DoubleUnaryOperator;

import static net.mahdilamb.dataframe.utils.StringUtils.compareToIgnoreCase;
import static net.mahdilamb.dataframe.utils.StringUtils.mismatchIgnoreCase;
import static net.mahdilamb.statistics.ArrayUtils.map;

public final class Scatter extends PlotData.XYData<Scatter> {


    /**
     * Enum for markers where joining lines are supported
     */
    public enum MarkerMode {
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
        LINE_ONLY;
        private static final String MARKER = "markers";
        private static final String LINE = "lines";

        /**
         * Get the enum from a string. Whitespace is ignored and this is case-insensitive.
         * Must be a combination of "markers" and "lines", optionally joined with a "+".
         *
         * @param mode the string to parse
         * @return the associated mode
         * @throws StringParseException if the string cannot be parsed
         */
        public static MarkerMode from(final String mode) throws StringParseException {
            boolean useMarker = false;
            boolean useLine = false;
            int i = 0;
            while (i < mode.length()) {
                final char c = mode.charAt(i);
                switch (c) {
                    case ' ':
                    case '+':
                        break;
                    case 'm':
                    case 'M':
                        if (!compareToIgnoreCase(MARKER, mode, i)) {
                            throw new StringParseException(mode, mismatchIgnoreCase(MARKER, mode, i));
                        }
                        useMarker = true;
                        i += MARKER.length();
                        continue;
                    case 'L':
                    case 'l':
                        if (!compareToIgnoreCase(LINE, mode, i)) {
                            throw new StringParseException(mode, mismatchIgnoreCase(LINE, mode, i));
                        }
                        useLine = true;
                        i += LINE.length();
                        continue;
                    default:
                        throw new StringParseException(mode, i + 1);
                }
                ++i;
            }
            return useLine && useMarker ? MARKER_AND_LINE : (useLine ? LINE_ONLY : MARKER_ONLY);
        }
    }

    public final static double DEFAULT_MARKER_SIZE = 10;
    public final static double DEFAULT_MAX_MARKER_SIZE = 30;

    MarkerMode markerMode = MarkerMode.MARKER_ONLY;
    Color markerColor;
    protected Trace markerSizes;
    Trace.Categorical markerShapes;
    MarkerShape shape = MarkerShape.CIRCLE;
    protected double markerSize = DEFAULT_MARKER_SIZE;

    boolean showEdge = false;
    Color edgeColor = Color.white;
    private double edgeWidth = .5;

    DoubleArrayList errorXLower, errorXUpper, errorYLower, errorYUpper;

    public Scatter(DataFrame dataFrame, String x, String y) {
        super(dataFrame, x, y);
    }

    public Scatter(double[] x, double[] y) {
        super(x, y);


    }

    public Scatter(final String name, double[] x, double[] y) {
        super(x, y);
        this.name = name;

    }


    public Scatter(String[] x, double[] y) {
        super(x, y);
    }

    /**
     * Create a scatter plot using an array and a unary operator. This will change the mode to markers + lines
     *
     * @param x    the x values
     * @param func the function to apply
     */
    public Scatter(double[] x, DoubleUnaryOperator func) {
        super(x, map(x, func));
        setMarkerMode(MarkerMode.MARKER_AND_LINE);
    }

    @Override
    protected boolean isVisible(int index) {
        return super.isVisible(index) && PlotData.isVisible(markerSizes, index) && PlotData.isVisible(markerShapes, index);
    }

    private void setColorsQualitative(final Series<?> s) {
        colors = addAttribute(PlotData.Attribute.COLOR, new Trace.Categorical(s));
        addToHoverText(colors, "%s=%{color:s}", () -> colors.name, "color", ((Trace.Categorical) colors)::get);

    }

    private void setColorsSequential(final Series<?> s) {
        colors = addAttribute(PlotData.Attribute.COLOR, new Trace.Numeric(s));
        addToHoverText(colors, "%s=%{color:.1f}", () -> colors.name, "color", ((Trace.Numeric) colors)::getRaw);
    }

    public Scatter setColors(final String seriesName) {
        return PlotData.ifSeriesCategorical(
                dataFrame,
                seriesName,
                this::setColorsQualitative,
                this::setColorsSequential,
                () -> this);

    }

    private void setMarkerSizes(final Series<?> s) {
        markerSizes = addAttribute(PlotData.Attribute.SIZE, new Trace.Categorical(s));
        markerSizes.series = s;
        addToHoverText(markerSizes, "%s=%{size:s}", () -> markerSizes.name, "size", ((Trace.Categorical) markerSizes)::get);
    }

    private void setMarkerSizes(final Series<?> s, double minSize, double maxSize) {
        final Trace.Numeric grp = addAttribute(PlotData.Attribute.SIZE, new Trace.Numeric(s));
        markerSizes = grp;
        grp.scaleMin = minSize;
        grp.scaleMax = maxSize;
        addToHoverText(markerSizes, "%s=%{size:.1f}", () -> markerSizes.name, "size", ((Trace.Numeric) markerSizes)::getRaw);
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

    public Scatter setSizes(double first, double... rest) {
        double[] values = VarArgsUtils.full(new double[size()], first, rest);
        final Trace.Numeric grp = addAttribute(PlotData.Attribute.SIZE, new Trace.Numeric(null, values));
        grp.showInLegend = false;
        grp.scaleMin = DEFAULT_MARKER_SIZE;
        grp.scaleMax = DEFAULT_MAX_MARKER_SIZE;
        markerSizes = grp;
        return this;
    }

    public Scatter setColors(final double[] values) {
        final Trace.Numeric grp = addAttribute(PlotData.Attribute.COLOR, new Trace.Numeric(null, values));
        grp.showInLegend = false;
        colors = grp;
        return this;
    }

    public Scatter setColors(double first, double... rest) {
        return setColors(VarArgsUtils.full(new double[size()], first, rest));
    }

    public Scatter setMarkerMode(final MarkerMode mode) {
        this.markerMode = mode;
        return this;
    }

    public Scatter setMarkerMode(final String mode) {
        return setMarkerMode(MarkerMode.from(mode));
    }

    public Scatter setSize(final double size) {
        removeAttribute(PlotData.Attribute.SIZE);
        markerSize = size;
        return this;
    }

    public Scatter setEdgeWidth(final double size) {
        edgeWidth = size;
        showEdge = Double.isFinite(size) && size > 0;
        return this;
    }

    public Scatter setColor(String s) {
        this.markerColor = StringUtils.convertToColor(s);
        return this;
    }

    @Override
    protected Color getColor(int i) {
        if (colors != null || i == -1) {
            return super.getColor(i);
        }
        return markerColor;
    }

    @Override
    protected MarkerShape getShape(int i) {
        if (markerShapes != null) {
            return MarkerShape.get(markerShapes.getRaw(i));
        }
        return shape;
    }

    @Override
    protected double getSize(int i) {
        if (markerSizes == null || i == -1) {
            return markerSize;
        }
        return ((Trace.Numeric) markerSizes).get(i);
    }

    @Override
    public Scatter setLineWidth(double width) {
        if (markerMode == MarkerMode.MARKER_ONLY) {
            markerMode = MarkerMode.MARKER_AND_LINE;
        }
        return super.setLineWidth(width);
    }

    public Scatter setLineStyle(double... dashes) {
        if (markerMode == MarkerMode.MARKER_ONLY) {
            markerMode = MarkerMode.MARKER_AND_LINE;
        }
        this.lineDashes = dashes;
        this.lineStroke = null;
        return this;
    }

    public Scatter setLineStyle(Stroke lineStyle) {
        if (markerMode == MarkerMode.MARKER_ONLY) {
            markerMode = MarkerMode.MARKER_AND_LINE;
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
        if (markerMode == MarkerMode.MARKER_ONLY) {
            markerMode = MarkerMode.MARKER_AND_LINE;
        }
        return super.setLineColor(color);
    }

    public Scatter setShapes(final String seriesName) throws DataFrameOnlyOperationException {
        final Series<?> series = getSeries(seriesName);
        if (series.getType() == DataType.DOUBLE) {
            throw new UnsupportedOperationException("Cannot use a double series to set shapes");
        }
        clear();
        markerShapes = addAttribute(PlotData.Attribute.SHAPE, new Trace.Categorical(series.asString()));
        addToHoverText(markerShapes, "%s=%{shape:s}", () -> markerShapes.name, "shape", ((Trace.Categorical) markerShapes)::get);
        return this;
    }

    @Override
    void init(PlotLayout plotLayout) {
        if (lineStroke == null) {
            lineStroke = new Stroke(lineWidth, lineDashes);
            lineDashes = null;
        }
        double xBuff = (xMax - xMin) * .025;
        double yBuff = (yMax - yMin) * .025;
        plotLayout.getXAxis().dataLower = Math.min(plotLayout.getXAxis().dataLower, xMin - xBuff);
        plotLayout.getYAxis().dataLower = Math.min(plotLayout.getYAxis().dataLower, yMin - yBuff);
        plotLayout.getXAxis().dataUpper = Math.max(plotLayout.getXAxis().dataUpper, xMax + xBuff);
        plotLayout.getYAxis().dataUpper = Math.max(plotLayout.getYAxis().dataUpper, yMax + yBuff);
        plotLayout.getXAxis().reset(true);
        plotLayout.getYAxis().reset(true);
        if (fillMode != FillMode.NONE) {
            plot.putPolygons(this, createPolygons(plotLayout));
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
        if (markerMode != MarkerMode.LINE_ONLY) {
            plotLayout.putMarkers(this, createMarkers(plotLayout));
            /*for (final Node2D<Runnable> m : createMarkers().search(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY)) {
                PlotMarker marker = (PlotMarker) m;
                int i = marker.i();
                if (!(isVisible(i))) {
                    continue;
                }
                System.out.printf("x=%s y=%s color=%s size=%s%s hover=%s%n", x.get(i), y.get(i), colorSetter.apply(i).toHex(), sizeSetter.applyAsDouble(i), showEdge ? " edge=" + (edgeWidth) : EMPTY_STRING, getHoverText(i));
            }*/
        }
        if (markerMode != MarkerMode.MARKER_ONLY) {
            plotLayout.putLines(this, createLines(plotLayout));
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
