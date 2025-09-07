package com.jvs.compiler;

public class Token {
    private TokenType type;
    private String lexeme;

    public Token(TokenType type) {
        this(type, null);
    }

    public Token(TokenType type, String lexeme) {
        this.type = type;
        this.lexeme = lexeme;
    }

    public TokenType getType() {
        return type;
    }

    public String getLexeme() {
        return lexeme;
    }

    @Override
    public String toString(){
        return type+((lexeme==null)?"":"("+lexeme+")");
    }
}
