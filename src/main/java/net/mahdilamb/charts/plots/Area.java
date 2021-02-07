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

    @Override
    public Area setGroupName(int group, String name) {
        return super.setGroupName(group, name);
    }

    @Override
    public Area setGroupColormap(Colormap colormap) {
        return super.setGroupColormap(colormap);
    }

    @Override
    public Area setGroupColor(int group, Color color) {
        return super.setGroupColor(group, color);
    }

    @Override
    public Area setGroupStroke(int group, Stroke color) {
        return super.setGroupStroke(group, color);
    }

    @Override
    public Area setGroupLine(int group, Stroke line) {
        return super.setGroupLine(group, line);
    }

    @Override
    public Area setGroupName(String group, String name) {
        return super.setGroupName(group, name);
    }

    @Override
    public Area setGroupColor(String group, Color color) {
        return super.setGroupColor(group, color);
    }

    @Override
    public Area setGroupStroke(String group, Stroke color) {
        return super.setGroupStroke(group, color);
    }

    @Override
    public Area setGroupLine(String group, Stroke line) {
        return super.setGroupLine(group, line);
    }

    @Override
    public Area setColors(String name, Iterable<String> groups) {
        return super.setColors(name, groups);
    }

    public Area showEdges(final boolean edgeVisible) {
        showEdges = edgeVisible;
        return requestLayout();
    }

    public Area setStacked(boolean stacked) {
        this.stacked = stacked;
        // todo recalculate
        return requestLayout();
    }
}
