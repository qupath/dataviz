package net.mahdilamb.charts;

import net.mahdilamb.charts.axes.LinearAxis;
import net.mahdilamb.charts.axes.NumericAxis;
import net.mahdilamb.charts.graphics.*;
import net.mahdilamb.charts.layouts.XYPlot;
import net.mahdilamb.charts.utils.StringUtils;
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
        this.title = new Title(label, new Font(Font.Family.SANS_SERIF, 18), Alignment.CENTER);
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

    void drawXAxis(Chart<?, ?> chart, ChartCanvas<?> canvas) {
        this.scale = boundsWidth / (upperBound - lowerBound);
        String labelLength = StringUtils.longerString(getLabel(lowerBound), getLabel(upperBound));
        double labelWidth = chart.getTextWidth(labelFont, labelLength);
        double textOffset = chart.getTextBaselineOffset(labelFont);
        canvas.setFont(labelFont);
        canvas.setFill(Fill.BLACK_FILL);//TODO

        double lastMark = Double.NaN;
        if (showMajorGridLines) {
            for (final double m : ticks(((long) lowerBound), ((long) upperBound + 1), majorTickSpacing)) {
                if (m > lowerBound) {
                    double p = (m - lowerBound) * scale;
                    if (p > boundsWidth) {
                        break;
                    }
                    canvas.setStroke(majorStroke);
                    canvas.strokeLine(boundsX + p, boundsY, boundsX + p, boundsY + majorTickLength);
                    if (showMajorGridLines) {
                        canvas.strokeLine(boundsX + p, boundsY, boundsX + p, boundsY - ((XYPlot<?>) chart.getPlot()).getYAxis().boundsHeight);

                    }
                    final String label = getLabel(m); //todo pad to max width
                    canvas.fillText(label, boundsX + p - labelWidth * .5, boundsY + majorTickLength + textOffset);

                    if (!Double.isNaN(lastMark)) {
                        if (showMinorGridLines) {
                            canvas.setStroke(minorStroke);
                            for (final double n : ticks(lastMark + minorTickSpacing, m - minorTickSpacing, minorTickSpacing)) {
                                if (n < lowerBound) {
                                    continue;
                                }
                                double q = (n - lowerBound) * scale;
                                if (q > boundsWidth) {
                                    break;
                                }
                                canvas.strokeLine(boundsX + q, boundsY, boundsX + q, boundsY + minorTickLength);
                                if (showMinorGridLines) {
                                    canvas.strokeLine(boundsX + q, boundsY, boundsX + q, boundsY - ((XYPlot<?>) chart.getPlot()).getYAxis().boundsHeight);

                                }
                                final String labelMin = getLabel(n); //todo pad to max width
                                canvas.fillText(labelMin, boundsX + q - labelWidth * .5, boundsY + minorTickLength + textOffset + labelPadding);
                            }
                        }
                    }
                }

                lastMark = m;
            }
            if (showMinorGridLines) {
                for (final double n : ticks(lastMark + minorTickSpacing, upperBound, minorTickSpacing)) {
                    double q = (n - lowerBound) * scale;
                    if (q > boundsWidth) {
                        break;
                    }
                    canvas.setStroke(minorStroke);
                    canvas.strokeLine(boundsX + q, boundsY, boundsX + q, boundsY + minorTickLength);
                    if (showMinorGridLines) {
                        canvas.strokeLine(boundsX + q, boundsY, boundsX + q, boundsY - ((XYPlot<?>) chart.getPlot()).getYAxis().boundsHeight);

                    }
                    final String labelMin = getLabel(n); //todo pad to max width
                    canvas.fillText(labelMin, boundsX + q - labelWidth * .5, boundsY + minorTickLength + textOffset);
                }
            }
        }
        if (title.isVisible()) {
            double titleOffset = chart.getTextBaselineOffset(title.getFont());
            double textWidth = chart.getTextWidth(title.getFont(), title.getText());
            canvas.setFont(title.getFont());
            canvas.fillText(title.getText(), boundsX + boundsWidth * .5 - textWidth * .5, boundsY + majorTickSpacing + labelPadding + titleOffset + textOffset);//todo check major tick showing

        }
    }

    @Override
    protected void layout(Chart<?, ?> chart, ChartCanvas<?> canvas, double minX, double minY, double maxX, double maxY) {
        switch (type) {
            case X:
                drawXAxis(chart, canvas);
                return;
            case Y:
                drawYAxis(chart, canvas);
                return;
            default:
                throw new UnsupportedOperationException();
        }
    }

    protected void drawYAxis(Chart<?, ?> chart, ChartCanvas<?> canvas) {
        //todo correct for when it doesn't start at .0
        this.scale = boundsHeight / (upperBound - lowerBound);
        String labelLength = StringUtils.longerString(getLabel(lowerBound), getLabel(upperBound));
        double labelWidth = chart.getTextWidth(labelFont, labelLength);
        double textOffset = chart.getTextBaselineOffset(labelFont);
        canvas.setFont(labelFont);
        canvas.setFill(Fill.BLACK_FILL);//TODO
        double lastMark = Double.NaN;
        final double endX = boundsX + boundsWidth;
        if (showMajorGridLines) {
            for (final double m : ticks(lowerBound, upperBound, majorTickSpacing)) {
                double p = (m - lowerBound) * scale;
                if (p > boundsHeight) {
                    break;
                }
                canvas.setStroke(majorStroke);
                canvas.strokeLine(endX - majorTickLength, boundsY + boundsHeight - p, endX, boundsY + boundsHeight - p);
                if (showMajorGridLines) {
                    canvas.strokeLine(endX, boundsY + boundsHeight - p, endX + ((XYPlot<?>) chart.getPlot()).getXAxis().boundsWidth, boundsY + boundsHeight - p);
                }
                final String label = getLabel(m); //todo pad to max width
                canvas.fillText(label, endX - majorTickLength - labelWidth - labelPadding, boundsY + boundsHeight - p + textOffset * .5);
                if (!Double.isNaN(lastMark)) {
                    if (showMinorGridLines) {
                        canvas.setStroke(minorStroke);
                        for (final double n : ticks(lastMark + minorTickSpacing, m - minorTickSpacing, minorTickSpacing)) {
                            double q = (n - lowerBound) * scale;
                            if (q > boundsHeight) {
                                break;
                            }

                            canvas.strokeLine(endX - minorTickLength, boundsY + boundsHeight - q, endX, boundsY + boundsHeight - q);
                            if (showMinorGridLines) {
                                canvas.strokeLine(endX, boundsY + boundsHeight - q, endX + ((XYPlot<?>) chart.getPlot()).getXAxis().boundsWidth, boundsY + boundsHeight - q);

                            }
                            final String labelMin = getLabel(n); //todo pad to max width
                            canvas.fillText(labelMin, endX - minorTickLength - labelWidth - labelPadding, boundsY + boundsHeight - q + textOffset * .5);
                        }
                    }
                }
                lastMark = m;
            }
            if (showMinorGridLines) {
                canvas.setStroke(minorStroke);
                for (final double n : ticks(lastMark + minorTickSpacing, upperBound - minorTickSpacing, minorTickSpacing)) {
                    double q = (n - lowerBound) * scale;
                    if (q > boundsHeight) {
                        break;
                    }

                    canvas.strokeLine(endX - minorTickLength, boundsY + boundsHeight - q, endX, boundsY + boundsHeight - q);
                    if (showMinorGridLines) {
                        canvas.strokeLine(endX, boundsY + boundsHeight - q, endX + ((XYPlot<?>) chart.getPlot()).getXAxis().boundsWidth, boundsY + boundsHeight - q);

                    }
                    final String labelMin = getLabel(n); //todo pad to max width
                    canvas.fillText(labelMin, endX - minorTickLength - labelWidth - labelPadding, boundsY + boundsHeight - q + textOffset * .5);
                }
            }
        }
        if (title.isVisible()) {
            double lineHeight = chart.getTextLineHeight(title.getFont());
            double titleWidth = chart.getTextWidth(title.getFont(), title.getText());
            canvas.setFont(title.getFont());
            double x = endX - majorTickLength - (titleWidth * .5) - labelWidth; //todo check
            double y = boundsY + boundsHeight * .5 - lineHeight * .5;
            canvas.fillText(title.getText(), x, y, -90, x + titleWidth * .5, y + lineHeight * .5);//todo check major tick showing

        }
    }
}
