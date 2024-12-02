package de.blazemcworld.fireflow.code.widget;

import de.blazemcworld.fireflow.code.CodeEditor;
import de.blazemcworld.fireflow.code.Interaction;
import de.blazemcworld.fireflow.code.action.WireAction;
import de.blazemcworld.fireflow.code.type.SignalType;
import de.blazemcworld.fireflow.code.type.WireType;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.InstanceContainer;

import java.util.ArrayList;
import java.util.List;

public class WireWidget implements Widget {

    public NodeIOWidget previousOutput;
    public List<WireWidget> previousWires = new ArrayList<>();
    public final LineElement line = new LineElement();
    public List<WireWidget> nextWires = new ArrayList<>();
    public NodeIOWidget nextInput;
    private final WireType<?> type;

    public WireWidget(NodeIOWidget previousOutput, WireType<?> type, Vec cursor) {
        if (!previousOutput.isInput()) this.previousOutput = previousOutput;
        else this.nextInput = previousOutput;
        if (previousOutput.isInput()) line.from = previousOutput.getPos().sub(1/8f-1/32f, 1/8f, 0);
        else line.from = previousOutput.getPos().sub(previousOutput.getSize().sub(1/8f, 1/8f, 0));
        line.to = cursor;
        line.color(previousOutput.color());
        this.type = type;
    }

    public WireWidget(Vec start, WireType<?> type, Vec end) {
        line.from = start;
        line.to = end;
        line.color(type.color);
        this.type = type;
    }

    public WireWidget(WireWidget previousWire, WireType<?> type, Vec cursor) {
        this.previousWires.add(previousWire);
        previousWire.nextWires.add(this);
        line.from = previousWire.line.to;
        line.to = cursor;
        line.color(previousWire.line.color());
        this.type = type;
    }

    public WireWidget(WireWidget wire, WireType<?> type, Vec cursor, boolean isNext) {
        if (!isNext) {
            this.previousWires.add(wire);
            line.from = wire.line.to;
        } else {
            this.nextWires.add(wire);
            line.from = wire.line.from;
        }
        line.to = cursor;
        line.color(wire.line.color());
        this.type = type;
    }

    public WireWidget(List<WireWidget> previousWires, WireType<?> type, Vec cursor) {
        this.previousWires.addAll(previousWires);
        line.from = previousWires.getFirst().line.to;
        line.to = cursor;
        line.color(previousWires.getFirst().line.color());
        this.type = type;
    }

    public WireWidget(WireType<?> type, Vec from, Vec to) {
        line.from = from;
        line.to = to;
        line.color(type.color);
        this.type = type;
    }

    @Override
    public void setPos(Vec pos) {
        if (Math.abs(pos.x() - line.from.x()) >= Math.abs(pos.y() - line.from.y())) pos = new Vec(pos.x(), line.from.y(), 0);
        else pos = new Vec(line.from.x(), pos.y(), 0);
        line.to = pos;
    }

    @Override
    public Vec getPos() {
        return new Vec(Math.max(line.from.x(), line.to.x()), Math.max(line.from.y(), line.to.y()), 0).add(1/16f, 1/16f, 0);
    }

    @Override
    public Vec getSize() {
        return getPos().sub(Math.min(line.from.x(), line.to.x()), Math.min(line.from.y(), line.to.y()), 0).add(1/16f, 1/16f, 0);
    }

    @Override
    public void update(InstanceContainer inst) {
        line.update(inst);
    }

    @Override
    public void remove() {
        line.remove();

        if (nextInput != null) {
            nextInput.connections.remove(this);
            nextInput.removed(this);
        }
        if (previousOutput != null) {
            previousOutput.connections.remove(this);
            previousOutput.removed(this);
        }

        for (WireWidget wire : previousWires) {
            wire.nextWires.remove(this);
        }

        for (WireWidget wire : nextWires) {
            wire.previousWires.remove(this);
        }
    }

    public void removeAll(CodeEditor editor) {
        this.remove();
        for (WireWidget wire : previousWires) {
            wire.removeAll(editor);
            editor.rootWidgets.remove(wire);
        }
        editor.rootWidgets.remove(this);
    }

    @Override
    public boolean interact(Interaction i) {
        if (!inBounds(i.pos())) return false;
        if (i.type() == Interaction.Type.LEFT_CLICK) {
            removeConnection(i.editor());
            return true;
        } else if (i.type() == Interaction.Type.RIGHT_CLICK) {
            if (type != SignalType.INSTANCE) {
                i.editor().setAction(i.player(), new WireAction(this, i.pos()));
                return true;
            }
        }
        return false;
    }

    public void addNextWire(WireWidget nextWire) {
        this.nextWires.add(nextWire);
    }

    public void addNextWires(List<WireWidget> nextWires) {
        this.nextWires.addAll(nextWires);
    }

    public void addPreviousWire(WireWidget previousWire) {
        this.previousWires.add(previousWire);
    }

    public void addPreviousWires(List<WireWidget> previousWires) {
        this.previousWires.addAll(previousWires);
    }

