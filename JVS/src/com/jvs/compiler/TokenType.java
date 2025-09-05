package com.jvs.compiler;

public enum TokenType {
    // --------------------
    // Keywords
    // --------------------
    PACKAGE, IMPORT, JVS, FUNC, VAR, CONST, RETURN_TYPE, RETURN,
    INHERITS, IMPLEMENTS, ANNOTATION, INIT,
    IF, ELIF, ELSE, SWITCH, CASE, DEFAULT,
    FOR, FOR_EACH, FOR_EVER, FOR_TIMES, WHILE, DO,
    BREAK, CONTINUE,
    TRY, CATCH, FINALLY, THROW, THROWS,
    PUBLIC, PRIVATE, STATIC, FINAL, NATIVE,
    PRINT, PRINTLN,

    // --------------------
    // Primitive Types
    // --------------------
    INT, FLOAT, CHAR, BOOLEAN,

    // --------------------
    // Identifiers & Literals
    // --------------------
    IDENTIFIER,
    INT_LITERAL, FLOAT_LITERAL, CHAR_LITERAL, STRING_LITERAL, BOOLEAN_LITERAL,

    // --------------------
    // Operators
    // --------------------
    PLUS, MINUS, MUL, DIV, MOD, POW,
    INCREMENT, DECREMENT,
    SWAP,

    ASSIGN,

    EQUALS, NOT_EQUALS, LT, GT, LTE, GTE,
    AND, OR, NOT, XOR,

    // --------------------
    // Separators / Punctuation
    // --------------------
    LPAREN, RPAREN, // ( )
    LBRACE, RBRACE, // { }
    LBRACKET, RBRACKET, // [ ]
    COMMA, SEMICOLON, DOT, COLON, ARROW, // ->

    // --------------------
    // Comments
    // --------------------
    LINE_COMMENT, BLOCK_COMMENT,

    // --------------------
    // Special
    // --------------------
    EOF, WHITESPACE;

    public static TokenType getTokenType(String ident) {
        return switch (ident) {
            case "package" -> TokenType.PACKAGE;
            case "import" -> TokenType.IMPORT;
            case "jvs" -> TokenType.JVS;
            case "func" -> TokenType.FUNC;
            case "var" -> TokenType.VAR;
            case "const" -> TokenType.CONST;
            case "inherits" -> TokenType.INHERITS;
            case "implements" -> TokenType.IMPLEMENTS;
            case "annotation" -> TokenType.ANNOTATION;
            case "_init" -> TokenType.INIT; // constructor
            case "if" -> TokenType.IF;
            case "elif" -> TokenType.ELIF;
            case "else" -> TokenType.ELSE;
            case "switch" -> TokenType.SWITCH;
            case "case" -> TokenType.CASE;
            case "default" -> TokenType.DEFAULT;
            case "for" -> TokenType.FOR;
            case "for each" -> TokenType.FOR_EACH;
            case "for ever" -> TokenType.FOR_EVER;
            case "for times" -> TokenType.FOR_TIMES;
            case "while" -> TokenType.WHILE;
            case "do" -> TokenType.DO;
            case "break" -> TokenType.BREAK;
            case "continue" -> TokenType.CONTINUE;
            case "try" -> TokenType.TRY;
            case "catch" -> TokenType.CATCH;
            case "finally" -> TokenType.FINALLY;
            case "throw" -> TokenType.THROW;
            case "throws" -> TokenType.THROWS;
            case "public" -> TokenType.PUBLIC;
            case "private" -> TokenType.PRIVATE;
            case "static" -> TokenType.STATIC;
            case "final" -> TokenType.FINAL;
            case "native" -> TokenType.NATIVE;
            case "print" -> TokenType.PRINT;
            case "println" -> TokenType.PRINTLN;
            case "int" -> TokenType.INT;
            case "float" -> TokenType.FLOAT;
            case "char" -> TokenType.CHAR;
            case "boolean" -> TokenType.BOOLEAN;
            default -> TokenType.IDENTIFIER; // fallback for non-keywords
        };
    }
}
