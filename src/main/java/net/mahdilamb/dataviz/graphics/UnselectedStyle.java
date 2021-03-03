package net.mahdilamb.dataviz.graphics;

import net.mahdilamb.dataviz.Style;

/**
 * The style to use when a marker is unselected
 */
public class UnselectedStyle extends Style {
    /**
     * The default unselected style
     */
    public static final UnselectedStyle DEFAULT_UNSELECTED_STYLE = new UnmodifiableUnselectedStyle(0.2, false);

    public UnselectedStyle() {

    }

    /**
     * An unmodifiable version of an unselected style
     */
    private static final class UnmodifiableUnselectedStyle extends UnselectedStyle {
        public UnmodifiableUnselectedStyle(double alpha, boolean showEdges) {
            this.opacity = alpha;
            this.showEdges = showEdges;
        }
    }

    @Override
    public void showEdges(boolean showEdges) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setOpacity(double opacity) {
        throw new UnsupportedOperationException();
    }
}
