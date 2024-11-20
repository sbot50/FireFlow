package de.blazemcworld.fireflow.code;

import de.blazemcworld.fireflow.FireFlow;
import de.blazemcworld.fireflow.code.action.Action;
import de.blazemcworld.fireflow.code.node.Node;
import de.blazemcworld.fireflow.code.node.NodeList;
import de.blazemcworld.fireflow.code.type.AllTypes;
import de.blazemcworld.fireflow.code.type.WireType;
import de.blazemcworld.fireflow.code.widget.NodeIOWidget;
import de.blazemcworld.fireflow.code.widget.NodeMenuWidget;
import de.blazemcworld.fireflow.code.widget.NodeWidget;
import de.blazemcworld.fireflow.code.widget.Widget;
import de.blazemcworld.fireflow.code.widget.WireWidget;
import de.blazemcworld.fireflow.space.Space;
import de.blazemcworld.fireflow.util.PlayerExitInstanceEvent;
import de.blazemcworld.fireflow.util.Translations;
import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.other.InteractionMeta;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.entity.EntityAttackEvent;
import net.minestom.server.event.player.*;
import net.minestom.server.event.trait.InstanceEvent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class CodeEditor {

    public final Space space;
    public final Set<Widget> rootWidgets = new HashSet<>();
    public final HashMap<Player, Set<Widget>> lockedWidgets = new HashMap<>();
    private final HashMap<Player, Action> actions = new HashMap<>();
    private final Path codePath;

    public CodeEditor(Space space) {
        this.space = space;
        codePath = Path.of("spaces/" + space.info.id + "/code.json");

        EventNode<InstanceEvent> events = space.code.eventNode();

        events.addListener(PlayerSpawnEvent.class, event -> {
            Player player = event.getPlayer();

            player.setAllowFlying(true);
            player.setFlying(true);

            Entity interactionHelper = new Entity(EntityType.INTERACTION);
            interactionHelper.setNoGravity(true);
            InteractionMeta meta = (InteractionMeta) interactionHelper.getEntityMeta();
            meta.setWidth(-0.5f);
            meta.setHeight(-0.5f);
            interactionHelper.setInstance(event.getInstance(), Pos.ZERO);
            player.addPassenger(interactionHelper);

            lockedWidgets.put(player, new HashSet<>());
        });

        events.addListener(PlayerExitInstanceEvent.class, event -> {
            for (Entity passenger : event.getEntity().getPassengers()) {
                if (passenger.getEntityType() == EntityType.INTERACTION) passenger.remove();
            }

            if (actions.containsKey(event.getPlayer())) actions.get(event.getPlayer()).stop(this, event.getPlayer());
            actions.remove(event.getPlayer());
            lockedWidgets.remove(event.getPlayer());
        });

        events.addListener(PlayerEntityInteractEvent.class, event -> {
            handleInteraction(event.getPlayer(), Interaction.Type.RIGHT_CLICK);
        });
        events.addListener(PlayerSwapItemEvent.class, event -> {
            handleInteraction(event.getPlayer(), Interaction.Type.SWAP_HANDS);
        });
        events.addListener(EntityAttackEvent.class, event -> {
            if (event.getEntity() instanceof Player player) {
                handleInteraction(player, Interaction.Type.LEFT_CLICK);
            }
        });

        events.addListener(PlayerTickEvent.class, event -> {
            Action a = actions.get(event.getPlayer());
            if (a == null) return;
            Vec cursor = getCursor(event.getPlayer());
            cursor = cursor.withX(Math.round(cursor.x() * 8) / 8f)
                    .withY(Math.round(cursor.y() * 8) / 8f);
            a.tick(cursor, this, event.getPlayer());
        });

        events.addListener(PlayerChatEvent.class, event -> {
            Vec pos = getCursor(event.getPlayer()).mul(8).apply(Vec.Operator.CEIL).div(8).withZ(15.999);
            for (Widget w : rootWidgets) {
                if (w.getWidget(pos) instanceof NodeIOWidget input) {
                    if (!input.isInput()) return;
                    event.setCancelled(true);
                    if (input.type().parseInset(event.getMessage()) == null) {
                        event.getPlayer().sendMessage(Component.text(Translations.get("error.invalid.inset")));
                        return;
                    }

                    input.insetValue(event.getMessage());
                    input.update(space.code);
                    w.update(space.code);
                    return;
                }
            }
        });

        load();
    }

    private void handleInteraction(Player player, Interaction.Type type) {
        Vec pos = getCursor(player).mul(8).apply(Vec.Operator.CEIL).div(8).withZ(15.999);
        Interaction i = new Interaction(this, player, pos, type);

        if (actions.containsKey(player)) {
            actions.get(player).interact(i);
            return;
        }

        for (Widget w : new HashSet<>(rootWidgets)) {
            if (w.interact(i)) return;
        }

        if (type == Interaction.Type.RIGHT_CLICK) {
            NodeMenuWidget n = new NodeMenuWidget(NodeList.nodes);
            Vec s = n.getSize();
            n.setPos(pos.add(Math.round(s.x() * 4) / 8f, Math.round(s.y() * 4) / 8f, 0));
            n.update(space.code);
            rootWidgets.add(n);
        }
    }

    private Vec getCursor(Player player) {
        double norm = player.getPosition().direction().dot(new Vec(0, 0, -1));
        if (norm >= 0) return Vec.ZERO.withZ(15.999);
        Vec start = player.getPosition().asVec().add(0.0, player.getEyeHeight(), -16);
        double dist = -start.dot(new Vec(0, 0, -1)) / norm;
        if (dist < 0) return Vec.ZERO.withZ(15.999);
        Vec out = start.add(player.getPosition().direction().mul(dist)).withZ(15.999);
        if (out.y() > 99999) out = out.withY(99999);
        if (out.y() < -99999) out = out.withY(-99999);
        if (out.x() > 99999) out = out.withX(99999);
        if (out.x() < -99999) out = out.withX(-99999);
        return out;
    }

    public void unlockWidget(Widget widget, Player player) {
        lockedWidgets.computeIfAbsent(player, p -> new HashSet<>()).remove(widget);
    }

    public void unlockWidgets(Player player) {
        lockedWidgets.remove(player);
    }

    public boolean lockWidget(Widget widget, Player player) {
        for (Map.Entry<Player, Set<Widget>> entry : lockedWidgets.entrySet()) {
            if (entry.getValue().contains(widget)) return entry.getKey() == player;
        }
        lockedWidgets.computeIfAbsent(player, p -> new HashSet<>()).add(widget);
        return true;
    }

    public void setAction(Player player, Action action) {
        if (actions.containsKey(player)) {
            actions.get(player).stop(this, player);
        }
        actions.put(player, action);
    }

    public void stopAction(Player player) {
        if (actions.containsKey(player)) {
            actions.get(player).stop(this, player);
        }
        actions.remove(player);
    }

    public void save() {
        JsonObject data = new JsonObject();
        JsonArray nodes = new JsonArray();
        JsonArray wires = new JsonArray();
        
        List<NodeWidget> nodeWidgets = new ArrayList<>();
        for (Widget widget : rootWidgets) {
            if (widget instanceof NodeWidget nodeWidget) {
                nodeWidgets.add(nodeWidget);
            }
        }
        for (NodeWidget nodeWidget : nodeWidgets) {
            JsonObject entry = new JsonObject();
            entry.addProperty("type", nodeWidget.node.id);
            entry.addProperty("x", nodeWidget.getPos().x());
            entry.addProperty("y", nodeWidget.getPos().y());
            
            JsonObject insets = new JsonObject();
            for (NodeIOWidget io : nodeWidget.getIOWidgets()) {
                if (io.isInput() && io.input.inset != null) {
                    insets.addProperty(io.input.id, io.input.inset);
                }
            }
            if (!insets.isEmpty()) {
                entry.add("insets", insets);
            }

            JsonObject inputs = new JsonObject();
            JsonObject outputs = new JsonObject();
            for (NodeIOWidget io : nodeWidget.getIOWidgets()) {
                if (io.isInput() && io.input.connected != null) {
                    int nodeIndex = 0;
                    for (NodeWidget node : nodeWidgets) {
                        if (node.node != io.input.connected.getNode()) {
                            nodeIndex++;
                            continue;
                        }
                        int outputIndex = node.node.outputs.indexOf(io.input.connected);
                        inputs.addProperty(io.input.id, nodeIndex + ":" + outputIndex);
                        break;
                    }
                } else if (!io.isInput() && io.output.connected != null) {
                    int nodeIndex = 0;
                    for (NodeWidget node : nodeWidgets) {
                        if (node.node != io.output.connected.getNode()) {
                            nodeIndex++;
                            continue;
                        }
                        int inputIndex = node.node.inputs.indexOf(io.output.connected);
                        outputs.addProperty(io.output.id, nodeIndex + ":" + inputIndex);
                        break;
                    }
                }
            }
            if (!inputs.isEmpty()) {
                entry.add("inputs", inputs);
            }
            if (!outputs.isEmpty()) {
                entry.add("outputs", outputs);
            }

            nodes.add(entry);
        }
        
        List<WireWidget> wireWidgets = new ArrayList<>();
        for (Widget widget : rootWidgets) {
            if (widget instanceof WireWidget wireWidget) {
                wireWidgets.add(wireWidget);
            }
        }

        for (WireWidget wireWidget : wireWidgets) {
            JsonObject wireObj = new JsonObject();
            wireObj.addProperty("type", wireWidget.type().id());
            wireObj.addProperty("fromX", wireWidget.line.from.x());
            wireObj.addProperty("fromY", wireWidget.line.from.y());
            wireObj.addProperty("toX", wireWidget.line.to.x());
            wireObj.addProperty("toY", wireWidget.line.to.y());
            
            if (wireWidget.previousOutput != null) {
                wireObj.addProperty("previousOutputNode", nodeWidgets.indexOf(wireWidget.previousOutput.parent));
                wireObj.addProperty("previousOutputId", wireWidget.previousOutput.output.id);
            }

            JsonArray previousWires = new JsonArray();
            for (WireWidget previousWire : wireWidget.previousWires) {
                previousWires.add(wireWidgets.indexOf(previousWire));
            }
            if (!previousWires.isEmpty()) {
                wireObj.add("previousWires", previousWires);
            }
            
            if (wireWidget.nextInput != null) {
                wireObj.addProperty("nextInputNode", nodeWidgets.indexOf(wireWidget.nextInput.parent));
                wireObj.addProperty("nextInputId", wireWidget.nextInput.input.id);
            }

            JsonArray nextWires = new JsonArray();
            for (WireWidget nextWire : wireWidget.nextWires) {
                nextWires.add(wireWidgets.indexOf(nextWire));
            }
            if (!nextWires.isEmpty()) {
                wireObj.add("nextWires", nextWires);
            }
            
            wires.add(wireObj);
        }
        
        data.add("nodes", nodes);
        data.add("wires", wires);

        try {
            if (!Files.exists(codePath.getParent())) Files.createDirectories(codePath.getParent());
            Files.writeString(codePath, data.toString());
        } catch (IOException e) {
            FireFlow.LOGGER.error("Failed to save code.json for space " + space.info.id + "!", e);
        }
    }

    @SuppressWarnings("unchecked")
    public void load() {
        try {
            if (!Files.exists(codePath)) return;
            JsonObject data = JsonParser.parseString(Files.readString(codePath)).getAsJsonObject();
            
            JsonArray nodes = data.getAsJsonArray("nodes");
            List<NodeWidget> nodeWidgets = new ArrayList<>();
            
            Set<Runnable> todo = new HashSet<>();

            for (JsonElement nodeElem : nodes) {
                JsonObject nodeObj = nodeElem.getAsJsonObject();
                String type = nodeObj.get("type").getAsString();
                double x = nodeObj.get("x").getAsDouble();
                double y = nodeObj.get("y").getAsDouble();
                Node node = null;
                for (Node n : NodeList.nodes) {
                    if (n.id.equals(type)) {
                        node = n.copy();
                        break;
                    }
                }

                NodeWidget nodeWidget = new NodeWidget(node);
                nodeWidget.setPos(new Vec(x, y, 15.999));
                nodeWidgets.add(nodeWidget);
                rootWidgets.add(nodeWidget);
                
                if (nodeObj.has("insets")) {
                    JsonObject insets = nodeObj.getAsJsonObject("insets");
                    for (Map.Entry<String, JsonElement> entry : insets.entrySet()) {
                        String inputId = entry.getKey();
                        String inset = entry.getValue().getAsString();
                        
                        for (NodeIOWidget io : nodeWidget.getIOWidgets()) {
                            if (io.isInput() && io.input.id.equals(inputId)) {
                                io.insetValue(inset);
                                break;
                            }
                        }
                    }
                }

                todo.add(() -> {
                    if (nodeObj.has("inputs")) {
                        JsonObject inputs = nodeObj.getAsJsonObject("inputs");
                        for (Map.Entry<String, JsonElement> entry : inputs.entrySet()) {
                            String inputId = entry.getKey();
                            int nodeIndex = Integer.parseInt(entry.getValue().getAsString().split(":")[0]);
                            int outputId = Integer.parseInt(entry.getValue().getAsString().split(":")[1]);

                            NodeWidget other = nodeWidgets.get(nodeIndex);
                            for (Node.Input<?> input : nodeWidget.node.inputs) {
                                if (input.id.equals(inputId)) {
                                    ((Node.Input<Object>) input).connected = (Node.Output<Object>) other.node.outputs.get(outputId);
                                    break;
                                }
                            }
                        }
                    }
                    if (nodeObj.has("outputs")) {
                        JsonObject outputs = nodeObj.getAsJsonObject("outputs");
                        for (Map.Entry<String, JsonElement> entry : outputs.entrySet()) {
                            String outputId = entry.getKey();
                            int nodeIndex = Integer.parseInt(entry.getValue().getAsString().split(":")[0]);
                            int inputId = Integer.parseInt(entry.getValue().getAsString().split(":")[1]);

                            NodeWidget other = nodeWidgets.get(nodeIndex);
                            for (Node.Output<?> output : nodeWidget.node.outputs) {
                                if (output.id.equals(outputId)) {
                                    ((Node.Output<Object>) output).connected = (Node.Input<Object>) other.node.inputs.get(inputId);
                                    break;
                                }
                            }
                        }
                    }
                    nodeWidget.update(space.code);
                });
            }
            
            JsonArray wires = data.getAsJsonArray("wires");
            List<WireWidget> wireWidgets = new ArrayList<>();
            
            for (JsonElement wireElem : wires) {
                JsonObject wireObj = wireElem.getAsJsonObject();
                String type = wireObj.get("type").getAsString();
                double fromX = wireObj.get("fromX").getAsDouble();
                double fromY = wireObj.get("fromY").getAsDouble();
                double toX = wireObj.get("toX").getAsDouble();
                double toY = wireObj.get("toY").getAsDouble();
                
                WireType<?> typeInst = null;
                for (WireType<?> t : AllTypes.list) {
                    if (t.id().equals(type)) {
                        typeInst = t;
                        break;
                    }
                }
                WireWidget wire = new WireWidget(typeInst, new Vec(fromX, fromY, 15.999), new Vec(toX, toY, 15.999));
                if (wireObj.has("previousOutputNode") && wireObj.has("previousOutputId")) {
                    int nodeIndex = wireObj.get("previousOutputNode").getAsInt();
                    String outputId = wireObj.get("previousOutputId").getAsString();
                    for (NodeIOWidget io : nodeWidgets.get(nodeIndex).getIOWidgets()) {
                        if (!io.isInput() && io.output.id.equals(outputId)) {
                            wire.setPreviousOutput(io);
                            io.connections.add(wire);
                            break;
                        }
                    }
                }

                if (wireObj.has("nextInputId") && wireObj.has("nextInputNode")) {
                    int nodeIndex = wireObj.get("nextInputNode").getAsInt();
                    String inputId = wireObj.get("nextInputId").getAsString();
                    for (NodeIOWidget io : nodeWidgets.get(nodeIndex).getIOWidgets()) {
                        if (io.isInput() && io.input.id.equals(inputId)) {
                            wire.setNextInput(io);
                            io.connections.add(wire);
                            break;
                        }
                    }
                }

                todo.add(() -> {
                    if (wireObj.has("previousWires")) {
                        for (JsonElement previousWireElem : wireObj.getAsJsonArray("previousWires")) {
                            int index = previousWireElem.getAsInt();
                            wire.previousWires.add(wireWidgets.get(index));
                        }
                    }
                    if (wireObj.has("nextWires")) {
                        for (JsonElement nextWireElem : wireObj.getAsJsonArray("nextWires")) {
                            int index = nextWireElem.getAsInt();
                            wire.nextWires.add(wireWidgets.get(index));
                        }
                    }
                    wire.update(space.code);
                });

                wireWidgets.add(wire);
                rootWidgets.add(wire);
            }

            for (Runnable item : todo) {
                item.run();
            }
        } catch (IOException e) {
            FireFlow.LOGGER.error("Failed to load code.json for space " + space.info.id + "!", e);
        }
    }
}
