package net.mahdilamb.charts.plots;

import net.mahdilamb.charts.Axis;
import net.mahdilamb.charts.Plot;
import net.mahdilamb.charts.styles.Orientation;

import java.util.Objects;

/**
 * Plot layout for plots laid out on a rectangular graph area
 */
public abstract class AbstractRectangularPlot<X extends Axis, Y extends Axis> extends Plot {
    private X xAxis;
    private Y yAxis;

    public AbstractRectangularPlot(X xAxis, Y yAxis) {
        this.xAxis = Objects.requireNonNull(xAxis);
        this.yAxis = Objects.requireNonNull(yAxis);
    }


    public final X getXAxis() {
        return xAxis;
    }

    public final Y getYAxis() {
        return yAxis;
    }


    @Override
    protected void layoutPlot(double x, double y, double width, double height) {
        //TODO
    }

    protected abstract void layoutSeries(double x, double y, double width, double height);

    @Override
    protected void layoutAxes() {
        layoutAxis(xAxis, Orientation.HORIZONTAL);
        layoutAxis(yAxis, Orientation.VERTICAL);
    }
}
