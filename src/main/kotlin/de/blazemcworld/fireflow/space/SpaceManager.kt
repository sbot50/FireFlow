package de.blazemcworld.fireflow.space

import de.blazemcworld.fireflow.Config
import de.blazemcworld.fireflow.database.DatabaseHelper
import de.blazemcworld.fireflow.database.table.PlayersTable
import de.blazemcworld.fireflow.database.table.SpaceRolesTable
import de.blazemcworld.fireflow.database.table.SpacesTable
import net.kyori.adventure.text.Component
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Player
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.transaction

object SpaceManager {

    private val loadedSpaces =  mutableMapOf<Int, Space>()

    fun createSpace(owner: Player): Int? {
        var createdId: Int? = null
        transaction {
            if (DatabaseHelper.ownedSpaces(owner).count() >= Config.store.limits.spacesPerPlayer) return@transaction

            val spaceId = SpacesTable.insertAndGetId {
                it[title] = Component.text(owner.username + "'s Space")
            }
            val playerId = PlayersTable.select(PlayersTable.uuid eq owner.uuid)
                .adjustSelect { select(PlayersTable.id) }.single()
            createdId = SpaceRolesTable.insertAndGetId {
                it[player] = playerId[PlayersTable.id].value
                it[space] = spaceId.value
                it[role] = SpaceRolesTable.Role.OWNER
            }.value
        }
        return createdId
    }

    fun sendToSpace(player: Player, id: Int) {
        player.setInstance(getOrLoadSpace(id).playInstance, Pos.ZERO)
    }

    private fun getOrLoadSpace(id: Int): Space {
        return loadedSpaces[id] ?: loadSpace(id)
    }

    private fun loadSpace(id: Int): Space {
        val space = Space(id)
        loadedSpaces[id] = space
        return space
    }

    fun forget(id: Int) = loadedSpaces.remove(id)

    fun currentSpace(player: Player) = loadedSpaces.values.find {
        player.instance == it.playInstance || player.instance == it.codeInstance
    }
}