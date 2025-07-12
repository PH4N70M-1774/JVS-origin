package com.jvs.velox;

import static com.jvs.velox.Opcode.*;

import java.util.ArrayList;
import java.util.List;

public class VeloxVM
{
    private static final int DEFAULT_STACK_SIZE=1024;
    private static final int DEFAULT_MEMORY_SIZE=1024;

    private int ip, ip2, sp, localLength, globalLength, poolLength;

    private int[] instructions;

    private int[] stack;
    private int[] local;
    private int[] global;
    private String[] pool;

    private boolean trace;
    private boolean traceLater;

    private List<String> disassembledInstructions;

    private Tracer tracer;

    public VeloxVM(int[] instructions, int start, String[] pool, int poolLength)
    {
        ip=start;
        ip2=ip;
        sp=-1;
        localLength=0;
        globalLength=0;
        this.poolLength=poolLength;

        this.instructions=instructions;

        stack=new int[DEFAULT_STACK_SIZE];
        local=new int[DEFAULT_MEMORY_SIZE];
        global=new int[DEFAULT_MEMORY_SIZE];

        this.pool=pool;

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
                        System.out.print(value);
                    }
                    catch(ArrayIndexOutOfBoundsException e)
                    {
                        throw new VeloxVMError("Stack Underflow Error", e);
                    }
                }
                case PRINTSTR->{
                    int elements=instructions[ip];
                    ip++;
                    for(int i=0;i<elements;i++)
                    {
                        System.out.print(((char)stack[sp--]));
                    }
                }
                case ICMPE->{
                    try
                    {
                        int b=stack[sp--];
                        int a=stack[sp--];
                        sp++;
                        stack[sp]=((a==b)?1: 0);
                    }
                    catch(ArrayIndexOutOfBoundsException e)
                    {
                        throw new VeloxVMError("Stack Underflow Error", e);
                    }
                }
                case ICMPL->{
                    try
                    {
                        int b=stack[sp--];
                        int a=stack[sp--];
                        sp++;
                        stack[sp]=((a<b)?1: 0);
                    }
                    catch(ArrayIndexOutOfBoundsException e)
                    {
                        throw new VeloxVMError("Stack Underflow Error", e);
                    }
                }
                case ICMPLE->{
                    try
                    {
                        int b=stack[sp--];
                        int a=stack[sp--];
                        sp++;
                        stack[sp]=((a<=b)?1: 0);
                    }
                    catch(ArrayIndexOutOfBoundsException e)
                    {
                        throw new VeloxVMError("Stack Underflow Error", e);
                    }
                }
                case ICMPG->{
                    try
                    {
                        int b=stack[sp--];
                        int a=stack[sp--];
                        sp++;
                        stack[sp]=((a>b)?1: 0);
                    }
                    catch(ArrayIndexOutOfBoundsException e)
                    {
                        throw new VeloxVMError("Stack Underflow Error", e);
                    }
                }
                case ICMPGE->{
                    try
                    {
                        int b=stack[sp--];
                        int a=stack[sp--];
                        sp++;
                        stack[sp]=((a>=b)?1: 0);
                    }
                    catch(ArrayIndexOutOfBoundsException e)
                    {
                        throw new VeloxVMError("Stack Underflow Error", e);
                    }
                }
                case ICMPNE->{
                    try
                    {
                        int b=stack[sp--];
                        int a=stack[sp--];
                        sp++;
                        stack[sp]=((a!=b)?1: 0);
                    }
                    catch(ArrayIndexOutOfBoundsException e)
                    {
                        throw new VeloxVMError("Stack Underflow Error", e);
                    }
                }
                case BRANCH->{
                    int line=instructions[ip];
                    ip++;
                    ip=line;
                }
                case BRANCHT->{
                    int line=instructions[ip];
                    ip++;
                    ip=((stack[sp--]==1)?line:ip);
                }
                case BRANCHF->{
                    int line=instructions[ip];
                    ip++;
                    ip=((stack[sp--]==0)?line:ip);
                }
                case POP->sp--;
                case GSTORE->{
                    int value=stack[sp--];
                    int index=instructions[ip];
                    ip++;
                    global[index]=value;
                    globalLength++;
                }
                case GLOAD->{
                    int index=instructions[ip];
                    ip++;
                    sp++;
                    stack[sp]=global[index];
                }
                case PRINTSP->{
                    int index=instructions[ip];
                    System.out.print(pool[index]);
                    ip++;

                }
                case JUMPNEXT->System.out.println();
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
            if(localLength!=0||globalLength!=0||poolLength!=0)
            {
                System.out.println("\n==================================================MEMORY==================================================");
            }
            if(localLength!=0)
            {
                System.out.println("==================================================LOCAL===================================================");
                for(int i=0;i<localLength;i++)
                {
                    System.out.printf("%04d: %d\n", i, local[i]);
                }
                System.out.println("==========================================================================================================");
            }
            if(globalLength!=0)
            {
                System.out.println("==================================================GLOBAL==================================================");
                for(int i=0;i<=globalLength;i++)
                {
                    System.out.printf("%04d: %d\n", i, global[i]);
                }
                System.out.println("==========================================================================================================");
            }
            if(poolLength!=0)
            {
                System.out.println("===================================================POOL===================================================");
                for(int i=0;i<poolLength;i++)
                {
                    System.out.printf("%04d: \"%s\"", i, pool[i]);
                }
                System.out.println("\n==========================================================================================================");
            }
            if(localLength!=0||globalLength!=0||poolLength!=0)
            {
                System.out.println("==========================================================================================================");
            }
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
