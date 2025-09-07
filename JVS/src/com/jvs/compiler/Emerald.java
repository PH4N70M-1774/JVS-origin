package com.jvs.compiler;

import java.util.List;

public class Emerald implements CompilerModel {
    private String filePath, fileName;

    private Emerald(String filePath) {
        this.filePath = filePath;
        if (filePath.lastIndexOf('/') == -1) {
            if (filePath.lastIndexOf('\\') == -1) {
                this.fileName = filePath;
            } else {
                this.fileName = filePath.substring(filePath.lastIndexOf('\\') + 1);
            }
        } else {
            this.fileName = filePath.substring(filePath.lastIndexOf('/') + 1);
        }
        System.out.println(filePath);
        System.out.println(fileName);
    }

    public static CompilerModel getModel(String filePath) {
        return new Emerald(filePath);
    }

    public void compile() throws Exception {
        // Scanner - Scanning file.
        String fileSrc = FileScanner.getSource(filePath);

        // Lexer - Tokenizing file.
        Lexer lexer = L1Lexer.getLexer(fileSrc, fileName);
        List<Token> tokens = lexer.tokenize();

        // Parser - Parsing tokens.
        // Printing the tokens since parser is not yet implemented.
        for (Token token : tokens) {
            System.out.println(token);
        }
    }
}
