package com.jvs.velox;

import static com.jvs.velox.Opcode.*;

import java.util.ArrayList;
import java.util.List;

public class VeloxVM
{
    private static final int DEFAULT_STACK_SIZE=1024;
    private static final int DEFAULT_MEMORY_SIZE=1024;

    private int ip, ip2, sp, localLength;

    private int[] instructions;

    private int[] stack;
    private int[] local;

    private boolean trace;
    private boolean traceLater;

    private List<String> disassembledInstructions;

    private Tracer tracer;

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
        this.traceLater=false;
        disassembledInstructions=new ArrayList<>();

        tracer=new Tracer(instructions, stack);
    }

    public void trace(boolean trace, boolean traceLater)
    {
        this.trace=trace;
        this.traceLater=traceLater;
        if (traceLater)
        {
            this.disassembledInstructions=new ArrayList<>();
        }
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
                    try
                    {
                        int value=instructions[ip];
                        ip++;
                        sp++;
                        stack[sp]=value;
                    }
                    catch(ArrayIndexOutOfBoundsException e)
                    {
                        throw new VeloxVMError("Stack Overflow Error", e);
                    }
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
            tracing(opcode);
        }
        tracePrint();
    }

    private void tracing(int opcode)
    {
        if(trace && traceLater)
        {
            disassembledInstructions.add(tracer.disassemble(ip2, opcode, sp));
        }
        else if(trace)
        {
            tracer.disassembleAndPrint(ip2, opcode, sp);
        }
    }

    private void tracePrint()
    {
        if(trace && traceLater)
        {
            System.out.println();
            System.out.println("==================================================TRACE===================================================");
            for(String s: disassembledInstructions)
            {
                System.out.println(s);
            }
            System.out.println("==========================================================================================================");
            if(localLength!=0)
            {
                System.out.println("==================================================MEMORY==================================================");
                for(int i=0;i<localLength;i++)
                {
                    System.out.printf("%04d: %d", i, local[i]);
                }
            }
            System.out.println("\n==========================================================================================================");
        }
        if(trace && !traceLater && localLength!=0)
        {
            System.out.println("\nMemory:");
            for(int i=0;i<localLength;i++)
            {
                System.out.printf("%04d: %d", i, local[i]);
            }
        }
    }
}
