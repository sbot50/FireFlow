package de.blazemcworld.fireflow.space;

import de.blazemcworld.fireflow.code.CodeEditor;
import de.blazemcworld.fireflow.code.CodeEvaluator;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.instance.IChunkLoader;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.anvil.AnvilLoader;
import net.minestom.server.instance.block.Block;

public class Space {

    public final int id;
    public final InstanceContainer play = MinecraftServer.getInstanceManager().createInstanceContainer();
    public final InstanceContainer code = MinecraftServer.getInstanceManager().createInstanceContainer();
    public final CodeEditor editor;
    private CodeEvaluator evaluator;

    public Space(int id) {
        this.id = id;

        play.setTimeRate(0);
        play.setChunkSupplier(LightingChunk::new);
        play.setChunkLoader(new AnvilLoader("spaces/" + id + "/world"));

        code.setTimeRate(0);
        code.setChunkSupplier(LightingChunk::new);
        code.setChunkLoader(IChunkLoader.noop());

        play.setGenerator((unit) -> {
            if (Math.abs(unit.absoluteStart().x() + 8) > 16) return;
            if (Math.abs(unit.absoluteStart().z() + 8) > 16) return;
            unit.modifier().fillHeight(-1, 0, Block.SMOOTH_STONE);
        });

        code.setGenerator(unit -> {
            if (unit.absoluteStart().z() != 16.0) return;
            unit.modifier().fill(
                    new BlockVec(0, 0, 0).add(unit.absoluteStart()),
                    new BlockVec(16, 128, 1).add(unit.absoluteStart()),
                    Block.POLISHED_BLACKSTONE
            );
        });

        editor = new CodeEditor(this);
        evaluator = new CodeEvaluator(this);
    }

    public void reload() {
        evaluator.stop();
        evaluator = new CodeEvaluator(this);
    }
}
