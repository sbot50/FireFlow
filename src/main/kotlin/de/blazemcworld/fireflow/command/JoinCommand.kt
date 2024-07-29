package de.blazemcworld.fireflow.command

import de.blazemcworld.fireflow.database.table.SpacesTable
import de.blazemcworld.fireflow.space.SpaceManager
import de.blazemcworld.fireflow.util.sendError
import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.arguments.number.ArgumentInteger
import net.minestom.server.entity.Player
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object JoinCommand : Command("join") {
    init {
        addSyntax(exec@{ sender, ctx ->
            if (sender !is Player) return@exec
            val id = ctx.get<Int>("space_id")

            if (SpaceManager.currentSpace(sender)?.id == id) {
                sender.sendError("You are already on this space!")
                return@exec
            }

            var found = false
            transaction {
                found = !SpacesTable.selectAll().where(SpacesTable.id eq id).empty()
            }

            if (!found) {
                sender.sendError("Unknown space id!")
                return@exec
            }

            SpaceManager.sendToSpace(sender, id)
        }, ArgumentInteger("space_id"))
    }
}