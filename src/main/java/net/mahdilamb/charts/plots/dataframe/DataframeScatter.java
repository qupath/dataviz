package net.mahdilamb.charts.plots.dataframe;


import net.mahdilamb.charts.dataframe.Axis;
import net.mahdilamb.charts.dataframe.DataFrame;
import net.mahdilamb.charts.dataframe.DataType;
import net.mahdilamb.charts.dataframe.DoubleSeries;
import net.mahdilamb.charts.graphics.MarkerShape;
import net.mahdilamb.charts.graphics.Stroke;
import net.mahdilamb.charts.plots.MarginalMode;
import net.mahdilamb.charts.plots.Scatter;
import net.mahdilamb.colormap.Color;
import net.mahdilamb.colormap.Colormap;
//TODO extend everything from scatter
public class DataframeScatter extends Scatter {
    private final DataFrame dataFrame;
    String title;

    public DataframeScatter(final DataFrame dataFrame, final String x, final String y) {
        super(((DoubleSeries) dataFrame.getDoubleSeries(x)).toArray(new double[dataFrame.size(Axis.INDEX)]), ((DoubleSeries) dataFrame.getDoubleSeries(y)).toArray(new double[dataFrame.size(Axis.INDEX)]));
        this.dataFrame = dataFrame;
        setXLabel(x).setYLabel(y);
    }

    @Override
    public DataframeScatter setName(String name) {
        return (DataframeScatter) super.setName(name);
    }

    @Override
    public DataframeScatter setLineStyle(Stroke style) {
        return (DataframeScatter) super.setLineStyle(style);
    }

    @Override
    public DataframeScatter setLineWidth(double size) {
        return (DataframeScatter) super.setLineWidth(size);
    }

    @Override
    public DataframeScatter setLineColor(Color color) {
        return (DataframeScatter) super.setLineColor(color);
    }

    @Override
    public DataframeScatter setEdgeVisible(boolean edgeVisible) {
        return (DataframeScatter) super.setEdgeVisible(edgeVisible);
    }

    @Override
    protected DataframeScatter showGroupInLegend(int group, boolean showInLegend) {
        return (DataframeScatter) super.showGroupInLegend(group, showInLegend);
    }

    @Override
    protected DataframeScatter showGroupInLegend(String group, boolean showInLegend) {
        return (DataframeScatter) super.showGroupInLegend(group, showInLegend);
    }

    @Override
    public DataframeScatter setLabels(String xLabel, String yLabel) {
        return (DataframeScatter) super.setLabels(xLabel, yLabel);
    }

    @Override
    public DataframeScatter setGroupName(int group, String name) {
        return (DataframeScatter) super.setGroupName(group, name);
    }

    @Override
    public DataframeScatter setGroupColor(int group, Color color) {
        return (DataframeScatter) super.setGroupColor(group, color);
    }

    @Override
    public DataframeScatter setGroupStroke(int group, Stroke color) {
        return (DataframeScatter) super.setGroupStroke(group, color);
    }

    @Override
    public DataframeScatter setGroupName(String group, String name) {
        return (DataframeScatter) super.setGroupName(group, name);
    }

    @Override
    public DataframeScatter setGroupColor(String group, Color color) {
        return (DataframeScatter) super.setGroupColor(group, color);
    }

    @Override
    public DataframeScatter setGroupStroke(String group, Stroke color) {
        return (DataframeScatter) super.setGroupStroke(group, color);
    }

    @Override
    public DataframeScatter setGroupLine(String group, Stroke line) {
        return (DataframeScatter) super.setGroupLine(group, line);
    }

    @Override
    public DataframeScatter setGroupLine(int group, Stroke line) {
        return (DataframeScatter) super.setGroupLine(group, line);
    }

    @Override
    public DataframeScatter setColors(String name, Iterable<String> groups) {
        return (DataframeScatter) super.setColors(name, groups);
    }

    @Override
    public DataframeScatter showInLegend(boolean showInLegend) {
        return (DataframeScatter) super.showInLegend(showInLegend);
    }

    @Override
    public DataframeScatter showColorBar(boolean showColorBar) {
        return (DataframeScatter) super.showColorBar(showColorBar);
    }

    @Override
    public DataframeScatter setOpacities(Iterable<Double> alphas) {
        return (DataframeScatter) super.setOpacities(alphas);
    }

    @Override
    public DataframeScatter setAbsoluteErrorX(Iterable<Double> errorX) {
        return (DataframeScatter) super.setAbsoluteErrorX(errorX);
    }

    @Override
    public DataframeScatter setAbsoluteErrorY(Iterable<Double> errorY) {
        return (DataframeScatter) super.setAbsoluteErrorY(errorY);
    }

    @Override
    public DataframeScatter setMode(Mode markerMode) {
        return (DataframeScatter) super.setMode(markerMode);
    }

    @Override
    public DataframeScatter setMarginalX(MarginalMode mode) {
        return (DataframeScatter) super.setMarginalX(mode);
    }

    @Override
    public DataframeScatter setMarginalY(MarginalMode mode) {
        return (DataframeScatter) super.setMarginalY(mode);
    }

    @Override
    public DataframeScatter setMarkerSize(Iterable<Double> size) {
        return (DataframeScatter) super.setMarkerSize(size);
    }

    @Override
    public DataframeScatter setMarker(char marker) {
        return (DataframeScatter) super.setMarker(marker);
    }

    @Override
    public DataframeScatter setColor(String colorName) {
        return (DataframeScatter) super.setColor(colorName);
    }

    @Override
    public DataframeScatter setColor(Color color) {
        return (DataframeScatter) super.setColor(color);
    }

    @Override
    public DataframeScatter setColors(Iterable<Color> colors) {
        return (DataframeScatter) super.setColors(colors);
    }

    @Override
    public DataframeScatter setShapes(Iterable<MarkerShape> shape) {
        return (DataframeScatter) super.setShapes(shape);
    }

    @Override
    public DataframeScatter setColormap(Colormap colormap) {
        return (DataframeScatter) super.setColormap(colormap);
    }

    @Override
    public DataframeScatter setColors(Colormap colormap, double[] colors) {
        return (DataframeScatter) super.setColors(colormap, colors);
    }

    @Override
    public DataframeScatter setColors(double[] colors) {
        return (DataframeScatter) super.setColors(colors);
    }

    @Override
    public DataframeScatter setXLabel(String name) {
        return (DataframeScatter) super.setXLabel(name);
    }

    @Override
    public DataframeScatter setYLabel(String name) {
        return (DataframeScatter) super.setYLabel(name);
    }

    public DataframeScatter setColors(final String colorSeries) {
        if (dataFrame.getType(colorSeries) == DataType.STRING) {
            return this.setColors(colorSeries, dataFrame.getStringSeries(colorSeries));
        }
        return setColors(((DoubleSeries) dataFrame.getDoubleSeries(colorSeries)).toArray(new double[dataFrame.size(Axis.INDEX)]));
    }

    public DataframeScatter setMarkerSizes(final String sizeSeries) {
        if (dataFrame.getType(sizeSeries) == DataType.STRING) {
            this.setColors(sizeSeries, dataFrame.getStringSeries(sizeSeries));//todo update the color per group
            return this;
        }
        return setMarkerSize(dataFrame.getDoubleSeries(sizeSeries));
    }

    public DataframeScatter setTitle(final String title) {
        this.title = title;
        return this;
    }
}
