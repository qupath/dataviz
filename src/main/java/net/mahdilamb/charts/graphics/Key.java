package net.mahdilamb.charts.graphics;

import net.mahdilamb.charts.Title;

public interface Key {
    /**
     * @return the border of the legend
     */
    Stroke getBorder();

    /**
     * @return the padding between the content and the border
     */
    double getPadding();

    /**
     * @return the spacing outside the border
     */
    double getMargin();

    /**
     * @return the title of the legend. May be {@code null}
     */
    Title getTitle();

    void setTitle(String title);

    /**
     * @return the side of the chart this legend is on.
     */
    Side getSide();

    /**
     * @return whether the legend takes up space in the layout or appears over the plot are
     */
    boolean isFloating();

    boolean isVisible();

    /**
     * @return the horizontal gap between legend items
     */
    double getHorizontalGap();

    /**
     * @return the vertical gap between legend items
     */
    double getVerticalGap();

}
