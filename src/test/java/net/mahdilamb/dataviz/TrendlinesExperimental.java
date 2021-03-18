package net.mahdilamb.dataviz;

import net.mahdilamb.dataviz.plots.Line;
import net.mahdilamb.dataviz.plots.Scatter;
import net.mahdilamb.stats.StatUtils;

import java.util.function.DoubleUnaryOperator;

import static net.mahdilamb.stats.ArrayUtils.linearlySpaced;

public final class TrendlinesExperimental {
    TrendlinesExperimental() {

    }

    /**
     * Linear regression adapted from https://github.com/Tom-Alexander/regression-js/blob/master/src/regression.js
     *
     * @param data the data set
     * @return a figure with the linear trendline
     */
    public static Figure linear(Scatter data) {
        final DoubleUnaryOperator pred = StatUtils.linearRegression(data::getX, data::getY, data.size());
        double minX = StatUtils.min(data::getX, data.size()),
                maxX = StatUtils.max(data::getX, data.size());
        final Line line = new Line(
                new double[]{minX, maxX},
                new double[]{pred.applyAsDouble(minX), pred.applyAsDouble(maxX)}
        )
                .setLineWidth(2.5)
                .showInLegend(false);
        return data
                .getFigure()
                .addTrace(
                        line
                );
    }

    /**
     * Exponential regression adapted from
     *
     * @param data the data set
     * @return a figure with the linear trendline
     */
    public static Figure exponential(Scatter data, int numberOfSamples) {
        double minX = StatUtils.min(data::getX, data.size()),
                maxX = StatUtils.max(data::getX, data.size());

        final DoubleUnaryOperator predict = StatUtils.exponentialRegression(data::getX, data::getY, data.size());
        final Line line = new Line(
                linearlySpaced(minX, maxX, numberOfSamples),
                predict
        )
                .setLineWidth(2.5)
                .showInLegend(false);
        return data
                .getFigure()
                .addTrace(
                        line
                );
    }

    /**
     * Power regression adapted from https://github.com/Tom-Alexander/regression-js/blob/master/src/regression.js
     *
     * @param data the data set
     * @return a figure with the linear trendline
     */
    public static Figure power(Scatter data, int numberOfSamples) {
        double minX = StatUtils.min(data::getX, data.size()),
                maxX = StatUtils.max(data::getX, data.size());
        DoubleUnaryOperator predict = StatUtils.powerRegression(data::getX, data::getY, data.size());
        Line line = new Line(
                linearlySpaced(minX, maxX, numberOfSamples),
                predict
        )
                .setLineWidth(2.5)
                .showInLegend(false);
        return data
                .getFigure()
                .addTrace(
                        line
                );
    }


}
