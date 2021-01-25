package net.mahdilamb.charts.styles;

public class Title implements Text {
    double fontSize = 12, paddingX = 0, paddingY = 2;
    String text;
    boolean isVisible;

    public boolean isVisible() {
        return isVisible && text != null && text.length() > 0;
    }

    public String getText() {
        return text;
    }

    @Override
    public double getFontSize() {
        return fontSize;
    }

    @Override
    public double getPaddingX() {
        return paddingX;
    }

    @Override
    public double getPaddingY() {
        return paddingY;
    }

    public void setTitle(String title) {
        this.text = title;
    }
}
