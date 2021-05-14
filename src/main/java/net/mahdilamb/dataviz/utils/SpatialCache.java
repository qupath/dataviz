package net.mahdilamb.dataviz.utils;

import net.mahdilamb.dataviz.figure.AbstractComponent;
import net.mahdilamb.dataviz.utils.functions.BiDoubleBiIntFunction;
import net.mahdilamb.dataviz.utils.functions.BiDoubleObjConsumer;
import net.mahdilamb.dataviz.utils.rtree.RTree;
import net.mahdilamb.dataviz.utils.rtree.RectangularNode;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static net.mahdilamb.dataviz.utils.Numbers.ceilDiv;

/**
 * A cache of tiles that are stored in a 2D infinite plane
 *
 * @param <E> the type of the type object to store
 */
public final class SpatialCache<E> {

    private final List<RectangularNode<E>> lru = new LinkedList<>();
    private final RTree<RectangularNode<E>> cache = new RTree<>();
    private final BiDoubleBiIntFunction<E> backgroundCacheFunction;

    private double viewportWidth = -1, viewportHeight = -1;
    private final int tileWidth, tileHeight;
    private final int suggestedMaxTiles;
    private int maxTiles;
    private double xScale, yScale;
    private final BiDoubleBiIntFunction<E> cacheFunction;
    private final BiDoubleObjConsumer<E> useFunction;

    /**
     * Create a spatial cache
     *
     * @param maxTiles              the maximum suggested number of tiles (will differ depending on viewport dimensions)
     * @param tileWidth             the width of the tile
     * @param tileHeight            the height of the tile
     * @param tileCreator           the function used to create a tile (method args: x (double), y (double), width (int), height (int); returns a tile (E))
     * @param backgroundTileCreator the function used to create a tile in a background thread (method args: x (double), y (double), width (int), height (int); returns a tile (E))
     * @param tileConsumer          the function used to consume a tile (method args: x (double), y (double), tile (E))
     */
    public SpatialCache(int maxTiles, int tileWidth, int tileHeight, BiDoubleBiIntFunction<E> tileCreator, BiDoubleBiIntFunction<E> backgroundTileCreator, BiDoubleObjConsumer<E> tileConsumer) {
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.cacheFunction = Objects.requireNonNull(tileCreator);
        this.backgroundCacheFunction = Objects.requireNonNull(backgroundTileCreator);
        this.useFunction = Objects.requireNonNull(tileConsumer);
        this.suggestedMaxTiles = maxTiles;
    }

    /**
     * Create a spatial cache using the minimum number of tiles that supports the given viewport and 128x128 tiles
     *
     * @param tileCreator           the function used to create a tile (method args: x (double), y (double), width (int), height (int); returns a tile (E))
     * @param tileConsumer          the function used to consume a tile (method args: x (double), y (double), tile (E))
     * @param backgroundTileCreator the function used to create a tile in a background thread (method args: x (double), y (double), width (int), height (int); returns a tile (E))
     */
    public SpatialCache(BiDoubleBiIntFunction<E> tileCreator, BiDoubleBiIntFunction<E> backgroundTileCreator, BiDoubleObjConsumer<E> tileConsumer) {
        this(-1, 128, 128, tileCreator, backgroundTileCreator, tileConsumer);
    }

    private void use(double viewportWidth, double viewportHeight,
                     boolean xReversed, boolean yReversed,
                     double minX, double minY, double maxX, double maxY, BiDoubleBiIntFunction<E> cacheFunction, boolean draw, int padX, int padY) {
        if (hasViewportChanged(viewportWidth, viewportHeight)) {
            final double width = maxX - minX;
            final double height = maxY - minY;

            xScale = viewportWidth / width;
            yScale = viewportHeight / height;
            this.maxTiles = (int) Math.max((ceilDiv((long) Math.ceil(this.viewportWidth), tileWidth) + 2) * (ceilDiv((long) Math.ceil(this.viewportHeight), tileHeight) + 2), suggestedMaxTiles);
            clear();
        }

        //store size to check if changes have been made to cache
        final int oldSize = cache.size();
        //get the start and end positions in viewport space, aligned to grid
        final double startX = floorX(minX * xScale)-(padX*tileWidth),
                endX = ceilX(maxX * xScale)+(padX*tileWidth);
        final double startY = floorY(minY * yScale)-(padY*tileHeight),
                endY = ceilY(maxY * yScale)+(padY*tileHeight);
        //note the movement from the grid
        final double offsetX = (minX * xScale) - startX,
                offsetY = (minY * yScale) - startY;
        //calculate factors for x/y reverse
        final int directionX, directionY;
        final double flipX, flipY;
        if (xReversed) {
            directionX = -1;
            flipX = viewportWidth - tileWidth;
        } else {
            directionX = 1;
            flipX = 0;
        }

        if (yReversed) {
            directionY = -1;
            flipY = viewportHeight - tileHeight;
        } else {
            directionY = 1;
            flipY = 0;
        }
        final int midX = tileWidth >> 1,
                midY = tileHeight >> 1;
        //iterate through visible tiles
        for (double y = startY; y < endY; y += tileHeight) {
            final double _y = ((y - startY - offsetY) * directionY) + flipY;
            for (double x = startX; x < endX; x += tileWidth) {
                final double _x = ((x - startX - offsetX) * directionX) + flipX;
                //search for midpoints so that we aren't getting overlapping edge matches
                final List<? extends RectangularNode<E>> cached = cache.search(
                        x + midX, y + midY, x + midX, y + midY
                );
                final RectangularNode<E> cacheTile;
                if (cached.isEmpty()) {
                    //create tile
                    final E data = cacheFunction.apply(_x, _y, tileWidth, tileHeight);
                    if (data == null) {
                        continue;
                    }
                    cacheTile = new RectangularNode<>(
                            x, y, x + tileWidth, y + tileHeight,
                            data
                    );
                    cache.put(cacheTile);
                    lru.add(cacheTile);
                } else {
                    //extract cached
                    cacheTile = cached.get(0);
                    if (lru.remove(cacheTile)) {
                        lru.add(cacheTile);
                    }
                }
                if (draw && cacheTile.data != null) {
                    useFunction.accept(_x, _y, cacheTile.data);
                }
            }
        }
        if (cache.size() != oldSize) {
            trimToSize();
        }
    }

