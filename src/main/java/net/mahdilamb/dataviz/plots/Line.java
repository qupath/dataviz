package net.mahdilamb.dataviz.plots;

import net.mahdilamb.dataframe.DataFrame;
import net.mahdilamb.dataframe.DataType;
import net.mahdilamb.dataframe.Series;
import net.mahdilamb.dataviz.PlotData;
import net.mahdilamb.dataviz.PlotLayout;
import net.mahdilamb.dataviz.PlotTrace;

import java.util.function.DoubleUnaryOperator;

/**
 * An XY line plot
 */
public final class Line extends PlotData.RelationalData<Line> {
    /**
     * Create an XY line plot
     * @param x the x doubles
     * @param y the y doubles
     */
    public Line(double[] x, double[] y) {
        super(x, y);
        markerMode = ScatterMode.LINE_ONLY;
    }

    public Line(double[] x, DoubleUnaryOperator func) {
        super(x, func);
        markerMode = ScatterMode.LINE_ONLY;
    }

    public Line(DataFrame dataFrame, String x, String y) {
        super(dataFrame, x, y);
        markerMode = ScatterMode.LINE_ONLY;
    }

    public Line(String[] x, double[] y) {
        super(x, y);
        markerMode = ScatterMode.LINE_ONLY;
    }

    @Override
    protected void init(PlotLayout plotLayout) {
        putLines(plotLayout, this, createLines(plotLayout));
        updateXYBounds(plotLayout, xMin, xMax, yMin, yMax,false,true);
    }

    public Line setColors(String series) throws DataFrameOnlyOperationException {
        final Series<?> s = getSeries(series);
        if (s.getType() == DataType.DOUBLE) {
            throw new UnsupportedOperationException("Series must not be double");
        }
        clear();
        setColors(new PlotTrace.Categorical(this, Attribute.COLOR, s));
        return this;
    }

    public Line setGroups(String series) throws DataFrameOnlyOperationException {
        final Series<?> s = getSeries(series);
        if (s.getType() == DataType.DOUBLE) {
            throw new UnsupportedOperationException("Series must not be double");
        }
        clear();
        if (group != null) {
            hoverFormatter.remove(group);
        }
        group = new PlotTrace.Categorical(this, Attribute.GROUP, s);
        return this;
    }

}
