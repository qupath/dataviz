package net.mahdilamb.dataviz.plots;

import net.mahdilamb.colormap.Colors;
import net.mahdilamb.dataframe.DataFrame;
import net.mahdilamb.dataviz.PlotBounds;
import net.mahdilamb.dataviz.PlotOptions;
import net.mahdilamb.dataviz.PlotShape;
import net.mahdilamb.dataviz.data.RelationalData;
import net.mahdilamb.dataviz.layouts.XYLayout;

import java.awt.*;

@PlotOptions(name = "Scatter", supportsZoom = true, supportsManualZoom = true, supportsPan = true, supportsPolygonSelection = true, supportsZoomByWheel = true)
public class Scatter extends RelationalData {
    PlotBounds<PlotBounds.XY, XYLayout> bounds;

    double markerSize = 10;
    Color markerColor = Colors.deepskyblue;

    public Scatter(DataFrame dataFrame, String xAxis, String yAxis) {
        super(dataFrame, xAxis, yAxis);
        init();

    }

    public Scatter(double[] x, double[] y) {
        super(x, y);
        init();
    }

    void init() {
        @SuppressWarnings("unchecked") final PlotShape<XYLayout>[] shapes = new PlotShape[x.size()];
        for (int i = 0; i < shapes.length; ++i) {
            shapes[i] = createMarker(this, i, x.getDouble(i), y.getDouble(i), markerSize);
        }
        addShapes(shapes, true);
    }


    @Override
    protected Color getColor(int i) {
        return markerColor;
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

    @Override
    public int size() {
        return x.size();
    }
}
