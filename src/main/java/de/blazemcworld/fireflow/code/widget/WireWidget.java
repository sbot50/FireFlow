package de.blazemcworld.fireflow.code.widget;

import de.blazemcworld.fireflow.code.CodeEditor;
import de.blazemcworld.fireflow.code.Interaction;
import de.blazemcworld.fireflow.code.action.DragWireAction;
import de.blazemcworld.fireflow.code.action.WireAction;
import de.blazemcworld.fireflow.code.node.Node;
import de.blazemcworld.fireflow.code.type.SignalType;
import de.blazemcworld.fireflow.code.type.WireType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.InstanceContainer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WireWidget implements Widget {

    public NodeIOWidget previousOutput;
    public List<WireWidget> previousWires = new ArrayList<>();
    public final LineElement line = new LineElement();
    private final List<TextWidget> arrows = new ArrayList<>();
    public List<WireWidget> nextWires = new ArrayList<>();
    public NodeIOWidget nextInput;
    private final WireType<?> type;

    public WireWidget(NodeIOWidget previousOutput, WireType<?> type, Vec cursor, InstanceContainer inst) {
        if (!previousOutput.isInput()) this.previousOutput = previousOutput;
        else this.nextInput = previousOutput;
        if (previousOutput.isInput()) line.from = previousOutput.getPos().sub(1/8f-1/32f, 1/8f, 0);
        else line.from = previousOutput.getPos().sub(previousOutput.getSize().sub(1/8f, 1/8f, 0));
        line.to = cursor;
        line.color(previousOutput.color());
        this.type = type;
        doArrows(inst);
    }

    public WireWidget(Vec start, WireType<?> type, Vec end, InstanceContainer inst) {
        line.from = start;
        line.to = end;
        line.color(type.color);
        this.type = type;
        doArrows(inst);
    }

    public WireWidget(WireWidget previousWire, WireType<?> type, Vec cursor, InstanceContainer inst) {
        this.previousWires.add(previousWire);
        previousWire.nextWires.add(this);
        line.from = previousWire.line.to;
        line.to = cursor;
        line.color(previousWire.line.color());
        this.type = type;
        doArrows(inst);
    }

    public WireWidget(WireWidget wire, WireType<?> type, Vec cursor, boolean isNext, InstanceContainer inst) {
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
        doArrows(inst);
    }

    public WireWidget(List<WireWidget> previousWires, WireType<?> type, Vec cursor, InstanceContainer inst) {
        this.previousWires.addAll(previousWires);
        line.from = previousWires.getFirst().line.to;
        line.to = cursor;
        line.color(previousWires.getFirst().line.color());
        this.type = type;
        doArrows(inst);
    }

    public WireWidget(WireType<?> type, Vec from, Vec to, InstanceContainer inst) {
        line.from = from;
        line.to = to;
        line.color(type.color);
        this.type = type;
        doArrows(inst);
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
        doArrows(inst);
    }

    private void doArrows(InstanceContainer inst) {
        double lineLength = Math.abs((line.from.x() == line.to.x()) ? (line.from.y() - line.to.y()) : (line.from.x() - line.to.x()));
        if (lineLength < 1) {
            arrows.forEach(TextWidget::remove);
            arrows.clear();
            return;
        }
        boolean horizontal = line.from.x() != line.to.x();
        int arrowCount = Math.max((int) Math.ceil(lineLength / 3), 1);

        for (int i = 0; i < arrowCount; i++) {
            if (arrows.size() <= i) {
                TextWidget arrow = new TextWidget(Component.text(">").color(type.color).decorate(TextDecoration.BOLD));
                arrow.stretch(1.5, 1);
                arrows.add(arrow);
            }
            TextWidget arrow = arrows.get(i);

            Vec pos = new Vec((horizontal) ? (Math.min(line.from.x(), line.to.x()) + (lineLength / (arrowCount + 1)) * (i+1)) : line.from.x() + ((line.from.y() < line.to.y()) ? 0.25/-4.15 : 0.25/1.19), ((!horizontal) ? (Math.min(line.from.y(), line.to.y()) + (lineLength / (arrowCount + 1)) * (i+1)) : line.from.y()) + ((line.from.x() > line.to.x()) ? 0.25/1.98 : 0.25*1.585), 15.999);
            arrow.setPos(pos);

            if (!horizontal) {
                if (line.from.y() > line.to.y()) arrow.setRotation(-90);
                else arrow.setRotation(90);
            } else {
                if (line.from.x() > line.to.x()) arrow.setRotation(0);
                else arrow.setRotation(180);
            }
            arrow.update(inst);
        }

        for (int i = arrowCount; i < arrows.size(); i++) {
            arrows.get(i).remove();
            arrows.remove(i);
            i--;
        }
    }

    @Override
    public void remove() {
        arrows.forEach(TextWidget::remove);
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
            List<NodeIOWidget> inputs = getInputs();
            List<NodeIOWidget> outputs = getOutputs();
            removeConnection(i.editor());
            if (this.type == SignalType.INSTANCE && !outputs.getFirst().connections.isEmpty()) outputs.getFirst().connections.getFirst().cleanup(i.editor());
            else if (!inputs.getFirst().connections.isEmpty()) inputs.getFirst().connections.getFirst().cleanup(i.editor());
            return true;
        } else if (i.type() == Interaction.Type.SWAP_HANDS) {
            if (type != SignalType.INSTANCE) {
                i.editor().setAction(i.player(), new WireAction(this, i.pos(), i.editor(), i.player()));
                return true;
            }
        } else if (i.type() == Interaction.Type.RIGHT_CLICK) {
            if (!previousWires.isEmpty() && !nextWires.isEmpty()) {
                boolean horizontal = line.from.x() == line.to.x();
                for (WireWidget wire : previousWires) {
                    if (horizontal && wire.line.from.x() == wire.line.to.x()) {
                        if (wire.previousWires.isEmpty() || wire.nextWires.isEmpty()) return false;
                    } else if (!horizontal && wire.line.from.y() == wire.line.to.y()) {
                        if (wire.previousWires.isEmpty() || wire.nextWires.isEmpty()) return false;
                    }
                }
                for (WireWidget wire : nextWires) {
                    if (horizontal && wire.line.from.x() == wire.line.to.x()) {
                        if (wire.previousWires.isEmpty() || wire.nextWires.isEmpty()) return false;
                    } else if (!horizontal && wire.line.from.y() == wire.line.to.y()) {
                        if (wire.previousWires.isEmpty() || wire.nextWires.isEmpty()) return false;
                    }
                }
                i.editor().setAction(i.player(), new DragWireAction(this, i.editor(), i.player()));
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
        if (this.previousWires.isEmpty()) w1 = new WireWidget(this.previousOutput, this.type(), pos, editor.space.code);
        else w1 = new WireWidget(this.previousWires, this.type(), pos, editor.space.code);
        editor.rootWidgets.add(w1);
        for (WireWidget wireWidget1 : this.previousWires) {
            wireWidget1.nextWires.remove(this);
            wireWidget1.nextWires.add(w1);
        }
        WireWidget w2 = new WireWidget(w1, this.type(), this.line.to, editor.space.code);
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
            previousOutput.connections.add(w1);
        }
        NodeIOWidget nextInput = this.nextInput;
        if (nextInput != null) {
            w2.setNextInput(nextInput);
            nextInput.connections.remove(this);
            nextInput.connections.add(w2);
        }
        this.arrows.forEach(TextWidget::remove);
        this.arrows.clear();
        this.line.remove();
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
            if (previousWires.size() == 1 && previousWires.getFirst().nextWires.size() == 1 && sameDirection(previousWires.getFirst().line.from, previousWires.getFirst().line.to, line.from, line.to)) {
                combine(previousWires.getFirst(), editor, true);
                continue;
            }
            if (nextWires.size() == 1 && nextWires.getFirst().previousWires.size() == 1 && sameDirection(nextWires.getFirst().line.from, nextWires.getFirst().line.to, line.from, line.to)) {
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
            if (previousWires.size() == 1 && previousWires.getFirst().nextWires.size() == 1 && sameDirection(previousWires.getFirst().line.from, previousWires.getFirst().line.to, line.from, line.to)) {
                combine(previousWires.getFirst(), editor, true);
                continue;
            }
            break;
        }
        previousWires.forEach(wire -> wire.cleanupPrevious(editor));
    }

    private void cleanupNext(CodeEditor editor) {
        while (true) {
            if (nextWires.size() == 1 && nextWires.getFirst().previousWires.size() == 1 && sameDirection(nextWires.getFirst().line.from, nextWires.getFirst().line.to, line.from, line.to)) {
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
                Node.Varargs<?> varargs = wire.nextInput.input.varargsParent;
                if (varargs != null) varargs.ignoreUpdates = true;
                this.nextInput = wire.nextInput;
                this.nextInput.connections.remove(wire);
                this.nextInput.removed(wire);
                wire.nextInput = null;
                this.nextInput.connections.add(this);
                this.nextInput.connect(this);
                if (varargs != null) varargs.ignoreUpdates = false;
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

    public boolean lockWire(CodeEditor editor, Player player) {
        List<Widget> widgets = editor.lockWidgets(new ArrayList<>(getFullWire()), player);
        return widgets.isEmpty();
    }

    public void unlockWire(CodeEditor editor, Player player) {
        editor.unlockWidgets(new ArrayList<>(getFullWire()), player);
    }

    public Set<WireWidget> getFullWire() {
        Set<WireWidget> wires = new HashSet<>();
        wires.add(this);
        wires.addAll(previousWires);
        wires.addAll(nextWires);
        previousWires.forEach(wire -> wires.addAll(wire.getFullPreviousWires(this)));
        nextWires.forEach(wire -> wires.addAll(wire.getFullNextWires(this)));
        return wires;
    }

    private Set<WireWidget> getFullPreviousWires(WireWidget avoid) {
        Set<WireWidget> wires = new HashSet<>(previousWires);
        Set<WireWidget> nextWiresClone = new HashSet<>(nextWires);
        nextWiresClone.remove(avoid);
        wires.addAll(nextWiresClone);
        previousWires.forEach(wire -> wires.addAll(wire.getFullPreviousWires(this)));
        nextWires.forEach(wire -> {
            if (wire == avoid) return;
            wires.addAll(wire.getFullNextWires(this));
        });
        return wires;
    }

    private Set<WireWidget> getFullNextWires(WireWidget avoid) {
        Set<WireWidget> wires = new HashSet<>(nextWires);
        Set<WireWidget> previousWiresClone = new HashSet<>(previousWires);
        previousWiresClone.remove(avoid);
        wires.addAll(previousWiresClone);
        previousWires.forEach(wire -> {
            if (wire == avoid) return;
            wires.addAll(wire.getFullPreviousWires(this));
        });
        nextWires.forEach(wire -> wires.addAll(wire.getFullNextWires(this)));
        return wires;
    }
}
