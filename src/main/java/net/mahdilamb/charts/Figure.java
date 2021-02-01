package net.mahdilamb.charts;

import net.mahdilamb.charts.graphics.ChartCanvas;
import net.mahdilamb.charts.graphics.Theme;

import java.util.ArrayList;
import java.util.List;

public class Figure<S> extends ChartComponent {


    static class PlotLayout extends ChartComponent{

        @Override
        protected void layout(ChartCanvas<?> canvas, Chart<?, ?> source, double minX, double minY, double maxX, double maxY) {
            //todo
        }
    }



    Theme theme;

    Title title;
    ChartComponent topArea, leftArea, rightArea, bottomArea;
    final PlotLayout plotArea;
    ChartComponent central; // to be used when adding colorscale/legend to the chart
    ChartComponent overlay;//for interaction
    double width, height;

    S singleData;
    List<S> data;
    ChartComponent singleAnnotation;
    List<ChartComponent> annotations;

    public Figure(final Title title, final PlotLayout plot, final double width, final double height) {
        this.title = title;
        this.plotArea = plot;
        this.width = width;
        this.height = height;
    }


    public void addData(S data) {
        //todo update key/color bar
        if (singleData == null) {
            singleData = data;
            return;
        }
        if (data == null) {
            this.data = new ArrayList<>();
            this.data.add(singleData);
            singleData = null;
            return;
        }
        this.data.add(data);

    }

    public void addAnnotation(ChartComponent annotation) {
        if (singleAnnotation == null) {
            singleAnnotation = annotation;
            return;
        }
        if (annotation == null) {
            this.annotations = new ArrayList<>();
            this.annotations.add(singleAnnotation);
            singleAnnotation = null;
            return;
        }
        this.annotations.add(annotation);

    }

    @Override
    protected void layout(ChartCanvas<?> canvas,  Chart<?, ?>  source, double minX, double minY, double maxX, double maxY) {

    }

    void layout(ChartCanvas<?> canvas,  Chart<?, ?>  source) {
        layout(canvas, source, 0, 0, width, height);
    }

    protected void requestLayout() {
     //todo   layout(getCanvas(), this);
    }

    protected ChartCanvas<?> getCanvas() {
        //TODO
        return null;
    }


}
