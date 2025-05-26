package com.jvs.archvm;

import java.util.Map;
import java.util.HashMap;

/**
 * The VarManager class in Java is designed to manage variables of different types (int, float, char, boolean)
 * with dynamic arrays and a map for efficient storage and retrieval.
 */
public class VarManager
{
    private static final int INITIAL_CAPACITY = 4;

    private Map<String, Type> varMap;
    private String[] intNames, floatNames, charNames, boolNames;
    private long[] intValues;
    private double[] floatValues;
    private char[] charValues;
    private boolean[] boolValues;
    private int intCount, floatCount, charCount, boolCount;
    private int intLength, floatLength, charLength, boolLength;

    private enum Type
    {
        INT(1), FLOAT(2), CHAR(3), BOOLEAN(4);
        int typeCode;

        private Type(int typeCode)
        {
            this.typeCode = typeCode;
        }

        public int getTypeCode()
        {
            return typeCode;
        }
    }

    public VarManager()
    {
        varMap = new HashMap<>();
        intNames = floatNames = charNames = boolNames = new String[INITIAL_CAPACITY];
        intValues=new long[INITIAL_CAPACITY];
        floatValues = new double[INITIAL_CAPACITY];
        charValues = new char[INITIAL_CAPACITY];
        boolValues = new boolean[INITIAL_CAPACITY];
        intCount = 0;
        floatCount = 0;
        charCount = 0;
        boolCount = 0;

        intLength = 0;
        floatLength = 0;
        charLength = 0;
        boolLength = 0;
    }

    public void addEmpty(String type, String name)
    {
        switch(type)
        {
            case "int" -> addIntVar(name, 0);
            case "float" -> addFloatVar(name, 0.0f);
            case "char" -> addCharVar(name, '\0');
            case "boolean" -> addBoolVar(name, false);
            default -> VMError.logvm("Invalid type: " + type);
        }
    }

    public <T> void addVar(String name, T value)
    {
        if(value instanceof Integer)
        {
            addIntVar(name, (int)value);
        }
        else if(value instanceof Float)
        {
            addFloatVar(name, (float)value);
        }
        else if(value instanceof Character)
        {
            addCharVar(name, (char)value);
        }
        else if(value instanceof Boolean)
        {
            addBoolVar(name, (boolean)value);
        }
        else
        {
            VMError.logvm("Invalid type for variable " + name);
        }
    }

    public <T> void setVar(String name, T value)
    {
        if(varMap.containsKey(name))
        {
            if(varMap.get(name) == Type.INT)
            {
                intValues[getIndex(name, Type.INT)] = (long)value;
            }
            else if (varMap.get(name) == Type.FLOAT)
            {
                floatValues[getIndex(name, Type.FLOAT)] = (double)value;
            }
            else if (varMap.get(name) == Type.CHAR)
            {
                charValues[getIndex(name, Type.CHAR)] = (char)value;
            }
            else if (varMap.get(name) == Type.BOOLEAN)
            {
                boolValues[getIndex(name, Type.BOOLEAN)] = (boolean)value;
            }
            else
            {
                VMError.logvm("Invalid type for variable " + name);
            }
        }
        else
        {
            VMError.logvm("Variable " + name + " not found");
        }
    }

    public long getIntVar(String name)
    {
        if (varMap.containsKey(name) && varMap.get(name) == Type.INT)
        {
            return intValues[getIndex(name, Type.INT)];
        }
        VMError.logvm("Variable " + name + " not found or invalid type");
        return 0; // Default return value
    }

    public double getFloatVar(String name)
    {
        if (varMap.containsKey(name) && varMap.get(name) == Type.FLOAT)
        {
            return floatValues[getIndex(name, Type.FLOAT)];
        }
        VMError.logvm("Variable " + name + " not found or invalid type");
        return 0.0; // Default return value
    }

    public char getCharVar(String name)
    {
        if (varMap.containsKey(name) && varMap.get(name) == Type.CHAR)
        {
            return charValues[getIndex(name, Type.CHAR)];
        }
        VMError.logvm("Variable " + name + " not found or invalid type");
        return '\0'; // Default return value
    }

    public boolean getBoolVar(String name)
    {
        if (varMap.containsKey(name))// && varMap.get(name) == Type.BOOLEAN)
        {
            return boolValues[getIndex(name, Type.BOOLEAN)];
        }
        VMError.logvm("Variable " + name + " not found or invalid type");
        return false; // Default return value
    }

