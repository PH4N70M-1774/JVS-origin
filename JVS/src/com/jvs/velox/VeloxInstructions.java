package com.jvs.velox;

public class VeloxInstructions {
    private int instructions[];
    private String pool[];
    private int poolLength;
    private FunctionMeta[] metadata;
    private int start;

    public VeloxInstructions(int instructions[], String pool[], int poolLength, FunctionMeta metadata[], int start) {
        this.instructions = instructions;
        this.pool = pool;
        this.poolLength = poolLength;
        this.metadata = metadata;
        this.start = start;
    }

    public int[] getInstructions() {
        return instructions;
    }

    public String[] getPool() {
        return pool;
    }

    public int getPoolLength() {
        return poolLength;
    }

    public FunctionMeta[] getMetadata() {
        return metadata;
    }

    public int getStartIP() {
        return start;
    }
}
