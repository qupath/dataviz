package net.mahdilamb.charts.plots;

import net.mahdilamb.charts.dataframe.Axis;
import net.mahdilamb.charts.dataframe.DataFrame;
import net.mahdilamb.charts.dataframe.DoubleSeries;
import net.mahdilamb.charts.dataframe.StringSeries;
import net.mahdilamb.charts.dataframe.utils.GroupBy;
import net.mahdilamb.charts.graphics.MarkerShape;
import net.mahdilamb.charts.graphics.Stroke;
import net.mahdilamb.charts.utils.ArrayUtils;
import net.mahdilamb.charts.utils.StringUtils;
import net.mahdilamb.colormap.Color;
import net.mahdilamb.colormap.Colormap;
import net.mahdilamb.colormap.Colormaps;
import net.mahdilamb.geom2d.trees.PointNode;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.DoubleUnaryOperator;
import java.util.function.UnaryOperator;

import static net.mahdilamb.charts.utils.ArrayUtils.toArray;
import static net.mahdilamb.charts.utils.StringUtils.EMPTY_STRING;
import static net.mahdilamb.charts.utils.StringUtils.convertToColor;

@PlotType(name = "Scatter", compatibleSeries = {PlotType.DataType.NUMERIC, PlotType.DataType.NUMERIC})
public final class Scatter extends AbstractScatter<Scatter> implements RectangularPlot {
    public static double DEFAULT_MARKER_SIZE = 10;
    public static double DEFAULT_MAX_MARKER_SIZE = 40;

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

    /**
     * Create a scatter series from an array of doubles and a function which maps those values to y
     *
     * @param x           the x values
     * @param toYFunction the mapping function
     */
    public Scatter(double[] x, DoubleUnaryOperator toYFunction) {
        super(x, toYFunction);
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
        super(toArray(x, size), toArray(y, size));
    }

    /**
     * Create a scatter series from a dataframe
     *
     * @param dataFrame the data frame
     * @param x         the name of the series for x values (will be cast to double)
     * @param y         the name of the series for y values (will be cast to double)
     */
    public Scatter(DataFrame dataFrame, final String x, final String y) {
        this(dataFrame.getDoubleSeries(x), dataFrame.getDoubleSeries(y), dataFrame.size(Axis.INDEX));
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
        addAttribute(AttributeType.COLOR, new DataAttribute<>(null, ArrayUtils.fill(new Color[points.size()], colors, Color::get, color)));
        return redraw();
    }

    /**
     * Set the color of each data point from a dataframe. If the series type is floating point, then it is mapped to a
     * sequential colormap, otherwise it will be a qualitative colormap.
     *
     * @param seriesName the name of the series
     * @return this scatter series
     * @throws UnsupportedOperationException if the dataframe is null
     */
    public Scatter setColors(final String seriesName) throws UnsupportedOperationException {
        return ifSeriesCategorical(
                seriesName,
                (s, series) -> addAttribute(AttributeType.COLOR, new QualitativeColorAttribute(series.getName(), new GroupBy<>(series, series.size()))),
                (s, series) -> addAttribute(AttributeType.COLOR, new SequentialColorAttribute(series.getName(), series.toArray(new double[series.size()])))
        );
    }

    public Scatter setColors(final String seriesName, double colorMin, double colorMax) {
        if (data == null) {
            throw new UnsupportedOperationException("This method should only be used if working with a dataframe");
        }
        final DoubleSeries series = data.getDoubleSeries(seriesName);
        final SequentialColorAttribute attribute = addAttribute(AttributeType.COLOR, new SequentialColorAttribute(series.getName(), series.toArray(new double[series.size()])));
        attribute.valueMin = colorMin;
        attribute.valueMax = colorMax;
        return redraw();
    }

    public Scatter setColors(final String name, Colormap colormap, double[] colors) {
        addAttribute(AttributeType.COLOR, new SequentialColorAttribute(name, colors))
                .colormap = colormap;
        return redraw();
    }

