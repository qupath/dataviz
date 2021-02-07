package net.mahdilamb.charts.graphics;

/**
 * The style modifications to use when a marker is selected
 */
public class SelectedStyle extends Style {
    /**
     * The default selected style
     */
    public static final SelectedStyle DEFAULT_SELECTED_STYLE = new UnmodifiableSelectedStyle(1, true);

    private static final class UnmodifiableSelectedStyle extends SelectedStyle {
        public UnmodifiableSelectedStyle(double alpha, boolean showEdges) {
            this.opacity = alpha;
            this.showEdges = showEdges;
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
}
