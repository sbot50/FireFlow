package de.blazemcworld.fireflow.util

import net.minestom.server.entity.Player
import net.minestom.server.event.trait.InstanceEvent
import net.minestom.server.event.trait.PlayerEvent
import net.minestom.server.instance.Instance

data class PlayerExitInstanceEvent(private val thePlayer: Player, private val theInstance: Instance) : InstanceEvent, PlayerEvent {
    override fun getInstance() = theInstance
    override fun getPlayer() = thePlayer
}