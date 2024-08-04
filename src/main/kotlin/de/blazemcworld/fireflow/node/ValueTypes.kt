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
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.Player
import net.minestom.server.item.Material
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.abs
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
        dataOnly += DictionaryType
        dataOnly += VectorType
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
    open val extractions: MutableList<TypeExtraction<T, *>> = mutableListOf()
    open val insetable = false

    abstract fun serialize(v: T, objects: MutableMap<Any?, Pair<Int, JsonElement>>): JsonElement
    abstract fun deserialize(json: JsonElement, space: Space, objects: MutableMap<Int, Pair<Any?, JsonElement>>): T
    abstract fun stringify(v: T): String
}

class TypeExtraction<I, O>(val icon: Material, val name: String, val inputType: ValueType<I>, private val outputType: ValueType<O>, val extractor: (I) -> O) {
    val input: BaseNode.Input<I> = BaseNode.Input("", inputType)
    val output: BaseNode.Output<O> = BaseNode.Output(name, outputType)
    val formalName = "${inputType.name}-${name}"

    companion object {
        val list: HashMap<String, TypeExtraction<*, *>> = HashMap()

        fun get(name: String) = list[name]
    }

    init {
        list[formalName] = this
    }

    fun extract(input: I) = outputType.validate(extractor(input))
    fun asInput(i: Any) = i as? I
}

