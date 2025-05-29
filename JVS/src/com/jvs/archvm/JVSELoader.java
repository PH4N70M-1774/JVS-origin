package com.jvs.archvm;

import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

import com.jvs.bytecode.Instruction;

/**
 * The JVSELoader class provides a method to load instructions and methods from a file.
 */
public class JVSELoader
{
    /**
     * Loads instructions and methods from the specified file path.
     *
     * @param filePath The path to the file containing the instructions and methods.
     * @return A JVSEInstructions object containing the loaded instructions and methods.
     */
    public static JVSEInstructions load(String filePath)
    {
        Map<Integer, Instruction> instructions = new HashMap<>();
        Map<String, Integer> methods = new HashMap<>();
        int lineCount = 1;
        try
        {
            File file = new File(filePath);
            Scanner sc = new Scanner(file);
            while(sc.hasNextLine())
            {
                String line = sc.nextLine().trim();
                if(line.startsWith("FUNCDEC")||line.startsWith("funcdec"))
                {
                    methods.put(line.substring(8), lineCount);
                    instructions.put(lineCount, new Instruction(line, false));
                }
                else if(line.equals("CLRMAINL")||line.equals("clrmainl"))
                {
                    instructions.put(lineCount, new Instruction("clrmainl", true));
                }
                else if(line.equals("CLRTLIST")||line.equals("clrtlist"))
                {
                    instructions.put(lineCount, new Instruction("clrtlist", true));
                }
                else if(line.equalsIgnoreCase("eof"))
                {
                    instructions.put(lineCount, new Instruction("eof", true));
                }
                else
                {
                    instructions.put(lineCount, new Instruction(line, false));
                }
                lineCount++;
            }
            sc.close();
        }
        catch(FileNotFoundException e)
        {
            System.out.println("File not found");
        }
        return new JVSEInstructions(instructions, methods);
    }
}