package net.mahdilamb.charts.plots;

/**
 * Interface for rectangular plots
 */
public interface RectangularPlot {
    /**
     * @return the label for the x dimension
     */
    String getXLabel();

    /**
     * @return the label for the y dimension
     */
    String getYLabel();

    /**
     * @return the minimum x value
     */
    double getMinX();

    /**
     * @return the maximum x value
     */
    double getMaxX();

    /**
     * @return the minimum y value
     */
    double getMinY();

    /**
     * @return the maximum y value
     */
    double getMaxY();

    default MarginalMode getMarginalX() {
        return MarginalMode.NONE;
    }

    default MarginalMode getMarginalY() {
        return MarginalMode.NONE;
    }
}
