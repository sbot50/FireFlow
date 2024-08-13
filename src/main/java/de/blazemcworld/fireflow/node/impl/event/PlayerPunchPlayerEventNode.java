package de.blazemcworld.fireflow.node.impl.event;

import de.blazemcworld.fireflow.compiler.CompiledNode;
import de.blazemcworld.fireflow.compiler.instruction.InstanceMethodInstruction;
import de.blazemcworld.fireflow.compiler.instruction.RawInstruction;
import de.blazemcworld.fireflow.evaluation.CodeEvaluator;
import de.blazemcworld.fireflow.node.Node;
import de.blazemcworld.fireflow.node.NodeOutput;
import de.blazemcworld.fireflow.value.PlayerValue;
import de.blazemcworld.fireflow.value.SignalValue;
import it.unimi.dsi.fastutil.Pair;
import net.minestom.server.entity.Player;
import net.minestom.server.event.entity.EntityAttackEvent;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.List;

public class PlayerPunchPlayerEventNode extends Node {

    private final NodeOutput signalOutput;
    private final NodeOutput playerOutput;
    private final NodeOutput otherOutput;

    public PlayerPunchPlayerEventNode() {
        super("Player Punch Player");

        signalOutput = output("Signal", SignalValue.INSTANCE);
        playerOutput = output("Player", PlayerValue.INSTANCE);
        otherOutput = output("Other", PlayerValue.INSTANCE);

        playerOutput.setInstruction(PlayerValue.INSTANCE.cast(
                new InstanceMethodInstruction(CompiledNode.class,
                        new RawInstruction(Type.getType(CompiledNode.class), new VarInsnNode(Opcodes.ALOAD, 0)),
                        "getInternalVar", Type.getType(Object.class),
                        List.of(Pair.of(
                                Type.getType(String.class),
                                new RawInstruction(Type.getType(String.class), new LdcInsnNode(playerOutput.id))
                        ))
                )
        ));

        otherOutput.setInstruction(PlayerValue.INSTANCE.cast(
                new InstanceMethodInstruction(CompiledNode.class,
                        new RawInstruction(Type.getType(CompiledNode.class), new VarInsnNode(Opcodes.ALOAD, 0)),
                        "getInternalVar", Type.getType(Object.class),
                        List.of(Pair.of(
                                Type.getType(String.class),
                                new RawInstruction(Type.getType(String.class), new LdcInsnNode(otherOutput.id))
                        ))
                )
        ));
    }

    @Override
    public void register(CodeEvaluator evaluator) {
        String entrypoint = evaluator.compiler.markRoot(signalOutput);
        evaluator.events.addListener(EntityAttackEvent.class, event -> {
            if (event.getEntity() instanceof Player player) {
                if (event.getTarget() instanceof Player other) {
                    CompiledNode context = evaluator.newContext();
                    context.setInternalVar(playerOutput.id, new PlayerValue.Reference(evaluator.space, player));
                    context.setInternalVar(otherOutput.id, new PlayerValue.Reference(evaluator.space, other));
                    context.emit(entrypoint);
                }
            }
        });
    }
}
