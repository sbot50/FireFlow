package de.blazemcworld.fireflow.code.action;

import de.blazemcworld.fireflow.code.CodeEditor;
import de.blazemcworld.fireflow.code.Interaction;
import de.blazemcworld.fireflow.code.type.SignalType;
import de.blazemcworld.fireflow.code.widget.*;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class DeleteSelectAction implements Action {
    private final RectElement box;

    public DeleteSelectAction(Vec pos) {
        box = new RectElement();
        box.pos = pos;
        box.size = Vec.ZERO;
        box.color(NamedTextColor.RED);
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
            List<Widget> widgets = getAllWidgets(i);
            widgets.sort((w1, w2) -> {
                if (w1 instanceof WireWidget && w2 instanceof NodeWidget) return -1;
                if (w2 instanceof WireWidget && w1 instanceof NodeWidget) return 1;
                return 0;
            });
            for (Widget w : widgets) {
                if (w instanceof NodeWidget nodeWidget) {
                    for (NodeIOWidget io : nodeWidget.getIOWidgets()) {
                        for (WireWidget wire : new ArrayList<>(io.connections)) {
                            if (widgets.contains(wire)) continue;
                            List<NodeIOWidget> inputs = wire.getInputs();
                            List<NodeIOWidget> outputs = wire.getOutputs();
                            wire.removeConnection(i.editor());
                            if (wire.type() == SignalType.INSTANCE && !outputs.getFirst().connections.isEmpty()) outputs.getFirst().connections.getFirst().cleanup(i.editor());
                            else if (!inputs.getFirst().connections.isEmpty()) inputs.getFirst().connections.getFirst().cleanup(i.editor());
                        }
                    }
                }
                w.remove();
                i.editor().rootWidgets.remove(w);
            }
            i.editor().stopAction(i.player());
        }
    }

    private List<Widget> getAllWidgets(Interaction i) {
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
