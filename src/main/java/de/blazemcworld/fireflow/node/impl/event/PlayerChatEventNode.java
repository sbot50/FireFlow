package de.blazemcworld.fireflow.node.impl.event;

import de.blazemcworld.fireflow.compiler.CompiledNode;
import de.blazemcworld.fireflow.compiler.instruction.InstanceMethodInstruction;
import de.blazemcworld.fireflow.compiler.instruction.RawInstruction;
import de.blazemcworld.fireflow.evaluation.CodeEvaluator;
import de.blazemcworld.fireflow.node.Node;
import de.blazemcworld.fireflow.node.NodeOutput;
import de.blazemcworld.fireflow.value.PlayerValue;
import de.blazemcworld.fireflow.value.SignalValue;
import de.blazemcworld.fireflow.value.TextValue;
import it.unimi.dsi.fastutil.Pair;
import net.minestom.server.event.player.PlayerChatEvent;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.List;

public class PlayerChatEventNode extends Node {

    private final NodeOutput signal;
    private final NodeOutput player;
    private final NodeOutput message;

    public PlayerChatEventNode() {
        super("Player Chat");

        signal = output("Signal", SignalValue.INSTANCE);
        player = output("Player", PlayerValue.INSTANCE);
        message = output("Message", TextValue.INSTANCE);

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

        message.setInstruction(TextValue.INSTANCE.cast(
                new InstanceMethodInstruction(CompiledNode.class,
                        new RawInstruction(Type.getType(CompiledNode.class), new VarInsnNode(Opcodes.ALOAD, 0)),
                        "getInternalVar", Type.getType(Object.class),
                        List.of(Pair.of(
                                        Type.getType(String.class),
                                        new RawInstruction(Type.getType(String.class), new LdcInsnNode(message.id))
                                )
                        )
                )
        ));
    }

    @Override
    public void register(CodeEvaluator evaluator) {
        String entrypoint = evaluator.compiler.markRoot(signal);
        evaluator.events.addListener(PlayerChatEvent.class, event  -> {
            CompiledNode context = evaluator.newContext();
            context.setInternalVar(player.id, new PlayerValue.Reference(evaluator.space, event.getPlayer()));
            context.setInternalVar(message.id, event.getMessage());
            context.emit(entrypoint);
         });
    }
}
