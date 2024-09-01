package de.blazemcworld.fireflow.commands;

import de.blazemcworld.fireflow.space.Space;
import de.blazemcworld.fireflow.space.SpaceManager;
import de.blazemcworld.fireflow.util.Messages;
import de.blazemcworld.fireflow.value.AllValues;
import de.blazemcworld.fireflow.value.MessageValue;
import de.blazemcworld.fireflow.value.Value;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentLiteral;
import net.minestom.server.command.builder.arguments.ArgumentString;
import net.minestom.server.entity.Player;

import java.util.List;

public class SetVarCommand extends Command {

    private void callback(CommandSender sender, CommandContext ctx) {
        if (sender instanceof Player player) {
            Space space = SpaceManager.getSpace(player);
            if (space == null) {
                sender.sendMessage(Messages.error("You must be in a space to do this!"));
                return;
            }
            if (!space.info.owner.equals(player.getUuid()) && !space.info.contributors.contains(player.getUuid())) {
                sender.sendMessage(Messages.error("You are not the owner nor a contributor of this space!"));
                return;
            }
            for (Value v : AllValues.dataOnly) {
                if (!v.canInset() || ctx.get(v.getBaseName()) == null) continue;
                if (v.prepareInset(ctx.get("value")) != null) {
                    String value = v.formatInset(v.prepareInset(ctx.get("value")));
                    if (!(v instanceof MessageValue)) space.variables.put(ctx.get("varname"), v.prepareInset(ctx.get("value")));
                    else {
                        space.variables.put(ctx.get("varname"), MessageValue.MM.deserialize(ctx.get("value")));
                        value = MessageValue.MM.serialize(MessageValue.MM.deserialize(ctx.get("value")));
                    }
                    sender.sendMessage(Component.text("Set variable \"" + ctx.get("varname") + "\" to ").color(NamedTextColor.GREEN).append(Component.text(value).color(v.getColor())));
                } else {
                    sender.sendMessage(Messages.error("Invalid value for literal of type \"" + v.getBaseName() + "\"!"));
                    String finalStr = ctx.get("value");
                    new Thread(() -> {
                        List<String> suggestions = v.getSuggestions(finalStr);
                        if (!suggestions.isEmpty()) {
                            Component msg = Component.text("Did you mean:").color(NamedTextColor.YELLOW);
                            for (String s : suggestions) {
                                msg = msg.appendNewline().append(Component.text("- " + s).color(NamedTextColor.YELLOW));
                            }
                            Component finalMsg = msg;
                            MinecraftServer.getSchedulerManager().scheduleNextTick(() ->
                                sender.sendMessage(finalMsg)
                            );
                        }
                    }).start();
                }
                return;
            }
            sender.sendMessage(Messages.error("Couldn't find literal value!"));
        } else {
            sender.sendMessage(Messages.error("Only players can do this!"));
        }
    }

    public SetVarCommand() {
        super("set");
        for (Value v : AllValues.dataOnly) {
            if (v.canInset()) {
                addSyntax(
                        this::callback,
                        new ArgumentString("varname"),
                        new ArgumentLiteral(v.getBaseName()),
                        new ArgumentString("value")
                );
            }
        }
    }

}
