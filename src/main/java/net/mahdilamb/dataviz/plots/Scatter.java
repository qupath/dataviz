package net.mahdilamb.dataviz.plots;

import net.mahdilamb.colormap.Colors;
import net.mahdilamb.dataframe.DataFrame;
import net.mahdilamb.dataviz.*;
import net.mahdilamb.dataviz.data.RelationalData;
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
    public final static double DEFAULT_MARKER_SIZE = 10;
    /**
     * Default largest marker size (for numerical traces)
     */
    public final static double DEFAULT_MAX_MARKER_SIZE = 30;

    public final static double DEFAULT_MULTICOLOR_OPACITY = 0.8;

    double markerSize = DEFAULT_MARKER_SIZE;
    Color markerColor = Colors.deepskyblue;
    double markerOpacity = 1.0;

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
        hoverFormatter = new HoverText<>(this, formatters);
        hoverFormatter.add("%s=%{x:s}", this.getLayout().getXAxis()::getTitle);
        hoverFormatter.add("%s=%{y:s}", this.getLayout().getYAxis()::getTitle);
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
        return new Color(baseColor.getRed() / 255f, baseColor.getGreen() / 255f, baseColor.getBlue() / 255f, baseColor.getAlpha() / 255f * (float) markerOpacity);
    }

    public Scatter setColors(final String seriesName) throws DataFrameOnlyMethodException {
        setStyler(seriesName, DataStyler.StyleAttribute.COLOR,
                (attr, series) -> {
                    markerOpacity = DEFAULT_MULTICOLOR_OPACITY;
                    return new DataStyler.Numeric(this, attr, series, 0, 1);
                },
                (attr, series) -> {
                    markerOpacity = DEFAULT_MULTICOLOR_OPACITY;
                    return new DataStyler.Categorical(this, attr, series);
                });
        return this;
    }

    public Scatter setColor(final Color color) {
        clearStyler(DataStyler.StyleAttribute.COLOR);
        markerColor = color;
        return this;
    }

    public Scatter setColor(final String colorName) {
        return setColor(ColorUtils.convertToColor(colorName));
    }

    @Override
    public Scatter setXLabel(String name) {
        return (Scatter) super.setXLabel(name);
    }

    @Override
    public Scatter setYLabel(String name) {
        return (Scatter) super.setYLabel(name);
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
        setStyler(seriesName,
                DataStyler.StyleAttribute.SIZE,
                (attr, series) -> new DataStyler.Numeric(this, DataStyler.StyleAttribute.SIZE, series, minSize, maxSize),
                (attr, series) -> {
                    //todo
                    return null;

                }
        );
        return this;
    }

    public Scatter setSizes(final String seriesName) throws DataFrameOnlyMethodException {
        return setSizes(seriesName, DEFAULT_MARKER_SIZE, DEFAULT_MAX_MARKER_SIZE);
    }

    public Scatter setSize(final double size) {
        markerSize = Numbers.requireFinitePositive(size);
        clearStyler(DataStyler.StyleAttribute.SIZE);
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
        return markerSize;//todo
    }

    @Override
    protected double getSearchPaddingY() {
        return markerSize;//todo
    }

}
