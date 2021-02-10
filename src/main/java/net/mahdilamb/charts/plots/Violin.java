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
    protected Violin redraw() {
        return (Violin) super.redraw();
    }

    @Override
    protected Violin requestDataUpdate() {
        return (Violin) super.requestDataUpdate();
    }
}
