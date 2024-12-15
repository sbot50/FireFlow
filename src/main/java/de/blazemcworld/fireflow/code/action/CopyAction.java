package de.blazemcworld.fireflow.code.action;

import de.blazemcworld.fireflow.code.widget.Widget;

import java.util.List;

public class CopyAction implements Action {
    private List<Widget> widgets;

    public CopyAction(List<Widget> widgets) {
        this.widgets = widgets;
    }
}
