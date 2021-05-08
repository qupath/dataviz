package net.mahdilamb.dataviz.figure;

import net.mahdilamb.dataviz.graphics.GraphicsContext;
import net.mahdilamb.dataviz.ui.Toolbar;

public final class Overlay extends Group {

    private final FigureBase<?> figure;

    private Tooltip tooltip;

    boolean contentHasChanges = true,
            tooltipHasChanges = false,
            toolbarHasChanges = false;

    private Toolbar toolbar;

    boolean toolbarEnabled = true;
    boolean toolbarVisible = true,
            toolbarAlwaysShown = false;

    boolean contentVisible = true;

    Overlay(FigureBase<?> figure) {
        this.figure = figure;
    }

    <T> void draw(final Renderer<T> renderer) {
        final GraphicsContext<T> canvas = renderer.getOverlayContext();
        canvas.reset();
        if (contentVisible) {
            for (final AbstractComponent component : getChildren()) {
                component.markLayoutAsOldQuietly();
                component.layout(renderer, 0, 0, figure.getWidth(), figure.getHeight());
                component.draw(renderer, canvas);
            }
            contentHasChanges = false;
        }

        if (toolbarEnabled && (toolbarAlwaysShown || toolbarVisible)) {
            if (toolbar != null) {
                if (tooltipHasChanges) {
                    getToolbar().layout(renderer, 0, 0, figure.getWidth(), figure.getHeight());
                    toolbarHasChanges = false;
                }
                getToolbar().draw(renderer, canvas);
            }
        }

        if (tooltip != null) {
            if (tooltipHasChanges) {
                tooltip.layout(renderer, 0, 0, figure.getWidth(), figure.getHeight());
                tooltipHasChanges = false;
            }
            tooltip.draw(renderer, canvas);
        }
        canvas.done();
        renderer.done();
    }

    private Toolbar getToolbar() {
        if (toolbar == null) {
            if ((toolbar = figure.createToolbar()) != null) {
                ((AbstractComponent) toolbar).setContext(context);
                toolbar.layout(context.getRenderer(), 0, 0, figure.getWidth(), figure.getHeight());
            }
        }
        return toolbar;
    }

    final void showTooltip(final Tooltip tooltip) {
        tooltipHasChanges |= (this.tooltip != (this.tooltip = tooltip));
    }

    final void clearTooltip() {
        tooltipHasChanges |= tooltip != (tooltip = null);
    }

    void setVisible(final boolean visible) {
        if (contentVisible != (contentVisible = visible)) {
            redraw();
        }
    }

    @Override
    protected Component getComponentAt(double x, double y) {
        if (toolbarEnabled) {
            if (getToolbar() != null) {
                toolbarHasChanges = toolbarVisible != (toolbarVisible = getToolbar().containsPoint(x, y));
                final Component toolbarComponent;
                if ((toolbarComponent = ((AbstractComponent) getToolbar()).getComponentAt(x, y)) != null) {
                    return toolbarComponent;
                }
            }
        }
        return contentVisible ? super.getComponentAt(x, y) : null;
    }
}
