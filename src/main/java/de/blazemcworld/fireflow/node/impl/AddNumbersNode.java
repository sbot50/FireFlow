package de.blazemcworld.fireflow.node.impl;

import de.blazemcworld.fireflow.compiler.instruction.MultiInstruction;
import de.blazemcworld.fireflow.compiler.instruction.RawInstruction;
import de.blazemcworld.fireflow.node.Node;
import de.blazemcworld.fireflow.node.NodeInput;
import de.blazemcworld.fireflow.node.NodeOutput;
import de.blazemcworld.fireflow.values.NumberValue;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.InsnNode;

public class AddNumbersNode extends Node {
    public AddNumbersNode() {
        super("Add Numbers");
        NodeInput left = input("Left", NumberValue.INSTANCE);
        NodeInput right = input("Right", NumberValue.INSTANCE);
        NodeOutput result = output("Result", NumberValue.INSTANCE);
        
        result.setInstruction(new MultiInstruction(
                Type.DOUBLE_TYPE, left, right,
                new RawInstruction(Type.DOUBLE_TYPE, new InsnNode(Opcodes.DADD))
        ));
    }
}
