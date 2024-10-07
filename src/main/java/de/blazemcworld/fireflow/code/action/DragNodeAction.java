package de.blazemcworld.fireflow.code.action;

import de.blazemcworld.fireflow.code.CodeEditor;
import de.blazemcworld.fireflow.code.Interaction;
import de.blazemcworld.fireflow.code.widget.NodeWidget;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;

public class DragNodeAction implements Action {
    private final NodeWidget node;
    private final Vec offset;

    public DragNodeAction(NodeWidget node, Vec offset) {
        this.node = node;
        this.offset = offset;
        node.borderColor(NamedTextColor.AQUA);
    }

    @Override
    public void tick(Vec cursor, CodeEditor editor, Player player) {
        node.setPos(cursor.add(offset));
        node.update(editor.space.code);
    }

    @Override
    public void interact(Interaction i) {
        if (i.type() == Interaction.Type.RIGHT_CLICK) i.editor().stopAction(i.player());
    }

    @Override
    public void stop(CodeEditor editor, Player player) {
        node.borderColor(NamedTextColor.WHITE);
        editor.unlockWidget(node, player);
    }
}
