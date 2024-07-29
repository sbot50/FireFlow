package de.blazemcworld.fireflow.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import de.blazemcworld.fireflow.Config
import de.blazemcworld.fireflow.database.table.PlayersTable
import de.blazemcworld.fireflow.database.table.SpaceRolesTable
import de.blazemcworld.fireflow.database.table.SpacesTable
import net.minestom.server.entity.Player
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseHelper {

    fun onJoin(player: Player) {
        transaction {
            val results = PlayersTable.selectAll().where(PlayersTable.uuid eq player.uuid).adjustSelect {
                return@adjustSelect select(PlayersTable.uuid, PlayersTable.name, PlayersTable.id)
            }
            if (results.empty()) {
                PlayersTable.insert {
                    it[name] = player.username
                    it[uuid] = player.uuid
                }
                return@transaction
            }
            val info = results.single()
            if (info[PlayersTable.name] != player.username) {
                PlayersTable.update({ PlayersTable.uuid eq player.uuid }) {
                    it[name] = player.username
                }
            }
        }
    }

    fun ownedSpaces(player: Player) = SpaceRolesTable.join(SpacesTable, JoinType.INNER, SpaceRolesTable.space, SpacesTable.id)
        .join(PlayersTable, JoinType.INNER, PlayersTable.id, SpaceRolesTable.player)
        .selectAll().where((PlayersTable.uuid eq player.uuid) and (SpaceRolesTable.role eq SpaceRolesTable.Role.OWNER))

    fun init() {
        Database.connect(HikariDataSource(HikariConfig().apply {
            jdbcUrl = Config.store.database.url
            driverClassName = Config.store.database.driver
            username = Config.store.database.user
            password = Config.store.database.password
        }))
    }
}