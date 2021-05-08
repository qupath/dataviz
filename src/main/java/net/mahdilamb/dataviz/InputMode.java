package net.mahdilamb.dataviz;

import net.mahdilamb.dataviz.figure.Tooltip;
import net.mahdilamb.dataviz.ui.IconStore;
import net.mahdilamb.dataviz.ui.ToggleButton;
import net.mahdilamb.dataviz.ui.ToggleButtonGroup;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class InputMode extends ToggleButtonGroup {
    public enum State {
        PAN,
        MANUAL_ZOOM,
        POLYGON_SELECT(true);
        final boolean isSelection;

        State(final boolean isSelection) {
            this.isSelection = isSelection;
        }

        State() {
            this.isSelection = false;
        }
    }

    private final Figure figure;
    private ToggleButton pan, zoom, polygonSelect;
    public State state;
    Map<ToggleButton, State> stateToggleButtonMap = new HashMap<>(State.values().length);

    InputMode(final Figure figure, final PlotLayout<?> layout) {
        this.figure = figure;
        boolean enableManualZoom = true,
                enablePan = true,
                enablePolygonSelect = true;
        for (final PlotData<?, ?> data : layout.data) {
            if (data.getPlotOptions() == null) {
                continue;
            }
            enableManualZoom &= data.getPlotOptions().supportsManualZoom();
            enablePan &= data.getPlotOptions().supportsPan();
            enablePolygonSelect &= data.getPlotOptions().supportsPolygonSelection();
        }
        if (enablePan) {
            add(getPanButton());
        }
        /*
        if (enableManualZoom){
            add(getZoomButton());
        }*/
        if (enablePolygonSelect) {
            add(getPolygonSelectButton());
        }

    }

    protected <T> ToggleButton createButton(T image, String tooltipText, State state) {
        final ToggleButton tb = new ToggleButton(image, null);
        tb.setTooltip(Tooltip.create(Color.DARK_GRAY, tooltipText));
        if (this.state == null) {
            this.state = state;
        }
        stateToggleButtonMap.put(tb, state);
        return tb;
    }

    protected ToggleButton getPanButton() {
        if (pan == null) {
            pan = createButton(
                    figure.getMaterialIcon(IconStore.MaterialIconKey.OPEN_WITH, null),
                    "Pan",
                    State.PAN
            );
        }
        return pan;
    }

    protected ToggleButton getZoomButton() {
        if (zoom == null) {
            zoom = createButton(
                    figure.getMaterialIcon(IconStore.MaterialIconKey.SEARCH, null),
                    "Zoom",
                    State.MANUAL_ZOOM
            );
        }
        return zoom;
    }

    protected ToggleButton getPolygonSelectButton() {
        if (polygonSelect == null) {
            polygonSelect = createButton(
                    figure.getDataVizIcon(IconStore.DataVizIconKey.POLYGON_SELECTION, null),
                    "Polygon select",
                    State.POLYGON_SELECT
            );
        }
        return polygonSelect;
    }

    @Override
    public void setSelected(int i) {
        i = i < 0 ? defaultSelected : i;
        super.setSelected(i);
        if (this.state != (this.state = stateToggleButtonMap.get((ToggleButton) (getChildren()).get(i)))) {
            figure.layout.inputModeChanged(state);
        }
    }

}
