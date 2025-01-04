package de.blazemcworld.fireflow.code.widget;

import de.blazemcworld.fireflow.code.CodeEditor;
import de.blazemcworld.fireflow.code.Interaction;
import de.blazemcworld.fireflow.code.action.DragNodeAction;
import de.blazemcworld.fireflow.code.node.Node;
import de.blazemcworld.fireflow.code.node.impl.function.FunctionCallNode;
import de.blazemcworld.fireflow.code.node.impl.function.FunctionInputsNode;
import de.blazemcworld.fireflow.code.node.impl.function.FunctionOutputsNode;
import de.blazemcworld.fireflow.code.type.SignalType;
import de.blazemcworld.fireflow.util.Translations;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.InstanceContainer;

import java.util.ArrayList;
import java.util.List;

public class NodeWidget implements Widget {

    public final Node node;
    private final BorderWidget<VerticalContainerWidget> root;
    private final VerticalContainerWidget inputArea;
    private final CodeEditor editor;
    private boolean refreshingInputs = false;

    public NodeWidget(Node node, CodeEditor editor) {
        this.editor = editor;
        this.node = node;

        VerticalContainerWidget main = new VerticalContainerWidget();
        main.align = VerticalContainerWidget.Align.CENTER;
        HorizontalContainerWidget title = new HorizontalContainerWidget(new ItemWidget(node.icon), new TextWidget(Component.text(node.getTitle())));
        main.widgets.add(title);

        HorizontalContainerWidget ioArea = new HorizontalContainerWidget();
        main.widgets.add(ioArea);

        inputArea = new VerticalContainerWidget();
        ioArea.widgets.add(inputArea);

        SpacingWidget spacing = new SpacingWidget(new Vec(1 / 8f, 0, 0));
        ioArea.widgets.add(spacing);

        for (Node.Input<?> input : node.inputs) {
            inputArea.widgets.add(new NodeIOWidget(this, input));
        }

        VerticalContainerWidget outputArea = new VerticalContainerWidget();
        ioArea.widgets.add(outputArea);
        outputArea.align = VerticalContainerWidget.Align.RIGHT;

        for (Node.Output<?> output : node.outputs) {
            outputArea.widgets.add(new NodeIOWidget(this, output));
        }

        double needed = Math.max(0, title.getSize().x() - ioArea.getSize().x());
        spacing.size = spacing.size.withX(spacing.size.x() + Math.ceil(needed * 8) / 8);
        root = new BorderWidget<>(main);
        root.backgroundColor(0x99001100);
    }

    @Override
    public void setPos(Vec pos) {
        root.setPos(pos);
    }

    @Override
    public Vec getPos() {
        return root.getPos();
    }

    @Override
    public Vec getSize() {
        return root.getSize();
    }

    @Override
    public void update(InstanceContainer inst) {
        refreshInputs();
        root.update(inst);
    }

