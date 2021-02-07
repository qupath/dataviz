package net.mahdilamb.charts.graphics;

import net.mahdilamb.colormap.Color;

/**
 * Interface for the canvas element in a chart
 */
public interface ChartCanvas<IMAGE> {

    /**
     * Clears a specific rectangular area of the canvas.
     *
     * @apiNote Exporter canvases may ignore this. I.e. GUI-based canvas will use this and ignore reset (unless it's
     * the first draw), and exporter-canvases will ignore the reset
     * @param x      top-left x
     * @param y      top-left y
     * @param width  width of the rectangle
     * @param height height of the rectangle
     */
    void resetRect(double x, double y, double width, double height);

    /**
     * Called before the canvas is laid out. I.e. clears everything
     */
    void reset();

    /**
     * Method which is called when drawing is complete
     */
    void done();

    /**
     * Draw a stroked rectangle
     *
     * @param x      top-left x
     * @param y      top-left y
     * @param width  width of the rectangle
     * @param height height of the rectangle
     */
    void strokeRect(double x, double y, double width, double height);

    /**
     * Draw a filled rectangle
     *
     * @param x      top-left x
     * @param y      top-left y
     * @param width  width of the rectangle
     * @param height height of the rectangle
     */
    void fillRect(double x, double y, double width, double height);

    /**
     * Draw a stroked rectangle
     *
     * @param x         top-left x
     * @param y         top-left y
     * @param width     width of the rectangle
     * @param height    height of the rectangle
     * @param arcWidth  the arc width of the rounded rectangle
     * @param arcHeight the arc height of the rounded rectangle
     */
    void strokeRoundRect(double x, double y, double width, double height, double arcWidth, double arcHeight);

    /**
     * Draw a filled rectangle
     *
     * @param x         top-left x
     * @param y         top-left y
     * @param width     width of the rectangle
     * @param height    height of the rectangle
     * @param arcWidth  the arc width of the rounded rectangle
     * @param arcHeight the arc height of the rounded rectangle
     */
    void fillRoundRect(double x, double y, double width, double height, double arcWidth, double arcHeight);

    /**
     * Draw a stroked ellipse
     *
     * @param x      top-left x
     * @param y      top-left y
     * @param width  width of the ellipse
     * @param height height of the ellipse
     */
    void strokeOval(double x, double y, double width, double height);

    /**
     * Draw a filled ellipse
     *
     * @param x      top-left x
     * @param y      top-left y
     * @param width  width of the ellipse
     * @param height height of the ellipse
     */
    void fillOval(double x, double y, double width, double height);

    /**
     * Draw a line
     *
     * @param x0 the starting x
     * @param y0 the starting y
     * @param x1 the ending x
     * @param y1 the ending y
     */
    void strokeLine(double x0, double y0, double x1, double y1);


    /**
     * Set the fill
     *
     * @param fill the fill to set
     */
    void setFill(Fill fill);

    /**
     * Set the fill to a single color
     *
     * @param color the color to set the fill
     */
    default void setFill(Color color) {
        setFill(new Fill(color));
    }

    /**
     * Set the fill to a single color
     *
     * @param colorName the name of the color
     */
    default void setFill(final String colorName) {
        final Color color = Color.get(colorName);
        if (color == null) {
            throw new IllegalArgumentException(String.format("Color by the name %s could not be found", colorName));
        }
        setFill(color);
    }

    /**
     * Set the stroke
     *
     * @param stroke the stroke
     */
    void setStroke(Stroke stroke);

    /**
     * Begin a path
     */
    void beginPath();

    /**
     * Move the "cursor" of the path
     *
     * @param endX the x to move to
     * @param endY the y to move to
     */
    void moveTo(double endX, double endY);

    /**
     * Draw a line to the given point
     *
     * @param endX the end x
     * @param endY the end y
     */
    void lineTo(double endX, double endY);

    /**
     * Draw a quadratic bezier to the end point
     *
     * @param cpX  control point x
     * @param cpY  control point y
     * @param endX end x
     * @param endY end y
     */
    void quadTo(double cpX, double cpY, double endX, double endY);

    /**
     * Draw a cubic bezier to the end point
     *
     * @param cp1X control point 1 x
     * @param cp1Y control point 1 y
     * @param cp2X control point 2 x
     * @param cp2Y control point 2 y
     * @param endX end x
     * @param endY end y
     */
    void curveTo(double cp1X, double cp1Y, double cp2X, double cp2Y, double endX, double endY);

    /**
     * Closes the current path
     */
    void closePath();

    /**
     * Fills the current path
     */
    void fill();

    /**
     * Strokes the current path
     */
    void stroke();

    /**
     * Draw the outline of a polygon
     *
     * @param xPoints   the x points
     * @param yPoints   the y points
     * @param numPoints the number of points to draw
     */
    default void strokePolygon(double[] xPoints, double[] yPoints, int numPoints) {
        for (int i = 0, j = 1; j < numPoints; ++i, ++j) {
            strokeLine(xPoints[i], yPoints[i], xPoints[j], yPoints[j]);
        }
        int j = numPoints - 1;
        strokeLine(xPoints[0], yPoints[0], xPoints[j], yPoints[j]);

    }

    /**
     * Draw a polyline
     *
     * @param xPoints   the x points
     * @param yPoints   the y points
     * @param numPoints the number of points to draw
     */
    default void strokePolyline(double[] xPoints, double[] yPoints, int numPoints) {
        for (int i = 0, j = 1; j < numPoints; ++i, ++j) {
            strokeLine(xPoints[i], yPoints[i], xPoints[j], yPoints[j]);
        }
    }

    /**
     * Draw a filled polygon
     *
     * @param xPoints   the x points
     * @param yPoints   the y points
     * @param numPoints the number of points to draw
     */
    default void fillPolygon(double[] xPoints, double[] yPoints, int numPoints) {
        beginPath();
        moveTo(xPoints[0], yPoints[0]);
        if (numPoints > 0) {
            for (int i = 1; i < numPoints; i++) {
                lineTo(xPoints[i], yPoints[i]);
            }
        }
        closePath();
        fill();
    }

    /**
     * Draw the given text at the default position (i.e. x minus baseline)
     *
     * @param text the text to draw
     * @param x    the x position
     * @param y    the y position
     */
    void fillText(String text, double x, double y);

    void fillText(String text, double x, double y, double rotationDegrees, double pivotX, double pivotY);

    /**
     * Set the font of the canvas
     *
     * @param font the font
     */
    void setFont(final Font font);

    /**
     * Apply a clip
     *
     * @param shape  the shape of the clip
     * @param x      the x position of the clip
     * @param y      the y position of the clip
     * @param width  the width of the clip
     * @param height the height of the clip
     */
    void setClip(ClipShape shape, double x, double y, double width, double height);

    /**
     * Clear the current clip
     */
    void clearClip();

    void drawImage(IMAGE image, double x, double y);


}