    public void setNextInput(NodeIOWidget nextInput) {
        this.nextInput = nextInput;
    }

    public void setPreviousOutput(NodeIOWidget previousOutput) {
        this.previousOutput = previousOutput;
    }

    private void removeNext(CodeEditor editor) {
        List<WireWidget> nextWiresClone = new ArrayList<>(nextWires);
        this.remove();
        for (WireWidget wire : nextWiresClone) {
            wire.removeNext(editor);
            editor.rootWidgets.remove(wire);
        }
    }

    private void removePrevious(CodeEditor editor) {
        List<WireWidget> previousWiresClone = new ArrayList<>(previousWires);
        this.remove();
        for (WireWidget wire : previousWiresClone) {
            wire.removePrevious(editor);
            editor.rootWidgets.remove(wire);
        }
    }

    private boolean removeWithoutOutputs(CodeEditor editor) {
        if (!this.nextWires.isEmpty() || this.nextInput != null) return false;
        this.remove();
        for (WireWidget wire : previousWires) {
            if (wire.removeWithoutOutputs(editor)) editor.rootWidgets.remove(wire);
        }
        return true;
    }

    private boolean removeWithoutInputs(CodeEditor editor) {
        if (!this.previousWires.isEmpty() || this.previousOutput != null) return false;
        this.remove();
        for (WireWidget wire : nextWires) {
            if (wire.removeWithoutInputs(editor)) editor.rootWidgets.remove(wire);
        }
        return true;
    }

    public void removeConnection(CodeEditor editor) {
        List<WireWidget> nextWiresClone = new ArrayList<>(nextWires);
        List<WireWidget> previousWiresClone = new ArrayList<>(previousWires);
        this.remove();
        for (WireWidget wire : previousWiresClone) {
            if (this.type instanceof SignalType) {
                wire.removePrevious(editor);
                editor.rootWidgets.remove(wire);
            }
            else if (wire.removeWithoutOutputs(editor)) editor.rootWidgets.remove(wire);
        }

        for (WireWidget wire : nextWiresClone) {
            if (!(this.type instanceof SignalType)) {
                wire.removeNext(editor);
                editor.rootWidgets.remove(wire);
            }
            else if (wire.removeWithoutInputs(editor)) editor.rootWidgets.remove(wire);
        }
        editor.rootWidgets.remove(this);
    }

    public List<NodeIOWidget> getInputs() {
        List<NodeIOWidget> list = new ArrayList<>();
        for (WireWidget wire : nextWires) {
            list.addAll(wire.getInputs(this));
        }

        if (previousOutput != null) {
            list.add(previousOutput);
            return list;
        }
        for (WireWidget wire : previousWires) {
            list.addAll(wire.getInputs(this));
        }
        return list;
    }

    private List<NodeIOWidget> getInputs(WireWidget prev) {
        List<NodeIOWidget> list = new ArrayList<>();
        for (WireWidget wire : nextWires) {
            if (wire == prev) continue;
            list.addAll(wire.getInputs(this));
        }

        if (previousOutput != null) {
            list.add(previousOutput);
            return list;
        }
        for (WireWidget wire : previousWires) {
            if (wire == prev) continue;
            list.addAll(wire.getInputs(this));
        }
        return list;
    }

    public List<NodeIOWidget> getOutputs() {
        List<NodeIOWidget> list = new ArrayList<>();
        for (WireWidget wire : previousWires) {
            list.addAll(wire.getOutputs(this));
        }

        if (nextInput != null) {
            list.add(nextInput);
            return list;
        }
        for (WireWidget wire : nextWires) {
            list.addAll(wire.getOutputs(this));
        }
        return list;
    }

    private List<NodeIOWidget> getOutputs(WireWidget prev) {
        List<NodeIOWidget> list = new ArrayList<>();
        for (WireWidget wire : previousWires) {
            if (wire == prev) continue;
            list.addAll(wire.getOutputs(this));
        }

        if (nextInput != null) {
            list.add(nextInput);
            return list;
        }
        for (WireWidget wire : nextWires) {
            if (wire == prev) continue;
            list.addAll(wire.getOutputs(this));
        }
        return list;
    }

    public WireType<?> type() {
        return type;
    }

    @Override
    public List<Widget> getChildren() {
        return null;
    }

