package net.mahdilamb.dataviz.plots;

import net.mahdilamb.dataframe.DataFrame;
import net.mahdilamb.dataviz.PlotBounds;
import net.mahdilamb.dataviz.data.RelationalData;
import net.mahdilamb.dataviz.layouts.XYLayout;

import java.util.function.DoubleUnaryOperator;

public class Line extends RelationalData {
    protected Line(DataFrame dataFrame, String xAxis, String yAxis) {
        super(dataFrame, xAxis, yAxis);
    }

    protected Line(double[] x, double[] y) {
        super(x, y);
    }

    protected Line(double[] x, DoubleUnaryOperator y) {
        super(x, y);
    }

    @Override
    protected void init() {

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


}
