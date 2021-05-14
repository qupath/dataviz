package net.mahdilamb.dataviz.plots;

import net.mahdilamb.dataframe.DataFrame;
import net.mahdilamb.dataframe.utils.IntArrayList;
import net.mahdilamb.dataviz.GlyphFactory;
import net.mahdilamb.dataviz.PlotBounds;
import net.mahdilamb.dataviz.PlotDataAttribute;
import net.mahdilamb.dataviz.PlotShape;
import net.mahdilamb.dataviz.data.RelationalData;
import net.mahdilamb.dataviz.layouts.XYLayout;
import net.mahdilamb.stats.ArrayUtils;

import java.util.function.DoubleUnaryOperator;

public class Line extends RelationalData<Line> {
    public Line(DataFrame dataFrame, String xAxis, String yAxis) {
        super(dataFrame, xAxis, yAxis);
    }

    public Line(double[] x, double[] y) {
        super(x, y);
    }

    public Line(double[] x, DoubleUnaryOperator y) {
        super(x, y);
    }

    @Override
    protected void init() {
        markerMode = ScatterMode.LINE_ONLY;
        addShapes(createLines());
    }

    @Override
    protected PlotBounds<? extends PlotBounds.Bounds<XYLayout>, XYLayout> getBoundPreferences() {
        if (bounds == null) {
            double xMin = x.min(),
                    xMax = x.max(),
                    yMin = y.min(),
                    yMax = y.max();
            final PlotBounds.XY home = new PlotBounds.XY(xMin, yMin, xMax, yMax);
            bounds = new PlotBounds<>(home, home);
        }
        return bounds;
    }

    @Override
    protected GlyphFactory.Glyph getGlyph(PlotDataAttribute.Categorical attribute, int category) {
        return GlyphFactory.createScatterGlyph(this, calculateColor(attribute, getQualitativeColormap(), category));
    }

    @Override
    protected GlyphFactory.Glyph getGlyph(PlotDataAttribute.Numeric attribute, double value) {
        //TODO
        return null;
    }

    @Override
    public GlyphFactory.Glyph getGlyph(PlotDataAttribute.UncategorizedTrace uncategorizedTrace, int i) {
        return GlyphFactory.createScatterGlyph(this, calculateColorOf(uncategorizedTrace, getQualitativeColormap(), i));
    }


    public Line setColors(String seriesName) throws DataFrameOnlyMethodException {
        addAttribute(seriesName, PlotDataAttribute.Type.COLOR,
                (attr, series) -> {
                    throw new UnsupportedOperationException("Series must not be floating point");
                },
                (attr, series) -> new PlotDataAttribute.Categorical(this, attr, series));
        clear();
        init();
        return this;
    }

   /* public Line setGroups(String seriesName) throws DataFrameOnlyMethodException {
        addAttribute(seriesName, PlotDataAttribute.Type.GROUP,
                (attr, series) -> {
                    throw new UnsupportedOperationException("Series must not be floating point");
                },
                (attr, series) -> new PlotDataAttribute.Categorical(this, attr, series));
        clear();
        init();
        return this;
    }*/
}
