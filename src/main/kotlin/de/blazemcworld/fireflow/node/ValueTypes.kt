package de.blazemcworld.fireflow.node

import com.google.gson.*
import de.blazemcworld.fireflow.space.Space
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Player
import net.minestom.server.item.Material
import java.util.*
import kotlin.math.roundToInt

object AllTypes {
    val dataOnly = mutableListOf<SomeType>()
    val all = mutableListOf<SomeType>()

    init {
        dataOnly += PlayerType
        dataOnly += NumberType
        dataOnly += ConditionType
        dataOnly += TextType
        dataOnly += MessageType
        dataOnly += PositionType
        dataOnly += ListType
        all += dataOnly
        all += SignalType
    }
}

interface SomeType {
    val name: String
    val color: TextColor
    val material: Material
}

interface GenericType : SomeType {
    fun create(generics: MutableMap<String, ValueType<*>>): ValueType<*>

    val generics: Map<String, List<SomeType>>
}

abstract class ValueType<T> : SomeType  {
    abstract fun parse(str: String, space: Space): T?
    abstract fun compareEqual(left: T?, right: T?): Boolean
    abstract fun validate(something: Any?): T?
    open val generics = emptyMap<String, ValueType<*>>()
    open val generic: GenericType? = null
    open val insetable = false

    abstract fun serialize(v: T, objects: MutableMap<Any?, Pair<Int, JsonElement>>): JsonElement
    abstract fun deserialize(json: JsonElement, space: Space, objects: MutableMap<Int, Pair<Any?, JsonElement>>): T
    abstract fun stringify(v: T): String
}

object PlayerType : ValueType<PlayerReference>() {
    override val name = "Player"
    override val color: TextColor = NamedTextColor.GOLD
    override val material: Material = Material.PLAYER_HEAD

    override fun parse(str: String, space: Space) = kotlin.runCatching { PlayerReference(UUID.fromString(str), space) }.getOrNull()
    override fun compareEqual(left: PlayerReference?, right: PlayerReference?) = left is PlayerReference && right is PlayerReference && left.uuid == right.uuid
    override fun validate(something: Any?) = if (something is PlayerReference) something else null
    override fun deserialize(
        json: JsonElement,
        space: Space,
        objects: MutableMap<Int, Pair<Any?, JsonElement>>
    ): PlayerReference = PlayerReference(UUID.fromString(json.asString), space)

    override fun serialize(v: PlayerReference, objects: MutableMap<Any?, Pair<Int, JsonElement>>) = JsonPrimitive(v.uuid.toString())

    override fun stringify(v: PlayerReference) = v.resolve()?.username ?: ("Offline Player (" + v.uuid + ")")
}
class PlayerReference(val uuid: UUID, private val space: Space) {
    constructor(player: Player, space: Space) : this(player.uuid, space)

    fun resolve() = space.playInstance.getPlayerByUuid(uuid)
}

object SignalType : ValueType<Unit>() {
    override val name = "Signal"
    override val color: TextColor = NamedTextColor.AQUA
    override val material: Material = Material.LIGHT_BLUE_DYE

    override fun parse(str: String, space: Space) = null
    override fun compareEqual(left: Unit?, right: Unit?) = false
    override fun validate(something: Any?) = null
    override fun deserialize(json: JsonElement, space: Space, objects: MutableMap<Int, Pair<Any?, JsonElement>>): Unit = Unit
    override fun serialize(v: Unit, objects: MutableMap<Any?, Pair<Int, JsonElement>>): JsonNull = JsonNull.INSTANCE

    override fun stringify(v: Unit) = "Signal"
}

object NumberType : ValueType<Double>() {
    override val name = "Number"
    override val color: TextColor = NamedTextColor.RED
    override val material: Material = Material.SLIME_BALL
    override val insetable = true

    override fun parse(str: String, space: Space) = str.toDoubleOrNull()
    override fun compareEqual(left: Double?, right: Double?) = left == right
    override fun validate(something: Any?) = if (something is Double) something else null
    override fun deserialize(
        json: JsonElement,
        space: Space,
        objects: MutableMap<Int, Pair<Any?, JsonElement>>
    ): Double = json.asDouble

    override fun serialize(v: Double, objects: MutableMap<Any?, Pair<Int, JsonElement>>) = JsonPrimitive(v)

    override fun stringify(v: Double) = v.toString()
}


object ConditionType : ValueType<Boolean>() {
    override val name = "Condition"
    override val color: TextColor = NamedTextColor.LIGHT_PURPLE
    override val material: Material = Material.ANVIL
    override val insetable = true

    override fun parse(str: String, space: Space) = str == "true"
    override fun compareEqual(left: Boolean?, right: Boolean?) = left == right
    override fun validate(something: Any?) = if (something is Boolean) something else null
    override fun deserialize(
        json: JsonElement,
        space: Space,
        objects: MutableMap<Int, Pair<Any?, JsonElement>>
    ): Boolean = json.asBoolean

    override fun serialize(v: Boolean, objects: MutableMap<Any?, Pair<Int, JsonElement>>) = JsonPrimitive(v)
    override fun stringify(v: Boolean) = v.toString()
}

object TextType : ValueType<String>() {
    override val name = "Text"
    override val color: TextColor = NamedTextColor.GREEN
    override val material: Material = Material.BOOK
    override val insetable = true

