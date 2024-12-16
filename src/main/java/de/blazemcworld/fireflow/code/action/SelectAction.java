package de.blazemcworld.fireflow.code.action;

import de.blazemcworld.fireflow.code.CodeEditor;
import de.blazemcworld.fireflow.code.Interaction;
import de.blazemcworld.fireflow.code.widget.*;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class SelectAction implements Action {
    final RectElement box;

    public SelectAction(Vec pos) {
        box = new RectElement();
        box.pos = pos;
        box.size = Vec.ZERO;
        box.color(NamedTextColor.AQUA);
    }

    @Override
    public void tick(Vec cursor, CodeEditor editor, Player player) {
        box.size = cursor.sub(box.pos).mul(-1);
        box.update(editor.space.code);
    }

    @Override
    public void interact(Interaction i) {
        if (i.type() == Interaction.Type.LEFT_CLICK) i.editor().stopAction(i.player());
        else if (i.type() == Interaction.Type.RIGHT_CLICK) {
            i.editor().stopAction(i.player());
            i.editor().setAction(i.player(), new DragSelectionAction(getAllWidgets(i), i.pos(), i.editor()));
        } else if (i.type() == Interaction.Type.SWAP_HANDS) {
            i.editor().stopAction(i.player());
            i.editor().setAction(i.player(), new CopySelectionAction(getAllWidgets(i), i.pos(), i.editor()));
        }
    }

    List<Widget> getAllWidgets(Interaction i) {
        List<NodeWidget> nodeWidgets = new ArrayList<>();
        for (Widget w : new HashSet<>(i.editor().rootWidgets)) {
            if (w instanceof NodeWidget nodeWidget) {
                if (isVectorBetween(nodeWidget.getPos(), box.pos, i.pos()) && isVectorBetween(nodeWidget.getPos().sub(nodeWidget.getSize()), box.pos, i.pos()))
                    nodeWidgets.add(nodeWidget);
            }
        }

        List<Widget> widgets = new ArrayList<>(nodeWidgets);
        for (NodeWidget w : nodeWidgets) {
            for (NodeIOWidget io : w.getIOWidgets()) {
                for (WireWidget wire : io.connections) {
                    if (widgets.contains(wire)) continue;
                    List<NodeWidget> inputs = wire.getInputs().stream().map(widget -> widget.parent).toList();
                    List<NodeWidget> outputs = wire.getOutputs().stream().map(widget -> widget.parent).toList();
                    if (new HashSet<>(widgets).containsAll(inputs) && new HashSet<>(widgets).containsAll(outputs)) {
                        widgets.addAll(wire.getFullWire());
                    }
                }
            }
        }

        return widgets;
    }

    private static boolean isVectorBetween(Vec v, Vec p1, Vec p2) {
        Vec min = p1.min(p2);
        Vec max = p1.max(p2);

        return min.x() < v.x() && min.y() < v.y()
                && max.x() > v.x() && max.y() > v.y();
    }

    @Override
    public void stop(CodeEditor editor, Player player) {
        box.remove();
    }
}
