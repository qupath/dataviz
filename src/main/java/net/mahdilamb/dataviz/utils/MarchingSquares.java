package net.mahdilamb.dataviz.utils;

import net.mahdilamb.dataframe.functions.BiIntConsumer;

/**
 * Utility class to perform marching squares. Code adapted from https://web.archive.org/web/20160517103224/http://devblog.phillipspiess.com/better%20know%20an%20algorithm/2010/02/23/better-know-marching-squares.html
 */
public final class MarchingSquares {

    private MarchingSquares() {

    }

    private enum Direction {
        UP, LEFT, DOWN, RIGHT, NONE
    }

    private static boolean isPixelSolid(double[] texture, int width, int height, int x, int y, double threshold) {
        // Make sure we don't pick a point outside our
        // image boundary!
        if (x < 0 || y < 0 || x >= width || y >= height) {
            return false;
        }
        // Check the color value of the pixel
        // If it isn't 100% transparent, it is solid
        return texture[x + y * width] >= threshold;

    }

    public static void marchingSquares(double[] data, int width, double threshold, BiIntConsumer point) {
        int startX = -1, startY = -1;
        for (int pixel = 0; pixel < data.length; ++pixel) {
            // If the pixel is not entirely transparent
            // we've found a start point
            if (data[pixel] >= threshold) {
                startX = pixel % width;
                startY = pixel / width;
            }
        }

        if (startX != -1) {
            int height = data.length / width;
            Direction nextStep = Direction.NONE;

            // Our current x and y positions, initialized
            // to the init values passed in
            int x = startX;
            int y = startY;

            // The main while loop, continues stepping until
            // we return to our initial points
            do {
                // Evaluate our state, and set up our next direction
                nextStep = step(data, width, height, x, y, threshold, nextStep);

                // If our current point is within our image
                // add it to the list of points
                if (x >= 0 && x < width && y >= 0 && y < height) {
                    point.accept(x, y);
                }

                switch (nextStep) {
                    case UP:
                        y--;
                        break;
                    case LEFT:
                        x--;
                        break;
                    case DOWN:
                        y++;
                        break;
                    case RIGHT:
                        x++;
                        break;
                    default:
                        break;
                }
            } while (x != startX || y != startY);

        }
    }

    private static Direction step(double[] data, int width, int height, int x, int y, double threshold, Direction lastStep) {
        boolean upLeft = isPixelSolid(data, width, height, x - 1, y - 1, threshold);
        boolean upRight = isPixelSolid(data, width, height, x, y - 1, threshold);
        boolean downLeft = isPixelSolid(data, width, height, x - 1, y, threshold);
        boolean downRight = isPixelSolid(data, width, height, x, y, threshold);
        int state = 0;
        if (upLeft) {
            state |= 1;
        }
        if (upRight) {
            state |= 2;
        }
        if (downLeft) {
            state |= 4;
        }
        if (downRight) {
            state |= 8;
        }
        switch (state) {
            case 1:
            case 5:
            case 13:
                return Direction.UP;
            case 2:
            case 3:
            case 7:
                return Direction.RIGHT;
            case 4:
            case 12:
            case 14:
                return Direction.LEFT;
            case 6:
                if (lastStep == Direction.UP) {
                    return Direction.LEFT;
                } else {
                    return Direction.RIGHT;
                }
            case 9:
                if (lastStep == Direction.RIGHT) {
                    return Direction.UP;
                } else {
                    return Direction.DOWN;
                }
            case 8:
            case 10:
            case 11:
                return Direction.DOWN;
            default:
                return Direction.NONE;
        }

    }
}
