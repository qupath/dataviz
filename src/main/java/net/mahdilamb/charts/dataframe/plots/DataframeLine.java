package net.mahdilamb.charts.dataframe.plots;

import net.mahdilamb.charts.dataframe.Axis;
import net.mahdilamb.charts.dataframe.DataFrame;
import net.mahdilamb.charts.dataframe.DoubleSeries;
import net.mahdilamb.charts.graphics.Stroke;
import net.mahdilamb.charts.plots.Line;
import net.mahdilamb.colormap.Color;
//todo override Line
public class DataframeLine extends Line {
    private final DataFrame dataFrame;
    String title;

    public DataframeLine(final DataFrame dataFrame, final String x, final String y) {
        super(((DoubleSeries) dataFrame.getDoubleSeries(x)).toArray(new double[dataFrame.size(Axis.INDEX)]), ((DoubleSeries) dataFrame.getDoubleSeries(y)).toArray(new double[dataFrame.size(Axis.INDEX)]));
        this.dataFrame = dataFrame;
        setXLabel(x).setYLabel(y);
    }

    @Override
    public DataframeLine setBandUpper(Iterable<Double> error) {
        return (DataframeLine) super.setBandUpper(error);

    }

    @Override
    public DataframeLine setName(String name) {
        return (DataframeLine) super.setName(name);
    }

    @Override
    public DataframeLine setColors(String name, Iterable<String> groups) {
        return (DataframeLine) super.setColors(name, groups);
    }

    @Override
    public DataframeLine showInLegend(boolean showInLegend) {
        return (DataframeLine) super.showInLegend(showInLegend);
    }

    @Override
    public DataframeLine showColorBar(boolean showColorBar) {
        return (DataframeLine) super.showColorBar(showColorBar);
    }

    @Override
    public DataframeLine setOpacities(Iterable<Double> alphas) {
        return (DataframeLine) super.setOpacities(alphas);
    }

    @Override
    public DataframeLine setLineStyle(Stroke style) {
        return (DataframeLine) super.setLineStyle(style);
    }

    @Override
    public DataframeLine setLineWidth(double size) {
        return (DataframeLine) super.setLineWidth(size);
    }

    @Override
    public DataframeLine setLineColor(Color color) {
        return (DataframeLine) super.setLineColor(color);
    }

    public DataframeLine setGroups(final String groupSeries) {
        return this.setColors(groupSeries, dataFrame.getStringSeries(groupSeries));
    }

    public DataframeLine setTitle(final String title) {
        this.title = title;
        return this;
    }
}
