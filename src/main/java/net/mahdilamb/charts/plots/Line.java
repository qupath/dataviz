package net.mahdilamb.charts.plots;

import net.mahdilamb.charts.dataframe.utils.GroupBy;
import net.mahdilamb.charts.graphics.Stroke;
import net.mahdilamb.charts.utils.ArrayUtils;
import net.mahdilamb.colormap.Color;

import java.util.function.DoubleUnaryOperator;

public class Line extends AbstractScatter<Line> implements RectangularPlot {
    boolean fillGaps = false;

    Stroke errorUpperStroke;
    Stroke errorLowerStroke;

    double[] errorLowers;
    double[] errorUppers;
    double[] bandLowers;
    double[] bandUppers;

    Stroke bandStroke;
    Color bandFill;

    public Line(double[] x, double[] y) {
        super(x, y);

    }

    public Line(double[] x, DoubleUnaryOperator toYFunction) {
        super(x, toYFunction);

    }

    public Line setBandUpper(Iterable<Double> error) {
        bandUppers = ArrayUtils.fill(new double[points.size()], error, Double.NaN);
        return redraw();
    }

    public Line setBandLower(Iterable<Double> error) {
        bandLowers = ArrayUtils.fill(new double[points.size()], error, Double.NaN);
        return redraw();
    }

    public Line setErrorUpper(Iterable<Double> error) {
        errorUppers = ArrayUtils.fill(new double[points.size()], error, Double.NaN);
        return redraw();
    }

    public Line setErrorLower(Iterable<Double> error) {
        errorLowers = ArrayUtils.fill(new double[points.size()], error, Double.NaN);
        return redraw();
    }

    @Override
    public Line setLineStyle(Stroke style) {
        return super.setLineStyle(style);
    }

    public Line setBandFill(final Color color) {
        this.bandFill = color;
        return redraw();
    }

    public Line setBandStroke(final Stroke stroke) {
        this.bandStroke = stroke;
        return redraw();
    }

    public Line fillGaps(boolean fillGaps) {
        this.fillGaps = fillGaps;
        return requestDataUpdate();
    }


}
