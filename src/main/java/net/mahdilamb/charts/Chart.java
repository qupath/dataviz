package net.mahdilamb.charts;

import net.mahdilamb.charts.styles.Text;
import net.mahdilamb.charts.styles.Title;
import net.mahdilamb.colormap.Color;
import net.mahdilamb.colormap.Colormap;
import net.mahdilamb.colormap.reference.qualitative.Plotly;

import java.util.Iterator;

public abstract class Chart<L extends Plot> {

    Title title;
    L plot;
    Legend legend;
    final Colormap colormap = new Plotly();
    Iterator<Float> colormapIt;


    double width, height;
    double titleWidth, titleHeight;

    public Title getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title.setTitle(title);
        layout();
    }

    public Legend getLegend() {
        return legend;
    }

    public L getPlot() {
        return plot;
    }

    protected void layout() {
        titleWidth = 0;
        titleHeight = 0;
        legend.yOffset = 0;
        double legendY = 0, plotX = 0, plotY = 0, legendWidth = 0, legendHeight = 0;
        if (title != null && title.isVisible()) {
            titleWidth = getTextWidth(title);
            titleHeight = getTextHeight(title);
            legendY += titleHeight;
            plotY = legendY;
        }
        if (legend.isVisible() && !legend.isFloating()) {
            legend.layout(legendY);
            switch (legend.getSide()) {
                case TOP:
                    plotY += legend.height;
                    break;
                case LEFT:
                    plotX += legend.width;
                    break;
                case RIGHT:
                    legendWidth = legend.width;
                    break;
                case BOTTOM:
                    legendHeight = legend.height;
                    break;
            }
        }
        plot.layout(plotX, plotY, width - legendWidth - plotX, height - legendHeight - plotY);
        draw();
    }


    protected abstract double getTextWidth(Text text);

    protected abstract double getTextHeight(Text text);

    protected abstract void draw();

    static Color getNextColor(Chart<?> chart) {
        if (chart.colormapIt == null || !chart.colormapIt.hasNext()){
            chart.colormapIt = chart.colormap.iterator();
        }
        return (Color) chart.colormap.get( chart.colormapIt.next());
    }

}
