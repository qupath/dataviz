package net.mahdilamb.charts.axes;

import java.util.PrimitiveIterator;

/**
 * A linear axis where each tick increases linearly
 */
public class LinearAxis extends NumericAxis {

    public LinearAxis(double min, double max) {
        super(min, max == min ? (max + 1e-9) : max);
    }

    @Override
    protected Iterable<Double> ticks(double min, double max, double spacing) {
        return () -> new PrimitiveIterator.OfDouble() {
            private final double start = Math.max(min, getLowerBound());
            private final double maxIter = ((Math.min(max, getUpperBound()) - start) / spacing);
            private double i = 0;

            @Override
            public double nextDouble() {
                return start + (i++ * spacing);
            }

            @Override
            public boolean hasNext() {
                return i < maxIter;
            }
        };
    }


}
