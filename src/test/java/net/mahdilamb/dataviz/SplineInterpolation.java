package net.mahdilamb.dataviz;

import net.mahdilamb.dataframe.utils.IntroSort;
import net.mahdilamb.stats.ArrayUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;
import java.util.function.IntToDoubleFunction;

public class SplineInterpolation {
    static double smoothValue = 0.8;

    static final class Bezier {
        double startX, startY, cp1X, cp1Y, cp2X, cp2Y, endX, endY;

        public Bezier(double startX, double startY, double cp1X, double cp1Y, double cp2X, double cp2Y, double endX, double endY) {
            this.startX = startX;
            this.startY = startY;
            this.cp1X = cp1X;
            this.cp1Y = cp1Y;
            this.cp2X = cp2X;
            this.cp2Y = cp2Y;
            this.endX = endX;
            this.endY = endY;
        }
    }

    static final class Line {
        double startX, startY, endX, endY;

        public Line(double startX, double startY, double endX, double endY) {
            this.startX = startX;
            this.startY = startY;
            this.endX = endX;
            this.endY = endY;
        }
    }

    static List<Point2D> points = new ArrayList<>();
    static List<Bezier> lines = new ArrayList<>();
    static List<Line> mLines = new ArrayList<>();
    static Ellipse2D ellipse = new Ellipse2D.Double();
    static Path2D path = new Path2D.Double();

    static void set(Ellipse2D ellipse, Point2D point) {
        ellipse.setFrameFromCenter(point.getX(), point.getY(), point.getX() + 3, point.getY() + 3);
    }

    static void drawBeziers(final Graphics2D g, final List<Bezier> lines) {
        if (lines.size() >= 1) {
            path.reset();
            path.moveTo(lines.get(0).startX, lines.get(0).startY);
            for (final Bezier b : lines) {
                path.curveTo(b.cp1X, b.cp1Y, b.cp2X, b.cp2Y, b.endX, b.endY);
            }
            g.draw(path);
        }
    }

    static void drawLines(final Graphics2D g, final List<Line> lines) {
        if (lines.size() >= 1) {
            path.reset();
            path.moveTo(lines.get(0).startX, lines.get(0).startY);
            for (final Line b : lines) {
                path.lineTo(b.endX, b.endY);
            }
            g.draw(path);
        }
    }