    /**
     * Draw using the spatial cache
     *
     * @param viewportWidth  the width of the viewport
     * @param viewportHeight the height of the viewport
     * @param xReversed      whether the x axis is reversed
     * @param yReversed      whether the y axis is reversed
     * @param minX           the minimum x of the world area in view
     * @param minY           the minimum y of the world area in view
     * @param maxX           the maximum x of the world area in view
     * @param maxY           the maximum y of the world area in view
     */
    public void draw(double viewportWidth, double viewportHeight,
                     boolean xReversed, boolean yReversed,
                     double minX, double minY, double maxX, double maxY) {
        use(viewportWidth, viewportHeight, xReversed, yReversed, minX, minY, maxX, maxY, cacheFunction, true,0,0);
    }

    /**
     * Draw using the spatial cache with the background task
     *
     * @param viewportWidth  the width of the viewport
     * @param viewportHeight the height of the viewport
     * @param xReversed      whether the x axis is reversed
     * @param yReversed      whether the y axis is reversed
     * @param minX           the minimum x of the world area in view
     * @param minY           the minimum y of the world area in view
     * @param maxX           the maximum x of the world area in view
     * @param maxY           the maximum y of the world area in view
     * @param padX           the number of tiles to pad in the x direction
     * @param padY           the number of tiles to pad ing the y direction
     */
    public void backgroundCreate(double viewportWidth, double viewportHeight,
                                 boolean xReversed, boolean yReversed,
                                 double minX, double minY, double maxX, double maxY, int padX, int padY) {
        use(viewportWidth, viewportHeight, xReversed, yReversed, minX, minY, maxX, maxY, backgroundCacheFunction, false,padX,padY);
    }

    /**
     * Draw using the spatial cache (assumes x and y axis are not reversed)
     *
     * @param viewportWidth  the width of the viewport
     * @param viewportHeight the height of the viewport
     * @param minX           the minimum x of the world area in view
     * @param minY           the minimum y of the world area in view
     * @param maxX           the maximum x of the world area in view
     * @param maxY           the maximum y of the world area in view
     */
    public void draw(double viewportWidth, double viewportHeight,
                     double minX, double minY, double maxX, double maxY) {
        draw(viewportWidth, viewportHeight, false, false, minX, minY, maxX, maxY);
    }

    /**
     * Clear the cache
     */
    public void clear() {
        lru.clear();
        cache.clear();
    }

    /**
     * Check the viewport for changes
     *
     * @param viewportWidth  the width of the viewport
     * @param viewportHeight the height of the viewport
     */
    private boolean hasViewportChanged(double viewportWidth, double viewportHeight) {
        if (this.viewportWidth != (viewportWidth) || this.viewportHeight != (viewportHeight)) {
            this.viewportWidth = viewportWidth;
            this.viewportHeight = viewportHeight;
            return true;
        }
        return false;

    }

    /**
     * Remove the first elements from the tree so that the number of tiles is the max tiles
     */
    @SuppressWarnings("unchecked")
    private void trimToSize() {
        if (lru.size() < maxTiles) {
            return;
        }
        lru.subList(0, lru.size() - maxTiles).clear();
        cache.clear();
        cache.putAll(lru.toArray(new RectangularNode[0]));
    }

    /**
     * Floor division with the tile width
     *
     * @param x the x
     * @return the floor division in tiles
     */
    private double floorX(double x) {
        return Math.floor(x / tileWidth) * tileWidth;
    }

    /**
     * Floor division with the tile height
     *
     * @param y the y
     * @return the floor division in tiles
     */
    private double floorY(double y) {
        return Math.floor(y / tileHeight) * tileHeight;
    }

    /**
     * Ceiling division with the tile width
     *
     * @param x the x
     * @return the ceiling division in tiles
     */
    private double ceilX(double x) {
        return Math.ceil(x / tileWidth) * tileWidth;
    }

    /**
     * Ceiling division with the tile height
     *
     * @param y the y
     * @return the ceiling division in tiles
     */
    private double ceilY(double y) {
        return Math.ceil(y / tileHeight) * tileHeight;
    }

}
