package com.jvs.compiler.nodes;

public class NumberNode implements Node {
    public String value;
    public boolean isFloat;

    public NumberNode(String value, boolean isFloat) {
        this.value = value;
        this.isFloat = isFloat;
    }

    public int intValue() {
        return Integer.valueOf(value);
    }

    public double doubleValue() {
        return Double.valueOf(value);
    }

    @Override
    public String toString() {
        return "NumberNode(" + value + ")";
    }
}
