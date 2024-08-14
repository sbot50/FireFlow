package de.blazemcworld.fireflow.preferences;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.item.Material;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Preference {
    DELETE("Show deletion warning", new PreferenceState[]{
            new PreferenceState("On", Material.EMERALD_BLOCK),
            new PreferenceState("Off", Material.REDSTONE_BLOCK)
    })
    ;
    private final PreferenceState[] states;
    private final String desc;

    Preference(String desc, PreferenceState[] states) {
        this.desc = desc;
        this.states = states;
    }

    public Component getDesc() {
        return Component.text(desc)
                .color(NamedTextColor.GREEN)
                .decoration(TextDecoration.ITALIC, false);
    }

    public PreferenceState getState(int value) {
        return states[value];
    }

    public List<Component> getLore() {
        return Arrays.stream(states)
                .map(state -> Component.text("► " + state.name())
                        .color(NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public int decreaseState(int state) {
        return Math.floorMod(state - 1, states.length);
    }

    public int increaseState(int state) {
        return (state + 1) % states.length;
    }

    public record PreferenceState(String name, Material icon) {
    }
}