package net.mahdilamb.dataviz.graphics.shapes;

import net.mahdilamb.dataframe.utils.PrimitiveIterators;
import net.mahdilamb.dataviz.graphics.ChartCanvas;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A marker for use in plots
 */
//todo default order: https://github.com/mwaskom/seaborn/blob/6183f1e3b0fc2b56ef441acfe1f9f63a13ae4beb/seaborn/_core.py#L1657
public enum MarkerShape {

    /**
     * A point marker
     */
    POINT('.', MarkerShape::strokePoint, MarkerShape::fillPoint),
    /**
     * A pixel marker ','
     */
    PIXEL(',', MarkerShape::strokePixel, MarkerShape::fillPixel),
    /**
     * Circle marker 'o'
     */
    CIRCLE('o', MarkerShape::strokeCircle, MarkerShape::fillCircle),
    /**
     * Square marker 's'
     */
    SQUARE('s', MarkerShape::strokeSquare, MarkerShape::fillSquare),
    /**
     * Filled plus marker 'P'
     */
    FILLED_PLUS('P', MarkerShape::strokeFilledPlus, MarkerShape::fillFilledPlus),
    /**
     * Star marker '*'
     */
    STAR('*', MarkerShape::strokeStar, MarkerShape::fillStar),
    /**
     * Pentagon marker 'p'
     */
    PENTAGON('p', MarkerShape::strokePentagon, MarkerShape::fillPentagon),
    /**
     * Hexagonal marker 'h'
     */
    HEXAGON1('h', MarkerShape::strokeHexagon1, MarkerShape::fillHexagon1),
    /**
     * Hexagonal marker 'H'
     */
    HEXAGON2('H', MarkerShape::strokeHexagon2, MarkerShape::fillHexagon2),
    /**
     * Diamond marker. 'D'
     */
    DIAMOND('D', MarkerShape::strokeDiamond, MarkerShape::fillDiamond),
    /**
     * Diamond marker with width 1/2 the size of the height. 'd'
     */
    THIN_DIAMOND('d', MarkerShape::strokeThinDiamond, MarkerShape::fillThinDiamond),
    /**
     * Left triangle marker '{@literal <}'
     */
    TRIANGLE_LEFT('<', MarkerShape::strokeTriangleLeft, MarkerShape::fillTriangleLeft),
    /**
     * Right triangle marker '>'
     */
    TRIANGLE_RIGHT('>', MarkerShape::strokeTriangleRight, MarkerShape::fillTriangleRight),
    /**
     * Up triangle marker '^'
     */
    TRIANGLE_UP('^', MarkerShape::strokeTriangleUp, MarkerShape::fillTriangleUp),
    /**
     * Down triangle marker 'v'
     */
    TRIANGLE_DOWN('v', MarkerShape::strokeTriangleDown, MarkerShape::fillTriangleDown),
    /**
     * Outline left triangle marker '1'
     */
    TRI_LEFT('1', MarkerShape::strokeTriLeft, MarkerShape::fillTriLeft),
    /**
     * Outline right triangle marker '2'
     */
    TRI_RIGHT('2', MarkerShape::strokeTriRight, MarkerShape::fillTriRight),
    /**
     * Outline up triangle marker '3'
     */
    TRI_UP('3', MarkerShape::strokeTriUp, MarkerShape::fillTriUp),
    /**
     * Outline down triangle marker '4'
     */
    TRI_DOWN('4', MarkerShape::strokeTriDown, MarkerShape::fillTriDown),
    /**
     * Octagonal marker '8'
     */
    OCTAGON('8', MarkerShape::strokeOctagon, MarkerShape::fillOctagon),
    /**
     * Outline plus marker '+'
     */
    PLUS('+', MarkerShape::strokePlus, MarkerShape::fillPlus),
    /**
     * Outline X marker 'x'
     */
    X('x', MarkerShape::strokeX, MarkerShape::fillX),
    /**
     * Filled X marker 'X'
     */
    FILLED_X('X', MarkerShape::strokeFilledX, MarkerShape::fillFilledX),
    /**
     * Vertical line marker '|'
     */
    VERTICAL_LINE('|', MarkerShape::strokeVerticalLine, MarkerShape::fillVerticalLine),
    /**
     * Horizontal line marker '_'
     */
    HORIZONTAL_LINE('_', MarkerShape::strokeHorizontalLine, MarkerShape::fillHorizontalLine);

