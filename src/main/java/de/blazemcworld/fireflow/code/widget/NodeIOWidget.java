package de.blazemcworld.fireflow.code.widget;

import de.blazemcworld.fireflow.code.Interaction;
import de.blazemcworld.fireflow.code.action.WireAction;
import de.blazemcworld.fireflow.code.node.Node;
import de.blazemcworld.fireflow.code.type.SignalType;
import de.blazemcworld.fireflow.code.type.WireType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.InstanceContainer;

import java.util.ArrayList;
import java.util.List;

public class NodeIOWidget implements Widget {

    private final TextWidget text;
    public final List<WireWidget> connections = new ArrayList<>();
    private final boolean isInput;
    private final WireType<?> type;
    public final Node.Output<?> output;
    public final Node.Input<?> input;
    public final NodeWidget parent;

    public NodeIOWidget(NodeWidget parent, Node.Output<?> output) {
        type = output.type;
        isInput = false;
        this.output = output;
        this.input = null;
        this.parent = parent;
        text = new TextWidget(displayText());
        text.shiftLeft = true;
    }

    public NodeIOWidget(NodeWidget parent, Node.Input<?> input) {
        type = input.type;
        isInput = true;
        this.output = null;
        this.input = input;
        this.parent = parent;
        text = new TextWidget(displayText());
    }

    @Override
    public void setPos(Vec pos) {
        text.setPos(pos);
    }

    @Override
    public Vec getPos() {
        return text.getPos();
    }

    @Override
    public Vec getSize() {
        return text.getSize();
    }

    @Override
    public void update(InstanceContainer inst) {
        text.text(displayText());
        text.update(inst);
    }

    @Override
    public void remove() {
        text.remove();
    }

    @Override
    public boolean interact(Interaction i) {
        if (!inBounds(i.pos())) return false;
        if (i.type() == Interaction.Type.RIGHT_CLICK) {
            if (!this.isInput()) i.editor().setAction(i.player(), new WireAction(this));
            return true;
        }
        if (i.type() == Interaction.Type.LEFT_CLICK && isInput && input.inset != null) {
            insetValue(null);
            parent.update(i.editor().space.code);
            return true;
        }
        return false;
    }

    public boolean isInput() {
        return isInput;
    }

    public TextColor color() {
        return text.text().color();
    }

    public WireType<?> type() {
        return type;
    }

    @SuppressWarnings("unchecked")
    public void connect(WireWidget wire) {
        if (wire.type() == SignalType.INSTANCE) {
            NodeIOWidget input = wire.getOutputs().getFirst();
            for (NodeIOWidget output : wire.getInputs()) {
                ((Node.Output<Object>) output.output).connected = (Node.Input<Object>) input.input;
            }
        } else {
            NodeIOWidget output = wire.getInputs().getFirst();
            for (NodeIOWidget input : wire.getOutputs()) {
                ((Node.Input<Object>) input.input).connect((Node.Output<Object>) output.output);
            }
        }
        text.text(displayText());
        parent.refreshInputs();
    }

    @SuppressWarnings("unchecked")
    public void removed(WireWidget wire) {
        if (wire.type() == SignalType.INSTANCE) {
            if (wire.previousOutput != null) {
                wire.previousOutput.output.connected = null;
            }
        } else {
            if (wire.nextInput != null) {
                wire.nextInput.input.connected = null;
                wire.nextInput.input.setInset(null);
            }
        }
        text.text(displayText());
        parent.refreshInputs();
    }

    public void insetValue(String value) {
        for (WireWidget w : new ArrayList<>(connections)) {
            w.remove();
        }

        input.setInset(value);
        text.text(displayText());
        parent.refreshInputs();
    }

    private Component displayText() {
        String str = isInput ? ((connections.isEmpty() ? "○ " : "⏺ ") + input.getName()) :
            (output.getName() + (connections.isEmpty() ? " ○" : " ⏺"));
        
        if (isInput && input.inset != null) {
            str = "⏹ " + input.inset;
        }

        return Component.text(str).color(type.color);
    }

    @Override
    public List<Widget> getChildren() {
        return null;
    }
}