package com.jvs.velox;

import static com.jvs.velox.Opcode.*;

import java.util.ArrayList;
import java.util.List;

public class VeloxVM {
    private static final int DEFAULT_STACK_SIZE = 1024;
    private static final int DEFAULT_MEMORY_SIZE = 1024;

    private int ip, ip2, sp, globalLength, poolLength;

    private int[] instructions;

    private int[] stack;
    private int[] global;
    private String[] pool;

    private boolean trace;
    private boolean traceLater;
    private boolean cursorAtLineStart;

    private List<String> disassembledInstructions;

    private Tracer tracer;
    private Context ctx;
    private FunctionMeta[] metadata;

    public VeloxVM(int[] instructions, String[] pool, int poolLength, FunctionMeta[] metadata) {
        sp = -1;
        globalLength = -1;
        this.poolLength = poolLength;

        this.instructions = instructions;

        stack = new int[DEFAULT_STACK_SIZE];
        global = new int[DEFAULT_MEMORY_SIZE];

        this.pool = pool;

        this.trace = false;
        this.traceLater = false;
        this.cursorAtLineStart = true;
        disassembledInstructions = new ArrayList<>();

        tracer = new Tracer(instructions, stack, metadata);
        this.metadata = metadata;
    }

    public void trace(boolean trace, boolean traceLater) {
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
        while (ip < instructions.length && opcode != EXIT) {
            opcode = instructions[ip];
            ip2 = ip;
            ip++;

            switch (opcode) {
                case EXIT -> {
                }
                case ICONST -> {
                    try {
                        int value = instructions[ip++];
                        stack[++sp] = value;
                    } catch (ArrayIndexOutOfBoundsException e) {
                        throw new VeloxVMError("Stack Overflow Error", e);
                    }
                }
                case STORE -> {
                    int value = stack[sp--];
                    int index = instructions[ip++];
                    ctx.locals[index] = value;
                }
                case LOAD -> {
                    int index = instructions[ip++];
                    stack[++sp] = ctx.locals[index];
                }
                case IADD -> {
                    try {
                        int b = stack[sp--];
                        int a = stack[sp--];
                        stack[++sp] = (a + b);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        throw new VeloxVMError("Stack Underflow Error", e);
                    }
                }
                case ISUB -> {
                    try {
                        int b = stack[sp--];
                        int a = stack[sp--];
                        stack[++sp] = (a - b);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        throw new VeloxVMError("Stack Underflow Error", e);
                    }
                }
                case IMUL -> {
                    try {
                        int b = stack[sp--];
                        int a = stack[sp--];
                        stack[++sp] = (a * b);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        throw new VeloxVMError("Stack Underflow Error", e);
                    }
                }
                case IDIV -> {
                    try {
                        int b = stack[sp--];
                        int a = stack[sp--];
                        stack[++sp] = (a / b);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        throw new VeloxVMError("Stack Underflow Error", e);
                    }
                }
                case IMOD -> {
                    try {
                        int b = stack[sp--];
                        int a = stack[sp--];
                        stack[++sp] = (a % b);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        throw new VeloxVMError("Stack Underflow Error", e);
                    }
                }
                case PRINT -> {
                    try {
                        int value = stack[sp--];
                        System.out.print(value);
                        cursorAtLineStart = false;
                    } catch (ArrayIndexOutOfBoundsException e) {
                        throw new VeloxVMError("Stack Underflow Error", e);
                    }
                }
                case PRINTSTR -> {
                    int elements = instructions[ip++];
                    for (int i = 0; i < elements; i++) {
                        System.out.print(((char) stack[sp--]));
                    }
                    cursorAtLineStart = false;
                }
                case ICMPE -> {
                    try {
                        int b = stack[sp--];
                        int a = stack[sp--];
                        stack[++sp] = ((a == b) ? 1 : 0);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        throw new VeloxVMError("Stack Underflow Error", e);
                    }
                }
                case ICMPL -> {
                    try {
                        int b = stack[sp--];
                        int a = stack[sp--];
                        stack[++sp] = ((a < b) ? 1 : 0);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        throw new VeloxVMError("Stack Underflow Error", e);
                    }
                }
                case ICMPLE -> {
                    try {
                        int b = stack[sp--];
                        int a = stack[sp--];
                        stack[++sp] = ((a <= b) ? 1 : 0);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        throw new VeloxVMError("Stack Underflow Error", e);
                    }
                }
                case ICMPG -> {
                    try {
                        int b = stack[sp--];
                        int a = stack[sp--];
                        stack[++sp] = ((a > b) ? 1 : 0);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        throw new VeloxVMError("Stack Underflow Error", e);
                    }
                }
                case ICMPGE -> {
                    try {
                        int b = stack[sp--];
                        int a = stack[sp--];
                        stack[++sp] = ((a >= b) ? 1 : 0);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        throw new VeloxVMError("Stack Underflow Error", e);
                    }
                }
                case ICMPNE -> {
                    try {
                        int b = stack[sp--];
                        int a = stack[sp--];
                        stack[++sp] = ((a != b) ? 1 : 0);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        throw new VeloxVMError("Stack Underflow Error", e);
                    }
                }
                case BRANCH -> {
                    int line = instructions[ip++];
                    ip = line;
                }
                case BRANCHT -> {
                    int line = instructions[ip++];
                    ip = ((stack[sp--] == 1) ? line : ip);
                }
                case BRANCHF -> {
                    int line = instructions[ip++];
                    ip = ((stack[sp--] == 0) ? line : ip);
                }
                case CALL -> {
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
                case RET -> {
                    ip = ctx.getReturnIP();
                    ctx = ctx.getInvokingContext();
                }
                case POP -> sp--;
                case GSTORE -> {
                    int value = stack[sp--];
                    int index = instructions[ip++];
                    global[index] = value;
                    globalLength++;
                }
                case GLOAD -> {
                    int index = instructions[ip++];
                    stack[++sp] = global[index];
                }
                case PRINTSP -> {
                    int index = instructions[ip++];
                    System.out.print(pool[index]);
                    cursorAtLineStart = false;

                }
                case JUMPNEXT -> {
                    System.out.println();
                    cursorAtLineStart = true;
                }
                case AND -> {
                    try {
                        int b = stack[sp--];
                        int a = stack[sp--];
                        stack[++sp] = (a == 1 && b == 1) ? 1 : 0;
                    } catch (ArrayIndexOutOfBoundsException e) {
                        throw new VeloxVMError("Stack Underflow Error", e);
                    }
                }
                case OR -> {
                    try {
                        int b = stack[sp--];
                        int a = stack[sp--];
                        stack[++sp] = (a == 1 || b == 1) ? 1 : 0;
                    } catch (ArrayIndexOutOfBoundsException e) {
                        throw new VeloxVMError("Stack Underflow Error", e);
                    }
                }
                case NOT -> {
                    try {
                        int a = stack[sp--];
                        stack[++sp] = (a == 1) ? 0 : 1;
                    } catch (ArrayIndexOutOfBoundsException e) {
                        throw new VeloxVMError("Stack Underflow Error", e);
                    }
                }
                case XOR -> {
                    try {
                        int b = stack[sp--];
                        int a = stack[sp--];
                        stack[++sp] = (a == 1 && b == 0) || (a == 0 && b == 1) ? 1 : 0;
                    } catch (ArrayIndexOutOfBoundsException e) {
                        throw new VeloxVMError("Stack Underflow Error", e);
                    }
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
            disassembledInstructions.add(tracer.disassemble(ip2, opcode, sp));
        } else if (trace) {
            if (!cursorAtLineStart) {
                System.out.println();
                cursorAtLineStart = true;
            }
            tracer.disassembleAndPrint(ip2, opcode, sp);
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
