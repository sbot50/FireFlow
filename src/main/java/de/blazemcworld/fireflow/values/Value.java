package de.blazemcworld.fireflow.values;

import de.blazemcworld.fireflow.compiler.NodeCompiler;
import de.blazemcworld.fireflow.compiler.instruction.Instruction;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.InsnList;

public interface Value {
    Type getType();
    InsnList compile(NodeCompiler ctx, Object inset);
    Instruction cast(Instruction value);
    Instruction wrapPrimitive(Instruction value);
}
