package net.mahdilamb.charts.plots;

import net.mahdilamb.charts.series.SeriesType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for finding a plot type based on the series type.
 */
@Target(ElementType.CONSTRUCTOR)
@Retention(RetentionPolicy.CLASS)
public @interface PlotType {
    /**
     * @return the name of the plot
     */
    String name();

    /**
     * @return the ordered combination of compatible series.
     */
    SeriesType[] compatibleSeries();

}
