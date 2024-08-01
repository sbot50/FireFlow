package de.blazemcworld.fireflow.space

import com.google.gson.*
import de.blazemcworld.fireflow.FireFlow
import de.blazemcworld.fireflow.Lobby
import de.blazemcworld.fireflow.gui.NodeComponent
import de.blazemcworld.fireflow.gui.Pos2d
import de.blazemcworld.fireflow.inventory.ToolsInventory
import de.blazemcworld.fireflow.node.*
import de.blazemcworld.fireflow.node.impl.NodeList
import de.blazemcworld.fireflow.tool.Tool
import de.blazemcworld.fireflow.util.PlayerExitInstanceEvent
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
import net.minestom.server.event.inventory.PlayerInventoryItemChangeEvent
import net.minestom.server.event.item.ItemDropEvent
import net.minestom.server.event.player.*
import net.minestom.server.instance.InstanceContainer
import net.minestom.server.instance.LightingChunk
import net.minestom.server.instance.anvil.AnvilLoader
import net.minestom.server.instance.block.Block
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.timer.TaskSchedule
import java.io.File
import java.util.*
import kotlin.math.abs

private val TOOLS_ITEM = ItemStack.builder(Material.CHEST)
    .customName(Component.text("Tools").color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false))
    .lore(
        Component.text("Get tools for").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
        Component.text("editing your code").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)
    ).build()

class Space(val id: Int) {

    val codeInstance: InstanceContainer
    val playInstance: InstanceContainer
    val codeNodes = mutableListOf<NodeComponent>()
    val functions = mutableListOf<Pair<FunctionInputsNode, FunctionOutputsNode>>()
    val functionNodes = mutableSetOf<FunctionCallNode>()
    val varStore = mutableMapOf<String, Pair<ValueType<*>, Any?>>()
    private var isUnused = false
    private var globalNodeContext: GlobalNodeContext
    private val spaceDir = File("spaces").resolve(id.toString())
    private val codeFile = spaceDir.resolve("code.json")
    private val variableFile = spaceDir.resolve("vars.json")

