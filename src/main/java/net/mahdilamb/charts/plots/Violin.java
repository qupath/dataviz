package net.mahdilamb.charts.plots;

public final class Violin extends Box {
    boolean showBox = false;

    protected Violin(double[] values) {
        super(values);
        boxWidth = 0.25;

    }

    public Violin showBox(final boolean showBox) {
        this.showBox = showBox;
        return requestDataUpdate();
    }

    @Override
    protected Violin requestLayout() {
        return (Violin) super.requestLayout();
    }

    @Override
    protected Violin requestDataUpdate() {
        return (Violin) super.requestDataUpdate();
    }
}