    override fun parse(str: String, space: Space) = str
    override fun compareEqual(left: String?, right: String?) = left == right
    override fun validate(something: Any?) = if (something is String) something else null
    override fun deserialize(
        json: JsonElement,
        space: Space,
        objects: MutableMap<Int, Pair<Any?, JsonElement>>
    ): String = json.asString
    override fun serialize(v: String, objects: MutableMap<Any?, Pair<Int, JsonElement>>) = JsonPrimitive(v)

    override fun stringify(v: String) = v
}

private val mm = MiniMessage.builder()
    .tags(TagResolver.builder().resolvers(
        StandardTags.color(),
        StandardTags.decorations(),
        StandardTags.font(),
        StandardTags.gradient(),
        StandardTags.keybind(),
        StandardTags.newline(),
        StandardTags.rainbow(),
        StandardTags.reset(),
        StandardTags.transition(),
        StandardTags.translatable(),
    ).build()).build()

object MessageType : ValueType<Component>() {
    override val name = "Message"
    override val color: TextColor = NamedTextColor.YELLOW
    override val material: Material = Material.ENCHANTED_BOOK
    override val insetable = true

    override fun parse(str: String, space: Space) = mm.deserialize(str)
    override fun compareEqual(left: Component?, right: Component?) = left is Component && right is Component && mm.serialize(left) == mm.serialize(right)
    override fun validate(something: Any?) = if (something is Component) something else null
    override fun deserialize(
        json: JsonElement,
        space: Space,
        objects: MutableMap<Int, Pair<Any?, JsonElement>>
    ): Component = mm.deserialize(json.asString)
    override fun serialize(v: Component, objects: MutableMap<Any?, Pair<Int, JsonElement>>) = JsonPrimitive(mm.serialize(v))

    override fun stringify(v: Component) = mm.serialize(v)
}

object PositionType : ValueType<Pos>() {
    override val name: String = "Position"
    override val color: TextColor = NamedTextColor.YELLOW
    override val material: Material = Material.FILLED_MAP

    override fun parse(str: String, space: Space) = null
    override fun compareEqual(left: Pos?, right: Pos?) = left is Pos && right is Pos
            && left.x == right.x && left.y == right.y && left.z == right.z && left.yaw == right.yaw && left.pitch == right.pitch
    override fun validate(something: Any?) = if (something is Pos) something else null
    override fun deserialize(json: JsonElement, space: Space, objects: MutableMap<Int, Pair<Any?, JsonElement>>): Pos {
        return Pos(
            json.asJsonObject.get("x").asDouble,
            json.asJsonObject.get("y").asDouble,
            json.asJsonObject.get("z").asDouble,
            json.asJsonObject.get("yaw").asFloat,
            json.asJsonObject.get("pitch").asFloat
        )
    }

    override fun stringify(v: Pos) = "Pos(${shorten(v.x)}, ${shorten(v.y)}, ${shorten(v.z)}, ${shorten(v.pitch)}, ${shorten(v.yaw)})"

    private fun shorten(pos: Number) = (pos.toDouble() * 1000.0).roundToInt() / 1000.0

    override fun serialize(v: Pos, objects: MutableMap<Any?, Pair<Int, JsonElement>>): JsonElement {
        val obj = JsonObject()
        obj.addProperty("x", v.x)
        obj.addProperty("y", v.y)
        obj.addProperty("z", v.z)
        obj.addProperty("yaw", v.yaw)
        obj.addProperty("pitch", v.pitch)
        return obj
    }
}

object ListType : GenericType {
    private val cache = WeakHashMap<ValueType<*>, Impl<*>>()
    override fun create(generics: MutableMap<String, ValueType<*>>): Impl<*> = create(generics["Type"]!!)
    fun <T> create(type: ValueType<T>): Impl<T> = cache.computeIfAbsent(type) { Impl(type) } as Impl<T>

    override val generics = mapOf("Type" to AllTypes.dataOnly)
    override val name: String = "List"
    override val material: Material = Material.STRING
    override val color: TextColor = NamedTextColor.WHITE

    class Impl<T>(val type: ValueType<T>) : ValueType<ListReference<T>>() {
        override val generics = mapOf("Type" to type)
        override val generic = ListType

        override fun deserialize(json: JsonElement, space: Space, objects: MutableMap<Int, Pair<Any?, JsonElement>>): ListReference<T> {
            val id = json.asInt
            objects[id]?.first?.let { return it as ListReference<T> }
            val jsonInfo = objects[id]!!.second
            val out = mutableListOf<T>()
            for (each in jsonInfo.asJsonArray) {
                out.add(type.deserialize(each, space, objects))
            }
            val res = ListReference(type, out)
            objects[id] = res to jsonInfo
            return res
        }

        override fun serialize(v: ListReference<T>, objects: MutableMap<Any?, Pair<Int, JsonElement>>): JsonElement {
            if (objects.containsKey(this)) return JsonPrimitive(objects[this]!!.first)
            val json = JsonArray()
            val id = objects.size
            objects[this] = id to json
            for (each in v.store) {
                json.add(type.serialize(each, objects))
            }
            return JsonPrimitive(id)
        }

        override fun stringify(v: ListReference<T>) = "List(${v.store.size} Entries)"

        override val name: String = "List(${type.name})"
        override val color: TextColor = type.color
        override val material: Material = type.material

        override fun parse(str: String, space: Space) = null
        override fun validate(something: Any?) = if (something is ListReference<*> && something.type == type) something as ListReference<T> else null

        override fun compareEqual(left: ListReference<T>?, right: ListReference<T>?) = left != null && right != null && left.store == right.store
    }
}
class ListReference<T>(val type: ValueType<T>, val store: MutableList<T>)