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

    public void disassemble(int ip, int opcode, int sp)
    {
        String name=Opcode.get(opcode).getName();
        int numOperands=Opcode.get(opcode).getNumOperands();
        String instruction="";

        instruction+=getIndex(getDigits(ip), ip)+": ";
        instruction+=getName(name);

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
        System.out.printf("%-35s", instruction);
        printStack(sp);
    }

    private void printStack(int sp)
    {
        List<Integer> stackString=new ArrayList<>();
        for(int i=0;i<=sp;i++)
        {
            stackString.add(stack[i]);
        }
        System.out.println(stackString);
    }

    public int getDigits(int number)
    {
        int digits=0;
        while(number!=0)
        {
            digits++;
            number/=10;
        }
        return digits;
    }

    public String getIndex(int digits, int index)
    {
        if(digits==0)
        {
            return "0000";
        }
        else if(digits==1)
        {
            return "000"+index;
        }
        else if(digits==2)
        {
            return "00"+index;
        }
        else if(digits==3)
        {
            return "0"+index;
        }
        return ""+index;
    }

    private String getName(String name)
    {
        String s="";
        for(int i=0;i<10;i++)
        {
            s+=((i<name.length())?name.charAt(i):" ");
        }
        return s;
    }
}