    public int getTypeCode(String name)
    {
        if(varMap.containsKey(name))
        {
            return varMap.get(name).getTypeCode();
        }
        return -1;
    }

    public boolean contains(String name)
    {
        return varMap.containsKey(name);
    }

    public void clear()
    {
        varMap = new HashMap<>();
        intNames = floatNames = charNames = boolNames = new String[INITIAL_CAPACITY];
        intValues=new long[INITIAL_CAPACITY];
        floatValues = new double[INITIAL_CAPACITY];
        charValues = new char[INITIAL_CAPACITY];
        boolValues = new boolean[INITIAL_CAPACITY];
        intCount = 0;
        floatCount = 0;
        charCount = 0;
        boolCount = 0;

        intLength = 0;
        floatLength = 0;
        charLength = 0;
        boolLength = 0;
    }

    private int getIndex(String name, Type type)
    {
        switch(type)
        {
            case INT:
                for(int i = 0; i < intCount; i++)
                {
                    if(intNames[i].equals(name))
                    {
                        return i;
                    }
                }
                break;
            case FLOAT:
                for(int i = 0; i < floatCount; i++)
                {
                    if(floatNames[i].equals(name))
                    {
                        return i;
                    }
                }
                break;
            case CHAR:
                for(int i = 0; i < charCount; i++)
                {
                    if(charNames[i].equals(name))
                    {
                        return i;
                    }
                }
                break;
            case BOOLEAN:
                for(int i = 0; i < boolCount; i++)
                {
                    if(boolNames[i].equals(name))
                    {
                        return i;
                    }
                }
                break;
        }

        return -1;
    }

    private void addIntVar(String name, int value)
    {
        intLength++;
        varMap.put(name, Type.INT);

        if(intLength < intValues.length)
        {
            intNames[intCount] = name;
            intValues[intCount] = value;
        }
        else
        {
            expandIntArr();
            intNames[intCount] = name;
            intValues[intCount] = value;
        }

        intCount++;
    }

    private void addFloatVar(String name, float value)
    {
        floatLength++;
        varMap.put(name, Type.FLOAT);

        if(floatLength < floatValues.length)
        {
            floatNames[floatCount] = name;
            floatValues[floatCount] = value;
        }
        else
        {
            expandFloatArr();
            floatNames[floatCount] = name;
            floatValues[floatCount] = value;
        }

        floatCount++;
    }

    private void addCharVar(String name, char value)
    {
        charLength++;
        varMap.put(name, Type.CHAR);

        if(charLength < charValues.length)
        {
            charNames[charCount] = name;
            charValues[charCount] = value;
        }
        else
        {
            expandCharArr();
            charNames[charCount] = name;
            charValues[charCount] = value;
        }

        charCount++;
    }

    private void addBoolVar(String name, boolean value)
    {
        boolLength++;
        varMap.put(name, Type.BOOLEAN);

        if(boolLength < boolValues.length)
        {
            boolNames[boolCount] = name;
            boolValues[boolCount] = value;
        }
        else
        {
            expandBoolArr();
            boolNames[boolCount] = name;
            boolValues[boolCount] = value;
        }

        boolCount++;
    }

    private void expandIntArr()
    {
        long temp[] = intValues;
        String tempStr[] = intNames;

        intValues = new long[temp.length+2];
        intNames = new String[tempStr.length+2];

        System.arraycopy(temp, 0, intValues, 0, temp.length);
        System.arraycopy(tempStr, 0, intNames, 0, tempStr.length);
    }

    private void expandFloatArr()
    {
        double temp[] = floatValues;
        String tempStr[] = floatNames;

        floatValues = new double[temp.length + 2];
        floatNames = new String[tempStr.length + 2];

        System.arraycopy(temp, 0, floatValues, 0, temp.length);
        System.arraycopy(tempStr, 0, floatNames, 0, tempStr.length);
    }

    private void expandCharArr()
    {
        char temp[] = charValues;
        String tempStr[] = charNames;

        charValues = new char[temp.length + 2];
        charNames = new String[tempStr.length + 2];

        System.arraycopy(temp, 0, charValues, 0, temp.length);
        System.arraycopy(tempStr, 0, charNames, 0, tempStr.length);
    }

    private void expandBoolArr()
    {
        boolean temp[] = boolValues;
        String tempStr[] = boolNames;

        boolValues = new boolean[temp.length + 2];
        boolNames = new String[tempStr.length + 2];

        System.arraycopy(temp, 0, boolValues, 0, temp.length);
        System.arraycopy(tempStr, 0, boolNames, 0, tempStr.length);
    }

}