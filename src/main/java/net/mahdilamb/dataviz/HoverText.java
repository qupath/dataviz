package net.mahdilamb.dataviz;

import net.mahdilamb.dataviz.utils.StringUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.IntFunction;
import java.util.function.Supplier;

import static net.mahdilamb.dataviz.utils.StringUtils.EMPTY_STRING;

public final class HoverText<O extends PlotData<O>> {

    public static class Segment {
        String formatting;
        final Supplier<?>[] suppliers;

        Segment(final String formatting, final Supplier<?>[] suppliers) {
            this.formatting = formatting;
            this.suppliers = suppliers;
            int args = 0;
            int i = 0;
            //make sure arguments match
            while (i < formatting.length()) {
                final char c = formatting.charAt(i);
                if (c == '%') {
                    int j = i + 1;
                    if (i + 1 < formatting.length() && (formatting.charAt(j) != '%')) {
                        if (formatting.charAt(j) == '{') {
                            while (formatting.charAt(i) != '}') {
                                ++i;
                            }
                            ++i;
                            continue;
                        } else {
                            ++args;
                        }
                    }
                }
                ++i;
            }
            if (args != suppliers.length) {
                throw new IllegalArgumentException("Not enough arguments for formatting");
            }
        }

        @Override
        public String toString() {
            return String.format("HoverSegment [%s]", formatting);
        }
    }

    private static final String DEFAULT_JOIN = ";";

    private final List<Segment> segments = new LinkedList<>();
    private final Map<String, IntFunction<?>> formatters;
    private final StringBuilder sb = new StringBuilder();
    private final String join;
    private final O plotData;

    public HoverText(final O plotData, final Map<String, IntFunction<?>> formatters, String join) {
        this.formatters = formatters;
        this.plotData = plotData;
        this.join = join == null ? DEFAULT_JOIN : join;
    }

    public HoverText(final O plotData, final Map<String, IntFunction<?>> formatters) {
        this(plotData, formatters, ";");
    }

    void put(String key, IntFunction<?> getter) {
        formatters.put(key, getter);
        clear();
    }

    public Segment add(final String formatting, Supplier<?>... suppliers) {
        final Segment segment = new Segment(formatting, suppliers);
        segments.add(segment);
        clear();
        return segment;
    }

    private void clear() {
        plotData.hoverText = null;
    }

    public void remove(Segment seg) {
        segments.remove(seg);
        clear();
    }

    public void remove(PlotTrace seg) {
        remove(seg.defaultSeg);
    }

    void setFormatting(final String key, final String formatting) {
        for (final Segment segment : segments) {
            int i = 0;
            final int j = segment.formatting.length() - 1;
            while (i < j) {
                final char c = segment.formatting.charAt(i);
                if (c == '%') {
                    int k = i + 1;
                    int l = 0;
                    if (segment.formatting.charAt(k) == '{') {
                        boolean found = true;
                        while (l < key.length()) {
                            if (key.charAt(l) != segment.formatting.charAt(k + l + 1)) {
                                found = false;
                                break;
                            }
                            ++l;
                        }
                        if (found) {
                            int m = k + 2 + key.length();
                            int n = m;
                            while (segment.formatting.charAt(n) != '}') {
                                ++n;
                            }
                            segment.formatting = String.format("%s%s%s", segment.formatting.substring(0, m), formatting.substring(1), segment.formatting.substring(n));
                            clear();
                            return;
                        }
                    }
                }
                ++i;
            }
        }
    }

    public String get(int i) {
        sb.setLength(0);
        for (final Segment segment : segments) {
            final int j = segment.formatting.length() - 1;
            int k = 0, o = 0;
            while (k < j) {
                final char c = segment.formatting.charAt(k);
                if (c != '%') {
                    sb.append(c);
                } else {
                    int a = k + 1;
                    if (segment.formatting.charAt(a) != '%') {
                        int b = a;
                        if (segment.formatting.charAt(a) == '{') {
                            int w = b;
                            while (segment.formatting.charAt(b) != '}') {
                                if (segment.formatting.charAt(b) == ':') {
                                    w = b;
                                }
                                ++b;
                            }
                            final String name = segment.formatting.substring(k + 2, w);
                            final String formatting = String.format("%%%s", segment.formatting.substring(w + 1, b));
                            final IntFunction<?> fn = formatters.get(name);
                            if (fn == null) {
                                throw new UnsupportedOperationException("Could not find attribute with the name " + name);
                            }
                            sb.append(String.format(formatting, fn.apply(i)));

                        } else {
                            while (!StringUtils.isFormatSpecifier(segment.formatting.charAt(b))) {
                                ++b;
                            }
                            sb.append(String.format(segment.formatting.substring(k, b + 1), segment.suppliers[o++].get()));
                        }
                        k = b + 1;
                        continue;
                    }
                }
                ++k;
            }
            if (k != segment.formatting.length()) {
                sb.append(segment.formatting.charAt(j));
            }
            sb.append(join);
        }
        if (sb.length() < join.length()) {
            return EMPTY_STRING;
        }
        sb.setLength(sb.length() - join.length());
        return sb.toString();
    }
}
