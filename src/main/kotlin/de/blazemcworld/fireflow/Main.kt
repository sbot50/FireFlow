package de.blazemcworld.fireflow

import de.blazemcworld.fireflow.gui.IOComponent
import de.blazemcworld.fireflow.gui.NodeComponent
import de.blazemcworld.fireflow.gui.Pos2d
import de.blazemcworld.fireflow.node.impl.NodeList
import io.github.oshai.kotlinlogging.KotlinLogging
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.BlockVec
import net.minestom.server.entity.GameMode
import net.minestom.server.entity.Player
import net.minestom.server.entity.attribute.Attribute
import net.minestom.server.event.player.*
import net.minestom.server.extras.MojangAuth
import net.minestom.server.instance.LightingChunk
import net.minestom.server.instance.block.Block

private val LOGGER = KotlinLogging.logger {}

fun main() {
    LOGGER.info { "Starting up FireFlow..." }
    val srv = MinecraftServer.init()!!
    MojangAuth.init()

    val manager = MinecraftServer.getInstanceManager()
    val inst = manager.createInstanceContainer()

    inst.setChunkSupplier(::LightingChunk)

    inst.setGenerator {
        if (it.absoluteStart().z() != 16.0) return@setGenerator
        it.modifier().fill(
            BlockVec(0, 0, 0).add(it.absoluteStart()),
            BlockVec(16, 128, 1).add(it.absoluteStart()),
            Block.POLISHED_BLACKSTONE
        )
    }

    val events = MinecraftServer.getGlobalEventHandler()

    events.addListener(AsyncPlayerConfigurationEvent::class.java) {
        it.spawningInstance = inst
        it.player.gameMode = GameMode.ADVENTURE
    }
    events.addListener(PlayerSpawnEvent::class.java) {
        it.player.isAllowFlying = true
        it.player.isFlying = true
        it.player.getAttribute(Attribute.PLAYER_BLOCK_INTERACTION_RANGE).baseValue = 16.0
    }
    events.addListener(PlayerBlockBreakEvent::class.java) {
        it.isCancelled = true
    }
    events.addListener(PlayerBlockPlaceEvent::class.java) {
        it.isCancelled = true
    }

    var creatingLine: IOComponent.Output? = null

    val allNodes = mutableListOf<NodeComponent>()
    events.addListener(PlayerBlockInteractEvent::class.java) {
        if (it.hand == Player.Hand.OFF) return@addListener
        val cursor = Pos2d(
            it.blockPosition.x() + it.cursorPosition.x(),
            it.blockPosition.y() + it.cursorPosition.y()
        )
        for (node in allNodes) {
            if (node.includes(cursor)) {
                for (output in node.outputs) {
                    if (output.includes(cursor)) {
                        creatingLine = output
                        return@addListener
                    }
                }
                creatingLine?.let { output ->
                    for (input in node.inputs) {
                        if (input.includes(cursor)) {
                            input.connect(output)
                            input.update(inst)
                            creatingLine = null
                            return@addListener
                        }
                    }
                }


                if (it.player.heldSlot.toInt() == 8) {
                    creatingLine = null
                    node.remove()
                    allNodes.remove(node)
                } else {
                    node.pos = cursor
                    node.update(inst)
                }
                return@addListener
            } else if (it.player.heldSlot.toInt() == 8) {
                for (input in node.inputs) {
                    for (line in input.lines) {
                        if (line.distance(cursor) < 0.1) {
                            input.connections.remove(input.lineOutputMap[line])
                            input.update(inst)
                            return@addListener
                        }
                    }
                }
            }
        }
        if (it.player.heldSlot >= NodeList.all.size) return@addListener

        val node = NodeList.all[it.player.heldSlot.toInt()]
        allNodes.add(node.newComponent().apply {
            pos = cursor
            update(inst)
        })
    }

    srv.start("0.0.0.0", 25565)

    LOGGER.info { "Ready!" }
}