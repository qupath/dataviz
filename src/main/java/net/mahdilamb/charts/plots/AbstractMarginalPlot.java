package net.mahdilamb.charts.plots;

import net.mahdilamb.charts.Plot;
import net.mahdilamb.charts.axes.NumericAxis;

/**
 * A scatter plot that contains an extra two regions to show distribution data
 *
 * @param <X> the type of the X axis
 * @param <Y> the type of the Y axis
 */
abstract class AbstractMarginalPlot<X extends NumericAxis, Y extends NumericAxis> extends AbstractRectangularPlot<X, Y> {

    net.mahdilamb.charts.Plot xMarginal, yMarginal;

    public AbstractMarginalPlot(X xAxis, Y yAxis) {
        super(xAxis, yAxis);
    }

    @Override
    protected void layoutPlot(double x, double y, double width, double height) {
        //TODO
    }

    @Override
    protected void layoutAxes() {
        super.layoutAxes();
        //todo layout marginals
    }

    public net.mahdilamb.charts.Plot getXMarginal() {
        return xMarginal;
    }

    public Plot getYMarginal() {
        return yMarginal;
    }


}
