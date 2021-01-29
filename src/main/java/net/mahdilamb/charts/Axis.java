package net.mahdilamb.charts;

import net.mahdilamb.charts.axes.LinearAxis;

public abstract class Axis {
    /**
     * Create a new linear axis with the given initial min and max
     *
     * @param min the initial min of the range
     * @param max the initial max of the range
     * @return a linear axis
     */
    public static Axis linear(double min, double max) {
        return new LinearAxis(min, max);
    }

    /**
     * Create a new linear axis that defaults to the data range
     *
     * @return a linear axis
     */
    public static Axis linear() {
        return new LinearAxis(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    double minExtent, maxExtent, currentMin, currentMax;
    Title title;

    Chart<?,?> chart;

    protected Axis(double min, double max) {
        currentMin = this.minExtent = min;
        currentMax = this.maxExtent = max;

    }

    /**
     * @return the minimum possible value in this axis
     */
    protected double getLowerBound() {
        return minExtent;
    }

    /**
     * @return the maximum possible value in this axis.
     */
    protected double getUpperBound() {
        return maxExtent;
    }


    /**
     * Get the label from a value
     *
     * @param val the value
     * @return the label at the value
     */
    protected abstract String getLabel(double val);

    /**
     * Get an iterable over the major tick marks. The first element will be
     * the min. The last element will be max. The axis may not be checked for correct
     * bounds
     *
     * @param min     the position of the minimum major tick
     * @param max     the position of the last major tick
     * @param spacing the requested spacing between the min and max
     * @return an iterable of the major ticks
     */
    protected abstract Iterable<Double> ticks(double min, double max, double spacing);


}
