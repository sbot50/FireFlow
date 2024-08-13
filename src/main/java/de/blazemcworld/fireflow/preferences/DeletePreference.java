package de.blazemcworld.fireflow.preferences;

import net.minestom.server.item.Material;

public class DeletePreference extends Preference {
    public DeletePreference() {
        super("preference-delete", "Show delete warning");
        states.add(new PreferenceState("On", Material.EMERALD_BLOCK));
        states.add(new PreferenceState("Off", Material.REDSTONE_BLOCK));
    }
}