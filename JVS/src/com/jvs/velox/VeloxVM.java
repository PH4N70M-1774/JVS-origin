package com.jvs.velox;

import static com.jvs.velox.Opcode.*;

import java.util.ArrayList;
import java.util.List;

public class VeloxVM {
    private static final int DEFAULT_STACK_SIZE = 1024;
    private static final int DEFAULT_MEMORY_SIZE = 1024;

    private int ip, ip2, sp, dsp, globalLength, poolLength;

    private int[] instructions;

    private long[] stack;
    private long[] global;
    private String[] pool;

    private boolean trace;
    private boolean traceLater;
    private boolean cursorAtLineStart;

    private List<String> disassembledInstructions;

    private Tracer tracer;
    private Context ctx;
    private FunctionMeta[] metadata;

    public VeloxVM(VeloxInstructions vi) {
        this(vi.getInstructions(), vi.getPool(), vi.getPoolLength(), vi.getMetadata());
    }

    public VeloxVM(int[] instructions, String[] pool, int poolLength, FunctionMeta[] metadata) {
        sp = -1;
        globalLength = -1;
        this.poolLength = poolLength;

        this.instructions = instructions;

        stack = new long[DEFAULT_STACK_SIZE];
        global = new long[DEFAULT_MEMORY_SIZE];

        this.pool = pool;

        this.trace = false;
        this.traceLater = false;
        this.cursorAtLineStart = true;
        disassembledInstructions = new ArrayList<>();
        this.metadata = metadata;
    }

    public void trace(boolean trace, boolean traceLater, boolean printStack) {
        tracer = new Tracer(instructions, stack, metadata, printStack);
        this.trace = trace;
        this.traceLater = traceLater;
        if (traceLater) {
            this.disassembledInstructions = new ArrayList<>();
        }
    }

    public void exec(int startIP) throws VeloxVMError {
        ip = startIP;
        ip2 = ip;
        ctx = new Context(null, 0, metadata[0]); // simulate a call to main()
        cpu();
    }

