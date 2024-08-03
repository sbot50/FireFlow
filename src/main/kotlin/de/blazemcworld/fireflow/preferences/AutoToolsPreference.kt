package de.blazemcworld.fireflow.preferences

import net.minestom.server.item.Material

object AutoToolsPreference: Preference("Automatically get code tools") {
    override val states = mutableListOf(
        PreferenceState("On", Material.CHEST),
        PreferenceState("Off", Material.ENDER_CHEST)
    )
}