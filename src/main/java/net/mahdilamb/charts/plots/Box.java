package net.mahdilamb.charts.plots;

public class Box extends Strip {
    public enum Whisker {
        TUKEY,
        SPEAR,
        ALTMAN
    }

    public enum Points {
        ALL,
        OUTLIERS,
        SUSPECTED_OUTLIERS,
        NONE
    }

    Whisker whisker = Whisker.TUKEY;
    Points points = Points.NONE;

    protected double whiskerWidth = 0.75, notchWidth = 0.25, boxWidth = 0.8;

    boolean useNotched = false, showMean = false, showSD = false;

    public Box(double[] values) {
        super(values);
    }

    @Override
    protected Box requestLayout() {
        return (Box) super.requestLayout();
    }

    @Override
    protected Box requestDataUpdate() {
        return (Box) super.requestDataUpdate();
    }

    public Box setNotched(final boolean notched) {
        this.useNotched = notched;
        return requestDataUpdate();
    }

    public Box showMean(boolean showMean) {
        this.showMean = showMean;
        return requestDataUpdate();
    }

    public Box showMeanAndSD(boolean useShowMeanAndSd) {
        this.showMean = useShowMeanAndSd;
        this.showSD = useShowMeanAndSd;
        return requestDataUpdate();
    }

    public Box setWhisker(final Whisker whisker) {
        this.whisker = whisker;
        return requestDataUpdate();
    }

    public Box setPoints(final Points points) {
        this.points = points;
        return requestDataUpdate();
    }

    public Box setJitter(double jitter) {
        this.jitter = jitter;
        return requestDataUpdate();
    }

    public Box setPointPos(double pointPos) {
        this.pointPos = pointPos;
        return requestDataUpdate();
    }

    public Box setNotchWidth(double width) {
        this.notchWidth = width;
        return requestDataUpdate();
    }

    public Box setBoxWidth(double width) {
        this.boxWidth = width;
        return requestDataUpdate();
    }
}
