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

    public GetVariableNode(WireType<T> type) {
        super("get_variable", Material.IRON_INGOT);
        this.type = type;

        Input<String> name = new Input<>("name", StringType.INSTANCE);
        Input<String> scope = new Input<>("scope", StringType.INSTANCE)
                .options("thread", "session", "saved");
        Output<T> value = new Output<>("value", type);

        value.valueFrom((ctx) -> {
            VariableStore store = switch (scope.getValue(ctx)) {
                case "saved" -> ctx.evaluator.space.savedVariables;
                case "session" -> ctx.evaluator.sessionVariables;
                case "thread" -> ctx.threadVariables;
                default -> null;
            };
            if (store == null) return type.defaultValue();
            return store.get(name.getValue(ctx), type);
        });
    }

    @Override
    public String getTitle() {
        if (type == null) return Translations.get("node.get_variable.base_title");
        return Translations.get("node.get_variable.title", type.getName());
    }

    @Override
    public Node copy() {
        return new GetVariableNode<>(type);
    }

    @Override
    public int getTypeCount() {
        return 1;
    }

    @Override
    public Node copyWithTypes(List<WireType<?>> types) {
        return new GetVariableNode<>(types.get(0));
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
