package de.blazemcworld.fireflow.command

import de.blazemcworld.fireflow.space.SpaceManager
import de.blazemcworld.fireflow.util.sendError
import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.CommandExecutor
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Player

object PlayCommand : Command("play") {
    init {
        defaultExecutor = CommandExecutor exec@{ sender, _ ->
            if (sender !is Player) return@exec

            val space = SpaceManager.currentSpace(sender)

            if (space == null) {
                sender.sendError("You need to be in a space to do this!")
                return@exec
            }
            if (space.playInstance == sender.instance) {
                sender.sendError("You are already playing on this space!")
                return@exec
            }
            sender.setInstance(space.playInstance, Pos.ZERO)
        }
    }
}