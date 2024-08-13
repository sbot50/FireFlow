package de.blazemcworld.fireflow.node.impl.extraction.player;

import de.blazemcworld.fireflow.node.ExtractionNode;
import de.blazemcworld.fireflow.node.annotation.FlowValueInput;
import de.blazemcworld.fireflow.node.annotation.FlowValueOutput;
import de.blazemcworld.fireflow.value.PlayerValue;
import de.blazemcworld.fireflow.value.TextValue;

public class PlayerUUIDNode extends ExtractionNode {
    public PlayerUUIDNode() {
        super("Player UUID", PlayerValue.INSTANCE, TextValue.INSTANCE);

        loadJava(PlayerUUIDNode.class);
    }

    @FlowValueOutput("")
    private static String output() {
        return input().uuid().toString();
    }

    @FlowValueInput("")
    private static PlayerValue.Reference input() {
        throw new IllegalStateException();
    }
}
