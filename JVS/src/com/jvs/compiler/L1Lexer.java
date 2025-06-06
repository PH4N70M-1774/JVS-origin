package com.jvs.compiler;

import java.util.Scanner;

import com.jvs.compiler.interfaces.Lexer;

import java.util.List;
import java.util.ArrayList;

import java.io.File;
import java.io.FileNotFoundException;

public class L1Lexer implements Lexer
{
    private int start, end, current;
    private Scanner sc;
    private String line;
    private char[] characters;
    private boolean skip;

    public L1Lexer(String file) throws FileNotFoundException
    {
        sc=new Scanner(new File(file));
        line=sc.nextLine();
        characters=line.toCharArray();
        start=0;
        end=line.length();
        skip=false;
    }

    public L1Lexer(String line, int i)
    {
        this.line=line;
        characters=line.toCharArray();
        start=0;
        end=line.length();
        System.out.println(start);
        System.out.println(end);
    }

    @Override
    public List<Token> tokenize() throws LexingException
    {
        List<Token> tokens=new ArrayList<>();

        for(current=start;current<end;current++)
        {
            char c=characters[current];
            System.out.println(c);
            if(skip)
            {
                skip=false;
                current+=1;
                continue;
            }
            switch(c)
            {
                case '+'->
                    {
                        if(peek()=='=')
                        {
                            tokens.add(new Token(TokenType.PLUS_ASSIGN));
                            skip=true;
                        }
                        else
                        {
                            tokens.add(new Token(TokenType.PLUS));
                        }
                    }
                case '-'->
                    {
                        if(peek()=='=')
                        {
                            tokens.add(new Token(TokenType.MINUS_ASSIGN));
                            skip=true;
                        }
                        else
                        {
                            tokens.add(new Token(TokenType.MINUS));
                        }
                    }
                case '*'->
                    {
                        if(peek()=='=')
                        {
                            tokens.add(new Token(TokenType.MUL_ASSIGN));
                            skip=true;
                        }
                        else
                        {
                            tokens.add(new Token(TokenType.MUL));
                        }
                    }
                case '/'->
                    {
                        if(peek()=='=')
                        {
                            tokens.add(new Token(TokenType.DIV_ASSIGN));
                            skip=true;
                        }
                        else
                        {
                            tokens.add(new Token(TokenType.DIV));
                        }
                    }
                case '%'->
                    {
                        if(peek()=='=')
                        {
                            tokens.add(new Token(TokenType.MOD_ASSIGN));
                            skip=true;
                        }
                        else
                        {
                            tokens.add(new Token(TokenType.MOD));
                        }
                    }
                case '^'->
                    {
                        tokens.add(new Token(TokenType.POWER));
                    }
                case '='->
                    {
                        if(peek()=='=')
                        {
                            tokens.add(new Token(TokenType.EQUAL));
                            skip=true;
                        }
                        else
                        {
                            tokens.add(new Token(TokenType.ASSIGN));
                        }
                    }
                case '!'->
                    {
                        if(peek()=='=')
                        {
                            tokens.add(new Token(TokenType.NOT_EQUAL));
                            skip=true;
                        }
                        else
                        {
                            tokens.add(new Token(TokenType.NOT));
                        }
                    }
                case '<'->
                    {
                        if(peek()=='=')
                        {
                            tokens.add(new Token(TokenType.LESS_EQUAL));
                            skip=true;
                        }
                        else
                        {
                            tokens.add(new Token(TokenType.LESS_THAN));
                        }
                    }
                case '>'->
                    {
                        if(peek()=='=')
                        {
                            tokens.add(new Token(TokenType.GREATER_EQUAL));
                            skip=true;
                        }
                        else
                        {
                            tokens.add(new Token(TokenType.GREATER_THAN));
                        }
                    }
                case '&'->
                    {
                        tokens.add(new Token(TokenType.AND));
                    }
                case '|'->
                    {
                        tokens.add(new Token(TokenType.OR));
                    }
                default->{System.out.println(c);}
            }
        }

        return tokens;
    }

    @Override
    public String scan()
    {
        return sc.nextLine();
    }

    public char advance()
    {
        if(current==end)return '#';
        return line.charAt(current++);
    }

    public void increment()
    {
        current+=1;
    }

    @Override
    public String makeIdentifier()
    {
        return null;
    }

    @Override
    public String makeNumber()
    {
        return null;
    }

    @Override
    public char peek()
    {
        if(current+1>=end) return ' ';
        return line.charAt(current + 1);
    }

    @Override
    public char peek(int position)
    {
        return ' ';
    }

    public boolean hasMoreLines()
    {
        return sc.hasNextLine();
    }
}
