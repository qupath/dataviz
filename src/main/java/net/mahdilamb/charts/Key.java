package net.mahdilamb.charts;

import net.mahdilamb.charts.styles.Orientation;
import net.mahdilamb.charts.styles.Side;
import net.mahdilamb.charts.styles.Stroke;
import net.mahdilamb.charts.styles.Title;

/**
 * A class representing meta information for the chart (e.g. Legend and Colorbar)
 */
class Key {
    final Chart<?, ?> chart;
    Title title;
    boolean isFloating = false, visible = true;
    double borderPadding = 0;
    Side side = Side.LEFT;
    Orientation layoutOrientation = Orientation.VERTICAL;
    Stroke border;

    Key(Chart<?, ?> chart) {
        this.chart = chart;

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
}
