package net.mahdilamb.charts.plots;

import net.mahdilamb.charts.graphics.Stroke;
import net.mahdilamb.charts.statistics.utils.GroupBy;
import net.mahdilamb.colormap.Color;

import java.util.function.DoubleUnaryOperator;

import static net.mahdilamb.charts.utils.ArrayUtils.fill;

public class Line extends AbstractScatter<Line> {
    boolean fillGaps = false;

    double[] errorLower;
    double[] errorUpper;
    Stroke errorUpperStroke;
    Stroke errorLowerStroke;

    double[] bandLower;
    double[] bandUpper;
    Stroke bandStroke;
    Color bandFill;

    String subGroupName;
    GroupBy<String> subGroups;
    GroupAttributes[] subGroupAttributes;

    public Line(double[] x, double[] y) {
        super(x, y);

    }

    public Line(double[] x, DoubleUnaryOperator toYFunction) {
        super(x, toYFunction);

    }

    public Line setBandUpper(Iterable<Double> error) {
        this.bandUpper = fill(new double[x.size()], error, Double.NaN);
        return requestLayout();
    }

    public Line setBandLower(Iterable<Double> error) {
        this.bandLower = fill(new double[x.size()], error, Double.NaN);
        return requestLayout();
    }

    public Line setErrorUpper(Iterable<Double> error) {
        this.errorUpper = fill(new double[x.size()], error, Double.NaN);
        return requestLayout();
    }

    public Line setErrorLower(Iterable<Double> error) {
        this.errorLower = fill(new double[x.size()], error, Double.NaN);
        return requestLayout();
    }

    @Override
    public Line setLineStyle(Stroke style) {
        return super.setLineStyle(style);
    }

    @Override
    public Line setLineWidth(double size) {
        return super.setLineWidth(size);
    }

    @Override
    public Line setLineColor(Color color) {
        return super.setLineColor(color);
    }

    @Override
    public Line setGroupLine(int group, Stroke line) {
        return super.setGroupLine(group, line);
    }

    @Override
    public Line setGroupLine(String group, Stroke line) {
        return super.setGroupLine(group, line);
    }

    @Override
    public Line setColors(String name, Iterable<String> groups) {
        return super.setColors(name, groups);
    }

    public Line setBandFill(final Color color) {
        this.bandFill = color;
        return requestLayout();
    }

    public Line setBandStroke(final Stroke stroke) {
        this.bandStroke = stroke;
        return requestLayout();
    }

    public Line setFillGaps(boolean fillGaps) {
        this.fillGaps = fillGaps;
        return requestDataUpdate();
    }

    public Line setGroups(String name, Iterable<String> groups) {
        this.subGroupName = name;
        this.subGroups = new GroupBy<>(groups);
        subGroupAttributes = new GroupAttributes[this.subGroups.numGroups()];
        int i = 0;
        for (final GroupBy.Group<String> g : this.subGroups) {
            subGroupAttributes[i++] = new GroupAttributes(g);
        }
        showInLegend = true;
        return requestDataUpdate();
    }

}
