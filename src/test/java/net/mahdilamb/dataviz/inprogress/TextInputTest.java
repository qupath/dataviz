package net.mahdilamb.dataviz.inprogress;

import net.mahdilamb.colormap.Colors;
import net.mahdilamb.dataframe.utils.DoubleArrayList;
import net.mahdilamb.dataviz.figure.Component;
import net.mahdilamb.dataviz.figure.FigureBase;
import net.mahdilamb.dataviz.figure.Renderer;
import net.mahdilamb.dataviz.graphics.Font;
import net.mahdilamb.dataviz.graphics.GraphicsBuffer;
import net.mahdilamb.dataviz.graphics.Stroke;
import net.mahdilamb.dataviz.ui.InputComponent;
import net.mahdilamb.dataviz.ui.KeyCombination;
import net.mahdilamb.dataviz.utils.ColorUtils;
import net.mahdilamb.dataviz.utils.FIFOQueue;
import net.mahdilamb.dataviz.utils.StringUtils;
import net.mahdilamb.dataviz.utils.functions.ObjBiBooleanIntCharConsumer;
import net.mahdilamb.dataviz.utils.functions.Runnables;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class TextInputTest {

    private static final class TextInput extends Component implements InputComponent<String> {
        private static final Map<KeyCombination, ObjBiBooleanIntCharConsumer<TextInput>> keyMap = new HashMap<>();

        static {
            //TODO pgup/down
            keyMap.put(new KeyCombination(false, false, KeyEvent.VK_LEFT, Character.MAX_VALUE), TextInput::keyLeft);
            keyMap.put(new KeyCombination(false, true, KeyEvent.VK_LEFT, Character.MAX_VALUE), TextInput::shiftKeyLeft);
            keyMap.put(new KeyCombination(true, false, KeyEvent.VK_LEFT, Character.MAX_VALUE), TextInput::ctrlKeyLeft);

            keyMap.put(new KeyCombination(false, false, KeyEvent.VK_RIGHT, Character.MAX_VALUE), TextInput::keyRight);
            keyMap.put(new KeyCombination(false, true, KeyEvent.VK_RIGHT, Character.MAX_VALUE), TextInput::shiftKeyRight);
            keyMap.put(new KeyCombination(true, false, KeyEvent.VK_RIGHT, Character.MAX_VALUE), TextInput::ctrlKeyRight);

            keyMap.put(new KeyCombination(false, false, KeyEvent.VK_HOME, Character.MAX_VALUE), TextInput::keyHome);
            keyMap.put(new KeyCombination(false, false, KeyEvent.VK_END, Character.MAX_VALUE), TextInput::keyEnd);

            keyMap.put(new KeyCombination(false, false, KeyEvent.VK_BACK_SPACE, KeyEvent.VK_BACK_SPACE), TextInput::keyBackspace);
            keyMap.put(new KeyCombination(false, false, KeyEvent.VK_DELETE, KeyEvent.VK_DELETE), TextInput::keyDelete);
            keyMap.put(new KeyCombination(false, false, KeyEvent.VK_ESCAPE, KeyEvent.VK_ESCAPE), TextInput::keyEscape);

            keyMap.put(new KeyCombination(true, false, KeyEvent.VK_A), TextInput::keyCtrlA);
            keyMap.put(new KeyCombination(true, false, KeyEvent.VK_C), TextInput::keyCtrlC);
            keyMap.put(new KeyCombination(true, false, KeyEvent.VK_V), TextInput::keyCtrlV);
            keyMap.put(new KeyCombination(true, false, KeyEvent.VK_X), TextInput::keyCtrlX);

            keyMap.put(new KeyCombination(true, false, KeyEvent.VK_Y), TextInput::keyCtrlY);
            keyMap.put(new KeyCombination(true, false, KeyEvent.VK_Z), TextInput::keyCtrlZ);

        }


        private static void keyCtrlA(final TextInput input, boolean ctrlDown, boolean shiftDown, int keyCode, char keyChar) {
            input.selectAll();
        }

        private static void keyCtrlZ(final TextInput input, boolean ctrlDown, boolean shiftDown, int keyCode, char keyChar) {
            input.undo();
        }

        private static void keyCtrlY(final TextInput input, boolean ctrlDown, boolean shiftDown, int keyCode, char keyChar) {
            input.redo();
        }

        private static void keyCtrlC(final TextInput input, boolean ctrlDown, boolean shiftDown, int keyCode, char keyChar) {
            if (input.hasSelection()) {
                final int start = Math.min(input.selectionStart, input.selectionEnd),
                        end = Math.max(input.selectionStart, input.selectionEnd);
                addToClipboard(input.getContext().getRenderer(), input.rawValue.substring(start, end));
            }
        }

        private static void keyCtrlX(final TextInput input, boolean ctrlDown, boolean shiftDown, int keyCode, char keyChar) {
            if (input.hasSelection()) {
                final int start = Math.min(input.selectionStart, input.selectionEnd),
                        end = Math.max(input.selectionStart, input.selectionEnd);
                addToClipboard(input.getContext().getRenderer(), input.rawValue.substring(start, end));
                input.cursorPosition = Math.min(input.selectionStart, input.selectionEnd);
                input.remove(input.cursorPosition, Math.max(input.selectionStart, input.selectionEnd));
                input.clearSelection();
            }
        }

        private static void keyCtrlV(final TextInput input, boolean ctrlDown, boolean shiftDown, int keyCode, char keyChar) {
            if (input.hasSelection()) {
                input.cursorPosition = Math.min(input.selectionStart, input.selectionEnd);
                input.remove(input.cursorPosition, Math.max(input.selectionStart, input.selectionEnd));
                input.clearSelection();
            }
            final String fromClipboard;
            if ((fromClipboard = getFromClipboard(input.getContext().getRenderer())) != null) {
                input.insert(input.cursorPosition, fromClipboard);
            }
        }

        private static void keyBackspace(final TextInput input, boolean ctrlDown, boolean shiftDown, int keyCode, char keyChar) {
            if (input.hasSelection()) {
                input.cursorPosition = Math.min(input.selectionStart, input.selectionEnd);
                input.remove(input.cursorPosition, Math.max(input.selectionStart, input.selectionEnd));
            } else {
                if (input.rawValue.length() > 0 && input.cursorPosition > 0) {
                    input.remove(input.cursorPosition - 1, input.cursorPosition);
                    --input.cursorPosition;
                }
            }
        }

        private static void keyDelete(final TextInput input, boolean ctrlDown, boolean shiftDown, int keyCode, char keyChar) {
            if (input.hasSelection()) {
                input.cursorPosition = Math.min(input.selectionStart, input.selectionEnd);
                input.remove(input.cursorPosition, Math.max(input.selectionStart, input.selectionEnd));
            } else {
                if (input.rawValue.length() > 0 && input.cursorPosition < input.rawValue.length()) {
                    input.remove(input.cursorPosition, input.cursorPosition + 1);
                }
            }
        }

        private static void keyEscape(final TextInput input, boolean ctrlDown, boolean shiftDown, int keyCode, char keyChar) {
            if (input.hasSelection()) {
                input.clearSelection();
            }
        }

        private static void keyHome(final TextInput input, boolean ctrlDown, boolean shiftDown, int keyCode, char keyChar) {
            input.cursorPosition = 0;
        }

        private static void keyEnd(final TextInput input, boolean ctrlDown, boolean shiftDown, int keyCode, char keyChar) {
            input.cursorPosition = input.rawValue.length();
        }

        private static void keyLeft(final TextInput input, boolean ctrlDown, boolean shiftDown, int keyCode, char keyChar) {
            if (input.cursorPosition > 0) {
                if (input.hasSelection()) {
                    input.cursorPosition = input.selectionStart;
                    input.clearSelection();
                }
                input.cursorPosition = Math.max(0, input.cursorPosition - 1);
            }
        }

        private static void shiftKeyLeft(final TextInput input, boolean ctrlDown, boolean shiftDown, int keyCode, char keyChar) {
            if (input.cursorPosition > 0) {
                if (input.hasSelection()) {
                    input.selectionStart = Math.max(0, input.selectionStart - 1);
                } else {
                    input.selectionStart = Math.max(0, input.cursorPosition - 1);
                    input.selectionEnd = input.cursorPosition;
                }
            }
        }

        private static void ctrlKeyLeft(final TextInput input, boolean ctrlDown, boolean shiftDown, int keyCode, char keyChar) {
            if (input.hasSelection()) {
                input.clearSelection();
            }
            if (input.cursorPosition > 0) {
                if (Character.isWhitespace(input.rawValue.charAt(input.cursorPosition - 1))) {
                    --input.cursorPosition;
                    while (Character.isWhitespace(input.rawValue.charAt(input.cursorPosition - 1))) {
                        --input.cursorPosition;
                    }
                } else {
                    input.cursorPosition = StringUtils.getWordStart(input.rawValue.toString(), Math.min(input.cursorPosition - 1, input.rawValue.length() - 1));
                }
            }
        }

        private static void keyRight(final TextInput input, boolean ctrlDown, boolean shiftDown, int keyCode, char keyChar) {
            if (input.cursorPosition < input.rawValue.length()) {
                if (input.hasSelection()) {
                    input.cursorPosition = input.selectionEnd;
                    input.clearSelection();
                }
                ++input.cursorPosition;
                if (input.cursorPosition >= input.rawValue.length()) {
                    input.cursorPosition = input.rawValue.length();
                }
            }
        }

        private static void shiftKeyRight(final TextInput input, boolean ctrlDown, boolean shiftDown, int keyCode, char keyChar) {
            if (input.cursorPosition < input.rawValue.length()) {
                if (input.hasSelection()) {
                    input.selectionEnd = Math.min(input.selectionEnd + 1, input.rawValue.length());
                } else {
                    input.selectionStart = input.cursorPosition;
                    input.selectionEnd = Math.min(input.cursorPosition + 1, input.rawValue.length());
                }
            }
        }

        private static void ctrlKeyRight(final TextInput input, boolean ctrlDown, boolean shiftDown, int keyCode, char keyChar) {
            if (input.hasSelection()) {
                input.clearSelection();
            }
            if (input.cursorPosition < input.rawValue.length()) {
                if (Character.isWhitespace(input.rawValue.charAt(input.cursorPosition)) && input.cursorPosition < input.rawValue.length()) {
                    ++input.cursorPosition;
                    while (input.cursorPosition < input.rawValue.length() - 1 && Character.isWhitespace(input.rawValue.charAt(input.cursorPosition))) {
                        ++input.cursorPosition;
                    }
                } else {
                    input.cursorPosition = StringUtils.getWordEnd(input.rawValue.toString(), Math.min(input.cursorPosition, input.rawValue.length() - 1));
                }
            }
        }

        @Override
        public void setValue(String value) {
            setText(value);
        }

        @Override
        public String getValue() {
            return rawValue.toString();
        }

        private static final class State {
            final String text;
            final int cursorPosition;

            private State(String text, int cursorPosition) {
                this.text = text;
                this.cursorPosition = cursorPosition;
            }

            @Override
            public String toString() {
                return "State {'" + text + "', pos=" + cursorPosition + '}';
            }
        }

        double paddingX = 4, paddingY = 4;
        net.mahdilamb.dataviz.graphics.Font font = Font.DEFAULT_FONT;
        Color selectionColor = Colors.skyblue;
        Color textColor = Color.BLACK;

        StringBuilder rawValue = new StringBuilder();
        DoubleArrayList charPositions = null;

        boolean mouseDown = false;
        double startX, startY;

        int selectionStart = -1, selectionEnd = -1;
        static final long DEFAULT_CURSOR_DELAY_MS = 750;
        static final AtomicLong cursorDelay = new AtomicLong(DEFAULT_CURSOR_DELAY_MS);
        final AtomicBoolean blinkOn = new AtomicBoolean(false);
        int cursorPosition = 0;

        boolean hasChanges = false;
        final FIFOQueue<State> states = new FIFOQueue<>(100);
        int bufferPoint = 0;

        final Runnables.PausableRunnable blink = new Runnables.PausableRunnable(
                () -> {
                    final long end = System.currentTimeMillis() + cursorDelay.get();
                    if (cursorDelay.get() != DEFAULT_CURSOR_DELAY_MS) {
                        cursorDelay.set(DEFAULT_CURSOR_DELAY_MS);
                    }
                    if (blinkOn.get()) {
                        updateStateBuffer();
                    }
                    blinkOn.set(!blinkOn.get());
                    redraw();
                    try {
                        Thread.sleep(end - System.currentTimeMillis());
                    } catch (InterruptedException ignored) {
                        cursorDelay.set(cursorDelay.get() / 2);
                        blinkOn.set(false);
                    }

                }, true);


        final Thread cursor = new Thread(blink);
        double width;

        TextInput(final double width) {
            this.width = width;
            cursor.start();
            setOnMouseClick(this::setCaretFromPosition);
            setOnMouseDoubleClick(this::selectNearestWord);
        }

        TextInput() {
            this(200);
        }

        public void setText(String value) {
            rawValue = new StringBuilder(value);
            charPositions = null;
            cursorPosition = value.length();
            markDirty();
            updateStateBuffer();
        }

        public void insert(int index, String text) {
            double width = 0;
            if (charPositions != null) {
                final double insertWidth = getTextWidth(getContext().getRenderer(), font, text);
                for (int i = index; i < rawValue.length(); ++i) {
                    charPositions.set(i, charPositions.get(i) + insertWidth);
                }
                width = index == 0 ? 0 : charPositions.get(index - 1);
            }
            rawValue.ensureCapacity(rawValue.length() + text.length());
            for (int i = 0; i < text.length(); ++i) {
                final char c = text.charAt(i);
                if (c == '\n' || Character.isISOControl(c)) {
                    continue;
                }
                final int j = index + i;
                rawValue.insert(j, c);
                if (charPositions == null) {
                    continue;
                }
                width += getTextCharWidth(getContext().getRenderer(), font, c);
                charPositions.add(j, width);
            }
            cursorPosition += text.length();
            markDirty();
        }

        private void undo() {
            if (bufferPoint >= 0) {
                restore(states.get(bufferPoint--));
            }
        }

        private void redo() {
            if (bufferPoint < states.size() - 1) {
                restore(states.get((bufferPoint = Math.max(0, bufferPoint) + 1)));
            }
        }

        private void updateStateBuffer() {
            if (!hasChanges) {
                return;
            }
            if (states.isEmpty() || !Objects.equals(states.getLast().text, rawValue.toString())) {
                //TODO clear buffer
                if (bufferPoint < 0) {
                    if (!states.isEmpty()) {
                        states.subList(1, states.size()).clear();
                    }
                }
                states.add(save());
                bufferPoint = Math.min(Math.max(0, states.size() - 2), states.size() - 1);
            }
            hasChanges = false;
        }

        private State save() {
            return new State(new String(rawValue.toString().toCharArray()), cursorPosition);
        }

        private void restore(final State state) {
            rawValue = new StringBuilder(state.text);
            charPositions = null;
            cursorPosition = state.cursorPosition;
            hasChanges = false;
            cursor.interrupt();
        }


        public void setSelection(int from, int to) {
            if (from == to) {
                clearSelection();
            }
            if (from < 0) {
                throw new IndexOutOfBoundsException("from is less than 0");
            }
            if (from > to) {
                throw new IllegalArgumentException("form is greater than to");
            }
            if (to > rawValue.length()) {
                throw new IndexOutOfBoundsException("to is greater than the length of the string");
            }
            selectionStart = from;
            selectionEnd = to;
        }

        private void selectNearestWord(double x, double y) {
           /*todo if (hasSelection()) {
                selectAll();
            } else {*/
            final int i = Math.min(getCaretFromPosition(x), rawValue.length() - 1);
            final String text = rawValue.toString();
            selectionStart = StringUtils.getWordStart(text, i);
            selectionEnd = StringUtils.getWordEnd(text, i);
            cursor.interrupt();

            /*   }*/
        }

        private void setCaretFromPosition(double x, double y) {
            final int search;
            if ((search = getCaretFromPosition(x)) >= 0) {
                cursorPosition = search;
                clearSelection();
                cursor.interrupt();
            }
        }

        private void clearSelection() {
            selectionStart = -1;
            selectionEnd = -1;
        }

        private int getCaretFromPosition(double x) {
            if (getCharPositions().size() == 0) {
                return 0;
            }
            return roundBinarySearch(charPositions, x - getX() - paddingX);

        }

        private double cursorPositionToX(int cursorPosition) {
            if (cursorPosition > 0) {
                return getX() + paddingX + (getCharPositions().get(cursorPosition - 1));
            } else {
                return getX() + paddingX;
            }
        }

        private boolean hasSelection() {
            return selectionEnd >= 0 && selectionStart >= 0;
        }

        private DoubleArrayList getCharPositions() {
            if (charPositions == null) {
                charPositions = new DoubleArrayList(rawValue.length());
                for (int i = 0; i < rawValue.length(); ++i) {
                    double width = getTextCharWidth(getContext().getRenderer(), font, rawValue.charAt(i));
                    charPositions.add(charPositions.size() == 0 ? width : (charPositions.get(charPositions.size() - 1) + width));
                }
            }
            return charPositions;
        }


        private void selectAll() {
            selectionStart = 0;
            selectionEnd = rawValue.length();
            cursor.interrupt();
        }

        private void remove(int from, int to) {
            if (from < 0) {
                throw new IndexOutOfBoundsException("From is below 0");
            }
            double widthToRemove = charPositions.get(to - 1) - (from == 0 ? 0 : charPositions.get(from - 1));
            for (int i = charPositions.size() - 1; i >= to; --i) {
                charPositions.set(i, charPositions.get(i) - widthToRemove);
            }
            charPositions.remove(from, to);
            rawValue.delete(from, to);

            if (hasSelection()) {
                clearSelection();
            }
            markDirty();
        }

        public void insert(int index, final char c) {
            rawValue.insert(index, c);
            cursorPosition = index + 1;

            if (charPositions != null) {
                final double width = getTextCharWidth(getContext().getRenderer(), font, c);
                for (int i = index; i < charPositions.size(); ++i) {
                    charPositions.set(i, charPositions.get(i) + width);
                }
                charPositions.add(index, charPositions.size() == 0 ? width : (charPositions.get(index - 1) + width));
            }
            markDirty();
        }

        public int size() {
            return rawValue.length();
        }

        private synchronized void startCursor() {
            blinkOn.set(false);
            blink.resume();

        }

        private synchronized void pauseCursor() {
            blink.pause();
            blinkOn.set(false);
            redraw();
        }

        @Override
        protected void layoutComponent(Renderer renderer, double minX, double minY, double maxX, double maxY) {
            setBoundsFromRect(minX, minY, width, paddingY * 2 + getTextLineHeight(renderer, font, null));
        }

        @Override
        protected final void drawComponent(Renderer renderer, GraphicsBuffer canvas) {
            //draw outline
            canvas.setStroke(Stroke.SOLID);
            canvas.setStroke(Color.black);
            canvas.strokeRect(getX(), getY(), getWidth(), getHeight());
            //draw text
            canvas.setFont(font);
            canvas.setFill(textColor);

            canvas.fillText(rawValue.toString(), getX() + paddingX, paddingY + getY() + getTextBaselineOffset(renderer, font));
            final boolean hasSelection = hasSelection();
            if (hasSelection || mouseDown) {
                if (hasSelection) {
                    canvas.setFill(selectionColor);
                    final int start = Math.min(selectionStart, selectionEnd),
                            end = Math.max(selectionStart, selectionEnd);
                    double startX = cursorPositionToX(start),
                            endX = cursorPositionToX(end);
                    canvas.fillRect(startX, getY() + 2, endX - startX, getHeight() - 4);
                    canvas.setFill(ColorUtils.getForegroundFromBackground(selectionColor));
                    canvas.fillText(rawValue.substring(start, end), startX, paddingY + getY() + getTextBaselineOffset(renderer, font));
                }
            } else {
                //draw cursor
                if (blinkOn.get()) {
                    final double x = cursorPositionToX(cursorPosition);
                    canvas.setStroke(Stroke.SOLID);
                    canvas.strokeLine(x, getY() + 2, x, getY() + getHeight() - 4);
                }
            }
        }

        @Override
        protected void onFocus() {
            startCursor();
        }

        @Override
        protected void onBlur() {
            updateStateBuffer();
            clearSelection();
            pauseCursor();
            super.onBlur();
        }

        private void markDirty() {
            hasChanges = true;
            if (!isFocused()) {
                redraw();
            }
        }

        @Override
        protected void onKeyPress(boolean ctrlDown, boolean shiftDown, int keyCode, char keyChar) {
            final ObjBiBooleanIntCharConsumer<TextInput> action;
            if ((action = keyMap.get(new KeyCombination(ctrlDown, shiftDown, keyCode, keyChar))) != null) {
                action.accept(this, ctrlDown, shiftDown, keyCode, keyChar);
                cursor.interrupt();
            }
            super.onKeyPress(ctrlDown, shiftDown, keyCode, keyChar);
        }

        @Override
        protected void onKeyType(boolean ctrlDown, boolean shiftDown, int keyCode, char keyChar) {
            if (!Character.isISOControl(keyChar)) {
                if (hasSelection()) {
                    cursorPosition = Math.min(selectionStart, selectionEnd);
                    remove(cursorPosition, Math.max(selectionStart, selectionEnd));
                    clearSelection();
                }
                pauseCursor();
                insert(cursorPosition, keyChar);
                startCursor();
                cursor.interrupt();
            }
            super.onKeyType(ctrlDown, shiftDown, keyCode, keyChar);
        }

        @Override
        protected final void onMouseDown(boolean ctrlDown, boolean shiftDown, double x, double y) {
            startX = x;
            startY = y;
            mouseDown = true;

            selectionStart = selectionEnd = getCaretFromPosition(x);
            cursor.interrupt();
            super.onMouseDown(ctrlDown, shiftDown, x, y);
        }

        @Override
        protected final void onMouseUp(boolean ctrlDown, boolean shiftDown, double x, double y) {
            mouseDown = false;
            super.onMouseUp(ctrlDown, shiftDown, x, y);
            redraw();
        }

        @Override
        protected final void onMouseMove(boolean ctrlDown, boolean shiftDown, double x, double y) {
            if (mouseDown) {
                selectionEnd = getCaretFromPosition(x);
            }
            redraw();
        }
    }

    private static int roundBinarySearch(DoubleArrayList a, double value) {
        int low = 0;
        int high = a.size() - 1;
        if (value < a.get(low)) {
            return low;
        }
        if (value > a.get(high)) {
            return a.size();
        }
        while (low <= high) {
            final int mid = (high + low) >>> 1;
            if (value < a.get(mid)) {
                high = mid - 1;
            } else if (value > a.get(mid)) {
                low = mid + 1;
            } else {
                return mid + 1;
            }
        }
        return ((a.get(low) - value) < (value - a.get(high)) ? low : high) + 1;
    }

    private final static class Figure extends FigureBase<Figure> {
        final TextInput textInput = new TextInput();

        Figure() {
            textInput.setText("Hello world");
            add(textInput);
        }
    }

    public static void main(String[] args) {
        final Figure fig = new Figure();
        fig.show();


    }
}
