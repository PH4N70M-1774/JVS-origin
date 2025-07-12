package com.jvs.velox;

import java.util.ArrayList;
import java.util.List;

public class Tracer
{
    private int[] instructions;
    private int[] stack;

    public Tracer(int[] instructions, int[] stack)
    {
        this.instructions=instructions;
        this.stack=stack;
    }

    public String disassemble(int ip, int opcode, int sp)
    {
        String name=Opcode.get(opcode).getName();
        int numOperands=Opcode.get(opcode).getNumOperands();
        String instruction=String.format("%04d: %-10s", ip, name);

        if(numOperands==1)
        {
            instruction+=instructions[ip+1];
        }
        else if(numOperands==2)
        {
            instruction+=instructions[ip+1];
            instruction+=", ";
            instruction+=instructions[ip+2];
        }
        return String.format("%-35s%s", instruction, getStackString(sp));
    }

    public void disassembleAndPrint(int ip, int opcode, int sp)
    {

        System.out.println(disassemble(ip, opcode, sp));
    }

    public String getStackString(int sp)
    {
        List<Integer> stackString=new ArrayList<>();
        for(int i=0;i<=sp;i++)
        {
            stackString.add(stack[i]);
        }
        return stackString.toString();
    }
}
