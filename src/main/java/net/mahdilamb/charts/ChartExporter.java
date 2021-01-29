package net.mahdilamb.charts;

import net.mahdilamb.charts.graphics.ChartCanvas;

/**
 * An exporter for the chart that allows the chart to be laid out on a different canvas
 */
public class ChartExporter {
    protected static void layoutChart(ChartCanvas dest, Chart<?, ?> source) {
        source.layout(dest);
    }

    protected static double getTextHeight(Text text){
        return text.getHeight();
    }
    protected static double getTextWidth(Text text){
        return text.getWidth();
    }
    protected static double getTextBaselineOffset(Text text){
        return text.getBaselineOffset();
    }
}
