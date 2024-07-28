package de.blazemcworld.fireflow

import de.blazemcworld.fireflow.gui.IOComponent
import de.blazemcworld.fireflow.gui.NodeComponent
import de.blazemcworld.fireflow.gui.Pos2d
import de.blazemcworld.fireflow.node.impl.NodeList
import io.github.oshai.kotlinlogging.KotlinLogging
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.BlockVec
import net.minestom.server.coordinate.Pos
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.GameMode
import net.minestom.server.entity.Player
import net.minestom.server.entity.attribute.Attribute
import net.minestom.server.event.player.*
import net.minestom.server.extras.MojangAuth
import net.minestom.server.instance.Instance
import net.minestom.server.instance.InstanceContainer
import net.minestom.server.instance.InstanceManager
import net.minestom.server.instance.LightingChunk
import net.minestom.server.instance.block.Block
import net.minestom.server.timer.Scheduler
import net.minestom.server.timer.TaskSchedule

private val LOGGER = KotlinLogging.logger {}
private var selectedNode: NodeComponent? = null
private var cursorOffset: Pos2d? = null
private var player: Player? = null

fun main() {
    LOGGER.info { "Starting up FireFlow..." }
    val srv = MinecraftServer.init()!!
    MojangAuth.init()

    val manager = MinecraftServer.getInstanceManager()
    val inst = manager.createInstanceContainer()
    val scheduler = MinecraftServer.getSchedulerManager()

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
                } else if (it.player.heldSlot.toInt() == 7) {
                    if (selectedNode == node) {
                        selectedNode?.setOutlineColor(NamedTextColor.WHITE)
                        selectedNode?.update(inst)
                        selectedNode = null
                    } else {
                        node.setOutlineColor(NamedTextColor.GREEN)
                        node.update(inst)
                        cursorOffset = node.pos - cursor
                        selectedNode = node
                        player = it.player
                        scheduler.scheduleNextTick { moveNode(inst, scheduler) }
                    }
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

fun rayCast(player: Player): Vec {
    val norm = player.position.direction().dot(Vec(0.0, 0.0, -1.0))
    if (norm >= 0) return Vec(0.0, 0.0, 0.0)
    val start = Pos(player.position).asVec().add(0.0, player.eyeHeight, -16.0)
    val dist = -start.dot(Vec(0.0, 0.0, -1.0)) / norm
    if (dist < 0) return Vec(0.0, 0.0, 0.0)
    return start.add(0.0, 0.0, 16.0).add(player.position.direction().mul(dist))
}

fun moveNode(inst: InstanceContainer, scheduler: Scheduler) {
    if (player == null || selectedNode == null || cursorOffset == null) return

    val raycast = rayCast(player!!)
    val cursor = Pos2d(
        raycast.x(),
        raycast.y()
    )

    if (selectedNode != null && player?.heldSlot?.toInt() == 7) {
        selectedNode!!.pos = cursor + cursorOffset!!
        selectedNode!!.update(inst)
        scheduler.scheduleNextTick { moveNode(inst, scheduler) }
    } else {
        selectedNode = null
        cursorOffset = null
        player = null
        return
    }
}