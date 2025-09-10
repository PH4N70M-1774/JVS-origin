package com.jvs.compiler;

import java.util.List;

import com.jvs.compiler.nodes.*;

public class P54Parser implements Parser {

    private List<Token> tokens;

    private P54Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public static Parser getParser(List<Token> tokens) {
        return new P54Parser(tokens);
    }

    public ProgramNode parse() {
        for (Token token : tokens) {
            System.out.println(token);
        }

        ProgramNode programNode = new ProgramNode("Test.jvs");

        IdentifierNode identifierNode = new IdentifierNode("a");

        NumberNode n1 = new NumberNode("10", false);
        NumberNode n2 = new NumberNode("20", false);
        NumberNode n3 = new NumberNode("50", false);

        BinaryOpNode binop1 = new BinaryOpNode(n1, TokenType.MUL, n2);
        BinaryOpNode binop = new BinaryOpNode(binop1, TokenType.PLUS, n3);

        IntAssignNode intAssignNode = new IntAssignNode(identifierNode, binop);

        programNode.addNode(intAssignNode);

        PrintNode printNode = new PrintNode();

        printNode.addNode(identifierNode);
        printNode.addNode(identifierNode);

        programNode.addNode(printNode);

        return programNode;
    }
}
