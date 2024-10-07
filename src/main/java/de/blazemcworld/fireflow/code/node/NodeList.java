package de.blazemcworld.fireflow.code.node;

import de.blazemcworld.fireflow.code.node.impl.AddNumbersNode;
import de.blazemcworld.fireflow.code.node.impl.SendMessageNode;

import java.util.Set;

public class NodeList {

    public static Set<Node> nodes = Set.of(
            new AddNumbersNode(),
            new SendMessageNode()
    );

}
