package de.blazemcworld.fireflow.node

import de.blazemcworld.fireflow.gui.NodeComponent
import de.blazemcworld.fireflow.gui.TextComponent
import net.kyori.adventure.text.Component

interface Node {
    val title: String
    val inputs: List<NodeIO>
    val outputs: List<NodeIO>

    fun newComponent(): NodeComponent {
        val c = NodeComponent()
        c.title.text = Component.text(title)
        for (i in inputs) {
            c.inputs.add(TextComponent().apply {
                text = Component.text("○ " + i.name).color(i.type.color)
            })
        }
        for (o in outputs) {
            c.outputs.add(TextComponent().apply {
                text = Component.text(o.name + " ○").color(o.type.color)
            })
        }
        return c
    }
}