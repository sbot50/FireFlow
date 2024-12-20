package de.blazemcworld.fireflow.code.action;

import de.blazemcworld.fireflow.code.CodeEditor;
import de.blazemcworld.fireflow.code.Interaction;
import de.blazemcworld.fireflow.code.widget.WireWidget;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;

public class DragWireAction implements Action {
    private final WireWidget wire;

    public DragWireAction(WireWidget wire, CodeEditor editor, Player player) {
        this.wire = wire;
        wire.lockWire(editor, player);
    }

    @Override
    public void tick(Vec cursor, CodeEditor editor, Player player) {
        cursor = cursor.mul(8).apply(Vec.Operator.CEIL).div(8).withZ(15.999);
        moveWire(wire, cursor, editor, null);
        wire.update(editor.space.code);
    }

    private void moveWire(WireWidget wire, Vec cursor, CodeEditor editor, WireWidget avoid) {
        if (wire.line.from.y() != wire.line.to.y()) {
            wire.line.from = wire.line.from.withX(cursor.x());
            wire.line.to = wire.line.to.withX(cursor.x());
            for (WireWidget wireWidget : wire.previousWires) {
                if (wireWidget == avoid) continue;
                if (wireWidget.line.from.y() != wireWidget.line.to.y())
                    moveWire(wireWidget, wire.line.from, editor, wire);
                else
                    wireWidget.line.to = wireWidget.line.to.withX(cursor.x());
            }
            for (WireWidget wireWidget : wire.nextWires) {
                if (wireWidget == avoid) continue;
                if (wireWidget.line.from.y() != wireWidget.line.to.y())
                    moveWire(wireWidget, wire.line.to, editor, wire);
                else
                    wireWidget.line.from = wireWidget.line.from.withX(cursor.x());
            }
        } else {
            wire.line.from = wire.line.from.withY(cursor.y());
            wire.line.to = wire.line.to.withY(cursor.y());
            for (WireWidget wireWidget : wire.previousWires) {
                if (wireWidget == avoid) continue;
                if (wireWidget.line.from.x() != wireWidget.line.to.x())
                    moveWire(wireWidget, wire.line.from, editor, wire);
                else
                    wireWidget.line.to = wireWidget.line.to.withY(cursor.y());
            }
            for (WireWidget wireWidget : wire.nextWires) {
                if (wireWidget == avoid) continue;
                if (wireWidget.line.from.x() != wireWidget.line.to.x())
                    moveWire(wireWidget, wire.line.to, editor, wire);
                else
                    wireWidget.line.from = wireWidget.line.from.withY(cursor.y());
            }
        }
        wire.update(editor.space.code);
        wire.previousWires.forEach(wireWidget -> wireWidget.update(editor.space.code));
        wire.nextWires.forEach(wireWidget -> wireWidget.update(editor.space.code));
    }

    @Override
    public void interact(Interaction i) {
        if (i.type() == Interaction.Type.RIGHT_CLICK) i.editor().stopAction(i.player());
    }

    @Override
    public void stop(CodeEditor editor, Player player) {
        wire.unlockWire(editor, player);
        wire.cleanup(editor);
    }
}
