package net.mahdilamb.dataviz.data;

import net.mahdilamb.dataframe.DataFrame;
import net.mahdilamb.dataframe.DoubleSeries;
import net.mahdilamb.dataframe.Series;
import net.mahdilamb.dataviz.PlotData;
import net.mahdilamb.dataviz.layouts.XYAxis;
import net.mahdilamb.dataviz.layouts.XYLayout;

/**
 * Data for XY series
 */
public abstract class RelationalData extends PlotData<XYLayout> {
    protected final DoubleSeries x, y;

    protected RelationalData(final DataFrame dataFrame, final String xAxis, final String yAxis) {
        super(dataFrame);
        x = dataFrame.getDoubleSeries(xAxis);
        y = dataFrame.getDoubleSeries(yAxis);
        getLayout().getXAxis().setTitle(xAxis);
        getLayout().getYAxis().setTitle(yAxis);
    }

    protected RelationalData(double[] x, double[] y) {
        super();
        this.x = Series.of(null, x);
        this.y = Series.of(null, y);
        if (this.x.size() != this.y.size()) {
            throw new IllegalArgumentException("x and y are of different sizes");
        }
    }

    @Override
    protected final XYLayout createLayout() {
        //todo consider multiple axes
        return new XYLayout(new XYAxis.XAxis(), new XYAxis.YAxis(true));
    }

}
