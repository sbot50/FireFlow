package de.blazemcworld.fireflow.database.table

import org.jetbrains.exposed.dao.id.IntIdTable

object PlayersTable : IntIdTable("players") {
    val name = varchar("name", 16)
    val uuid = uuid("uuid")
}