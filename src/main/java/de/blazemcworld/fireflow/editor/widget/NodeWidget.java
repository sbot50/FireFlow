package de.blazemcworld.fireflow.editor.widget;

import de.blazemcworld.fireflow.compiler.FunctionDefinition;
import de.blazemcworld.fireflow.editor.Bounds;
import de.blazemcworld.fireflow.editor.CodeEditor;
import de.blazemcworld.fireflow.editor.Widget;
import de.blazemcworld.fireflow.editor.action.CreateWireAction;
import de.blazemcworld.fireflow.editor.action.MoveNodeAction;
import de.blazemcworld.fireflow.node.*;
import de.blazemcworld.fireflow.util.Messages;
import de.blazemcworld.fireflow.util.TextWidth;
import de.blazemcworld.fireflow.value.AllValues;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerChatEvent;
import net.minestom.server.instance.InstanceContainer;

import java.util.ArrayList;
import java.util.List;

public class NodeWidget implements Widget {

    public final List<NodeInputWidget> inputs = new ArrayList<>();
    public final List<NodeOutputWidget> outputs = new ArrayList<>();
    private final InstanceContainer inst;
    public final Node node;
    private TextWidget title;
    public RectWidget border;
    private Bounds bounds;
    public Vec origin;

    public NodeWidget(Vec origin, InstanceContainer inst, Node node) {
        this.origin = origin;
        this.inst = inst;
        this.node = node;
        update(true);
    }

    public void update(boolean init) {
        double inputWidth = 0.1;
        for (NodeInput input : node.inputs) {
            String text;
            if (input.getInset() != null) {
                text = "⏹ " + input.type.formatInset(input.getInset());
            } else {
                text = "○ " + input.getName();
            }
            if (input.hasDefault()) text += "*";
            inputWidth = Math.max(inputWidth, TextWidth.calculate(text, false) / 40);
        }
        double outputWidth = 0.1;
        for (NodeOutput output : node.outputs) {
            outputWidth = Math.max(outputWidth, TextWidth.calculate(output.getName() + " ○", false) / 40);
        }
        double titleWidth = TextWidth.calculate(node.name, false) / 40;

        if (node instanceof ExtractionNode) {
            inputWidth = titleWidth * 0.5 + 0.1;
            outputWidth = titleWidth * 0.5 + 0.1;
        }
        if (titleWidth > inputWidth + outputWidth) {
            double scale = titleWidth / (inputWidth + outputWidth);
            inputWidth *= scale;
            outputWidth *= scale;
        }

        Vec titlePos = origin.add(inputWidth * 0.5 - outputWidth * 0.5 + titleWidth * 0.5, 0.3, 0);
        if (init) {
            title = new TextWidget(titlePos, inst, Component.text(node.name));
        } else {
            title.position = titlePos;
            title.update();
        }
        int maxIO = Math.max(node.inputs.size(), node.outputs.size());
        if (node instanceof ExtractionNode) maxIO = 0;
        bounds = new Bounds(
                origin.add(inputWidth + 0.2, 0.7, 0),
                origin.add(-outputWidth - 0.2, -maxIO * 0.3 + 0.25, 0)
        );
        if (init) {
            border = new RectWidget(inst, bounds);
        } else {
            border.update(bounds);
        }

        Vec pos = origin.add(inputWidth + 0.1, 0, 0);
        if (node instanceof ExtractionNode) pos = origin.add(titleWidth * 0.5 + 0.2, 0.3, 0);
        int index = 0;
        for (NodeInput input : node.inputs) {
            if (init) {
                Component text = Component.text("○ " + input.getName()).color(input.type.getColor());
                if (input.hasDefault()) text = text.append(Component.text("*").color(NamedTextColor.GRAY));
                NodeInputWidget btn = new NodeInputWidget(pos, inst, text, input, this);
                inputs.add(btn);
                btn.leftClick = (player, editor) -> {
                    if (input.getInset() != null) {
                        input.inset(null);
                        btn.update();
                        return;
                    }
                    tryRemove(editor);
                };
                btn.rightClick = (player, editor) -> editor.setAction(player, new CreateWireAction(input, btn, player, editor));
            } else {
                inputs.get(index).position = pos;
                inputs.get(index).update();
            }
            pos = pos.add(0, -0.3, 0);
            index++;
        }
        index = 0;
        pos = origin.add(-outputWidth - 0.1, 0, 0);
        if (node instanceof ExtractionNode) pos = origin.add(-titleWidth * 0.5 - 0.2, 0.3, 0);
        for (NodeOutput output : node.outputs) {
            if (init) {
                NodeOutputWidget btn = new NodeOutputWidget(pos.add(TextWidth.calculate(output.getName() + " ○", false) / 40, 0, 0), inst, Component.text(output.getName() + " ○").color(output.type.getColor()), output, this);
                outputs.add(btn);
                btn.leftClick = (player, editor) -> tryRemove(editor);
                btn.rightClick = (player, editor) -> editor.setAction(player, new CreateWireAction(output, btn, player, editor));
            } else {
                outputs.get(index).position = pos.add(TextWidth.calculate(output.getName() + " ○", false) / 40, 0, 0);
                outputs.get(index).update();
            }
            pos = pos.add(0, -0.3, 0);
            index++;
        }
    }

