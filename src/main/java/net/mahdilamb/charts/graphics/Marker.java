package net.mahdilamb.charts.graphics;

/**
 * The style of a marker
 */
public interface Marker {
    /**
     * @return the face color
     */
    Fill getFill();

    /**
     * @return the edge color
     */
    Stroke getStroke();

    /**
     * @return the size of the marker
     */
    double getSize();

    /**
     * @return the type of the marker
     */
    MarkerShape getShape();

    static void draw(ChartCanvas<?> canvas, double x, double y, Marker marker) {
        double r = marker.getSize() * .5;
        double minX, minY;
        switch (marker.getShape()) {
            case POINT:
                canvas.setFill(marker.getFill());
                double sqrtS = Math.sqrt(marker.getSize());
                double sr = sqrtS * .5;
                minX = x - sr;
                minY = y - sr;
                canvas.fillOval(minX, minY, sqrtS, sqrtS);
                if (marker.getStroke().getWidth() <= 0 || marker.getStroke().getColor() == null) {
                    return;
                }
                canvas.setStroke(marker.getStroke());
                canvas.strokeOval(minX, minY, sqrtS, sqrtS);
                return;
            case PIXEL:

                canvas.setFill(marker.getFill());
                minX = x - .5;
                minY = y - .5;
                canvas.fillRect(minX, minY, 1, 1);
                if (marker.getStroke().getWidth() <= 0 || marker.getStroke().getColor() == null) {
                    return;
                }
                canvas.setStroke(marker.getStroke());
                canvas.strokeRect(minX, minY, 1, 1);
                return;
            case CIRCLE:
                canvas.setFill(marker.getFill());
                minX = x - r;
                minY = y - r;
                canvas.fillOval(minX, minY, marker.getSize(), marker.getSize());
                if (marker.getStroke().getWidth() <= 0 || marker.getStroke().getColor() == null) {
                    return;
                }
                canvas.setStroke(marker.getStroke());
                canvas.strokeOval(minX, minY, marker.getSize(), marker.getSize());
                return;
            case SQUARE:
                minX = x - r;
                minY = y - r;
                canvas.setFill(marker.getFill());
                canvas.fillRect(minX, minY, marker.getSize(), marker.getSize());
                if (marker.getStroke().getWidth() <= 0 || marker.getStroke().getColor() == null) {
                    return;
                }
                canvas.setStroke(marker.getStroke());
                canvas.strokeRect(minX, minY, marker.getSize(), marker.getSize());
                return;
            case FILLED_PLUS:
                minX = x - r;
                minY = y - r;
                double sizeBy3 = marker.getSize() / 3;
                canvas.setFill(marker.getFill());
                canvas.fillRect(minX + sizeBy3, minY, sizeBy3, marker.getSize());
                canvas.fillRect(minX, minY + sizeBy3, marker.getSize(), sizeBy3);
                if (marker.getStroke().getWidth() <= 0 || marker.getStroke().getColor() == null) {
                    return;
                }
                canvas.beginPath();
                canvas.setStroke(marker.getStroke());
                canvas.strokeLine(minX + sizeBy3, minY, minX + sizeBy3 + sizeBy3, minY);
                canvas.strokeLine(minX + sizeBy3 + sizeBy3, minY + sizeBy3, minX + sizeBy3 + sizeBy3, minY);
                canvas.strokeLine(minX + sizeBy3 + sizeBy3, minY + sizeBy3, minX + marker.getSize(), minY + sizeBy3);
                canvas.strokeLine(minX + marker.getSize(), minY + sizeBy3, minX + marker.getSize(), minY + sizeBy3 + sizeBy3);
                canvas.strokeLine(minX + marker.getSize(), minY + sizeBy3 + sizeBy3, minX + sizeBy3 + sizeBy3, minY + sizeBy3 + sizeBy3);
                canvas.strokeLine(minX + sizeBy3 + sizeBy3, minY + sizeBy3 + sizeBy3, minX + sizeBy3 + sizeBy3, minY + marker.getSize());
                canvas.strokeLine(minX + sizeBy3 + sizeBy3, minY + marker.getSize(), minX + sizeBy3, minY + marker.getSize());
                canvas.strokeLine(minX + sizeBy3, minY + marker.getSize(), minX + sizeBy3, minY + sizeBy3 + sizeBy3);
                canvas.strokeLine(minX + sizeBy3, minY + sizeBy3 + sizeBy3, minX, minY + sizeBy3 + sizeBy3);
                canvas.strokeLine(minX, minY + sizeBy3 + sizeBy3, minX, minY + sizeBy3);
                canvas.strokeLine(minX, minY + sizeBy3, minX + sizeBy3, minY + sizeBy3);
                canvas.strokeLine(minX + sizeBy3, minY + sizeBy3, minX + sizeBy3, minY);
                return;
            // (STAR) adapted from https://stackoverflow.com/questions/16327588/how-to-make-star-shape-in-java
            case STAR:
                double deltaAngleRad = Math.PI / 5;
                double innerRadius = r / 2.63;
                canvas.setFill(marker.getFill());
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
                if (marker.getStroke().getWidth() <= 0 || marker.getStroke().getColor() == null) {
                    return;
                }
                canvas.setStroke(marker.getStroke());
                canvas.stroke();
                return;
            case PENTAGON:
            case HEXAGON1:
            case HEXAGON2:
            case OCTAGON:
            case DIAMOND:
            case TRIANGLE_UP:
            case TRIANGLE_LEFT:
            case TRIANGLE_RIGHT:
            case TRIANGLE_DOWN:
                fillRegularPolygon(canvas, x, y, r, marker);
                return;
            case THIN_DIAMOND:
                double sizeBy4 = marker.getSize() / 4;
                canvas.setFill(marker.getFill());
                canvas.beginPath();
                canvas.moveTo(x, y - r);
                canvas.lineTo(x + sizeBy4, y);
                canvas.lineTo(x, y + r);
                canvas.lineTo(x - sizeBy4, y);
                canvas.closePath();
                canvas.fill();
                if (marker.getStroke().getWidth() <= 0 || marker.getStroke().getColor() == null) {
                    return;
                }
                canvas.setStroke(marker.getStroke());
                canvas.stroke();
                return;
            case TRI_LEFT:
            case TRI_DOWN:
            case TRI_RIGHT:
            case TRI_UP:
                strokeTriangle(canvas, x, y, r, marker);
                return;
            case PLUS:
                canvas.setFill(marker.getFill());
                canvas.beginPath();
                canvas.moveTo(x, y - r);
                canvas.lineTo(x, y + r);
                canvas.moveTo(x - r, y);
                canvas.lineTo(x + r, y);
                canvas.stroke();
                if (marker.getStroke().getWidth() <= 0 || marker.getStroke().getColor() == null) {
                    return;
                }
                canvas.setStroke(marker.getStroke());
                canvas.stroke();
                return;
            case X:
                canvas.beginPath();
                canvas.setFill(marker.getFill());
                canvas.moveTo(x - r, y - r);
                canvas.lineTo(x + r, y + r);
                canvas.moveTo(x + r, y - r);
                canvas.lineTo(x - r, y + r);
                canvas.stroke();
                if (marker.getStroke().getWidth() <= 0 || marker.getStroke().getColor() == null) {
                    return;
                }
                canvas.setStroke(marker.getStroke());
                canvas.stroke();
                return;
            case HORIZONTAL_LINE:
                canvas.setFill(marker.getFill());
                canvas.strokeLine(x, y - r, x, y + r);
                if (marker.getStroke().getWidth() <= 0 || marker.getStroke().getColor() == null) {
                    return;
                }
                canvas.setStroke(marker.getStroke());
                canvas.strokeLine(x, y - r, x, y + r);
                return;
            case VERTICAL_LINE:
                canvas.setFill(marker.getFill());
                canvas.strokeLine(x - r, y, x + r, y);
                if (marker.getStroke().getWidth() <= 0 || marker.getStroke().getColor() == null) {
                    return;
                }
                canvas.setStroke(marker.getStroke());
                canvas.strokeLine(x - r, y, x + r, y);
                return;
            case FILLED_X:
                filledX(canvas, x, y, marker);
                return;

        }
    }

