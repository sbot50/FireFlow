package de.blazemcworld.fireflow.code;

import de.blazemcworld.fireflow.code.node.Node;
import de.blazemcworld.fireflow.code.type.SignalType;
import de.blazemcworld.fireflow.space.Space;
import de.blazemcworld.fireflow.util.Translations;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Player;

import java.util.HashMap;
import java.util.WeakHashMap;

public class CodeDebugger {

    private final HashMap<String, Group> groups = new HashMap<>();
    public final Space space;

    public CodeDebugger(Space space) {
        this.space = space;
    }

    public boolean isActive() {
        return !groups.isEmpty();
    }

    public Group getGroup(String id) {
        return groups.get(id);
    }

    public Group getOrNewGroup(String id) {
        return groups.computeIfAbsent(id, Group::new);
    }

    public void removeGroup(String id) {
        groups.remove(id);
    }

    public void onSignal(Node.Output<Void> signal, CodeThread ctx) {
        for (Group g : groups.values()) {
            g.onSignal(signal, ctx);
        }
    }

    public class Group {
        public final WeakHashMap<Player, Boolean> players = new WeakHashMap<>();
        public final WeakHashMap<Node.Output<?>, Boolean> outputs = new WeakHashMap<>();
        public final String id;

        public Group(String id) {
            this.id = id;
        }

        public void onSignal(Node.Output<Void> signal, CodeThread ctx) {
            if (players.isEmpty() || outputs.isEmpty()) {
                removeGroup(id);
                return;
            }
            boolean match = false;

            for (Node.Output<?> out : outputs.keySet()) {
                if (out.getNode() == signal.getNode().clonedFrom && out.id.equals(signal.id)) {
                    match = true;
                    break;
                }
            }

            if (!match) return;

            for (Player p : players.keySet()) {
                p.sendMessage(Component.text(Translations.get("info.debug.on_signal", id, signal.getName(), signal.getNode().getTitle())).color(NamedTextColor.AQUA));
            }
            for (Node.Output<?> out : outputs.keySet()) {
                if (out.type == SignalType.INSTANCE) continue;

                Node check = out.getNode();
                for (Node n : space.evaluator.nodes) {
                    if (n.clonedFrom != check) continue;
                    for (Node.Output<?> o : n.outputs) {
                        if (!o.id.equals(out.id)) continue;

                        String str = o.type.stringify(o.computeNow(ctx));
                        for (Player p : players.keySet()) {
                            p.sendMessage(Component.text(Translations.get("info.debug.value", str, out.getName(), out.getNode().getTitle())).color(NamedTextColor.AQUA));
                        }
                    }
                }
            }
        }
    }
}
