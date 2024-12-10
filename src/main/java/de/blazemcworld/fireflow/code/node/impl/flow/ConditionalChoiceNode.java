package de.blazemcworld.fireflow.code.node.impl.flow;

import de.blazemcworld.fireflow.code.node.Node;
import de.blazemcworld.fireflow.code.type.AllTypes;
import de.blazemcworld.fireflow.code.type.ConditionType;
import de.blazemcworld.fireflow.code.type.WireType;
import de.blazemcworld.fireflow.util.Translations;
import net.minestom.server.item.Material;

import java.util.List;

public class ConditionalChoiceNode<T> extends Node {

    private final WireType<T> type;

    public ConditionalChoiceNode(WireType<T> type) {
        super("conditional_choice", Material.WATER_BUCKET);
        this.type = type;

        Input<Boolean> condition = new Input<>("condition", ConditionType.INSTANCE);
        Input<T> trueValue = new Input<>("trueValue", type);
        Input<T> falseValue = new Input<>("falseValue", type);
        Output<T> choice = new Output<>("choice", type);

        choice.valueFrom(ctx -> {
            if (condition.getValue(ctx)) return trueValue.getValue(ctx);
            return falseValue.getValue(ctx);
        });
    }

    @Override
    public Node copy() {
        return new ConditionalChoiceNode<>(type);
    }

    @Override
    public boolean acceptsType(WireType<?> type, int index) {
        return AllTypes.isValue(type);
    }

    @Override
    public List<WireType<?>> getTypes() {
        return List.of(type);
    }

    @Override
    public String getTitle() {
        if (type == null) return Translations.get("node.conditional_choice.base_title");
        return Translations.get("node.conditional_choice.title", type.getName());
    }

    @Override
    public int getTypeCount() {
        return 1;
    }

    @Override
    public Node copyWithTypes(List<WireType<?>> types) {
        return new ListForEachNode<>(types.getFirst());
    }

}
