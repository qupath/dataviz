package net.mahdilamb.charts.graphics;

public class UnselectedStyle extends Style {
    public static final UnselectedStyle DEFAULT_UNSELECTED_STYLE = new UnmodifiableUnselectedStyle(0.8, false);

    public UnselectedStyle() {

    }

    //TODO
    private static final class UnmodifiableUnselectedStyle extends UnselectedStyle {
        public UnmodifiableUnselectedStyle(double alpha, boolean showEdges) {
            this.alpha = alpha;
            this.showEdges = showEdges;
        }
    }
}
