package com.jvs.velox;

import java.io.FileInputStream;

public class VeloxLoader {
    public static VeloxInstructions load(String fileName) throws VeloxVMError {

        if (!(fileName.endsWith(".jvelox"))) {
            throw new VeloxVMError("Invalid file format. Only \".jvelox\" files allowed.", null);
        }

        try (FileInputStream fis = new FileInputStream(fileName)) {
            if (!(fis.read() == 74 && fis.read() == 86 && fis.read() == 76 && fis.read() == 88)) {
                throw new VeloxVMError("Invalid file content.", null);
            }

            int instructions[];
            String pool[];
            int poolLength = 0;
            FunctionMeta metadata[];

            int numOfMethods = fis.read();

            metadata = new FunctionMeta[numOfMethods];
            for (int i = 0; i < numOfMethods; i++) {
                int nameLength = fis.read();
                String name = "";
                for (int j = 0; j < nameLength; j++) {
                    name += ((char) fis.read());
                }
                int nArgs = fis.read();
                int nLocals = fis.read();
                int address = fis.read();
                metadata[i] = new FunctionMeta(name, nArgs, nLocals, address);
            }
            poolLength = fis.read();

            pool = new String[poolLength];
            for (int i = 0; i < poolLength; i++) {
                int length = fis.read();
                String s = "";
                for (int j = 0; j < length; j++) {
                    s += (char) fis.read();
                }
                pool[i] = s;
            }

            int start = fis.read();

            instructions = new int[fis.available()];
            for (int i = 0; i < instructions.length; i++) {
                instructions[i] = fis.read();
            }

            return new VeloxInstructions(instructions, pool, poolLength, metadata, start);
        } catch (Exception e) {
            throw new VeloxVMError(e.getMessage(), e);
        }
    }
}
