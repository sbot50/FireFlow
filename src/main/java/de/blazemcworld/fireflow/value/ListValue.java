package de.blazemcworld.fireflow.value;

import de.blazemcworld.fireflow.compiler.NodeCompiler;
import de.blazemcworld.fireflow.compiler.instruction.Instruction;
import de.blazemcworld.fireflow.compiler.instruction.MultiInstruction;
import de.blazemcworld.fireflow.compiler.instruction.RawInstruction;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.List;
import java.util.WeakHashMap;

public class ListValue implements Value {

    private static final WeakHashMap<Value, ListValue> cache = new WeakHashMap<>();
    private final Value type;

    private ListValue(Value type) {
        this.type = type;
    }

    public static ListValue get(Value type) {
        return cache.computeIfAbsent(type, ListValue::new);
    }

    @Override
    public Type getType() {
        return Type.getType(List.class);
    }

    @Override
    public InsnList compile(NodeCompiler ctx, Object inset) {
        if (inset != null) throw new IllegalStateException("List values can not be inset!");
        InsnList out = new InsnList();
        out.add(new TypeInsnNode(Opcodes.NEW, "java/util/ArrayList"));
        out.add(new InsnNode(Opcodes.DUP));
        out.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V", false));
        return out;
    }

    @Override
    public Instruction cast(Instruction value) {
        LabelNode cast = new LabelNode();
        LabelNode end = new LabelNode();
        return new MultiInstruction(getType(),
                value,
                new RawInstruction(getType(),
                        new InsnNode(Opcodes.DUP),
                        new TypeInsnNode(Opcodes.INSTANCEOF, "java/util/List"),
                        new JumpInsnNode(Opcodes.IFGT, cast),
                        new InsnNode(Opcodes.POP),
                        new TypeInsnNode(Opcodes.NEW, "java/util/ArrayList"),
                        new InsnNode(Opcodes.DUP),
                        new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V", false),
                        new JumpInsnNode(Opcodes.GOTO, end),
                        cast,
                        new TypeInsnNode(Opcodes.CHECKCAST, "java/util/List"),
                        end
                )
        );
    }

    @Override
    public Instruction wrapPrimitive(Instruction value) {
        return value;
    }


}
