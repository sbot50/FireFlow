package de.blazemcworld.fireflow.value;

import de.blazemcworld.fireflow.compiler.NodeCompiler;
import de.blazemcworld.fireflow.compiler.instruction.Instruction;
import de.blazemcworld.fireflow.compiler.instruction.MultiInstruction;
import de.blazemcworld.fireflow.compiler.instruction.RawInstruction;
import de.blazemcworld.fireflow.util.TextCase;
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
        return value instanceof DynamicRegistry.Key<?>;
    }

    @Override
    public InsnList compile(NodeCompiler ctx, Object inset) {
        InsnList out = new InsnList();
        String enchant = NamespaceID.from("minecraft", "protection").asString();
        if (inset instanceof DynamicRegistry.Key<?> casted) enchant = casted.namespace().asString();
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
        if (!(inset instanceof DynamicRegistry.Key<?>)) return String.valueOf(inset);
        return TextCase.namespaceToName(((DynamicRegistry.Key<?>) inset).namespace());
    }

    @Override
    public DynamicRegistry.Key<Enchantment> prepareInset(String message) {
        Enchantment enchant = REGISTRY.get(NamespaceID.from("minecraft", message));
        if (enchant == null) return null;
        return REGISTRY.getKey(enchant);
    }

    @Override
    public List<String> getSuggestions(String message) {
        List<String> list = REGISTRY.values().stream()
                .map(REGISTRY::getKey)
                .filter(Objects::nonNull)
                .map(key -> TextCase.namespaceToName(key.namespace()))
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
        if (inset instanceof DynamicRegistry.Key<?> casted) enchant = casted.namespace().asString();
        buffer.write(NetworkBuffer.STRING, enchant);
    }

    @Override
    public Object readInset(NetworkBuffer buffer) {
        Enchantment enchant = REGISTRY.get(NamespaceID.from(buffer.read(NetworkBuffer.STRING)));
        if (enchant == null) return Enchantment.PROTECTION;
        return REGISTRY.getKey(enchant);
    }
}
