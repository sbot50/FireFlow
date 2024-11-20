package de.blazemcworld.fireflow.code.node;

import de.blazemcworld.fireflow.code.node.impl.*;

import java.util.Set;

public class NodeList {

    public static Set<Node> nodes = Set.of(
            new AddNumbersNode(),
            new DebugValuesNode(), //needed until insets are implemented
            new IfNode(),
            new NumberToTextNode(),
            new OnPlayerJoinNode(),
            new ScheduleNode(),
            new SendMessageNode()
    );

}
