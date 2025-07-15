package com.jvs.archvm;

import java.util.*;

/**
 * VarManager manages variables of types int, float, char, and boolean.
 * It stores variable names and values separately by type and provides
 * efficient add, set, get, and clear operations.
 */
public class VarManager {

    private static final int INITIAL_CAPACITY = 4;

    private Map<String, Type> variableMap;

    private List<String> intVarNames;
    private List<String> floatVarNames;
    private List<String> charVarNames;
    private List<String> boolVarNames;

    private long[] intValues;
    private double[] floatValues;
    private char[] charValues;
    private boolean[] boolValues;

    private int intCount;
    private int floatCount;
    private int charCount;
    private int boolCount;

    private enum Type {
        INT, FLOAT, CHAR, BOOLEAN
    }

    /**
     * Creates a new VarManager with initial capacity.
     */
    public VarManager() {
        variableMap = new HashMap<>();

        intVarNames = new ArrayList<>();
        floatVarNames = new ArrayList<>();
        charVarNames = new ArrayList<>();
        boolVarNames = new ArrayList<>();

        intValues = new long[INITIAL_CAPACITY];
        floatValues = new double[INITIAL_CAPACITY];
        charValues = new char[INITIAL_CAPACITY];
        boolValues = new boolean[INITIAL_CAPACITY];

        intCount = 0;
        floatCount = 0;
        charCount = 0;
        boolCount = 0;
    }

    /**
     * Adds a variable of a given type with default value.
     *
     * @param type Variable type ("int", "float", "char", "boolean").
     * @param name Variable name.
     */
    public void addEmpty(String type, String name) {
        switch (type) {
            case "int" -> addIntVar(name, 0L);
            case "float" -> addFloatVar(name, 0.0);
            case "char" -> addCharVar(name, '\0');
            case "boolean" -> addBoolVar(name, false);
            default -> VMError.logvm("Invalid type: " + type);
        }
    }

    /**
     * Adds a variable with a given value. Supports Integer, Float, Character,
     * Boolean.
     *
     * @param name  Variable name.
     * @param value Variable value.
     * @param <T>   Variable type.
     */
    public <T> void addVar(String name, T value) {
        if (value instanceof Integer) {
            addIntVar(name, ((Integer) value).longValue());
        } else if (value instanceof Float) {
            addFloatVar(name, ((Float) value).doubleValue());
        } else if (value instanceof Character) {
            addCharVar(name, (Character) value);
        } else if (value instanceof Boolean) {
            addBoolVar(name, (Boolean) value);
        } else {
            VMError.logvm("Invalid type for variable " + name);
        }
    }

    /**
     * Sets the value of an existing variable.
     *
     * @param name  Variable name.
     * @param value New value.
     * @param <T>   Value type.
     */
    public <T> void setVar(String name, T value) {
        if (!variableMap.containsKey(name)) {
            VMError.logvm("Variable " + name + " not found");
            return;
        }
        Type type = variableMap.get(name);
        int index = getIndex(name, type);
        if (index == -1) {
            VMError.logvm("Variable " + name + " not found in storage");
            return;
        }

        switch (type) {
            case INT -> intValues[index] = ((Number) value).longValue();
            case FLOAT -> floatValues[index] = ((Number) value).doubleValue();
            case CHAR -> charValues[index] = (Character) value;
            case BOOLEAN -> boolValues[index] = (Boolean) value;
        }
    }

    /**
     * Returns the type code for a given variable name.
     * Type codes:
     * 1 = int
     * 2 = float
     * 3 = char
     * 4 = boolean
     * 0 = variable not found
     *
     * @param name Variable name
     * @return int representing the variable type code
     */
    public int getTypeCode(String name) {
        Type type = variableMap.get(name);
        if (type == null) {
            return 0;
        }

        return switch (type) {
            case INT -> 1;
            case FLOAT -> 2;
            case CHAR -> 3;
            case BOOLEAN -> 4;
        };
    }

    /**
     * Retrieves the int value of a variable.
     *
     * @param name Variable name.
     * @return Variable value or 0 if not found or wrong type.
     */
    public long getIntVar(String name) {
        if (variableMap.containsKey(name) && variableMap.get(name) == Type.INT) {
            return intValues[getIndex(name, Type.INT)];
        }
        VMError.logvm("Variable " + name + " not found or invalid type");
        return 0L;
    }

