package net.mahdilamb.charts;

import net.mahdilamb.charts.axes.LinearAxis;
import net.mahdilamb.charts.axes.NumericAxis;
import net.mahdilamb.charts.graphics.*;
import net.mahdilamb.colormap.Color;

public abstract class Axis extends ChartComponent {


    double scale;

    enum AxisType {
        X,
        Y,
        RADIAL,
        ANGULAR,
    }

    /**
     * Create a new linear axis with the given initial min and max
     *
     * @param min the initial min of the range
     * @param max the initial max of the range
     * @return a linear axis
     */
    public static NumericAxis linear(final String title, double min, double max) {
        return new LinearAxis(title, min, max);
    }

    /**
     * Create a new linear axis that defaults to the data range
     *
     * @return a linear axis
     */
    public static NumericAxis linear() {
        return new LinearAxis("", Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    protected double minorTickSpacing = 2, majorTickSpacing = 10;
    double labelPadding = 2;
    boolean showMinorTicks = true, showMajorTicks = true, showLabels = true, showMajorGridLines = true, showMinorGridLines = true;
    double majorTickLength = 5, minorTickLength = 2;
    double lowerBound, upperBound;
    Title title;
    Font labelFont = Font.DEFAULT_FONT;
    AxisType type;
    Stroke majorStroke = new Stroke(Color.white, 2.5), minorStroke = new Stroke(Color.white, 1);

    @Override
    public String toString() {
        return String.format("%s between %.3f and %.3f", getClass().getSimpleName(), getLowerBound(), getUpperBound());
    }

    protected Axis(final String label, double min, double max) {
        //todo
        this.title = new Title(label, new Font(Font.Family.SANS_SERIF, 18));
        this.lowerBound = min;
        this.upperBound = max;


    }

    /**
     * @return the minimum possible value in this axis
     */
    protected double getLowerBound() {
        return lowerBound;
    }

    /**
     * @return the maximum possible value in this axis.
     */
    protected double getUpperBound() {
        return upperBound;
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


    public void setMajorTickSpacing(double majorTickSpacing) {
        minorTickSpacing = Math.min(minorTickSpacing, majorTickSpacing);
        this.majorTickSpacing = majorTickSpacing;
        requestLayout();
    }



    @Override
    protected void layout(ChartCanvas<?> canvas, Chart<?,?> source, double minX, double minY, double maxX, double maxY) {
        switch (type) {
            case X:
                return;
            case Y:
                return;
            default:
                throw new UnsupportedOperationException();
        }
    }

    @Override
    protected void calculateBounds(ChartCanvas<?> canvas, Chart<?, ?> source, double minX, double minY, double maxX, double maxY) {
        //TODO
    }

}
