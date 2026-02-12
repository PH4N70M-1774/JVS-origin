package com.jvs.velox;

public class VeloxInstructions {
    private int instructions[];
    private String pool[];
    private int poolLength;
    private FunctionMeta[] metadata;
    private int start;

    public VeloxInstructions(int instructions[], String pool[], int poolLength, FunctionMeta metadata[], int start) {
        this.instructions = instructions;
        this.pool = pool;
        this.poolLength = poolLength;
        this.metadata = metadata;
        this.start = start;
    }

    public int[] getInstructions() {
        return instructions;
    }

    public String[] getPool() {
        return pool;
    }

    public int getPoolLength() {
        return poolLength;
    }

    public FunctionMeta[] getMetadata() {
        return metadata;
    }

    public int getStartIP() {
        return start;
    }

    public void printContent() {
        System.out.println("============================================FILE CONTENT============================================");
        System.out.println("Function Metadata:");
        for (int i = 0; i < metadata.length; i++) {
            System.out.printf("  %04d: %s", i, metadata[i].toString());
            System.out.println(((i < (metadata.length - 1)) ? "," : ""));
        }

        System.out.println("\nPool:");
        System.out.println("  Length: " + poolLength);
        System.out.println("  Content:");
        for (int i = 0; i < pool.length; i++) {
            System.out.printf("    %04d: \"%s\"", i, Utilities.getPrintString(pool[i]));
            System.out.println(((i < (pool.length - 1)) ? "," : ""));
        }

        System.out.println("\nStart Index: " + start);

        System.out.println("\nInstructions:");
        for (int i = 0; i < instructions.length; i++) {
            int opcode = instructions[i];
            String name = Opcode.get(opcode).getName();
            int numOperands = Opcode.get(opcode).getNumOperands();

            if (i == 0) {
                for (int j = 0; j < metadata.length; j++) {
                    if (metadata[j].getAddress() == 0) {
                        System.out.println("  Function \"" + metadata[j].getName() + "\":");
                    }
                }
            }

            String instruction = String.format("    %04d: %-10s", i, name);

            if (numOperands == 1) {
                if (name.equals("invoke")) {
                    instruction += metadata[instructions[i + 1]].getName();
                } else {
                    instruction += instructions[i + 1];
                }
            } else if (numOperands == 2) {
                instruction += instructions[i + 1];
                instruction += ", ";
                instruction += instructions[i + 2];
            } else if (numOperands == 8) {
                byte[] bytes = new byte[8];
                for (int j = 0; j < 8; j++) {
                    bytes[i] = (byte) instructions[i + 1 + j];
                }
                instruction += Utilities.bytesToLong(bytes);
            }
            System.out.println(instruction);

            if (name.equals("ret")) {
                for (int j = 0; j < metadata.length; j++) {
                    if (metadata[j].getAddress() == (i + numOperands + 1)) {
                        System.out.println("  Function \"" + metadata[j].getName() + "\":");
                    }
                }
            }

            i += numOperands;
        }
        System.out.println("====================================================================================================");
    }
}
