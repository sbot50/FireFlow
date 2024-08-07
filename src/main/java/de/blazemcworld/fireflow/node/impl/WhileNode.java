package de.blazemcworld.fireflow.node.impl;

import de.blazemcworld.fireflow.compiler.instruction.CpuCheckInstruction;
import de.blazemcworld.fireflow.compiler.instruction.MultiInstruction;
import de.blazemcworld.fireflow.compiler.instruction.RawInstruction;
import de.blazemcworld.fireflow.node.Node;
import de.blazemcworld.fireflow.node.NodeInput;
import de.blazemcworld.fireflow.node.NodeOutput;
import de.blazemcworld.fireflow.value.ConditionValue;
import de.blazemcworld.fireflow.value.SignalValue;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;

public class WhileNode extends Node {
    public WhileNode() {
        super("While Loop");
        NodeInput signal = input("Signal", SignalValue.INSTANCE);
        NodeInput condition = input("Condition", ConditionValue.INSTANCE);
        NodeOutput loop = output("Loop", SignalValue.INSTANCE);
        NodeOutput next = output("Next", SignalValue.INSTANCE);

        LabelNode start = new LabelNode();
        LabelNode end = new LabelNode();
        signal.setInstruction(new MultiInstruction(
                Type.VOID_TYPE,
                new RawInstruction(Type.VOID_TYPE, start),
                new CpuCheckInstruction(),
                condition,
                new RawInstruction(Type.VOID_TYPE, new JumpInsnNode(Opcodes.IFLE, end)),
                loop,
                new RawInstruction(Type.VOID_TYPE, new JumpInsnNode(Opcodes.GOTO, start)),
                new RawInstruction(Type.VOID_TYPE, end),
                next
        ));
    }
}