    private static void strokeTriangle(ChartCanvas<?> canvas, double x, double y, double r, Marker marker) {
        double j = 0;
        switch (marker.getShape()) {
            case TRI_LEFT:
                j = 1;
                break;
            case TRI_DOWN:
                j = Math.toRadians(90);
                break;
            case TRI_UP:
                j = Math.toRadians(-90);
                break;
            default:
                throw new UnsupportedOperationException();
        }
        double rad = Math.PI / (1.5);
        canvas.setFill(marker.getFill());
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
        canvas.fill();
        if (marker.getStroke().getWidth() <= 0 || marker.getStroke().getColor() == null) {
            return;
        }
        canvas.setStroke(marker.getStroke());
        canvas.stroke();
    }

    private static void filledX(ChartCanvas<?> canvas, double x, double y, Marker marker) {
        //divide into three
        double sby3 = marker.getSize() / 4;
        //get the size of the hypotenuse
        double sbysC = Math.sqrt(sby3 * sby3 + sby3 * sby3);
        //get the size of half the hypotenuse
        double sbysCby2 = sbysC * .5;

        canvas.beginPath();
        canvas.setFill(marker.getFill());
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
        canvas.fill();
        if (marker.getStroke().getWidth() <= 0 || marker.getStroke().getColor() == null) {
            return;
        }
        canvas.setStroke(marker.getStroke());
        canvas.stroke();
    }

    // Adapted from https://stackoverflow.com/questions/16327588/how-to-make-star-shape-in-java
    private static void fillRegularPolygon(ChartCanvas<?> canvas, double x, double y, double r, Marker marker) {
        int sides;
        double j = 0;
        switch (marker.getShape()) {
            case PENTAGON:
                sides = 5;
                j = Math.toRadians(-18);
                break;
            case HEXAGON1:
                sides = 6;
                j = Math.toRadians(-30);
                break;
            case HEXAGON2:
                sides = 6;
                j = 0;
                break;
            case OCTAGON:
                sides = 8;
                j = Math.toRadians(-22.5);
                break;
            case DIAMOND:
                sides = 4;
                j = 0;
                break;
            case TRIANGLE_UP:
                sides = 3;
                j = Math.toRadians(-90);
                break;
            case TRIANGLE_LEFT:
                sides = 3;
                j = Math.toRadians(-180);
                break;
            case TRIANGLE_RIGHT:
                sides = 3;
                break;
            case TRIANGLE_DOWN:
                j = Math.toRadians(90);
                sides = 3;
                break;
            default:
                throw new UnsupportedOperationException();
        }
        double rad = Math.PI / (sides * .5);

        canvas.setFill(marker.getFill());
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
        canvas.fill();
        if (marker.getStroke().getWidth() <= 0 || marker.getStroke().getColor() == null) {
            return;
        }
        canvas.setStroke(marker.getStroke());
        canvas.stroke();

    }

}

