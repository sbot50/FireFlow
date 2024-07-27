package de.blazemcworld.fireflow.node.impl

import de.blazemcworld.fireflow.node.Node
import de.blazemcworld.fireflow.node.NodeIO

object KillPlayerNode : Node {
    override val title = "Kill Player"
    override val inputs = listOf(
        NodeIO("Signal", NodeIO.Type.SIGNAL),
        NodeIO("Player", NodeIO.Type.PLAYER)
    )
    override val outputs = listOf(
        NodeIO("Next", NodeIO.Type.SIGNAL)
    )
}