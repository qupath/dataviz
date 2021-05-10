package net.mahdilamb.dataviz.plots;

import net.mahdilamb.colormap.Colors;
import net.mahdilamb.dataframe.DataFrame;
import net.mahdilamb.dataframe.utils.IntArrayList;
import net.mahdilamb.dataviz.Legend;
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

        final PlotShape<XYLayout> line = createPolyLine(this, 0, new IntArrayList(ArrayUtils.intRange(x.size())));
        addShapes(line);
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
    protected Legend.Glyph getGlyph(PlotDataAttribute.Categorical attribute, int category) {
        //TODO
        return null;
    }

    @Override
    protected Legend.Glyph getGlyph(PlotDataAttribute.Numeric attribute, double value) {
        //TODO
        return null;
    }

    @Override
    public Line setXLabel(String name) {
        return (Line) super.setXLabel(name);
    }

    @Override
    public Line setYLabel(String name) {
        return (Line) super.setYLabel(name);
    }

}
