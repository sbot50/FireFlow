package de.blazemcworld.fireflow.value;

import de.blazemcworld.fireflow.compiler.NodeCompiler;
import de.blazemcworld.fireflow.compiler.instruction.Instruction;
import de.blazemcworld.fireflow.compiler.instruction.MultiInstruction;
import de.blazemcworld.fireflow.compiler.instruction.RawInstruction;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.item.ItemStack;
import net.minestom.server.network.NetworkBuffer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

public class ItemValue implements Value {
    public static final Value INSTANCE = new ItemValue();

    private ItemValue() {
    }

    @Override
    public String getBaseName() {
        return "Item";
    }

    @Override
    public TextColor getColor() {
        return TextColor.color(0xF6FF5E);
    }

    @Override
    public Type getType() {
        return Type.getType(ItemStack.class);
    }

    @Override
    public InsnList compile(NodeCompiler ctx, Object inset) {
        InsnList out = new InsnList();
        out.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minestom/server/item/ItemStack", "AIR", "Lnet/minestom/server/item/ItemStack;"));
        return out;
    }

    @Override
    public Instruction cast(Instruction value) {
        LabelNode cast = new LabelNode();
        LabelNode end = new LabelNode();

        return new MultiInstruction(Type.getType(ItemStack.class),
                value,
                new RawInstruction(Type.getType(ItemStack.class),
                        new InsnNode(Opcodes.DUP),
                        new TypeInsnNode(Opcodes.INSTANCEOF, "net/minestom/server/item/ItemStack"),
                        new JumpInsnNode(Opcodes.IFGT, cast),
                        new InsnNode(Opcodes.POP),
                        new FieldInsnNode(Opcodes.GETSTATIC, "net/minestom/server/item/ItemStack", "AIR", "Lnet/minestom/server/item/ItemStack;"),
                        new JumpInsnNode(Opcodes.GOTO, end),
                        cast,
                        new TypeInsnNode(Opcodes.CHECKCAST, "net/minestom/server/item/ItemStack"),
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
        return null;
    }

    @Override
    public void writeInset(NetworkBuffer buffer, Object inset) {
        throw new IllegalStateException("Item inputs can not be inset!");
    }

    @Override
    public Object readInset(NetworkBuffer buffer) {
        throw new IllegalStateException("Item inputs can not be inset!");
    }
}
