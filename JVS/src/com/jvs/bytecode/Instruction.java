package com.jvs.bytecode;

public class Instruction
{
    private Opcode op;
    private String args;
    public static final int EMPTYLINE=0; //Used to mark empty lines in the source code.

    public Instruction(int id)
    {
        if(id==EMPTYLINE)
        {
            op=Opcode.EMPTYLINE;
            args="";
        }
        else
        {
            throw new IllegalArgumentException("Invalid instruction ID: " + id);
        }
    }

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