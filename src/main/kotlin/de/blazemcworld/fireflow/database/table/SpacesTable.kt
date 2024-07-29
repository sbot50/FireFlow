package de.blazemcworld.fireflow.database.table

import de.blazemcworld.fireflow.database.SimpleStrSerialization
import kotlinx.serialization.json.Json
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import net.minestom.server.item.Material
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.json.jsonb

object SpacesTable : IntIdTable("worlds") {
    val title = jsonb("title", Json, SimpleStrSerialization(
        to = { GsonComponentSerializer.gson().serialize(it) },
        from = { GsonComponentSerializer.gson().deserialize(it) }
    ))
    val icon = jsonb("icon", Json, SimpleStrSerialization(
        to = { it.namespace().path() },
        from = { Material.fromNamespaceId(it) ?: Material.PAPER }
    )).default(Material.PAPER)
}