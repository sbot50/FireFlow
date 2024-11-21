package de.blazemcworld.fireflow.command;

import de.blazemcworld.fireflow.space.Space;
import de.blazemcworld.fireflow.space.SpaceManager;
import de.blazemcworld.fireflow.util.Translations;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.Command;
import net.minestom.server.entity.Player;

public class ReloadCommand extends Command {

    public ReloadCommand() {
        super("reload");

        setDefaultExecutor((sender, ctx) -> {
            if (sender instanceof Player player) {
                Space space = SpaceManager.getSpaceForPlayer(player);
                if (space == null) {
                    sender.sendMessage(Component.text(Translations.get("error.needs.space")).color(NamedTextColor.RED));
                    return;
                }

                space.reload("regular");
                sender.sendMessage(Component.text(Translations.get("success.reload")).color(NamedTextColor.GREEN));
            } else {
                sender.sendMessage(Component.text(Translations.get("error.needs.player")).color(NamedTextColor.RED));
            }
        });
    }
}