    /**
     * Retrieves the float value of a variable.
     *
     * @param name Variable name.
     * @return Variable value or 0.0 if not found or wrong type.
     */
    public double getFloatVar(String name) {
        if (variableMap.containsKey(name) && variableMap.get(name) == Type.FLOAT) {
            return floatValues[getIndex(name, Type.FLOAT)];
        }
        VMError.logvm("Variable " + name + " not found or invalid type");
        return 0.0;
    }

    /**
     * Retrieves the char value of a variable.
     *
     * @param name Variable name.
     * @return Variable value or '\0' if not found or wrong type.
     */
    public char getCharVar(String name) {
        if (variableMap.containsKey(name) && variableMap.get(name) == Type.CHAR) {
            return charValues[getIndex(name, Type.CHAR)];
        }
        VMError.logvm("Variable " + name + " not found or invalid type");
        return '\0';
    }

    /**
     * Retrieves the boolean value of a variable.
     *
     * @param name Variable name.
     * @return Variable value or false if not found or wrong type.
     */
    public boolean getBoolVar(String name) {
        if (variableMap.containsKey(name) && variableMap.get(name) == Type.BOOLEAN) {
            return boolValues[getIndex(name, Type.BOOLEAN)];
        }
        VMError.logvm("Variable " + name + " not found or invalid type");
        return false;
    }

    /**
     * Checks if a variable exists.
     *
     * @param name Variable name.
     * @return true if variable exists, false otherwise.
     */
    public boolean contains(String name) {
        return variableMap.containsKey(name);
    }

    /**
     * Clears all variables and resets VarManager to initial empty state.
     */
    public void clear() {
        variableMap.clear();
        intVarNames.clear();
        floatVarNames.clear();
        charVarNames.clear();
        boolVarNames.clear();

        intValues = new long[INITIAL_CAPACITY];
        floatValues = new double[INITIAL_CAPACITY];
        charValues = new char[INITIAL_CAPACITY];
        boolValues = new boolean[INITIAL_CAPACITY];

        intCount = 0;
        floatCount = 0;
        charCount = 0;
        boolCount = 0;
    }

    // PRIVATE METHODS

    private int getIndex(String name, Type type) {
        List<String> namesList = switch (type) {
            case INT -> intVarNames;
            case FLOAT -> floatVarNames;
            case CHAR -> charVarNames;
            case BOOLEAN -> boolVarNames;
        };

        for (int i = 0; i < namesList.size(); i++) {
            if (namesList.get(i).equals(name)) {
                return i;
            }
        }
        return -1;
    }

    private void addIntVar(String name, long value) {
        if (intCount == intValues.length) {
            expandIntArray();
        }
        intVarNames.add(name);
        intValues[intCount] = value;
        intCount++;
        variableMap.put(name, Type.INT);
    }

    private void addFloatVar(String name, double value) {
        if (floatCount == floatValues.length) {
            expandFloatArray();
        }
        floatVarNames.add(name);
        floatValues[floatCount] = value;
        floatCount++;
        variableMap.put(name, Type.FLOAT);
    }

    private void addCharVar(String name, char value) {
        if (charCount == charValues.length) {
            expandCharArray();
        }
        charVarNames.add(name);
        charValues[charCount] = value;
        charCount++;
        variableMap.put(name, Type.CHAR);
    }

    private void addBoolVar(String name, boolean value) {
        if (boolCount == boolValues.length) {
            expandBoolArray();
        }
        boolVarNames.add(name);
        boolValues[boolCount] = value;
        boolCount++;
        variableMap.put(name, Type.BOOLEAN);
    }

    private void expandIntArray() {
        int newSize = intValues.length + 4;
        intValues = Arrays.copyOf(intValues, newSize);
    }

    private void expandFloatArray() {
        int newSize = floatValues.length + 4;
        floatValues = Arrays.copyOf(floatValues, newSize);
    }

    private void expandCharArray() {
        int newSize = charValues.length + 4;
        charValues = Arrays.copyOf(charValues, newSize);
    }

    private void expandBoolArray() {
        int newSize = boolValues.length + 4;
        boolValues = Arrays.copyOf(boolValues, newSize);
    }
}
