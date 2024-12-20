package de.blazemcworld.fireflow.code.action;

import de.blazemcworld.fireflow.code.CodeEditor;
import de.blazemcworld.fireflow.code.node.Node;
import de.blazemcworld.fireflow.code.widget.NodeIOWidget;
import de.blazemcworld.fireflow.code.widget.NodeWidget;
import de.blazemcworld.fireflow.code.widget.Widget;
import de.blazemcworld.fireflow.code.widget.WireWidget;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CopySelectionAction implements Action {
    List<Widget> widgets = new ArrayList<>();
    Vec offset;

    public CopySelectionAction(List<Widget> widgets, Vec offset, CodeEditor editor) {
        this.offset = offset;
        widgets.sort((w1, w2) -> {
            if (w1 instanceof WireWidget && w2 instanceof NodeWidget) return -1;
            if (w2 instanceof WireWidget && w1 instanceof NodeWidget) return 1;
            return 0;
        });

        HashMap<NodeWidget, NodeWidget> oldToNewNodes = new HashMap<>();
        HashMap<WireWidget, WireWidget> oldToNewWires = new HashMap<>();

        for (Widget w : widgets) {
            if (w instanceof NodeWidget nodeWidget) {
                Node nodeCopy = nodeWidget.node.copy();
                NodeWidget nodeWidgetCopy = new NodeWidget(nodeCopy, editor);
                nodeWidgetCopy.setPos(nodeWidget.getPos());
                editor.rootWidgets.add(nodeWidgetCopy);
                nodeWidgetCopy.update(editor.space.code);
                oldToNewNodes.put(nodeWidget, nodeWidgetCopy);
                this.widgets.add(nodeWidgetCopy);
            }
        }

        for (Widget w : widgets) {
            if (w instanceof WireWidget wireWidget) {
                WireWidget wireWidgetCopy = new WireWidget(wireWidget.line.from, wireWidget.type(), wireWidget.line.to);
                editor.rootWidgets.add(wireWidgetCopy);
                wireWidgetCopy.update(editor.space.code);
                oldToNewWires.put(wireWidget, wireWidgetCopy);
                this.widgets.add(wireWidgetCopy);
            }
        }

        for (Widget w : widgets) {
            if (w instanceof WireWidget wireWidget) {
                WireWidget wireWidgetCopy = oldToNewWires.get(wireWidget);
                wireWidget.previousWires.forEach(wire -> wireWidgetCopy.addPreviousWire(oldToNewWires.get(wire)));
                wireWidget.nextWires.forEach(wire -> wireWidgetCopy.addNextWire(oldToNewWires.get(wire)));
                if (wireWidget.previousOutput != null) {
                    NodeIOWidget oldPreviousOutput = wireWidget.previousOutput;
                    NodeWidget oldWidget = oldPreviousOutput.parent;
                    int ioIndex = oldWidget.getIOWidgets().indexOf(oldPreviousOutput);
                    NodeWidget newWidget = oldToNewNodes.get(oldWidget);
                    NodeIOWidget newPreviousOutput = newWidget.getIOWidgets().get(ioIndex);
                    wireWidgetCopy.setPreviousOutput(newPreviousOutput);
                    newPreviousOutput.connections.add(wireWidgetCopy);
                }
                if (wireWidget.nextInput != null) {
                    NodeIOWidget oldNextInput = wireWidget.nextInput;
                    NodeWidget oldWidget = oldNextInput.parent;
                    int ioIndex = oldWidget.getIOWidgets().indexOf(oldNextInput);
                    NodeWidget newWidget = oldToNewNodes.get(oldWidget);
                    NodeIOWidget newNextInput = newWidget.getIOWidgets().get(ioIndex);
                    wireWidgetCopy.setNextInput(newNextInput);
                    newNextInput.connections.add(wireWidgetCopy);
                }
                wireWidgetCopy.update(editor.space.code);
            }
        }

        for (Widget w : widgets) {
            if (w instanceof NodeWidget nodeWidget) {
                NodeWidget nodeWidgetCopy = oldToNewNodes.get(nodeWidget);
                int index = 0;
                for (NodeIOWidget io : nodeWidget.getIOWidgets()) {
                    NodeIOWidget ioCopy = nodeWidgetCopy.getIOWidgets().get(index);
                    if (io.input != null && io.input.inset != null) ioCopy.insetValue(io.input.inset, editor);
                    ioCopy.connections.forEach(ioCopy::connect);
                    index++;
                }
                nodeWidgetCopy.update(editor.space.code);
            }
        }
    }

    @Override
    public void tick(Vec cursor, CodeEditor editor, Player player) {
        editor.stopAction(player);
        editor.lockWidgets(widgets, player);
        editor.setAction(player, new DragSelectionAction(widgets, offset, editor, player));
    }
}
