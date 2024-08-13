package de.blazemcworld.fireflow.preferences;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.item.Material;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Preference {
    protected List<PreferenceState> states = new ArrayList<>();
    private final String desc;
    private final String name;

    public Preference(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public Component getDesc() {
            return Component.text(desc)
            .color(NamedTextColor.GREEN)
            .decoration(TextDecoration.ITALIC, false);
    }

    public PreferenceState getState(int value) {
        return states.get(value);
    }

    public List<Component> getLore() {
        return states.stream()
                .map(state -> Component.text("► " + state.name())
                        .color(NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public int decreaseState(int state) {
        if (state - 1 < 0) return (states.size() - 1);
        return (state - 1);
    }

    public int increaseState(int state) {
        if (state + 1 > states.size() - 1) return 0;
        return (state + 1);
    }

    public record PreferenceState(String name, Material icon) {}
}