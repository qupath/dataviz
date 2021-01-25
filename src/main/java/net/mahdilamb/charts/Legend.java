package net.mahdilamb.charts;

import net.mahdilamb.charts.styles.Orientation;
import net.mahdilamb.charts.styles.Side;
import net.mahdilamb.charts.styles.Stroke;
import net.mahdilamb.charts.styles.Title;

public final class Legend {
    private final Chart<?> chart;
    double borderPadding = 0, width, height, hGap = 2, vGap = 2, yOffset;
    private Title title;
    private boolean isFloating = false, visible = true;
    private Side side = Side.LEFT;
    private Orientation layoutOrientation = Orientation.VERTICAL;
    private Stroke border;


    Legend(Chart<?> chart) {
        this.chart = chart;

    }

    /**
     * @return the horizontal gap between legend items
     */
    public double getHorizontalGap() {
        return hGap;
    }

    /**
     * @return the vertical gap between legend items
     */
    public double getVerticalGap() {
        return vGap;
    }

    /**
     * @return the border of the legend
     */
    public Stroke getBorder() {
        return border;
    }

    /**
     * @return the padding between the content and the border
     */
    public double getBorderPadding() {
        return borderPadding;
    }

    /**
     * @return the title of the legend. May be {@code null}
     */
    public Title getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title.setTitle(title);
        chart.layout();
    }

    /**
     * @return the side of the chart this legend is on.
     */
    public Side getSide() {
        return side;
    }

    /**
     * @return whether the legend takes up space in the layout or appears over the plot are
     */
    public boolean isFloating() {
        return isFloating;
    }

    public boolean isVisible() {
        return visible;
    }

    void layout(double yOffset) {
        if (title != null && title.isVisible()) {

        }
        for (final LegendItem legendItem : chart.plot.getLegendItems()) {

        }
        //TODO
    }
}
