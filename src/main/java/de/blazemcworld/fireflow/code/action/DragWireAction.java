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
        if (wire.line.from.y() != wire.line.to.y()) {
            wire.line.from = wire.line.from.withX(cursor.x());
            wire.line.to = wire.line.to.withX(cursor.x());
        } else {
            wire.line.from = wire.line.from.withY(cursor.y());
            wire.line.to = wire.line.to.withY(cursor.y());
        }
        moveWire(wire, editor, null);
        wire.update(editor.space.code);
    }

    private void moveWire(WireWidget wire, CodeEditor editor, WireWidget avoid) {
        if (wire.line.from.y() != wire.line.to.y()) {
            for (WireWidget wireWidget : wire.previousWires) {
                if (wireWidget == avoid) continue;
                wireWidget.line.to = wire.line.from;
                if (wireWidget.line.from.y() != wireWidget.line.to.y()) wireWidget.line.from = wireWidget.line.from.withX(wireWidget.line.to.x());
                else wireWidget.line.from = wireWidget.line.from.withY(wireWidget.line.to.y());
                moveWire(wireWidget, editor, wire);
            }
            for (WireWidget wireWidget : wire.nextWires) {
                if (wireWidget == avoid) continue;
                wireWidget.line.from = wire.line.to;
                if (wireWidget.line.from.y() != wireWidget.line.to.y()) wireWidget.line.to = wireWidget.line.to.withX(wireWidget.line.from.x());
                else wireWidget.line.to = wireWidget.line.to.withY(wireWidget.line.from.y());
                moveWire(wireWidget, editor, wire);
            }
        } else {
            for (WireWidget wireWidget : wire.previousWires) {
                if (wireWidget == avoid) continue;
                wireWidget.line.to = wire.line.from;
                if (wireWidget.line.from.x() != wireWidget.line.to.x()) wireWidget.line.from = wireWidget.line.from.withY(wireWidget.line.to.y());
                else wireWidget.line.from = wireWidget.line.from.withX(wireWidget.line.to.x());
                moveWire(wireWidget, editor, wire);
            }
            for (WireWidget wireWidget : wire.nextWires) {
                if (wireWidget == avoid) continue;
                wireWidget.line.from = wire.line.to;
                if (wireWidget.line.from.x() != wireWidget.line.to.x()) wireWidget.line.to = wireWidget.line.to.withY(wireWidget.line.from.y());
                else wireWidget.line.to = wireWidget.line.to.withX(wireWidget.line.from.x());
                moveWire(wireWidget, editor, wire);
            }
        }
        wire.update(editor.space.code);
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
