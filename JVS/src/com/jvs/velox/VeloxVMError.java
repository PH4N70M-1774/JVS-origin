package com.jvs.velox;

public class VeloxVMError extends Exception {
    private String msg;

    public VeloxVMError(String msg, Exception cause) {
        super(msg, cause);
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "\n|Error in VeloxVM: " + msg;
    }
}
