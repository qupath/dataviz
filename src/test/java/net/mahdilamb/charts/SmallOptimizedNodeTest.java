package net.mahdilamb.charts;

import net.mahdilamb.charts.graphics.ChartCanvas;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class SmallOptimizedNodeTest {
    public static final ChartComponent empty = new ChartComponent() {
        @Override
        protected void layout(Figure<?, ?> source, ChartCanvas<?> canvas, double minX, double minY, double maxX, double maxY) {

        }

        @Override
        protected void draw(Figure<?, ?> source, ChartCanvas<?> canvas, double minX, double minY, double maxX, double maxY) {

        }
    };
    ChartPane node = new ChartPane() ;
    @Test
    public void addWorksCorrectly() {
        assertEquals(0, node.size());
        node.add(empty);
        assertEquals(1, node.size());
        node.add(empty);
        assertEquals(2, node.size());
        node.add(empty);
        assertEquals(3, node.size());
    }

    @Test
    public void removeWorksCorrectly() {
        assertEquals(0, node.size());
        node.add(empty);
        assertEquals(1, node.size());
        node.remove(empty);
        assertEquals(0, node.size());
        node.add(empty);
        node.add(empty);
        assertEquals(2, node.size());
        node.remove(empty);
        assertEquals(1, node.size());
        node.remove(empty);
        assertEquals(0, node.size());
        node.add(empty);
        assertEquals(1, node.size());

    }

}
