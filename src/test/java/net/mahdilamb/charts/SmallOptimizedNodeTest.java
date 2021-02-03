package net.mahdilamb.charts;

import net.mahdilamb.charts.graphics.ChartCanvas;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SmallOptimizedNodeTest {
    public static final ChartComponent<Object, Object> empty = new ChartComponent<>() {
        @Override
        protected void calculateBounds(ChartCanvas<?> canvas, Chart<?, ?> source, double minX, double minY, double maxX, double maxY) {

        }

        @Override
        protected void layout(ChartCanvas<?> canvas, Chart<?, ?> source, double minX, double minY, double maxX, double maxY) {

        }
    };

    @Test
    public void addWorksCorrectly() {
        ChartNode<Object, Object> node = new ChartNode<>();
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
        ChartNode<Object, Object> node = new ChartNode<>();
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