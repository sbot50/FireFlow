package de.blazemcworld.fireflow.code.node.impl.variable;

import de.blazemcworld.fireflow.code.node.Node;
import de.blazemcworld.fireflow.code.type.AllTypes;
import de.blazemcworld.fireflow.code.type.SignalType;
import de.blazemcworld.fireflow.code.type.WireType;
import de.blazemcworld.fireflow.util.Translations;
import net.minestom.server.item.Material;

import java.util.List;

public class CacheValueNode<T> extends Node {

    private final WireType<T> type;

    public CacheValueNode(WireType<T> type) {
        super("cache_value", Material.KNOWLEDGE_BOOK);
        this.type = type;

        Input<Void> signal = new Input<>("signal", SignalType.INSTANCE);
        Input<T> store = new Input<>("store", type);
        Output<Void> next = new Output<>("next", SignalType.INSTANCE);
        Output<T> cache = new Output<>("cache", type);

        cache.valueFromThread();
        signal.onSignal((ctx) -> {
            ctx.setThreadValue(cache, store.getValue(ctx));
            ctx.sendSignal(next);
        });
    }

    @Override
    public String getTitle() {
        if (type == null) return Translations.get("node.cache_value.base_title");
        return Translations.get("node.cache_value.title", type.getName());
    }

    @Override
    public Node copy() {
        return new CacheValueNode<>(type);
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
        return new CacheValueNode<>(types.getFirst());
    }
}
