package de.blazemcworld.fireflow.code.node.impl;

import de.blazemcworld.fireflow.code.node.Node;
import de.blazemcworld.fireflow.code.type.PlayerType;
import de.blazemcworld.fireflow.code.type.SignalType;
import de.blazemcworld.fireflow.code.type.StringType;
import net.minestom.server.entity.Player;

public class SendMessageNode extends Node {

    public SendMessageNode() {
        super("send_message");

        Input<Void> signal = new Input<>("signal", SignalType.INSTANCE);
        Input<Player> player = new Input<>("player", PlayerType.INSTANCE);
        Input<String> message = new Input<>("message", StringType.INSTANCE);

        Output<Void> next = new Output<>("next", SignalType.INSTANCE);

        signal.onSignal(() -> {
            player.getValue().sendMessage(message.getValue());
            next.sendSignal();
        });
    }

    @Override
    public Node copy() {
        return new SendMessageNode();
    }
}
