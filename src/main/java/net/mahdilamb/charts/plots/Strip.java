package net.mahdilamb.charts.plots;

import net.mahdilamb.charts.PlotSeries;
import net.mahdilamb.charts.graphics.Orientation;
import net.mahdilamb.charts.statistics.utils.GroupBy;

public class Strip extends PlotSeries.Distribution<Strip>  {

    Orientation orientation = Orientation.VERTICAL;

    String xLabel, yLabel;

    String subGroupName;
    GroupBy<String> subGroups;
    GroupAttributes[] subGroupAttributes;

    protected double jitter = 0, pointPos = 0;

    public Strip(double[] values) {
        super(values);
    }


    public Strip setOrientation(final Orientation orientation) {
        this.orientation = orientation;
        return requestLayout();
    }

    @Override
    public Strip setColors(String name, Iterable<String> groups) {
        return super.setColors(name, groups);
    }

    public Strip setGroups(String name, Iterable<String> groups) {
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


}
