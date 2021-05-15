package net.mahdilamb.dataviz.inprogress;

import net.mahdilamb.colormap.Colors;
import net.mahdilamb.dataviz.figure.*;
import net.mahdilamb.dataviz.figure.Component;
import net.mahdilamb.dataviz.figure.Dialog;
import net.mahdilamb.dataviz.graphics.GraphicsBuffer;
import net.mahdilamb.dataviz.graphics.Side;
import net.mahdilamb.dataviz.ui.Button;
import net.mahdilamb.dataviz.ui.IconStore;
import net.mahdilamb.dataviz.ui.TextInput;
import net.mahdilamb.dataviz.ui.ToggleButton;

import java.awt.*;

public class HoverSandbox {
    static final class Rectangle extends Component {
        final double x, y, width, height;
        ToggleButton hover;
        Dialog dialog;

        Rectangle(double x, double y, double width, double height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        Rectangle(double width, double height) {
            this(0, 0, width, height);
        }

        private ToggleButton getHoverButton() {
            if (hover == null) {
                hover = new ToggleButton(getMaterialIcon(getContext().getRenderer(), IconStore.MaterialIconKey.ADD_CHART, Color.BLACK), null);
                assignParent(this, hover);
                hover.setTooltip(Tooltip.create(Color.BLACK, "Do something", true));
                hover.setOnMouseExit(() -> {
                    if (dialog != null && dialog.isVisible()) {
                        return;
                    }
                    super.onMouseExit(false, false, -1, -1);
                });
                hover.setOnMouseClick(() -> {
                    getDialog().setVisible(hover.getValue());
                    dialog.requestFocus();
                });
            }
            return hover;
        }

        private Dialog getDialog() {
            if (dialog == null) {
                final Form form = new Form();
                form.add("text", new TextInput<>());
                dialog = new Dialog(hover.getX() + hover.getWidth() * .5, hover.getY() + hover.getHeight(), Color.DARK_GRAY, Color.WHITE, Side.BOTTOM, form, true);
                assignParent(this, dialog);
                addToOverlay(dialog);
            }
            return dialog;
        }

        @Override
        protected void onMouseEnter(boolean ctrlDown, boolean shiftDown, double x, double y) {
            if (hover == null) {
                addToOverlay(getHoverButton());

            }
            hover.setVisible(true);
            super.onMouseEnter(ctrlDown, shiftDown, x, y);
        }

        @Override
        protected void onMouseExit(boolean ctrlDown, boolean shiftDown, double x, double y) {
            if (hover != null && !hover.getValue()) {
                hover.setVisible(false);
            }
            super.onMouseExit(ctrlDown, shiftDown, x, y);
        }

        @Override
        protected void layoutComponent(Renderer renderer, double minX, double minY, double maxX, double maxY) {
            setBoundsFromRect(minX + x, minY + y, width, height);
        }

        @Override
        protected void drawComponent(Renderer renderer, GraphicsBuffer canvas) {
            canvas.setFill(Colors.cornflowerblue);
            canvas.fillRect(getX(), getY(), getWidth(), getHeight());
        }

        @Override
        protected void onBlur() {
            if (dialog != null) {
                hover.setValue(false);
                dialog.setVisible(false);
                hover.setVisible(false);
            }
            super.onBlur();
        }
    }

    static final class Figure extends FigureBase<Figure> {
        Figure() {
            addAll(new Rectangle(90, 75));
        }
    }

    public static void main(String[] args) {
        new Figure()
                .show();
    }
}
