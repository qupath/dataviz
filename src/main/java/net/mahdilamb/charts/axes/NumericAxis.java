package net.mahdilamb.charts.axes;

import net.mahdilamb.charts.Axis;
//TODO 10x-9 formatting

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

}
