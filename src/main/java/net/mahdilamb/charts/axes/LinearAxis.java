package net.mahdilamb.charts.axes;

import java.util.PrimitiveIterator;

/**
 * A linear axis where each tick increases linearly
 */
public class LinearAxis extends NumericAxis {

    public LinearAxis(String label, double min, double max) {
        super(label, min, max);
    }

    @Override
    protected Iterable<Double> ticks(final double min, double max, double spacing) {
        if (!Double.isFinite(spacing)) {
            throw new IllegalArgumentException("could not generate ticks");
        }
        return () -> new PrimitiveIterator.OfDouble() {
            private double i = min - spacing;

            @Override
            public double nextDouble() {
                return i += spacing;
            }

            @Override
            public boolean hasNext() {
                return i <= max;
            }
        };
    }


}
