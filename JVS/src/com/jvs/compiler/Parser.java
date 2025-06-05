package com.jvs.compiler;

import java.util.List;

public interface Parser
{
    public ParsedInstruction parse(List<Token> tokens);
}
