package de.blazemcworld.fireflow

import de.blazemcworld.fireflow.command.Commands
import de.blazemcworld.fireflow.database.DatabaseHelper
import de.blazemcworld.fireflow.util.PlayerExitInstanceEvent
import io.github.oshai.kotlinlogging.KotlinLogging
import net.minestom.server.MinecraftServer
import net.minestom.server.entity.GameMode
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent
import net.minestom.server.event.player.PlayerDisconnectEvent
import net.minestom.server.extras.MojangAuth

object FireFlow {
    val LOGGER = KotlinLogging.logger {}
}

fun main() {
    FireFlow.LOGGER.info { "Starting up FireFlow..." }

    DatabaseHelper.init()

    val srv = MinecraftServer.init()!!
    MojangAuth.init()

    Lobby
    Commands

    val events = MinecraftServer.getGlobalEventHandler()

    events.addListener(AsyncPlayerConfigurationEvent::class.java) {
        it.spawningInstance = Lobby.instance
        it.player.gameMode = GameMode.ADVENTURE
        DatabaseHelper.onJoin(it.player)
    }
    events.addListener(PlayerDisconnectEvent::class.java) {
        MinecraftServer.getGlobalEventHandler().call(PlayerExitInstanceEvent(it.player, it.player.instance))
    }

    srv.start("0.0.0.0", 25565)

    FireFlow.LOGGER.info { "Ready!" }
}