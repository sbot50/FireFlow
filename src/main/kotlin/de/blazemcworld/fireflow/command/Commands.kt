package de.blazemcworld.fireflow.command

import net.minestom.server.MinecraftServer

object Commands {
    init {
        val all = listOf(
            LobbyCommand,
            PlayCommand,
            CodeCommand
        )
        all.forEach(MinecraftServer.getCommandManager()::register)
    }
}