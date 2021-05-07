package net.mahdilamb.dataviz;

import net.mahdilamb.dataviz.layouts.XYLayout;

import java.util.Objects;

public final class PlotBounds<B extends PlotBounds.Bounds<PL>, PL extends PlotLayout<PL>> {

    public interface Bounds<PL extends PlotLayout<PL>> {

    }

    public static final class XY implements Bounds<XYLayout> {
        double minX, minY, maxX, maxY;

        public XY(double minX, double minY, double maxX, double maxY) {
            this.minX = minX;
            this.minY = minY;
            this.maxX = maxX;
            this.maxY = maxY;
        }

        public double getMinX() {
            return minX;
        }

        public double getMinY() {
            return minY;
        }

        public double getMaxX() {
            return maxX;
        }

        public double getMaxY() {
            return maxY;
        }
    }

    B home;
    B max;

    public PlotBounds(B home, B max) {
        this.home = home;
        Objects.requireNonNull(this.max = max);
    }

}
