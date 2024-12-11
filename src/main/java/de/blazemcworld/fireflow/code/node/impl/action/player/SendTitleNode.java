package de.blazemcworld.fireflow.code.node.impl.action.player;

import de.blazemcworld.fireflow.code.node.Node;
import de.blazemcworld.fireflow.code.type.NumberType;
import de.blazemcworld.fireflow.code.type.PlayerType;
import de.blazemcworld.fireflow.code.type.SignalType;
import de.blazemcworld.fireflow.code.type.TextType;
import de.blazemcworld.fireflow.code.value.PlayerValue;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.TitlePart;
import net.minestom.server.entity.Player;
import net.minestom.server.item.Material;

import java.time.Duration;

public class SendTitleNode extends Node {
    public SendTitleNode() {
        super("send_title", Material.DARK_OAK_SIGN);
        Input<Void> signal = new Input<>("signal", SignalType.INSTANCE);
        Input<PlayerValue> player = new Input<>("player", PlayerType.INSTANCE);
        Input<Component> title = new Input<>("title", TextType.INSTANCE);
        Input<Component> subtitle = new Input<>("subtitle", TextType.INSTANCE);
        Input<Double> fade_in = new Input<>("fade_in", NumberType.INSTANCE);
        Input<Double> stay_number = new Input<>("stay", NumberType.INSTANCE);
        Input<Double> fade_out = new Input<>("fade_out", NumberType.INSTANCE);
        Output<Void> next = new Output<>("next", SignalType.INSTANCE);
        signal.onSignal((ctx) -> {
            PlayerValue pv = player.getValue(ctx);
            if (pv.available(ctx)) {
                Player p = pv.get(ctx);
                p.sendTitlePart(TitlePart.TITLE, title.getValue(ctx));
                p.sendTitlePart(TitlePart.SUBTITLE, subtitle.getValue(ctx));
                p.sendTitlePart(TitlePart.TIMES, Title.Times.times(
                        Duration.ofSeconds(fade_in.getValue(ctx).intValue() / 20),
                        Duration.ofSeconds(stay_number.getValue(ctx).intValue() / 20),
                        Duration.ofSeconds(fade_out.getValue(ctx).intValue() / 20)
                ));
            }
            ctx.sendSignal(next);
        });
    }

    @Override
    public Node copy() {
        return new SendTitleNode();
    }
}
