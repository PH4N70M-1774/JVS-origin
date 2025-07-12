package com.jvs.velox;

public class Opcode
{
    public static final short EXIT=0;
    public static final short ICONST=1;
    public static final short STORE=2;
    public static final short LOAD=3;
    public static final short IADD=4;
    public static final short ISUB=5;
    public static final short IMUL=6;
    public static final short IDIV=7;
    public static final short IMOD=8;
    public static final short PRINT=9;
    public static final short PRINTSTR=10;
    public static final short ICMPE=11;
    public static final short ICMPL=12;
    public static final short ICMPLE=13;
    public static final short ICMPG=14;
    public static final short ICMPGE=15;
    public static final short ICMPNE=16;
    public static final short BRANCH=17;
    public static final short BRANCHT=18;
    public static final short BRANCHF=19;
    public static final short CALL=20;
    public static final short RET=21;
    public static final short POP=22;
    public static final short GSTORE=23;
    public static final short GLOAD=24;
    public static final short PRINTSP=25;
    public static final short JUMPNEXT=26;

    private static Instruction instructions[]={
        new Instruction("EXIT", 0),
        new Instruction("ICONST", 1),
        new Instruction("STORE", 1),
        new Instruction("LOAD", 1),
        new Instruction("IADD", 0),
        new Instruction("ISUB", 0),
        new Instruction("IMUL", 0),
        new Instruction("IDIV", 0),
        new Instruction("IMOD", 0),
        new Instruction("PRINT", 0),
        new Instruction("PRINTSTR", 1),
        new Instruction("ICMPE", 0),
        new Instruction("ICMPL", 0),
        new Instruction("ICMPLE", 0),
        new Instruction("ICMPG", 0),
        new Instruction("ICMPGE", 0),
        new Instruction("ICMPNE", 0),
        new Instruction("BRANCH", 1),
        new Instruction("BRANCHT", 1),
        new Instruction("BRANCHF", 1),
        new Instruction("CALL", 2),
        new Instruction("RET", 0),
        new Instruction("POP", 0),
        new Instruction("GSTORE", 1),
        new Instruction("GLOAD", 1),
        new Instruction("PRINTSP", 1),
        new Instruction("JUMPNEXT", 0)
    };

    public static Instruction get(int opcode)
    {
        return instructions[opcode];
    }

    public static class Instruction
    {
        private String name;
        private int numOperands;

        public Instruction(String name, int numOperands)
        {
            this.name=name;
            this.numOperands=numOperands;
        }

        public String getName()
        {
            return name;
        }

        public int getNumOperands()
        {
            return numOperands;
        }
    }
}
