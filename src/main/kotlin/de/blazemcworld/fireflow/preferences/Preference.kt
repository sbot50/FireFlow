package de.blazemcworld.fireflow.preferences

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.minestom.server.item.Material

open class Preference(protected val name: String) {
    protected open val states = mutableListOf<PreferenceState>()

    fun getName() = Component.text(name).color(NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false)

    fun getState(value: Byte): PreferenceState {
        return states[value.toInt()]
    }

    fun getLore(): MutableList<Component> {
        return (states.map { Component.text("► " + it.getName()).color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false) }).toMutableList()
    }

    fun increaseState(state: Byte): Byte {
        if (state.toInt() + 1 > states.size - 1) return 0
        return (state.toInt() + 1).toByte()
    }

    class PreferenceState(private val name: String, private val icon: Material) {
        fun getName() = name
        fun getIcon() = icon
    }
}