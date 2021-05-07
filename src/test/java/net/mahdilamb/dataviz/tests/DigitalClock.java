package net.mahdilamb.dataviz.tests;

import javax.swing.*;
import java.awt.*;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DigitalClock extends JPanel {

    final Font theFont = new Font("TimesRoman", Font.BOLD, 24);
    String theDate;
    final long INTERVAL = 1_000;
    final Thread runner;
    Runnable action = () -> {
        theDate = new Date().toString();
        repaint();
    };

    public DigitalClock() {
        runner = new Thread(() -> {
            while (true) {
                final long start = System.currentTimeMillis();
                action.run();
                final long duration = System.currentTimeMillis() - start;
                try {
                    Thread.sleep(INTERVAL - duration);
                } catch (InterruptedException ignored) {
                }
            }
        });
        runner.start();

    }

    public void paint(Graphics g) {
        super.paint(g);
        g.setFont(theFont);
        g.drawString(theDate, 10, 50);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            final JFrame frame = new JFrame();
            final DigitalClock test = new DigitalClock();
            frame.getContentPane().add(test);
            test.setPreferredSize(new Dimension(400, 400));

            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
        });

    }
}
