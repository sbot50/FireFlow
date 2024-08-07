package de.blazemcworld.fireflow.compiler;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public abstract class CompiledNode {

    public Map<String, Object> locals = new HashMap<>();
    public Stack<Integer> fnStack = new Stack<>();
    private long cpuEnd = 0;

    public void setCpu(long allowance) {
        cpuEnd = System.nanoTime() + allowance;
    }

    @SuppressWarnings("unused") //Used by CpuCheckInstruction
    public void cpuCheck() {
        if (System.nanoTime() > cpuEnd) {
            fnStack.clear();
            throw new CpuLimitException();
        }
    }

    @SuppressWarnings("unused") //Used by FunctionDefinitions
    public void pushFnStack(int id) {
        fnStack.push(id);
    }

    @SuppressWarnings("unused") //Used by FunctionDefinitions
    public void popFnStack() {
        fnStack.pop();
    }

    @SuppressWarnings("unused") //Used by FunctionDefinitions
    public int peekFnStack() {
        if (fnStack.isEmpty()) return -1;
        return fnStack.peek();
    }

}
