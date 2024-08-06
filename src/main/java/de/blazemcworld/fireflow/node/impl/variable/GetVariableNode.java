package de.blazemcworld.fireflow.node.impl.variable;

import de.blazemcworld.fireflow.compiler.instruction.InstanceMethodInstruction;
import de.blazemcworld.fireflow.compiler.instruction.MultiInstruction;
import de.blazemcworld.fireflow.node.Node;
import de.blazemcworld.fireflow.node.NodeInput;
import de.blazemcworld.fireflow.node.NodeOutput;
import de.blazemcworld.fireflow.value.TextValue;
import de.blazemcworld.fireflow.value.Value;
import it.unimi.dsi.fastutil.Pair;
import org.objectweb.asm.Type;

import java.util.List;
import java.util.Map;

public class GetVariableNode extends Node {
    public GetVariableNode(VariableScope scope, Value type) {
        super("Get " + scope.getName() + " Variable");
        NodeInput name = input("Name", TextValue.INSTANCE);
        NodeOutput value = output("Value", type);

        value.setInstruction(new MultiInstruction(type.getType(),
                type.cast(new InstanceMethodInstruction(Map.class, scope.getStore(), "get",
                        Type.getType(Object.class),
                        List.of(Pair.of(Type.getType(Object.class), name))
                ))
        ));
    }
}
