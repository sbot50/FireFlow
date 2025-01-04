package de.blazemcworld.fireflow.code.node.impl.variable;

import de.blazemcworld.fireflow.code.VariableStore;
import de.blazemcworld.fireflow.code.node.Node;
import de.blazemcworld.fireflow.code.type.AllTypes;
import de.blazemcworld.fireflow.code.type.SignalType;
import de.blazemcworld.fireflow.code.type.StringType;
import de.blazemcworld.fireflow.code.type.WireType;
import de.blazemcworld.fireflow.util.Translations;
import net.minestom.server.item.Material;

import java.util.List;

public class SetVariableNode<T> extends Node {

    private final WireType<T> type;

    public SetVariableNode(WireType<T> type) {
        super("set_variable", Material.IRON_BLOCK);
        this.type = type;

        Input<Void> signal = new Input<>("signal", SignalType.INSTANCE);
        Input<String> name = new Input<>("name", StringType.INSTANCE);
        Input<String> scope = new Input<>("scope", StringType.INSTANCE)
                .options("thread", "session", "saved");
        Input<T> value = new Input<>("value", type);
        Output<Void> next = new Output<>("next", SignalType.INSTANCE);

        signal.onSignal((ctx) -> {
            VariableStore store = switch (scope.getValue(ctx)) {
                case "saved" -> ctx.evaluator.space.savedVariables;
                case "session" -> ctx.evaluator.sessionVariables;
                case "thread" -> ctx.threadVariables;
                default -> null;
            };
            if (store != null) store.set(name.getValue(ctx), type, value.getValue(ctx));
            ctx.sendSignal(next);
        });
    }

    @Override
    public String getTitle() {
        if (type == null) return Translations.get("node.set_variable.base_title");
        return Translations.get("node.set_variable.title", type.getName());
    }

    @Override
    public Node copy() {
        return new SetVariableNode<>(type);
    }

    @Override
    public int getTypeCount() {
        return 1;
    }

    @Override
    public List<WireType<?>> getTypes() {
        return List.of(type);
    }

    @Override
    public boolean acceptsType(WireType<?> type, int index) {
        return AllTypes.isValue(type);
    }

    @Override
    public Node copyWithTypes(List<WireType<?>> types) {
        return new SetVariableNode<>(types.get(0));
    }
}