    static final class GraphicsPanel extends JPanel {
        @Override
        public void paint(Graphics g) {
            super.paint(g);
            g.clearRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.BLACK);
            drawBeziers((Graphics2D) g, lines);
            g.setColor(Color.RED);
            drawLines((Graphics2D) g, mLines);
            g.setColor(Color.BLUE);
            for (final Point2D p : points) {
                set(ellipse, p);
                ((Graphics2D) g).draw(ellipse);
            }
        }
    }

    //https://www.codeproject.com/Articles/769055/Interpolate-D-points-usign-Bezier-curves-in-WPF
    static void interpolateCubicBezier(final List<Bezier> lines) {
        lines.clear();
        if (points.size() < 3) {
            return;
        }
        for (int i = 0; i < points.size() - 1; ++i) {
            double x1 = points.get(i).getX();
            double y1 = points.get(i).getY();

            double x2 = points.get(i + 1).getX();
            double y2 = points.get(i + 1).getY();

            double x0;
            double y0;
            if (i == 0) {
                Point2D previousPoint = points.get(i);  //if is the first point the previous one will be it self
                x0 = previousPoint.getX();
                y0 = previousPoint.getY();
            } else {
                x0 = points.get(i - 1).getX();
                y0 = points.get(i - 1).getY();
            }
            double x3, y3;
            if (i == points.size() - 2) {
                Point2D nextPoint = points.get(i + 1);  //if is the last point the next point will be the last one
                x3 = nextPoint.getX();
                y3 = nextPoint.getY();
            } else {
                x3 = points.get(i + 2).getX();
                y3 = points.get(i + 2).getY();
            }
            double xc1 = (x0 + x1) / 2.0;
            double yc1 = (y0 + y1) / 2.0;
            double xc2 = (x1 + x2) / 2.0;
            double yc2 = (y1 + y2) / 2.0;
            double xc3 = (x2 + x3) / 2.0;
            double yc3 = (y2 + y3) / 2.0;

            double len1 = Math.sqrt((x1 - x0) * (x1 - x0) + (y1 - y0) * (y1 - y0));
            double len2 = Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
            double len3 = Math.sqrt((x3 - x2) * (x3 - x2) + (y3 - y2) * (y3 - y2));

            double k1 = len1 / (len1 + len2);
            double k2 = len2 / (len2 + len3);

            double xm1 = xc1 + (xc2 - xc1) * k1;
            double ym1 = yc1 + (yc2 - yc1) * k1;

            double xm2 = xc2 + (xc3 - xc2) * k2;
            double ym2 = yc2 + (yc3 - yc2) * k2;
            double ctrl1_x = xm1 + (xc2 - xm1) * smoothValue + x1 - xm1;
            double ctrl1_y = ym1 + (yc2 - ym1) * smoothValue + y1 - ym1;

            double ctrl2_x = xm2 + (xc2 - xm2) * smoothValue + x2 - xm2;
            double ctrl2_y = ym2 + (yc2 - ym2) * smoothValue + y2 - ym2;

            lines.add(new Bezier(x1, y1, ctrl1_x, ctrl1_y, ctrl2_x, ctrl2_y, x2, y2));
        }
    }

    static void interpolateMonotonicCubicBezier(final List<Line> lines) {
        lines.clear();
        if (points.size() <= 1) {
            return;
        }
        int[] order = ArrayUtils.intRange(points.size());
        IntroSort.argSort(order, (IntToDoubleFunction) i -> points.get(i).getX(), true);
        double[] dxs = new double[order.length - 1],
                ms = new double[dxs.length];
        for (int i = 0; i < dxs.length; i++) {
            double dx = points.get(order[i + 1]).getX() - points.get(order[i]).getX(),
                    dy = points.get(order[i + 1]).getY() - points.get(order[i]).getY();
            dxs[i] = dx;
            ms[i] = dy / dx;
        }

        double[] c1s = new double[dxs.length + 1];
        c1s[0] = ms[0];
        for (int i = 0; i < dxs.length - 1; i++) {
            double m = ms[i], mNext = ms[i + 1];
            if (m * mNext <= 0) {
                c1s[i + 1] = 0;
            } else {
                double dx_ = dxs[i], dxNext = dxs[i + 1], common = dx_ + dxNext;
                c1s[i + 1] = 3 * common / ((common + dxNext) / m + (common + dx_) / mNext);
            }
        }
        c1s[dxs.length] = ms[ms.length - 1];

        // Get degree-2 and degree-3 coefficients
        double[] c2s = new double[c1s.length], c3s = new double[c1s.length];
        for (int i = 0; i < c1s.length - 1; i++) {
            double c1 = c1s[i], m_ = ms[i], invDx = 1 / dxs[i], common_ = c1 + c1s[i + 1] - m_ - m_;
            c2s[i] = (m_ - c1 - common_) * invDx;
            c3s[i] = common_ * invDx * invDx;
        }
        DoubleUnaryOperator interpolator = x -> {
            // The rightmost point in the dataset should give an exact result
            var i = points.size() - 1;
            if (x == points.get(order[i]).getX()) {
                return points.get(order[i]).getY();
            }

            // Search for the interval x is in, returning the corresponding y if x is one of the original xs
            int low = 0, mid, high = c3s.length - 1;
            while (low <= high) {
                mid = (low + high) >> 1;
                double xHere = points.get(order[mid]).getX();
                if (xHere < x) {
                    low = mid + 1;
                } else if (xHere > x) {
                    high = mid - 1;
                } else {
                    return points.get(order[mid]).getY();
                }
            }
            i = Math.max(0, high);

            // Interpolate
            double diff = x - points.get(order[i]).getX(), diffSq = diff * diff;
            return points.get(order[i]).getY() + c1s[i] * diff + c2s[i] * diffSq + c3s[i] * diff * diffSq;
        };
        double minX = points.get(order[0]).getX(),
                maxX = points.get(order[order.length - 1]).getX();
        double lastX = minX, lastY = interpolator.applyAsDouble(minX);

        for (double i = minX; i <= maxX; i += 1) {
            if (i != minX) {
                double _y = interpolator.applyAsDouble(i);
                lines.add(new Line(lastX, lastY, i, _y));
                lastX = i;
                lastY = _y;
            }
        }

    }

    static void show() {
        final JFrame frame = new JFrame();
        final GraphicsPanel panel = new GraphicsPanel();
        panel.setPreferredSize(new Dimension(1000, 1000));
        frame.getContentPane().add(panel);
        frame.pack();
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                points.add(new Point2D.Double(e.getX(), e.getY()));
                interpolateCubicBezier(lines);
                interpolateMonotonicCubicBezier(mLines);
                panel.repaint();
            }
        });
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SplineInterpolation::show);
    }
}
