package de.blazemcworld.fireflow.editor.action;

import de.blazemcworld.fireflow.editor.Bounds;
import de.blazemcworld.fireflow.editor.CodeEditor;
import de.blazemcworld.fireflow.editor.EditorAction;
import de.blazemcworld.fireflow.editor.widget.*;
import de.blazemcworld.fireflow.value.SignalValue;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.InstanceContainer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MoveSelectionAction implements EditorAction {

    private final RectWidget rect;
    private Vec start;
    private final CodeEditor editor;
    private final Player player;
    private Map<NodeWidget, Vec> nodes = new HashMap<>();
    private final Map<WireWidget, List<Vec>> wires = new HashMap<>();

    public MoveSelectionAction(InstanceContainer inst, Vec start, Player player, CodeEditor editor) {
        this.player = player;
        this.editor = editor;
        this.rect = new RectWidget(inst, new Bounds(start, start));
        this.start = start;
        rect.color(NamedTextColor.AQUA);
    }

    @Override
    public void rightClick(Vec cursor) {
        if (!nodes.isEmpty()) {
            editor.setAction(player, null);
        } else {
            for (NodeWidget node : editor.getNodesInBound(new Bounds(start, cursor))) {
                node.border.color(NamedTextColor.GREEN);
                nodes.put(node, node.origin.sub(cursor));
            }
            for (NodeWidget widget : nodes.keySet()) {
                for (NodeInputWidget input : widget.inputs) {
                    for (WireWidget wire : input.wires) {
                        if (!nodes.containsKey(wire.output.parent)) continue;

                        List<Vec> offsets = new ArrayList<>();
                        for (Vec relay : wire.relays) {
                            offsets.add(relay.sub(cursor));
                        }
                        wires.put(wire, offsets);
                    }
                }
            }
            start = cursor;
            rect.remove();
            if (nodes.isEmpty()) editor.setAction(player, null);
        }
    }

    @Override
    public void leftClick(Vec cursor) {
        editor.setAction(player, null);
    }

    @Override
    public void swapItem(Vec cursor) {
        Map<NodeWidget, Vec> newNodes = new HashMap<>();

        Map<NodeWidget, NodeWidget> cloneMap = new HashMap<>();
        for (NodeWidget node : nodes.keySet()) {
            NodeWidget newNode = node.cloneWidget();
            newNode.border.color(NamedTextColor.GREEN);
            editor.widgets.add(newNode);
            newNodes.put(newNode, nodes.get(node));
            node.border.color(NamedTextColor.WHITE);
            node.update(false);
            cloneMap.put(node, newNode);
        }

        wires.clear();
        for (Map.Entry<NodeWidget, NodeWidget> entry : cloneMap.entrySet()) {
            NodeWidget old = entry.getKey();
            NodeWidget cloned = entry.getValue();

            for (int i = 0; i < old.inputs.size(); i++) {
                NodeInputWidget oldInput = old.inputs.get(i);
                NodeInputWidget clonedInput = cloned.inputs.get(i);

                for (WireWidget wire : oldInput.wires) {
                    NodeWidget oldOther = wire.output.parent;
                    NodeWidget clonedOther = cloneMap.getOrDefault(oldOther, null);
                    if (clonedOther == null) continue;

                    int otherIndex = oldOther.outputs.indexOf(wire.output);
                    NodeOutputWidget clonedOtherOutput = clonedOther.outputs.get(otherIndex);

                    WireWidget clonedWire = new WireWidget(editor.inst, clonedInput, clonedOtherOutput, new ArrayList<>(wire.relays));
                    clonedInput.addWire(clonedWire);

                    List<Vec> offsets = new ArrayList<>();
                    for (Vec relay : clonedWire.relays) {
                        offsets.add(relay.sub(cursor));
                    }
                    wires.put(clonedWire, offsets);
                    
                    if (clonedInput.input.type == SignalValue.INSTANCE) {
                        clonedOtherOutput.output.connectSignal(clonedInput.input);
                    } else {
                        clonedInput.input.connectValue(clonedOtherOutput.output);
                    }
                }
            }
        }

        nodes = newNodes;
    }

    @Override
    public void tick(Vec cursor) {
        if (!nodes.isEmpty()) {
            for (NodeWidget node : nodes.keySet()) {
                node.origin = cursor.add(nodes.get(node));
                node.update(false);
            }
            for (Map.Entry<WireWidget, List<Vec>> entry : wires.entrySet()) {
                WireWidget wire = entry.getKey();
                for (int i = 0; i < wire.relays.size(); i++) {
                    wire.relays.set(i, entry.getValue().get(i).add(cursor));
                }
                wire.update();
            }
        } else rect.update(new Bounds(start, cursor));
    }

    @Override
    public void stop() {
        for (NodeWidget node : nodes.keySet()) {
            node.border.color(NamedTextColor.WHITE);
            node.update(false);
        }
        nodes.clear();
        rect.remove();
    }
}
