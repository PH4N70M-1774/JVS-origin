package com.jvs.velox;

import java.io.FileOutputStream;

public class VeloxWriter {
    private static final byte[] veloxMagic = { 74, 86, 76, 88 };
    private String fileName;
    private int instructions[];
    private String pool[];
    private int poolLength;
    private FunctionMeta metadata[];
    private int start;

    public VeloxWriter(String fileName, int instructions[], String pool[], int poolLength, FunctionMeta metadata[],
            int start) {
        this.fileName = fileName;
        this.instructions = instructions;
        this.pool = pool;
        this.poolLength = poolLength;
        this.metadata = metadata;
        this.start = start;
    }

    public void write() throws VeloxVMError {
        try {
            FileOutputStream fos = new FileOutputStream(fileName);

            fos.write(veloxMagic);
            fos.write(metadata.length);
            for (int i = 0; i < metadata.length; i++) {
                int nameLength = metadata[i].getName().length();
                fos.write(nameLength);
                for (int j = 0; j < nameLength; j++) {
                    fos.write((int) (metadata[i].getName().charAt(j)));
                }
                fos.write(metadata[i].getNumberOfArgs());
                fos.write(metadata[i].getNumberOfLocals());
                fos.write(metadata[i].getAddress());
            }

            fos.write(poolLength);

            for (int i = 0; i < pool.length; i++) {
                fos.write(pool[i].length());
                for (int j = 0; j < pool[i].length(); j++) {
                    fos.write(pool[i].charAt(j));
                }
            }

            fos.write(start);

            for (int i = 0; i < instructions.length; i++) {
                fos.write(instructions[i]);
            }

            fos.close();
        } catch (Exception e) {
            throw new VeloxVMError(e.getMessage(), e);
        }
    }
}
