package de.blazemcworld.fireflow.code.node.impl.string;

import de.blazemcworld.fireflow.code.node.Node;
import net.minestom.server.item.Material;
import de.blazemcworld.fireflow.code.type.StringType;
import de.blazemcworld.fireflow.code.type.NumberType;

public class CharacterAtNode extends Node {
    public CharacterAtNode() {
        super("character_at", Material.WHITE_WOOL);

        Input<String> string = new Input<>("string", StringType.INSTANCE);
        Input<Double> index = new Input<>("index", NumberType.INSTANCE);
        Output<String> character = new Output<>("character", StringType.INSTANCE);

        character.valueFrom((ctx -> {
            int i = index.getValue(ctx).intValue();
            String s = string.getValue(ctx);
            if (i < 0 || i >= s.length()) return "";
            return String.valueOf(s.charAt(i));
        }));
    }

    @Override
    public Node copy() {
        return new CharacterAtNode();
    }
}