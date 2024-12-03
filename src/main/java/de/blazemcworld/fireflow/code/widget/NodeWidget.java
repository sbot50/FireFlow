package de.blazemcworld.fireflow.code.widget;

import de.blazemcworld.fireflow.code.CodeEditor;
import de.blazemcworld.fireflow.code.Interaction;
import de.blazemcworld.fireflow.code.action.DragNodeAction;
import de.blazemcworld.fireflow.code.node.Node;
import de.blazemcworld.fireflow.code.node.impl.function.FunctionCallNode;
import de.blazemcworld.fireflow.code.node.impl.function.FunctionInputsNode;
import de.blazemcworld.fireflow.code.node.impl.function.FunctionOutputsNode;
import de.blazemcworld.fireflow.util.Translations;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.InstanceContainer;

import java.util.ArrayList;
import java.util.List;

public class NodeWidget implements Widget {

    public final Node node;
    private final BorderWidget<VerticalContainerWidget> root;

    public NodeWidget(Node node) {
        this.node = node;

        VerticalContainerWidget main = new VerticalContainerWidget();
        main.align = VerticalContainerWidget.Align.CENTER;
        HorizontalContainerWidget title = new HorizontalContainerWidget(new ItemWidget(node.icon), new TextWidget(Component.text(node.getTitle())));
        main.widgets.add(title);

        HorizontalContainerWidget ioArea = new HorizontalContainerWidget();
        main.widgets.add(ioArea);

        VerticalContainerWidget inputArea = new VerticalContainerWidget();
        ioArea.widgets.add(inputArea);

        SpacingWidget spacing = new SpacingWidget(new Vec(1/8f, 0, 0));
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
        root.update(inst);
    }

    @Override
    public void remove() {
        root.remove();
    }

    public void remove(CodeEditor editor) {
        for (NodeIOWidget io : getIOWidgets()) {
            for (WireWidget wire : new ArrayList<>(io.connections)) {
                wire.removeConnection(editor);
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
        if (i.type() == Interaction.Type.LEFT_CLICK) {
            if (node instanceof FunctionInputsNode || node instanceof FunctionOutputsNode) {
                i.player().sendMessage(Component.text(Translations.get("error.function.delete_command")).color(NamedTextColor.RED));
                return true;
            }
            remove(i.editor());
            i.editor().rootWidgets.remove(this);
            return true;
        }
        if (i.type() == Interaction.Type.RIGHT_CLICK && i.editor().lockWidget(this, i.player())) {
            i.editor().setAction(i.player(), new DragNodeAction(this, getPos().sub(i.pos()), i.editor()));
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
