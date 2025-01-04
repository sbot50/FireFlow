package de.blazemcworld.fireflow.command;

import de.blazemcworld.fireflow.space.Space;
import de.blazemcworld.fireflow.space.SpaceManager;
import de.blazemcworld.fireflow.util.Translations;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.util.HSVLike;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.entity.Player;
import net.minestom.server.timer.TaskSchedule;

import java.util.HashMap;

public class MonitorCommand extends Command {

    private static HashMap<Player, Space> monitoring = new HashMap<>();

    public MonitorCommand() {
        super("monitor");

        setDefaultExecutor((sender, ctx) -> {
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

            if (monitoring.containsKey(player)) {
                monitoring.remove(player);
                player.sendActionBar(Component.empty());
                return;
            }
            monitoring.put(player, space);

            MinecraftServer.getSchedulerManager().scheduleTask(() -> {
                if (!monitoring.containsKey(player)) return TaskSchedule.stop();
                Space target = monitoring.get(player);
                Space current = SpaceManager.getSpaceForPlayer(player);
                if (target == null || current == null || current != target || !target.isOwnerOrContributor(player)) {
                    monitoring.remove(player);
                    player.sendActionBar(Component.empty());
                    return TaskSchedule.stop();
                }

                int percentage = space.evaluator.cpuPercentage;
                TextColor color = TextColor.color(HSVLike.hsvLike(Math.clamp(0.3f - percentage / 300f, 0, 0.3f), 1f, 1f));

                Component display = Component.text(Translations.get("info.cpu_usage")).color(color)
                    .append(Component.text(" [").color(NamedTextColor.WHITE))
                    .append(Component.text("|".repeat(percentage / 2)).color(color))
                    .append(Component.text("|".repeat(50 - percentage / 2)).color(NamedTextColor.GRAY))
                    .append(Component.text("]").color(NamedTextColor.WHITE));

                player.sendActionBar(display);

                return TaskSchedule.tick(2);
            }, TaskSchedule.tick(2));
        });
    }

}
