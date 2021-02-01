package net.mahdilamb.charts.plots;

import net.mahdilamb.charts.series.Dataset;

public class DatasetValuePlot {
    Dataset dataset;
    String value, color;

    public DatasetValuePlot setValues(final String value) {
        this.value = value;
        return this;
    }

    public DatasetValuePlot setColorBy(final String color) {
        this.color = color;
        return this;
    }
}
