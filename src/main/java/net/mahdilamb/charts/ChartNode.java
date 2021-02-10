package net.mahdilamb.charts;

public abstract class ChartNode<C extends ChartComponent> extends ChartComponent {
    protected abstract boolean remove(C component);

    protected abstract void add(C component);
}
