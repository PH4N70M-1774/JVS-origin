package com.jvs.compiler;

import java.util.List;

public interface Lexer
{
    public String scan();
    public List<Token> tokenize(String input) throws LexingException;
    public char advance();
    public char peek();
    public char peek(int position);
    public boolean check(char expected);
    public boolean check(String expected);
    public String makeNumber();
    public String makeIdentifier();
}
