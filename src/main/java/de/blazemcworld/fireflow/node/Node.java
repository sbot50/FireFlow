package de.blazemcworld.fireflow.node;

import de.blazemcworld.fireflow.value.Value;

import java.util.ArrayList;
import java.util.List;

public class Node {

    public List<NodeInput> inputs = new ArrayList<>();
    public List<NodeOutput> outputs = new ArrayList<>();
    public final String name;

    public Node(String name) {
        this.name = name;
    }

    protected NodeInput input(String name, Value value) {
        NodeInput input = new NodeInput(name, value);
        inputs.add(input);
        return input;
    }

    protected NodeOutput output(String name, Value value) {
        NodeOutput output = new NodeOutput(name, value);
        outputs.add(output);
        return output;
    }
}
