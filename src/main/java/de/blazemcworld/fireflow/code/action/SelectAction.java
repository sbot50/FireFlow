package de.blazemcworld.fireflow.code.action;

import de.blazemcworld.fireflow.code.CodeEditor;
import de.blazemcworld.fireflow.code.Interaction;
import de.blazemcworld.fireflow.code.widget.RectElement;
import de.blazemcworld.fireflow.code.widget.Widget;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;

import java.util.List;

public class SelectAction implements Action {
    final RectElement box;

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
        List<Widget> widgets = i.editor().getAllWidgetsBetween(i, box.pos, i.pos());
        if (i.type() == Interaction.Type.LEFT_CLICK || widgets.isEmpty()) i.editor().stopAction(i.player());
        else if (i.type() == Interaction.Type.RIGHT_CLICK) {
            i.editor().stopAction(i.player());
            i.editor().setAction(i.player(), new DragSelectionAction(widgets, i.pos(), i.editor(), i.player()));
        } else if (i.type() == Interaction.Type.SWAP_HANDS) {
            i.editor().stopAction(i.player());
            i.editor().setAction(i.player(), new CopySelectionAction(widgets, i.pos(), i.editor()));
        }
    }

    @Override
    public void stop(CodeEditor editor, Player player) {
        box.remove();
    }
}
