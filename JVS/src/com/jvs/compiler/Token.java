package com.jvs.compiler;

public class Token
{
    private final TokenType type;
    private final String value;

    /**
     * Constructs a Token with the specified type.
     *
     * @param type  The type of the token.
     */
    public Token(TokenType type)
    {
        this(type, "");
    }

    /**
     * Constructs a Token with the specified type and value.
     *
     * @param type  The type of the token.
     * @param value The value of the token.
     */
    public Token(TokenType type, String value)
    {
        this.type = type;
        this.value = value;
    }

    public TokenType getType()
    {
        return type;
    }

    public String getValue()
    {
        return value;
    }
}
