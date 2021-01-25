package net.mahdilamb.charts;

import net.mahdilamb.charts.styles.Orientation;
import net.mahdilamb.charts.styles.Text;
import net.mahdilamb.charts.styles.Title;

public abstract class Axis {
    double minExtent, maxExtent, currentMin, currentMax;
    Title title;

    Chart<?> chart;

    protected Axis(double min, double max) {
        currentMin = this.minExtent = min;
        currentMax = this.maxExtent = max;

    }

    /**
     * @return the minimum possible value in this axis
     */
    protected double getMinExtent() {
        return minExtent;
    }

    /**
     * @return the maximum possible value in this axis.
     */
    protected double getMaxExtent() {
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

    protected abstract void layout(Orientation orientation);

    protected double getTextHeight(Text text){
        return chart.getTextHeight(text);
    }
    protected double getTextWidth(Text text){
        return chart.getTextWidth(text);
    }

}
