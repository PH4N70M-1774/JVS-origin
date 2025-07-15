package com.jvs.bytecode;

public class Instruction {
    private Opcode op;
    private String args;
    public static final int EMPTYLINE = 0; // Used to mark empty lines in the source code.
    public static final int RETURN = 1; // Used to mark return instructions in the source code.
    public static final int PRINT_STACK = 2; // Used to mark the end of the file in the source code.
    public static final int PRINT_TIME = 3; // Used to mark the end of the file in the source code.

    public Instruction(int id) {
        if (id == EMPTYLINE) {
            op = Opcode.EMPTYLINE;
            args = "";
        } else if (id == RETURN) {
            op = Opcode.RETURN;
            args = "";
        } else if (id == PRINT_STACK) {
            op = Opcode.PRINT_STACK;
            args = "";
        } else if (id == PRINT_TIME) {
            op = Opcode.PRINT_TIME;
            args = "";
        } else {
            throw new IllegalArgumentException("Invalid instruction ID: " + id);
        }
    }

    public Instruction(String instruction, boolean isWithoutArgs) {
        instruction = instruction.trim();
        if (isWithoutArgs) {
            op = Opcode.getOpcodeFor(instruction);
            args = "";
        } else {
            op = Opcode.getOpcodeFor(instruction.substring(0, instruction.indexOf(" ")));
            args = instruction.substring((instruction.indexOf(' ') + 1));
        }
    }

    public Opcode getOpcode() {
        return op;
    }

    public String getArgs() {
        return args;
    }

    @Override
    public String toString() {
        return op + " " + args;
    }
}