object PlayerType : ValueType<PlayerReference>() {
    override val name = "Player"
    override val color: TextColor = NamedTextColor.GOLD
    override val material: Material = Material.PLAYER_HEAD
    override val extractions: MutableList<TypeExtraction<PlayerReference, *>> = mutableListOf(
        TypeExtraction(Material.ANVIL, "UUID", this, TextType) { it.uuid.toString() },
        TypeExtraction(Material.NAME_TAG, "Username", this, TextType) { it.resolve()?.username ?: "Offline Player" },
        TypeExtraction(Material.GOLDEN_APPLE, "Health", this, NumberType) { it.resolve()?.health?.toDouble() ?: 0.0 },
        TypeExtraction(Material.COOKED_CHICKEN, "Food Level", this, NumberType) { it.resolve()?.food?.toDouble() ?: 0.0 },
        TypeExtraction(Material.COOKED_MUTTON, "Saturation", this, NumberType) { it.resolve()?.foodSaturation?.toDouble() ?: 0.0 },
        TypeExtraction(Material.EXPERIENCE_BOTTLE, "Experience Points", this, NumberType) { it.resolve()?.exp?.toDouble() ?: 0.0 },
        TypeExtraction(Material.EXPERIENCE_BOTTLE, "Experience Level", this, NumberType) { it.resolve()?.level?.toDouble() ?: 0.0 },
        TypeExtraction(Material.COMPASS, "Position", this, PositionType) { it.resolve()?.position ?: Pos(0.0, 0.0, 0.0, 0.0f, 0.0f) },
        TypeExtraction(Material.PRISMARINE_SHARD, "Direction", this, VectorType) { it.resolve()?.position?.direction() ?: Vec(0.0, 0.0, 0.0) },
        TypeExtraction(Material.RABBIT_FOOT, "Velocity", this, VectorType) { it.resolve()?.velocity ?: Vec(0.0, 0.0, 0.0) },
        // When item types come into play, we can add these
        TypeExtraction(Material.RED_DYE, "Is Dead", this, ConditionType) { it.resolve()?.isDead ?: false },
        TypeExtraction(Material.LIME_DYE, "Is Online", this, ConditionType) { it.resolve() != null },
        TypeExtraction(Material.ELYTRA, "Is Flying", this, ConditionType) { it.resolve()?.isFlying ?: false },
        TypeExtraction(Material.CHAINMAIL_LEGGINGS, "Is Sneaking", this, ConditionType) { it.resolve()?.isSneaking ?: false },
        TypeExtraction(Material.DIAMOND_BOOTS, "Is Sprinting", this, ConditionType) { it.resolve()?.isSprinting ?: false },
        TypeExtraction(Material.OAK_PRESSURE_PLATE, "Is Grounded", this, ConditionType) { it.resolve()?.isOnGround ?: false },
        TypeExtraction(Material.COOKED_BEEF, "Is Eating", this, ConditionType) { it.resolve()?.isEating ?: false },
    )

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
    override val extractions: MutableList<TypeExtraction<Double, *>> = mutableListOf(
        TypeExtraction(Material.NAME_TAG, "As String", this, TextType) { it.toString() },
        TypeExtraction(Material.GOLDEN_APPLE, "Absolute Value", this, NumberType) { abs(it) },
        TypeExtraction(Material.ANVIL, "Ceil", this, NumberType) { kotlin.math.ceil(it) },
        TypeExtraction(Material.IRON_INGOT, "Round", this, NumberType) { kotlin.math.round(it) },
        TypeExtraction(Material.LIGHT_WEIGHTED_PRESSURE_PLATE, "Floor", this, NumberType) { kotlin.math.floor(it) },
        TypeExtraction(Material.DIAMOND, "Square", this, NumberType) { it * it },
        TypeExtraction(Material.EMERALD, "Square Root", this, NumberType) { kotlin.math.sqrt(it) },
        TypeExtraction(Material.REDSTONE, "Negate", this, NumberType) { -it }
    )

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
    override val extractions: MutableList<TypeExtraction<Boolean, *>> = mutableListOf(
        TypeExtraction(Material.NAME_TAG, "As String", this, TextType) { it.toString() },
        TypeExtraction(Material.REDSTONE, "Not", this, ConditionType) { !it }
    )

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
    override val extractions: MutableList<TypeExtraction<String, *>> = mutableListOf(
        TypeExtraction(Material.NAME_TAG, "As Message", this, MessageType) { Component.text(it) },
        TypeExtraction(Material.INK_SAC, "Format MiniMessage", this, MessageType) { Component.text(mm.serialize(Component.text(it))) },
        TypeExtraction(Material.PAPER, "Remove Padding Spaces", this, TextType) { it.trim() },
        TypeExtraction(Material.HEAVY_WEIGHTED_PRESSURE_PLATE, "To Lower Case", this, TextType) { it.lowercase(Locale.getDefault()) },
        TypeExtraction(Material.GOLD_BLOCK, "To Upper Case", this, TextType) { it.uppercase(Locale.getDefault()) },
    )

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
    override val extractions: MutableList<TypeExtraction<Component, *>> = mutableListOf(
        TypeExtraction(Material.NAME_TAG, "As Text", this, TextType) { it.toString() },
        TypeExtraction(Material.INK_SAC, "Format MiniMessage", this, MessageType) { mm.deserialize(it.toString()) },
        TypeExtraction(Material.WHITE_DYE, "Strip Formatting", this, MessageType) { Component.text(mm.stripTags(it.toString())) },
        TypeExtraction(Material.HEAVY_WEIGHTED_PRESSURE_PLATE, "To Lower Case", this, MessageType) { Component.text(it.toString().lowercase(Locale.getDefault())) },
        TypeExtraction(Material.GOLD_BLOCK, "To Upper Case", this, MessageType) { Component.text(it.toString().uppercase(Locale.getDefault())) }
    )

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
    override val extractions: MutableList<TypeExtraction<Pos, *>> = mutableListOf(
        TypeExtraction(Material.NAME_TAG, "As String", this, TextType) { "(${shorten(it.x)}, ${shorten(it.y)}, ${shorten(it.z)}, ${shorten(it.pitch)}, ${shorten(it.yaw)})" },
        TypeExtraction(Material.COMPASS, "Positional Vector", this, VectorType) { Vec(it.x, it.y, it.z) },
        TypeExtraction(Material.PRISMARINE_SHARD, "Directional Vector", this, VectorType) { Vec(it.pitch.toDouble(), it.yaw.toDouble(), 0.0) },
        TypeExtraction(Material.CONDUIT, "Corner Position", this, PositionType) { Pos(it.x.toInt().toDouble(), it.y.toInt().toDouble(), it.z.toInt().toDouble(), it.pitch, it.yaw) },
        TypeExtraction(Material.HEAVY_CORE, "Center Position", this, PositionType) { Pos(it.x.toInt().toDouble() + 0.5, it.y.toInt().toDouble() + 0.5, it.z.toInt().toDouble() + 0.5, it.pitch, it.yaw) },
        TypeExtraction(Material.ARROW, "Reset Direction", this, PositionType) { Pos(it.x, it.y, it.z, 0.0f, 0.0f) },
        TypeExtraction(Material.RED_DYE, "X", this, NumberType) { it.x },
        TypeExtraction(Material.LIME_DYE, "Y", this, NumberType) { it.y },
        TypeExtraction(Material.CYAN_DYE, "Z", this, NumberType) { it.z },
        TypeExtraction(Material.MAGENTA_DYE, "Yaw", this, NumberType) { it.yaw.toDouble() },
        TypeExtraction(Material.PURPLE_DYE, "Pitch", this, NumberType) { it.pitch.toDouble() },
    )

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

object DictionaryType : GenericType {
    private val cache = WeakHashMap<ValueType<*>, WeakHashMap<ValueType<*>, Impl<*, *>>>()
    override fun create(generics: MutableMap<String, ValueType<*>>): Impl<*, *> = create(generics["Key"]!!, generics["Value"]!!)
    fun <K, V> create(key: ValueType<K>, value: ValueType<V>): Impl<K, V> = cache.computeIfAbsent(key) { WeakHashMap() }.computeIfAbsent(value) { Impl(key, value) } as Impl<K, V>

