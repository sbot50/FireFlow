package de.blazemcworld.fireflow.node.impl

object NodeList {
    val all = setOf(
        AddNumbersNode,
        DivideNumbersNode,
        EmptyListNode,
        EqualNode,
        FillBlocksNode,
        GetBlockNode,
        GreaterThanNode,
        IfNode,
        KillPlayerNode,
        ListAppendNode,
        ListGetNode,
        ListInsertNode,
        ListLengthNode,
        ListRemoveNode,
        MultiplyNumbersNode,
        OnPlayerChatNode,
        OnPlayerJoinNode,
        PackPositionNode,
        PlayerPositionNode,
        ScheduleNode,
        SendMessageNode,
        SetBlockNode,
        SubtractNumbersNode,
        UnpackPositionNode,
    ) + ValueLiteralNode.all + VariableNodes.all
}