package de.blazemcworld.fireflow.code.node.impl.list;

import java.util.List;

import de.blazemcworld.fireflow.code.node.Node;
import de.blazemcworld.fireflow.code.type.AllTypes;
import de.blazemcworld.fireflow.code.type.ListType;
import de.blazemcworld.fireflow.code.type.WireType;
import de.blazemcworld.fireflow.code.value.ListValue;
import de.blazemcworld.fireflow.util.Translations;
import net.minestom.server.item.Material;

public class ListAppendNode<T> extends Node {
    
    private final WireType<T> type;

    public ListAppendNode(WireType<T> type) {
        super("list_append", Material.DISPENSER);
        this.type = type;

        Input<ListValue<T>> list = new Input<>("list", ListType.of(type));
        Varargs<T> value = new Varargs<>("value", type);

        Output<ListValue<T>> output = new Output<>("list", ListType.of(type));
        output.valueFrom((ctx) -> {
            ListValue<T> listValue = list.getValue(ctx);
            for (T t : value.getVarargs(ctx)) {
                listValue = listValue.add(t);
            }
            return listValue;
        });
    }

    @Override
    public String getTitle() {
        if (type == null) return Translations.get("node.list_append.base_title");
        return Translations.get("node.list_append.title", type.getName());
    }

    @Override
    public Node copy() {
        return new ListAppendNode<>(type);
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
    public int getTypeCount() {
        return 1;
    }

    @Override
    public Node copyWithTypes(List<WireType<?>> types) {
        return new ListAppendNode<>(types.get(0));

    }

}
