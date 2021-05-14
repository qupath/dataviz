package net.mahdilamb.dataviz.ui;

import java.util.Objects;

public final class KeyCombination {
    public final boolean ctrlDown, shiftDown;
    public final int keyCode;
    private final char keyChar;
    private final boolean ignoreKeyChar;

    public KeyCombination(boolean ctrlDown, boolean shiftDown, int keyCode, char keyChar) {
        this.ctrlDown = ctrlDown;
        this.shiftDown = shiftDown;
        this.keyCode = keyCode;
        this.keyChar = keyChar;
        ignoreKeyChar = false;
    }

    public KeyCombination(boolean ctrlDown, boolean shiftDown, int keyCode, int keyChar) {
        this(ctrlDown, shiftDown, keyCode, (char) keyChar);
    }

    public KeyCombination(boolean ctrlDown, boolean shiftDown, int keyCode) {
        this.ctrlDown = ctrlDown;
        this.shiftDown = shiftDown;
        this.keyCode = keyCode;
        keyChar = Character.MAX_VALUE;
        ignoreKeyChar = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof KeyCombination)) return false;
        final KeyCombination that = (KeyCombination) o;
        if (ignoreKeyChar || that.ignoreKeyChar) {
            return ctrlDown == that.ctrlDown && shiftDown == that.shiftDown && keyCode == that.keyCode;
        }
        return ctrlDown == that.ctrlDown && shiftDown == that.shiftDown && keyCode == that.keyCode && keyChar == that.keyChar;

    }

    @Override
    public int hashCode() {
        return Objects.hash(ctrlDown, shiftDown, keyCode);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder("KeyCombination {");
        if (ctrlDown) {
            builder.append("ctrl+");
        }
        if (shiftDown) {
            builder.append("shift+");
        }
        builder.append(keyCode);
        if (!ignoreKeyChar) {
            builder.append(" (").append(keyChar).append(")");
        }
        return builder.append('}').toString();
    }
}
