package de.blazemcworld.fireflow.code.node.impl.flow;

import java.util.List;

import de.blazemcworld.fireflow.code.node.Node;
import de.blazemcworld.fireflow.code.type.AllTypes;
import de.blazemcworld.fireflow.code.type.ListType;
import de.blazemcworld.fireflow.code.type.SignalType;
import de.blazemcworld.fireflow.code.type.WireType;
import de.blazemcworld.fireflow.code.value.ListValue;
import de.blazemcworld.fireflow.util.Translations;

public class ListForEachNode<T> extends Node {
    
    private final WireType<T> type;

    public ListForEachNode(WireType<T> type) {
        super("list_for_each");
        this.type = type;

        Input<Void> signal = new Input<>("signal", SignalType.INSTANCE);
        Input<ListValue<T>> list = new Input<>("list", ListType.of(type));
        
        Output<Void> each = new Output<>("each", SignalType.INSTANCE);
        Output<T> value = new Output<>("value", type);
        Output<Void> next = new Output<>("next", SignalType.INSTANCE);

        value.valueFromThread();

        signal.onSignal((ctx) -> {
            ListValue<T> listValue = list.getValue(ctx);
            for (int i = 0; i < listValue.size(); i++) {
                if (ctx.timelimitHit()) return;
                ctx.setThreadValue(value, listValue.get(i));
                each.sendSignalImmediately(ctx);
            }
            ctx.sendSignal(next);
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
