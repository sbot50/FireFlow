package de.blazemcworld.fireflow.preferences

import net.minestom.server.item.Material

object ReloadPreference: Preference("Rejoin after reload") {
    override val states = mutableListOf(
        PreferenceState("On owned spaces", Material.POLISHED_ANDESITE),
        PreferenceState("On owned & contributer spaces", Material.STONE),
        PreferenceState("On any space", Material.COBBLESTONE),
        PreferenceState("Never", Material.SMOOTH_STONE)
    )
}