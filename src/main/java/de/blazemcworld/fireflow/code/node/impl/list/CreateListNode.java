package de.blazemcworld.fireflow.code.node.impl.list;

import java.util.List;

import de.blazemcworld.fireflow.code.node.Node;
import de.blazemcworld.fireflow.code.type.AllTypes;
import de.blazemcworld.fireflow.code.type.ListType;
import de.blazemcworld.fireflow.code.type.WireType;
import de.blazemcworld.fireflow.code.value.ListValue;
import de.blazemcworld.fireflow.util.Translations;
import net.minestom.server.item.Material;

public class CreateListNode<T> extends Node {

    private final WireType<T> type;

    public CreateListNode(WireType<T> type) {
        super("create_list", Material.MINECART);
        this.type = type;

        Varargs<T> content = new Varargs<>("content", type);
        Output<ListValue<T>> output = new Output<>("list", ListType.of(type));
        output.valueFrom((ctx) -> {
            return new ListValue<>(type, content.getVarargs(ctx));
        });
    }

    @Override
    public String getTitle() {
        if (type == null) return Translations.get("node.create_list.base_title");
        return Translations.get("node.create_list.title", type.getName());
    }

    @Override
    public Node copy() {
        return new CreateListNode<>(type);
    }
    
    @Override
    public boolean acceptsType(WireType<?> type, int index) {
        return AllTypes.isValue(type);
    }

    @Override
    public Node copyWithTypes(List<WireType<?>> types) {
        return new CreateListNode<>(types.get(0));
    }

    @Override
    public int getTypeCount() {
        return 1;
    }

    @Override
    public List<WireType<?>> getTypes() {
        return List.of(type);
    }

}
