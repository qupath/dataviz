package net.mahdilamb.charts;

import net.mahdilamb.charts.graphics.*;
import net.mahdilamb.colormap.Color;

public class Axis extends ChartComponent {
    public enum Mode {
        LINEAR,
        LOGICAL,
        CATEGORICAL
    }

    public enum AutoRange {
        FULL,
        TO_ZERO,
        NON_NEGATIVE
    }

    Mode mode = Mode.LINEAR;
    protected double minorTickSpacing = 2,
            majorTickSpacing = 10;

    boolean showMinorTicks = true,
            showMajorTicks = true,
            showLabels = true,
            showMajorGridLines = true,
            showMinorGridLines = true,
            showZeroLine = true;
    boolean majorTicksInside = false,
            minorTicksInside = false;
    double majorTickLength = 5,
            minorTickLength = 2;

    double lowerLimit = Double.NEGATIVE_INFINITY, upperLimit = Double.POSITIVE_INFINITY;
    boolean reversed = false;
    boolean autoRanged = true;
    AutoRange rangeMode = AutoRange.FULL;
    double currentLowerBound, currentUpperBound;

    Title title;
    double titlePadding = 5;

    Stroke majorGridStroke = new Stroke(Color.white, 2.5),
            minorGridStroke = new Stroke(Color.white, 1),
            zeroGridStroke = new Stroke(Color.white, 3);

    Stroke majorLineStroke = new Stroke(Color.black, 2.5),
            minorLineStroke = new Stroke(Color.black, 1),
            axisStroke = null;

    Font labelFont = Font.DEFAULT_FONT;
    double labelPadding = 2;
    double labelRotation = 0;
    Boundary labelPosition;
    HAlign labelAlignment;

    double[] tickPositions;
    String[] tickLabels;

    double scale;
    Side side;

    /**
     * Create a new axis with the given initial min and max
     *
     * @param label the title of the axis
     * @param min   the initial min of the range
     * @param max   the initial max of the range
     */
    public Axis(final String label, double min, double max) {
        this.title = new Title(label, new Font(Font.Family.SANS_SERIF, 18));
        this.currentLowerBound = min;
        this.currentUpperBound = max;

    }

    public Axis setAxisMode(final Mode mode) {
        this.mode = mode;
        return redraw();
    }

    public Axis setTitle(String newTitle) {
        title.setTitle(newTitle);
        return redraw();
    }

    public Axis setTitleFont(final Font font) {
        title.setFont(font);
        return redraw();
    }

    public Axis setTitleColor(final Color color) {
        title.color = color;
        return redraw();
    }

    public Axis showTitle(boolean visible) {
        title.setVisible(visible);
        return redraw();
    }

    public Axis setMajorGridStroke(final Stroke stroke) {
        majorGridStroke = stroke;
        return redraw();
    }

    public Axis setMajorTickStroke(final Stroke stroke) {
        majorLineStroke = stroke;
        return redraw();
    }

    public Axis showMajorTicks(boolean visible) {
        this.showMajorTicks = visible;
        return redraw();
    }

    public Axis setMajorTickSpacing(double majorTickSpacing) {
        tickLabels = null;
        tickPositions = null;
        minorTickSpacing = Math.min(minorTickSpacing, majorTickSpacing);
        this.majorTickSpacing = majorTickSpacing;
        return redraw();
    }

    public Axis setMajorTickLabels(double[] tickPositions, String[] tickLabels) {
        this.tickPositions = tickPositions;
        this.tickLabels = tickLabels;
        return redraw();
    }

    public Axis setMajorTicksInside() {
        majorTicksInside = true;
        return redraw();
    }

    public Axis setMajorTicksOutside() {
        majorTicksInside = false;
        return redraw();
    }

    public Axis showMajorGridLines(boolean visible) {
        showMajorGridLines = visible;
        return redraw();
    }

    public Axis setMinorGridStroke(final Stroke stroke) {
        minorGridStroke = stroke;
        return redraw();
    }

    public Axis setMinorTickStroke(final Stroke stroke) {
        minorLineStroke = stroke;
        return redraw();
    }

    public Axis showMinorGridLines(boolean visible) {
        showMinorGridLines = visible;
        return redraw();
    }

    public Axis showZeroLine(boolean visible) {
        showZeroLine = visible;
        return redraw();
    }

    public Axis setZeroLineStroke(final Stroke stroke) {
        this.zeroGridStroke = stroke;
        return redraw();
    }

    public Axis setMinorTicksInside() {
        minorTicksInside = true;
        return redraw();
    }

    public Axis setMinorTicksOutside() {
        minorTicksInside = false;
        return redraw();
    }

    public Axis showMinorTicks(boolean visible) {
        this.showMinorTicks = visible;
        return redraw();
    }

    public Axis setLabelAlignment(final HAlign alignment) {
        this.labelAlignment = alignment;
        return redraw();
    }

    public Axis setLabelPosition(final Boundary boundary) {
        this.labelPosition = boundary;
        return redraw();
    }

    public Axis setLabelFont(final Font font) {
        this.labelFont = font;
        return redraw();
    }

    public Axis setLabelRotation(final double rotationDegrees) {
        this.labelRotation = rotationDegrees;
        return redraw();
    }

    public Axis showLabels(boolean visible) {
        showLabels = visible;
        return redraw();
    }

    public Axis setAxisStroke(final Stroke stroke) {
        this.axisStroke = stroke;
        return redraw();
    }

    public Axis setRange(double lowerBound, double upperBound) {
        if (!Double.isFinite(lowerBound) || !Double.isFinite(upperBound)) {
            autoRanged = true;
            lowerLimit = Double.NEGATIVE_INFINITY;
            upperLimit = Double.POSITIVE_INFINITY;
            return redraw();
        }
        autoRanged = false;
        this.lowerLimit = lowerBound;
        this.upperLimit = upperBound;
        return redraw();
    }

    /**
     * Get the label from a value
     *
     * @param val the value
     * @return the label at the value
     */
    protected String getLabel(double val) {
        return null;//TODO
    }

    protected Axis redraw() {
        return (Axis) super.redraw();
    }

    @Override
    protected void draw(Figure<?, ?> source, ChartCanvas<?> canvas, double minX, double minY, double maxX, double maxY) {
        //TODO
    }

    @Override
    protected void layout(Figure<?, ?> source, ChartCanvas<?> canvas, double minX, double minY, double maxX, double maxY) {
        //TODO
    }

    @Override
    public String toString() {
        return String.format("Axis between %.3f and %.3f", currentLowerBound, currentUpperBound);
    }
}
