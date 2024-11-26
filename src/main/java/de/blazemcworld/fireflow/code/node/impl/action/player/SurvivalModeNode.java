package de.blazemcworld.fireflow.code.node.impl.action.player;

import de.blazemcworld.fireflow.code.node.Node;
import de.blazemcworld.fireflow.code.type.PlayerType;
import de.blazemcworld.fireflow.code.type.SignalType;
import de.blazemcworld.fireflow.code.value.PlayerValue;
import net.minestom.server.entity.GameMode;
import net.minestom.server.item.Material;

public class SurvivalModeNode extends Node {
    
    public SurvivalModeNode() {
        super("survival_mode", Material.APPLE);

        Input<Void> signal = new Input<>("signal", SignalType.INSTANCE);
        Input<PlayerValue> player = new Input<>("player", PlayerType.INSTANCE);
        Output<Void> next = new Output<>("next", SignalType.INSTANCE);
        
        signal.onSignal((ctx) -> {
            PlayerValue p = player.getValue(ctx);
            if (p.available(ctx)) p.get(ctx).setGameMode(GameMode.SURVIVAL);
            ctx.sendSignal(next);
        });
    }

    @Override
    public Node copy() {
        return new SurvivalModeNode();
    }

}
