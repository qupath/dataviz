package net.mahdilamb.charts.graphics.shapes;

import net.mahdilamb.charts.graphics.ChartCanvas;
import net.mahdilamb.charts.graphics.MarkerShape;

//TODO check all
final class Markers {
    private Markers() {
    }

    static void stroke(ChartCanvas<?> canvas, double x, double y, double size, MarkerShape marker) {
        double r = size * .5;
        double minX, minY;
        switch (marker) {
            case POINT:
                double sr = (size) * .5;
                minX = x - sr;
                minY = y - sr;
                canvas.strokeOval(minX, minY, (size), (size));
                return;
            case PIXEL:
                minX = x - .5;
                minY = y - .5;
                canvas.strokeRect(minX, minY, 1, 1);
                return;
            case CIRCLE:
                minX = x - r;
                minY = y - r;
                canvas.strokeOval(minX, minY, size, size);
                return;
            case SQUARE:
                minX = x - r;
                minY = y - r;
                canvas.strokeRect(minX, minY, size, size);
                return;
            case FILLED_PLUS:
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
                return;
            // (STAR) adapted from https://stackoverflow.com/questions/16327588/how-to-make-star-shape-in-java
            case STAR:
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
                double sizeBy4 = size / 4;
                canvas.beginPath();
                canvas.moveTo(x, y - r);
                canvas.lineTo(x + sizeBy4, y);
                canvas.lineTo(x, y + r);
                canvas.lineTo(x - sizeBy4, y);
                canvas.closePath();
                canvas.stroke();
                return;
            case TRI_LEFT:
            case TRI_DOWN:
            case TRI_RIGHT:
            case TRI_UP:
                strokeTriangle(canvas, x, y, r, marker);
                canvas.stroke();
                return;
            case PLUS:
                canvas.beginPath();
                canvas.moveTo(x, y - r);
                canvas.lineTo(x, y + r);
                canvas.moveTo(x - r, y);
                canvas.lineTo(x + r, y);
                canvas.stroke();
                return;
            case X:
                canvas.beginPath();
                canvas.moveTo(x - r, y - r);
                canvas.lineTo(x + r, y + r);
                canvas.moveTo(x + r, y - r);
                canvas.lineTo(x - r, y + r);

                canvas.stroke();
                return;
            case HORIZONTAL_LINE:
                canvas.strokeLine(x, y - r, x, y + r);
                return;
            case VERTICAL_LINE:
                canvas.strokeLine(x - r, y, x + r, y);
                return;
            case FILLED_X:
                filledX(canvas, x, y, size);
                canvas.stroke();
                return;
            default:
                throw new UnsupportedOperationException();

        }
    }

    static void fill(ChartCanvas<?> canvas, double x, double y, double size, MarkerShape marker) {
        double r = size * .5;
        double minX, minY;
        switch (marker) {
            case POINT:
                double sr = (size) * .5;
                minX = x - sr;
                minY = y - sr;
                canvas.fillOval(minX, minY, (size), (size));
                return;
            case PIXEL:
                minX = x - .5;
                minY = y - .5;
                canvas.fillRect(minX, minY, 1, 1);
                return;
            case CIRCLE:
                minX = x - r;
                minY = y - r;
                canvas.fillOval(minX, minY, size, size);
                return;
            case SQUARE:
                minX = x - r;
                minY = y - r;
                canvas.fillRect(minX, minY, size, size);
                return;
            case FILLED_PLUS:
                minX = x - r;
                minY = y - r;
                double sizeBy3 = size / 3;
                canvas.fillRect(minX + sizeBy3, minY, sizeBy3, size);
                canvas.fillRect(minX, minY + sizeBy3, size, sizeBy3);
                return;
            // (STAR) adapted from https://stackoverflow.com/questions/16327588/how-to-make-star-shape-in-java
            case STAR:
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
                double sizeBy4 = size / 4;
                canvas.beginPath();
                canvas.moveTo(x, y - r);
                canvas.lineTo(x + sizeBy4, y);
                canvas.lineTo(x, y + r);
                canvas.lineTo(x - sizeBy4, y);
                canvas.closePath();
                canvas.fill();
                return;
            case TRI_LEFT:
            case TRI_DOWN:
            case TRI_RIGHT:
            case TRI_UP:
                strokeTriangle(canvas, x, y, r, marker);
                canvas.fill();
                return;
            case PLUS:
                canvas.beginPath();
                canvas.moveTo(x, y - r);
                canvas.lineTo(x, y + r);
                canvas.moveTo(x - r, y);
                canvas.lineTo(x + r, y);
                canvas.fill();
                return;
            case X:
                canvas.beginPath();
                canvas.moveTo(x - r, y - r);
                canvas.lineTo(x + r, y + r);
                canvas.moveTo(x + r, y - r);
                canvas.lineTo(x - r, y + r);
                canvas.fill();
                return;
            case HORIZONTAL_LINE:
                canvas.strokeLine(x, y - r, x, y + r);
                return;
            case VERTICAL_LINE:
                canvas.strokeLine(x - r, y, x + r, y);
                return;
            case FILLED_X:
                filledX(canvas, x, y, size);
                canvas.fill();
                return;
            default:
                throw new UnsupportedOperationException();

        }
    }

    private static void strokeTriangle(ChartCanvas<?> canvas, double x, double y, double r, MarkerShape marker) {
        double j;
        switch (marker) {
            case TRI_RIGHT:
                j = 0;
                break;
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

    // Adapted from https://stackoverflow.com/questions/16327588/how-to-make-star-shape-in-java
    private static void fillRegularPolygon(ChartCanvas<?> canvas, double x, double y, double r, MarkerShape marker) {
        int sides;
        double j = 0;
        switch (marker) {
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

    }

}
