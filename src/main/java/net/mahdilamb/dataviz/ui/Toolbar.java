package net.mahdilamb.dataviz.ui;

import net.mahdilamb.dataviz.figure.Group;
import net.mahdilamb.dataviz.figure.Renderer;
import net.mahdilamb.dataviz.figure.Tooltip;
import net.mahdilamb.dataviz.ui.layouts.HBoxLayout;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Toolbar used in the overlay
 */
public final class Toolbar extends Group {

    double paddingX = 2, paddingY = 0;

    public Toolbar() {
        super(HBoxLayout.INSTANCE);
    }

    public Button addIconButton(BufferedImage icon, final String tooltipText) {
        final Button button = new Button(icon);
        button.setTooltip(Tooltip.create(Color.DARK_GRAY, tooltipText));
        add(button);
        return button;
    }

    public Button addButton(String text, final String tooltipText) {
        final Button button = new Button(text);
        button.setTooltip(Tooltip.create(Color.DARK_GRAY, tooltipText));
        add(button);
        return button;
    }

    public ToggleButton addToggleButton(BufferedImage icon, final String tooltipText, boolean defaultSelected) {
        final ToggleButton button = new ToggleButton(icon, null,defaultSelected);
        button.setTooltip(Tooltip.create(Color.DARK_GRAY, tooltipText));
        add(button);
        return button;
    }

    public void addToggleButtonGroup(ToggleButton... buttons) {
        final ToggleButtonGroup group = new ToggleButtonGroup();
        for (final ToggleButton button : buttons) {
            group.add(button);
        }
        add(group);

    }

    public void addToggleButtonGroup(ToggleButtonGroup group) {
        add(group);
    }

    public HorizontalSpacing addSpacing() {
        add(HorizontalSpacing.HORIZONTAL_SPACING_5);
        return HorizontalSpacing.HORIZONTAL_SPACING_5;
    }

    @Override
    protected void layoutComponent(Renderer renderer, double minX, double minY, double maxX, double maxY) {
        minX += paddingX;
        minY += paddingY;
        maxX -= paddingX;
        maxY -= paddingY;
        super.layoutComponent(renderer, minX, minY, maxX, maxY);
        markLayoutAsOldQuietly();
        super.layoutComponent(renderer, maxX - getWidth() , minY , maxX, maxY);
        setBoundsFromRect(maxX - getWidth() - paddingX - paddingX, minY, getWidth() + paddingX * 2, getHeight() + paddingY * 2);
    }

}
