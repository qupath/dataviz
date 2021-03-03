package net.mahdilamb.dataviz;

import net.mahdilamb.dataviz.plots.Scatter;
import org.junit.Test;

import java.util.concurrent.ThreadLocalRandom;

import static net.mahdilamb.statistics.ArrayUtils.full;
import static net.mahdilamb.statistics.ArrayUtils.linearlySpaced;

public class SelectionTest {
    @Test
    public void PolygonSelection() {
        int N = 100;
        double[] random_x = linearlySpaced(0, 1, N);
        double[] random_y0 = full(() -> ThreadLocalRandom.current().nextGaussian() + 7.5, N);
        final Scatter scatter = new Scatter(random_x, random_y0);
        final Selection.Polygon polygon = new Selection.Polygon(true)
                .add(0, 9)
                .add(0.8, 9)
                .add(0.8, 5.5)
                .add(0, 5.5)
                .close();
        PlotLayout p = new PlotLayout.Rectangular();
        p.add(scatter);

        polygon.apply(p);
    }
}
