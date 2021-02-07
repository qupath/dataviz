package net.mahdilamb.charts.plots;

import net.mahdilamb.charts.PlotSeries;
import net.mahdilamb.charts.graphics.Orientation;
import net.mahdilamb.charts.graphics.Stroke;
import net.mahdilamb.charts.statistics.utils.GroupBy;

import static net.mahdilamb.charts.utils.ArrayUtils.fill;

public final class Bar extends PlotSeries.Categorical<Bar> implements RectangularPlot {


    public enum Mode {
        STACKED,
        GROUPED,
        OVERLAY
    }

    static final double DEFAULT_WIDTH = 0.8;
    static final double DEFAULT_BASE = 0;
    /**
     * The default offset on the categorical axis
     */
    static final double DEFAULT_OFFSET = 0;

    Orientation orientation = Orientation.VERTICAL;

    String xLabel, yLabel;

    String subGroupName;
    GroupBy<String> subGroups;
    GroupAttributes[] subGroupAttributes;

    double groupGap = 0;
    double gap = 0;
    Mode mode = Mode.GROUPED;
    double[] widths;
    double[] bases;

    double[] errorLower;
    double[] errorUpper;
    Stroke errorUpperStroke;
    Stroke errorLowerStroke;

    double[] colorScale;

    double offset = DEFAULT_OFFSET;

    public Bar(String[] names, double[] values) {
        super(names, values);
        groups = new GroupBy<>(names);
        groupAttributes = new GroupAttributes[groups.numGroups()];
        int i = 0;
        for (final GroupBy.Group<String> g : this.groups) {
            groupAttributes[i++] = new GroupAttributes(g);
        }
    }

    public Bar setWidths(Iterable<Double> widths) {
        this.widths = fill(new double[values.length], widths, DEFAULT_WIDTH);
        return requestLayout();
    }

    public Bar setBases(Iterable<Double> bases) {
        this.bases = fill(new double[values.length], bases, 0);
        return requestLayout();
    }

    public Bar setErrorUpper(Iterable<Double> error) {
        this.errorUpper = fill(new double[values.length], error, Double.NaN);
        return requestLayout();
    }

    public Bar setErrorLower(Iterable<Double> error) {
        this.errorLower = fill(new double[values.length], error, Double.NaN);
        return requestLayout();
    }

    public Bar setBarGap(double gap) {
        this.gap = gap;
        return requestLayout();
    }

    public Bar setOffset(double offset) {
        this.offset = offset;
        return requestLayout();
    }

    public Bar setMode(Mode mode) {
        this.mode = mode;
        return requestLayout();
    }

    public Bar setGroupGap(double gap) {
        this.groupGap = gap;
        return requestLayout();
    }

    public Bar setColors(final Iterable<Double> colors) {
        this.colorScale = fill(new double[values.length], colors, Double.NaN);
        return requestLayout();
    }

    @Override
    public Bar setColors(String name, Iterable<String> groups) {
        return super.setColors(name, groups);
    }

    public Bar setGroups(String name, Iterable<String> groups) {
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

    @Override
    public String getXLabel() {
        return xLabel;
    }

    @Override
    public String getYLabel() {
        return yLabel;
    }

    @Override
    public double getMinX() {
        return super.getMinX();
    }

    @Override
    public double getMaxX() {
        return super.getMaxX();
    }

    @Override
    public double getMinY() {
        return super.getMinY();
    }

    @Override
    public double getMaxY() {
        return super.getMaxY();
    }

}
