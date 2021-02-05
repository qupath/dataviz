package net.mahdilamb.charts.plots;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for finding a plot type based on the series type.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface PlotType {
    enum DataType  {
        NUMERIC,
        DOUBLE,
        LONG,
        STRING,
        BOOLEAN
    }
    /**
     * @return the name of the plot
     */
    String name();

    /**
     * @return the ordered combination of compatible series.
     */
    DataType[] compatibleSeries();

}
