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
    private final VariableStore.Scope scope;

    public SetVariableNode(WireType<T> type, VariableStore.Scope scope) {
        super("set_variable_" + scope.id, Material.IRON_BLOCK);
        this.type = type;
        this.scope = scope;

        Input<Void> signal = new Input<>("signal", SignalType.INSTANCE);
        Input<String> name = new Input<>("name", StringType.INSTANCE);
        Input<T> value = new Input<>("value", type);
        Output<Void> next = new Output<>("next", SignalType.INSTANCE);

        signal.onSignal((ctx) -> {
            VariableStore store = switch (scope) {
                case SAVED -> ctx.evaluator.space.savedVariables;
                case SESSION -> ctx.evaluator.sessionVariables;
                case THREAD -> ctx.threadVariables;
            };
            store.set(name.getValue(ctx), type, value.getValue(ctx));
            ctx.sendSignal(next);
        });
    }

    @Override
    public String getTitle() {
        if (type == null) return Translations.get("node.set_variable_" + scope.id + ".base_title");
        return Translations.get("node.set_variable_" + scope.id + ".title", type.getName());
    }

    @Override
    public Node copy() {
        return new SetVariableNode<>(type, scope);
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
        return new SetVariableNode<>(types.get(0), scope);
    }
}
