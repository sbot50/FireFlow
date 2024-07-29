package de.blazemcworld.fireflow.util

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.entity.GameMode
import net.minestom.server.entity.Player
import net.minestom.server.entity.attribute.Attribute

fun Player.reset() {
    gameMode = GameMode.ADVENTURE
    isFlying = false
    isAllowFlying = false
    inventory.clear()

    getAttribute(Attribute.PLAYER_BLOCK_INTERACTION_RANGE).baseValue = 4.5
}

fun Player.sendError(msg: String) {
    sendMessage(Component.text(msg).color(NamedTextColor.RED))
}