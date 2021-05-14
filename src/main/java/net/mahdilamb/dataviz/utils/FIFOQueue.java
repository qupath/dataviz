package net.mahdilamb.dataviz.utils;

import java.util.LinkedList;

public class FIFOQueue<E> extends LinkedList<E> {

    private final int limit;

    public FIFOQueue(int limit) {
        this.limit = limit;
    }

    @Override
    public boolean add(E o) {
        final boolean added = super.add(o);
        while (added && size() > limit) {
            super.remove();
        }
        return added;
    }
}
