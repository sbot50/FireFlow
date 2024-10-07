package de.blazemcworld.fireflow.command;

import de.blazemcworld.fireflow.space.Space;
import de.blazemcworld.fireflow.space.SpaceManager;
import de.blazemcworld.fireflow.util.Transfer;
import de.blazemcworld.fireflow.util.Translations;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.Command;
import net.minestom.server.entity.Player;

public class PlayCommand extends Command {

    public PlayCommand() {
        super("play");

        setDefaultExecutor((sender, ctx) -> {
            if (sender instanceof Player player) {
                Space space = SpaceManager.getSpaceForPlayer(player);
                if (space == null) {
                    sender.sendMessage(Component.text(Translations.get("error.needs.space")).color(NamedTextColor.RED));
                    return;
                }

                if (space.play == player.getInstance()) {
                    sender.sendMessage(Component.text(Translations.get("error.already.playing")).color(NamedTextColor.RED));
                    return;
                }

                Transfer.move(player, space.play);
            } else {
                sender.sendMessage(Component.text(Translations.get("error.needs.player")).color(NamedTextColor.RED));
            }
        });
    }

}
