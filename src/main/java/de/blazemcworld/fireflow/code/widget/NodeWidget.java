package de.blazemcworld.fireflow.code.widget;

import de.blazemcworld.fireflow.code.Interaction;
import de.blazemcworld.fireflow.code.action.DragNodeAction;
import de.blazemcworld.fireflow.code.node.Node;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.InstanceContainer;

public class NodeWidget implements Widget {

    private final Node node;
    private final BorderWidget root;

    public NodeWidget(Node node) {
        this.node = node;

        VerticalContainerWidget main = new VerticalContainerWidget();
        main.align = VerticalContainerWidget.Align.CENTER;
        main.widgets.add(new TextWidget(Component.text(node.getTitle())));

        HorizontalContainerWidget ioArea = new HorizontalContainerWidget();
        main.widgets.add(ioArea);

        VerticalContainerWidget inputArea = new VerticalContainerWidget();
        ioArea.widgets.add(inputArea);

        ioArea.widgets.add(new SpacingWidget(new Vec(1/8f, 0, 0)));

        for (Node.Input<?> input : node.inputs) {
            inputArea.widgets.add(new NodeIOWidget(input));
        }

        VerticalContainerWidget outputArea = new VerticalContainerWidget();
        ioArea.widgets.add(outputArea);

        for (Node.Output<?> output : node.outputs) {
            outputArea.widgets.add(new NodeIOWidget(output));
        }

        root = new BorderWidget(main);
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
        if (!inBounds(i.pos())) return false;
        if (i.type() == Interaction.Type.LEFT_CLICK) {
            remove();
            i.editor().rootWidgets.remove(this);
            return true;
        }
        if (root.interact(i)) return true;
        if (i.type() == Interaction.Type.RIGHT_CLICK && i.editor().lockWidget(this, i.player())) {
            i.editor().setAction(i.player(), new DragNodeAction(this, getPos().sub(i.pos())));
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
}
