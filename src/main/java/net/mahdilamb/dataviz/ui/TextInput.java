package net.mahdilamb.dataviz.ui;

import net.mahdilamb.dataframe.utils.DoubleArrayList;
import net.mahdilamb.dataviz.figure.Component;
import net.mahdilamb.dataviz.figure.Renderer;
import net.mahdilamb.dataviz.graphics.Font;
import net.mahdilamb.dataviz.graphics.GraphicsBuffer;
import net.mahdilamb.dataviz.graphics.Stroke;
import net.mahdilamb.dataviz.utils.StringUtils;

import java.awt.*;
import java.awt.event.KeyEvent;

public class TextInput<E> extends Component implements InputComponent<E>, ValidatableInputComponent<String, E> {
    private static final Stroke THIN_LINE = new Stroke(.5);
    final String label;
    final StringBuilder rawValue = new StringBuilder();
    private final Validation<String, E> validation;
    E value;
    final DoubleArrayList charPositions = new DoubleArrayList();
    int cursorPosition = 0;
    int selectionStart = -1, selectionEnd = -1;

    Font font = Font.DEFAULT_FONT;
    double width = 120;
    double paddingX = 4, paddingY = 4;


    private Stroke stroke = THIN_LINE;
    private Color strokeColor = Color.BLACK;

    public TextInput(final String label, Validation<String, E> validation) {
        this.validation = validation;
        this.label = label;
    }

    public TextInput(final String label) {
        this.validation = null;
        this.label = label;
    }

    public TextInput() {
        this(StringUtils.EMPTY_STRING);
    }

    public void setValue(final String value) {
        rawValue.setLength(0);
        rawValue.append(value);
        setValue(validate(value));
    }

    @Override
    @SuppressWarnings("unchecked")
    public E validate(String value) {
        if (validation == null) {
            return (E) value;
        }
        return validation.validate(value);
    }

    @Override
    public int isValid(String value) {
        if (validation == null) {
            return 0;
        }
        return validation.isValid(value);
    }

    @Override
    public void setValue(E value) {
        this.value = value;
    }

    @Override
    public E getValue() {
        return value;
    }

    private void setSelection(int start, int end) {
        this.selectionStart = start;
        this.selectionEnd = end;
        redraw();
    }

    private void resetSelection() {
        selectionStart = selectionEnd = -1;
        redraw();
    }

    @Override
    protected <T> void layoutComponent(Renderer<T> renderer, double minX, double minY, double maxX, double maxY) {
        setBoundsFromRect(minX, minY, paddingX * 2 + width, getTextLineHeight(renderer, font, rawValue.toString()) + paddingY * 2);
    }

    @Override
    protected void onMouseEnter(boolean ctrlDown, boolean shiftDown, double x, double y) {
        stroke = Stroke.SOLID;
        redraw();
    }

    @Override
    protected void onMouseExit(boolean ctrlDown, boolean shiftDown, double x, double y) {
        if (!isFocused()) {
            stroke = THIN_LINE;
        }
        redraw();
    }

    @Override
    protected void onMouseDown(boolean ctrlDown, boolean shiftDown, double x, double y) {
        //TODO set caret position
        super.onMouseDown(ctrlDown, shiftDown, x, y);
    }

    @Override
    protected void onFocus() {
        strokeColor = FOCUS_COLOR;
        stroke = Stroke.SOLID;
        super.onFocus();
    }

    @Override
    protected void onBlur() {
        strokeColor = Color.BLACK;
        stroke = THIN_LINE;
        super.onBlur();
    }

    @Override
    protected <T> void drawComponent(Renderer<T> renderer, GraphicsBuffer<T> canvas) {
        canvas.setFont(font);
        canvas.setFill(Color.BLACK);
        canvas.fillText(rawValue.toString(), getX() + paddingX, paddingY + getY() + getTextBaselineOffset(renderer, font));

        canvas.setStroke(stroke);
        canvas.setStroke(strokeColor);
        canvas.strokeLine(getX(), getY() + getHeight(), getX() + getWidth(), getY() + getHeight());

    }

    @Override
    protected void onKeyPress(boolean ctrlDown, boolean shiftDown, int keyCode) {
        rawValue.append(KeyEvent.getKeyText(keyCode));
        markBufferAsOldQuietly();
        super.onKeyPress(ctrlDown, shiftDown, keyCode);
    }

    @Override
    protected void onKeyRelease(boolean ctrlDown, boolean shiftDown, int keyCode) {
        //    super.onKeyRelease(ctrlDown, shiftDown, keyCode);
    }

    @Override
    protected void onKeyType(boolean ctrlDown, boolean shiftDown, int keyCode) {
        //  super.onKeyType(ctrlDown, shiftDown, keyCode);

    }
}
