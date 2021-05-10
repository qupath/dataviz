package net.mahdilamb.dataviz.data;

import net.mahdilamb.dataframe.DataFrame;
import net.mahdilamb.dataframe.DoubleSeries;
import net.mahdilamb.dataframe.Series;
import net.mahdilamb.dataviz.PlotBounds;
import net.mahdilamb.dataviz.PlotData;
import net.mahdilamb.dataviz.graphics.FillMode;
import net.mahdilamb.dataviz.layouts.XYAxis;
import net.mahdilamb.dataviz.layouts.XYLayout;
import net.mahdilamb.dataviz.plots.ScatterMode;
import net.mahdilamb.stats.ArrayUtils;

import java.util.Arrays;
import java.util.function.DoubleUnaryOperator;

/**
 * Data for XY series
 */
public abstract class RelationalData<PD extends RelationalData<PD>> extends PlotData<PD, XYLayout> {
    protected final DoubleSeries x, y;
    protected PlotBounds<PlotBounds.XY, XYLayout> bounds;

    protected FillMode fillMode = FillMode.NONE;

    protected ScatterMode markerMode = ScatterMode.MARKER_ONLY;

    protected RelationalData(final DataFrame dataFrame, final String xAxis, final String yAxis) {
        super(dataFrame);
        x = dataFrame.getDoubleSeries(xAxis);
        y = dataFrame.getDoubleSeries(yAxis);
        getLayout().getXAxis().setTitle(xAxis);
        getLayout().getYAxis().setTitle(yAxis);
        init();

    }

    protected RelationalData(double[] x, double[] y) {
        super();
        this.x = Series.of(null, x);
        this.y = Series.of(null, y);
        if (this.x.size() != this.y.size()) {
            throw new IllegalArgumentException("x and y are of different sizes");
        }
        init();

    }

    protected RelationalData(double[] x, DoubleUnaryOperator y) {
        super();
        this.x = Series.of(null, x);
        this.y = Series.of(null, ArrayUtils.map(x, y));
        if (this.x.size() != this.y.size()) {
            throw new IllegalArgumentException("x and y are of different sizes");
        }
        init();
    }

    protected abstract void init();

    protected RelationalData setXLabel(final String name) {
        getLayout().getXAxis().setTitle(name);
        return this;
    }

    protected RelationalData setYLabel(final String name) {
        getLayout().getYAxis().setTitle(name);
        return this;
    }

    @Override
    protected final XYLayout createLayout() {
        //todo consider multiple axes
        return new XYLayout(new XYAxis.XAxis(), new XYAxis.YAxis(true));
    }

    public double getX(int i) {
        return x.get(i);
    }

    public double getY(int i) {
        return y.get(i);
    }

    @Override
    public int size() {
        return x.size();
    }
}
