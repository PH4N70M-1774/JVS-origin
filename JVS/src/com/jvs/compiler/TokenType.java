package com.jvs.compiler;

public enum TokenType
{
    IF, ELIF, ELSE, SWITCH, CASE, DEFAULT, WHILE, FOR, DO, BREAK, CONTINUE, RETURN, EVER, TIMES,
    FUNC, IMPORT, RETURNS, CONST, INT, FLOAT, BOOLEAN, CHAR, TRUE, FALSE, NULL, SELF, SUPER,
    JVS, INTERFACE, ENUM,
    PUBLIC, PRIVATE, STATIC, FINAL, NATIVE, SYNCHRONIZED,
    NEW, INHERITS, IMPLEMENTS, OVERRIDE,

    IDENTIFIER, NUM_LITERAL, STRING_LITERAL, CHAR_LITERAL,
    PLUS, MINUS, MUL, DIV, MOD, POWER, ASSIGN, PLUS_ASSIGN, MINUS_ASSIGN, MUL_ASSIGN, DIV_ASSIGN, MOD_ASSIGN,
    EQUAL, NOT_EQUAL, LESS_THAN, GREATER_THAN, LESS_EQUAL, GREATER_EQUAL,
    AND, OR, NOT;
}
