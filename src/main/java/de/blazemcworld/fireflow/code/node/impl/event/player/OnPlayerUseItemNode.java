package de.blazemcworld.fireflow.code.node.impl.event.player;

import de.blazemcworld.fireflow.code.CodeEvaluator;
import de.blazemcworld.fireflow.code.CodeThread;
import de.blazemcworld.fireflow.code.node.Node;
import de.blazemcworld.fireflow.code.type.ItemType;
import de.blazemcworld.fireflow.code.type.PlayerType;
import de.blazemcworld.fireflow.code.type.SignalType;
import de.blazemcworld.fireflow.code.value.PlayerValue;
import net.minestom.server.event.player.PlayerUseItemEvent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

public class OnPlayerUseItemNode extends Node {

    Output<Void> signal;
    Output<PlayerValue> player;
    Output<ItemStack> item;

    public OnPlayerUseItemNode() {
        super("on_player_use_item", Material.IRON_HOE);

        signal = new Output<>("signal", SignalType.INSTANCE);
        player = new Output<>("player", PlayerType.INSTANCE);
        item = new Output<>("item", ItemType.INSTANCE);

        player.valueFromThread();
        item.valueFromThread();
    }

    @Override
    public void init(CodeEvaluator evaluator) {
        evaluator.events.addListener(PlayerUseItemEvent.class, event -> {
            CodeThread thread = evaluator.newCodeThread();
            thread.setThreadValue(player, new PlayerValue(event.getPlayer()));
            thread.setThreadValue(item, event.getItemStack());
            thread.sendSignal(signal);
            thread.clearQueue();
        });
    }

    @Override
    public Node copy() {
        return new OnPlayerUseItemNode();
    }
}
