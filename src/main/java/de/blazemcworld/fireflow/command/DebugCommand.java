package de.blazemcworld.fireflow.command;

import de.blazemcworld.fireflow.code.CodeDebugger;
import de.blazemcworld.fireflow.code.widget.NodeIOWidget;
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

public class DebugCommand extends Command {

    public DebugCommand() {
        super("debug");

        addSyntax((sender, ctx) -> {
            CodeDebugger debugger = getDebugger(sender);
            if (debugger == null) return;

            NodeIOWidget io = debugger.space.editor.selectIOWidget((Player) sender);
            if (io == null || io.isInput()) {
                sender.sendMessage(Component.text(Translations.get("error.needs.output")).color(NamedTextColor.RED));
                return;
            }
            CodeDebugger.Group g = debugger.getOrNewGroup(ctx.get("group"));
            g.outputs.put(io.output, true);
            g.players.put((Player) sender, true);
            sender.sendMessage(Component.text(Translations.get("success.added.debug_point")).color(NamedTextColor.GREEN));
        }, new ArgumentLiteral("add"), new ArgumentString("group"));

        addSyntax((sender, ctx) -> {
            CodeDebugger debugger = getDebugger(sender);
            if (debugger == null) return;

            CodeDebugger.Group g = debugger.getGroup(ctx.get("group"));
            if (g == null) {
                sender.sendMessage(Component.text(Translations.get("error.invalid.debug_group")).color(NamedTextColor.RED));
                return;
            }

            NodeIOWidget io = debugger.space.editor.selectIOWidget((Player) sender);
            if (io == null || io.isInput()) {
                debugger.removeGroup(g.id);
                sender.sendMessage(Component.text(Translations.get("success.removed.debug_group")).color(NamedTextColor.GREEN));
                return;
            }

            g.outputs.remove(io.output);
            if (g.outputs.isEmpty()) debugger.removeGroup(g.id);
            sender.sendMessage(Component.text(Translations.get("success.removed.debug_point")).color(NamedTextColor.GREEN));
        }, new ArgumentLiteral("remove"), new ArgumentString("group"));

        addSyntax((sender, ctx) -> {
            CodeDebugger debugger = getDebugger(sender);
            if (debugger == null) return;

            CodeDebugger.Group g = debugger.getGroup(ctx.get("group"));
            if (g == null) {
                sender.sendMessage(Component.text(Translations.get("error.invalid.debug_group")).color(NamedTextColor.RED));
                return;
            }
            g.players.put((Player) sender, true);
            sender.sendMessage(Component.text(Translations.get("success.watch.debug_group")).color(NamedTextColor.GREEN));
        }, new ArgumentLiteral("watch"), new ArgumentString("group"));

        addSyntax((sender, ctx) -> {
            CodeDebugger debugger = getDebugger(sender);
            if (debugger == null) return;

            CodeDebugger.Group g = debugger.getGroup(ctx.get("group"));
            if (g == null) {
                sender.sendMessage(Component.text(Translations.get("error.invalid.debug_group")).color(NamedTextColor.RED));
                return;
            }
            g.players.remove((Player) sender);
            if (g.players.isEmpty()) debugger.removeGroup(g.id);
            sender.sendMessage(Component.text(Translations.get("success.ignore.debug_group")).color(NamedTextColor.GREEN));
        }, new ArgumentLiteral("ignore"), new ArgumentString("group"));
    }

    private CodeDebugger getDebugger(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text(Translations.get("error.needs.player")).color(NamedTextColor.RED));
            return null;
        }

        Space space = SpaceManager.getSpaceForPlayer(player);
        if (space == null) {
            sender.sendMessage(Component.text(Translations.get("error.needs.space")).color(NamedTextColor.RED));
            return null;
        }

        if (space.code != player.getInstance()) {
            sender.sendMessage(Component.text(Translations.get("error.mode.code")).color(NamedTextColor.RED));
            return null;
        }

        return space.debugger;
    }

}
