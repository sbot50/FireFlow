package de.blazemcworld.fireflow.command;

import java.io.IOException;
import java.util.UUID;

import de.blazemcworld.fireflow.space.Space;
import de.blazemcworld.fireflow.space.SpaceManager;
import de.blazemcworld.fireflow.util.Translations;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentLiteral;
import net.minestom.server.command.builder.arguments.ArgumentString;
import net.minestom.server.entity.Player;
import net.minestom.server.utils.mojang.MojangUtils;

public class ContributorCommand extends Command {
    
    public ContributorCommand() {
        super("contributor");

        setDefaultExecutor((sender, ctx) -> {
            run(sender, "list", null);
        });

        addSyntax((sender, ctx) -> {
            run(sender, "list", null);
        }, new ArgumentLiteral("list"));

        addSyntax((sender, ctx) -> {
            run(sender, "add", ctx.get("name"));
        }, new ArgumentLiteral("add"), new ArgumentString("name"));

        addSyntax((sender, ctx) -> {
            run(sender, "remove", ctx.get("name"));
        }, new ArgumentLiteral("remove"), new ArgumentString("name"));
    }

    private void run(CommandSender sender, String action, String other) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text(Translations.get("error.needs.player")).color(NamedTextColor.RED));
            return;
        }
        Space space = SpaceManager.getSpaceForPlayer(player);
        if (space == null) {
            sender.sendMessage(Component.text(Translations.get("error.needs.space")).color(NamedTextColor.RED));
            return;
        }

        if (!space.info.owner.equals(player.getUuid())) {
            sender.sendMessage(Component.text(Translations.get("error.needs.owner")).color(NamedTextColor.RED));
            return;
        }

        new Thread(() -> {
                if (action.equals("list")) {
                    if (space.info.contributors.isEmpty()) {
                        sender.sendMessage(Component.text(Translations.get("error.empty.contributors")).color(NamedTextColor.GRAY));
                        return;
                    }
                    sender.sendMessage(Component.text(Translations.get("success.contributors.list")).color(NamedTextColor.YELLOW));
                    for (UUID contributor : space.info.contributors) {
                        String name = "Internal Error";
                        try {
                            name = MojangUtils.getUsername(contributor);
                        } catch (IOException ignored) {
                        }
                        sender.sendMessage(Component.text(name).color(NamedTextColor.GOLD)
                            .append(Component.text(" (" + contributor + ")").color(NamedTextColor.GRAY))
                        );
                    }
                    return;
                }

                if (action.equals("add")) {
                    UUID contributor = null;
                    try {
                        contributor = MojangUtils.getUUID(other);
                    } catch (IOException ignored) {
                    }
                    if (contributor == null) {
                        sender.sendMessage(Component.text(Translations.get("error.invalid.player")).color(NamedTextColor.RED));
                        return;
                    }
                    if (space.info.contributors.contains(contributor)) {
                        sender.sendMessage(Component.text(Translations.get("error.already.contributor")).color(NamedTextColor.RED));
                        return;
                    }
                    space.info.contributors.add(contributor);
                    sender.sendMessage(Component.text(Translations.get("success.contributors.added")).color(NamedTextColor.GREEN));
                    return;
                }

                if (action.equals("remove")) {
                    UUID contributor = null;
                    try {
                        contributor = MojangUtils.getUUID(other);
                    } catch (IOException ignored) {
                    }
                    if (contributor == null) {
                        sender.sendMessage(Component.text(Translations.get("error.invalid.player")).color(NamedTextColor.RED));
                        return;
                    }
                    if (!space.info.contributors.contains(contributor)) {
                        sender.sendMessage(Component.text(Translations.get("error.not.contributor")).color(NamedTextColor.RED));
                        return;
                    }
                    space.info.contributors.remove(contributor);
                    sender.sendMessage(Component.text(Translations.get("success.contributors.removed")).color(NamedTextColor.GREEN));
                    return;
                }
        }).start();
    }
}
