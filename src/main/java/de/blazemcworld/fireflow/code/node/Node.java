package de.blazemcworld.fireflow.code.node;

import de.blazemcworld.fireflow.code.type.WireType;
import de.blazemcworld.fireflow.space.Space;
import de.blazemcworld.fireflow.util.Translations;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public abstract class Node {

    private final String id;
    public List<Input<?>> inputs = new ArrayList<>();
    public List<Output<?>> outputs = new ArrayList<>();
    protected Space space;

    protected Node(String id) {
        this.id = id;
    }

    public String getTitle() {
        return Translations.get("node." + id + ".title");
    }

    public abstract Node copy();

    public class Input<T> {
        private final String id;
        public final WireType<T> type;
        private Output<T> connected;
        private T valueOverwrite;
        private Runnable logic;

        public Input(String id, WireType<T> type) {
            this.id = id;
            this.type = type;
            inputs.add(this);
        }

        public T getValue() {
            if (valueOverwrite != null) return valueOverwrite;
            if (connected != null) return connected.computeNow();
            return type.defaultValue();
        }

        public void onSignal(Runnable logic) {
            this.logic = logic;
        }

        private void computeNow() {
            if (logic == null) return;
            logic.run();
        }

        public String getName() {
            return Translations.get("node." + Node.this.id + ".input." + id);
        }
    }

    public class Output<T> {
        private final String id;
        public final WireType<T> type;
        private Input<T> connected;
        private Supplier<T> logic;

        public Output(String id, WireType<T> type) {
            this.id = id;
            this.type = type;
            outputs.add(this);
        }

        public void valueFrom(Supplier<T> logic) {
            this.logic = logic;
        }

        public void sendSignal() {
            if (connected == null) return;
            connected.computeNow();
        }

        private T computeNow() {
            return logic.get();
        }

        public String getName() {
            return Translations.get("node." + Node.this.id + ".output." + id);
        }
    }

}
