package net.mahdilamb.dataviz.ui;

public interface ValidatableInputComponent<S, T> extends InputComponent<T>, Validation<S, T> {
    @Override
    T validate(S value);

    @Override
    void setValue(T value);

    @Override
    T getValue();
}
