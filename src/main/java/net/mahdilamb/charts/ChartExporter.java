package net.mahdilamb.charts;

public class ChartExporter {


    protected static double getTitleWidth(Chart<?> chart) {
        return chart.titleWidth;
    }

    protected static double getTitleHeight(Chart<?> chart) {
        return chart.titleHeight;
    }

    protected static double getPlotX(Chart<?> chart) {
        return chart.getPlot().x;
    }

    protected static double getPlotY(Chart<?> chart) {
        return chart.getPlot().y;
    }

    protected static double getPlotWidth(Chart<?> chart) {
        return chart.getPlot().width;
    }

    protected static double getPlotHeight(Chart<?> chart) {
        return chart.getPlot().height;
    }

    protected static double getLegendY(Chart<?> chart) {
        return chart.getLegend().yOffset;
    }

    protected static double getLegendWidth(Chart<?> chart) {
        return chart.getLegend().width;
    }

    protected static double getLegendHeight(Chart<?> chart) {
        return chart.getLegend().height;
    }

    protected static Iterable<LegendItem> getLegendItems(Chart<?> chart) {
        return chart.plot.getLegendItems();
    }
}
