package de.blazemcworld.fireflow;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.blazemcworld.fireflow.code.node.NodeList;
import de.blazemcworld.fireflow.code.type.AllTypes;
import de.blazemcworld.fireflow.command.CodeCommand;
import de.blazemcworld.fireflow.command.ContributorCommand;
import de.blazemcworld.fireflow.command.FunctionCommand;
import de.blazemcworld.fireflow.command.JoinCommand;
import de.blazemcworld.fireflow.command.LobbyCommand;
import de.blazemcworld.fireflow.command.PlayCommand;
import de.blazemcworld.fireflow.command.ReloadCommand;
import de.blazemcworld.fireflow.space.Lobby;
import de.blazemcworld.fireflow.space.SpaceManager;
import de.blazemcworld.fireflow.util.Config;
import de.blazemcworld.fireflow.util.PlayerExitInstanceEvent;
import de.blazemcworld.fireflow.util.TextWidth;
import de.blazemcworld.fireflow.util.Translations;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.GameMode;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.server.ServerListPingEvent;
import net.minestom.server.extras.MojangAuth;
import net.minestom.server.ping.ResponseData;

public class FireFlow {

    public static final Logger LOGGER = LogManager.getLogger("FireFlow");
    private static final Component motd = MiniMessage.miniMessage().deserialize(Config.store.motd());
    
    public static void main(String[] args) {
        LOGGER.info("Starting...");
        MinecraftServer server = MinecraftServer.init();

        MinecraftServer.setBrandName("FireFlow");
        MojangAuth.init();

        Translations.init();
        NodeList.init();
        AllTypes.init();
        Lobby.init();
        SpaceManager.init();
        TextWidth.init();

        MinecraftServer.getCommandManager().register(
                new CodeCommand(),
                new ContributorCommand(),
                new JoinCommand(),
                new PlayCommand(),
                new ReloadCommand(),
                new LobbyCommand(),
                new FunctionCommand()
        );

        GlobalEventHandler events = MinecraftServer.getGlobalEventHandler();

        events.addListener(AsyncPlayerConfigurationEvent.class, event -> {
            event.setSpawningInstance(Lobby.instance);
            event.getPlayer().setGameMode(GameMode.ADVENTURE);
        });

        events.addListener(PlayerDisconnectEvent.class, event -> {
            events.call(new PlayerExitInstanceEvent(event.getPlayer()));
        });

        events.addListener(ServerListPingEvent.class, event -> {
            ResponseData res = new ResponseData();
            res.setDescription(motd);

            try {
                Path favicon = Path.of("favicon.png");
                if (Files.exists(favicon))
                    res.setFavicon("data:image/png;base64," + Base64.getEncoder().encodeToString(Files.readAllBytes(favicon)));
            } catch (IOException e) {
                LOGGER.error(e);
            }

            event.setResponseData(res);
        });

        server.start("0.0.0.0", Config.store.port());
        LOGGER.info("Ready!");
    }

}
