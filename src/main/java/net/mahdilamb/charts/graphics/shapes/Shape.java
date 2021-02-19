package net.mahdilamb.charts.graphics.shapes;

import net.mahdilamb.charts.graphics.ChartCanvas;

public interface Shape {

    void fill(ChartCanvas<?> canvas);

    void stroke(ChartCanvas<?> canvas);


}
