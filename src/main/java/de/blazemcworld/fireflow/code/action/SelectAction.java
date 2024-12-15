package de.blazemcworld.fireflow.code.action;

import de.blazemcworld.fireflow.code.CodeEditor;
import de.blazemcworld.fireflow.code.Interaction;
import de.blazemcworld.fireflow.code.widget.NodeWidget;
import de.blazemcworld.fireflow.code.widget.RectElement;
import de.blazemcworld.fireflow.code.widget.Widget;
import de.blazemcworld.fireflow.code.widget.WireWidget;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class SelectAction implements Action {
    private final RectElement box;

    public SelectAction(Vec pos) {
        box = new RectElement();
        box.pos = pos;
        box.size = Vec.ZERO;
        box.color(NamedTextColor.AQUA);
    }

    @Override
    public void tick(Vec cursor, CodeEditor editor, Player player) {
        box.size = cursor.sub(box.pos).mul(-1);
        box.update(editor.space.code);
    }

    @Override
    public void interact(Interaction i) {
        if (i.type() == Interaction.Type.LEFT_CLICK) i.editor().stopAction(i.player());
        else if (i.type() == Interaction.Type.RIGHT_CLICK) {
            i.editor().stopAction(i.player());
            i.editor().setAction(i.player(), new DragAndDeleteAction(getAllWidgets(i), i.pos()));
        } else if (i.type() == Interaction.Type.SWAP_HANDS) {
            i.editor().stopAction(i.player());
            i.editor().setAction(i.player(), new CopyAction(getAllWidgets(i)));
        }
    }

    private List<Widget> getAllWidgets(Interaction i) {
        List<Widget> widgets = new ArrayList<>();

        for (Widget w : new HashSet<>(i.editor().rootWidgets)) {
            if (w instanceof NodeWidget nodeWidget) {
                if (isVectorBetween(nodeWidget.getPos(), box.pos, i.pos()) && isVectorBetween(nodeWidget.getPos().sub(nodeWidget.getSize()), box.pos, i.pos()))
                    widgets.add(w);
            } else if (w instanceof WireWidget wire) {
                if (isVectorBetween(wire.line.from, box.pos, i.pos()) && isVectorBetween(wire.line.to, box.pos, i.pos()))
                    widgets.add(w);
            }
        }

        return widgets;
    }

    private static boolean isVectorBetween(Vec v, Vec p1, Vec p2) {
        Vec min = p1.min(p2);
        Vec max = p1.max(p2);

        return min.x() < v.x() && min.y() < v.y()
                && max.x() > v.x() && max.y() > v.y();
    }

    @Override
    public void stop(CodeEditor editor, Player player) {
        box.remove();
    }
}
