package de.blazemcworld.fireflow.compiler.instruction;

import de.blazemcworld.fireflow.compiler.NodeCompiler;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public record StaticMethodInstruction(Class<?> owner, String method, Type returnType, Map<Type, Instruction> arguments) implements Instruction {

    @Override
    public void prepare(NodeCompiler ctx) {}

    @Override
    public InsnList compile(NodeCompiler ctx) {
        InsnList out = new InsnList();
        List<Type> paramTypes = new ArrayList<>();
        for (Map.Entry<Type, Instruction> arg : arguments.entrySet()) {
            paramTypes.add(arg.getKey());
            out.add(ctx.compile(arg.getValue()));
        }
        out.add(new MethodInsnNode(Opcodes.INVOKESTATIC, owner.getName().replace('.', '/'), method, Type.getMethodDescriptor(returnType, paramTypes.toArray(new Type[0]))));
        return out;
    }

    @Override
    public Type returnType() {
        return returnType;
    }
}
