package net.mahdilamb.charts.plots;

import net.mahdilamb.charts.graphics.Stroke;
import net.mahdilamb.colormap.Color;
import net.mahdilamb.colormap.Colormap;

public final class Area extends AbstractScatter<Area> {
    boolean showEdges = true;
    boolean stacked = false;

    public Area(String name, double[] x, double[] y) {
        super(name, x, y);
    }

    public Area showEdges(final boolean edgeVisible) {
        showEdges = edgeVisible;
        return redraw();
    }

    public Area setStacked(boolean stacked) {
        this.stacked = stacked;
        // todo recalculate
        return redraw();
    }
}
