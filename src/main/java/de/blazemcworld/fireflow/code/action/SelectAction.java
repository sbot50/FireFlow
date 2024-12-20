package de.blazemcworld.fireflow.code.action;

import de.blazemcworld.fireflow.code.CodeEditor;
import de.blazemcworld.fireflow.code.Interaction;
import de.blazemcworld.fireflow.code.widget.RectElement;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;

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
        List<Widget> widgets = i.editor().getAllWidgetsBetween(i, box.pos, i.pos())
        if (i.type() == Interaction.Type.LEFT_CLICK || widgets.isEmpty()) i.editor().stopAction(i.player());
        else if (i.type() == Interaction.Type.RIGHT_CLICK) {
            i.editor().stopAction(i.player());
            i.editor().setAction(i.player(), new DragSelectionAction(widgets, i.pos(), i.editor(), i.player()));
        } else if (i.type() == Interaction.Type.SWAP_HANDS) {
            i.editor().stopAction(i.player());
            i.editor().setAction(i.player(), new CopySelectionAction(widgets, i.pos(), i.editor()));
        }
    }

    List<Widget> getAllWidgets(Interaction i) {
        List<NodeWidget> nodeWidgets = new ArrayList<>();
        for (Widget w : new HashSet<>(i.editor().rootWidgets)) {
            if (w instanceof NodeWidget nodeWidget) {
                if ((i.editor().isLockedByPlayer(nodeWidget, i.player()) || i.editor().isLocked(nodeWidget) == null) && isVectorBetween(nodeWidget.getPos(), box.pos, i.pos()) && isVectorBetween(nodeWidget.getPos().sub(nodeWidget.getSize()), box.pos, i.pos())) {
                    boolean foundLocked = false;
                    for (NodeIOWidget io : nodeWidget.getIOWidgets()) {
                        for (WireWidget wire : io.connections) {
                            if (i.editor().isLocked(wire) != null && !i.editor().isLockedByPlayer(wire, i.player())) {
                                foundLocked = true;
                                break;
                            }
                        }
                    }
                    if (!foundLocked) nodeWidgets.add(nodeWidget);
                }
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
                        if (i.editor().isLockedByPlayer(wire, i.player()) || i.editor().isLocked(wire) == null) widgets.addAll(wire.getFullWire());
                    }
                }
            }
        }
    }

    @Override
    public void stop(CodeEditor editor, Player player) {
        box.remove();
    }
}
