package net.mahdilamb.charts.plots;

import net.mahdilamb.charts.PlotSeries;
import net.mahdilamb.charts.statistics.utils.GroupBy;
import net.mahdilamb.colormap.Color;
import net.mahdilamb.colormap.Colormap;

import java.util.Map;

import static net.mahdilamb.charts.utils.ArrayUtils.fill;

@PlotType(name = "Pie", compatibleSeries = {PlotType.DataType.STRING, PlotType.DataType.NUMERIC})
public final class Pie extends PlotSeries.Categorical<Pie> {
    double hole = 0;
    double[] pull;

    public Pie(String[] names, double[] values) {
        super(names, values);
        groups = new GroupBy<>(names);
        groupAttributes = new GroupAttributes[groups.numGroups()];
        int i = 0;
        for (final GroupBy.Group<String> g : this.groups) {
            groupAttributes[i++] = new GroupAttributes(g);
        }
    }

    public Pie setColorSequence(final Colormap colormap) {
        groupColormap = colormap;
        return requestLayout();
    }

    public Pie setColors(final Map<String, Color> colors) {
        for (final Map.Entry<String, Color> entry : colors.entrySet()) {
            final GroupAttributes g = getGroupAttribute(entry.getKey());
            g.setMarkerColor(entry.getValue());
        }
        return requestLayout();
    }

    public Pie setHoleSize(double size) {
        this.hole = size;
        return requestLayout();
    }

    public Pie setPulls(final Iterable<Double> pulls) {
        this.pull = fill(new double[values.size()], pulls, 0);
        return requestDataUpdate();
    }
}
