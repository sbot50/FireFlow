package de.blazemcworld.fireflow.command;

import de.blazemcworld.fireflow.FireFlow;
import de.blazemcworld.fireflow.code.CodeEditor;
import de.blazemcworld.fireflow.space.Space;
import de.blazemcworld.fireflow.space.SpaceManager;
import de.blazemcworld.fireflow.util.Config;
import de.blazemcworld.fireflow.util.Translations;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentLiteral;
import net.minestom.server.command.builder.arguments.ArgumentString;
import net.minestom.server.entity.Player;
import net.minestom.server.timer.TaskSchedule;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

public class SnippetCommand extends Command {

    public SnippetCommand() {
        super("snippet");

        addSyntax((sender, ctx) -> {
            CodeEditor editor = getEditor(sender);
            if (editor == null) return;

            editor.createSnippet((Player) sender);
        }, new ArgumentLiteral("create"));

        addSyntax((sender, ctx) -> {
            CodeEditor editor = getEditor(sender);
            if (editor == null) return;
            Player player = (Player) sender;

            if (Config.store.useFileURLs()) {
                try {
                    Path path = Path.of(ctx.get("url").toString());
                    if (!path.isAbsolute()) {
                        FireFlow.LOGGER.error("User provided non-absolute file path");
                        player.sendMessage(Component.text(Translations.get("error.internal")).color(NamedTextColor.RED));
                        return;
                    }
                    byte[] bytes = Files.readAllBytes(path);
                    MinecraftServer.getSchedulerManager().scheduleTask(() -> {
                       if (player.getInstance() != editor.space.code) return;

                       editor.placeSnippet((Player) sender, bytes);
                    }, TaskSchedule.nextTick(), TaskSchedule.stop());
                } catch (IOException e) {
                    FireFlow.LOGGER.error("Failed to read code snippet from " + ctx.get("url"), e);
                    player.sendMessage(Component.text(Translations.get("error.internal")).color(NamedTextColor.RED));
                }
            } else {
                try (HttpClient client = HttpClient.newHttpClient()) {
                    client.sendAsync(HttpRequest.newBuilder()
                                    .uri(URI.create(ctx.get("url")))
                                    .timeout(Duration.ofSeconds(10))
                                    .build(), HttpResponse.BodyHandlers.ofInputStream())
                            .thenAccept(res -> {
                                if (res == null) return;
                                try (InputStream stream = res.body()) {
                                    byte[] bytes = stream.readNBytes(1048576);
                                    MinecraftServer.getSchedulerManager().scheduleTask(() -> {
                                        if (player.getInstance() != editor.space.code) return;

                                        editor.placeSnippet((Player) sender, bytes);
                                    }, TaskSchedule.nextTick(), TaskSchedule.stop());
                                } catch (IOException e) {
                                    FireFlow.LOGGER.error("Failed to read code snippet from " + ctx.get("url"), e);
                                    player.sendMessage(Component.text(Translations.get("error.internal")).color(NamedTextColor.RED));
                                }
                            })
                            .exceptionally((e) -> {
                                FireFlow.LOGGER.error("Failed to read code snippet from " + ctx.get("url"), e);
                                player.sendMessage(Component.text(Translations.get("error.internal")).color(NamedTextColor.RED));
                                return null;
                            });
                }
            }
        }, new ArgumentLiteral("place"), new ArgumentString("url"));
    }

    private CodeEditor getEditor(CommandSender sender) {
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

        return space.editor;
    }

}