    interface MarkerShapePainter {
        void paint(ChartCanvas<?> canvas, double x, double y, double size);
    }

    private static final Map<Character, MarkerShape> store = new HashMap<>();
    private static final char[] ordered = {'o', 'X', 'D', 'P', 's', '*', '^', '+', 'v', 'p', 'h', '8'};

    static {
        for (final MarkerShape m : MarkerShape.class.getEnumConstants()) {
            store.put(m.shortcut, m);
        }
    }

    private final char shortcut;
    final MarkerShapePainter stroke;
    final MarkerShapePainter fill;

    MarkerShape(char shortcut, MarkerShapePainter stroke, MarkerShapePainter fill) {
        this.shortcut = shortcut;
        this.stroke = stroke;
        this.fill = fill;
    }

    /**
     * Get a character from the marker
     *
     * @param character the character representation of a marker
     * @return the marker
     */
    public static MarkerShape get(final char character) {
        return store.get(character);
    }

    /**
     * Get a marker from a character sequence (the first matching marker)
     *
     * @param string the character sequence
     * @return the marker
     */
    public static MarkerShape get(final CharSequence string) {
        MarkerShape out = null;
        int i = 0;
        while (out == null && i < string.length()) {
            out = get(string.charAt(i++));
        }
        return out;
    }

    /**
     * Get the markers from each character in a string
     *
     * @param out    the output list
     * @param string the string
     * @return the output list
     */
    public static List<MarkerShape> get(final List<MarkerShape> out, final CharSequence string) {
        int i = 0;
        while (i < string.length()) {
            final MarkerShape m = get(string.charAt(i++));
            if (m != null) {
                out.add(m);
            }
        }
        return out;
    }

    /**
     * Get a marker from the ordered list
     *
     * @param order the order
     * @return the marker at the position
     */
    public static MarkerShape get(int order) {
        return get(ordered[order]);
    }

    /**
     * Get an iterable over the first n ordered markers
     *
     * @param numValues the number of values
     * @return an iterable over the default order of markers
     * @throws ArrayIndexOutOfBoundsException if the number is greater than 12
     */
    public static Iterable<Character> orderedValues(int numValues) {
        return () -> new PrimitiveIterators.OfCharacter() {
            private int i = 0;

            @Override
            public boolean hasNext() {
                return i < numValues;
            }

            @Override
            public char nextChar() {
                return ordered[i++];
            }
        };
    }

    private static void strokePoint(ChartCanvas<?> canvas, double x, double y, double size) {
        double minX, minY;
        double sr = (size) * .5;
        minX = x - sr;
        minY = y - sr;
        canvas.strokeOval(minX, minY, (size), (size));
    }

    private static void strokePixel(ChartCanvas<?> canvas, double x, double y, double size) {
        double minX, minY;
        minX = x - .5;
        minY = y - .5;
        canvas.strokeRect(minX, minY, 1, 1);
    }

    private static void strokeCircle(ChartCanvas<?> canvas, double x, double y, double size) {
        double minX, minY;
        double r = size * .5;
        minX = x - r;
        minY = y - r;
        canvas.strokeOval(minX, minY, size, size);
    }

    private static void strokeSquare(ChartCanvas<?> canvas, double x, double y, double size) {
        double minX, minY;
        double r = size * .5;
        minX = x - r;
        minY = y - r;
        canvas.strokeRect(minX, minY, size, size);
    }

