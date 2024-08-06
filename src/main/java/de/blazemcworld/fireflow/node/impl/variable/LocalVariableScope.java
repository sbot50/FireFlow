package de.blazemcworld.fireflow.node.impl.variable;

import de.blazemcworld.fireflow.compiler.CompiledNode;
import de.blazemcworld.fireflow.compiler.instruction.GetInstanceFieldInstruction;
import de.blazemcworld.fireflow.compiler.instruction.Instruction;
import de.blazemcworld.fireflow.compiler.instruction.RawInstruction;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.Map;

public class LocalVariableScope implements VariableScope {
    public static final LocalVariableScope INSTANCE = new LocalVariableScope();
    private LocalVariableScope() {}

    @Override
    public String getName() {
        return "Local";
    }

    @Override
    public Instruction getStore() {
        return new GetInstanceFieldInstruction(CompiledNode.class,
                new RawInstruction(Type.getType(CompiledNode.class), new VarInsnNode(Opcodes.ALOAD, 0)),
                "locals", Type.getType(Map.class)
        );
    }
}
