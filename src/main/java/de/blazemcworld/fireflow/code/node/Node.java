package de.blazemcworld.fireflow.code.node;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

import de.blazemcworld.fireflow.code.CodeEvaluator;
import de.blazemcworld.fireflow.code.CodeThread;
import de.blazemcworld.fireflow.code.node.impl.function.FunctionCallNode;
import de.blazemcworld.fireflow.code.node.impl.function.FunctionInputsNode;
import de.blazemcworld.fireflow.code.node.impl.function.FunctionOutputsNode;
import de.blazemcworld.fireflow.code.type.WireType;
import de.blazemcworld.fireflow.util.Translations;
import net.minestom.server.item.Material;

public abstract class Node {

    public final String id;
    public final Material icon;
    public List<Input<?>> inputs = new ArrayList<>();
    public List<Varargs<?>> varargs = new ArrayList<>();
    public List<Output<?>> outputs = new ArrayList<>();

    protected Node(String id, Material icon) {
        this.id = id;
        this.icon = icon;
    }

    public String getTitle() {
        return Translations.get("node." + id + ".title");
    }

    public abstract Node copy();

    public void init(CodeEvaluator evaluator) {
    }

    public List<WireType<?>> getTypes() {
        return null;
    }

    public int getTypeCount() {
        return 0;
    }

    public boolean acceptsType(WireType<?> type, int index) {
        return false;
    }

    public Node copyWithTypes(List<WireType<?>> types) {
        return copy();
    }

    public class Input<T> {
        public final String id;
        public final WireType<T> type;
        public String inset;
        public Output<T> connected;
        public Varargs<T> varargsParent;
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
            if (Node.this instanceof FunctionCallNode || Node.this instanceof FunctionOutputsNode || Node.this instanceof FunctionInputsNode) return id;
            if (varargsParent != null) return Translations.get("node." + Node.this.id + ".input." + varargsParent.id);
            return Translations.get("node." + Node.this.id + ".input." + id);
        }

        public Node getNode() {
            return Node.this;
        }

        public void setInset(String value) {
            inset = value;
            connected = null;
            if (varargsParent != null) varargsParent.update();
        }

        public void connect(Output<T> output) {
            connected = output;
            inset = null;
            if (varargsParent != null) varargsParent.update();
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
            if (ctx.timelimitHit()) return type.defaultValue();
            return logic.apply(ctx);
        }

        public String getName() {
            if (Node.this instanceof FunctionCallNode || Node.this instanceof FunctionOutputsNode || Node.this instanceof FunctionInputsNode) return id;
            return Translations.get("node." + Node.this.id + ".output." + id);
        }

        public Node getNode() {
            return Node.this;
        }

        public void valueFromThread() {
            logic = (ctx) -> ctx.getThreadValue(this);
        }
    }

    public class Varargs<T> {
        public final String id;
        public final WireType<T> type;
        public List<Input<T>> children = new ArrayList<>();
        public boolean ignoreUpdates = false;

        public Varargs(String id, WireType<T> type) {
            this.id = id;
            this.type = type;
            varargs.add(this);
            addInput(UUID.randomUUID().toString());
        }

        public List<T> getVarargs(CodeThread ctx) {
            List<T> list = new ArrayList<>();
            for (Input<T> input : children) {
                if (input.inset == null && input.connected == null) continue;
                list.add(input.getValue(ctx));
            }
            return list;
        }

        public void update() {
            if (ignoreUpdates) return;
            List<Input<T>> used = new ArrayList<>();
            for (Input<T> input : children) {
                if (input.inset != null || input.connected != null) {
                    used.add(input);
                }
            }

            if (used.size() == children.size()) {
                addInput(UUID.randomUUID().toString());
                return;
            }

            for (Input<T> input : new ArrayList<>(children)) {
                if (used.contains(input)) continue;
                if (input != children.getLast()) {
                    inputs.remove(input);
                    children.remove(input);
                }
            }

            if (!used.contains(children.getLast())) return;
            addInput(UUID.randomUUID().toString());
        }

        public void addInput(String uuid) {
            Input<T> input = new Input<>(uuid, type);
            input.varargsParent = this;
            children.add(input);
            inputs.remove(input);
            for (int i = inputs.size() - 1; i >= 0; i--) {
                if (inputs.get(i).varargsParent == this) {
                    inputs.add(i + 1, input);
                    return;
                }
            }
            inputs.add(input);
        }
    }
}
