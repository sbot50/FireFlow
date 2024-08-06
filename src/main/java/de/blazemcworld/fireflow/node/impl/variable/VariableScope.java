package de.blazemcworld.fireflow.node.impl.variable;

import de.blazemcworld.fireflow.compiler.instruction.Instruction;

public interface VariableScope {
    String getName();
    Instruction getStore();
}
