package de.blazemcworld.fireflow.editor.widget;

import de.blazemcworld.fireflow.editor.Bounds;
import de.blazemcworld.fireflow.editor.CodeEditor;
import de.blazemcworld.fireflow.editor.Widget;
import de.blazemcworld.fireflow.node.Node;
import de.blazemcworld.fireflow.node.NodeInput;
import de.blazemcworld.fireflow.node.NodeOutput;
import de.blazemcworld.fireflow.util.TextWidth;
import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.InstanceContainer;

import java.util.ArrayList;
import java.util.List;

public class NodeWidget implements Widget {

    private final List<ButtonWidget> buttons = new ArrayList<>();
    private final TextWidget title;
    private final RectWidget border;
    private final Bounds bounds;

    public NodeWidget(Vec origin, InstanceContainer inst, Node node) {
        double inputWidth = 0;
        for (NodeInput input : node.inputs) {
            inputWidth = Math.max(inputWidth, TextWidth.calculate(input.getName(), false) / 40);
        }
        double outputWidth = 0;
        for (NodeOutput output : node.outputs) {
            outputWidth = Math.max(outputWidth, TextWidth.calculate(output.getName(), false) / 40);
        }
        double titleWidth = TextWidth.calculate(node.name, false) / 40;
        title = new TextWidget(origin.add(inputWidth * 0.5 - outputWidth * 0.5 + titleWidth * 0.5, 0.3, 0), inst, Component.text(node.name));

        if (titleWidth > inputWidth + outputWidth) {
            double scale = titleWidth / (inputWidth + outputWidth);
            inputWidth *= scale;
            outputWidth *= scale;
        }
        bounds = new Bounds(
                origin.add(inputWidth + 0.2, 0.7, 0),
                origin.add(-outputWidth - 0.2, -Math.max(node.inputs.size(), node.outputs.size()) * 0.3 + 0.2, 0)
        );
        border = new RectWidget(inst, bounds);

        Vec pos = origin.add(inputWidth + 0.1, 0, 0);
        for (NodeInput input : node.inputs) {
            ButtonWidget btn = new ButtonWidget(pos, inst, Component.text(input.getName()).color(input.type.getColor()));
            buttons.add(btn);
            btn.leftClick = (player, editor) -> {
                editor.remove(this);
            };
            pos = pos.add(0, -0.3, 0);
        }
        pos = origin.add(-outputWidth - 0.1, 0, 0);
        for (NodeOutput output : node.outputs) {
            ButtonWidget btn = new ButtonWidget(pos.add(TextWidth.calculate(output.getName(), false) / 40, 0, 0), inst, Component.text(output.getName()).color(output.type.getColor()));
            buttons.add(btn);
            btn.leftClick = (player, editor) -> {
                editor.remove(this);
            };
            pos = pos.add(0, -0.3, 0);
        }
    }

    @Override
    public Widget select(Player player, Vec cursor) {
        for (ButtonWidget btn : buttons) {
            Widget res = btn.select(player, cursor);
            if (res != null) return res;
        }
        return bounds.includes2d(cursor) ? this : null;
    }

    @Override
    public void leftClick(Vec cursor, Player player, CodeEditor editor) {
        editor.remove(this);
    }

    @Override
    public void remove() {
        for (ButtonWidget button : buttons) {
            button.remove();
        }
        title.remove();
        border.remove();
    }
}
