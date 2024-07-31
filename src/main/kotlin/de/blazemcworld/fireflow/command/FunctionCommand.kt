package de.blazemcworld.fireflow.command

import de.blazemcworld.fireflow.inventory.SelectionInventories
import de.blazemcworld.fireflow.node.AllTypes
import de.blazemcworld.fireflow.node.FunctionCallNode
import de.blazemcworld.fireflow.node.FunctionInputsNode
import de.blazemcworld.fireflow.node.FunctionOutputsNode
import de.blazemcworld.fireflow.space.SpaceManager
import de.blazemcworld.fireflow.util.sendError
import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.CommandExecutor
import net.minestom.server.command.builder.arguments.ArgumentLiteral
import net.minestom.server.command.builder.arguments.ArgumentString
import net.minestom.server.entity.Player

object FunctionCommand : Command("function") {
    init {
        addSyntax(CommandExecutor exec@{ sender, ctx ->
            if (sender !is Player) return@exec
            val space = SpaceManager.currentSpace(sender)

            if (space == null || space.codeInstance != sender.instance) {
                sender.sendError("You must be coding to use this!")
                return@exec
            }

            if (space.functions.any { it.first.fn == ctx.get("name") }) {
                sender.sendError("A function by that name already exists!")
                return@exec
            }

            val cursor = space.codeCursor(sender)
            val inputs = FunctionInputsNode(ctx.get("name"))
            val outputs = FunctionOutputsNode(ctx.get("name"))

            space.functions += inputs to outputs
            space.functionNodes += FunctionCallNode(inputs, outputs)
            space.codeNodes += inputs.component.also { it.pos = cursor; it.update(space.codeInstance) }
            space.codeNodes += outputs.component.also { it.pos = cursor; it.update(space.codeInstance) }
        }, ArgumentLiteral("create"), ArgumentString("name"))

        addSyntax(CommandExecutor exec@{ sender, ctx ->
            if (sender !is Player) return@exec
            val space = SpaceManager.currentSpace(sender)

            if (space == null || space.codeInstance != sender.instance) {
                sender.sendError("You must be coding to use this!")
                return@exec
            }

            val cursor = space.codeCursor(sender)
            space.codeNodes.find { it.node is FunctionInputsNode && it.includes(cursor) }?.let {
                if (it.node !is FunctionInputsNode) return@let

                if (it.outputs.any { i -> i.io.name.equals(ctx.get("name"), ignoreCase = true) }) {
                    sender.sendError("Function input by name already exists!")
                    return@exec
                }

                if (space.codeNodes.any { other -> other.node is FunctionCallNode && other.node.fn == it.node.fn }) {
                    sender.sendError("Function must not be in use!")
                    return@exec
                }

                SelectionInventories.selectValueType(sender, "Input Type", AllTypes.all) { type ->
                    it.node.add(ctx.get("name"), type)
                    space.functionNodes.removeIf { f -> f.fn == it.node.fn }
                    space.functions.find { p -> p.first.fn == it.node.fn }?.let { r -> space.functionNodes += FunctionCallNode(r.first, r.second) }
                    it.update(space.codeInstance)

                }

                return@exec
            }
            sender.sendError("You must be looking at a function inputs node for this!")
        }, ArgumentLiteral("add"), ArgumentLiteral("input"), ArgumentString("name"))

        addSyntax(CommandExecutor exec@{ sender, ctx ->
            if (sender !is Player) return@exec
            val space = SpaceManager.currentSpace(sender)

            if (space == null || space.codeInstance != sender.instance) {
                sender.sendError("You must be coding to use this!")
                return@exec
            }

            val cursor = space.codeCursor(sender)
            space.codeNodes.find { it.node is FunctionOutputsNode && it.includes(cursor) }?.let {
                if (it.node !is FunctionOutputsNode) return@let

                if (it.inputs.any { i -> i.io.name.equals(ctx.get("name"), ignoreCase = true) }) {
                    sender.sendError("Function output by name already exists!")
                    return@exec
                }

                SelectionInventories.selectValueType(sender, "Input Type", AllTypes.all) { type ->
                    it.node.add(ctx.get("name"), type)
                    space.functionNodes.removeIf { f -> f.fn == it.node.fn }
                    space.functions.find { p -> p.first.fn == it.node.fn }?.let { r -> space.functionNodes += FunctionCallNode(r.first, r.second) }
                    it.update(space.codeInstance)
                }
                return@exec
            }
            sender.sendError("You must be looking at a function outputs node for this!")
        }, ArgumentLiteral("add"), ArgumentLiteral("output"), ArgumentString("name"))

        addSyntax(CommandExecutor exec@{ sender, ctx ->
            if (sender !is Player) return@exec
            val space = SpaceManager.currentSpace(sender)

            if (space == null || space.codeInstance != sender.instance) {
                sender.sendError("You must be coding to use this!")
                return@exec
            }

            val cursor = space.codeCursor(sender)
            space.codeNodes.find { it.node is FunctionInputsNode && it.includes(cursor) }?.let {
                if (it.node !is FunctionInputsNode) return@let

                if (!it.outputs.any { o -> o.io.name.equals(ctx.get("name"), ignoreCase = true) }) {
                    sender.sendError("Function input by name doesn't exist!")
                    return@exec
                }

                if (space.codeNodes.any { other -> other.node is FunctionCallNode && other.node.fn.equals(ctx.get("name"), ignoreCase=true) }) {
                    sender.sendError("Function must not be in use!")
                    return@exec
                }

                it.node.remove(ctx.get("name"))
                space.functionNodes.removeIf { f -> f.fn == it.node.fn }
                space.functions.find { p -> p.first.fn == it.node.fn }?.let { r -> space.functionNodes += FunctionCallNode(r.first, r.second) }
                it.update(space.codeInstance)

                return@exec
            }
            sender.sendError("You must be looking at a function inputs node for this!")
        }, ArgumentLiteral("remove"), ArgumentLiteral("input"), ArgumentString("name"))

        addSyntax(CommandExecutor exec@{ sender, ctx ->
            if (sender !is Player) return@exec
            val space = SpaceManager.currentSpace(sender)

            if (space == null || space.codeInstance != sender.instance) {
                sender.sendError("You must be coding to use this!")
                return@exec
            }

            val cursor = space.codeCursor(sender)
            space.codeNodes.find { it.node is FunctionOutputsNode && it.includes(cursor) }?.let {
                if (it.node !is FunctionOutputsNode) return@let

                if (!it.inputs.any { o -> o.io.name.equals(ctx.get("name"), ignoreCase = true) }) {
                    sender.sendError("Function output by name doesn't exists!")
                    return@exec
                }

                if (space.codeNodes.any { other -> other.node is FunctionCallNode && other.node.fn == it.node.fn }) {
                    sender.sendError("Function must not be in use!")
                    return@exec
                }

                it.node.remove(ctx.get("name"))
                space.functionNodes.removeIf { f -> f.fn == it.node.fn }
                space.functions.find { p -> p.first.fn == it.node.fn }?.let { r -> space.functionNodes += FunctionCallNode(r.first, r.second) }
                it.update(space.codeInstance)

                return@exec
            }
            sender.sendError("You must be looking at a function outputs node for this!")
        }, ArgumentLiteral("remove"), ArgumentLiteral("output"), ArgumentString("name"))
    }
}