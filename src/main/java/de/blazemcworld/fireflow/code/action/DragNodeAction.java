package de.blazemcworld.fireflow.code.action;

import de.blazemcworld.fireflow.code.CodeEditor;
import de.blazemcworld.fireflow.code.Interaction;
import de.blazemcworld.fireflow.code.node.impl.function.FunctionInputsNode;
import de.blazemcworld.fireflow.code.node.impl.function.FunctionOutputsNode;
import de.blazemcworld.fireflow.code.widget.NodeIOWidget;
import de.blazemcworld.fireflow.code.widget.NodeWidget;
import de.blazemcworld.fireflow.code.widget.WireWidget;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DragNodeAction implements Action {
    private final NodeWidget node;
    private final Vec offset;
    private final List<NodeIOWidget> iowidgets;

    public DragNodeAction(NodeWidget node, Vec offset, CodeEditor editor, Player player) {
        this.node = node;
        this.offset = offset;

        editor.lockWidget(node, player);
        node.borderColor(NamedTextColor.AQUA);
        iowidgets = node.getIOWidgets();
        for (NodeIOWidget IOWidget : new ArrayList<>(iowidgets)) {
            for (WireWidget wire : new ArrayList<>(IOWidget.connections)) {
                List<WireWidget> prevWires;
                if (IOWidget.isInput()) prevWires = wire.previousWires;
                else prevWires = wire.nextWires;
                if (prevWires.size() != 1 || prevWires.getFirst().line.to.y() == prevWires.getFirst().line.from.y()) {
                    Vec mid = wire.line.from.add(wire.line.to).div(2);
                    List<WireWidget> splitWires = wire.splitWire(editor, mid);
                    WireWidget nw = new WireWidget(splitWires.getFirst(), wire.type(), mid);
                    nw.addNextWire(splitWires.getLast());
                    nw.setPos(mid);
                    splitWires.getFirst().nextWires.remove(splitWires.getLast());
                    splitWires.getLast().addPreviousWire(nw);
                    splitWires.getLast().previousWires.remove(splitWires.getFirst());
                    editor.rootWidgets.add(nw);
                    nw.update(editor.space.code);
                    nw.lockWire(editor, player);
                } else {
                    wire.lockWire(editor, player);
                }
            }
        }
    }

    @Override
    public void tick(Vec cursor, CodeEditor editor, Player player) {
        cursor = cursor.mul(8).apply(Vec.Operator.CEIL).div(8).withZ(15.999);
        node.setPos(cursor.add(offset));
        node.update(editor.space.code);
        for (NodeIOWidget IOWidget : iowidgets) {
            for (WireWidget wire : IOWidget.connections) {
                if (IOWidget.isInput()) {
                    wire.line.to = IOWidget.getPos().sub(1 / 8f - 1 / 32f, 1 / 8f, 0);
                    wire.line.from = new Vec(wire.line.from.x(), IOWidget.getPos().y() - 1 / 8f, wire.line.from.z());
                    wire.previousWires.getFirst().line.to = new Vec(wire.previousWires.getFirst().line.to.x(), IOWidget.getPos().y() - 1 / 8f, wire.previousWires.getFirst().line.to.z());
                    wire.previousWires.getFirst().update(editor.space.code);
                } else {
                    wire.line.from = IOWidget.getPos().sub(IOWidget.getSize().sub(1 / 8f, 1 / 8f, 0));
                    wire.line.to = new Vec(wire.line.to.x(), IOWidget.getPos().y() - 1 / 8f, wire.line.to.z());
                    wire.nextWires.getFirst().line.from = new Vec(wire.nextWires.getFirst().line.from.x(), IOWidget.getPos().y() - 1 / 8f, wire.nextWires.getFirst().line.from.z());
                    wire.nextWires.getFirst().update(editor.space.code);
                }
                wire.update(editor.space.code);
            }
        }
    }

    @Override
    public void interact(Interaction i) {
        if (i.type() == Interaction.Type.RIGHT_CLICK) i.editor().stopAction(i.player());
        if (i.type() == Interaction.Type.SWAP_HANDS) {
            if (node.node instanceof FunctionInputsNode || node.node instanceof FunctionOutputsNode) return;
            NodeWidget copy = new NodeWidget(node.node.copy(), i.editor().space.editor);
            copy.setPos(node.getPos());
            for (NodeIOWidget io : node.getInputs()) {
                List<NodeIOWidget> inputs = copy.getInputs();
                NodeIOWidget match = null;
                for (int j = inputs.size() - 1; j >= 0; j--) {
                    match = inputs.get(j);
                    if ((match.input.varargsParent == null || io.input.varargsParent == null) && Objects.equals(io.input.id, match.input.id)) break;
                    if (match.input.varargsParent != null && io.input.varargsParent != null && Objects.equals(io.input.varargsParent.id, match.input.varargsParent.id)) break;
                }
                if (match != null && io.input.inset != null) match.insetValue(io.input.inset, i.editor());
            }
            copy.update(i.editor().space.code);
            i.editor().rootWidgets.add(copy);
            i.editor().stopAction(i.player());
            i.editor().setAction(i.player(), new DragNodeAction(copy, offset, i.editor(), i.player()));
        }
    }

    @Override
    public void stop(CodeEditor editor, Player player) {
        node.borderColor(NamedTextColor.WHITE);
        editor.unlockWidget(node, player);
        for (NodeIOWidget IOWidget : iowidgets) {
            for (WireWidget wire : IOWidget.connections) {
                wire.unlockWire(editor, player);
                wire.cleanup(editor);
            }
        }

        editor.unlockWidget(node, player);
    }
}
