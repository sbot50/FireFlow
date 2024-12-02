package de.blazemcworld.fireflow.code.action;

import de.blazemcworld.fireflow.code.CodeEditor;
import de.blazemcworld.fireflow.code.Interaction;
import de.blazemcworld.fireflow.code.type.SignalType;
import de.blazemcworld.fireflow.code.type.WireType;
import de.blazemcworld.fireflow.code.widget.NodeIOWidget;
import de.blazemcworld.fireflow.code.widget.Widget;
import de.blazemcworld.fireflow.code.widget.WireWidget;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class WireAction implements Action {
    private NodeIOWidget input;
    private NodeIOWidget output;
    private WireWidget inputWire;
    private WireWidget outputWire;
    private Vec startPos;
    private List<WireWidget> wires = new ArrayList<>();
    private List<List<WireWidget>> permanentWires = new ArrayList<>();
    private WireWidget startWire;
    private WireWidget endWire;

    public WireAction(NodeIOWidget io) {
        if (!io.isInput()) {
            output = io;
            startPos = io.getPos().sub(output.getSize().sub(-1 / 4f, 1 / 8f, 0));
            startWire = new WireWidget(io.getPos().sub(output.getSize().sub(1 / 8f, 1 / 8f, 0)), output.type(), startPos);
        }
//        else {
//            input = io;
//            startPos = io.getPos().sub(-1 / 4f, 1 / 8f, 0);
//            startWire = new WireWidget(io.getPos().sub(1 / 8f - 1 / 32f, 1 / 8f, 0), input.type(), startPos);
//        }
    }

    public WireAction(WireWidget wire, Vec cursor) {
        inputWire = wire;
        startPos = cursor;
    }

    @Override
    public void tick(Vec cursor, CodeEditor editor, Player player) {
        if (startWire != null) startWire.update(editor.space.code);
        Vec endPos = cursor;
        NodeIOWidget hover = null;
        WireType<?> type = (output != null) ? output.type() : inputWire.type();
        for (Widget widget : new HashSet<>(editor.rootWidgets)) {
            if (widget.getWidget(cursor) instanceof NodeIOWidget nodeIOWidget) {
                if (nodeIOWidget.type() != type) continue;
                if (!nodeIOWidget.isInput()) continue;
                endPos = nodeIOWidget.getPos().sub(-1 / 4f, 1 / 8f, 0);
                hover = nodeIOWidget;
                break;
            }
        }
        List<Vec> positions = editor.pathfinder.findPath(startPos, endPos, 5, 1000);
        if (wires.isEmpty()) {
            WireWidget lastWire = new WireWidget(startPos, type, startPos);
            lastWire.update(editor.space.code);
            wires.add(lastWire);
        }
        int index = 0;
        for (Vec position : positions) {
            WireWidget lastWire = wires.get(index);
            if (index == wires.size() - 1) {
                WireWidget wire = new WireWidget(lastWire, type, position);
                wire.update(editor.space.code);
                wires.add(wire);
            } else {
                wires.get(index + 1).line.from = lastWire.line.to;
                wires.get(index + 1).line.to = position;
                wires.get(index + 1).update(editor.space.code);
            }
            index++;
        }

        for (int i = index + 1; i < wires.size(); i++) {
            wires.get(i).remove();
        }
        wires = wires.subList(0, index + 1);

        if (hover != null) {
            WireWidget lastWire = wires.get(index);
            if (endWire == null) endWire = new WireWidget(lastWire, hover.type(), hover.getPos());
            endWire.line.from = lastWire.line.to;
            endWire.line.to = (hover.isInput()) ? hover.getPos().sub(1 / 8f, 1 / 8f, 0) : hover.getPos().sub(hover.getSize().sub(1 / 8f, 1 / 8f, 0));
            endWire.update(editor.space.code);
        } else if (endWire != null) {
            endWire.remove();
            endWire = null;
        }
    }

    @Override
    public void interact(Interaction i) {
        if (i.type() == Interaction.Type.RIGHT_CLICK) {
            for (Widget widget : new HashSet<>(i.editor().rootWidgets)) {
                if (widget.getWidget(i.pos()) instanceof NodeIOWidget nodeIOWidget) {
                    WireType<?> type = (output != null) ? output.type() : inputWire.type();
                    if (endWire == null) return;
                    if (nodeIOWidget.type() != type) return;
                    if (!nodeIOWidget.isInput()) return;
                    if (!nodeIOWidget.connections.isEmpty() && type != SignalType.INSTANCE) return;
                    input = nodeIOWidget;

                    for (int j = 0; j < permanentWires.size(); j++) {
                        if (j == 0) continue;
                        permanentWires.get(j - 1).getLast().connectNext(permanentWires.get(j).getFirst());
                    }
                    if (!permanentWires.isEmpty()) permanentWires.getLast().getLast().connectNext(wires.getFirst());

                    WireWidget firstWire = wires.getFirst();
                    if (!permanentWires.isEmpty()) firstWire = permanentWires.getFirst().getFirst();

                    if (output != null) {
                        startWire.connectNext(firstWire);
                        startWire.connectPrevious(output);
                        i.editor().rootWidgets.add(startWire);
                    }
                    else {
                        List<WireWidget> wires = inputWire.splitWire(i.editor(), startPos);
                        wires.getFirst().connectNext(firstWire);
                    }

                    endWire.connectNext(input);
                    i.editor().rootWidgets.add(endWire);

                    for (List<WireWidget> list : permanentWires) {
                        for (WireWidget wire : list) {
                            i.editor().rootWidgets.add(wire);
                        }
                    }

                    for (WireWidget wire : wires) {
                        i.editor().rootWidgets.add(wire);
                    }

                    if (output != null) output.connect(startWire);
                    input.connect(endWire);
                    endWire.cleanup(i.editor());
                    permanentWires = null;
                    wires = null;
                    startWire = null;
                    endWire = null;
                    i.editor().stopAction(i.player());
                    return;
                } else if (widget.getWidget(i.pos()) instanceof WireWidget wireWidget) {
                    WireType<?> type = output.type();
                    if (wireWidget.type() != type) return;
                    if (type != SignalType.INSTANCE) return;

                    for (int j = 0; j < permanentWires.size(); j++) {
                        if (j == 0) continue;
                        permanentWires.get(j - 1).getLast().connectNext(permanentWires.get(j).getFirst());
                    }
                    if (!permanentWires.isEmpty()) permanentWires.getLast().getLast().connectNext(wires.getFirst());

                    for (List<WireWidget> list : permanentWires) {
                        for (WireWidget wire : list) {
                            i.editor().rootWidgets.add(wire);
                        }
                    }

                    for (WireWidget wire : wires) {
                        i.editor().rootWidgets.add(wire);
                    }

                    List<WireWidget> wires = wireWidget.splitWire(i.editor(), i.pos());
                    wires.getLast().connectPrevious(this.wires.getLast());

                    WireWidget firstWire = wires.getFirst();
                    if (!permanentWires.isEmpty()) firstWire = permanentWires.getFirst().getFirst();
                    startWire.connectNext(firstWire);
                    startWire.connectPrevious(output);
                    i.editor().rootWidgets.add(startWire);
                    output.connect(startWire);
                    startWire.cleanup(i.editor());
                    permanentWires = null;
                    this.wires = null;
                    startWire = null;
                    endWire = null;
                    i.editor().stopAction(i.player());
                    return;
                }
            }
            permanentWires.add(new ArrayList<>(wires));
            startPos = wires.getLast().line.to;
            wires = new ArrayList<>();
        } else if (i.type() == Interaction.Type.LEFT_CLICK) {
            if (permanentWires.isEmpty()) {
                i.editor().stopAction(i.player());
                return;
            }
            for (WireWidget wire : wires) {
                wire.remove();
            }
            wires = permanentWires.removeLast();
            if (permanentWires.isEmpty())
                startPos = input != null ? input.getPos().sub(-1 / 4f, 1 / 8f, 0) : output.getPos().sub(output.getSize().sub(-1 / 4f, 1 / 8f, 0));
            else startPos = permanentWires.getLast().getLast().line.to;
        }
    }

    @Override
    public void stop(CodeEditor editor, Player player) {
        if (permanentWires != null) {
            for (List<WireWidget> list : permanentWires) {
                for (WireWidget wire : list) {
                    wire.remove();
                }
            }
        }
        if (wires != null) {
            for (WireWidget wire : wires) {
                wire.remove();
            }
        }
        if (startWire != null) startWire.remove();
        if (endWire != null) endWire.remove();
        if (permanentWires != null && !permanentWires.isEmpty()) permanentWires.clear();
        if (wires != null && !wires.isEmpty()) wires.clear();
    }
}
