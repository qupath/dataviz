package net.mahdilamb.charts.axes;

import java.util.PrimitiveIterator;

/**
 * A logarithmic axis where each tick increases logarithmically
 */
public final class LogarithmicAxis extends NumericAxis {

    public LogarithmicAxis(double min, double max) {
        super(min, max);
    }

    @Override
    protected Iterable<Double> ticks(double min, double max, double spacing) {
        return () -> new PrimitiveIterator.OfDouble() {
            private final double start = Math.max(min, getMinExtent());
            private final double maxIter = ((Math.min(max, getMaxExtent()) - start) / spacing);
            private double i = 0;

            @Override
            public double nextDouble() {
                return Math.pow(10, start + (i++ * spacing));
            }

            @Override
            public boolean hasNext() {
                return i < maxIter;
            }
        };
    }


}
