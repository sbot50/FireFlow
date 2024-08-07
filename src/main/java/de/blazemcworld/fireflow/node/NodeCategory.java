package de.blazemcworld.fireflow.node;

import de.blazemcworld.fireflow.node.impl.AddNumbersNode;
import de.blazemcworld.fireflow.node.impl.WhileNode;
import de.blazemcworld.fireflow.node.impl.variable.GetVariableNode;
import de.blazemcworld.fireflow.node.impl.variable.LocalVariableScope;
import de.blazemcworld.fireflow.node.impl.variable.SetVariableNode;
import de.blazemcworld.fireflow.value.NumberValue;
import it.unimi.dsi.fastutil.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class NodeCategory {

    public static final NodeCategory ROOT = new NodeCategory("Root", null, List.of(
            AddNumbersNode::new,
            WhileNode::new
    ));

    public static final NodeCategory VARIABLES = new NodeCategory("Variables", ROOT, List.of(
            () -> new GetVariableNode(LocalVariableScope.INSTANCE, NumberValue.INSTANCE),
            () -> new SetVariableNode(LocalVariableScope.INSTANCE, NumberValue.INSTANCE)
    ));

    public final String name;
    public final @Nullable NodeCategory parent;
    public final List<Pair<String, Supplier<Node>>> nodes = new ArrayList<>();
    public final List<NodeCategory> subcategories = new ArrayList<>();
    public NodeCategory(String name, @Nullable NodeCategory parent, List<Supplier<Node>> nodes) {
        this.name = name;
        this.parent = parent;

        for (Supplier<Node> s : nodes) {
            this.nodes.add(Pair.of(s.get().name, s));
        }

        if (parent != null) parent.subcategories.add(this);
    }
}
