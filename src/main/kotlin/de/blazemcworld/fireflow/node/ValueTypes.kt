package de.blazemcworld.fireflow.node

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
}

object PlayerType : ValueType<PlayerReference>() {
    override val name = "Player"
    override val color: TextColor = NamedTextColor.GOLD
    override val material: Material = Material.PLAYER_HEAD

    override fun parse(str: String, space: Space) = kotlin.runCatching { PlayerReference(UUID.fromString(str), space) }.getOrNull()
    override fun compareEqual(left: PlayerReference?, right: PlayerReference?) = left is PlayerReference && right is PlayerReference && left.uuid == right.uuid
    override fun validate(something: Any?) = if (something is PlayerReference) something else null
}
class PlayerReference(val uuid: UUID, private val space: Space) {
    constructor(player: Player, space: Space) : this(player.uuid, space)

    fun resolve() = space.playInstance.getPlayerByUuid(uuid)
}

object SignalType : ValueType<Void>() {
    override val name = "Signal"
    override val color: TextColor = NamedTextColor.AQUA
    override val material: Material = Material.LIGHT_BLUE_DYE
    override fun parse(str: String, space: Space) = null
    override fun compareEqual(left: Void?, right: Void?) = false
    override fun validate(something: Any?) = null
}

object NumberType : ValueType<Double>() {
    override val name = "Number"
    override val color: TextColor = NamedTextColor.RED
    override val material: Material = Material.SLIME_BALL

    override fun parse(str: String, space: Space) = str.toDoubleOrNull()
    override fun compareEqual(left: Double?, right: Double?) = left == right
    override fun validate(something: Any?) = if (something is Double) something else null
}


object ConditionType : ValueType<Boolean>() {
    override val name = "Condition"
    override val color: TextColor = NamedTextColor.LIGHT_PURPLE
    override val material: Material = Material.ANVIL

    override fun parse(str: String, space: Space) = str == "true"
    override fun compareEqual(left: Boolean?, right: Boolean?) = left == right
    override fun validate(something: Any?) = if (something is Boolean) something else null
}

object TextType : ValueType<String>() {
    override val name = "Text"
    override val color: TextColor = NamedTextColor.GREEN
    override val material: Material = Material.BOOK

    override fun parse(str: String, space: Space) = str
    override fun compareEqual(left: String?, right: String?) = left == right
    override fun validate(something: Any?) = if (something is String) something else null
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

    override fun parse(str: String, space: Space) = mm.deserialize(str)
    override fun compareEqual(left: Component?, right: Component?) = left is Component && right is Component && mm.serialize(left) == mm.serialize(right)
    override fun validate(something: Any?) = if (something is Component) something else null
}

object PositionType : ValueType<Pos>() {
    override val name: String = "Position"
    override val color: TextColor = NamedTextColor.YELLOW
    override val material: Material = Material.FILLED_MAP

    override fun parse(str: String, space: Space) = null
    override fun compareEqual(left: Pos?, right: Pos?) = left is Pos && right is Pos
            && left.x == right.x && left.y == right.y && left.z == right.z && left.yaw == right.yaw && left.pitch == right.pitch
    override fun validate(something: Any?) = if (something is Pos) something else null
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
        override val name: String = "List(${type.name})"
        override val color: TextColor = type.color
        override val material: Material = type.material

        override fun parse(str: String, space: Space) = null
        override fun validate(something: Any?) = if (something is ListReference<*> && something.type == type) something as ListReference<T> else null

        override fun compareEqual(left: ListReference<T>?, right: ListReference<T>?) = left != null && right != null && left.store.size == right.store.size && left.store.indices.all { type.compareEqual(left.store[it], right.store[it]) }
    }
}
class ListReference<T>(val type: ValueType<T>, val store: MutableList<T>)