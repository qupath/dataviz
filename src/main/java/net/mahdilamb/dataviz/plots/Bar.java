package net.mahdilamb.dataviz.plots;

import net.mahdilamb.dataframe.DataFrame;
import net.mahdilamb.dataviz.PlotData;

/**
 * Bar chart
 */
public class Bar extends PlotData.CategoricalData<Bar> {
    /**
     * Create a bar chart with an array of categories and values
     *
     * @param categories the categories
     * @param values     the values
     */
    public Bar(String[] categories, double[] values) {
        super(categories, values);
    }

    /**
     * Create a bar chart from a data frame
     *
     * @param dataFrame the data frame
     * @param category  the name of the category series
     * @param values    the name of the value series
     */
    public Bar(DataFrame dataFrame, String category, String values) {
        super(dataFrame, category, values);
    }

    public Bar(DataFrame dataFrame, String x, String[] y) {
        super(dataFrame, x, y);

    }
}