    init {
        FireFlow.LOGGER.info { "Loading Space #$id" }
        val manager = MinecraftServer.getInstanceManager()
        codeInstance = manager.createInstanceContainer()
        playInstance = manager.createInstanceContainer()
        codeInstance.timeRate = 0
        playInstance.timeRate = 0

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
        playInstance.setGenerator gen@{
            if (abs(it.absoluteStart().x() + 8) > 16) return@gen
            if (abs(it.absoluteStart().z() + 8) > 16) return@gen
            it.modifier().fillHeight(-1, 0, Block.SMOOTH_STONE)
        }

        readCodeFromDisk()
        readVariablesFromDisk()

        val scheduler = MinecraftServer.getSchedulerManager()
        scheduler.submitTask {
            save()

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
            it.player.inventory.setItemStack(8, TOOLS_ITEM)
        }

        codeEvents.addListener(PlayerBlockBreakEvent::class.java) {
            it.isCancelled = true
        }
        codeEvents.addListener(PlayerBlockPlaceEvent::class.java) {
            it.isCancelled = true
        }
        codeEvents.addListener(ItemDropEvent::class.java) {
            it.isCancelled = true
        }

        val playerTools = WeakHashMap<Player, Tool.Handler>()

        fun updateTool(player: Player, quit: Boolean = false) {
            if (!quit) for (tool in Tool.allTools) {
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

        codeEvents.addListener(PlayerUseItemEvent::class.java) click@{
            if (it.hand == Player.Hand.OFF) return@click

            if (it.player.itemInMainHand == TOOLS_ITEM) {
                ToolsInventory.open(it.player)
                return@click
            }

            updateTool(it.player)
            playerTools[it.player]?.use()
        }

        codeEvents.addListener(PlayerChatEvent::class.java) {
            it.isCancelled = playerTools[it.player]?.chat(it.message) ?: false
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
        codeEvents.addListener(PlayerExitInstanceEvent::class.java) {
            updateTool(it.player, quit=true)
        }

        globalNodeContext = GlobalNodeContext(this)
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

    fun reload() {
        for (p in playInstance.players) {
            p.sendMessage(Component.text("Space is reloading!").color(NamedTextColor.RED))
            Lobby.playerJoin(p)
        }
        globalNodeContext.onDestroy.forEach { it() }
        globalNodeContext = GlobalNodeContext(this)
    }

    private fun writeType(t: ValueType<*>): JsonElement {
        if (t.generic == null) {
            return JsonPrimitive(t.name)
        }
        val info = JsonObject()
        info.addProperty("__type", t.generic!!.name)
        for ((k, v) in t.generics) {
            info.add(k, writeType(v))
        }
        return info
    }

    private fun writeCodeToDisk() {
        val data = JsonObject()

        val functionJson = JsonArray()
        for ((input, output) in functions) {
            val fnData = JsonObject()
            fnData.addProperty("id", input.fn)
            fnData.addProperty("inId", codeNodes.indexOf(input.component))
            fnData.addProperty("outId", codeNodes.indexOf(output.component))

            val inputs = JsonArray()
            for (type in input.outputs) {
                val info = JsonObject()
                info.addProperty("name", type.name)
                info.add("type", writeType(type.type))
                inputs.add(info)
            }
            fnData.add("in", inputs)

            val outputs = JsonArray()
            for (type in output.inputs) {
                val info = JsonObject()
                info.addProperty("name", type.name)
                info.add("type", writeType(type.type))
                outputs.add(info)
            }
            fnData.add("out", outputs)

            functionJson.add(fnData)
        }
        data.add("functions", functionJson)

        val nodeList = JsonArray()
        for (node in codeNodes) {
            val nodeJson = JsonObject()
            nodeJson.addProperty("id", node.node.title)
            nodeJson.addProperty("x", node.pos.x)
            nodeJson.addProperty("y", node.pos.y)
            if (node.node is FunctionCallNode) {
                nodeJson.addProperty("fn", 1)
            }

            if (node.node.generics.isNotEmpty()) {
                nodeJson.addProperty("id", node.node.generic!!.title)
                val generics = JsonObject()
                for ((k, v) in node.node.generics) {
                    generics.add(k, writeType(v))
                }
                nodeJson.add("g", generics)
            }

            if (node.valueLiteral != "unset") nodeJson.addProperty("literal", node.valueLiteral)

            val connections = JsonArray()
            for (input in node.inputs) {
                val outputs = JsonArray()
                for (c in input.connections) {
                    val id = JsonArray()
                    id.add(codeNodes.indexOf(c.node))
                    id.add(c.node.node.outputs.indexOf(c.io))
                    outputs.add(id)
                }
                connections.add(outputs)
            }
            nodeJson.add("connections", connections)
            nodeList.add(nodeJson)
        }
        data.add("nodes", nodeList)
        if (!codeFile.parentFile.exists()) codeFile.parentFile.mkdirs()
        codeFile.writeText(data.toString())
    }

    private fun readType(json: JsonElement): ValueType<*>? {
        if (json.isJsonPrimitive) {
            return AllTypes.all.find { it is ValueType<*> && it.name == json.asString } as ValueType<*>?
        }
        if (json !is JsonObject) return null
        val genericType = AllTypes.all.find { it is GenericType && json.get("__type").asString == it.name } as GenericType
        val info = mutableMapOf<String, ValueType<*>>()
        for ((k, v) in json.entrySet()) {
            if (k == "__type") continue
            info[k] = readType(v) ?: return null
        }
        return genericType.create(info)
    }


    private fun readCodeFromDisk() {
        if (!codeFile.exists()) return
        val data = JsonParser.parseString(codeFile.readText()).asJsonObject
        codeNodes.clear()
        functions.clear()
        functionNodes.clear()

        val newNodes = mutableListOf<NodeComponent?>()
        while (newNodes.size < data.getAsJsonArray("nodes").size()) {
            newNodes += null
        }

        fn@for (fnJson in data.getAsJsonArray("functions")) {
            if (fnJson !is JsonObject) throw IllegalStateException("Expected only json objects in functions array!")

            val id = fnJson.get("id").asString
            val inputs = FunctionInputsNode(id)
            val outputs = FunctionOutputsNode(id)

            for (input in fnJson.getAsJsonArray("in")) {
                if (input !is JsonObject) throw IllegalStateException("Expected only json objects in function inputs array!")
                inputs.add(input.get("name").asString, readType(input.get("type")) ?: continue@fn)
            }
            for (output in fnJson.getAsJsonArray("out")) {
                if (output !is JsonObject) throw IllegalStateException("Expected only json objects in function outputs array!")
                outputs.add(output.get("name").asString, readType(output.get("type")) ?: continue@fn)
            }

            functions += inputs to outputs
            newNodes[fnJson.get("inId").asInt] = inputs.component
            newNodes[fnJson.get("outId").asInt] = outputs.component
            functionNodes.add(FunctionCallNode(inputs, outputs))
        }

        for ((id, nodeJson) in data.getAsJsonArray("nodes").withIndex()) {
            if (nodeJson !is JsonObject) throw IllegalStateException("Expected only json objects in node array.")

            val comp = newNodes[id] ?: run {
                if (nodeJson.has("g")) {
                    val type = NodeList.all.find { it is GenericNode && it.title == nodeJson.get("id").asString } as GenericNode? ?: return@run null

                    val info = mutableMapOf<String, ValueType<*>>()
                    for ((k, v) in nodeJson.get("g").asJsonObject.entrySet()) {
                        info[k] = readType(v) ?: return@run null
                    }
                    return@run type.create(info).newComponent()
                }
                var search = NodeList.all
                if (nodeJson.has("fn")) {
                    search = functionNodes
                }
                val type = search.find { it.title == nodeJson.get("id").asString && it is BaseNode } as BaseNode?
                type?.newComponent()
            } ?: continue
            comp.pos = Pos2d(
                nodeJson.get("x").asDouble,
                nodeJson.get("y").asDouble
            )
            if (nodeJson.has("literal")) comp.valueLiteral = nodeJson.get("literal").asString
            newNodes[id] = comp
        }
        for ((index, nodeJson) in data.get("nodes").asJsonArray.withIndex()) {
            if (nodeJson !is JsonObject) continue
            val node = newNodes[index] ?: continue

            for ((inputIndex, conn) in nodeJson.get("connections").asJsonArray.withIndex()) {
                val input = node.inputs[inputIndex]
                for (outputInfo in conn.asJsonArray) {
                    if (outputInfo !is JsonArray) throw IllegalStateException("Expected only json arrays in connections array.")
                    (newNodes[outputInfo[0].asInt] ?: continue).outputs[outputInfo[1].asInt].connect(input)
                }
            }
        }
        codeNodes.addAll(newNodes.filterNotNull())
        for (node in codeNodes) node.update(codeInstance)
    }

    fun save() {
        FireFlow.LOGGER.info { "Saving Space #$id" }
        playInstance.saveChunksToStorage()
        writeCodeToDisk()
        writeVariablesToDisk()
    }

    private fun writeVariablesToDisk() {
        val types = mutableMapOf<ValueType<*>, MutableSet<Pair<String, Any?>>>()
        for ((n, v) in varStore) {
            types.computeIfAbsent(v.first) { mutableSetOf() }.add(n to v.second)
        }
        val store = JsonObject()
        val objectsJson = JsonArray()
        val typesMap = JsonArray()
        for ((t, all) in types) {
            val entry = JsonObject()
            entry.add("type", writeType(t))
            val map = JsonObject()
            val objects = mutableMapOf<Any?, Pair<Int, JsonElement>>()
            for ((k, v) in all) {
                map.add(k, (t as ValueType<Any?>).serialize(v, objects))
            }
            entry.add("vars", map)
            for ((id, v) in objects.values) {
                while (objectsJson.size() < id) objectsJson.add(JsonNull.INSTANCE)
                objectsJson.set(id, v)
            }
            typesMap.add(entry)
        }
        store.add("types", typesMap)
        store.add("objects", objectsJson)
        if (!variableFile.parentFile.exists()) variableFile.parentFile.mkdirs()
        variableFile.writeText(store.toString())
    }

    private fun readVariablesFromDisk() {
        varStore.clear()
        if (!variableFile.exists()) return
        val json = JsonParser.parseString(variableFile.readText()).asJsonObject

        val objectsJson = json.getAsJsonArray("objects")
        val objects = mutableMapOf<Int, Pair<Any?, JsonElement>>()
        for ((i, v) in objectsJson.withIndex()) objects[i] = null to v

        for (type in json.getAsJsonArray("types")) {
            val t = readType(type.asJsonObject.get("type")) ?: continue
            for ((k, v) in type.asJsonObject.getAsJsonObject("vars").entrySet()) {
                varStore[k] = t to t.deserialize(v, this, objects)
            }
        }
    }
}