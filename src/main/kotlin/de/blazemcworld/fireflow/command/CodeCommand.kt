package de.blazemcworld.fireflow.command

import de.blazemcworld.fireflow.database.table.PlayersTable
import de.blazemcworld.fireflow.database.table.SpaceRolesTable
import de.blazemcworld.fireflow.space.SpaceManager
import de.blazemcworld.fireflow.util.sendError
import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.CommandExecutor
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Player
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object CodeCommand : Command("code", "dev") {
    init {
        defaultExecutor = CommandExecutor exec@{ sender, _ ->
            if (sender !is Player) return@exec

            val space = SpaceManager.currentSpace(sender)

            if (space == null) {
                sender.sendError("You need to be in a space to do this!")
                return@exec
            }
            if (space.codeInstance == sender.instance) {
                sender.sendError("You are already coding on this space!")
                return@exec
            }

            val role = transaction {
                val result = SpaceRolesTable.join(PlayersTable, JoinType.INNER, SpaceRolesTable.player, PlayersTable.id)
                    .selectAll().where((SpaceRolesTable.space eq space.id) and (PlayersTable.uuid eq sender.uuid))
                    .adjustSelect { select(SpaceRolesTable.role) }
                if (result.empty()) return@transaction null
                result.single()[SpaceRolesTable.role]
            }

            if (role != SpaceRolesTable.Role.OWNER && role != SpaceRolesTable.Role.CONTRIBUTOR) {
                sender.sendError("You are not allowed to do that!")
                return@exec
            }

            sender.setInstance(space.codeInstance, Pos.ZERO)
        }
    }
}