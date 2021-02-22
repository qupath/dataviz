package net.mahdilamb.charts.plots;

import net.mahdilamb.charts.Figure;
import net.mahdilamb.charts.Plot;
import net.mahdilamb.charts.dataframe.Axis;
import net.mahdilamb.charts.dataframe.DataFrame;
import net.mahdilamb.charts.dataframe.DoubleSeries;
import net.mahdilamb.charts.dataframe.StringSeries;
import net.mahdilamb.charts.dataframe.utils.DoubleArrayList;
import net.mahdilamb.charts.dataframe.utils.GroupBy;
import net.mahdilamb.charts.graphics.ChartCanvas;
import net.mahdilamb.charts.graphics.MarkerShape;
import net.mahdilamb.charts.graphics.Stroke;
import net.mahdilamb.charts.graphics.shapes.Marker;
import net.mahdilamb.charts.utils.ArrayUtils;
import net.mahdilamb.charts.utils.StringUtils;
import net.mahdilamb.colormap.Color;
import net.mahdilamb.colormap.Colormap;
import net.mahdilamb.colormap.Colormaps;

import java.util.*;
import java.util.function.*;

import static net.mahdilamb.charts.Figure.DEFAULT_QUALITATIVE_COLORMAP;
import static net.mahdilamb.charts.utils.ArrayUtils.rescale;
import static net.mahdilamb.charts.utils.ArrayUtils.toArray;
import static net.mahdilamb.charts.utils.StringUtils.EMPTY_STRING;
import static net.mahdilamb.charts.utils.StringUtils.convertToColor;

@PlotType(name = "Scatter", compatibleSeries = {PlotType.DataType.NUMERIC, PlotType.DataType.NUMERIC})
public final class Scatter extends AbstractScatter<Scatter> implements RectangularPlot {
    public static double DEFAULT_MARKER_SIZE = 10;
    public static double DEFAULT_MAX_MARKER_SIZE = 30;

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
    Stroke edge = new Stroke(Color.white, 1);

    /*
    Marginal modes
     */
    MarginalMode marginalModeX = MarginalMode.NONE;
    MarginalMode marginalModeY = MarginalMode.NONE;

    Marker marker = new Marker();
    private List<ScatterPoint<Scatter>> points;
    List<LineElement> lines;


    /**
     * Create a scatter series from an array of doubles and a function which maps those values to y
     *
     * @param x           the x values
     * @param toYFunction the mapping function
     */
    public Scatter(double[] x, DoubleUnaryOperator toYFunction) {
        this(null, x, map(x, toYFunction));
    }

    /**
     * Create a scatter series from x and y points
     *
     * @param name the name of the series
     * @param x    the x values
     * @param y    the y values
     */
    public Scatter(String name, double[] x, double[] y) {
        super(name, x, y);
    }


    /**
     * Create a scatter series from x and y points
     *
     * @param x the x values
     * @param y the y values
     */
    public Scatter(double[] x, double[] y) {
        this(null, x, y);
    }

    /**
     * Create a scatter series from two iterables
     *
     * @param x    the x iterable
     * @param y    the y iterable
     * @param size the size of the two iterables
     */
    public Scatter(Iterable<Double> x, Iterable<Double> y, int size) {
        this(null, x, y, size);
    }

    public Scatter(final String name, Iterable<Double> x, Iterable<Double> y, int size) {
        this(name, toArray(x, size), toArray(y, size));
    }

    /**
     * Create a scatter series from a dataframe
     *
     * @param dataFrame the data frame
     * @param x         the name of the series for x values (will be cast to double)
     * @param y         the name of the series for y values (will be cast to double)
     */
    public Scatter(DataFrame dataFrame, final String x, final String y) {
        this(dataFrame.getName(), dataFrame.getDoubleSeries(x), dataFrame.getDoubleSeries(y), dataFrame.size(Axis.INDEX));
        this.data = dataFrame;
        this.xLabel = x;
        this.yLabel = y;
    }

    /**
     * Set the color for all scatter points. Clears any previously set colors
     *
     * @param colorName the name of the colors
     * @return this scatter series
     */
    public Scatter setColor(String colorName) {
        return setColor(convertToColor(colorName));
    }

