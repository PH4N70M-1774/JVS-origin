package com.jvs.compiler;

import java.util.List;

import com.jvs.compiler.nodes.*;

public interface Parser {
    public ProgramNode parse(List<Token> tokens);
}
