package net.mahdilamb.charts;

import net.mahdilamb.charts.graphics.*;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A class representing meta information for the chart (e.g. Legend and Colorbar)
 */
abstract class KeyImpl<T extends KeyImpl.KeyItem> implements Key {
    final Chart<?, ?> chart;
    Title title;
    boolean isFloating = false, visible = true;
    double padding = 0, margin = 10, hGap = 2, vGap = 2, markerPadding = 2;
    Side side = Side.RIGHT;
    Stroke border = Stroke.BLACK_STROKE;
    Font labelFont = new Font(Font.Family.SANS_SERIF, 12);

    Map<PlotSeries<?, ?>, T> items = new LinkedHashMap<>();

    boolean isDirty = true;
    double cellHeight, cellWidth;

    double renderHeight, renderWidth;

    KeyImpl(Chart<?, ?> chart) {
        this.chart = chart;

    }

    protected abstract void layout(ChartCanvas<?> canvas, double minX, double minY, double maxX, double maxY) ;


    static abstract class KeyItem {
        double labelWidth, itemHeight;

    }

    @Override
    public Stroke getBorder() {
        return border;
    }

    @Override
    public double getPadding() {
        return padding;
    }

    @Override
    public double getMargin() {
        return margin;
    }

    @Override
    public Title getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title.setTitle(title);
        chart.layout();
    }

    @Override
    public Side getSide() {
        return side;
    }

    @Override
    public boolean isFloating() {
        return isFloating;
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public double getHorizontalGap() {
        return hGap;
    }

    @Override
    public double getVerticalGap() {
        return vGap;
    }

}
