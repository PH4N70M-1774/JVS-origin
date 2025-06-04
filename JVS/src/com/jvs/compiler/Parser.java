package com.jvs.compiler;

import java.util.List;

public interface Parser
{
    public AST parse(List<Token> tokens);
}
