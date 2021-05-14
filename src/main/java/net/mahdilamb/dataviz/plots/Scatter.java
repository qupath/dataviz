package net.mahdilamb.dataviz.plots;

import net.mahdilamb.colormap.Colormap;
import net.mahdilamb.colormap.Colormaps;
import net.mahdilamb.colormap.Colors;
import net.mahdilamb.dataframe.DataFrame;
import net.mahdilamb.dataviz.*;
import net.mahdilamb.dataviz.data.RelationalData;
import net.mahdilamb.dataviz.figure.Renderer;
import net.mahdilamb.dataviz.graphics.GraphicsBuffer;
import net.mahdilamb.dataviz.layouts.XYLayout;
import net.mahdilamb.dataviz.utils.ColorUtils;
import net.mahdilamb.dataviz.utils.Numbers;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.DoubleUnaryOperator;
import java.util.function.IntFunction;

@PlotOptions(name = "Scatter", supportsZoom = true, supportsManualZoom = true, supportsPan = true, supportsPolygonSelection = true, supportsZoomByWheel = true)
public class Scatter extends RelationalData<Scatter> {


    /**
     * Default marker size
     */
    private final static double DEFAULT_MARKER_SIZE = 10;
    /**
     * Default largest marker size (for numerical traces)
     */
    private final static double DEFAULT_MAX_MARKER_SIZE = 30;

    private final static double DEFAULT_MULTICOLOR_OPACITY = 0.8;

    double markerSize = DEFAULT_MARKER_SIZE;
    Color markerColor = Colors.deepskyblue;
    double markerOpacity = 1.0;
    MarkerShape shape = null;

    public Scatter(DataFrame dataFrame, String xAxis, String yAxis) {
        super(dataFrame, xAxis, yAxis);
    }

    public Scatter(double[] x, double[] y) {
        super(x, y);
    }

    public Scatter(double[] x, DoubleUnaryOperator y) {
        super(x, y);
    }

    @Override
    protected void init() {
        @SuppressWarnings("unchecked") final PlotShape<XYLayout>[] shapes = new PlotShape[x.size()];
        for (int i = 0; i < shapes.length; ++i) {
            shapes[i] = createMarker(this, i, x.getDouble(i), y.getDouble(i));
        }
        addShapes(shapes, true);

        final Map<String, IntFunction<?>> formatters = new HashMap<>(2);
        formatters.put("x", this.x::get);
        formatters.put("y", this.y::get);
        hoverFormatter = new HoverText<>(formatters);
        hoverFormatter.add("%s = %{x:s}", this.getLayout().getXAxis()::getTitle);
        hoverFormatter.add("%s = %{y:s}", this.getLayout().getYAxis()::getTitle);
    }

    @Override
    protected Color getColor(int i) {
        final Color baseColor;
        final Color color;
        if ((color = super.getColor(i)) != null) {
            baseColor = color;
        } else {
            baseColor = markerColor;
        }
        if (markerOpacity == 1.0) {
            return baseColor;
        }
        return ColorUtils.applyAlpha(baseColor, (float) getOpacity(i));
    }

    @Override
    protected MarkerShape getShape(int i) {
        final PlotDataAttribute attribute;
        if ((attribute = getAttribute(PlotDataAttribute.Type.SHAPE)) != null) {
            //TODO
        }
        if (shape != null) {
            return shape;
        }
        return super.getShape(i);
    }

    public Scatter setShape(MarkerShape shape) {
        removeAttribute(PlotDataAttribute.Type.SHAPE);
        this.shape = shape;
        return refresh();
    }

    public Scatter setShape(final String shape) {
        return setShape(MarkerShape.get(shape));
    }

    @Override
    protected double getOpacity(int i) {
        return markerOpacity;
    }

    public Scatter setOpacity(double opacity) {
        if (!Double.isFinite(opacity)) {
            throw new IllegalArgumentException("Opacity must be finite");
        }
        if (opacity < 0 || opacity > 1) {
            throw new IllegalArgumentException("Opacity must be between 0 and 1");
        }
        removeAttribute(PlotDataAttribute.Type.OPACITY);
        this.markerOpacity = opacity;
        return refresh();
    }

