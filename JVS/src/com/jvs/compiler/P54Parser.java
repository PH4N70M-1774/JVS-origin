package com.jvs.compiler;

import java.util.List;

import com.jvs.compiler.nodes.ProgramNode;

public class P54Parser implements Parser {
    public ProgramNode parse(List<Token> tokens) {
        return new ProgramNode("Test");
    }
}