    public Scatter setColor(Color color) {
        if (color == null) {
            throw new IllegalArgumentException("Color name is not valid");
        }
        this.color = color;
        removeAttribute(AttributeType.COLOR);
        return redraw();
    }

    /**
     * Set the color for each scatter point using the name of the color
     *
     * @param colors the name of the colors
     * @return this scatter series
     */
    public Scatter setColors(String... colors) {
        getPoints().forEach(ScatterPoint::clear);
        addAttribute(
                AttributeType.COLOR,
                TraceGroup.createForArrayList(
                        Arrays.asList(ArrayUtils.fill(new Color[size()], colors, StringUtils::convertToColor, null))
                )
        );
        return redraw();
    }

    /**
     * Set the color of each data point from a dataframe. If the series type is floating point, then it is mapped to a
     * sequential colormap, otherwise it will be a qualitative colormap.
     *
     * @param seriesName the name of the series
     * @return this scatter series
     * @throws DataFrameOnlyOperationException if this series was not constructed around a dataframe
     */
    public Scatter setColors(final String seriesName) throws DataFrameOnlyOperationException {
        getPoints().forEach(ScatterPoint::clear);

        return ifSeriesCategorical(
                seriesName,
                (s, series) -> {
                    addAttribute(AttributeType.COLOR, TraceGroup.createForQualitativeColor(series.getName(), new QualitativeColorAttribute(new GroupBy<>(series, series.size()))));
                },
                (s, series) -> {
                    //TODO
                    SequentialColorAttribute sequentialColormap = new SequentialColorAttribute(series.toArray(new double[series.size()]));
                    addAttribute(AttributeType.COLOR, TraceGroup.createForSequentialColormap(series.getName(), sequentialColormap));
                }
        );
    }

    public Scatter setColors(final String seriesName, double colorMin, double colorMax) throws DataFrameOnlyOperationException {
        if (data == null) {
            throw new DataFrameOnlyOperationException("This method should only be used if working with a dataframe");
        }
        getPoints().forEach(ScatterPoint::clear);

        final DoubleSeries series = data.getDoubleSeries(seriesName);
        final SequentialColorAttribute map = new SequentialColorAttribute(series.toArray(new double[series.size()]));
        final TraceGroup<?> attribute = addAttribute(AttributeType.COLOR, TraceGroup.createForSequentialColormap(series.getName(), map));

        //todo attribute.valueMin = colorMin;
        //todo attribute.valueMax = colorMax;
        return redraw();
    }

    public Scatter setColors(final String name, Colormap colormap, double[] colors) {
        addAttribute(AttributeType.COLOR, TraceGroup.createForSequentialColormap(name, new SequentialColorAttribute(colormap, colors)));
        return redraw();
    }

    public Scatter setColors(final String name, double[] colors) {
        addAttribute(AttributeType.COLOR, TraceGroup.createForSequentialColormap(name, new SequentialColorAttribute(colors)));
        return redraw();
    }

    public Scatter setColors(double[] colors) {
        return setColors(null, colors);
    }

    /**
     * Set the size of the markers. Clears any other previously set size
     *
     * @param size the size to set
     * @return this series
     */
    public Scatter setSize(final double size) {
        this.size = size;
        removeAttribute(AttributeType.SIZE);
        return requestDataUpdate();
    }

    /**
     * Set the size of each marker. If the length of the sizes are shorter than the number of points
     * then those points will have the size of the default marker (or as set in the {@link #setSize} method)
     *
     * @param size the sizes
     * @return this series
     */
    public Scatter setSizes(Iterable<Double> size) {
        addAttribute(AttributeType.SIZE,
                TraceGroup.createForDoubleArrayList(new DoubleArrayList(ArrayUtils.fill(new double[size()], size, Double.NaN)))
        );
        return redraw();
    }

