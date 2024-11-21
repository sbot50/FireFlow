package de.blazemcworld.fireflow.code.node.impl.event.player;

import de.blazemcworld.fireflow.code.CodeEvaluator;
import de.blazemcworld.fireflow.code.CodeThread;
import de.blazemcworld.fireflow.code.node.Node;
import de.blazemcworld.fireflow.code.type.PlayerType;
import de.blazemcworld.fireflow.code.type.SignalType;
import de.blazemcworld.fireflow.code.type.StringType;
import de.blazemcworld.fireflow.code.value.PlayerValue;
import net.minestom.server.event.player.PlayerChatEvent;

public class OnPlayerChatNode extends Node {
    
    private final Output<Void> signal;
    private final Output<PlayerValue> player;
    private final Output<String> message;

    public OnPlayerChatNode() {
        super("on_player_chat");

        signal = new Output<>("signal", SignalType.INSTANCE);
        player = new Output<>("player", PlayerType.INSTANCE);
        message = new Output<>("message", StringType.INSTANCE);
        player.valueFromThread();
        message.valueFromThread();
    }

    @Override
    public void init(CodeEvaluator evaluator) {
        evaluator.events.addListener(PlayerChatEvent.class, event -> {
            CodeThread thread = evaluator.newCodeThread();
            thread.setThreadValue(player, new PlayerValue(event.getPlayer()));
            thread.setThreadValue(message, event.getMessage());
            thread.sendSignal(signal);
            thread.clearQueue();
        });
    }

    @Override
    public Node copy() {
        return new OnPlayerChatNode();
    }

}
