package net.mahdilamb.dataviz;

import net.mahdilamb.dataviz.plots.Line;
import net.mahdilamb.dataviz.plots.Scatter;

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
        double a = 0, b = 0, c = 0, d = 0;
        double minX = Double.POSITIVE_INFINITY,
                maxX = Double.NEGATIVE_INFINITY;
        for (int n = 0; n < data.size(); n++) {
            minX = Math.min(minX, data.getX(n));
            maxX = Math.max(maxX, data.getX(n));
            a += data.getX(n);
            b += data.getY(n);
            c += data.getX(n) * data.getX(n);
            d += data.getX(n) * data.getY(n);

        }
        double run = ((data.size() * c) - (a * a));
        double rise = ((data.size() * d) - (a * b));
        double gradient = run == 0 ? 0 : (rise / run);
        double intercept = ((b / data.size()) - ((gradient * a) / data.size()));
        final DoubleUnaryOperator pred = x -> intercept + x * gradient;
        Line line = new Line(
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
     * Exponential regression adapted from https://github.com/Tom-Alexander/regression-js/blob/master/src/regression.js
     *
     * @param data the data set
     * @return a figure with the linear trendline
     */
    public static Figure exponential(Scatter data, int numberOfSamples) {
        double b = 0, c = 0, d = 0, e = 0, f = 0;
        double minX = Double.POSITIVE_INFINITY,
                maxX = Double.NEGATIVE_INFINITY;
        for (int n = 0; n < data.size(); n++) {
            minX = Math.min(minX, data.getX(n));
            maxX = Math.max(maxX, data.getX(n));
            b += data.getY(n);
            c += data.getX(n) * data.getX(n) * data.getY(n);
            d += data.getY(n) * Math.log(data.getY(n));
            e += data.getX(n) * data.getY(n) * Math.log(data.getY(n));
            f += data.getX(n) * data.getY(n);
        }

        double denominator = ((b * c) - (f * f));
        double A = Math.exp(((c * d) - (f * e)) / denominator);
        double B = ((b * e) - (f * d)) / denominator;
        DoubleUnaryOperator predict = x -> A * Math.exp(B * x);
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

    /**
     * Power regression adapted from https://github.com/Tom-Alexander/regression-js/blob/master/src/regression.js
     *
     * @param data the data set
     * @return a figure with the linear trendline
     */
    public static Figure power(Scatter data, int numberOfSamples) {
        double sum_0 = 0, sum_1 = 0, sum_2 = 0, sum_3 = 0;
        double minX = Double.POSITIVE_INFINITY,
                maxX = Double.NEGATIVE_INFINITY;
        for (int n = 0; n < data.size(); n++) {
            minX = Math.min(minX, data.getX(n));
            maxX = Math.max(maxX, data.getX(n));
            sum_0 += Math.log(data.getX(n));
            sum_1 += Math.log(data.getY(n)) * Math.log(data.getX(n));
            sum_2 += Math.log(data.getY(n));
            sum_3 += (Math.pow(Math.log(data.getX(n)), 2));
        }

        double coeffB = ((data.size() * sum_1) - (sum_0 * sum_2)) / ((data.size() * sum_3) - (sum_0 * sum_0));
        double coeffA = Math.exp(((sum_2 - (coeffB * sum_0)) / data.size()));
        DoubleUnaryOperator predict = x -> coeffA * Math.pow(x, coeffB);
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
