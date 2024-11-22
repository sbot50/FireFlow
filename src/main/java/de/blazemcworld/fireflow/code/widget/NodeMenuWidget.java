package de.blazemcworld.fireflow.code.widget;

import java.util.ArrayList;
import java.util.List;

import de.blazemcworld.fireflow.code.CodeEditor;
import de.blazemcworld.fireflow.code.Interaction;
import de.blazemcworld.fireflow.code.node.Node;
import de.blazemcworld.fireflow.code.node.NodeList;
import de.blazemcworld.fireflow.code.node.impl.function.FunctionCallNode;
import de.blazemcworld.fireflow.code.node.impl.function.FunctionDefinition;
import de.blazemcworld.fireflow.code.type.AllTypes;
import de.blazemcworld.fireflow.code.type.WireType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.InstanceContainer;

public class NodeMenuWidget implements Widget {

    private Widget root;

    public NodeMenuWidget(NodeList.Category category, CodeEditor editor, List<NodeList.Category> parents) {
        VerticalContainerWidget list = new VerticalContainerWidget();

        if (parents != null && !parents.isEmpty()) {
            NodeList.Category parent = parents.get(parents.size() - 1);
            ButtonWidget button = new ButtonWidget(new TextWidget(Component.text("🠄 Back").color(NamedTextColor.GRAY)));

            button.handler = interaction -> {
                if (interaction.type() != Interaction.Type.RIGHT_CLICK) return false;
                remove();
                interaction.editor().rootWidgets.remove(this);

                NodeMenuWidget n = new NodeMenuWidget(parent, interaction.editor(), parents.subList(0, parents.size() - 1));
                n.setPos(root.getPos());
                n.update(interaction.editor().space.code);
                interaction.editor().rootWidgets.add(n);
                return true;
            };

            list.widgets.add(button);
        }

        for (NodeList.Category subCategory : category.categories) {
            ButtonWidget button = new ButtonWidget(new ItemWidget(subCategory.icon), new TextWidget(Component.text(subCategory.name)));

            button.handler = interaction -> {
                if (interaction.type() != Interaction.Type.RIGHT_CLICK) return false;
                remove();
                interaction.editor().rootWidgets.remove(this);

                List<NodeList.Category> path = new ArrayList<>();
                if (parents != null) path.addAll(parents);
                path.add(category);
                NodeMenuWidget n = new NodeMenuWidget(subCategory, interaction.editor(), path);
                n.setPos(root.getPos());
                n.update(interaction.editor().space.code);
                interaction.editor().rootWidgets.add(n);
                return true;
            };

            list.widgets.add(button);
        }

        List<Node> nodes = category.nodes;
        if (category.isFunctions) {
            nodes = new ArrayList<>();
            for (FunctionDefinition fn : editor.functions.values()) {
                FunctionCallNode fnNode = new FunctionCallNode(fn);
                fn.callNodes.remove(fnNode); // Remove since its not actually a real node
                nodes.add(fnNode);
            }
        }

        for (Node node : nodes) {
            ButtonWidget button = new ButtonWidget(new ItemWidget(node.icon), new TextWidget(Component.text(node.getTitle())));

            button.handler = interaction -> {
                if (interaction.type() != Interaction.Type.RIGHT_CLICK) return false;
                remove();
                interaction.editor().rootWidgets.remove(this);

                createNode(interaction.editor(), interaction.pos(), node, new ArrayList<>());
                return true;
            };

            list.widgets.add(button);
        }

        if (list.widgets.size() == 1) { // Back button
            list.widgets.add(new TextWidget(Component.text("Empty category")));
        }

        BorderWidget<VerticalContainerWidget> border = new BorderWidget<>(list);
        border.backgroundColor(0x99000011);
        ButtonWidget button = new ButtonWidget(border);

        button.handler = interaction -> {
            if (interaction.type() == Interaction.Type.LEFT_CLICK) {
                remove();
                interaction.editor().rootWidgets.remove(this);
                return true;
            }
            return false;
        };

        root = button;
    }

    private void createNode(CodeEditor e, Vec pos, Node node, List<WireType<?>> types) {
        if (node instanceof FunctionCallNode call) {
            if (!e.functions.containsKey(call.function.name)) return;
            if (e.functions.get(call.function.name) != call.function) return;
        }
        if (node.getTypeCount() > types.size()) {
            List<WireType<?>> filtered = new ArrayList<>();
            for (WireType<?> type : AllTypes.all) {
                if (node.acceptsType(type, types.size())) {
                    filtered.add(type);
                }
            }

            TypeSelectorWidget selector = new TypeSelectorWidget(filtered, type -> {
                types.add(type);
                createNode(e, pos, node.copyWithTypes(types), types);
            });
            selector.setPos(pos);
            selector.update(e.space.code);
            e.rootWidgets.add(selector);
            return;
        }

        NodeWidget n;
        if (types.isEmpty()) {
            n = new NodeWidget(node.copy());
        } else {
            n = new NodeWidget(node.copyWithTypes(types));
        }

        Vec s = n.getSize();
        n.setPos(pos.add(Math.round(s.x() * 4) / 8f, Math.round(s.y() * 4) / 8f, 0));
        n.update(e.space.code);
        e.rootWidgets.add(n);
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
        root.update(inst);
    }

    @Override
    public void remove() {
        root.remove();
    }

    @Override
    public boolean interact(Interaction i) {
        return root.interact(i);
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
}
