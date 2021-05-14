package net.mahdilamb.dataviz.swing;

import net.mahdilamb.dataviz.graphics.Gradient;
import net.mahdilamb.dataviz.utils.Numbers;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Utility class to convert between charting engine and Swing
 */
public final class SwingUtils {
    private SwingUtils() {

    }

    static final Map<net.mahdilamb.dataviz.graphics.Stroke, java.awt.BasicStroke> strokesToAWT = new WeakHashMap<>();

    /**
     * Convert a double to int
     *
     * @param value the value to convert
     * @return the rounded value
     */
    public static int convert(final double value) {
        return (int) Math.round(value);
    }

    /**
     * Convert a gradient to an AWT gradient
     *
     * @param gradient the source gradient
     * @return AWT gradient
     */
    public static MultipleGradientPaint convert(final Gradient gradient) {
        final Point2D start = new Point2D.Double(gradient.getStartX(), gradient.getStartY());
        final float[] dist = new float[gradient.getColorMap().size()];
        final java.awt.Color[] colors = new java.awt.Color[dist.length];
        int i = 0;
        for (final Map.Entry<Float, Color> entry : gradient.getColorMap().entrySet()) {
            dist[i] = entry.getKey();
            colors[i] = entry.getValue();
            ++i;
        }
        if (gradient.getType() == Gradient.GradientType.LINEAR) {
            final Point2D end = new Point2D.Double(gradient.getEndX(), gradient.getEndY());
            return new LinearGradientPaint(start, end, dist, colors);
        } else {
            return new RadialGradientPaint(start, (float) Numbers.distance(gradient.getStartX(), gradient.getStartY(), gradient.getEndX(), gradient.getEndY()), dist, colors);
        }
    }

    /**
     * Convert a buffered image to a PNG byte array
     *
     * @param image image to convert
     * @return png byte array representation
     */
    public static byte[] convertToByteArray(final BufferedImage image) {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", baos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return baos.toByteArray();
    }


    public static BasicStroke convert(final net.mahdilamb.dataviz.graphics.Stroke stroke) {
        final BasicStroke cached = strokesToAWT.get(stroke);
        if (cached != null) {
            return cached;
        }
        float width = (float) stroke.getWidth();
        float miterLimit = (float) stroke.getMiterLimit();
        float dashOffset = (float) stroke.getDashOffset();
        final int join;
        switch (stroke.getLineJoin()) {
            case BEVEL:
                join = BasicStroke.JOIN_BEVEL;
                break;
            case ROUND:
                join = BasicStroke.JOIN_ROUND;
                break;
            case MITER:
            default:
                join = BasicStroke.JOIN_MITER;
        }
        final int cap;
        switch (stroke.getEndCap()) {
            case SQUARE:
                cap = BasicStroke.CAP_SQUARE;
                break;
            case ROUND:
                cap = BasicStroke.CAP_ROUND;
                break;
            default:
                cap = BasicStroke.CAP_BUTT;
        }
        final BasicStroke bs;
        if (stroke.numDashes() > 0) {
            float[] dashes = new float[stroke.numDashes()];

            for (int i = 0; i < dashes.length; ++i) {
                dashes[i] = (float) stroke.getDash(i);
            }
            bs = new BasicStroke(width, cap, join, miterLimit, dashes, dashOffset);
        } else {
            bs = new BasicStroke(width, cap, join, miterLimit);
        }

        strokesToAWT.put(stroke, bs);
        return bs;
    }

    /*
    computeArc function copied from Batik:


       Licensed to the Apache Software Foundation (ASF) under one or more
       contributor license agreements.  See the NOTICE file distributed with
       this work for additional information regarding copyright ownership.
       The ASF licenses this file to You under the Apache License, Version 2.0
       (the "License"); you may not use this file except in compliance with
       the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing, software
       distributed under the License is distributed on an "AS IS" BASIS,
       WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
       See the License for the specific language governing permissions and
       limitations under the License.


     */
    public static Arc2D computeArc(final Arc2D.Double arc,double x0, double y0, double rx, double ry, double angle, boolean largeArcFlag,
                                   boolean sweepFlag, double x, double y) {
        //
        // Elliptical arc implementation based on the SVG specification notes
        //
        // Compute the half distance between the current and the final point
        double dx2 = (x0 - x) / 2.0;
        double dy2 = (y0 - y) / 2.0;
        // Convert angle from degrees to radians
        angle = Math.toRadians(angle % 360.0);
        double cosAngle = Math.cos(angle);
        double sinAngle = Math.sin(angle);

        //
        // Step 1 : Compute (x1, y1)
        //
        double x1 = (cosAngle * dx2 + sinAngle * dy2);
        double y1 = (-sinAngle * dx2 + cosAngle * dy2);
        // Ensure radii are large enough
        rx = Math.abs(rx);
        ry = Math.abs(ry);
        double Prx = rx * rx;
        double Pry = ry * ry;
        double Px1 = x1 * x1;
        double Py1 = y1 * y1;
        // check that radii are large enough
        double radiiCheck = Px1 / Prx + Py1 / Pry;
        if (radiiCheck > 1) {
            rx = Math.sqrt(radiiCheck) * rx;
            ry = Math.sqrt(radiiCheck) * ry;
            Prx = rx * rx;
            Pry = ry * ry;
        }

        //
        // Step 2 : Compute (cx1, cy1)
        //
        double sign = (largeArcFlag == sweepFlag) ? -1 : 1;
        double sq = ((Prx * Pry) - (Prx * Py1) - (Pry * Px1)) / ((Prx * Py1) + (Pry * Px1));
        sq = (sq < 0) ? 0 : sq;
        double coef = (sign * Math.sqrt(sq));
        double cx1 = coef * ((rx * y1) / ry);
        double cy1 = coef * -((ry * x1) / rx);

        //
        // Step 3 : Compute (cx, cy) from (cx1, cy1)
        //
        double sx2 = (x0 + x) / 2.0;
        double sy2 = (y0 + y) / 2.0;
        double cx = sx2 + (cosAngle * cx1 - sinAngle * cy1);
        double cy = sy2 + (sinAngle * cx1 + cosAngle * cy1);

        //
        // Step 4 : Compute the angleStart (angle1) and the angleExtent (dangle)
        //
        double ux = (x1 - cx1) / rx;
        double uy = (y1 - cy1) / ry;
        double vx = (-x1 - cx1) / rx;
        double vy = (-y1 - cy1) / ry;
        double p, n;
        // Compute the angle start
        n = Math.sqrt((ux * ux) + (uy * uy));
        p = ux; // (1 * ux) + (0 * uy)
        sign = (uy < 0) ? -1.0 : 1.0;
        double angleStart = Math.toDegrees(sign * Math.acos(p / n));

        // Compute the angle extent
        n = Math.sqrt((ux * ux + uy * uy) * (vx * vx + vy * vy));
        p = ux * vx + uy * vy;
        sign = (ux * vy - uy * vx < 0) ? -1.0 : 1.0;
        double angleExtent = Math.toDegrees(sign * Math.acos(p / n));
        if (!sweepFlag && angleExtent > 0) {
            angleExtent -= 360f;
        } else if (sweepFlag && angleExtent < 0) {
            angleExtent += 360f;
        }
        angleExtent %= 360f;
        angleStart %= 360f;

        //
        // We can now build the resulting Arc2D in double precision
        //
        arc.x = cx - rx;
        arc.y = cy - ry;
        arc.width = rx * 2.0;
        arc.height = ry * 2.0;
        arc.start = -angleStart;
        arc.extent = -angleExtent;

        return arc;
    }
}
