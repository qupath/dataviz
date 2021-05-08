package net.mahdilamb.dataviz.ui;

import net.mahdilamb.dataviz.utils.functions.Cropper;

import java.io.InputStream;
import java.util.Objects;

/**
 * Icon store
 *
 * @param <IMG> the type of the image
 */
public final class IconStore<IMG> {
    /**
     * Location of the icons file
     */
    public static final InputStream MATERIAL_ICONS =Objects.requireNonNull(IconStore.class.getClassLoader().getResourceAsStream("material_icons.png"));
    public static final InputStream DATAVIZ_ICONS = Objects.requireNonNull(IconStore.class.getClassLoader().getResourceAsStream("dataviz_icons.png"));

    /**
     * Enum for the current icon keys
     */
    public enum MaterialIconKey {
        /**
         * Get the capture image icon
         */
        PANORAMA(0),
        /**
         * Get the move icon
         */
        OPEN_WITH(1),
        /**
         * Get the reset icon
         */
        BASELINE_FULLSCREEN(2),
        /**
         * Get the manual zoom icon
         */
        SEARCH(3),
        /**
         * Zoom in icon
         */
        ADD_CIRCLE(4),
        /**
         * Zoom out icon
         */
        REMOVE_CIRCLE(5),
        /**
         * Add chart icon
         */
        ADD_CHART(6),
        /**
         * Show table icon
         */
        TABLE_VIEW(7),
        CHECK_BOX(8),
        CHECK_BOX_OUTLINE(9),
        FILTER(10),
        MENU(11),
        SORT(12),
        COLOR_LENS(13),
        SORT_RESERVED(14),
        CAMERA_ALT(15),
        LABEL(16);
        /**
         * The column of the icon
         */
        final int index;

        MaterialIconKey(final int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }
    }

    public enum DataVizIconKey {
        POLYGON_SELECTION(0);
        final int index;

        DataVizIconKey(final int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }
    }

    private final IMG source;
    private final Cropper<IMG> cropper;
    private final IMG[] darkIcons;
    private final IMG[] lightIcons;
    private final int iconWidth, iconHeight;

    /**
     * Create an icon store from an image. It is assumed that the top row is dark and the bottom row is light
     *
     * @param source  the source image
     * @param cols    the number of images
     * @param width   the width of the image
     * @param height  the height of the image
     * @param cropper the function that crops the image
     */
    @SuppressWarnings("unchecked")
    public IconStore(Class<? extends Enum<?>> key, IMG source, int cols, int width, int height, Cropper<IMG> cropper) {
        this.source = source;
        this.cropper = cropper;
        iconWidth = width / cols;
        iconHeight = height / 2;
        darkIcons = (IMG[]) new Object[key.getEnumConstants().length];
        lightIcons = (IMG[]) new Object[key.getEnumConstants().length];

    }

    /**
     * @param key the key
     * @return the dark of the image from the given column
     */
    public IMG getDark(int key) {
        return getImage(key, true);
    }

    /**
     * @param key the key
     * @return the light of the image from the given column
     */
    public IMG getLight(int key) {
        return getImage(key, false);
    }

    /**
     * @return the width of each icon
     */
    public int getIconWidth() {
        return iconWidth;
    }

    /**
     * @return the height of each icon
     */
    public int getIconHeight() {
        return iconHeight;
    }

    private IMG getImage(final int key, boolean dark) {
        final IMG[] store = dark ? darkIcons : lightIcons;
        if (store[key] != null) {
            return store[key];
        }
        final IMG cropped = cropper.crop(this.source, key * iconWidth, dark ? 0 : iconHeight, iconWidth, iconHeight);
        return store[key] = cropped;
    }
}
