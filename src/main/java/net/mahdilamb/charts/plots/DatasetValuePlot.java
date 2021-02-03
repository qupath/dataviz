package net.mahdilamb.charts.plots;

import net.mahdilamb.charts.dataframe.DataFrame;

public class DatasetValuePlot {
    DataFrame dataset;
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