    public Scatter setSizes(Iterable<Double> sizes, double minSize, double maxSize) {
        minSize = !Double.isFinite(minSize) ? DEFAULT_MARKER_SIZE : minSize;
        maxSize = !Double.isFinite(maxSize) ? DEFAULT_MAX_MARKER_SIZE : maxSize;
        addAttribute(AttributeType.SIZE,
                TraceGroup.createForDoubleArrayList(new DoubleArrayList(rescale(ArrayUtils.fill(new double[size()], sizes, DEFAULT_MARKER_SIZE), minSize, maxSize)))
        );
        return requestDataUpdate();
    }

    public Scatter setSizes(final String seriesName) throws DataFrameOnlyOperationException {
        return setSizes(seriesName, DEFAULT_MARKER_SIZE, DEFAULT_MAX_MARKER_SIZE);
    }

    public Scatter setSizes(String seriesName, double minSize, double maxSize) throws DataFrameOnlyOperationException {
        return ifSeriesCategorical(seriesName,
                (s, series) -> setSizeFromCategoricalSeries(series, minSize, maxSize),
                (s, series) -> setSizeFromScalar(series, minSize, maxSize)
        );

    }

    private void setSizeFromCategoricalSeries(final StringSeries series, double min, double max) {
        final GroupBy<String> factors = new GroupBy<>(series, series.size());
        final double[] values = new double[size()];
        final String[] names = new String[factors.numGroups()];
        final double[] groupVals = new double[factors.numGroups()];
        int h = 0;
        for (final GroupBy.Group<String> g : factors) {
            double j = ((double) g.getID()) / (factors.numGroups() - 1);
            for (int i : g) {
                values[i] = j;
            }
            groupVals[h] = j;
            names[h++] = g.get();
        }
        final TraceGroup<DimensionMap> traceGroup = TraceGroup.createForDimension(
                series.getName(),
                new DimensionMap(values),
                !Double.isFinite(min) ? DEFAULT_MARKER_SIZE : min,
                !Double.isFinite(max) ? DEFAULT_MAX_MARKER_SIZE : max,
                names,
                groupVals);
        addAttribute(AttributeType.SIZE, traceGroup);
    }


    private void setSizeFromScalar(final DoubleSeries series, double min, double max) {
        final DimensionMap map = new DimensionMap(series.toArray(new double[series.size()]));
        final TraceGroup<DimensionMap> traceGroup = TraceGroup.createForDimension(
                series.getName(),
                map,
                !Double.isFinite(min) ? DEFAULT_MARKER_SIZE : min,
                !Double.isFinite(max) ? DEFAULT_MAX_MARKER_SIZE : max
        );
        addAttribute(AttributeType.SIZE, traceGroup);
    }

    public Scatter setShape(char marker) {
        this.shape = MarkerShape.get(marker);
        removeAttribute(AttributeType.SHAPE);
        return redraw();
    }

    public Scatter setShapes(Iterable<MarkerShape> shape) {
        final MarkerShape[] shapes = ArrayUtils.fill(new MarkerShape[size()], shape, null);
        addAttribute(AttributeType.SHAPE,
                TraceGroup.createForArrayList(Arrays.asList(shapes)));
        return redraw();
    }

    public Scatter setShapes(final String seriesName) throws DataFrameOnlyOperationException {
        if (data == null) {
            throw new DataFrameOnlyOperationException("This method should only be used when using a dataframe.");
        }
        final StringSeries series = data.getStringSeries(seriesName);
        addAttribute(AttributeType.SHAPE, TraceGroup.createForGroupID(
                seriesName,
                new GroupBy<>(series, series.size()),
                MarkerShape::get
        ));
        return redraw();
    }


    public Scatter setShapes(final String seriesName, final Map<String, String> valToMarkerMap) throws DataFrameOnlyOperationException {
        if (data == null) {
            throw new DataFrameOnlyOperationException("This method should only be used when using a dataframe.");
        }
        final StringSeries series = data.getStringSeries(seriesName);
        final GroupBy<String> groups = new GroupBy<>(series, series.size());
        //TODO check
        addAttribute(
                AttributeType.SHAPE,
                TraceGroup.createForGroupVal(
                        seriesName,
                        groups,
                        grp -> MarkerShape.get(valToMarkerMap.get(grp)))
        );
        return redraw();
    }

