package de.blazemcworld.fireflow.space

import de.blazemcworld.fireflow.FireFlow
import de.blazemcworld.fireflow.gui.NodeComponent
import de.blazemcworld.fireflow.gui.Pos2d
import de.blazemcworld.fireflow.inventory.ToolsInventory
import de.blazemcworld.fireflow.tool.Tool
import de.blazemcworld.fireflow.util.reset
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.BlockVec
import net.minestom.server.coordinate.Pos
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.GameMode
import net.minestom.server.entity.Player
import net.minestom.server.entity.attribute.Attribute
import net.minestom.server.event.inventory.PlayerInventoryItemChangeEvent
import net.minestom.server.event.player.*
import net.minestom.server.instance.InstanceContainer
import net.minestom.server.instance.LightingChunk
import net.minestom.server.instance.anvil.AnvilLoader
import net.minestom.server.instance.block.Block
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.timer.TaskSchedule
import java.util.*

private val TOOLS_ITEM = ItemStack.builder(Material.CHEST)
    .customName(Component.text("Tools").color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false))
    .lore(
        Component.text("Get tools for").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
        Component.text("editing your code").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)
    ).build()

class Space(val id: Int) {

    val codeInstance: InstanceContainer
    val playInstance: InstanceContainer
    private var isUnused = false
    val codeNodes = mutableListOf<NodeComponent>()

    init {
        FireFlow.LOGGER.info { "Loading Space #$id" }
        val manager = MinecraftServer.getInstanceManager()
        codeInstance = manager.createInstanceContainer()
        playInstance = manager.createInstanceContainer()

        codeInstance.setChunkSupplier(::LightingChunk)
        playInstance.setChunkSupplier(::LightingChunk)

        playInstance.chunkLoader = AnvilLoader("spaces/$id")

        codeInstance.setGenerator {
            if (it.absoluteStart().z() != 16.0) return@setGenerator
            it.modifier().fill(
                BlockVec(0, 0, 0).add(it.absoluteStart()),
                BlockVec(16, 128, 1).add(it.absoluteStart()),
                Block.POLISHED_BLACKSTONE
            )
        }

        val scheduler = MinecraftServer.getSchedulerManager()
        scheduler.submitTask {
            playInstance.saveChunksToStorage()

            if (codeInstance.players.size == 0 && playInstance.players.size == 0) {
                if (isUnused) {
                    FireFlow.LOGGER.info { "Unloading Space #$id" }
                    SpaceManager.forget(id)
                    manager.unregisterInstance(playInstance)
                    manager.unregisterInstance(codeInstance)
                    return@submitTask TaskSchedule.stop()
                }
                isUnused = true
            }
            return@submitTask TaskSchedule.minutes(1)
        }

        val playEvents = playInstance.eventNode()
        val codeEvents = codeInstance.eventNode()

        playEvents.addListener(PlayerSpawnEvent::class.java) {
            isUnused = false
            it.player.reset()
            it.player.gameMode = GameMode.SURVIVAL
        }

        codeEvents.addListener(PlayerSpawnEvent::class.java) {
            isUnused = false
            it.player.reset()
            it.player.isAllowFlying = true
            it.player.isFlying = true
            it.player.getAttribute(Attribute.PLAYER_BLOCK_INTERACTION_RANGE).baseValue = 16.0
            it.player.inventory.setItemStack(8, TOOLS_ITEM)
        }

        codeEvents.addListener(PlayerBlockBreakEvent::class.java) {
            it.isCancelled = true
        }
        codeEvents.addListener(PlayerBlockPlaceEvent::class.java) {
            it.isCancelled = true
        }

        val playerTools = WeakHashMap<Player, Tool.Handler>()

        fun updateTool(player: Player) {
            for (tool in Tool.allTools) {
                if (tool.item == player.itemInMainHand) {
                    if (playerTools[player]?.tool == tool) return
                    playerTools[player]?.deselect()
                    playerTools[player] = tool.handler(player, this).also { it.select() }
                    return
                }
            }
            playerTools[player]?.deselect()
            playerTools[player] = null
        }

        codeEvents.addListener(PlayerBlockInteractEvent::class.java) click@{
            if (it.hand == Player.Hand.OFF) return@click

            if (it.player.itemInMainHand == TOOLS_ITEM) {
                ToolsInventory.open(it.player)
                return@click
            }

            updateTool(it.player)
            playerTools[it.player]?.use()
        }
        codeEvents.addListener(PlayerSwapItemEvent::class.java) {
            scheduler.execute { updateTool(it.player) }
        }
        codeEvents.addListener(PlayerInventoryItemChangeEvent::class.java) {
            scheduler.execute { updateTool(it.player) }
        }
        codeEvents.addListener(PlayerChangeHeldSlotEvent::class.java) {
            scheduler.execute { updateTool(it.player) }
        }
    }

    fun codeCursor(player: Player): Pos2d {
        val norm = player.position.direction().dot(Vec(0.0, 0.0, -1.0))
        if (norm >= 0) return Pos2d.ZERO
        val start = Pos(player.position).asVec().add(0.0, player.eyeHeight, -16.0)
        val dist = -start.dot(Vec(0.0, 0.0, -1.0)) / norm
        if (dist < 0) return Pos2d.ZERO
        val out = start.add(player.position.direction().mul(dist))
        return Pos2d(out.x, out.y)
    }
}