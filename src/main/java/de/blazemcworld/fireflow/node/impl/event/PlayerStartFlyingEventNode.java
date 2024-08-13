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
import net.minestom.server.event.player.PlayerStartFlyingEvent;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.List;

public class PlayerStartFlyingEventNode extends Node {

    private final NodeOutput player;
    private final NodeOutput signal;

    public PlayerStartFlyingEventNode() {
        super("Player Start Flying");

        signal = output("Signal", SignalValue.INSTANCE);
        player = output("Player", PlayerValue.INSTANCE);

        player.setInstruction(PlayerValue.INSTANCE.cast(
                new InstanceMethodInstruction(CompiledNode.class,
                        new RawInstruction(Type.getType(CompiledNode.class), new VarInsnNode(Opcodes.ALOAD, 0)),
                        "getInternalVar", Type.getType(Object.class),
                        List.of(Pair.of(
                                Type.getType(String.class),
                                new RawInstruction(Type.getType(String.class), new LdcInsnNode(player.id))
                        ))
                )
        ));
    }

    @Override
    public void register(CodeEvaluator evaluator) {
        String entrypoint = evaluator.compiler.markRoot(signal);
        evaluator.events.addListener(PlayerStartFlyingEvent.class, event -> {
            CompiledNode context = evaluator.newContext();
            context.setInternalVar(player.id, new PlayerValue.Reference(evaluator.space, event.getPlayer()));
            context.emit(entrypoint);
        });
    }
}