    public List<WireWidget> splitWire(CodeEditor editor, Vec pos) {
        WireWidget w1;
        if (this.previousWires.isEmpty()) w1 = new WireWidget(this.previousOutput, this.type(), pos);
        else w1 = new WireWidget(this.previousWires, this.type(), pos);
        editor.rootWidgets.add(w1);
        for (WireWidget wireWidget1 : this.previousWires) {
            wireWidget1.nextWires.remove(this);
            wireWidget1.nextWires.add(w1);
        }
        WireWidget w2 = new WireWidget(w1, this.type(), this.line.to);
        editor.rootWidgets.add(w2);
        w2.addNextWires(this.nextWires);
        for (WireWidget nextWire : this.nextWires) {
            nextWire.previousWires.remove(this);
            nextWire.previousWires.add(w2);
        }
        NodeIOWidget previousOutput = this.previousOutput;
        if (previousOutput != null) {
            w1.setPreviousOutput(previousOutput);
            previousOutput.connections.remove(this);
            previousOutput.removed(this);
            previousOutput.connections.add(w1);
        }
        NodeIOWidget nextInput = this.nextInput;
        if (nextInput != null) {
            w2.setNextInput(nextInput);
            nextInput.connections.remove(this);
            nextInput.removed(this);
            nextInput.connections.add(w2);
        }
        this.remove();
        if (previousOutput != null) previousOutput.connect(w1);
        if (nextInput != null) nextInput.connect(w2);
        editor.rootWidgets.remove(this);
        w1.update(editor.space.code);
        w2.update(editor.space.code);
        return List.of(w1, w2);
    }

    public boolean isValid() {
        return !getInputs().isEmpty() && !getOutputs().isEmpty();
    }

    public void connectNext(WireWidget wire) {
        this.nextWires.add(wire);
        wire.previousWires.add(this);
    }

    public void connectNext(NodeIOWidget node) {
        this.nextInput = node;
        node.connections.add(this);
    }

    public void connectPrevious(WireWidget wire) {
        this.previousWires.add(wire);
        wire.nextWires.add(this);
    }

    public void connectPrevious(NodeIOWidget node) {
        this.previousOutput = node;
        node.connections.add(this);
    }

    public void cleanup(CodeEditor editor) {
        while (true) {
            if (previousWires.size() == 1 && sameDirection(previousWires.getFirst().line.from, previousWires.getFirst().line.to, line.from, line.to)) {
                combine(previousWires.getFirst(), editor, true);
                continue;
            }
            if (nextWires.size() == 1 && sameDirection(nextWires.getFirst().line.from, nextWires.getFirst().line.to, line.from, line.to)) {
                combine(nextWires.getFirst(), editor, false);
                continue;
            }
            break;
        }
        previousWires.forEach(wire -> wire.cleanupPrevious(editor));
        nextWires.forEach(wire -> wire.cleanupNext(editor));
    }

    private void cleanupPrevious(CodeEditor editor) {
        while (true) {
            if (previousWires.size() == 1 && sameDirection(previousWires.getFirst().line.from, previousWires.getFirst().line.to, line.from, line.to)) {
                combine(previousWires.getFirst(), editor, true);
                continue;
            }
            break;
        }
        previousWires.forEach(wire -> wire.cleanupPrevious(editor));
    }

    private void cleanupNext(CodeEditor editor) {
        while (true) {
            if (nextWires.size() == 1 && sameDirection(nextWires.getFirst().line.from, nextWires.getFirst().line.to, line.from, line.to)) {
                combine(nextWires.getFirst(), editor, false);
                continue;
            }
            break;
        }
        nextWires.forEach(wire -> wire.cleanupNext(editor));
    }

    private void combine(WireWidget wire, CodeEditor editor, boolean isPrevious) {
        if (isPrevious) {
            this.previousWires.remove(wire);
            this.previousWires.addAll(wire.previousWires);
            this.previousWires.forEach(prevWire -> {
                prevWire.nextWires.remove(wire);
                prevWire.nextWires.add(this);
            });
            this.line.from = wire.line.from;
            if (wire.previousOutput != null) {
                this.previousOutput = wire.previousOutput;
                this.previousOutput.connections.remove(wire);
                this.previousOutput.removed(wire);
                wire.previousOutput = null;
                this.previousOutput.connections.add(this);
                this.previousOutput.connect(this);
            }
        } else {
            this.nextWires.remove(wire);
            this.nextWires.addAll(wire.nextWires);
            this.nextWires.forEach(prevWire -> {
                prevWire.previousWires.remove(wire);
                prevWire.previousWires.add(this);
            });
            this.line.to = wire.line.to;
            if (wire.nextInput != null) {
                this.nextInput = wire.nextInput;
                this.nextInput.connections.remove(wire);
                this.nextInput.removed(wire);
                wire.nextInput = null;
                this.nextInput.connections.add(this);
                this.nextInput.connect(this);
            }
        }
        this.update(editor.space.code);
        wire.remove();
        editor.rootWidgets.remove(wire);
    }

    public static boolean sameDirection(Vec u, Vec v, Vec w, Vec z) {
        double d1x = v.x() - u.x();
        double d1y = v.y() - u.y();
        double d2x = z.x() - w.x();
        double d2y = z.y() - w.y();

        boolean d1IsZero = (d1x == 0 && d1y == 0);
        boolean d2IsZero = (d2x == 0 && d2y == 0);
        if (d1IsZero || d2IsZero) {
            return true;
        }

        double crossProduct = d1x * d2y - d1y * d2x;
        double dotProduct = d1x * d2x + d1y * d2y;
        return crossProduct == 0 && dotProduct > 0;
    }
}
