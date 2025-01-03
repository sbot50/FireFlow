package de.blazemcworld.fireflow.code.node.impl.action.player;

import de.blazemcworld.fireflow.code.node.Node;
import de.blazemcworld.fireflow.code.type.PlayerType;
import de.blazemcworld.fireflow.code.type.SignalType;
import de.blazemcworld.fireflow.code.type.StringType;
import de.blazemcworld.fireflow.code.value.PlayerValue;
import net.minestom.server.entity.GameMode;
import net.minestom.server.item.Material;

public class SetGamemodeNode extends Node {
    public SetGamemodeNode() {
        super("set_gamemode", Material.DIAMOND);
        Input<Void> signal = new Input<>("signal", SignalType.INSTANCE);

        Input<PlayerValue> player = new Input<>("player", PlayerType.INSTANCE);
        Input<String> gamemode = new Input<>("gamemode", StringType.INSTANCE)
                .options("creative", "survival", "adventure", "spectator");
        Output<Void> next = new Output<>("next", SignalType.INSTANCE);

        signal.onSignal((ctx) -> {
            PlayerValue p = player.getValue(ctx);
            if (p.available(ctx)) {
                GameMode mode = switch (gamemode.getValue(ctx)) {
                    case "creative" -> GameMode.CREATIVE;
                    case "survival" -> GameMode.SURVIVAL;
                    case "adventure" -> GameMode.ADVENTURE;
                    case "spectator" -> GameMode.SPECTATOR;
                    default -> null;
                };
                if (mode != null) p.get(ctx).setGameMode(mode);
            }
            ctx.sendSignal(next);
        });
    }

    @Override
    public Node copy() {
        return new SetGamemodeNode();
    }
}