package de.blazemcworld.fireflow.command;

import de.blazemcworld.fireflow.space.Lobby;
import de.blazemcworld.fireflow.util.Transfer;
import de.blazemcworld.fireflow.util.Translations;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.Command;
import net.minestom.server.entity.Player;

public class LobbyCommand extends Command {

    public LobbyCommand() {
        super("lobby", "spawn");

        setDefaultExecutor((sender, ctx) -> {
            if (sender instanceof Player player) {
                if (player.getInstance() == Lobby.instance) {
                    sender.sendMessage(Component.text(Translations.get("error.already.lobby")).color(NamedTextColor.RED));
                    return;
                }

                Transfer.move(player, Lobby.instance);
            } else {
                sender.sendMessage(Component.text(Translations.get("error.needs.player")).color(NamedTextColor.RED));
            }
        });
    }

}
