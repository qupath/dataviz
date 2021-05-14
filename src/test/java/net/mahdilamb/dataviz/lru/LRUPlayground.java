package net.mahdilamb.dataviz.lru;

import net.mahdilamb.dataviz.utils.SpatialCache;
import net.mahdilamb.dataviz.utils.rtree.SpaceFillingCurves;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;

public class LRUPlayground extends JPanel {
    final BufferedImage buffer;
    final Graphics2D g;
    int tileWidth = 64, tileHeight = 64;
    int viewportX = 100, viewportY = 100, viewportWidth = 400, viewportHeight = 400;
    int padX = viewportX, padY = viewportY;
    double minX, minY, maxX = 10, maxY = 20;
    boolean xReversed = false, yReversed = true;
    final Map<Integer, Color> colors = new HashMap<>();
    final SpatialCache<BufferedImage> spatialCache;

    LRUPlayground() {
        buffer = new BufferedImage(viewportWidth + padX + padX, viewportHeight + padY + padY, BufferedImage.TYPE_INT_ARGB);
        g = buffer.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g.setTransform(AffineTransform.getTranslateInstance(padX - viewportX, padY - viewportX));
        spatialCache = new SpatialCache<>(-1, tileWidth, tileHeight,
                this::createTile,
                this::createTile,
                this::drawTile
        );
        update();
    }

    double getScaleX() {
        return (maxX - minX) / viewportWidth;
    }

    double getScaleY() {
        return (maxY - minY) / viewportHeight;
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(buffer, -padX + viewportX, -padY + viewportY, null);

    }

    void update() {
        final Composite before = g.getComposite();
        g.setComposite(AlphaComposite.Clear);
        g.fillRect(-padX + viewportX, -padY + viewportY, buffer.getWidth(), buffer.getHeight());
        g.setComposite(before);

        g.setColor(Color.BLACK);
        drawXAxis();
        drawYAxis();
        g.setClip(new Rectangle2D.Double(viewportX, viewportY, viewportWidth, viewportHeight));

        spatialCache.draw(
                viewportWidth, viewportHeight,
                xReversed, yReversed,
                minX, minY, maxX, maxY
        );
        g.setClip(null);
        g.setColor(Color.BLACK);
        g.draw(new Rectangle2D.Double(viewportX, viewportY, viewportWidth, viewportHeight));
        repaint();
    }

    BufferedImage createTile(double x, double y, int width, int height) {
        final String text = Integer.toString(SpaceFillingCurves.encodeHilbert((int) ((x - viewportX) / tileWidth), (int) ((y - viewportY) / tileHeight)));
        final BufferedImage image = new BufferedImage(width + 2, height + 2, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g.setTransform(AffineTransform.getTranslateInstance(-x - 1, -y - 1));
        g.setColor(Color.BLACK);
        g.draw(new Rectangle2D.Double(x, y, width, height));
        final int i = Integer.parseInt(text);
        Color color;
        if ((color = colors.get(i)) == null) {
            color = new Color(ThreadLocalRandom.current().nextFloat(), ThreadLocalRandom.current().nextFloat(), ThreadLocalRandom.current().nextFloat());
            colors.put(i, color);
        }
        g.setColor(color);
        g.fill(new Rectangle2D.Double(x, y, width, height));
        g.setColor(Color.BLACK);
        g.drawString(text, (float) x + .5f * width - (g.getFontMetrics().stringWidth(text) * .5f), (float) y + g.getFontMetrics().getAscent() - .5f * g.getFontMetrics().getHeight() + .5f * height);
        g.dispose();
        return image;
    }

    void drawTile(double x, double y, BufferedImage image) {
        this.g.drawImage(image, AffineTransform.getTranslateInstance(x+padX, y+padY), null);
    }

    void drawYAxis() {
        final String top;
        final String bottom;
        if (yReversed) {
            top = String.format("%.2f", maxY);
            bottom = String.format("%.2f", minY);
        } else {
            top = String.format("%.2f", minY);
            bottom = String.format("%.2f", maxY);
        }
        g.drawString(top, viewportX - g.getFontMetrics().stringWidth(top), viewportY - Math.round(g.getFontMetrics().getHeight() * .5) + g.getFontMetrics().getAscent());
        g.drawString(bottom, viewportX - g.getFontMetrics().stringWidth(bottom), viewportY - Math.round(g.getFontMetrics().getHeight() * .5) + g.getFontMetrics().getAscent() + viewportHeight);
    }

    void drawXAxis() {
        final String left;
        final String right;
        if (xReversed) {
            left = String.format("%.2f", maxX);
            right = String.format("%.2f", minX);
        } else {
            left = String.format("%.2f", minX);
            right = String.format("%.2f", maxX);
        }
        g.drawString(left, viewportX - g.getFontMetrics().stringWidth(left) * .5f, viewportY + viewportHeight + g.getFontMetrics().getAscent());
        g.drawString(right, viewportX - g.getFontMetrics().stringWidth(right) * .5f + viewportWidth, viewportY + viewportHeight + g.getFontMetrics().getAscent());
    }

    public static void main(String[] args) {
        final JFrame frame = new JFrame();
        final LRUPlayground panel = new LRUPlayground();
        final double[] point = new double[2];
        panel.setPreferredSize(new Dimension(panel.viewportX + panel.viewportWidth + panel.viewportX, panel.viewportY + panel.viewportHeight + panel.viewportY));
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                point[0] = e.getX();
                point[1] = e.getY();
            }
        });
        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                double dX = (point[0] - e.getX()) * panel.getScaleX();
                double dY = (point[1] - e.getY()) * panel.getScaleY();
                if (panel.yReversed) {
                    dY *= -1;
                }
                if (panel.xReversed) {
                    dX *= -1;
                }
                panel.minX += dX;
                panel.maxX += dX;
                panel.minY += dY;
                panel.maxY += dY;
                point[0] = e.getX();
                point[1] = e.getY();
                panel.update();
            }
        });
        frame.add(panel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

}
