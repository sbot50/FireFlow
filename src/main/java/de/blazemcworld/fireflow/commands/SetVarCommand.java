package de.blazemcworld.fireflow.commands;

import de.blazemcworld.fireflow.space.Space;
import de.blazemcworld.fireflow.space.SpaceManager;
import de.blazemcworld.fireflow.util.Messages;
import de.blazemcworld.fireflow.value.AllValues;
import de.blazemcworld.fireflow.value.Value;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentLiteral;
import net.minestom.server.command.builder.arguments.ArgumentString;
import net.minestom.server.entity.Player;

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
            //space.info.title = ctx.get("setvar");
            //sender.sendMessage(Messages.success("Changed space title!"));
        } else {
            sender.sendMessage(Messages.error("Only players can do this!"));
        }
    }

    public SetVarCommand() {
        super("setvar");
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
