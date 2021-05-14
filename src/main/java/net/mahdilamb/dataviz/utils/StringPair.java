package net.mahdilamb.dataviz.utils;

import java.util.Objects;

public class StringPair {
    public final String a, b;

    public StringPair(String a, String b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StringPair)) return false;
        StringPair that = (StringPair) o;
        return a.equals(that.a) && b.equals(that.b);
    }

    @Override
    public int hashCode() {
        return Objects.hash(a, b);
    }

    @Override
    public String toString() {
        return "StringPair{" + a + ", " + b + '}';
    }
}
