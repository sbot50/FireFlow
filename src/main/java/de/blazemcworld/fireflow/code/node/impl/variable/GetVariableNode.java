package de.blazemcworld.fireflow.code.node.impl.variable;

import de.blazemcworld.fireflow.code.VariableStore;
import de.blazemcworld.fireflow.code.node.Node;
import de.blazemcworld.fireflow.code.type.AllTypes;
import de.blazemcworld.fireflow.code.type.StringType;
import de.blazemcworld.fireflow.code.type.WireType;
import de.blazemcworld.fireflow.util.Translations;
import net.minestom.server.item.Material;

import java.util.List;

public class GetVariableNode<T> extends Node {
    
    private final WireType<T> type;
    private final VariableStore.Scope scope;

    public GetVariableNode(WireType<T> type, VariableStore.Scope scope) {
        super("get_variable_" + scope.id, Material.IRON_INGOT);
        this.type = type;
        this.scope = scope;

        Input<String> name = new Input<>("name", StringType.INSTANCE);
        Output<T> value = new Output<>("value", type);

        value.valueFrom((ctx) -> {
            VariableStore store = switch (scope) {
                case SAVED -> ctx.evaluator.space.savedVariables;
                case SESSION -> ctx.evaluator.sessionVariables;
                case THREAD -> ctx.threadVariables;
            };

            return store.get(name.getValue(ctx), type);
        });
    }

    @Override
    public String getTitle() {
        if (type == null) return Translations.get("node.get_variable_" + scope.id + ".base_title");
        return Translations.get("node.get_variable_" + scope.id + ".title", type.getName());
    }

    @Override
    public Node copy() {
        return new GetVariableNode<>(type, scope);
    }

    @Override
    public int getTypeCount() {
        return 1;
    }

    @Override
    public Node copyWithTypes(List<WireType<?>> types) {
        return new GetVariableNode<>(types.get(0), scope);
    }

    @Override
    public List<WireType<?>> getTypes() {
        return List.of(type);
    }

    @Override
    public boolean acceptsType(WireType<?> type, int index) {
        return AllTypes.isValue(type);
    }

}