    private static void strokeFilledPlus(ChartCanvas<?> canvas, double x, double y, double size) {
        double minX, minY;
        double r = size * .5;
        minX = x - r;
        minY = y - r;
        double sizeBy3 = size / 3;
        canvas.beginPath();
        canvas.strokeLine(minX + sizeBy3, minY, minX + sizeBy3 + sizeBy3, minY);
        canvas.strokeLine(minX + sizeBy3 + sizeBy3, minY + sizeBy3, minX + sizeBy3 + sizeBy3, minY);
        canvas.strokeLine(minX + sizeBy3 + sizeBy3, minY + sizeBy3, minX + size, minY + sizeBy3);
        canvas.strokeLine(minX + size, minY + sizeBy3, minX + size, minY + sizeBy3 + sizeBy3);
        canvas.strokeLine(minX + size, minY + sizeBy3 + sizeBy3, minX + sizeBy3 + sizeBy3, minY + sizeBy3 + sizeBy3);
        canvas.strokeLine(minX + sizeBy3 + sizeBy3, minY + sizeBy3 + sizeBy3, minX + sizeBy3 + sizeBy3, minY + size);
        canvas.strokeLine(minX + sizeBy3 + sizeBy3, minY + size, minX + sizeBy3, minY + size);
        canvas.strokeLine(minX + sizeBy3, minY + size, minX + sizeBy3, minY + sizeBy3 + sizeBy3);
        canvas.strokeLine(minX + sizeBy3, minY + sizeBy3 + sizeBy3, minX, minY + sizeBy3 + sizeBy3);
        canvas.strokeLine(minX, minY + sizeBy3 + sizeBy3, minX, minY + sizeBy3);
        canvas.strokeLine(minX, minY + sizeBy3, minX + sizeBy3, minY + sizeBy3);
        canvas.strokeLine(minX + sizeBy3, minY + sizeBy3, minX + sizeBy3, minY);
    }

    // (STAR) adapted from https://stackoverflow.com/questions/16327588/how-to-make-star-shape-in-java
    private static void strokeStar(ChartCanvas<?> canvas, double x, double y, double size) {
        double r = size * .5;
        double deltaAngleRad = Math.PI / 5;
        double innerRadius = r / 2.63;
        canvas.beginPath();
        for (int i = 0; i < 5 * 2; i++) {
            double angleRad = Math.toRadians(-18) + i * deltaAngleRad;
            double ca = Math.cos(angleRad);
            double sa = Math.sin(angleRad);
            double relX = ca;
            double relY = sa;
            if ((i & 1) == 0) {
                relX *= r;
                relY *= r;
            } else {
                relX *= innerRadius;
                relY *= innerRadius;
            }
            if (i == 0) {
                canvas.moveTo(x + relX, y + relY);
            } else {
                canvas.lineTo(x + relX, y + relY);
            }
        }
        canvas.closePath();
        canvas.stroke();
    }

    private static void strokePentagon(ChartCanvas<?> canvas, double x, double y, double size) {
        double r = size * .5;
        regularPolygonPath(canvas, x, y, r, Math.toRadians(-18), 5);
        canvas.stroke();
    }

    private static void strokeHexagon1(ChartCanvas<?> canvas, double x, double y, double size) {
        double r = size * .5;
        regularPolygonPath(canvas, x, y, r, Math.toRadians(-30), 6);
        canvas.stroke();
    }

    private static void strokeHexagon2(ChartCanvas<?> canvas, double x, double y, double size) {
        double r = size * .5;
        regularPolygonPath(canvas, x, y, r, 0, 6);
        canvas.stroke();
    }

    private static void strokeOctagon(ChartCanvas<?> canvas, double x, double y, double size) {
        double r = size * .5;
        regularPolygonPath(canvas, x, y, r, Math.toRadians(22.5), 8);
        canvas.stroke();
    }

    private static void strokeDiamond(ChartCanvas<?> canvas, double x, double y, double size) {
        double r = size * .5;
        regularPolygonPath(canvas, x, y, r, 0, 4);
        canvas.stroke();
    }

    private static void strokeTriangleUp(ChartCanvas<?> canvas, double x, double y, double size) {
        double r = size * .5;
        regularPolygonPath(canvas, x, y, r, Math.toRadians(-90), 3);
        canvas.stroke();
    }

    private static void strokeTriangleLeft(ChartCanvas<?> canvas, double x, double y, double size) {
        double r = size * .5;
        regularPolygonPath(canvas, x, y, r, Math.toRadians(-180), 3);
        canvas.stroke();
    }