    public Scatter setShapes(final String seriesName, final UnaryOperator<String> valToMarkerMap) throws DataFrameOnlyOperationException {
        if (data == null) {
            throw new DataFrameOnlyOperationException("This method should only be used when using a dataframe.");
        }
        final StringSeries series = data.getStringSeries(seriesName);
        final GroupBy<String> groups = new GroupBy<>(series, series.size());

        //TODO check
        addAttribute(
                AttributeType.SHAPE,
                TraceGroup.createForGroupVal(
                        seriesName,
                        groups,
                        grp -> MarkerShape.get(valToMarkerMap.apply(grp)))
        );
        return redraw();
    }


    public Scatter setColorMap(final Colormap colormap) {
        final TraceGroup<?> colors = getAttribute(AttributeType.COLOR);
        if (colors != null) {
            if (colors.source.getClass() == QualitativeColorAttribute.class) {
                ((QualitativeColorAttribute) colors.source).colormap = colormap;
            } else if (colors.source.getClass() == SequentialColorAttribute.class) {
                ((SequentialColorAttribute) colors.source).colormap = colormap;
            } else {
                throw new UnsupportedOperationException();
            }
            return redraw();
        }
        this.colormap = colormap;
        return redraw();
    }

    public Scatter setColorMap(final String colormapName) {
        final Colormap colormap = Colormaps.get(colormapName);
        if (colormap == null) {
            throw new IllegalArgumentException(String.format("Colormap '%s' could not be found", colormapName));
        }
        return setColorMap(colormap);

    }

    public Scatter setColorMap(final boolean qualitative, final String... colorNames) {
        return setColorMap(StringUtils.convertToColormap(qualitative, colorNames));
    }

    public Scatter setColorMap(final String... colorNames) {
        return setColorMap(true, colorNames);
    }


