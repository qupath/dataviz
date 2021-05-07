package net.mahdilamb.dataviz.ui;

import net.mahdilamb.dataviz.figure.LayoutManager;

public class ToggleButtonGroup extends ButtonGroup<ToggleButton> {
    protected int defaultSelected = 0;
    boolean usingDefault = true;
    int selected = -1;

    public ToggleButtonGroup(LayoutManager layoutManager) {
        super(layoutManager);
    }

    public ToggleButtonGroup() {
    }

    public void setSelected(int i) {
        usingDefault = i < 0;
        if (i < 0) {
            i = defaultSelected;
        } else if (i > getChildren().size()) {
            throw new IndexOutOfBoundsException();
        }
        for (int j = 0; j < getChildren().size(); ++j) {
            ((ToggleButton) getChildren().get(j)).selected = i == j;
        }
        selected = i;
        redraw();
    }

    public void setSelected(ToggleButton toggleButton) {
        setSelected(getChildren().indexOf(toggleButton));
    }

    @Override
    public void add(ToggleButton button) {
        super.add(button);
        if (selected == -1 && usingDefault) {
            setSelected(-1);
        }
    }
}
