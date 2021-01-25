package net.mahdilamb.charts.styles;

import net.mahdilamb.colormap.RGBA;

public class Stroke {
    private final double width;
    private final RGBA color;

    Stroke(final RGBA color, final double width) {
        this.width = width;
        this.color = color;
    }

    public double getWidth() {
        return width;
    }

    public RGBA getColor() {
        return color;
    }
}
