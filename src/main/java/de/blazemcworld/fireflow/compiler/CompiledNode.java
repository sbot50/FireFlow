package de.blazemcworld.fireflow.compiler;

import java.util.HashMap;
import java.util.Map;

public abstract class CompiledNode {

    public Map<String, Object> locals = new HashMap<>();
    private long cpuEnd = 0;

    public void setCpu(long allowance) {
        cpuEnd = System.nanoTime() + allowance;
    }

    @SuppressWarnings("unused") //Used by CpuCheckInstruction
    public void cpuCheck() {
        if (System.nanoTime() > cpuEnd) {
            throw new CpuLimitException();
        }
    }

}
