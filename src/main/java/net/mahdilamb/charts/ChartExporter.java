package net.mahdilamb.charts;

/**
 * An exporter for the chart that includes protected, methods to package-protected fields in the chart so that
 * it can be used to calculate metrics
 */
public class ChartExporter {
    /**
     * Get the width of the title
     *
     * @param chart the chart of interest
     * @return the width of the chart title
     */
    protected static double getTitleWidth(Chart<?, ?> chart) {
        return chart.titleWidth;
    }

    /**
     * Get the height of the title
     *
     * @param chart the chart of interest
     * @return the height of the chart title
     */
    protected static double getTitleHeight(Chart<?, ?> chart) {
        return chart.titleHeight;
    }

    protected static double getPlotX(Chart<?, ?> chart) {
        return ((PlotImpl<?>) chart.getPlot()).x;
    }

    protected static double getPlotY(Chart<?, ?> chart) {
        return ((PlotImpl<?>) chart.getPlot()).y;
    }

    protected static double getPlotWidth(Chart<?, ?> chart) {
        return ((PlotImpl<?>) chart.getPlot()).width;
    }

    protected static double getPlotHeight(Chart<?, ?> chart) {
        return ((PlotImpl<?>) chart.getPlot()).height;
    }

    protected static double getLegendY(Chart<?, ?> chart) {
        return chart.getLegend().yOffset;
    }

    protected static double getLegendWidth(Chart<?, ?> chart) {
        return chart.getLegend().width;
    }

    protected static double getLegendHeight(Chart<?, ?> chart) {
        return chart.getLegend().height;
    }

    protected static Iterable<Legend.LegendItem> getLegendItems(Chart<?, ?> chart) {
        return ((PlotImpl<?>) chart.plot).getLegendItems();
    }
}
