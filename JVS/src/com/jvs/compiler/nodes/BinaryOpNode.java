package com.jvs.compiler.nodes;

import com.jvs.compiler.TokenType;

public class BinaryOpNode implements Node {
    public Node left, right;
    public TokenType operator;

    public BinaryOpNode(Node left, TokenType operator, Node right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    private char getOpChar() {
        return switch(operator) {
            case PLUS -> '+';
            case MINUS -> '-';
            case MUL -> '*';
            case DIV -> '/';
            case MOD -> '%';
            case POW -> '^';
            default -> '\0';
        };
    }

    @Override
    public String toString() {
        return "BinaryOpNode("+getOpChar()+")";
    }
}
