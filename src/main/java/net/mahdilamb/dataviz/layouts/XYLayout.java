package net.mahdilamb.dataviz.layouts;

import net.mahdilamb.dataviz.*;
import net.mahdilamb.dataviz.figure.Renderer;
import net.mahdilamb.dataviz.utils.functions.BiDoubleConsumer;

/**
 * An XY or "rectangular" layout
 */
public final class XYLayout extends PlotLayout<XYLayout> {
    protected final PlotAxis<XYLayout> xAxis, yAxis, secondaryXAxis, secondaryYAxis;
    /**
     * The plot area
     */
    protected final RectanglePlotArea plotArea = new RectanglePlotArea(this);
    /*
     * Bounds for when resetting the display
     */
    double homeMinX = Double.NEGATIVE_INFINITY, homeMinY = Double.NEGATIVE_INFINITY, homeMaxX = Double.POSITIVE_INFINITY, homeMaxY = Double.POSITIVE_INFINITY;

    /*
     * The maximum allowed bounds
     */
    double minX = Double.NEGATIVE_INFINITY, minY = Double.NEGATIVE_INFINITY, maxX = Double.POSITIVE_INFINITY, maxY = Double.POSITIVE_INFINITY;


    public XYLayout(final PlotAxis<XYLayout> xAxis, final PlotAxis<XYLayout> yAxis) {
        super();
        this.xAxis = xAxis;
        this.yAxis = yAxis;
        this.secondaryXAxis = PlotAxis.emptyAxis();
        this.secondaryYAxis = PlotAxis.emptyAxis();
        add(plotArea);
        addAxes(xAxis, yAxis);
    }

    public XYLayout(final PlotAxis<XYLayout> xAxis, final PlotAxis<XYLayout> yAxis, final PlotAxis<XYLayout> xAxis2, final PlotAxis<XYLayout> yAxis2) {
        super();
        this.xAxis = xAxis;
        this.yAxis = yAxis;
        this.secondaryXAxis = xAxis2;
        this.secondaryYAxis = yAxis2;
        add(plotArea);
        addAxes(xAxis, yAxis, xAxis2, yAxis2);
    }

    public XYAxis.XAxis getXAxis() {
        return (XYAxis.XAxis) xAxis;
    }

    public XYAxis.YAxis getYAxis() {
        return (XYAxis.YAxis) yAxis;
    }

    @Override
    protected <T> void layoutComponent(Renderer<T> renderer, double minX, double minY, double maxX, double maxY) {
        setBoundsFromExtent(minX, minY, maxX, maxY);

        if (title != null) {
            //TODO deal with title
        }
        layout(yAxis, renderer, minX, minY, maxX, maxY);
        layout(xAxis, renderer, minX, minY, maxX, maxY);
        layout(plotArea, renderer, minX + yAxis.getWidth(), minY, maxX, maxY - xAxis.getHeight());
        getXAxis().updateScale();
        getYAxis().updateScale();
    }


    @Override
    protected void onAdd(PlotData<XYLayout> data) {
        updateHomeBounds(getHomeMinX(data), getHomeMinY(data), getHomeMaxX(data), getHomeMaxY(data));
        setRange(homeMinX, homeMinY, homeMaxX, homeMaxY);
    }

    public void setRange(double minX, double minY, double maxX, double maxY) {
        getXAxis().lower = Math.max(minX, this.minX);
        getXAxis().upper = Math.min(maxX, this.maxX);
        getYAxis().lower = Math.max(minY, this.minY);
        getYAxis().upper = Math.min(maxY, this.maxY);
        relayout();
    }

    @Override
    protected void panPlotArea(double dx, double dy) {
        final double xMin = getXAxis().lower - dx / getXAxis().scale,
                yMin = getYAxis().lower + dy / getYAxis().scale,
                xRange = getXAxis().upper - getXAxis().lower,
                yRange = getYAxis().upper - getYAxis().lower;

        setRange(xMin, yMin, xMin + xRange, yMin + yRange);
    }

    @Override
    protected void zoomPlotArea(double ex, double ey, double zoom) {
        final double scaleFactor = 1 + zoom;
        //zoom into current mouse position by the an amount proportionate to the scroll amount
        double minX = getXAxis().lower;
        double maxX = getXAxis().upper;
        double minY = getYAxis().lower;
        double maxY = getYAxis().upper;
        if (!getYAxis().containsPoint(ex, ey)) {
            final double px = getXAxis().getValueFromPosition(ex);
            double newXRange = getXAxis().range * scaleFactor;
            double left = (ex - plotArea.getX()) * newXRange / plotArea.getWidth();
            minX = px - left;
            maxX = px + newXRange - left;
        }
        if (!getXAxis().containsPoint(ex, ey)) {
            final double py = getYAxis().getValueFromPosition(ey);
            double newYRange = getYAxis().range * scaleFactor;
            double top = (ey - plotArea.getY()) * newYRange / plotArea.getHeight();
            minY = top + py - newYRange;
            maxY = top + py;
        }
        setRange(minX, minY, maxX, maxY);
        clearCache();
    }

    @Override
    public void transformValueToPosition(double x, double y, BiDoubleConsumer xy) {
        xy.accept(
                getXAxis().getPositionFromValue(x),
                getYAxis().getPositionFromValue(y)
        );
    }

    @Override
    public void transformPositionToValue(double x, double y, BiDoubleConsumer xy) {
        xy.accept(
                getXAxis().getValueFromPosition(x),
                getYAxis().getValueFromPosition(y)
        );
    }

    @Override
    public final PlotArea<XYLayout> getPlotArea() {
        return plotArea;
    }

    @Override
    public void increaseZoom() {
        zoomPlotArea(plotArea.getX() + plotArea.getWidth() * .5, plotArea.getY() + plotArea.getHeight() * .5, -.25);
    }

    @Override
    public void decreaseZoom() {
        zoomPlotArea(plotArea.getX() + plotArea.getWidth() * .5, plotArea.getY() + plotArea.getHeight() * .5, 1d / 3);

    }

    @Override
    protected void inputModeChanged(InputMode.State state) {
        clearCache();
        redraw();
    }

    private void updateHomeBounds(double minX, double minY, double maxX, double maxY) {
        this.homeMinX = Double.isFinite(this.homeMinX) ? Math.min(minX, this.homeMinX) : minX;
        this.homeMinY = Double.isFinite(this.homeMinY) ? Math.min(minY, this.homeMinY) : minY;
        this.homeMaxX = Double.isFinite(this.homeMaxX) ? Math.max(maxX, this.homeMaxX) : maxX;
        this.homeMaxY = Double.isFinite(this.homeMaxY) ? Math.max(maxY, this.homeMaxY) : maxY;
        //TODO
        getXAxis().reset(true, homeMinX, homeMaxX);
        getYAxis().reset(true, homeMinY, homeMaxY);
    }

    private void updateBounds(double minX, double minY, double maxX, double maxY) {
        this.minX = Double.isFinite(this.minX) ? Math.min(minX, this.minX) : minX;
        this.minY = Double.isFinite(this.minY) ? Math.min(minY, this.minY) : minY;
        this.maxX = Double.isFinite(this.maxX) ? Math.max(maxX, this.maxX) : maxX;
        this.maxY = Double.isFinite(this.maxY) ? Math.max(maxY, this.maxY) : maxY;

    }


}