    private static void strokeTriangleRight(ChartCanvas<?> canvas, double x, double y, double size) {
        double r = size * .5;
        regularPolygonPath(canvas, x, y, r, 0, 3);
        canvas.stroke();
    }

    private static void strokeTriangleDown(ChartCanvas<?> canvas, double x, double y, double size) {
        double r = size * .5;
        regularPolygonPath(canvas, x, y, r, 90, 3);
        canvas.stroke();
    }

    private static void strokeThinDiamond(ChartCanvas<?> canvas, double x, double y, double size) {
        double r = size * .5;
        double sizeBy4 = size / 4;
        canvas.beginPath();
        canvas.moveTo(x, y - r);
        canvas.lineTo(x + sizeBy4, y);
        canvas.lineTo(x, y + r);
        canvas.lineTo(x - sizeBy4, y);
        canvas.closePath();
        canvas.stroke();
    }

    private static void strokeTriLeft(ChartCanvas<?> canvas, double x, double y, double size) {
        double r = size * .5;
        triPath(canvas, x, y, r, 0);
        canvas.stroke();
    }

    private static void strokeTriRight(ChartCanvas<?> canvas, double x, double y, double size) {
        double r = size * .5;
        triPath(canvas, x, y, r, 1);
        canvas.stroke();
    }

    private static void strokeTriUp(ChartCanvas<?> canvas, double x, double y, double size) {
        double r = size * .5;
        triPath(canvas, x, y, r, Math.toRadians(-90));
        canvas.stroke();
    }

    private static void strokeTriDown(ChartCanvas<?> canvas, double x, double y, double size) {
        double r = size * .5;
        triPath(canvas, x, y, r, Math.toRadians(90));
        canvas.stroke();
    }

    private static void strokePlus(ChartCanvas<?> canvas, double x, double y, double size) {
        double r = size * .5;
        canvas.beginPath();
        canvas.moveTo(x, y - r);
        canvas.lineTo(x, y + r);
        canvas.moveTo(x - r, y);
        canvas.lineTo(x + r, y);
        canvas.stroke();
    }

    private static void strokeX(ChartCanvas<?> canvas, double x, double y, double size) {
        double r = size * .5;
        canvas.beginPath();
        canvas.moveTo(x - r, y - r);
        canvas.lineTo(x + r, y + r);
        canvas.moveTo(x + r, y - r);
        canvas.lineTo(x - r, y + r);
        canvas.stroke();
    }

    private static void strokeHorizontalLine(ChartCanvas<?> canvas, double x, double y, double size) {
        double r = size * .5;
        canvas.strokeLine(x, y - r, x, y + r);

    }

    private static void strokeVerticalLine(ChartCanvas<?> canvas, double x, double y, double size) {
        double r = size * .5;
        canvas.strokeLine(x - r, y, x + r, y);
    }

    private static void strokeFilledX(ChartCanvas<?> canvas, double x, double y, double size) {
        filledX(canvas, x, y, size);
        canvas.stroke();
    }

    private static void fillPoint(ChartCanvas<?> canvas, double x, double y, double size) {
        double minX, minY;
        double sr = (size) * .5;
        minX = x - sr;
        minY = y - sr;
        canvas.fillOval(minX, minY, (size), (size));
    }

    private static void fillPixel(ChartCanvas<?> canvas, double x, double y, double size) {
        double minX, minY;
        minX = x - .5;
        minY = y - .5;
        canvas.fillRect(minX, minY, 1, 1);
    }

    private static void fillCircle(ChartCanvas<?> canvas, double x, double y, double size) {
        double r = size * .5;
        double minX, minY;
        minX = x - r;
        minY = y - r;
        canvas.fillOval(minX, minY, size, size);
    }

    private static void fillSquare(ChartCanvas<?> canvas, double x, double y, double size) {
        double r = size * .5;
        double minX, minY;
        minX = x - r;
        minY = y - r;
        canvas.fillRect(minX, minY, size, size);
    }

    private static void fillFilledPlus(ChartCanvas<?> canvas, double x, double y, double size) {
        double r = size * .5;
        double minX, minY;
        minX = x - r;
        minY = y - r;
        double sizeBy3 = size / 3;
        canvas.fillRect(minX + sizeBy3, minY, sizeBy3, size);
        canvas.fillRect(minX, minY + sizeBy3, size, sizeBy3);
    }

