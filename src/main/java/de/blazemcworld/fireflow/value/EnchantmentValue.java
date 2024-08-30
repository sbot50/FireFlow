package de.blazemcworld.fireflow.value;

import de.blazemcworld.fireflow.compiler.NodeCompiler;
import de.blazemcworld.fireflow.compiler.instruction.Instruction;
import de.blazemcworld.fireflow.compiler.instruction.MultiInstruction;
import de.blazemcworld.fireflow.compiler.instruction.RawInstruction;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.item.enchant.Enchantment;
import net.minestom.server.network.NetworkBuffer;
import net.minestom.server.registry.DynamicRegistry;
import net.minestom.server.utils.NamespaceID;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.List;
import java.util.Objects;

import static de.blazemcworld.fireflow.util.CamelCase.camelCase;
import static de.blazemcworld.fireflow.util.Levenshtein.calculateAndSmartSort;
import static de.blazemcworld.fireflow.util.Levenshtein.smartSort;

public class EnchantmentValue implements Value {
    public static final Value INSTANCE = new EnchantmentValue();
    public static final DynamicRegistry<Enchantment> REGISTRY = MinecraftServer.getEnchantmentRegistry();

    private EnchantmentValue() {
    }

    @Override
    public String getBaseName() {
        return "Enchantment";
    }

    @Override
    public TextColor getColor() {
        return NamedTextColor.DARK_PURPLE;
    }

    @Override
    public Type getType() {
        return Type.getType(DynamicRegistry.Key.class);
    }

    @Override
    public boolean typeCheck(Object value) {
        return value instanceof NamespaceID;
    }

    @Override
    public InsnList compile(NodeCompiler ctx, Object inset) {
        InsnList out = new InsnList();
        String enchant = NamespaceID.from("minecraft", "protection").asString();
        if (inset instanceof NamespaceID casted) enchant = casted.asString();
        out.add(new FieldInsnNode(Opcodes.GETSTATIC, "de/blazemcworld/fireflow/value/EnchantmentValue", "REGISTRY", "Lnet/minestom/server/registry/DynamicRegistry;"));
        out.add(new InsnNode(Opcodes.DUP));
        out.add(new LdcInsnNode(enchant));
        out.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minestom/server/utils/NamespaceID", "from", "(Ljava/lang/String;)Lnet/minestom/server/utils/NamespaceID;"));
        out.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "net/minestom/server/registry/DynamicRegistry", "get", "(Lnet/minestom/server/utils/NamespaceID;)Ljava/lang/Object;"));
        out.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "net/minestom/server/registry/DynamicRegistry", "getKey", "(Ljava/lang/Object;)Lnet/minestom/server/registry/DynamicRegistry$Key;"));
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
                        new TypeInsnNode(Opcodes.INSTANCEOF, "net/minestom/server/registry/DynamicRegistry$Key"),
                        new JumpInsnNode(Opcodes.IFGT, cast),
                        new InsnNode(Opcodes.POP),
                        new FieldInsnNode(Opcodes.GETSTATIC, "de/blazemcworld/fireflow/value/EnchantmentValue", "REGISTRY", "Lnet/minestom/server/registry/DynamicRegistry;"),
                        new InsnNode(Opcodes.DUP),
                        new LdcInsnNode("minecraft:protection"),
                        new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minestom/server/utils/NamespaceID", "from", "(Ljava/lang/String;)Lnet/minestom/server/utils/NamespaceID;"),
                        new MethodInsnNode(Opcodes.INVOKEINTERFACE, "net/minestom/server/registry/DynamicRegistry", "get", "(Lnet/minestom/server/utils/NamespaceID;)Ljava/lang/Object;"),
                        new MethodInsnNode(Opcodes.INVOKEINTERFACE, "net/minestom/server/registry/DynamicRegistry", "getKey", "(Ljava/lang/Object;)Lnet/minestom/server/registry/DynamicRegistry$Key;"),
                        new JumpInsnNode(Opcodes.GOTO, end),
                        cast,
                        new TypeInsnNode(Opcodes.CHECKCAST, "net/minestom/server/registry/DynamicRegistry$Key"),
                        end
                )
        );
    }

    @Override
    public Instruction wrapPrimitive(Instruction value) {
        return value;
    }

    @Override
    public String formatInset(Object inset) {
        if (!(inset instanceof NamespaceID)) return String.valueOf(inset);
        return camelCase(((NamespaceID) inset).asString().split(":")[1].replaceAll("_", " "));
    }

    @Override
    public NamespaceID prepareInset(String message) {
        if (REGISTRY.get(NamespaceID.from("minecraft", message)) == null) return null;
        return NamespaceID.from("minecraft", message);
    }

    @Override
    public List<String> getSuggestions(String message) {
        List<String> list = REGISTRY.values().stream()
                .map(REGISTRY::getKey)
                .filter(Objects::nonNull)
                .map(key -> camelCase(key.name().split(":")[1].replaceAll("_", " ")))
                .toList();
        if (list.size() > 30) list = smartSort(message, list.toArray(String[]::new)).subList(0, 30);
        list = calculateAndSmartSort(message, list.toArray(String[]::new));
        if (list.size() > 5) list = list.subList(0, 5);
        return list;
    }

    @Override
    public boolean canInset() {
        return true;
    }

    @Override
    public void writeInset(NetworkBuffer buffer, Object inset) {
        String enchant = NamespaceID.from("minecraft", "protection").asString();
        if (inset instanceof NamespaceID casted) enchant = casted.asString();
        buffer.write(NetworkBuffer.STRING, enchant);
    }

    @Override
    public Object readInset(NetworkBuffer buffer) {
        return NamespaceID.from(buffer.read(NetworkBuffer.STRING));
    }
}