    @Override
    public Widget select(Player player, Vec cursor) {
        for (NodeInputWidget btn : inputs) {
            Widget res = btn.select(player, cursor);
            if (res != null) return res;
        }
        for (NodeOutputWidget btn : outputs) {
            Widget res = btn.select(player, cursor);
            if (res != null) return res;
        }
        return bounds.includes2d(cursor) ? this : null;
    }

    @Override
    public void chat(Vec cursor, PlayerChatEvent event, CodeEditor editor) {
        if (node instanceof FunctionDefinition.DefinitionNode defNode) {
            event.setCancelled(true);
            FunctionDefinition prev = defNode.getDefinition();
            if (editor.inUse(prev)) {
                event.getPlayer().sendMessage(Messages.error("Can't rename used functions!"));
                return;
            }
            FunctionDefinition next = new FunctionDefinition(event.getMessage(), prev.fnInputs, prev.fnOutputs);
            editor.redefine(prev, next);
        }
    }

    @Override
    public void swapItem(Vec cursor, Player player, CodeEditor editor) {
        if (node instanceof FunctionDefinition.DefinitionNode defNode) {
            FunctionDefinition prev = defNode.getDefinition();
            if (editor.inUse(prev)) {
                player.sendMessage(Messages.error("Can't modify used functions!"));
                return;
            }

            boolean isInputs = defNode.getDefinition().fnInputsNode == node;
            GenericSelectorWidget.choose(cursor.add(isInputs ? 2 : -2, 0, 0), editor, List.of(AllValues.any), chosen -> {
                if (editor.inUse(prev)) return;
                List<NodeOutput> inputs = new ArrayList<>(prev.fnInputs);
                List<NodeInput> outputs = new ArrayList<>(prev.fnOutputs);
                if (isInputs) {
                    inputs.add(new NodeOutput("Unnamed", chosen.getFirst()));
                } else {
                    outputs.add(new NodeInput("Unnamed", chosen.getFirst()));
                }
                FunctionDefinition next = new FunctionDefinition(prev.fnName, inputs, outputs);
                editor.redefine(prev, next);
            });
        }
    }

    @Override
    public void leftClick(Vec cursor, Player player, CodeEditor editor) {
        tryRemove(editor);
    }

    private void tryRemove(CodeEditor editor) {
        if (node instanceof FunctionDefinition.DefinitionNode def) {
            if (editor.inUse(def.getDefinition())) return;
            editor.remove(def.getDefinition());
            return;
        }
        editor.remove(this);
    }

    @Override
    public void rightClick(Vec cursor, Player player, CodeEditor editor) {
        Vec offset = origin.sub(cursor);
        border.color(NamedTextColor.GREEN);
        editor.setAction(player, new MoveNodeAction(offset, this, player, editor));
    }

    @Override
    public void remove() {
        for (NodeInputWidget button : inputs) {
            button.remove();
        }
        for (NodeOutputWidget button : outputs) {
            button.remove();
        }
        title.remove();
        border.remove();
    }

    public NodeWidget cloneWidget() {
        Node newNode = NodeList.nodes.get(node.getBaseName()).get();
        newNode = newNode.fromGenerics(node.generics());
        NodeWidget newNodeWidget = new NodeWidget(origin, inst, newNode);

        for (int i = 0; i < inputs.size(); i++) {
            NodeInputWidget oldInput = inputs.get(i);
            NodeInputWidget newInput = newNodeWidget.inputs.get(i);
            if (oldInput.input.getInset() != null) {
                newInput.input.inset(oldInput.input.getInset());
            }
        }

        newNodeWidget.update(false);
        return newNodeWidget;
    }

    public Bounds getBounds() { return bounds; }
}
