package de.blazemcworld.fireflow.node

import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor

data class NodeIO(val name: String, val type: Type) {
    enum class Type(val color: TextColor) {
        SIGNAL(NamedTextColor.AQUA),
        PLAYER(NamedTextColor.GOLD),
        NUMBER(NamedTextColor.GREEN)
    }
}