    /*

    public Scatter setRelativeErrorX(Iterable<Double> relativeErrorX) {
        return setRelativeErrorX(relativeErrorX, relativeErrorX);
    }

    public Scatter setRelativeErrorX(Iterable<Double> relativeLowerX, Iterable<Double> relativeUpperX) {
        fillDoubles((p, v) -> p.xUpper = p.getMidX() + v, relativeUpperX, Double.NaN);
        fillDoubles((p, v) -> p.xLower = p.getMidX() - v, relativeLowerX, Double.NaN);
        return requestDataUpdate();
    }

    public Scatter setRelativeErrorX(final double relativeLowerX, final double relativeUpperX) {
        fill(p -> p.xUpper = p.getMidX() + relativeUpperX);
        fill(p -> p.xLower = p.getMidX() - relativeLowerX);
        return requestDataUpdate();
    }

    public Scatter setRelativeErrorX(double relativeErrorX) {
        return setRelativeErrorX(relativeErrorX, relativeErrorX);
    }

    public Scatter setRelativeErrorY(Iterable<Double> relativeErrorY) {
        return setRelativeErrorY(relativeErrorY, relativeErrorY);
    }

    public Scatter setRelativeErrorY(final double relativeLowerY, final double relativeUpperY) {
        fill(p -> p.yUpper = p.getMidY() + relativeUpperY);
        fill(p -> p.yLower = p.getMidY() - relativeLowerY);
        return requestDataUpdate();
    }

    public Scatter setRelativeErrorY(double relativeErrorY) {
        return setRelativeErrorY(relativeErrorY, relativeErrorY);
    }

    public Scatter setRelativeErrorY(Iterable<Double> relativeLowerY, Iterable<Double> relativeUpperY) {
        fillDoubles((p, v) -> p.yUpper = p.getMidY() + v, relativeUpperY, Double.NaN);
        fillDoubles((p, v) -> p.yLower = p.getMidY() - v, relativeLowerY, Double.NaN);
        return requestDataUpdate();
    }

    public Scatter setErrorX(Iterable<Double> lower, Iterable<Double> upper) {
        fillDoubles((p, v) -> p.xUpper = v, upper, Double.NaN);
        fillDoubles((p, v) -> p.xLower = v, lower, Double.NaN);
        return requestDataUpdate();
    }

    public Scatter setErrorY(Iterable<Double> lower, Iterable<Double> upper) {
        fillDoubles((p, v) -> p.yUpper = v, upper, Double.NaN);
        fillDoubles((p, v) -> p.yLower = v, lower, Double.NaN);
        return requestDataUpdate();
    }
    */
    public Scatter showEdges(final boolean edgeVisible) {
        showEdges = edgeVisible;
        return redraw();
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

    @Override
    public MarginalMode getMarginalX() {
        return marginalModeX;
    }

    @Override
    public MarginalMode getMarginalY() {
        return marginalModeY;
    }

    private List<ScatterPoint<Scatter>> getPoints() {
        if (points == null) {
            points = new ArrayList<>(x.size());
            for (int i = 0; i < x.size(); ) {
                final ScatterPoint<Scatter> sp = new ScatterPoint<>(this, x.get(i), y.get(i), EMPTY_RUNNABLE);
                sp.i = i++;
                points.add(sp);
            }
        }
        return points;
    }

    @Override
    protected Scatter requestDataUpdate() {

        //todo - recalc lines, marginals, etc. - need to add booleans to find which things need updated
        return super.requestDataUpdate();
    }

    @Override
    public String toString() {
        final String original = super.toString();
        final StringBuilder out = new StringBuilder(original).deleteCharAt(original.length() - 1);
        final String padding = StringUtils.repeatCharacter(' ', original.indexOf('{'));
        for (final Map.Entry<AttributeType, TraceGroup<?>> tg : attributeEntries()) {
            final AttributeType name = tg.getKey();
            final TraceGroup<?> attr = tg.getValue();
            if (attr.next != null) {
                TraceGroup<?> f = attr;
                out.append(String.format(",\n%s ", padding)).append(f.type);
                FactorMap factors = null;

                do {
                    if (f.source instanceof FactorMap) {
                        factors = (FactorMap) f.source;
                    }
                    if (f != attr) {
                        out.append('+').append(f.type);
                    }
                    f = f.next;
                } while (f != null);

                if (factors != null) {
                    out.append(String.format("%s: %d class%s", formatName(attr.name), factors.groups.numGroups(), factors.groups.numGroups() == 1 ? EMPTY_STRING : "es"));
                } else {
                    out.append(String.format("%s", formatName(attr.name)));
                }
                continue;
            }
            switch (name) {
                case COLOR:
                    formatAttribute(out, "color", attr, padding);
                    break;
                case SIZE:
                    formatAttribute(out, "size", attr, padding);
                    break;
                case SHAPE:
                    formatAttribute(out, "shape", attr, padding);
                    break;
                default:
                    throw new UnsupportedOperationException(name.toString());
            }
        }

        return out.append('}').toString();
    }

    static void formatAttribute(final StringBuilder out, final String prefix, TraceGroup<?> attr, final String padding) {
        if (attr.source != null) {
            if (attr.source.getClass() == DimensionMap.class) {
                out.append(String.format(",\n%s %s%s: %.2f to %.2f", padding, prefix, formatName(attr.name), ((DimensionMap) attr.source).valMin, ((DimensionMap) attr.source).valMin + ((DimensionMap) attr.source).valRange));
            } else if (attr.source.getClass() == DoubleArrayList.class) {
                out.append(String.format(",\n%s %s%s", padding, prefix, formatName(attr.name)));
            } else if (attr.source.getClass() == QualitativeColorAttribute.class) {
                out.append(String.format(",\n%s %s%s: %d class%s%s", padding, prefix, formatName(attr.name), ((QualitativeColorAttribute) attr.source).groups.numGroups(), ((QualitativeColorAttribute) attr.source).groups.numGroups() == 1 ? EMPTY_STRING : "es", formatWord(((QualitativeColorAttribute) attr.source).colormap, s -> String.format(" (colormap: %s)", s.getClass().getSimpleName()))));
            } else if (attr.source instanceof FactorMap) {
                out.append(String.format(",\n%s %s%s: %d class%s", padding, prefix, formatName(attr.name), ((FactorMap) attr.source).groups.numGroups(), ((FactorMap) attr.source).groups.numGroups() == 1 ? EMPTY_STRING : "es"));
            } else {
                throw new UnsupportedOperationException(attr.source.getClass().getName());
            }
        } else {
            out.append(String.format(",\n%s %s%s", padding, prefix, formatName(attr.name)));
        }
    }

    @Override
    protected void drawSeries(Figure<?, ?> source, ChartCanvas<?> canvas, Plot<? extends Scatter> plot) {
        if (markerMode != Mode.MARKER_ONLY) {
            if (lineStyle == null) {
                lineStyle = new Stroke(DEFAULT_QUALITATIVE_COLORMAP.get(0), 2);
            }
            canvas.setStroke(lineStyle);
            for (final LineElement l : getLines()) {

                canvas.strokeLine(convertXToPosition(plot, l.startX), convertYToPosition(plot, l.startY), convertXToPosition(plot, l.endX), convertYToPosition(plot, l.endY));
            }
        }
        if(markerMode != Mode.LINE_ONLY){
            final TraceGroup<?> shapes = getAttribute(AttributeType.SHAPE);
            if (showEdges) {
                canvas.setStroke(edge);
            }
            @SuppressWarnings("unchecked") final FromGroupIdMap<MarkerShape> markers = (shapes != null && shapes.source.getClass() == FromGroupIdMap.class) ? ((FromGroupIdMap<MarkerShape>) shapes.source) : null;
            for (final ScatterPoint<Scatter> s : getPoints()) {
                marker.shape = markers == null ? shape : markers.get(s.i);
                canvas.setFill(s.getColor());
                marker.x = convertXToPosition(plot, s.getMidX());
                marker.y = convertYToPosition(plot, s.getMidY());
                marker.size = s.getSize();

                marker.fill(canvas);
                if (showEdges) {
                    marker.stroke(canvas);
                }

            }
        }

    }

    @Override
    public int size() {
        return x.size();
    }

    protected <U> void fill(BiConsumer<ScatterPoint<Scatter>, U> setter, Iterable<U> values) {
        final Iterator<ScatterPoint<Scatter>> iterator = getPoints().iterator();
        final Iterator<U> valueIterator = values.iterator();
        while (iterator.hasNext() && valueIterator.hasNext()) {
            setter.accept(iterator.next(), valueIterator.next());
        }
    }

    protected final <U> void fill(BiConsumer<ScatterPoint<Scatter>, U> setter, U[] values) {
        final Iterator<ScatterPoint<Scatter>> iterator = getPoints().iterator();
        int i = 0;
        while (iterator.hasNext() && i < values.length) {
            setter.accept(iterator.next(), values[i++]);
        }
    }

    protected final <U> void fill(BiConsumer<ScatterPoint<Scatter>, U> setter, U[] values, U defaultValue) {
        final Iterator<ScatterPoint<Scatter>> iterator = getPoints().iterator();
        int i = 0;
        while (iterator.hasNext()) {
            setter.accept(iterator.next(), i < values.length ? values[i++] : defaultValue);
        }
    }

    protected void fillDoubles(ObjDoubleConsumer<ScatterPoint<Scatter>> setter, Iterable<Double> values, double defaultValue) {
        final Iterator<ScatterPoint<Scatter>> iterator = getPoints().iterator();
        final Iterator<Double> valueIterator = values.iterator();
        while (iterator.hasNext()) {
            setter.accept(iterator.next(), valueIterator.hasNext() ? valueIterator.next() : defaultValue);
        }
    }

    protected void fillDoubles(ObjDoubleConsumer<ScatterPoint<Scatter>> setter, Iterable<Double> values) {
        final Iterator<ScatterPoint<Scatter>> iterator = getPoints().iterator();
        final Iterator<Double> valueIterator = values.iterator();
        while (iterator.hasNext() && valueIterator.hasNext()) {
            setter.accept(iterator.next(), valueIterator.next());
        }
    }

    protected void fill(Consumer<ScatterPoint<Scatter>> setter) {
        for (ScatterPoint<Scatter> point : getPoints()) {
            setter.accept(point);
        }
    }
}
