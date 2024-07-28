package de.blazemcworld.fireflow.node

import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor

data class NodeIO(val name: String, val type: Type) {
    enum class Type(val color: TextColor, val flow: Flow) {
        SIGNAL(NamedTextColor.AQUA, Flow.FORWARDS),
        PLAYER(NamedTextColor.GOLD, Flow.BACKWARDS),
        NUMBER(NamedTextColor.GREEN, Flow.BACKWARDS)
    }

    enum class Flow {
        /**
         * Forwards is for data types, that go from the input(s) to a single output
         * Each input goes to one output, but one output can have multiple inputs.
         * Example: Signals, can come from any output(s), but can only go to one input.
         */
        FORWARDS,
        /**
         * Backwards is for data types, that go from the output to one or more input(s)
         * Each output comes from one input, but an input can have multiple outputs.
         * Example: Values, can only come from one output, but can be used in multiple inputs.
         */
        BACKWARDS
    }
}