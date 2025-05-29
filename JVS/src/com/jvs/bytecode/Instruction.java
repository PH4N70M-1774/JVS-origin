package com.jvs.bytecode;

public class Instruction
{
    private Opcode op;
    private String args;

    public Instruction(String instruction, boolean isWithoutArgs)
    {
        instruction=instruction.trim();
        if(isWithoutArgs)
        {
            op=Opcode.getOpcodeFor(instruction);
            args="";
        }
        else
        {
            op=Opcode.getOpcodeFor(instruction.substring(0, instruction.indexOf(" ")));
            args=instruction.substring((instruction.indexOf(' ')+1));
        }
    }

    public Opcode getOpcode()
    {
        return op;
    }

    public String getArgs()
    {
        return args;
    }

    @Override
    public String toString()
    {
        return op+" "+args;
    }
}