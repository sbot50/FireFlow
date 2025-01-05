package de.blazemcworld.fireflow.code.node.impl.number;

import de.blazemcworld.fireflow.code.node.Node;
import de.blazemcworld.fireflow.code.type.NumberType;
import de.blazemcworld.fireflow.code.type.StringType;
import de.blazemcworld.fireflow.code.type.VectorType;
import fastnoiselite.FastNoiseLite;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.item.Material;

public class BasicNoiseNode extends Node {
    public BasicNoiseNode() {
        super("basic_noise", Material.DEAD_BRAIN_CORAL_BLOCK);
        Input<String> noiseType = new Input<>("noise_type", StringType.INSTANCE).options("Simplex", "SmoothSimplex", "Perlin", "Value", "ValueCubic");
        Input<String> dimension = new Input<>("dimension", StringType.INSTANCE).options("3D", "2D");
        Input<Vec> position = new Input<>("position", VectorType.INSTANCE);
        Input<Double> frequency = new Input<>("frequency", NumberType.INSTANCE);
        Input<Double> octaves = new Input<>("octaves", NumberType.INSTANCE);
        Input<Double> gain = new Input<>("gain", NumberType.INSTANCE);
        Input<Double> lacunarity = new Input<>("lacunarity", NumberType.INSTANCE);
        Input<Double> seed = new Input<>("seed", NumberType.INSTANCE);
        Output<Double> output = new Output<>("output", NumberType.INSTANCE);

        output.valueFrom((ctx) -> {
            FastNoiseLite.NoiseType noiseType1 = switch (noiseType.getValue(ctx)) {
                case "Simplex" -> FastNoiseLite.NoiseType.OpenSimplex2;
                case "SmoothSimplex" -> FastNoiseLite.NoiseType.OpenSimplex2S;
                case "Perlin" -> FastNoiseLite.NoiseType.Perlin;
                case "Value" -> FastNoiseLite.NoiseType.Value;
                case "ValueCubic" -> FastNoiseLite.NoiseType.ValueCubic;
                default -> null;
            };
            if (noiseType1 != null) {
                FastNoiseLite noise = new FastNoiseLite();
                noise.SetNoiseType(noiseType1);
                noise.SetFractalGain(gain.getValue(ctx).floatValue());
                noise.SetFractalLacunarity(lacunarity.getValue(ctx).floatValue());
                noise.SetFractalOctaves(octaves.getValue(ctx).intValue());
                noise.SetFrequency(frequency.getValue(ctx).floatValue());
                noise.SetSeed(seed.getValue(ctx).intValue());
                Vec loc = position.getValue(ctx);
                switch (dimension.getValue(ctx)) {
                    case "3D" -> {
                        return (double) noise.GetNoise(loc.x(), loc.y(), loc.z());
                    }
                    case "2D" -> {
                        return (double) noise.GetNoise(loc.x(), loc.z());
                    }
                    default -> {
                        return 0.0;
                    }
                }
            }
            return 0.0;
        });
    }

    @Override
    public Node copy() {
        return new BasicNoiseNode();
    }
}