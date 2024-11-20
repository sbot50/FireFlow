package de.blazemcworld.fireflow.space;

import java.util.Set;
import java.util.UUID;

import net.minestom.server.item.Material;

public class SpaceInfo {

    public final int id;
    public String name;
    public Material icon;
    public UUID owner;
    public Set<UUID> contributors;

    public SpaceInfo(int id) {
        this.id = id;
    }

}
