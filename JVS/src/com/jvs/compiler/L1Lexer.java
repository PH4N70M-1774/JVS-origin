package com.jvs.compiler;

import java.util.ArrayList;
import java.util.List;

public class L1Lexer {

    private final String source;
    private final String fileName;
    private int current = 0; // index in source
    private int line = 1; // current line
    private int column = 1; // current column

    private int tokenStartColumn = 1;
    private int tokenEndColumn = 1; // column where token ends

    public L1Lexer(String source, String fileName) {
        this.source = source;
        this.fileName = fileName;
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        int start = 0, end = 0;
        while (!isAtEnd()) {
            skipWhitespace();

            // Start of a new token
            tokenStartColumn = column; // column BEFORE consuming first char

            char ch = advance(); // advance increments column

            switch (ch) {
                case '+' -> tokens.add(new Token(TokenType.PLUS));
                case '-' -> tokens.add(new Token(TokenType.MINUS));
                case '*' -> tokens.add(new Token(TokenType.MUL));
                case '/' -> handleSlash(tokens);
                case '%' -> tokens.add(new Token(TokenType.MOD));
                case '^' -> tokens.add(new Token(TokenType.POW));

                case '<' -> handleLess(tokens);
                case '>' -> handleGreater(tokens);
                case '=' -> handleEqual(tokens);
                case '!' -> handleNot(tokens);
                case '&' -> handleAnd(tokens);
                case '|' -> handleOr(tokens);

                case '(' -> tokens.add(new Token(TokenType.LPAREN));
                case ')' -> tokens.add(new Token(TokenType.RPAREN));
                case '{' -> tokens.add(new Token(TokenType.LBRACE));
                case '}' -> tokens.add(new Token(TokenType.RBRACE));
                case '[' -> tokens.add(new Token(TokenType.LBRACKET));
                case ']' -> tokens.add(new Token(TokenType.RBRACKET));
                case ';' -> tokens.add(new Token(TokenType.SEMICOLON));
                case ',' -> tokens.add(new Token(TokenType.COMMA));
                case '.' -> tokens.add(new Token(TokenType.DOT));
                case ':' -> tokens.add(new Token(TokenType.COLON));

                default -> {
                    // Handle identifiers, numbers, strings, or errors
                    if (Character.isLetter(ch) || ch == '_') {
                        String ident = readIdentifier(ch); // read first word
                        TokenType type = TokenType.getTokenType(ident);

                        // Check multi-word keywords starting with "for"
                        if (ident.equals("for")) {
                            // peek next non-whitespace character
                            int saveCurrent = current;
                            int saveColumn = column;

                            skipWhitespace();
                            StringBuilder nextWord = new StringBuilder();

                            while (!isAtEnd() && (Character.isLetter(peek()) || peek() == '_')) {
                                nextWord.append(advance());
                            }

                            String combined = "for " + nextWord;

                            type = switch (combined) {
                                case "for each" -> TokenType.FOR_EACH;
                                case "for ever" -> TokenType.FOR_EVER;
                                case "for times" -> TokenType.FOR_TIMES;
                                default -> type; // just "for"
                            };

                            if (type != TokenType.FOR) {
                                ident = combined; // use combined as token text
                            } else {
                                // reset if it wasn’t a multi-word keyword
                                current = saveCurrent;
                                column = saveColumn;
                            }
                        }
                        if (type == TokenType.IDENTIFIER) {
                            tokens.add(new Token(type, ident));
                        } else {
                            tokens.add(new Token(type));
                        }
                    } else if (Character.isDigit(ch)) {
                        String num = readNumber(ch);
                        tokenEndColumn = tokenStartColumn + num.length() - 1;
                        tokens.add(new Token(TokenType.INT_LITERAL, num));
                    } else if (ch == '"') {
                        String str = readString();
                        tokenEndColumn = tokenStartColumn + str.length() + 1; // include quotes
                        tokens.add(new Token(TokenType.STRING_LITERAL, str));
                    } else if (!Character.isWhitespace(ch) && ch != '\0') {
                        String stmt = getLineSource(line);
                        start = tokenStartColumn - 1;
                        end = 0;
                        String e = "" + ch;
                        while (!isAtEnd() && !Character.isWhitespace(peek()) && invalid(peek())) {
                            e += advance();
                        }
                        System.out.println("\"" + e + "\"");
                        if (e.length() == 1) {
                            end = start;
                            JVSError.log(fileName, line, "Invalid character '" + e + '\'', stmt, start, end);
                        } else {
                            end = start + e.length() - 1;
                            JVSError.log(fileName, line, "Invalid sequence of characters \"" + e + '"', stmt, start,
                                    end);
                        }
                    }
                }
            }
        }

        // Add EOF token at the end
        tokens.add(new Token(TokenType.EOF));
        return tokens;
    }

