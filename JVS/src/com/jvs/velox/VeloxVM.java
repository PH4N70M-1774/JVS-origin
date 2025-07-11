package com.jvs.velox;

import static com.jvs.velox.Opcode.*;
import java.util.List;
import java.util.ArrayList;

public class VeloxVM
{
    private static final int DEFAULT_STACK_SIZE=1024;
    private static final int DEFAULT_MEMORY_SIZE=1024;

    private int ip, ip2, sp, localLength;

    private int[] instructions;

    private int[] stack;
    private int[] local;

    private boolean trace;

    public VeloxVM(int[] instructions, int start)
    {
        ip=start;
        ip2=ip;
        sp=-1;
        localLength=0;

        this.instructions=instructions;

        stack=new int[DEFAULT_STACK_SIZE];
        local=new int[DEFAULT_MEMORY_SIZE];

        this.trace=false;
    }

    public void trace(boolean trace)
    {
        this.trace=trace;
    }

    public void exec() throws VeloxVMError
    {
        int opcode=instructions[ip];
        while(ip<instructions.length && opcode!=EXIT)
        {
            opcode=instructions[ip];
            ip2=ip;
            ip++;

            switch (opcode)
            {
                case EXIT->{}
                case ICONST->{
                    int value=instructions[ip];
                    ip++;
                    sp++;
                    stack[sp]=value;
                }
                case STORE->{
                    int value=stack[sp--];
                    int index=instructions[ip];
                    ip++;
                    local[index]=value;
                    localLength++;
                }
                case LOAD->{
                    int index=instructions[ip];
                    ip++;
                    sp++;
                    stack[sp]=local[index];
                }
                case IADD->{
                    try
                    {
                        int b=stack[sp--];
                        int a=stack[sp--];
                        sp++;
                        stack[sp]=(a+b);
                    }
                    catch(ArrayIndexOutOfBoundsException e)
                    {
                        throw new VeloxVMError("Stack Underflow Error", e);
                    }
                }
                case ISUB->{
                    try
                    {
                        int b=stack[sp--];
                        int a=stack[sp--];
                        sp++;
                        stack[sp]=(a-b);
                    }
                    catch(ArrayIndexOutOfBoundsException e)
                    {
                        throw new VeloxVMError("Stack Underflow Error", e);
                    }
                }
                case IMUL->{
                    try
                    {
                        int b=stack[sp--];
                        int a=stack[sp--];
                        sp++;
                        stack[sp]=(a*b);
                    }
                    catch(ArrayIndexOutOfBoundsException e)
                    {
                        throw new VeloxVMError("Stack Underflow Error", e);
                    }
                }
                case IDIV->{
                    try
                    {
                        int b=stack[sp--];
                        int a=stack[sp--];
                        sp++;
                        stack[sp]=(a/b);
                    }
                    catch(ArrayIndexOutOfBoundsException e)
                    {
                        throw new VeloxVMError("Stack Underflow Error", e);
                    }
                }
                case IMOD->{
                    try
                    {
                        int b=stack[sp--];
                        int a=stack[sp--];
                        sp++;
                        stack[sp]=(a%b);
                    }
                    catch(ArrayIndexOutOfBoundsException e)
                    {
                        throw new VeloxVMError("Stack Underflow Error", e);
                    }
                }
                case PRINT->{
                    try
                    {
                        int value=stack[sp--];
                        System.out.println(value);
                    }
                    catch(ArrayIndexOutOfBoundsException e)
                    {
                        throw new VeloxVMError("Stack Underflow Error", e);
                    }
                }
                default->{
                    ip+=Opcode.get(opcode).getNumOperands();
                }
            }

            if(trace)
            {
                disassemble(opcode);
            }
        }
        if(trace && localLength!=0)
        {
            System.out.println("Memory:");
            for(int i=0;i<localLength;i++)
            {
                System.out.println(getIndex(getDigits(i), i)+":  "+local[i]);
            }
        }
    }

    private void disassemble(int opcode)
    {
        String name=Opcode.get(opcode).getName();
        int numOperands=Opcode.get(opcode).getNumOperands();
        String instruction="";

        instruction+=getIndex(getDigits(ip2), ip2)+": ";
        instruction+=getName(name);

        if(numOperands==1)
        {
            instruction+=instructions[ip2+1];
        }
        else if(numOperands==2)
        {
            instruction+=instructions[ip2+1];
            instruction+=", ";
            instruction+=instructions[ip2+2];
        }
        System.out.printf("%-35s", instruction);
        printStack();
    }

    private void printStack()
    {
        List<Integer> stackString=new ArrayList<>();
        for(int i=0;i<=sp;i++)
        {
            stackString.add(stack[i]);
        }
        System.out.println(stackString);
    }

    private int getDigits(int number)
    {
        int digits=0;
        while(number!=0)
        {
            digits++;
            number/=10;
        }
        return digits;
    }

    private String getIndex(int digits, int index)
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
