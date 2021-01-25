package net.mahdilamb.charts.axes;

import net.mahdilamb.charts.Axis;
import net.mahdilamb.charts.styles.Orientation;

/**
 * A numeric axis
 */
public abstract class NumericAxis extends Axis {
    protected NumericAxis(double min, double max) {
        super(min, max);
    }

    /**
     * @return the number of decimal points to show
     */
    protected double getDecimalFormat() {
        return 2;
    }

    @Override
    protected String getLabel(double val) {
        return String.format("%" + getDecimalFormat() + "f", val);
    }

    @Override
    protected void layout(Orientation orientation) {
        //TODO
    }
}
