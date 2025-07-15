//*Refer to this file for opcodes and required implementation details.

package com.jvs.bytecode;

public enum Opcode {
    PUT, SET, CAST,
    IADD, FADD,
    ISUB, FSUB,
    IMUL, FMUL,
    IDIV, FDIV,
    IMOD, FMOD,
    IPOW, FPOW,
    INCR, DECR,
    CLRTLIST, CLRMAINL,
    CMPGT,
    CMPGTE,
    CMPLT,
    CMPLTE,
    CMPE,
    CMPNE,
    AND, OR, XOR, NOT, EQ, NEQ,
    ASSERT, GOTO, CALL, RETURN,
    PRINT, PRINT_TIME, PRINT_STACK,
    // The following instructions are not implemented in the VMInterpreter class,
    // but are used to mark the start of a function.
    FUNCDEC,
    EMPTYLINE, // Used to mark empty lines in the source code.
    NATIVE,
    EOF;

    public static Opcode getOpcodeFor(String s) {
        Opcode op = switch (s) {
            case "PUT", "put" -> PUT; // Method implemented (In VMInterpreter class).
            case "SET", "set" -> SET; // Method implemented (In VMInterpreter class).
            case "CAST", "cast" -> CAST; // Method implemented (In VMInterpreter class).
            case "IADD", "iadd" -> IADD; // Method implemented (In VMInterpreter class).
            case "FADD", "fadd" -> FADD; // Method implemented (In VMInterpreter class).
            case "ISUB", "isub" -> ISUB; // Method implemented (In VMInterpreter class).
            case "FSUB", "fsub" -> FSUB; // Method implemented (In VMInterpreter class).
            case "IMUL", "imul" -> IMUL; // Method implemented (In VMInterpreter class).
            case "FMUL", "fmul" -> FMUL; // Method implemented (In VMInterpreter class).
            case "IDIV", "idiv" -> IDIV; // Method implemented (In VMInterpreter class).
            case "FDIV", "fdiv" -> FDIV; // Method implemented (In VMInterpreter class).
            case "IMOD", "imod" -> IMOD; // Method implemented (In VMInterpreter class).
            case "FMOD", "fmod" -> FMOD; // Method implemented (In VMInterpreter class).
            case "IPOW", "ipow" -> IPOW; // Method implemented (In VMInterpreter class).
            case "FPOW", "fpow" -> FPOW; // Method implemented (In VMInterpreter class).
            case "INCR", "incr" -> INCR; // Method implemented (In VMInterpreter class).
            case "DECR", "decr" -> DECR; // Method implemented (In VMInterpreter class).
            case "CMPGT", "cmpgt" -> CMPGT; // Method implemented (In VMInterpreter class).
            case "CMPGTE", "cmpgte" -> CMPGTE; // Method implemented (In VMInterpreter class).
            case "CMPLT", "cmplt" -> CMPLT; // Method implemented (In VMInterpreter class).
            case "CMPLTE", "cmplte" -> CMPLTE; // Method implemented (In VMInterpreter class).
            case "CMPE", "cmpe" -> CMPE; // Method implemented (In VMInterpreter class).
            case "CMPNE", "cmpne" -> CMPNE; // Method implemented (In VMInterpreter class).
            case "AND", "and" -> AND; // Method implemented (In VMInterpreter class).
            case "OR", "or" -> OR; // Method implemented (In VMInterpreter class).
            case "XOR", "xor" -> XOR; // Method implemented (In VMInterpreter class).
            case "NOT", "not" -> NOT; // Method implemented (In VMInterpreter class).
            case "EQ", "eq" -> EQ; // Method implemented (In VMInterpreter class).
            case "NEQ", "neq" -> NEQ; // Method implemented (In VMInterpreter class).
            case "ASSERT", "assert" -> ASSERT; // Method implemented (In VMInterpreter class).
            case "GOTO", "goto" -> GOTO; // Method implemented (In VMInterpreter class).
            case "PRINT", "print" -> PRINT; // Method implemented (In VMInterpreter class).
            case "PRINTTIME", "printTime" -> PRINT_TIME; // Method implemented (In VMInterpreter class).
            case "PRINTSTACK", "printStack" -> PRINT_STACK; // Method implemented (In VMInterpreter class).
            case "CLRTLIST", "clrtlist" -> CLRTLIST; // Method implemented (In VMInterpreter class).
            case "NATIVE", "native" -> NATIVE; // Method implemented (In VMInterpreter class).
            case "CLRMAINL", "clrmainl" -> CLRMAINL; // Method implemented (In VMInterpreter class).
            case "CALL", "call" -> CALL; // To be sorted by the VM itself, no explicit method.
            case "RETURN", "return" -> RETURN; // To be sorted by the VM itself, no explicit method.
            case "FUNCDEC", "funcdec" -> FUNCDEC; // Marker opcode, no implementation required.
            case "EOF", "eof" -> EOF; // Marker opcode, no implementation required.
            case "EMPTYLINE", "emptyline" -> EMPTYLINE; // Marker opcode, no implementation required.
            default -> EOF;
        };
        return op;
    }
}
