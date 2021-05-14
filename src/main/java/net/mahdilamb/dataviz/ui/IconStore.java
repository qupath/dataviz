package net.mahdilamb.dataviz.ui;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Icon store
 */
public final class IconStore {
    private static final Map<InputStream, IconStore> icons = new HashMap<>();

    /**
     * Location of the icons file
     */
    public static final InputStream MATERIAL_ICONS = Objects.requireNonNull(IconStore.class.getClassLoader().getResourceAsStream("material_icons.png"));
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

    private final BufferedImage source;
    private final BufferedImage[] darkIcons;
    private final BufferedImage[] lightIcons;
    private final int iconWidth, iconHeight;

    /**
     * Create an icon store from an image. It is assumed that the top row is dark and the bottom row is light
     *
     * @param source the source image
     * @param cols   the number of images
     * @param width  the width of the image
     * @param height the height of the image
     */
    public IconStore(Class<? extends Enum<?>> key, BufferedImage source, int cols, int width, int height) {
        this.source = source;
        iconWidth = width / cols;
        iconHeight = height / 2;
        darkIcons = new BufferedImage[key.getEnumConstants().length];
        lightIcons = new BufferedImage[key.getEnumConstants().length];

    }

    private static BufferedImage loadImage(final InputStream stream) {
        try {
            return ImageIO.read(stream);
        } catch (IOException e) {
            e.printStackTrace();

        }
        System.err.println("could not load stream " + stream);
        return null;
    }

    public static IconStore get(final InputStream source, final Class<? extends Enum<?>> keys) {
        final IconStore icons = IconStore.icons.get(source);
        if (icons == null) {
            final BufferedImage img = loadImage(source);
            int width = img.getWidth();
            int height = img.getHeight();
            int cols = width / (height / 2);
            IconStore i = new IconStore(keys, img, cols, width, height);
            IconStore.icons.put(source, i);
            return i;
        }
        return icons;
    }

    /**
     * @param key the key
     * @return the dark of the image from the given column
     */
    public BufferedImage getDark(int key) {
        return getImage(key, true);
    }

    /**
     * @param key the key
     * @return the light of the image from the given column
     */
    public BufferedImage getLight(int key) {
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

    private BufferedImage getImage(final int key, boolean dark) {
        final BufferedImage[] store = dark ? darkIcons : lightIcons;
        if (store[key] != null) {
            return store[key];
        }
        final BufferedImage cropped = source.getSubimage(key * iconWidth, dark ? 0 : iconHeight, iconWidth, iconHeight);
        return store[key] = cropped;
    }
}
