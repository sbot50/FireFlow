package de.blazemcworld.fireflow.node.impl

import de.blazemcworld.fireflow.node.Node
import de.blazemcworld.fireflow.node.NodeIO

object PlayerJumpEventNode : Node {
    override val title = "On Player Jump"
    override val inputs = emptyList<NodeIO>()
    override val outputs = listOf(
        NodeIO("Signal", NodeIO.Type.SIGNAL),
        NodeIO("Player", NodeIO.Type.PLAYER)
    )
}