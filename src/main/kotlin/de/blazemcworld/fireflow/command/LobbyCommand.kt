package de.blazemcworld.fireflow.command

import de.blazemcworld.fireflow.Lobby
import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.CommandExecutor
import net.minestom.server.entity.Player

object LobbyCommand : Command("lobby", "spawn", "hub") {
    init {
        defaultExecutor = CommandExecutor exec@{ sender, _ ->
            if (sender !is Player) return@exec
            if (sender.instance == Lobby.instance) return@exec
            Lobby.playerJoin(sender)
        }
    }
}