    // (STAR) adapted from https://stackoverflow.com/questions/16327588/how-to-make-star-shape-in-java
    private static void fillStar(ChartCanvas<?> canvas, double x, double y, double size) {
        double r = size * .5;
        double deltaAngleRad = Math.PI / 5;
        double innerRadius = r / 2.63;
        canvas.beginPath();
        for (int i = 0; i < 5 * 2; i++) {
            double angleRad = Math.toRadians(-18) + i * deltaAngleRad;
            double ca = Math.cos(angleRad);
            double sa = Math.sin(angleRad);
            double relX = ca;
            double relY = sa;
            if ((i & 1) == 0) {
                relX *= r;
                relY *= r;
            } else {
                relX *= innerRadius;
                relY *= innerRadius;
            }
            if (i == 0) {
                canvas.moveTo(x + relX, y + relY);
            } else {
                canvas.lineTo(x + relX, y + relY);
            }
        }
        canvas.closePath();
        canvas.fill();
    }

    private static void fillPentagon(ChartCanvas<?> canvas, double x, double y, double size) {
        double r = size * .5;
        regularPolygonPath(canvas, x, y, r, Math.toRadians(-18), 5);
        canvas.fill();
    }

    private static void fillHexagon1(ChartCanvas<?> canvas, double x, double y, double size) {
        double r = size * .5;
        regularPolygonPath(canvas, x, y, r, Math.toRadians(-30), 6);
        canvas.fill();
    }

    private static void fillHexagon2(ChartCanvas<?> canvas, double x, double y, double size) {
        double r = size * .5;
        regularPolygonPath(canvas, x, y, r, 0, 6);
        canvas.fill();
    }

    private static void fillOctagon(ChartCanvas<?> canvas, double x, double y, double size) {
        double r = size * .5;
        regularPolygonPath(canvas, x, y, r, Math.toRadians(22.5), 8);
        canvas.fill();
    }

    private static void fillDiamond(ChartCanvas<?> canvas, double x, double y, double size) {
        double r = size * .5;
        regularPolygonPath(canvas, x, y, r, 0, 4);
        canvas.fill();
    }

    private static void fillTriangleUp(ChartCanvas<?> canvas, double x, double y, double size) {
        double r = size * .5;
        regularPolygonPath(canvas, x, y, r, Math.toRadians(-90), 3);
        canvas.fill();
    }

    private static void fillTriangleLeft(ChartCanvas<?> canvas, double x, double y, double size) {
        double r = size * .5;
        regularPolygonPath(canvas, x, y, r, Math.toRadians(-180), 3);
        canvas.fill();
    }

    private static void fillTriangleRight(ChartCanvas<?> canvas, double x, double y, double size) {
        double r = size * .5;
        regularPolygonPath(canvas, x, y, r, 0, 3);
        canvas.fill();
    }

    private static void fillTriangleDown(ChartCanvas<?> canvas, double x, double y, double size) {
        double r = size * .5;
        regularPolygonPath(canvas, x, y, r, 90, 3);
        canvas.fill();
    }

    private static void fillThinDiamond(ChartCanvas<?> canvas, double x, double y, double size) {
        double r = size * .5;
        double sizeBy4 = size / 4;
        canvas.beginPath();
        canvas.moveTo(x, y - r);
        canvas.lineTo(x + sizeBy4, y);
        canvas.lineTo(x, y + r);
        canvas.lineTo(x - sizeBy4, y);
        canvas.closePath();
        canvas.fill();
    }

    private static void fillTriLeft(ChartCanvas<?> canvas, double x, double y, double size) {
        double r = size * .5;
        triPath(canvas, x, y, r, 1);
        canvas.fill();
    }

    private static void fillTriDown(ChartCanvas<?> canvas, double x, double y, double size) {
        double r = size * .5;
        triPath(canvas, x, y, r, Math.toRadians(90));
        canvas.fill();
    }

    private static void fillTriRight(ChartCanvas<?> canvas, double x, double y, double size) {
        double r = size * .5;
        triPath(canvas, x, y, r, 0);
        canvas.fill();
    }

