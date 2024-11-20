package de.blazemcworld.fireflow.code.node;

import de.blazemcworld.fireflow.code.CodeEvaluator;
import de.blazemcworld.fireflow.code.CodeThread;
import de.blazemcworld.fireflow.code.type.WireType;
import de.blazemcworld.fireflow.util.Translations;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class Node {

    public final String id;
    public List<Input<?>> inputs = new ArrayList<>();
    public List<Output<?>> outputs = new ArrayList<>();

    protected Node(String id) {
        this.id = id;
    }

    public String getTitle() {
        return Translations.get("node." + id + ".title");
    }

    public abstract Node copy();

    public void init(CodeEvaluator evaluator) {
    }

    public class Input<T> {
        public final String id;
        public final WireType<T> type;
        public String inset;
        public Output<T> connected;
        private Consumer<CodeThread> logic;

        public Input(String id, WireType<T> type) {
            this.id = id;
            this.type = type;
            inputs.add(this);
        }

        public T getValue(CodeThread ctx) {
            if (connected != null) return connected.computeNow(ctx);
            if (inset != null) return type.parseInset(inset);
            return type.defaultValue();
        }

        public void onSignal(Consumer<CodeThread> logic) {
            this.logic = logic;
        }

        private void computeNow(CodeThread ctx) {
            if (ctx.timelimitHit()) return;
            if (logic == null) return;
            logic.accept(ctx);
        }

        public String getName() {
            return Translations.get("node." + Node.this.id + ".input." + id);
        }

        public Node getNode() {
            return Node.this;
        }

        public void setInset(String value) {
            inset = value;
            connected = null;
        }
    }

    public class Output<T> {
        public final String id;
        public final WireType<T> type;
        public Input<T> connected;
        private Function<CodeThread, T> logic;

        public Output(String id, WireType<T> type) {
            this.id = id;
            this.type = type;
            outputs.add(this);
        }

        public void valueFrom(Function<CodeThread, T> logic) {
            this.logic = logic;
        }

        public void sendSignalImmediately(CodeThread ctx) {
            if (connected == null) return;
            connected.computeNow(ctx);
        }

        private T computeNow(CodeThread ctx) {
            return logic.apply(ctx);
        }

        public String getName() {
            return Translations.get("node." + Node.this.id + ".output." + id);
        }

        public Node getNode() {
            return Node.this;
        }

        public void valueFromThread() {
            logic = (ctx) -> ctx.getThreadValue(this);
        }
    }

}
