package net.mahdilamb.dataviz.ui;

public interface InputComponent<T>  {

    void setValue(T value) ;

    T getValue();

}
