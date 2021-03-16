package net.mahdilamb.dataviz.utils;

import net.mahdilamb.dataframe.utils.DoubleArrayList;
import net.mahdilamb.stats.StatUtils;

/**
 * Bandwidth estimators based on:
 * https://www.statsmodels.org/stable/_modules/statsmodels/nonparametric/bandwidths.html
 */
public final class BandwidthEstimators {
    private BandwidthEstimators() {

    }

    public interface BandwidthEstimator<T, U> {
        double apply(T a, U b);
    }

    private static double selectSigma(DoubleArrayList values, int[] order) {
        double iqr = StatUtils.interQuartileRange(values::get, values.size(), order) / 1.349;
        double stdev = StatUtils.standardDeviation(values::get, values.size(), 1);
        if (iqr > 0) {
            return Math.min(stdev, iqr);
        }
        return stdev;
    }


    public static double scottsRule(DoubleArrayList values, int[] order) {
        return 1.059 * selectSigma(values, order) * Math.pow(values.size(), -.2);
    }

    public static double silvermansRule(DoubleArrayList values, int[] order) {
        return .9 * selectSigma(values, order) * Math.pow(values.size(), -.2);
    }


    public static BandwidthEstimator<DoubleArrayList, int[]> getEstimator(final String estimator) {
        switch (estimator) {
            case "scott":
                return BandwidthEstimators::scottsRule;
            case "silverman":
                return BandwidthEstimators::silvermansRule;
            default:
                throw new UnsupportedOperationException("Could not found estimator called " + estimator);
        }
    }
}
