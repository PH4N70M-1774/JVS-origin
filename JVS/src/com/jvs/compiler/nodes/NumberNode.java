package com.jvs.compiler.nodes;

public class NumberNode implements Node {
    public double value;
    public boolean isFloat;

    public NumberNode(double value, boolean isFloat) {
        this.value = value;
        this.isFloat = isFloat;
    }

    public int intValue() {
        return (int) value;
    }

    public double doubleValue() {
        return value;
    }

    @Override
    public String toString() {
        return "NumberNode(" + ((isFloat) ? value : ((int) value)) + ")";
    }
}
