package com.jvs.compiler.nodes;

public class IntAssignNode implements Node {
    public IdentifierNode identifierNode;
    public BinaryOpNode binaryOpNode;

    public IntAssignNode(IdentifierNode identifierNode, BinaryOpNode binaryOpNode) {
        this.identifierNode = identifierNode;
        this.binaryOpNode = binaryOpNode;
    }

    @Override
    public String toString() {
        return "IntAssignNode";
    }
}
