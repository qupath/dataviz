package net.mahdilamb.dataviz.ui;

import net.mahdilamb.dataviz.figure.AbstractComponent;
import net.mahdilamb.dataviz.figure.Group;
import net.mahdilamb.dataviz.figure.LayoutManager;

public class ButtonGroup<B extends Button> extends Group {
    public ButtonGroup(LayoutManager layoutManager) {
        super(layoutManager);
    }

    public ButtonGroup() {
    }

    public void add(B button) {
        button.group = this;
        add((AbstractComponent) button);
    }

    @Override
    protected int indexOf(AbstractComponent component) {
        return super.indexOf(component);
    }

    @Override
    protected int lastIndexOf(AbstractComponent component) {
        return super.lastIndexOf(component);
    }

    @Override
    public int size() {
        return super.size();
    }
}
