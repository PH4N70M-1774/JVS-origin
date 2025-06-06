package com.jvs.compiler.interfaces;

import java.util.List;

import com.jvs.compiler.LexingException;
import com.jvs.compiler.Token;

public interface Lexer
{
    public String scan();
    public List<Token> tokenize() throws LexingException;
    public char peek();
    public char peek(int position);
    public String makeNumber();
    public String makeIdentifier();
}
