package de.blazemcworld.fireflow.preferences

import net.minestom.server.entity.Player
import net.minestom.server.item.Material
import java.util.WeakHashMap

object MousePreference: Preference("Code control method") {
    override val states = mutableListOf(
        PreferenceState("Using code tools", Material.CHEST),
        PreferenceState("Using the mouse", Material.COMPASS)
    )

    val playerPreference = WeakHashMap<Player, Byte>()
}