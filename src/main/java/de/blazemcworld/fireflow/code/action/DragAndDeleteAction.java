package de.blazemcworld.fireflow.code.action;

import de.blazemcworld.fireflow.code.CodeEditor;
import de.blazemcworld.fireflow.code.Interaction;
import de.blazemcworld.fireflow.code.widget.NodeWidget;
import de.blazemcworld.fireflow.code.widget.Widget;
import de.blazemcworld.fireflow.code.widget.WireWidget;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;

import java.util.List;
import java.util.Map;

public class DragAndDeleteAction implements Action {
    private Map<NodeWidget, Vec> nodeWidgets = new java.util.HashMap<>();
    private Map<WireWidget, List<Vec>> wireWidgets = new java.util.HashMap<>();
    private Vec offset;

    public DragAndDeleteAction(List<Widget> widgets, Vec offset) {
        this.offset = offset;

        for (Widget w : widgets) {
            if (w instanceof NodeWidget nodeWidget) {
                nodeWidget.borderColor(NamedTextColor.AQUA);
                nodeWidgets.put(nodeWidget, nodeWidget.getPos());
            } else if (w instanceof WireWidget wire) {
                wireWidgets.put(wire, List.of(wire.line.from, wire.line.to));
            }
        }
    }

    @Override
    public void tick(Vec cursor, CodeEditor editor, Player player) {
        Vec newPos = cursor.sub(offset).mul(8).apply(Vec.Operator.CEIL).div(8).withZ(15.999);
        nodeWidgets.forEach((nodeWidget, pos) -> {
            nodeWidget.setPos(pos.add(newPos));
            nodeWidget.update(editor.space.code);
        });
        wireWidgets.forEach((wire, points) -> {
            wire.line.from = points.get(0).add(newPos);
            wire.line.to = points.get(1).add(newPos);
            wire.update(editor.space.code);
        });
    }

    @Override
    public void interact(Interaction i) {
        if (i.type() == Interaction.Type.RIGHT_CLICK) i.editor().stopAction(i.player());
    }

    @Override
    public void stop(CodeEditor editor, Player player) {
        nodeWidgets.forEach((nodeWidget, pos) -> nodeWidget.borderColor(NamedTextColor.WHITE));
        wireWidgets.forEach((wire, points) -> wire.cleanup(editor));
    }
}