    public Scatter setColors(final String seriesName) throws DataFrameOnlyMethodException {
        addAttribute(seriesName, PlotDataAttribute.Type.COLOR,
                (attr, series) -> {
                    markerOpacity = DEFAULT_MULTICOLOR_OPACITY;
                    return new PlotDataAttribute.Numeric(this, attr, series, 0, 1);
                },
                (attr, series) -> {
                    markerOpacity = DEFAULT_MULTICOLOR_OPACITY;
                    final PlotDataAttribute.Categorical styler = new PlotDataAttribute.Categorical(this, attr, series);
                    addToHoverText(styler, "%s = %{color:s}", styler::getName, "color", styler::get);
                    return styler;
                });
        return this;
    }

    public Scatter setColormap(final Colormap colormap) {
        //TODO if color is already set
        if (colormap.isQualitative()) {
            qualitativeColormap = colormap;
        } else {
            sequentialColormap = colormap;
        }
        return refresh();
    }

    public Scatter setColormap(final String colormapName) {
        return setColormap(Colormaps.get(colormapName));
    }

    public Scatter setColor(final Color color) {
        removeAttribute(PlotDataAttribute.Type.COLOR);
        markerColor = color;
        return this;
    }

    public Scatter setColor(final String colorName) {
        return setColor(ColorUtils.convertToColor(colorName));
    }

    @Override
    protected double getSize(int i) {
        final double size;
        if (!Double.isNaN(size = super.getSize(i))) {
            return size;
        }
        return markerSize;
    }

    public Scatter setSizes(final String seriesName, double minSize, double maxSize) throws DataFrameOnlyMethodException {
        addAttribute(seriesName,
                PlotDataAttribute.Type.SIZE,
                (attr, series) -> {
                    final PlotDataAttribute.Numeric styler = new PlotDataAttribute.Numeric(this, PlotDataAttribute.Type.SIZE, series, minSize, maxSize);
                    addToHoverText(styler, "%s = %{size:.1f}", styler::getName, "size", i -> getRaw(styler, i));
                    return styler;
                },
                (attr, series) -> {
                    //todo
                    return null;

                });
        return this;
    }

    public Scatter setSizes(final String seriesName) throws DataFrameOnlyMethodException {
        return setSizes(seriesName, DEFAULT_MARKER_SIZE, DEFAULT_MAX_MARKER_SIZE);
    }

    public Scatter setSize(final double size) {
        markerSize = Numbers.requireFinitePositive(size);
        removeAttribute(PlotDataAttribute.Type.SIZE);
        return this;
    }

    @Override
    protected PlotBounds<PlotBounds.XY, XYLayout> getBoundPreferences() {
        if (bounds == null) {
            double padding = 0.05;
            double xMin = x.min(),
                    xMax = x.max(),
                    yMin = y.min(),
                    yMax = y.max(),
                    xBuf = (xMax - xMin) * padding,
                    yBuf = (yMax - yMin) * padding;
            final PlotBounds.XY home = new PlotBounds.XY(xMin - xBuf, yMin - yBuf, xMax + xBuf, yMax + yBuf);
            bounds = new PlotBounds<>(home, home);
        }
        return bounds;
    }

    @Override
    protected double getSearchPaddingX() {
        //TODO check
        final PlotDataAttribute styler;
        if ((styler = getAttribute(PlotDataAttribute.Type.SIZE)) instanceof PlotDataAttribute.Numeric) {
            return ((PlotDataAttribute.Numeric) styler).getMax() * .5;
        }
        return markerSize;
    }

    @Override
    protected double getSearchPaddingY() {
        return getSearchPaddingX();
    }

    @Override
    protected GlyphFactory.Glyph getGlyph(PlotDataAttribute.Categorical attribute, int category) {
        if (attribute.getType() == PlotDataAttribute.Type.COLOR) {
            return GlyphFactory.createScatterGlyph(this, calculateColor(attribute, getQualitativeColormap(), category));
        }
        return null;
    }

    @Override
    protected GlyphFactory.Glyph getGlyph(PlotDataAttribute.Numeric attribute, double value) {
        //TODO for other types
        return GlyphFactory.createSizedScatterGlyph(this, Color.DARK_GRAY, scale(attribute, value), attribute.getMax());
    }

}
