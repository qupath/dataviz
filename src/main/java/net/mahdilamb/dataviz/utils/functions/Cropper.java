package net.mahdilamb.dataviz.utils.functions;

/**
 * A cropping method
 *
 * @param <IMG> the type of the input and output image
 */
@FunctionalInterface
public interface Cropper<IMG> {
    /**
     * Crop  an image
     *
     * @param source the source image
     * @param x      the x position
     * @param y      the y position in the source
     * @param width  the output width
     * @param height the output height
     * @return the cropped image
     */
    IMG crop(IMG source, int x, int y, int width, int height);
}
