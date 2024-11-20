package de.blazemcworld.fireflow.code.node;

import java.util.Set;

import de.blazemcworld.fireflow.code.node.impl.AddNumbersNode;
import de.blazemcworld.fireflow.code.node.impl.IfNode;
import de.blazemcworld.fireflow.code.node.impl.NumberToTextNode;
import de.blazemcworld.fireflow.code.node.impl.OnPlayerJoinNode;
import de.blazemcworld.fireflow.code.node.impl.RepeatNode;
import de.blazemcworld.fireflow.code.node.impl.ScheduleNode;
import de.blazemcworld.fireflow.code.node.impl.SendMessageNode;

public class NodeList {

    public static Set<Node> nodes = Set.of(
            new AddNumbersNode(),
            new IfNode(),
            new NumberToTextNode(),
            new OnPlayerJoinNode(),
            new RepeatNode(),
            new ScheduleNode(),
            new SendMessageNode()
    );

}
