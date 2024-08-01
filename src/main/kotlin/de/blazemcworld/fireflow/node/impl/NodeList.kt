package de.blazemcworld.fireflow.node.impl

object NodeList {
    val all = setOf(
        OnPlayerChatNode,
        OnPlayerJoinNode,
        AddNumbersNode,
        SubtractNumbersNode,
        MultiplyNumbersNode,
        DivideNumbersNode,
        ModuloNumbersNode,
        PowerNumbersNode,
        RandomNumberNode,
        EmptyListNode,
        ConcatNode,
        SubstringNode,
        ToStringNode(),
        FormatMiniMessageNode,
        ToMessageNode(),
        ListLengthNode,
        ListAppendNode,
        ListInsertNode,
        ListGetNode,
        ListRemoveNode,
        IfNode,
        EqualNode,
        GreaterThanNode,
        FillBlocksNode,
        GetBlockNode,
        KillPlayerNode,
        PackPositionNode,
        UnpackPositionNode,
        PlayerPositionNode,
        ScheduleNode,
        SendMessageNode,
        ActionBarNode,
        SetBlockNode
    ) + ValueLiteralNode.all + VariableNodes.all
}