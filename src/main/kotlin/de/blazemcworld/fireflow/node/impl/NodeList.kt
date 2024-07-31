package de.blazemcworld.fireflow.node.impl

object NodeList {
    val all = setOf(
        AddNumbersNode,
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