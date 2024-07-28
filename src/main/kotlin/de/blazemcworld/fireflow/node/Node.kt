package de.blazemcworld.fireflow.node

import de.blazemcworld.fireflow.gui.IOComponent
import de.blazemcworld.fireflow.gui.NodeComponent
import net.kyori.adventure.text.Component

interface Node {
    val title: String
    val inputs: List<NodeIO>
    val outputs: List<NodeIO>

    fun newComponent(): NodeComponent {
        val c = NodeComponent()
        c.title.text = Component.text(title)
        for (i in inputs) {
            c.inputs.add(IOComponent.Input(i))
        }
        for (o in outputs) {
            c.outputs.add(IOComponent.Output(o))
        }
        return c
    }
}