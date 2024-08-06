package de.blazemcworld.fireflow.values;

import de.blazemcworld.fireflow.compiler.NodeCompiler;
import de.blazemcworld.fireflow.compiler.instruction.Instruction;
import de.blazemcworld.fireflow.compiler.instruction.MultiInstruction;
import de.blazemcworld.fireflow.compiler.instruction.RawInstruction;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

public class ConditionValue implements Value {

    public static ConditionValue INSTANCE = new ConditionValue();
    private ConditionValue() {}

    @Override
    public Type getType() {
        return Type.BOOLEAN_TYPE;
    }

    @Override
    public InsnList compile(NodeCompiler ctx, Object inset) {
        InsnList out = new InsnList();
        out.add(new LdcInsnNode(inset == Boolean.TRUE));
        return out;
    }

    @Override
    public Instruction cast(Instruction value) {
        LabelNode end = new LabelNode();
        return new MultiInstruction(getType(),
                value,
                new RawInstruction(getType(),
                        new InsnNode(Opcodes.DUP),
                        new FieldInsnNode(Opcodes.GETSTATIC, "java/lang/Boolean", "TRUE", "Ljava/lang/Boolean;"),
                        new JumpInsnNode(Opcodes.IF_ACMPEQ, end),
                        new InsnNode(Opcodes.POP),
                        new FieldInsnNode(Opcodes.GETSTATIC, "java/lang/Boolean", "FALSE", "Ljava/lang/Boolean;"),
                        end
                )
        );
    }

    @Override
    public Instruction wrapPrimitive(Instruction value) {
        return new MultiInstruction(Type.getType(Double.class),
                value,
                new RawInstruction(Type.getType(Double.class),
                        new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;")
                )
        );
    }
}
