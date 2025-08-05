package com.jvs.velox;

public class Opcode {
    public static final short exit = 0;
    public static final short iconst = 1;
    public static final short store = 2;
    public static final short load = 3;
    public static final short iadd = 4;
    public static final short isub = 5;
    public static final short imul = 6;
    public static final short idiv = 7;
    public static final short imod = 8;
    public static final short print = 9;
    public static final short printstr = 10;
    public static final short icmpe = 11;
    public static final short icmpl = 12;
    public static final short icmple = 13;
    public static final short icmpg = 14;
    public static final short icmpge = 15;
    public static final short icmpne = 16;
    public static final short branch = 17;
    public static final short brancht = 18;
    public static final short branchf = 19;
    public static final short invoke = 20;
    public static final short ret = 21;
    public static final short pop = 22;
    public static final short gstore = 23;
    public static final short gload = 24;
    public static final short printsp = 25;
    public static final short jumpnext = 26;
    public static final short and = 27;
    public static final short or = 28;
    public static final short not = 29;
    public static final short xor = 30;

    private static Instruction instructions[] = {
            new Instruction("exit", 0),
            new Instruction("iconst", 1),
            new Instruction("store", 1),
            new Instruction("load", 1),
            new Instruction("iadd", 0),
            new Instruction("isub", 0),
            new Instruction("imul", 0),
            new Instruction("idiv", 0),
            new Instruction("imod", 0),
            new Instruction("print", 0),
            new Instruction("printstr", 1),
            new Instruction("icmpe", 0),
            new Instruction("icmpl", 0),
            new Instruction("icmple", 0),
            new Instruction("icmpg", 0),
            new Instruction("icmpge", 0),
            new Instruction("icmpne", 0),
            new Instruction("branch", 1),
            new Instruction("brancht", 1),
            new Instruction("branchf", 1),
            new Instruction("invoke", 1),
            new Instruction("ret", 0),
            new Instruction("pop", 0),
            new Instruction("gstore", 1),
            new Instruction("gload", 1),
            new Instruction("printsp", 1),
            new Instruction("jumpnext", 0),
            new Instruction("and", 0),
            new Instruction("or", 0),
            new Instruction("not", 0),
            new Instruction("xor", 0)
    };

    public static Instruction get(int opcode) {
        return instructions[opcode];
    }

    public static class Instruction {
        private String name;
        private int numOperands;

        public Instruction(String name, int numOperands) {
            this.name = name;
            this.numOperands = numOperands;
        }

        public String getName() {
            return name;
        }

        public int getNumOperands() {
            return numOperands;
        }
    }
}