    public void refreshInputs() {
        if (refreshingInputs) return;

        boolean didRemove = inputArea.widgets.removeIf(w -> {
            if (w instanceof NodeIOWidget io && !node.inputs.contains(io.input)) {
                io.remove();
                return true;
            }
            return false;
        });

        for (int i = 0; i < node.inputs.size(); i++) {
            Node.Input<?> input = node.inputs.get(i);
            if (i < inputArea.widgets.size() && inputArea.widgets.get(i) instanceof NodeIOWidget io && io.input == input) {
                continue;
            }
            NodeIOWidget io = new NodeIOWidget(this, input);
            inputArea.widgets.add(i, io);
        }
        if (editor != null) {
            refreshingInputs = true;
            update(editor.space.code);
            refreshingInputs = false;
        }

        if (didRemove) {
            for (NodeIOWidget i : getIOWidgets()) {
                if (!i.isInput()) continue;
                if (i.connections.isEmpty()) continue;

                double targetY = i.getPos().y() - 1 / 8f;
                for (WireWidget w : i.connections) {
                    if (w.line.to.y() == targetY) continue;
                    if (w.line.from.y() != w.line.to.y()) {
                        w.line.to = w.line.to.withY(targetY);
                        w.line.update(editor.space.code);
                    } else {
                        if (w.previousWires.isEmpty() || w.previousWires.getFirst().line.from.y() == w.previousWires.getFirst().line.to.y()) {
                            Vec mid = w.line.from.add(w.line.to).div(2);
                            List<WireWidget> wires = w.splitWire(editor, mid);
                            WireWidget nw = new WireWidget(wires.getFirst(), w.type(), mid, editor.space.code);
                            nw.addNextWire(wires.getLast());
                            wires.getFirst().nextWires.remove(wires.getLast());
                            wires.getLast().addPreviousWire(nw);
                            wires.getLast().previousWires.remove(wires.getFirst());
                            wires.getLast().line.from = wires.getLast().line.from.withY(targetY);
                            wires.getLast().line.to = wires.getLast().line.to.withY(targetY);
                            nw.line.to = wires.getLast().line.from;
                            wires.getLast().update(editor.space.code);
                            editor.rootWidgets.add(nw);
                            nw.update(editor.space.code);
                        } else {
                            w.previousWires.getFirst().line.to = w.previousWires.getFirst().line.to.withY(targetY);
                            w.previousWires.getFirst().update(editor.space.code);
                            w.line.from = w.line.from.withY(targetY);
                            w.line.to = w.line.to.withY(targetY);
                            w.line.update(editor.space.code);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void remove() {
        root.remove();
    }

    public void remove(CodeEditor editor) {
        for (NodeIOWidget io : getIOWidgets()) {
            for (WireWidget wire : new ArrayList<>(io.connections)) {
                List<NodeIOWidget> inputs = wire.getInputs();
                List<NodeIOWidget> outputs = wire.getOutputs();
                wire.removeConnection(editor);
                if (wire.type() == SignalType.INSTANCE && !outputs.getFirst().connections.isEmpty()) outputs.getFirst().connections.getFirst().cleanup(editor);
                else if (!inputs.getFirst().connections.isEmpty()) inputs.getFirst().connections.getFirst().cleanup(editor);
            }
        }
        remove();
        if (node instanceof FunctionCallNode call) {
            call.function.callNodes.remove(call);
        }
    }

    @Override
    public boolean interact(Interaction i) {
        if (!inBounds(i.pos())) return false;
        if (root.interact(i)) return true;
        boolean lockedWire = false;
        Player lockPlayer = i.player();
        for (NodeIOWidget io : getIOWidgets()) {
            for (WireWidget wire : io.connections) {
                if (editor.isLocked(wire) != null && !editor.isLockedByPlayer(wire, i.player())) {
                    lockedWire = true;
                    lockPlayer = editor.isLocked(wire);
                    break;
                }
            }
        }
        if (i.type() == Interaction.Type.LEFT_CLICK) {
            if (lockedWire) {
                i.player().sendMessage(Component.text(Translations.get("error.locked.connected", lockPlayer.getUsername())).color(NamedTextColor.RED));
            } else {
                if (node instanceof FunctionInputsNode || node instanceof FunctionOutputsNode) {
                    i.player().sendMessage(Component.text(Translations.get("error.function.delete_command")).color(NamedTextColor.RED));
                    return true;
                }
                remove(i.editor());
                i.editor().rootWidgets.remove(this);
            }
            return true;
        }
        if (i.type() == Interaction.Type.RIGHT_CLICK && i.editor().lockWidget(this, i.player())) {
            if (!lockedWire) i.editor().setAction(i.player(), new DragNodeAction(this, getPos().sub(i.pos()), i.editor(), i.player()));
            else i.player().sendMessage(Component.text(Translations.get("error.locked.connected", lockPlayer.getUsername())).color(NamedTextColor.RED));
            return true;
        }
        return false;
    }

    public void borderColor(TextColor color) {
        root.color(color);
    }

    @Override
    public Widget getWidget(Vec pos) {
        if (!inBounds(pos)) return null;
        return root.getWidget(pos);
    }

    @Override
    public List<Widget> getChildren() {
        return List.of(root);
    }

    public List<NodeIOWidget> getInputs() {
        List<NodeIOWidget> list = new ArrayList<>();
        collectIOWidgets(root, list);
        list = list.stream().filter(NodeIOWidget::isInput).toList();
        return list;
    }

    public List<NodeIOWidget> getOutputs() {
        List<NodeIOWidget> list = new ArrayList<>();
        collectIOWidgets(root, list);
        list = list.stream().filter(io -> !io.isInput()).toList();
        return list;
    }

    public List<NodeIOWidget> getIOWidgets() {
        List<NodeIOWidget> list = new ArrayList<>();
        collectIOWidgets(root, list);
        return list;
    }

    private void collectIOWidgets(Widget node, List<NodeIOWidget> list) {
        if (node == null) {
            return;
        }

        if (node instanceof NodeIOWidget) {
            list.add((NodeIOWidget) node);
            return;
        }

        if (node.getChildren() == null) return;
        for (Widget widget : node.getChildren()) {
            collectIOWidgets(widget, list);
        }
    }
}
