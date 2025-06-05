package com.jvs.compiler;

import java.util.List;

public class ParsedInstruction
{
    private InstructionType type;
    private List<String> args;

    public ParsedInstruction(InstructionType type, List<String> args)
    {
        this.type=type;
        this.args=args;
    }

    public InstructionType getType()
    {
        return type;
    }

    public void setType(InstructionType type)
    {
        this.type = type;
    }

    public List<String> getArgs()
    {
        return args;
    }

    public void setArgs(List<String> args)
    {
        this.args = args;
    }

    @Override
    public String toString()
    {
        return type+" : "+args;
    }
}
