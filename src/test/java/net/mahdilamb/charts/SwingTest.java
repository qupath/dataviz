package net.mahdilamb.charts;

import net.mahdilamb.charts.swing.ChartSwing;

import javax.swing.*;
import java.awt.*;

public class SwingTest {
    public static void main(String[] args) {
        final JFrame frame = new JFrame();
        frame.setLayout(new BorderLayout());

        final ChartSwing<?, ?> chart = new ChartSwing<>("Chart", 200, 200, null);
        chart.addTo(frame.getContentPane(), BorderLayout.CENTER);
        frame.setSize((int) Math.ceil(chart.getWidth()), (int) Math.ceil(chart.getHeight()));
     //   frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


    }
}
