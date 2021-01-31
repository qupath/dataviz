package net.mahdilamb.charts.axes;

import net.mahdilamb.charts.Axis;

import java.util.List;
import java.util.PrimitiveIterator;

/**
 * A labeled axis where is each label is associated with a tick mark
 */
public class LabeledAxis extends Axis {
    final boolean useArray;
    final String[] labelsArray;
    final List<String> labelsList;

    public LabeledAxis(final String title, List<String> labels) {
        super(title, -.5, labels.size() + .5);//TODO check
        useArray = false;
        labelsList = labels;
        labelsArray = null;
    }

    @Override
    protected String getLabel(double val) {
        final int i = (int) val;
        return useArray ? labelsArray[i] : labelsList.get(i);
    }

    @Override
    public Iterable<Double> ticks(double min, double max, double spacing) {
        return () -> new PrimitiveIterator.OfDouble() {
            private final double start = Math.ceil(Math.max(min, getLowerBound()));
            private final double stop = Math.floor(Math.min(max, getUpperBound()));
            private double i = start;

            @Override
            public double nextDouble() {
                return i++;
            }

            @Override
            public boolean hasNext() {
                return i <= stop;
            }
        };
    }

}
