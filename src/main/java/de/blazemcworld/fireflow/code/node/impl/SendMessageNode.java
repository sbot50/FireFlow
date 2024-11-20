package de.blazemcworld.fireflow.code.node.impl;

import de.blazemcworld.fireflow.code.node.Node;
import de.blazemcworld.fireflow.code.type.PlayerType;
import de.blazemcworld.fireflow.code.type.SignalType;
import de.blazemcworld.fireflow.code.type.TextType;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;

public class SendMessageNode extends Node {

    public SendMessageNode() {
        super("send_message");

        Input<Void> signal = new Input<>("signal", SignalType.INSTANCE);
        Input<Player> player = new Input<>("player", PlayerType.INSTANCE);
        Input<Component> message = new Input<>("message", TextType.INSTANCE);

        Output<Void> next = new Output<>("next", SignalType.INSTANCE);

        signal.onSignal((ctx) -> {
            Player p = player.getValue(ctx);
            if (p != null && p.getInstance() == ctx.evaluator.space.play) {
                player.getValue(ctx).sendMessage(message.getValue(ctx));
            }
            ctx.sendSignal(next);
        });
    }

    @Override
    public Node copy() {
        return new SendMessageNode();
    }
}
