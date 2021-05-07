package net.mahdilamb.dataviz.tests;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;

public class DrawImageTest extends JPanel {
    static BufferedImage strawberry;

    static {
        try {
            strawberry = ImageIO.read(Objects.requireNonNull(DrawImageTest.class.getClassLoader().getResourceAsStream("strawberry.jpg")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    double offsetX = 0,
            offsetY = 0;
    private static final AffineTransform transform = new AffineTransform();

    DrawImageTest() {
        setPreferredSize(new Dimension(400, 400));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        transform.setToTranslation(offsetX, offsetY);
        ((Graphics2D) g).setTransform(transform);
        g.drawImage(strawberry, 0, 0, null);

        transform.setToIdentity();
        ((Graphics2D) g).setTransform(transform);
        g.drawLine(0, 0, strawberry.getWidth(), strawberry.getHeight());
    }

    public static void main(String[] args) {
        final JFrame frame = new JFrame();

        final DrawImageTest panel = new DrawImageTest();
        final JSlider x = new JSlider(0, 100, 0);
        final JSlider y = new JSlider(0, 100, 0);
        x.addChangeListener(e -> {
            panel.offsetX = ((JSlider) e.getSource()).getValue() / 100.;
            panel.repaint();
        });
        y.addChangeListener(e -> {
            panel.offsetY = ((JSlider) e.getSource()).getValue() / 100.;
            panel.repaint();
        });
        final JPanel content = new JPanel();
        content.add(panel);
        content.add(x);
        content.add(y);
        frame.getContentPane().add(content);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
