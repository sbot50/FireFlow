package de.blazemcworld.fireflow.code.node.impl.flow;

import de.blazemcworld.fireflow.code.node.Node;
import de.blazemcworld.fireflow.code.type.AllTypes;
import de.blazemcworld.fireflow.code.type.ListType;
import de.blazemcworld.fireflow.code.type.SignalType;
import de.blazemcworld.fireflow.code.type.WireType;
import de.blazemcworld.fireflow.code.value.ListValue;
import de.blazemcworld.fireflow.util.Translations;
import net.minestom.server.item.Material;

import java.util.List;

public class ListForEachNode<T> extends Node {
    
    private final WireType<T> type;

    public ListForEachNode(WireType<T> type) {
        super("list_for_each", Material.HOPPER);
        this.type = type;

        Input<Void> signal = new Input<>("signal", SignalType.INSTANCE);
        Input<ListValue<T>> list = new Input<>("list", ListType.of(type));
        
        Output<Void> each = new Output<>("each", SignalType.INSTANCE);
        Output<T> value = new Output<>("value", type);
        Output<Void> next = new Output<>("next", SignalType.INSTANCE);

        value.valueFromThread();

        signal.onSignal((ctx) -> {
            int[] index = new int[] { 0 };
            ListValue<T> listValue = list.getValue(ctx);

            Runnable[] step = { null };
            step[0] = () -> {
                if (index[0] >= listValue.size()) {
                    ctx.sendSignal(next);
                    return;
                }
                ctx.setThreadValue(value, listValue.get(index[0]--));
                ctx.submit(step[0]);
                ctx.sendSignal(each);
            };

            step[0].run();
        });
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
    public Node copy() {
        return new ListForEachNode<>(type);
    }

    @Override
    public String getTitle() {
        if (type == null) return Translations.get("node.list_for_each.base_title");
        return Translations.get("node.list_for_each.title", type.getName());
    }

    @Override
    public int getTypeCount() {
        return 1;
    }

    @Override
    public Node copyWithTypes(List<WireType<?>> types) {
        return new ListForEachNode<>(types.get(0));
    }

}
