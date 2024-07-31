package de.blazemcworld.fireflow.node.impl

import de.blazemcworld.fireflow.node.*

class ValueLiteralNode<T>(val type: ValueType<T>) : BaseNode(type.name + " Literal", type.material) {
    private val value = output("Value", type)

    override fun setup(ctx: NodeContext) {
        val v = type.parse(ctx.component.valueLiteral, ctx.global.space)
        ctx[value].defaultHandler = { v }
    }

    companion object {
        val all = setOf(
            ValueLiteralNode(NumberType),
            ValueLiteralNode(TextType),
            ValueLiteralNode(MessageType),
            ValueLiteralNode(ConditionType),
        )
    }
}