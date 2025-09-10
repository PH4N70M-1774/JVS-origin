package com.jvs.compiler.nodes;

public class IdentifierNode implements Node {
    public String name;

    public IdentifierNode(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "IdentifierNode(\""+name+"\")";
    }
}
