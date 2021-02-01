package net.mahdilamb.charts.axes;

import net.mahdilamb.charts.Axis;
//TODO 10x-9 formatting

/**
 * A numeric axis
 */
public abstract class NumericAxis extends Axis {
    protected NumericAxis(final String title, double min, double max) {
        super(title, min, max);
    }

    /**
     * @return the number of decimal points to show
     */
    protected String getFormatting() {
        return "%." + 2 + "f";
    }

    @Override
    protected String getLabel(double val) {
        return String.format(getFormatting(), val);
    }

    public void setMinorTickSpacing(double minorTickSpacing) {
        this.minorTickSpacing = minorTickSpacing;
        requestLayout();
    }
}