    // -------------------
    // Helpers
    // -------------------

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private char advance() {
        if (isAtEnd())
            return '\0'; // sentinel
        char c = source.charAt(current++);
        if (c == '\n') {
            line++;
            column = 1;
        } else {
            column++;
        }
        return c;
    }

    private char peek() {
        return isAtEnd() ? '\0' : source.charAt(current);
    }

    private char peekNext() {
        return (current + 1 >= source.length()) ? '\0' : source.charAt(current + 1);
    }

    private void skipWhitespace() {
        while (!isAtEnd()) {
            char c = peek();
            if (c == ' ' || c == '\r' || c == '\t' || c == '\n') {
                advance();
            } else {
                break;
            }
        }
    }

    private String getLineSource(int lineNum) {
        String[] lines = source.split("\n");
        if (lineNum - 1 < lines.length)
            return lines[lineNum - 1];
        return "";
    }

    // -------------------
    // Token readers
    // -------------------

    private void handleSlash(List<Token> tokens) {
        if (peek() == '/') { // Line comment
            advance();
            while (peek() != '\n' && !isAtEnd())
                advance();
            tokenEndColumn = column - 1;
            tokens.add(new Token(TokenType.LINE_COMMENT));
        } else if (peek() == '*') { // Block comment
            advance();
            while (!(peek() == '*' && peekNext() == '/') && !isAtEnd())
                advance();

            if (!isAtEnd()) {
                advance(); // consume '*'
                advance(); // consume '/'
            }
            tokenEndColumn = column - 1;
            tokens.add(new Token(TokenType.BLOCK_COMMENT));
        } else {
            tokenEndColumn = column - 1;
            tokens.add(new Token(TokenType.DIV));
        }
    }

    private void handleLess(List<Token> tokens) {
        if (peek() == '=') {
            advance();
            tokenEndColumn = column - 1;
            tokens.add(new Token(TokenType.LTE));
        } else if (peek() == '<') {
            advance();
            tokenEndColumn = column - 1;
            tokens.add(new Token(TokenType.RETURN));
        } else {
            tokenEndColumn = column - 1;
            tokens.add(new Token(TokenType.LT));
        }
    }

    private void handleGreater(List<Token> tokens) {
        if (peek() == '=') {
            advance();
            tokenEndColumn = column - 1;
            tokens.add(new Token(TokenType.GTE));
        } else if (peek() == '<') {
            advance();
            tokenEndColumn = column - 1;
            tokens.add(new Token(TokenType.SWAP));
        } else {
            tokenEndColumn = column - 1;
            tokens.add(new Token(TokenType.GT));
        }
    }

    private void handleEqual(List<Token> tokens) {
        if (peek() == '=') {
            advance();
            tokenEndColumn = column - 1;
            tokens.add(new Token(TokenType.EQUALS));
        } else {
            tokenEndColumn = column - 1;
            tokens.add(new Token(TokenType.ASSIGN));
        }
    }

    private void handleNot(List<Token> tokens) {
        if (peek() == '=') {
            advance();
            tokenEndColumn = column - 1;
            tokens.add(new Token(TokenType.NOT_EQUALS));
        } else {
            tokenEndColumn = column - 1;
            tokens.add(new Token(TokenType.NOT));
        }
    }

    private void handleAnd(List<Token> tokens) {
        if (peek() == '&') {
            advance();
            tokenEndColumn = column - 1;
            tokens.add(new Token(TokenType.AND));
        } else {
            String stmt = getLineSource(line);
            int start = tokenStartColumn;
            int end = tokenEndColumn - 1; // inclusive
            JVSError.log(fileName, line, "Expected '&'.", stmt, start, end);
        }
    }

    private void handleOr(List<Token> tokens) {
        if (peek() == '|') {
            advance();
            tokenEndColumn = column - 1;
            tokens.add(new Token(TokenType.OR));
        } else {
            String stmt = getLineSource(line);
            int start = tokenStartColumn;
            int end = tokenEndColumn - 1; // inclusive
            JVSError.log(fileName, line, "Expected '|'.", stmt, start, end);
        }
    }

    private String readIdentifier(char first) {
        StringBuilder sb = new StringBuilder();
        sb.append(first);
        while (Character.isLetterOrDigit(peek()) || peek() == '_') {
            sb.append(advance());
        }
        return sb.toString();
    }

    private String readNumber(char first) {
        StringBuilder sb = new StringBuilder();
        sb.append(first);
        while (Character.isDigit(peek()))
            sb.append(advance());
        if (peek() == '.' && Character.isDigit(peekNext())) {
            sb.append(advance()); // dot
            while (Character.isDigit(peek()))
                sb.append(advance());
        }
        return sb.toString();
    }

    private String readString() {
        StringBuilder sb = new StringBuilder();
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n')
                line++;
            sb.append(advance());
        }
        if (!isAtEnd())
            advance(); // closing "
        return sb.toString();
    }

    private boolean invalid(char ch) {
        return !Character.isLetterOrDigit(ch) || ch != '_' || ch != '"';
    }
}
