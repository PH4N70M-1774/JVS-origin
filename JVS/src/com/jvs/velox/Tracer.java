package com.jvs.velox;

import java.util.ArrayList;
import java.util.List;

public class Tracer {
    private int[] instructions;
    private long[] stack;
    private FunctionMeta[] metadata;
    private boolean printStack;

    public Tracer(int[] instructions, long[] stack, FunctionMeta[] metadata, boolean printStack) {
        this.instructions = instructions;
        this.stack = stack;
        this.metadata = metadata;
        this.printStack = printStack;
    }

    public String disassemble(int ip, int opcode, int sp, int dsp) {
        String name = Opcode.get(opcode).getName();
        int numOperands = Opcode.get(opcode).getNumOperands();
        String instruction = String.format("%04d: %-10s", ip, name);

        if (numOperands == 1) {
            if (name.equals("CALL")) {
                instruction += metadata[instructions[ip + 1]].getName();
            } else {
                instruction += instructions[ip + 1];
            }
        } else if (numOperands == 2) {
            instruction += instructions[ip + 1];
            instruction += ", ";
            instruction += instructions[ip + 2];
        } else if (numOperands == 8) {
            byte[] bytes = new byte[8];
            for (int i = 0; i < 8; i++) {
                bytes[i] = (byte) instructions[ip + 1 + i];
            }
            instruction += Utilities.bytesToLong(bytes);
        }
        return String.format("%-45s%s", instruction,((printStack)? getStackString(dsp):""));
    }

    public void disassembleAndPrint(int ip, int opcode, int sp, int dsp) {

        System.out.println(disassemble(ip, opcode, sp, dsp));
    }

    public String getStackString(int sp) {
        List<Long> stackString = new ArrayList<>();
        for (int i = 0; i <= sp; i++) {
            stackString.add(stack[i]);
        }
        return stackString.toString();
    }
}
