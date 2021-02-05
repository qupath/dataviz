package net.mahdilamb.charts.plots;

import net.mahdilamb.charts.PlotSeries;
import net.mahdilamb.charts.graphics.Stroke;
import net.mahdilamb.colormap.Color;

import java.util.function.DoubleUnaryOperator;

import static net.mahdilamb.charts.utils.ArrayUtils.fill;

abstract class AbstractScatter<S extends AbstractScatter<S>> extends PlotSeries.XY<S> {
    double[] opacities;
    Stroke lineStyle;

    protected AbstractScatter(double[] x, DoubleUnaryOperator toYFunction) {
        super(x, toYFunction);
    }

    protected AbstractScatter(String name, double[] x, double[] y) {
        super(x, y);
        setName(name);
    }

    protected AbstractScatter(double[] x, double[] y) {
        super(x, y);
        showInLegend(false);
    }


    public S setOpacities(Iterable<Double> alphas) {
        this.opacities = fill(new double[x.length], alphas, 1);
        return requestLayout();
    }

    public S setLineStyle(final Stroke style) {
        this.lineStyle = style;
        return requestLayout();
    }

    public S setLineWidth(double size) {
        return setLineStyle(new Stroke(lineStyle.getColor(), size));
    }

    public S setLineColor(Color color) {
        return setLineStyle(new Stroke(color, lineStyle.getWidth()));
    }

    //todo trendline, cluster

    @Override
    public S setColors(String name, Iterable<String> groups) {
        return super.setColors(name, groups);
    }

    @Override
    public S setXLabel(String name) {
        return super.setXLabel(name);
    }

    @Override
    public S setYLabel(String name) {
        return super.setYLabel(name);
    }
}
