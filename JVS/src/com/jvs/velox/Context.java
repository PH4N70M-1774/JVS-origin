package com.jvs.velox;

public class Context {
    private Context invokingContext; // parent in the stack or "caller"
    private FunctionMeta metadata; // info about function we're executing
    private int returnIP;
    long[] locals; // args + locals, indexed from 0

    public Context(Context invokingContext, int returnIP, FunctionMeta metadata) {
        this.invokingContext = invokingContext;
        this.returnIP = returnIP;
        this.metadata = metadata;
        locals = new long[metadata.getNumberOfArgs() + metadata.getNumberOfLocals()];
    }

    public Context getInvokingContext() {
        return invokingContext;
    }

    public FunctionMeta getMetadata() {
        return metadata;
    }

    public int getReturnIP() {
        return returnIP;
    }
}