    override val generics = mapOf("Key" to AllTypes.dataOnly, "Value" to AllTypes.dataOnly)
    override val name: String = "Dictionary"
    override val material: Material = Material.COBWEB
    override val color: TextColor = NamedTextColor.WHITE

    class Impl<K, V>(private val key: ValueType<K>, private val value: ValueType<V>) : ValueType<DictionaryReference<K, V>>() {
        override val generics = mapOf("Key" to key, "Value" to value)
        override val generic = DictionaryType

        override fun deserialize(json: JsonElement, space: Space, objects: MutableMap<Int, Pair<Any?, JsonElement>>): DictionaryReference<K, V> {
            val id = json.asInt
            objects[id]?.first?.let { return it as DictionaryReference<K, V> }
            val jsonInfo = objects[id]!!.second
            val out = mutableMapOf<K, V>()
            for (i in 0..jsonInfo.asJsonArray.size() step 2) {
                out[key.deserialize(jsonInfo.asJsonArray[i], space, objects)] = value.deserialize(json.asJsonArray[i + 1], space, objects)
            }
            val res = DictionaryReference(key, value, out)
            objects[id] = res to jsonInfo
            return res
        }

        override fun serialize(v: DictionaryReference<K, V>, objects: MutableMap<Any?, Pair<Int, JsonElement>>): JsonElement {
            if (objects.containsKey(this)) return JsonPrimitive(objects[this]!!.first)
            val json = JsonArray()
            val id = objects.size
            objects[this] = id to json
            for (each in v.store) {
                json.add(key.serialize(each.key, objects))
                json.add(value.serialize(each.value, objects))
            }
            return JsonPrimitive(id)
        }

        override fun stringify(v: DictionaryReference<K, V>) = "Dictionary(${v.store.size} Entries)"

        override val name: String = "Dictionary(${key.name}, ${value.name})"
        override val color: TextColor = key.color
        override val material: Material = key.material

        override fun parse(str: String, space: Space) = null
        override fun validate(something: Any?) = if (something is DictionaryReference<*, *> && something.key == key && something.value == value) something as DictionaryReference<K, V> else null

        override fun compareEqual(left: DictionaryReference<K, V>?, right: DictionaryReference<K, V>?) = left != null && right != null && left.store == right.store
    }
}
data class DictionaryReference<K, V>(val key: ValueType<K>, val value: ValueType<V>, val store: MutableMap<K, V>)


object VectorType : ValueType<Vec>() {
    override val name: String = "Vector"
    override val color: TextColor = NamedTextColor.AQUA
    override val material: Material = Material.PRISMARINE_SHARD
    override val extractions: MutableList<TypeExtraction<Vec, *>> = mutableListOf(
        TypeExtraction(Material.NAME_TAG, "As String", this, TextType) { "<${shorten(it.x)}, ${shorten(it.y)}, ${shorten(it.z)}>" },
        TypeExtraction(Material.COMPASS, "As Position", this, PositionType) { Pos(it.x, it.y, it.z, 0.0f, 0.0f) },
        TypeExtraction(Material.STONE, "Normalize", this, VectorType) { it.normalize() },
        TypeExtraction(Material.REDSTONE, "Invert", this, VectorType) { it.mul(-1.0) },
        TypeExtraction(Material.RED_DYE, "X", this, NumberType) { it.x },
        TypeExtraction(Material.LIME_DYE, "Y", this, NumberType) { it.y },
        TypeExtraction(Material.CYAN_DYE, "Z", this, NumberType) { it.z },
        TypeExtraction(Material.SPECTRAL_ARROW, "Magnitude", this, NumberType) { it.length() }
    )

    override fun parse(str: String, space: Space) = null
    override fun compareEqual(left: Vec?, right: Vec?) = left is Vec && right is Vec && left == right
    override fun validate(something: Any?) = if (something is Vec) something else null
    override fun deserialize(json: JsonElement, space: Space, objects: MutableMap<Int, Pair<Any?, JsonElement>>): Vec {
        return Vec(
            json.asJsonObject.get("x").asDouble,
            json.asJsonObject.get("y").asDouble,
            json.asJsonObject.get("z").asDouble
        )
    }

    override fun stringify(v: Vec) = "Vector(${shorten(v.x)}, ${shorten(v.y)}, ${shorten(v.z)})"

    private fun shorten(num: Number) = (num.toDouble() * 1000.0).roundToInt() / 1000.0

    override fun serialize(v: Vec, objects: MutableMap<Any?, Pair<Int, JsonElement>>): JsonElement {
        val obj = JsonObject()
        obj.addProperty("x", v.x)
        obj.addProperty("y", v.y)
        obj.addProperty("z", v.z)
        return obj
    }
}
