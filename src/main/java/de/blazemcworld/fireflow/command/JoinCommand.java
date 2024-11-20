package de.blazemcworld.fireflow.command;

import de.blazemcworld.fireflow.space.SpaceInfo;
import de.blazemcworld.fireflow.space.SpaceManager;
import de.blazemcworld.fireflow.util.Transfer;
import de.blazemcworld.fireflow.util.Translations;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.number.ArgumentInteger;
import net.minestom.server.entity.Player;

public class JoinCommand extends Command {
    
    public JoinCommand() {
        super("join");

        addSyntax((sender, ctx) -> {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(Component.text(Translations.get("error.needs.player")).color(NamedTextColor.RED));
                return;
            }
            SpaceInfo info = SpaceManager.info.get(ctx.get("id"));
            if (info == null) {
                sender.sendMessage(Component.text(Translations.get("error.invalid.space")).color(NamedTextColor.RED));
                return;
            }

            Transfer.move(player, SpaceManager.getOrLoadSpace(info).play);
        }, new ArgumentInteger("id"));
    }

}
