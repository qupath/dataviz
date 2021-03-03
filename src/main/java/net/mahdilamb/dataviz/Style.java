package net.mahdilamb.dataviz;

/**
 * A simple style that contains fields related to how the source style should change
 */
public class Style {
    /**
     * The proportion of the original opacity
     */
    protected double opacity = 1;
    /**
     * To {@literal &}= with the current show edges
     */
    protected boolean showEdges = true;

    protected Style() {

    }

    /**
     * Set whether to enable showing edges
     *
     * @param showEdges the new value
     */
    public void showEdges(boolean showEdges) {
        this.showEdges = showEdges;
    }

    /**
     * Set the proportion of opacity to keep
     *
     * @param opacity the new opacity proportion
     */
    public void setOpacity(double opacity) {
        this.opacity = opacity;
    }
}
