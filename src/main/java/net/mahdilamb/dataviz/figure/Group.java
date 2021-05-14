package net.mahdilamb.dataviz.figure;

import net.mahdilamb.dataviz.graphics.GraphicsBuffer;
import net.mahdilamb.dataviz.graphics.GraphicsContext;
import net.mahdilamb.dataviz.ui.layouts.HBoxLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A group of components whose positions are controlled by a layout manager
 */
public class Group extends AbstractComponent {
    private static final LayoutManager DEFAULT_LAYOUT_MANAGER = HBoxLayout.INSTANCE;
    private final LayoutManager layoutManager;
    private List<AbstractComponent> children;

    /**
     * Create a node with the given layout manager
     *
     * @param layoutManager the layout manager
     */
    public Group(LayoutManager layoutManager) {
        this.layoutManager = Objects.requireNonNull(layoutManager);
    }

    /**
     * Create a node where the child components are laid out horizontally
     */
    public Group() {
        this.layoutManager = DEFAULT_LAYOUT_MANAGER;
    }

    @Override
    protected final void draw(Renderer renderer, GraphicsBuffer context) {

        drawComponent(renderer, context);
    }

    @Override
    final boolean hasChildren() {
        return children != null;
    }

    /**
     * @return the child components
     */
    protected final List<AbstractComponent> getChildren() {
        if (children == null) {
            children = new ArrayList<>();
        }
        return children;
    }

    protected int indexOf(final AbstractComponent component) {
        return children == null ? -1 : children.indexOf(component);
    }

    protected int lastIndexOf(final AbstractComponent component) {
        return children == null ? -1 : children.lastIndexOf(component);
    }

    protected int size() {
        return children == null ? 0 : children.size();
    }

    /**
     * Add a component to the node
     *
     * @param component the component to add
     */
    protected final void add(final AbstractComponent component) {
        getChildren().add(component);
        component.setContext(context);
        relayout();
    }

    protected final void add(int index, final AbstractComponent component) {
        getChildren().add(index, component);
        component.setContext(context);
        relayout();
    }

    /**
     * Add many components
     *
     * @param components the components to add
     */
    protected final void addAll(final AbstractComponent... components) {
        getChildren();
        for (final AbstractComponent component : components) {
            children.add(component);
            component.setContext(context);
            markLayoutAsOldQuietly();
        }
        relayout();
    }

    /**
     * Remove a component
     *
     * @param component the component to remove
     * @return whether the component was removed
     */
    protected final boolean remove(final AbstractComponent component) {
        if (children != null && children.remove(component)) {
            relayout();
            return true;
        }
        return false;
    }

    /**
     * Remove all children
     */
    protected final void clear() {
        if (children == null) {
            return;
        }
        children.clear();
    }

    @Override
    protected Component getComponentAt(double x, double y) {
        if (children != null) {
            for (final AbstractComponent c : children) {
                Component d = c.getComponentAt(x, y);
                if (d != null) {
                    return d;
                }
            }
        }
        return null;
    }

    @Override
    final void setContext(GraphicsContext context) {
        super.setContext(context);
        if (children != null) {
            for (final AbstractComponent c : children) {
                c.setContext(context);
            }
        }
    }

    @Override
    protected void layoutComponent(Renderer renderer, double minX, double minY, double maxX, double maxY) {
        if (children == null) {
            return;
        }
        layoutManager.layoutChildren(this, renderer, minX, minY, maxX, maxY);
    }

    @Override
    protected final void drawComponent(Renderer renderer, GraphicsBuffer canvas) {
        if (children == null) {
            return;
        }
        for (final AbstractComponent c : children) {
            c.draw(renderer, canvas);
        }
    }

    @Override
    protected final void markLayoutAsOldQuietly() {
        if (children != null) {
            for (final AbstractComponent c : children) {
                c.markLayoutAsOldQuietly();
            }
        }
    }

    @Override
    protected final void markDrawAsOldQuietly() {
        if (children != null) {
            for (final AbstractComponent c : children) {
                c.markDrawAsOldQuietly();
            }
        }
    }

    protected static void setBoundsFromRect(Component component, double boundsX, double boundsY, double boundsWidth, double boundsHeight) {
        component.setBoundsFromRect(boundsX, boundsY, boundsWidth, boundsHeight);
    }

    protected static void setBoundsFromExtent(Component component, double minX, double minY, double maxX, double maxY) {
        component.setBoundsFromExtent(minX, minY, maxX, maxY);

    }
}
