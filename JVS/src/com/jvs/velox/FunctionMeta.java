package com.jvs.velox;

public class FunctionMeta
{
    private String name;
    private int nArgs;
    private int nLocals;
    private int address;

    public FunctionMeta(String name, int nArgs, int nLocals, int address)
    {
        this.name=name;
        this.nArgs=nArgs;
        this.nLocals=nLocals;
        this.address=address;
    }

    public String getName()
    {
        return name;
    }

    public int getNumberOfArgs()
    {
        return nArgs;
    }

    public int getNumberOfLocals()
    {
        return nLocals;
    }

    public int getAddress()
    {
        return address;
    }
}
