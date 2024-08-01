package de.blazemcworld.fireflow.node.impl

object NodeList {
    val all = setOf(
        AddNumbersNode,
        SubtractNumbersNode,
        MultiplyNumbersNode,
        DivideNumbersNode,
        ModuloNumbersNode,
        PowerNumbersNode,
        RandomNumberNode,
        EmptyListNode,
        ListAppendNode,
        ListGetNode,
        ListInsertNode,
        ListLengthNode,
        ListRemoveNode,
        EqualNode,
        FillBlocksNode,
        GetBlockNode,
        GreaterThanNode,
        IfNode,
        KillPlayerNode,
        OnPlayerChatNode,
        OnPlayerJoinNode,
        PackPositionNode,
        PlayerPositionNode,
        ScheduleNode,
        SendMessageNode,
        SetBlockNode,
        UnpackPositionNode,
    ) + ValueLiteralNode.all + VariableNodes.all
}