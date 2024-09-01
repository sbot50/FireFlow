package de.blazemcworld.fireflow.value;

import de.blazemcworld.fireflow.compiler.NodeCompiler;
import de.blazemcworld.fireflow.compiler.instruction.Instruction;
import de.blazemcworld.fireflow.compiler.instruction.MultiInstruction;
import de.blazemcworld.fireflow.compiler.instruction.RawInstruction;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.network.NetworkBuffer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

public class TextValue implements Value {

    public static TextValue INSTANCE = new TextValue();
    private TextValue() {}

    @Override
    public String getBaseName() {
        return "Text";
    }

    @Override
    public TextColor getColor() {
        return NamedTextColor.YELLOW;
    }

    @Override
    public boolean typeCheck(Object value) {
        return value instanceof String;
    }

    @Override
    public Type getType() {
        return Type.getType(String.class);
    }

    @Override
    public InsnList compile(NodeCompiler ctx, Object inset) {
        InsnList out = new InsnList();
        out.add(new LdcInsnNode(String.valueOf(inset)));
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
                        new TypeInsnNode(Opcodes.INSTANCEOF, "java/lang/String"),
                        new JumpInsnNode(Opcodes.IFGT, cast),
                        new InsnNode(Opcodes.POP),
                        new LdcInsnNode(""),
                        new JumpInsnNode(Opcodes.GOTO, end),
                        cast,
                        new TypeInsnNode(Opcodes.CHECKCAST, "java/lang/String"),
                        end
                )
        );
    }

    @Override
    public Instruction wrapPrimitive(Instruction value) {
        return value;
    }

    @Override
    public Object prepareInset(String message) {
        return message;
    }

    @Override
    public boolean canInset() {
        return true;
    }

    @Override
    public void writeInset(NetworkBuffer buffer, Object inset) {
        buffer.write(NetworkBuffer.STRING, (String) inset);
    }

    @Override
    public Object readInset(NetworkBuffer buffer) {
        return buffer.read(NetworkBuffer.STRING);
    }
}
