package de.blazemcworld.fireflow.command;

import de.blazemcworld.fireflow.code.VariableStore;
import de.blazemcworld.fireflow.space.Space;
import de.blazemcworld.fireflow.space.SpaceManager;
import de.blazemcworld.fireflow.util.Translations;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentString;
import net.minestom.server.entity.Player;

import java.util.Set;
import java.util.function.Predicate;

public class VariablesCommand extends Command {

    public VariablesCommand() {
        super("variables", "vars");

        setDefaultExecutor((sender, ctx) -> {
            listVariables(sender, null);
        });

        addSyntax((sender, ctx) -> {
            listVariables(sender, ctx.get("filter"));
        },  new ArgumentString("filter"));
    }

    private void listVariables(CommandSender sender, String filter) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text(Translations.get("error.needs.player")).color(NamedTextColor.RED));
            return;
        }

        Space space = SpaceManager.getSpaceForPlayer(player);
        if (space == null) {
            sender.sendMessage(Component.text(Translations.get("error.needs.space")).color(NamedTextColor.RED));
            return;
        }

        if (!space.isOwnerOrContributor(player)) {
            sender.sendMessage(Component.text(Translations.get("error.needs.permission")).color(NamedTextColor.RED));
            return;
        }

        Predicate<String> p = filter == null ? s -> true : s -> s.contains(filter);
        Set<VariableStore.VarEntry> session = space.evaluator.sessionVariables.iterator(p, 50);

        for (VariableStore.VarEntry entry : session) {
            sender.sendMessage(Component.text(entry.name()).color(entry.type().color)
                    .append(Component.text(": ").color(NamedTextColor.GRAY))
                    .append(Component.text(entry.type().stringify(entry.value())).color(NamedTextColor.WHITE))
            );
        }

        int remaining = 50 - session.size();
        if (remaining <= 0) return;

        Set<VariableStore.VarEntry> saved = space.savedVariables.iterator(p, remaining);

        for (VariableStore.VarEntry entry : saved) {
            sender.sendMessage(Component.text(entry.name()).color(entry.type().color)
                    .append(Component.text(": ").color(NamedTextColor.GRAY))
                    .append(Component.text(entry.type().stringify(entry.value())).color(NamedTextColor.WHITE))
            );
        }
    }
}
