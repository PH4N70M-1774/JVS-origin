package com.jvs.archvm;

import java.util.Map;

import com.jvs.bytecode.*;

/**
 * The JVSEInstructions class manages a collection of instructions and methods.
 * It provides methods to retrieve instructions and method indices, as well as counts of instructions and methods.
 */
public class JVSEInstructions
{
    private Map<Integer, Instruction> instructions;
    private Map<String, Integer> methods;
    private int instructionCount, methodCount;

    /**
     * Constructs a new JVSEInstructions object with the specified instructions and methods.
     *
     * @param instructions A map of instruction indices to Instruction objects.
     * @param methods A map of method names to their corresponding indices.
     */
    public JVSEInstructions(Map<Integer, Instruction> instructions, Map<String, Integer> methods)
    {
        this.instructions = instructions;
        this.methods = methods;
        instructionCount = instructions.size();
        methodCount = methods.size();
    }

    /**
     * Retrieves the instruction at the specified index.
     *
     * @param index The index of the instruction to retrieve.
     * @return The Instruction object at the specified index.
     */
    public Instruction getInstruction(int index)
    {
        return instructions.get(index);
    }

    /**
     * Retrieves the index of the specified method name.
     *
     * @param methodName The name of the method to retrieve the index for.
     * @return The index of the specified method name.
     */
    public int getMethodIndex(String methodName)
    {
        return methods.get(methodName);
    }

    /**
     * Returns the total number of instructions.
     *
     * @return The number of instructions.
     */
    public int getInstructionCount()
    {
        return instructionCount;
    }

    /**
     * Returns the total number of methods.
     *
     * @return The number of methods.
     */
    public int getMethodCount()
    {
        return methodCount;
    }

    @Override
    public String toString()
    {
        String toString="";
        toString+="Instructions:\n[\n";
        for(int i=1;i<=getInstructionCount();i++)
        {
            toString+="\t"+getInstruction(i)+"\n";
        }
        toString+="]";

        return toString;
    }
}