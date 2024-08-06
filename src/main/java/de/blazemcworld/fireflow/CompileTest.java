package de.blazemcworld.fireflow;

import de.blazemcworld.fireflow.compiler.ByteClassLoader;
import de.blazemcworld.fireflow.compiler.CompiledNode;
import de.blazemcworld.fireflow.compiler.CpuLimitException;
import de.blazemcworld.fireflow.compiler.NodeCompiler;
import de.blazemcworld.fireflow.compiler.instruction.Instruction;
import de.blazemcworld.fireflow.node.impl.AddNumbersNode;
import de.blazemcworld.fireflow.node.impl.WhileNode;
import de.blazemcworld.fireflow.node.impl.lists.ListAppendNode;
import de.blazemcworld.fireflow.node.impl.variable.GetVariableNode;
import de.blazemcworld.fireflow.node.impl.variable.LocalVariableScope;
import de.blazemcworld.fireflow.node.impl.variable.SetVariableNode;
import de.blazemcworld.fireflow.value.ListValue;
import de.blazemcworld.fireflow.value.NumberValue;

import java.lang.reflect.InvocationTargetException;

public class CompileTest {

    public static void main(String[] args) {
        SetVariableNode initList = new SetVariableNode(LocalVariableScope.INSTANCE, ListValue.get(NumberValue.INSTANCE));
        Instruction entry = initList.inputs.get(0 /*Signal*/);
        initList.inputs.get(1 /*Name*/).inset("list");
        // Value omitted, will default to a new empty list

        GetVariableNode getCounter = new GetVariableNode(LocalVariableScope.INSTANCE, NumberValue.INSTANCE);
        getCounter.inputs.getFirst(/*Name*/).inset("counter");

        AddNumbersNode addCounter = new AddNumbersNode();
        addCounter.inputs.get(0 /*Left*/).connectValue(getCounter.outputs.getFirst(/*Value*/));
        addCounter.inputs.get(1 /*Right*/).inset(1);

        WhileNode repeat = new WhileNode();
        initList.outputs.getFirst(/*Next*/).connectSignal(repeat.inputs.getFirst(/*Signal*/));
        repeat.inputs.get(1 /*Condition*/).inset(true);

        SetVariableNode increaseCounter = new SetVariableNode(LocalVariableScope.INSTANCE, NumberValue.INSTANCE);
        repeat.outputs.getFirst(/*Loop*/).connectSignal(increaseCounter.inputs.getFirst(/*Signal*/));
        increaseCounter.inputs.get(1 /*Name*/).inset("counter");
        increaseCounter.inputs.get(2 /*Value*/).connectValue(addCounter.outputs.getFirst(/*Result*/));

        GetVariableNode getList = new GetVariableNode(LocalVariableScope.INSTANCE, ListValue.get(NumberValue.INSTANCE));
        getList.inputs.getFirst(/*Name*/).inset("list");

        ListAppendNode appender = new ListAppendNode(NumberValue.INSTANCE);
        increaseCounter.outputs.getFirst(/*Next*/).connectSignal(appender.inputs.get(0 /*Signal*/));
        appender.inputs.get(1 /*List*/).connectValue(getList.outputs.getFirst(/*Value*/));
        appender.inputs.get(2 /*Value*/).connectValue(getCounter.outputs.getFirst(/*Value*/));
        NodeCompiler compiler = new NodeCompiler("Something");

        compiler.prepare(entry);
        String entrypoint = compiler.markRoot(entry);

        byte[] bytes = compiler.compile();

        ByteClassLoader loader = new ByteClassLoader(NodeCompiler.class.getClassLoader());

        Class<?> c = loader.define(compiler.className, bytes);

        try {
            CompiledNode inst = (CompiledNode) c.getDeclaredConstructor().newInstance();

            inst.setCpu(10_000_000);
            try {
                c.getDeclaredMethod(entrypoint).invoke(inst);
            } catch (InvocationTargetException invoke) {
                if (invoke.getTargetException() instanceof CpuLimitException) {
                    System.out.println("Reached cpu limit!");
                } else {
                    System.err.println("Threw exception!");
                    invoke.getTargetException().printStackTrace(System.err);
                }
            }

            System.out.println(inst.locals);
        } catch (Exception err) {
            err.printStackTrace(System.err);
        }
    }
}
