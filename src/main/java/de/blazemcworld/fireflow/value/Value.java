package de.blazemcworld.fireflow.value;

import de.blazemcworld.fireflow.compiler.NodeCompiler;
import de.blazemcworld.fireflow.compiler.instruction.Instruction;
import net.kyori.adventure.text.format.TextColor;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.InsnList;

public interface Value {
    String getName();
    TextColor getColor();
    Type getType();
    InsnList compile(NodeCompiler ctx, Object inset);
    Instruction cast(Instruction value);
    Instruction wrapPrimitive(Instruction value);
}