    public Scatter setColors(final String name, double[] colors) {
        addAttribute(AttributeType.COLOR, new SequentialColorAttribute(name, colors));
        return redraw();
    }

    public Scatter setColors(double[] colors) {
        return setColors(null, colors);
    }

    public Scatter setColorMap(final Colormap colormap) {
        final Attribute<?> colors = getAttribute(AttributeType.COLOR);
        if (colors != null) {
            if (colors.getClass() == QualitativeColorAttribute.class) {
                ((QualitativeColorAttribute) colors).colormap = colormap;
            } else if (colors.getClass() == SequentialColorAttribute.class) {
                ((SequentialColorAttribute) colors).colormap = colormap;
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

    public Scatter setSize(final double size) {
        this.size = size;
        removeAttribute(AttributeType.SIZE);
        return requestDataUpdate();
    }

    public Scatter setSizes(final String seriesName) {
        return ifSeriesCategorical(seriesName,
                (s, series) -> addAttribute(AttributeType.SIZE, new FactorizedDataAttribute<>(series.getName(), new GroupBy<>(series, series.size()))),
                (s, series) -> setSizes(series.getName(), series)
        );
    }

    public Scatter setSizes(final String sizeName, Iterable<Double> size) {
        return setSizes(sizeName, size, DEFAULT_MARKER_SIZE, DEFAULT_MAX_MARKER_SIZE);
    }

    public Scatter setSizes(final String sizeName, Iterable<Double> sizes, double minSize, double maxSize) {
        final ScaledDoubleDataAttribute sizeAttribute = addAttribute(AttributeType.SIZE, new ScaledDoubleDataAttribute(sizeName, ArrayUtils.fill(new double[points.size()], sizes, Double.NaN)));
        sizeAttribute.scaleMin = (!Double.isFinite(minSize)) ? DEFAULT_MARKER_SIZE : minSize;
        sizeAttribute.scaleNaN = sizeAttribute.scaleMin;
        sizeAttribute.scaleMax = (!Double.isFinite(maxSize)) ? DEFAULT_MAX_MARKER_SIZE : maxSize;
        return requestDataUpdate();
    }

    public Scatter setSizes(String seriesName, double minSize, double maxSize) {
        return ifSeriesCategorical(seriesName,
                (s, series) -> {
                    double range = maxSize - minSize;
                    final GroupBy<String> factors = new GroupBy<>(series, series.size());
                    final FactorizedDataAttribute<Double> sizes = addAttribute(AttributeType.SIZE, new FactorizedDataAttribute<>(seriesName, factors));
                    sizes.mapFactor(i -> (((double) i * range) / factors.numGroups()) + minSize, Double.class);
                },
                (s, series) -> setSizes(series.getName(), series, minSize, maxSize)
        );

    }

    public Scatter setShape(char marker) {
        this.shape = MarkerShape.get(marker);
        removeAttribute(AttributeType.SHAPE);
        return redraw();
    }

    public Scatter setShapes(final String name, Iterable<MarkerShape> shape) {
        addAttribute(AttributeType.SHAPE, new FactorizedDataAttribute<>(name, null, null))
                .data = ArrayUtils.fill(new MarkerShape[points.size()], shape, this.shape);
        return redraw();
    }

    public Scatter setShapes(final String seriesName) {
        if (data == null) {
            throw new UnsupportedOperationException("This method should only be used when using a dataframe.");
        }
        final StringSeries series = data.getStringSeries(seriesName);
        addAttribute(AttributeType.SHAPE, new FactorizedDataAttribute<MarkerShape>(seriesName, new GroupBy<>(series, series.size())))
                .mapFactor(MarkerShape::get, MarkerShape.class);
        return redraw();
    }

    public Scatter setShapes(final String seriesName, final Map<String, String> valToMarkerMap) {
        if (data == null) {
            throw new UnsupportedOperationException("This method should only be used when using a dataframe.");
        }
        final StringSeries series = data.getStringSeries(seriesName);
        final GroupBy<String> groups = new GroupBy<>(series, series.size());

        final MarkerShape[] shapes = new MarkerShape[series.size()];
        for (final GroupBy.Group<String> group : groups) {
            final MarkerShape marker = MarkerShape.get(valToMarkerMap.get(group.get()));
            for (final int i : group) {
                shapes[i] = marker;
            }
        }
        addAttribute(AttributeType.SHAPE, new FactorizedDataAttribute<>(seriesName, groups))
                .data = shapes;
        return redraw();
    }

    public Scatter setShapes(final String seriesName, final UnaryOperator<String> valToMarkerMap) {
        if (data == null) {
            throw new UnsupportedOperationException("This method should only be used when using a dataframe.");
        }
        final StringSeries series = data.getStringSeries(seriesName);
        final GroupBy<String> groups = new GroupBy<>(series, series.size());

        final MarkerShape[] shapes = new MarkerShape[series.size()];
        for (final GroupBy.Group<String> group : groups) {
            final MarkerShape marker = MarkerShape.get(valToMarkerMap.apply(group.get()));
            for (final int i : group) {
                shapes[i] = marker;
            }
        }
        addAttribute(AttributeType.SHAPE, new FactorizedDataAttribute<>(seriesName, groups))
                .data = shapes;
        return redraw();
    }

    public Scatter setRelativeErrorX(final String name, Iterable<Double> relativeErrorX) {
        return setRelativeErrorX(name, relativeErrorX, relativeErrorX);
    }

    public Scatter setRelativeErrorX(final String name, Iterable<Double> relativeLowerX, Iterable<Double> relativeUpperX) {
        addAttribute(AttributeType.ERROR_X,
                new PairedDoubleDataAttribute(
                        name,
                        ArrayUtils.fill(new double[points.size()], points, PointNode::getMidX, relativeLowerX, (i, j) -> i - j, Double.NaN),
                        ArrayUtils.fill(new double[points.size()], points, PointNode::getMidX, relativeUpperX, Double::sum, Double.NaN)
                )
        );
        return requestDataUpdate();
    }

    public Scatter setRelativeErrorX(final String name, final double relativeLowerX, final double relativeUpperX) {
        addAttribute(AttributeType.ERROR_X,
                new PairedDoubleDataAttribute(
                        name,
                        ArrayUtils.fill(new double[points.size()], i -> points.get(i).getMidX() - relativeLowerX),
                        ArrayUtils.fill(new double[points.size()], i -> points.get(i).getMidX() + relativeUpperX)
                )
        );
        return requestDataUpdate();
    }

    public Scatter setRelativeErrorX(final String name, double relativeErrorX) {
        return setRelativeErrorX(name, relativeErrorX, relativeErrorX);
    }

    public Scatter setRelativeErrorY(final String name, Iterable<Double> relativeErrorY) {
        return setRelativeErrorY(name, relativeErrorY, relativeErrorY);
    }

    public Scatter setRelativeErrorY(final String name, final double relativeLowerY, final double relativeUpperY) {
        addAttribute(AttributeType.ERROR_Y,
                new PairedDoubleDataAttribute(
                        name,
                        ArrayUtils.fill(new double[points.size()], i -> points.get(i).getMidY() - relativeLowerY),
                        ArrayUtils.fill(new double[points.size()], i -> points.get(i).getMidY() + relativeUpperY)
                )
        );
        return requestDataUpdate();
    }

    public Scatter setRelativeErrorY(final String name, double relativeErrorY) {
        return setRelativeErrorY(name, relativeErrorY, relativeErrorY);
    }

    public Scatter setRelativeErrorY(final String name, Iterable<Double> relativeLowerY, Iterable<Double> relativeUpperY) {
        addAttribute(AttributeType.ERROR_Y,
                new PairedDoubleDataAttribute(
                        name,
                        ArrayUtils.fill(new double[points.size()], points, PointNode::getMidY, relativeLowerY, (i, j) -> i - j, Double.NaN),
                        ArrayUtils.fill(new double[points.size()], points, PointNode::getMidY, relativeUpperY, Double::sum, Double.NaN)
                )
        );
        return requestDataUpdate();
    }

    public Scatter setErrorX(final String name, Iterable<Double> lower, Iterable<Double> upper) {
        addAttribute(AttributeType.ERROR_X,
                new PairedDoubleDataAttribute(
                        name,
                        ArrayUtils.fill(new double[points.size()], lower, Double.NaN),
                        ArrayUtils.fill(new double[points.size()], upper, Double.NaN)
                )
        );
        return requestDataUpdate();
    }

    public Scatter setErrorY(final String name, Iterable<Double> lower, Iterable<Double> upper) {
        addAttribute(AttributeType.ERROR_Y,
                new PairedDoubleDataAttribute(
                        name,
                        ArrayUtils.fill(new double[points.size()], lower, Double.NaN),
                        ArrayUtils.fill(new double[points.size()], upper, Double.NaN)
                )
        );
        return requestDataUpdate();
    }

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

    public MarginalMode getMarginalX() {
        return marginalModeX;
    }

    public MarginalMode getMarginalY() {
        return marginalModeY;
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
        final Set<AttributeType> attributes = new LinkedHashSet<>();
        for (final Map.Entry<AttributeType, Attribute<?>> attribute : attributeEntries()) {
            attributes.add(attribute.getKey());
        }
        for (final AttributeType name : attributes) {
            final Attribute<?> attr = getAttribute(name);
            FactorizedDataAttribute<?> factors = null;
            if (attr.next != null) {
                Attribute<?> f = attr;
                out.append(String.format(",\n%s ", padding)).append(f.getType());
                do {
                    if (f instanceof FactorizedDataAttribute) {
                        factors = (FactorizedDataAttribute<?>) f;
                    }
                    if (f != attr) {
                        out.append('+').append(f.getType());
                    }
                    f = f.next;
                } while (f != null);

                if (factors != null) {
                    out.append(String.format("%s: %d class%s", formatName(attr.name), factors.factors.length, factors.factors.length == 1 ? EMPTY_STRING : "es"));
                } else {
                    out.append(String.format("%s", formatName(attr.name)));
                }
                continue;
            }

            switch (name) {
                case COLOR:
                    if (attr.getClass() == QualitativeColorAttribute.class) {
                        out.append(String.format(",\n%s colors%s: %d class%s%s", padding, formatName(attr.name), ((QualitativeColorAttribute) attr).factors.length, ((QualitativeColorAttribute) attr).factors.length == 1 ? EMPTY_STRING : "es", formatWord(((QualitativeColorAttribute) attr).colormap, s -> String.format(" (colormap: %s)", s.getClass().getSimpleName()))));
                    } else if (attr.getClass() == SequentialColorAttribute.class) {
                        out.append(String.format(",\n%s colors%s: %.2f to %.2f", padding, formatName(attr.name), ((SequentialColorAttribute) attr).valueMin, ((SequentialColorAttribute) attr).valueMax));
                    } else {
                        out.append(String.format(",\n%s colors%s", padding, formatName(attr.name)));
                    }
                    break;
                case SIZE:
                    if (attr.getClass() == ScaledDoubleDataAttribute.class) {
                        out.append(String.format(",\n%s sizes%s: %.2f to %.2f", padding, formatName(attr.name), ((ScaledDoubleDataAttribute) attr).valueMin, ((ScaledDoubleDataAttribute) attr).valueMax));
                    } else {
                        out.append(String.format(",\n%s sizes%s", padding, formatName(attr.name)));
                    }
                    break;
                case SHAPE:
                    out.append(String.format(",\n%s shapes%s: %d class%s", padding, formatName(attr.name), ((FactorizedDataAttribute<?>) attr).factors.length, ((FactorizedDataAttribute<?>) attr).factors.length == 1 ? EMPTY_STRING : "es"));
                    break;
            }
        }
        return out.append('}').toString();
    }


}
