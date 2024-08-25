package de.blazemcworld.fireflow.value;

import de.blazemcworld.fireflow.compiler.NodeCompiler;
import de.blazemcworld.fireflow.compiler.instruction.Instruction;
import de.blazemcworld.fireflow.compiler.instruction.MultiInstruction;
import de.blazemcworld.fireflow.compiler.instruction.RawInstruction;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.item.Material;
import net.minestom.server.network.NetworkBuffer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

public class MaterialValue implements Value {
    public static final Value INSTANCE = new MaterialValue();

    private MaterialValue() {
    }

    @Override
    public String getBaseName() {
        return "Material";
    }

    @Override
    public TextColor getColor() {
        return TextColor.color(0xffc348);
    }

    @Override
    public Type getType() {
        return Type.getType(Material.class);
    }

    @Override
    public InsnList compile(NodeCompiler ctx, Object inset) {
        InsnList out = new InsnList();
        Material mat = Material.AIR;
        if (inset instanceof Material casted) mat = casted;
        out.add(new LdcInsnNode(mat.namespace().asString()));
        out.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "net/minestom/server/item/Material", "fromNamespaceId", "(Ljava/lang/Object;)Lnet/minestom/server/item/Material;"));
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
                        new TypeInsnNode(Opcodes.INSTANCEOF, "net/minestom/server/item/Material"),
                        new JumpInsnNode(Opcodes.IFGT, cast),
                        new InsnNode(Opcodes.POP),
                        new FieldInsnNode(Opcodes.GETSTATIC, "net/minestom/server/item/Material", "AIR", "Lnet/minestom/server/item/Material;"),
                        new JumpInsnNode(Opcodes.GOTO, end),
                        cast,
                        new TypeInsnNode(Opcodes.CHECKCAST, "net/minestom/server/item/Material"),
                        end
                )
        );
    }

    @Override
    public Instruction wrapPrimitive(Instruction value) {
        return value;
    }

    @Override
    public Object prepareInset(String value) {
        value = value.replaceAll(" ", "_").toLowerCase();
        for (Material material : Material.values()) {
            if (material.name().equals(value)) {
                return material;
            }
        }
        return null;
    }

    @Override
    public boolean canInset() {
        return true;
    }

    @Override
    public void writeInset(NetworkBuffer buffer, Object inset) {
        String mat = Material.AIR.namespace().asString();
        if (inset instanceof Material casted) mat = casted.namespace().asString();
        buffer.write(NetworkBuffer.STRING, mat);
    }

    @Override
    public Object readInset(NetworkBuffer buffer) {
        return Material.fromNamespaceId(buffer.read(NetworkBuffer.STRING));
    }
}