    public void cpu() throws VeloxVMError {
        int opcode = instructions[ip];
        while (ip < instructions.length && opcode != exit) {
            opcode = instructions[ip];
            ip2 = ip;
            ip++;

            switch (opcode) {
                case exit -> {
                }
                case iconst -> {
                    try {
                        int value = instructions[ip++];
                        stack[++sp] = value;
                    } catch (ArrayIndexOutOfBoundsException e) {
                        throw new VeloxVMError("Stack Overflow Error", e);
                    }
                }
                case store -> {
                    long value = stack[sp--];
                    int index = instructions[ip++];
                    ctx.locals[index] = value;
                }
                case load -> {
                    int index = instructions[ip++];
                    stack[++sp] = ctx.locals[index];
                }
                case iadd -> {
                    try {
                        long b = stack[sp--];
                        long a = stack[sp--];
                        stack[++sp] = (a + b);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        throw new VeloxVMError("Stack Underflow Error", e);
                    }
                }
                case isub -> {
                    try {
                        long b = stack[sp--];
                        long a = stack[sp--];
                        stack[++sp] = (a - b);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        throw new VeloxVMError("Stack Underflow Error", e);
                    }
                }
                case imul -> {
                    try {
                        long b = stack[sp--];
                        long a = stack[sp--];
                        stack[++sp] = (a * b);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        throw new VeloxVMError("Stack Underflow Error", e);
                    }
                }
                case idiv -> {
                    try {
                        long b = stack[sp--];
                        long a = stack[sp--];
                        stack[++sp] = (a / b);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        throw new VeloxVMError("Stack Underflow Error", e);
                    }
                }
                case imod -> {
                    try {
                        long b = stack[sp--];
                        long a = stack[sp--];
                        stack[++sp] = (a % b);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        throw new VeloxVMError("Stack Underflow Error", e);
                    }
                }
                case print -> {
                    try {
                        long value = stack[sp--];
                        System.out.print(value);
                        cursorAtLineStart = false;
                    } catch (ArrayIndexOutOfBoundsException e) {
                        throw new VeloxVMError("Stack Underflow Error", e);
                    }
                }
                case printstr -> {
                    int elements = instructions[ip++];
                    for (int i = 0; i < elements; i++) {
                        System.out.print(((char) stack[sp--]));
                    }
                    cursorAtLineStart = false;
                }
                case icmpe -> {
                    try {
                        long b = stack[sp--];
                        long a = stack[sp--];
                        stack[++sp] = ((a == b) ? 1 : 0);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        throw new VeloxVMError("Stack Underflow Error", e);
                    }
                }
                case icmpl -> {
                    try {
                        long b = stack[sp--];
                        long a = stack[sp--];
                        stack[++sp] = ((a < b) ? 1 : 0);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        throw new VeloxVMError("Stack Underflow Error", e);
                    }
                }
                case icmple -> {
                    try {
                        long b = stack[sp--];
                        long a = stack[sp--];
                        stack[++sp] = ((a <= b) ? 1 : 0);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        throw new VeloxVMError("Stack Underflow Error", e);
                    }
                }
                case icmpg -> {
                    try {
                        long b = stack[sp--];
                        long a = stack[sp--];
                        stack[++sp] = ((a > b) ? 1 : 0);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        throw new VeloxVMError("Stack Underflow Error", e);
                    }
                }
                case icmpge -> {
                    try {
                        long b = stack[sp--];
                        long a = stack[sp--];
                        stack[++sp] = ((a >= b) ? 1 : 0);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        throw new VeloxVMError("Stack Underflow Error", e);
                    }
                }
                case icmpne -> {
                    try {
                        long b = stack[sp--];
                        long a = stack[sp--];
                        stack[++sp] = ((a != b) ? 1 : 0);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        throw new VeloxVMError("Stack Underflow Error", e);
                    }
                }
                case branch -> {
                    int line = instructions[ip++];
                    ip = line;
                }
                case brancht -> {
                    int line = instructions[ip++];
                    ip = ((stack[sp--] == 1) ? line : ip);
                }
                case branchf -> {
                    int line = instructions[ip++];
                    ip = ((stack[sp--] == 0) ? line : ip);
                }
                case invoke -> {
                    // expects all args on stack
                    int funcIndex = instructions[ip++]; // index of target function
                    int nArgs = metadata[funcIndex].getNumberOfArgs(); // how many args got pushed
                    ctx = new Context(ctx, ip, metadata[funcIndex]);
                    // copy args into new context
                    int firstArg = sp - nArgs + 1;
                    for (int i = 0; i < nArgs; i++) {
                        ctx.locals[i] = stack[firstArg + i];
                    }
                    sp -= nArgs;
                    ip = metadata[funcIndex].getAddress();
                }
                case ret -> {
                    ip = ctx.getReturnIP();
                    ctx = ctx.getInvokingContext();
                }
                case pop -> sp--;
                case gstore -> {
                    long value = stack[sp--];
                    int index = instructions[ip++];
                    global[index] = value;
                    globalLength++;
                }
                case gload -> {
                    int index = instructions[ip++];
                    stack[++sp] = global[index];
                }
                case printsp -> {
                    int index = instructions[ip++];
                    System.out.print(pool[index]);
                    cursorAtLineStart = false;

                }
                case jumpnext -> {
                    System.out.println();
                    cursorAtLineStart = true;
                }
                case and -> {
                    try {
                        long b = stack[sp--];
                        long a = stack[sp--];
                        stack[++sp] = (a == 1 && b == 1) ? 1 : 0;
                    } catch (ArrayIndexOutOfBoundsException e) {
                        throw new VeloxVMError("Stack Underflow Error", e);
                    }
                }
                case or -> {
                    try {
                        long b = stack[sp--];
                        long a = stack[sp--];
                        stack[++sp] = (a == 1 || b == 1) ? 1 : 0;
                    } catch (ArrayIndexOutOfBoundsException e) {
                        throw new VeloxVMError("Stack Underflow Error", e);
                    }
                }
                case not -> {
                    try {
                        long a = stack[sp--];
                        stack[++sp] = (a == 1) ? 0 : 1;
                    } catch (ArrayIndexOutOfBoundsException e) {
                        throw new VeloxVMError("Stack Underflow Error", e);
                    }
                }
                case xor -> {
                    try {
                        long b = stack[sp--];
                        long a = stack[sp--];
                        stack[++sp] = (a == 1 && b == 0) || (a == 0 && b == 1) ? 1 : 0;
                    } catch (ArrayIndexOutOfBoundsException e) {
                        throw new VeloxVMError("Stack Underflow Error", e);
                    }
                }
                case iconst8 -> {
                    byte[] longBytes = new byte[8];
                    for (int i = 0; i < 8; i++) {
                        longBytes[i] = (byte) instructions[ip++];
                    }
                    stack[++sp] = Utilities.bytesToLong(longBytes);

                }
                default -> {
                    ip += Opcode.get(opcode).getNumOperands();
                }
            }
            tracing(opcode);
        }
        tracePrint();
    }

    private void tracing(int opcode) {
        if (trace && traceLater) {
            disassembledInstructions.add(tracer.disassemble(ip2, opcode, sp, dsp));
        } else if (trace) {
            if (!cursorAtLineStart) {
                System.out.println();
                cursorAtLineStart = true;
            }
            tracer.disassembleAndPrint(ip2, opcode, sp, dsp);
        }
    }

    private void tracePrint() {
        if (trace && traceLater) {
            System.out.println();
            System.out.println(
                    "==================================================TRACE===================================================");
            for (String s : disassembledInstructions) {
                System.out.println(s);
            }
            System.out.println(
                    "==========================================================================================================");
            if (globalLength != -1 || poolLength != 0) {
                System.out.println(
                        "\n==================================================MEMORY==================================================");
            }
            if (globalLength != -1) {
                System.out.println(
                        "==================================================GLOBAL==================================================");
                for (int i = 0; i <= globalLength; i++) {
                    System.out.printf("%04d: %d\n", i, global[i]);
                }
                System.out.println(
                        "==========================================================================================================");
            }
            if (poolLength != 0) {
                System.out.println(
                        "===================================================POOL===================================================");
                for (int i = 0; i < poolLength; i++) {
                    System.out.printf("%04d: \"%s\"\n", i, pool[i]);
                }
                System.out.println(
                        "==========================================================================================================");
            }
            if (globalLength != -1 || poolLength != 0) {
                System.out.println(
                        "==========================================================================================================");
            }
        }
        if (trace && !traceLater && globalLength != -1) {
            System.out.println("\nMemory:");
            for (int i = 0; i <= globalLength; i++) {
                System.out.printf("%04d: %d\n", i, global[i]);
            }
        }
    }
}
