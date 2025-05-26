package com.jvs.archvm;

/**
 * The Context class is used to keep track of method calls.
 * It stores a string representation of the calls made, with the most recent call listed last.
 */
public class Context
{
    /**
     * A string that stores the method calls.
     */
    private String calls;

    /**
     * A boolean variable to check if the call is the first one or not.
     */
    private boolean isFirst;

    /**
     * Constructs a new Context object and initializes the calls string.
     * It initializes the calls string.
     */
    public Context()
    {
        isFirst=true;
        calls = "File: <STDIO>\n"+
                "Method calls (Most recent call last):";
    }

    /**
     * Constructs a new Context object and initializes the calls string
     * with the file name specified.
     *
     * @param fileName The name of the file. (<STDIO> used if null or empty.)
     */
    public Context(String fileName)
    {
        isFirst=true;
        fileName=((fileName==null||fileName=="")?"<STDIO>":fileName);
        calls = "File: "+fileName+'\n'+
                "Method calls (Most recent call last):";
    }

    /**
     * Constructs a new Context object and initializes the calls string
     * with the method name specified.
     *
     * @param methodName The name of the starting method. (main() used if null or empty..)
     * @param line The line number of method.
     */
    public Context(String methodName, int line)
    {
        this();
        addCall(methodName, line);
        isFirst=false;
    }

    /**
     * Constructs a new Context object and initiates the calls string
     * with the file name & method name specified.
     *
     * @param fileName The name of the file. (<STDIO> used if null or empty.)
     * @param methodName The name of the starting method. (main() used if null or empty.)
     * @param line The line number of method.
     */
    public Context(String fileName, String methodName, int line)
    {
        this(fileName);
        addCall(methodName, line);
        isFirst=false;
    }

    /**
     * Adds a method call to the calls string.
     *
     * @param call The name of the method call to add.
     * @param line The line number where the method call occurred.
     */
    public void addCall(String call, int line)
    {
        calls += ((isFirst)?'\n':",\n");
        calls += "    Method call: "+call+", Line: "+line+"";
    }

    /**
     * Clears the calls string, resetting it to its initial state.
     */
    public void clearCalls()
    {
        calls = "Method calls (Most recent call last):\n"+
                "    File: <STDIO>, Line: 1";
    }

    /**
     * Returns the string representation of the calls.
     *
     * @return The calls string.
     */
    @Override
    public String toString()
    {
        return calls;
    }
}