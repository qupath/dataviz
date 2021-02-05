package net.mahdilamb.charts.graphics;

public class SelectedStyle extends Style {
    public static final SelectedStyle DEFAULT_SELECTED_STYLE = new UnmodifiableSelectedStyle(1, true);

    private static final class UnmodifiableSelectedStyle extends SelectedStyle {
        public UnmodifiableSelectedStyle(double alpha, boolean showEdges) {
            this.alpha = alpha;
            this.showEdges = showEdges;
        }
    }
}
