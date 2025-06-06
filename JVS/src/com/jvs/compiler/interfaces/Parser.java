package com.jvs.compiler.interfaces;

import java.util.List;

import com.jvs.compiler.ParsedInstruction;
import com.jvs.compiler.Token;

public interface Parser
{
    public ParsedInstruction parse(List<Token> tokens);
}