    private static void fillTriUp(ChartCanvas<?> canvas, double x, double y, double size) {
        double r = size * .5;
        triPath(canvas, x, y, r, Math.toRadians(-90));
        canvas.fill();
    }

    private static void fillPlus(ChartCanvas<?> canvas, double x, double y, double size) {
        double r = size * .5;
        canvas.beginPath();
        canvas.moveTo(x, y - r);
        canvas.lineTo(x, y + r);
        canvas.moveTo(x - r, y);
        canvas.lineTo(x + r, y);
        canvas.fill();
    }

    private static void fillX(ChartCanvas<?> canvas, double x, double y, double size) {
        double r = size * .5;
        canvas.beginPath();
        canvas.moveTo(x - r, y - r);
        canvas.lineTo(x + r, y + r);
        canvas.moveTo(x + r, y - r);
        canvas.lineTo(x - r, y + r);
        canvas.fill();
    }

    private static void fillHorizontalLine(ChartCanvas<?> canvas, double x, double y, double size) {
        double r = size * .5;
        canvas.strokeLine(x, y - r, x, y + r);
    }

    private static void fillVerticalLine(ChartCanvas<?> canvas, double x, double y, double size) {
        double r = size * .5;
        canvas.strokeLine(x - r, y, x + r, y);
    }

    private static void fillFilledX(ChartCanvas<?> canvas, double x, double y, double size) {
        filledX(canvas, x, y, size);
        canvas.fill();
    }

    private static void triPath(ChartCanvas<?> canvas, double x, double y, double r, double j) {
        double rad = Math.PI / (1.5);
        canvas.beginPath();
        for (int i = 0; i < 6; i++) {
            double angleRad = j + i * rad;
            double relX = 0;
            double relY = 0;
            if ((i & 1) == 0) {
                relX = Math.cos(angleRad);
                relY = Math.sin(angleRad);
                relX *= r;
                relY *= r;
            }
            if (i == 0) {
                canvas.moveTo(x + relX, y + relY);
            } else {
                canvas.lineTo(x + relX, y + relY);
            }
        }
        canvas.closePath();
    }

    private static void filledX(ChartCanvas<?> canvas, double x, double y, double size) {
        //divide into three
        double sby3 = size / 3;
        //get the size of the hypotenuse
        double sbysC = Math.sqrt(sby3 * sby3 + sby3 * sby3);
        //get the size of half the hypotenuse
        double sbysCby2 = sbysC * .5;

        canvas.beginPath();
        canvas.moveTo(x, y - sbysCby2);
        canvas.lineTo(x + sbysCby2, y - sbysCby2 - sbysCby2);
        canvas.lineTo(x + sbysCby2 + sbysCby2, y - sbysCby2);
        canvas.lineTo(x + sbysCby2, y);
        canvas.lineTo(x + sbysCby2 + sbysCby2, y + sbysCby2);
        canvas.lineTo(x + sbysCby2, y + sbysCby2 + sbysCby2);
        canvas.lineTo(x, y + sbysCby2);
        canvas.lineTo(x - sbysCby2, y + sbysCby2 + sbysCby2);
        canvas.lineTo(x - sbysCby2 - sbysCby2, y + sbysCby2);
        canvas.lineTo(x - sbysCby2, y);
        canvas.lineTo(x - sbysCby2 - sbysCby2, y - sbysCby2);
        canvas.lineTo(x - sbysCby2, y - sbysCby2 - sbysCby2);
        canvas.closePath();
    }

    private static void regularPolygonPath(ChartCanvas<?> canvas, double x, double y, double r, double j, int sides) {
        double rad = Math.PI / (sides * .5);
        canvas.beginPath();
        for (int i = 0; i < sides; i++) {
            double angleRad = j + i * rad;
            double ca = Math.cos(angleRad);
            double sa = Math.sin(angleRad);
            double relX = ca;
            double relY = sa;
            relX *= r;
            relY *= r;
            if (i == 0) {
                canvas.moveTo(x + relX, y + relY);
            } else {
                canvas.lineTo(x + relX, y + relY);
            }
        }
        canvas.closePath();

    }
}
