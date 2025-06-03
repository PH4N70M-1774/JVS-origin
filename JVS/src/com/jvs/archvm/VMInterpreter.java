package com.jvs.archvm;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.lang.reflect.Method;

/**
 * Interprets the instructions of the JVS Virtual Machine (ArchVM).<p>
 *
 *
 * =============================[NOTE]=============================<p>
 *  Please read the documentation of the methods defined by this <p>
 *  by this class as the names given to the methods are such that<p>
 *  they match the instructions of the JVS Bytecode (IS-SC23).   <p>
 *  This has lead to many weird names which are not easy to      <p>
 *  understand(Which is usually a bad practice by the programmer <p>
 *  as he is expected to give clear names to his methods for more<p>
 *  convenience.)                                                <p>
 *  Inconvenience caused is regretted.
 * ================================================================<p>
 */
public class VMInterpreter
{
    private VarManager main, temp;

    /**
     * Constructs a VMInterpreter object.
     *
     * @param main VarManager object to store main variables of a class.<p>
     * @param temp VarManager object to store temporary variables of a class (The temporary variables are not present in the source code, but in the bytecode).<p>
     */
    public VMInterpreter(VarManager main, VarManager temp)
    {
        this.main=main;
        this.temp=temp;
    }

    /**
     * Interprets the 'clrmainl' function in the ArchVM Bytecode.<p>
     * The 'clrmainl' function clears the main variable list (main VarManager object).<p>
     */
    public void clrmainl()
    {
        main.clear();
    }

    /**
     * Interprets the 'clrtlist' function in the ArchVM Bytecode.<p>
     * The 'clrtlist' function clears the temporary variable list (temp VarManager object).<p>
     */
    public void clrtlist()
    {
        temp.clear();
    }


    /**
     * Interprets the 'put' function in the ArchVM Bytecode.<p>
     * The 'put' function adds a variable into the main VarManager object.<p>
     *
     * @param args Contains the arguments of the instruction.
     */
    public void put(String args)
    {
        StringTokenizer st = new StringTokenizer(args, ", ");
        int count=st.countTokens();
        String type=st.nextToken();
        String name=st.nextToken();
        String value=((count==3)?st.nextToken():"");
        if(count==2)
        {
            main.addEmpty(type, name);
            return;
        }

        switch(type)
        {
            case "int" -> main.addVar(name, Integer.parseInt(value));
            case "float" -> main.addVar(name, Float.parseFloat(value));
            case "char" -> main.addVar(name, value.charAt(0));
            case "boolean" -> main.addVar(name, Boolean.parseBoolean(value));
            default -> VMError.logvm("Invalid type: "+type);
        }
    }

    /**
     * Interprets the 'set' function in the ArchVM Bytecode.<p>
     * The 'set' function sets a new value for an existing main variable.<p>
     *
     * @param args Contains the arguments of the instruction.
     */
    public void set(String args)
    {
        StringTokenizer st = new StringTokenizer(args, ", ");
        String name=st.nextToken();
        String value=st.nextToken();

        if(main.contains(name))
        {
            switch(main.getTypeCode(name))
            {
                case 2 -> main.setVar(name, Float.parseFloat(value));
                case 1 -> main.setVar(name, Integer.parseInt(value));
                case 3 -> main.setVar(name, value.charAt(0));
                case 4 -> main.setVar(name, Boolean.parseBoolean(value));
                default -> VMError.logvm("Invalid type: "+value);
            }
        }
        else
        {
            VMError.logvm("Variable not found: "+name);
        }
    }

    /**
     * Interprets the 'cast' function in the ArchVM Bytecode.<p>
     * The 'cast' function casts a variable of one type to another type.<p>
     * A 'cast' instruction has the following format: cast type1, type2, name, target<p>
     *
     * @param args Contains the arguments of the instruction.
     */
    public void cast(String args)
    {
        StringTokenizer st = new StringTokenizer(args, ", ");
        String type1=st.nextToken();
        String type2=st.nextToken();
        String name=st.nextToken();
        String target=st.nextToken();

        if(isCompatible(type1, type2))
        {
            if(main.contains(name))
            {
                if(main.contains(target))
                {
                    switch(type2)
                    {
                        case "int" -> main.setVar(target, main.getIntVar(name));
                        case "float" -> main.setVar(target, main.getFloatVar(name));
                        case "char" -> main.setVar(target, main.getCharVar(name));
                        case "boolean" -> main.setVar(target, main.getBoolVar(name));
                    }
                }
                else if(temp.contains(target))
                {
                    main.addEmpty(type2, target);
                    switch(type2)
                    {
                        case "int" -> main.setVar(target, temp.getIntVar(name));
                        case "float" -> main.setVar(target, temp.getFloatVar(name));
                        case "char" -> main.setVar(target, temp.getCharVar(name));
                        case "boolean" -> main.setVar(target, temp.getBoolVar(name));
                    }
                }
            }
            else if(temp.contains(name))
            {
                if(main.contains(target))
                {
                    switch(type2)
                    {
                        case "int" -> main.setVar(target, main.getIntVar(name));
                        case "float" -> main.setVar(target, main.getFloatVar(name));
                        case "char" -> main.setVar(target, main.getCharVar(name));
                        case "boolean" -> main.setVar(target, main.getBoolVar(name));
                    }
                }
                else if(temp.contains(target))
                {
                    main.addEmpty(type2, target);
                    switch(type2)
                    {
                        case "int" -> main.setVar(target, temp.getIntVar(name));
                        case "float" -> main.setVar(target, temp.getFloatVar(name));
                        case "char" -> main.setVar(target, temp.getCharVar(name));
                        case "boolean" -> main.setVar(target, temp.getBoolVar(name));
                    }
                }
            }
        }

    }

    /**
     * The following two methods interpret the instructions related to addition.
     */

    /**
     * Interprets the 'iadd' function in the ArchVM Bytecode.<p>
     * The 'iadd' function adds two integers and stores the value in another variable.<p>
     * An 'iadd' instruction has the following format: iadd v1, v2, target<p>
     *
     * @param args Contains the arguments of the instruction.
     */
    public void iadd(String args)
    {
        StringTokenizer st = new StringTokenizer(args, ", ");
        String name1=st.nextToken();
        String name2=st.nextToken();
        String name3=st.nextToken();
        long result=0;

        if(isInt(name1))
        {
            result+=Integer.parseInt(name1);
        }
        else if(name1.startsWith("$"))
        {
            if(temp.contains(name1) && temp.getTypeCode(name1)==1)
            {
                result+=temp.getIntVar(name1);
            }
            else
            {
                VMError.logvm("Variable not found: "+name1);
            }
        }
        else
        {
            if(main.contains(name1) && main.getTypeCode(name1)==1)
            {
                result+=main.getIntVar(name1);
            }
            else
            {
                VMError.logvm("Variable not found: "+name1);
            }
        }

        if(isInt(name2))
        {
            result+=Integer.parseInt(name2);
        }
        else if(name2.startsWith("$"))
        {
            if(temp.contains(name2) && temp.getTypeCode(name2)==1)
            {
                result+=temp.getIntVar(name2);
            }
            else
            {
                VMError.logvm("Variable not found: "+name2);
            }
        }
        else
        {
            if(main.contains(name2) && main.getTypeCode(name2)==1)
            {
                result+=main.getIntVar(name2);
            }
            else
            {
                VMError.logvm("Variable not found: "+name2);
            }
        }

        if(name3.startsWith("$"))
        {
            if(temp.contains(name3))
            {
                temp.addVar(name3, result);
            }
            else
            {
                temp.addEmpty("int", name3);
                temp.setVar(name3, result);
            }
        }
        else
        {
            if(main.contains(name3))
            {
                main.setVar(name3, result);
            }
            else
            {
                VMError.logvm("Variable not found: "+name1);
            }
        }
    }

    /**
     *Interprets the 'fadd' function in the ArchVM Bytecode.<p>
     * The 'fadd' function adds two floats and stores the value in another variable.<p>
     * A 'fadd' instruction has the following format: fadd v1, v2, target<p>
     *
     * @param args Contains the arguments of the instruction.
     */
    public void fadd(String args)
    {
        StringTokenizer st = new StringTokenizer(args, ", ");
        String name1=st.nextToken();
        String name2=st.nextToken();
        String name3=st.nextToken();
        double result=0.0;

        if(isFloat(name1))
        {
            result+=Double.parseDouble(name1);
        }
        else if(name1.startsWith("$"))
        {
            if(temp.contains(name1) && temp.getTypeCode(name1)==2)
            {
                result+=temp.getFloatVar(name1);
            }
            else
            {
                VMError.logvm("Variable not found: "+name1);
            }
        }
        else
        {
            if(main.contains(name1) && main.getTypeCode(name1)==2)
            {
                result+=main.getFloatVar(name1);
            }
            else
            {
                VMError.logvm("Variable not found: "+name1);
            }
        }

        if(isFloat(name2))
        {
            result+=Double.parseDouble(name2);
        }
        else if(name2.startsWith("$"))
        {
            if(temp.contains(name2) && temp.getTypeCode(name2)==2)
            {
                result+=temp.getFloatVar(name2);
            }
            else
            {
                VMError.logvm("Variable not found: "+name2);
            }
        }
        else
        {
            if(main.contains(name2) && main.getTypeCode(name2)==2)
            {
                result+=main.getFloatVar(name2);
            }
            else
            {
                VMError.logvm("Variable not found: "+name2);
            }
        }

        if(name3.startsWith("$"))
        {
            if(temp.contains(name3))
            {
                temp.addVar(name3, result);
            }
            else
            {
                temp.addEmpty("float", name3);
                temp.setVar(name3, result);
            }
        }
        else
        {
            if(main.contains(name3))
            {
                main.setVar(name3, result);
            }
            else
            {
                VMError.logvm("Variable not found: "+name1);
            }
        }
    }

    /**
     * The following two methods interpret the instructions related to subtraction.
     */

    /**
     * Interprets the 'isub' function in the ArchVM Bytecode.<p>
     * The 'isub' function subtracts two integers and stores the value in another variable.<p>
     * An 'isub' instruction has the following format: isub v1, v2, target<p>
     *
     * @param args Contains the arguments of the instruction.
     */
    public void isub(String args)
    {
        StringTokenizer st = new StringTokenizer(args, ", ");
        String name1=st.nextToken();
        String name2=st.nextToken();
        String name3=st.nextToken();
        long result=0;

        if(isInt(name1))
        {
            result+=Integer.parseInt(name1);
        }
        else if(name1.startsWith("$"))
        {
            if(temp.contains(name1) && temp.getTypeCode(name1)==1)
            {
                result+=temp.getIntVar(name1);
            }
            else
            {
                VMError.logvm("Variable not found: "+name1);
            }
        }
        else
        {
            if(main.contains(name1) && main.getTypeCode(name1)==1)
            {
                result+=main.getIntVar(name1);
            }
            else
            {
                VMError.logvm("Variable not found: "+name1);
            }
        }

        if(isInt(name2))
        {
            result-=Integer.parseInt(name2);
        }
        else if(name2.startsWith("$"))
        {
            if(temp.contains(name2) && temp.getTypeCode(name2)==1)
            {
                result-=temp.getIntVar(name2);
            }
            else
            {
                VMError.logvm("Variable not found: "+name2);
            }
        }
        else
        {
            if(main.contains(name2) && main.getTypeCode(name2)==1)
            {
                result-=main.getIntVar(name2);
            }
            else
            {
                VMError.logvm("Variable not found: "+name2);
            }
        }

        if(name3.startsWith("$"))
        {
            if(temp.contains(name3))
            {
                temp.addVar(name3, result);
            }
            else
            {
                temp.addEmpty("int", name3);
                temp.setVar(name3, result);
            }
        }
        else
        {
            if(main.contains(name3))
            {
                main.setVar(name3, result);
            }
            else
            {
                VMError.logvm("Variable not found: "+name1);
            }
        }
    }

    /**
     *Interprets the 'fsub' function in the ArchVM Bytecode.<p>
     * The 'fsub' function subtracts two floats and stores the value in another variable.<p>
     * A 'fsub' instruction has the following format: fsub v1, v2, target<p>
     *
     * @param args Contains the arguments of the instruction.
     */
    public void fsub(String args)
    {
        StringTokenizer st = new StringTokenizer(args, ", ");
        String name1=st.nextToken();
        String name2=st.nextToken();
        String name3=st.nextToken();
        double result=0.0;

        if(isFloat(name1))
        {
            result+=Double.parseDouble(name1);
        }
        else if(name1.startsWith("$"))
        {
            if(temp.contains(name1) && temp.getTypeCode(name1)==2)
            {
                result+=temp.getFloatVar(name1);
            }
            else
            {
                VMError.logvm("Variable not found: "+name1);
            }
        }
        else
        {
            if(main.contains(name1) && main.getTypeCode(name1)==2)
            {
                result+=main.getFloatVar(name1);
            }
            else
            {
                VMError.logvm("Variable not found: "+name1);
            }
        }

        if(isFloat(name2))
        {
            result-=Double.parseDouble(name2);
        }
        else if(name2.startsWith("$"))
        {
            if(temp.contains(name2) && temp.getTypeCode(name2)==2)
            {
                result-=temp.getFloatVar(name2);
            }
            else
            {
                VMError.logvm("Variable not found: "+name2);
            }
        }
        else
        {
            if(main.contains(name2) && main.getTypeCode(name2)==2)
            {
                result-=main.getFloatVar(name2);
            }
            else
            {
                VMError.logvm("Variable not found: "+name2);
            }
        }

        if(name3.startsWith("$"))
        {
            if(temp.contains(name3))
            {
                temp.addVar(name3, result);
            }
            else
            {
                temp.addEmpty("float", name3);
                temp.setVar(name3, result);
            }
        }
        else
        {
            if(main.contains(name3))
            {
                main.setVar(name3, result);
            }
            else
            {
                VMError.logvm("Variable not found: "+name1);
            }
        }
    }

    /**
     * The following two methods interpret the instructions related to multiplication.
     */

    /**
     * Interprets the 'imult' function in the ArchVM Bytecode.<p>
     * The 'imult' function multiplies two integers and stores the value in another variable.<p>
     * An 'imult' instruction has the following format: imult v1, v2, target<p>
     *
     * @param args Contains the arguments of the instruction.
     */
    public void imult(String args)
    {
        StringTokenizer st = new StringTokenizer(args, ", ");
        String name1=st.nextToken();
        String name2=st.nextToken();
        String name3=st.nextToken();
        long result=0;

        if(isInt(name1))
        {
            result+=Integer.parseInt(name1);
        }
        else if(name1.startsWith("$"))
        {
            if(temp.contains(name1) && temp.getTypeCode(name1)==1)
            {
                result+=temp.getIntVar(name1);
            }
            else
            {
                VMError.logvm("Variable not found: "+name1);
            }
        }
        else
        {
            if(main.contains(name1) && main.getTypeCode(name1)==1)
            {
                result+=main.getIntVar(name1);
            }
            else
            {
                VMError.logvm("Variable not found: "+name1);
            }
        }

        if(isInt(name2))
        {
            result*=Integer.parseInt(name2);
        }
        else if(name2.startsWith("$"))
        {
            if(temp.contains(name2) && temp.getTypeCode(name2)==1)
            {
                result*=temp.getIntVar(name2);
            }
            else
            {
                VMError.logvm("Variable not found: "+name2);
            }
        }
        else
        {
            if(main.contains(name2) && main.getTypeCode(name2)==1)
            {
                result*=main.getIntVar(name2);
            }
            else
            {
                VMError.logvm("Variable not found: "+name2);
            }
        }

        if(name3.startsWith("$"))
        {
            if(temp.contains(name3))
            {
                temp.addVar(name3, result);
            }
            else
            {
                temp.addEmpty("int", name3);
                temp.setVar(name3, result);
            }
        }
        else
        {
            if(main.contains(name3))
            {
                main.setVar(name3, result);
            }
            else
            {
                VMError.logvm("Variable not found: "+name1);
            }
        }
    }

    /**
     *Interprets the 'fmult' function in the ArchVM Bytecode.<p>
     * The 'fmult' function multiplies two floats and stores the value in another variable.<p>
     * A 'fmult' instruction has the following format: fmult v1, v2, target<p><p>
     *
     * @param args Contains the arguments of the instruction.
     */
    public void fmult(String args)
    {
        StringTokenizer st = new StringTokenizer(args, ", ");
        String name1=st.nextToken();
        String name2=st.nextToken();
        String name3=st.nextToken();
        double result=0.0;

        if(isFloat(name1))
        {
            result+=Double.parseDouble(name1);
        }
        else if(name1.startsWith("$"))
        {
            if(temp.contains(name1) && temp.getTypeCode(name1)==2)
            {
                result+=temp.getFloatVar(name1);
            }
            else
            {
                VMError.logvm("Variable not found: "+name1);
            }
        }
        else
        {
            if(main.contains(name1) && main.getTypeCode(name1)==2)
            {
                result+=main.getFloatVar(name1);
            }
            else
            {
                VMError.logvm("Variable not found: "+name1);
            }
        }

        if(isFloat(name2))
        {
            result*=Double.parseDouble(name2);
        }
        else if(name2.startsWith("$"))
        {
            if(temp.contains(name2) && temp.getTypeCode(name2)==2)
            {
                result*=temp.getFloatVar(name2);
            }
            else
            {
                VMError.logvm("Variable not found: "+name2);
            }
        }
        else
        {
            if(main.contains(name2) && main.getTypeCode(name2)==2)
            {
                result*=main.getFloatVar(name2);
            }
            else
            {
                VMError.logvm("Variable not found: "+name2);
            }
        }

        if(name3.startsWith("$"))
        {
            if(temp.contains(name3))
            {
                temp.addVar(name3, result);
            }
            else
            {
                temp.addEmpty("float", name3);
                temp.setVar(name3, result);
            }
        }
        else
        {
            if(main.contains(name3))
            {
                main.setVar(name3, result);
            }
            else
            {
                VMError.logvm("Variable not found: "+name1);
            }
        }
    }

    /**
     * The following two methods interpret the instructions related to division.
     */

    /**
     * Interprets the 'idiv' function in the ArchVM Bytecode.<p>
     * The 'idiv' function divides two integers and stores the value in another variable.<p>
     * An 'idiv' instruction has the following format: idiv v1, v2, target<p>
     *
     * @param args Contains the arguments of the instruction.
     */
    public void idiv(String args)
    {
        StringTokenizer st = new StringTokenizer(args, ", ");
        String name1=st.nextToken();
        String name2=st.nextToken();
        String name3=st.nextToken();
        long result=0;

        if(isInt(name1))
        {
            result+=Integer.parseInt(name1);
        }
        else if(name1.startsWith("$"))
        {
            if(temp.contains(name1) && temp.getTypeCode(name1)==1)
            {
                result+=temp.getIntVar(name1);
            }
            else
            {
                VMError.logvm("Variable not found: "+name1);
            }
        }
        else
        {
            if(main.contains(name1) && main.getTypeCode(name1)==1)
            {
                result+=main.getIntVar(name1);
            }
            else
            {
                VMError.logvm("Variable not found: "+name1);
            }
        }

        if(isInt(name2))
        {
            result/=Integer.parseInt(name2);
        }
        else if(name2.startsWith("$"))
        {
            if(temp.contains(name2) && temp.getTypeCode(name2)==1)
            {
                result/=temp.getIntVar(name2);
            }
            else
            {
                VMError.logvm("Variable not found: "+name2);
            }
        }
        else
        {
            if(main.contains(name2) && main.getTypeCode(name2)==1)
            {
                result/=main.getIntVar(name2);
            }
            else
            {
                VMError.logvm("Variable not found: "+name2);
            }
        }

        if(name3.startsWith("$"))
        {
            if(temp.contains(name3))
            {
                temp.addVar(name3, result);
            }
            else
            {
                temp.addEmpty("int", name3);
                temp.setVar(name3, result);
            }
        }
        else
        {
            if(main.contains(name3))
            {
                main.setVar(name3, result);
            }
            else
            {
                VMError.logvm("Variable not found: "+name1);
            }
        }
    }

    /**
     *Interprets the 'fdiv' function in the ArchVM Bytecode.<p>
     * The 'fdiv' function divides two floats and stores the value in another variable.<p>
     * A 'fdiv' instruction has the following format: fdiv v1, v2, target<p>
     *
     * @param args Contains the arguments of the instruction.
     */
    public void fdiv(String args)
    {
        StringTokenizer st = new StringTokenizer(args, ", ");
        String name1=st.nextToken();
        String name2=st.nextToken();
        String name3=st.nextToken();
        double result=0.0;

        if(isFloat(name1))
        {
            result+=Double.parseDouble(name1);
        }
        else if(name1.startsWith("$"))
        {
            if(temp.contains(name1) && temp.getTypeCode(name1)==2)
            {
                result+=temp.getFloatVar(name1);
            }
            else
            {
                VMError.logvm("Variable not found: "+name1);
            }
        }
        else
        {
            if(main.contains(name1) && main.getTypeCode(name1)==2)
            {
                result+=main.getFloatVar(name1);
            }
            else
            {
                VMError.logvm("Variable not found: "+name1);
            }
        }

        if(isFloat(name2))
        {
            result/=Double.parseDouble(name2);
        }
        else if(name2.startsWith("$"))
        {
            if(temp.contains(name2) && temp.getTypeCode(name2)==2)
            {
                result/=temp.getFloatVar(name2);
            }
            else
            {
                VMError.logvm("Variable not found: "+name2);
            }
        }
        else
        {
            if(main.contains(name2) && main.getTypeCode(name2)==2)
            {
                result/=main.getFloatVar(name2);
            }
            else
            {
                VMError.logvm("Variable not found: "+name2);
            }
        }

        if(name3.startsWith("$"))
        {
            if(temp.contains(name3))
            {
                temp.addVar(name3, result);
            }
            else
            {
                temp.addEmpty("float", name3);
                temp.setVar(name3, result);
            }
        }
        else
        {
            if(main.contains(name3))
            {
                main.setVar(name3, result);
            }
            else
            {
                VMError.logvm("Variable not found: "+name1);
            }
        }
    }

    /**
     * The following two methods interpret the instructions related to modulus.
     */

    /**
     * Interprets the 'imod' function in the ArchVM Bytecode.<p>
     * The 'imod' function performs modulus operation on two integers and stores the value in another variable.<p>
     * An 'imod' instruction has the following format: imod v1, v2, target<p>
     *
     * @param args Contains the arguments of the instruction.
     */
    public void imod(String args)
    {
        StringTokenizer st = new StringTokenizer(args, ", ");
        String name1=st.nextToken();
        String name2=st.nextToken();
        String name3=st.nextToken();
        long result=0;

        if(isInt(name1))
        {
            result+=Integer.parseInt(name1);
        }
        else if(name1.startsWith("$"))
        {
            if(temp.contains(name1) && temp.getTypeCode(name1)==1)
            {
                result+=temp.getIntVar(name1);
            }
            else
            {
                VMError.logvm("Variable not found: "+name1);
            }
        }
        else
        {
            if(main.contains(name1) && main.getTypeCode(name1)==1)
            {
                result+=main.getIntVar(name1);
            }
            else
            {
                VMError.logvm("Variable not found: "+name1);
            }
        }

        if(isInt(name2))
        {
            result%=Integer.parseInt(name2);
        }
        else if(name2.startsWith("$"))
        {
            if(temp.contains(name2) && temp.getTypeCode(name2)==1)
            {
                result%=temp.getIntVar(name2);
            }
            else
            {
                VMError.logvm("Variable not found: "+name2);
            }
        }
        else
        {
            if(main.contains(name2) && main.getTypeCode(name2)==1)
            {
                result%=main.getIntVar(name2);
            }
            else
            {
                VMError.logvm("Variable not found: "+name2);
            }
        }

        if(name3.startsWith("$"))
        {
            if(temp.contains(name3))
            {
                temp.addVar(name3, result);
            }
            else
            {
                temp.addEmpty("int", name3);
                temp.setVar(name3, result);
            }
        }
        else
        {
            if(main.contains(name3))
            {
                main.setVar(name3, result);
            }
            else
            {
                VMError.logvm("Variable not found: "+name1);
            }
        }
    }

    /**
     *Interprets the 'fmod' function in the ArchVM Bytecode.<p>
     * The 'fmod' function adds two floats and stores the value in another variable.<p>
     * A 'fmod' instruction has the following format: fmod v1, v2, target<p>
     *
     * @param args Contains the arguments of the instruction.
     */
    public void fmod(String args)
    {
        StringTokenizer st = new StringTokenizer(args, ", ");
        String name1=st.nextToken();
        String name2=st.nextToken();
        String name3=st.nextToken();
        double result=0.0;

        if(isFloat(name1))
        {
            result+=Double.parseDouble(name1);
        }
        else if(name1.startsWith("$"))
        {
            if(temp.contains(name1) && temp.getTypeCode(name1)==2)
            {
                result+=temp.getFloatVar(name1);
            }
            else
            {
                VMError.logvm("Variable not found: "+name1);
            }
        }
        else
        {
            if(main.contains(name1) && main.getTypeCode(name1)==2)
            {
                result+=main.getFloatVar(name1);
            }
            else
            {
                VMError.logvm("Variable not found: "+name1);
            }
        }

        if(isFloat(name2))
        {
            result%=Double.parseDouble(name2);
        }
        else if(name2.startsWith("$"))
        {
            if(temp.contains(name2) && temp.getTypeCode(name2)==2)
            {
                result%=temp.getFloatVar(name2);
            }
            else
            {
                VMError.logvm("Variable not found: "+name2);
            }
        }
        else
        {
            if(main.contains(name2) && main.getTypeCode(name2)==2)
            {
                result%=main.getFloatVar(name2);
            }
            else
            {
                VMError.logvm("Variable not found: "+name2);
            }
        }

        if(name3.startsWith("$"))
        {
            if(temp.contains(name3))
            {
                temp.addVar(name3, result);
            }
            else
            {
                temp.addEmpty("float", name3);
                temp.setVar(name3, result);
            }
        }
        else
        {
            if(main.contains(name3))
            {
                main.setVar(name3, result);
            }
            else
            {
                VMError.logvm("Variable not found: "+name1);
            }
        }
    }

    /**
     * The following two methods interpret the instructions related to power.
     */

    /**
     * Interprets the 'ipow' function in the ArchVM Bytecode.<p>
     * The 'ipow' function raises an integer to the power of another integer and stores the value in another variable.<p>
     * An 'ipow' instruction has the following format: ipow v1, v2, target<p>
     *
     * @param args Contains the arguments of the instruction.
     */
    public void ipow(String args)
    {
        StringTokenizer st = new StringTokenizer(args, ", ");
        String name1=st.nextToken();
        String name2=st.nextToken();
        String name3=st.nextToken();
        long result=0;

        if(isInt(name1))
        {
            result+=Integer.parseInt(name1);
        }
        else if(name1.startsWith("$"))
        {
            if(temp.contains(name1) && temp.getTypeCode(name1)==1)
            {
                result+=temp.getIntVar(name1);
            }
            else
            {
                VMError.logvm("Variable not found: "+name1);
            }
        }
        else
        {
            if(main.contains(name1) && main.getTypeCode(name1)==1)
            {
                result+=main.getIntVar(name1);
            }
            else
            {
                VMError.logvm("Variable not found: "+name1);
            }
        }

        if(isInt(name2))
        {
            result=(long)Math.pow(result, Integer.parseInt(name2));
        }
        else if(name2.startsWith("$"))
        {
            if(temp.contains(name2) && temp.getTypeCode(name2)==1)
            {
                result=(long)Math.pow(result, temp.getIntVar(name2));
            }
            else
            {
                VMError.logvm("Variable not found: "+name2);
            }
        }
        else
        {
            if(main.contains(name2) && main.getTypeCode(name2)==1)
            {
                result=(long)Math.pow(result, main.getIntVar(name2));
            }
            else
            {
                VMError.logvm("Variable not found: "+name2);
            }
        }

        if(name3.startsWith("$"))
        {
            if(temp.contains(name3))
            {
                temp.addVar(name3, result);
            }
            else
            {
                temp.addEmpty("int", name3);
                temp.setVar(name3, result);
            }
        }
        else
        {
            if(main.contains(name3))
            {
                main.setVar(name3, result);
            }
            else
            {
                VMError.logvm("Variable not found: "+name1);
            }
        }
    }

    /**
     * Interprets the 'fpow' function in the ArchVM Bytecode.<p>
     * The 'fpow' function raises a float to the power of another float and stores the value in another variable.<p>
     * An 'fpow' instruction has the following format: fpow v1, v2, target<p>
     *
     * @param args Contains the arguments of the instruction.
     */
    public void fpow(String args)
    {
        StringTokenizer st = new StringTokenizer(args, ", ");
        String name1=st.nextToken();
        String name2=st.nextToken();
        String name3=st.nextToken();
        double result=0.0;

        if(isFloat(name1))
        {
            result+=Double.parseDouble(name1);
        }
        else if(name1.startsWith("$"))
        {
            if(temp.contains(name1) && temp.getTypeCode(name1)==2)
            {
                result+=temp.getFloatVar(name1);
            }
            else
            {
                VMError.logvm("Variable not found: "+name1);
            }
        }
        else
        {
            if(main.contains(name1) && main.getTypeCode(name1)==2)
            {
                result+=main.getFloatVar(name1);
            }
            else
            {
                VMError.logvm("Variable not found: "+name1);
            }
        }

        if(isFloat(name2))
        {
            result=Math.pow(result, Double.parseDouble(name2));
        }
        else if(name2.startsWith("$"))
        {
            if(temp.contains(name2) && temp.getTypeCode(name2)==2)
            {
                result=Math.pow(result, temp.getFloatVar(name2));
            }
            else
            {
                VMError.logvm("Variable not found: "+name2);
            }
        }
        else
        {
            if(main.contains(name2) && main.getTypeCode(name2)==2)
            {
                result=Math.pow(result, main.getFloatVar(name2));
            }
            else
            {
                VMError.logvm("Variable not found: "+name2);
            }
        }

        if(name3.startsWith("$"))
        {
            if(temp.contains(name3))
            {
                temp.addVar(name3, result);
            }
            else
            {
                temp.addEmpty("float", name3);
                temp.setVar(name3, result);
            }
        }
        else
        {
            if(main.contains(name3))
            {
                main.setVar(name3, result);
            }
            else
            {
                VMError.logvm("Variable not found: "+name1);
            }
        }
    }

    /**
     * The following methods work with increment or decrement.
     */

    /**
     * Interprets the 'incr' function in the ArchVM Bytecode.<p>
     * The 'incr' function increments a int/float variable by one.<p>
     * An 'incr' instruction has the following format: incr v<p>
     *
     * @param args Contains the arguments of the instruction.
     */
    public void incr(String args)
    {
        if(main.contains(args))
        {
            if(main.getTypeCode(args)==1)
            {
                main.setVar(args, (main.getIntVar(args)+1));
            }
            else if(main.getTypeCode(args)==2)
            {
                main.setVar(args, (main.getFloatVar(args)+1.0));
            }
            else if(main.getTypeCode(args)==3)
            {
                main.setVar(args, (main.getCharVar(args)+1));
            }
        }
    }

    /**
     * Interprets the 'decr' function in the ArchVM Bytecode.<p>
     * The 'decr' function increments a int/float variable by one.<p>
     * An 'decr' instruction has the following format: decr v<p>
     *
     * @param args Contains the arguments of the instruction.
     */
    public void decr(String args)
    {
        if(main.contains(args))
        {
            if(main.getTypeCode(args)==1)
            {
                main.setVar(args, (main.getIntVar(args)-1));
            }
            else if(main.getTypeCode(args)==2)
            {
                main.setVar(args, (main.getFloatVar(args)-1.0));
            }
            else if(main.getTypeCode(args)==3)
            {
                main.setVar(args, (main.getCharVar(args)-1));
            }
        }
    }

    /**
     * The following methods work with relational operations.
     */

    /**
     * Interprets the 'cmpgt' function in the ArchVM Bytecode.<p>
     * The 'cmpgt' function checks if v1 > v2 and stores a boolean value in another variable.<p>
     * A 'cmpgt' instruction has the following format: cmpgt v1, v2, target<p>
     *
     * @param args Contains the arguments of the instruction.
     */
    public void cmpgt(String args)
    {
        StringTokenizer st = new StringTokenizer(args, ", ");
        double a = 0.0,b=0.0;
        String v1=st.nextToken(),v2=st.nextToken(), target=st.nextToken();

        if(isInt(v1)||isFloat(v1))
        {
            a+=Double.valueOf(v1);
        }
        else if(main.contains(v1))
        {
            a+=switch(main.getTypeCode(v1))
            {
                case 1->main.getIntVar(v1);
                case 2->main.getFloatVar(v1);
                case 3->main.getCharVar(v1);
                default->getDefaultError("Invalid type for comparison (Only int, float and char allowed).");
            };
        }
        else if(temp.contains(v1))
        {
            a+=switch(temp.getTypeCode(v1))
            {
                case 1->temp.getIntVar(v1);
                case 2->temp.getFloatVar(v1);
                case 3->temp.getCharVar(v1);
                default->getDefaultError("Invalid type for comparison (Only int, float and char allowed).");
            };
        }
        else
        {
            VMError.logvm("Invalid value for comparison.");
        }

        if(isInt(v2)||isFloat(v2))
        {
            b+=Double.valueOf(v2);
        }
        else if(main.contains(v2))
        {
            b+=switch(main.getTypeCode(v2))
            {
                case 1->main.getIntVar(v2);
                case 2->main.getFloatVar(v2);
                case 3->main.getCharVar(v2);
                default->getDefaultError("Invalid type for comparison (Only int, float and char allowed).");
            };
        }
        else if(temp.contains(v2))
        {
            b+=switch(temp.getTypeCode(v2))
            {
                case 1->temp.getIntVar(v2);
                case 2->temp.getFloatVar(v2);
                case 3->temp.getCharVar(v2);
                default->getDefaultError("Invalid type for comparison (Only int, float and char allowed).");
            };
        }
        else
        {
            VMError.logvm("Invalid value for comparison.");
        }

        if(main.contains(target))
        {
            main.setVar(target, (a>b));
        }
        else if(target.startsWith("$"))
        {
            if(temp.contains(target))
            {
                temp.setVar(target, (a>b));
            }
            else
            {
                temp.addVar(target, (a>b));
            }
        }
        else
        {
            VMError.logvm("Target variable not found.");
        }
    }

    /**
     * Interprets the 'cmpgte' function in the ArchVM Bytecode.<p>
     * The 'cmpgte' function checks if v1 >= v2 and stores a boolean value in another variable.<p>
     * A 'cmpgte' instruction has the following format: cmpgte v1, v2, target<p>
     *
     * @param args Contains the arguments of the instruction.
     */
    public void cmpgte(String args)
    {
        StringTokenizer st = new StringTokenizer(args, ", ");
        double a = 0.0,b=0.0;
        String v1=st.nextToken(),v2=st.nextToken(), target=st.nextToken();

        if(isInt(v1)||isFloat(v1))
        {
            a+=Double.valueOf(v1);
        }
        else if(main.contains(v1))
        {
            a+=switch(main.getTypeCode(v1))
            {
                case 1->main.getIntVar(v1);
                case 2->main.getFloatVar(v1);
                case 3->main.getCharVar(v1);
                default->getDefaultError("Invalid type for comparison (Only int, float and char allowed).");
            };
        }
        else if(temp.contains(v1))
        {
            a+=switch(temp.getTypeCode(v1))
            {
                case 1->temp.getIntVar(v1);
                case 2->temp.getFloatVar(v1);
                case 3->temp.getCharVar(v1);
                default->getDefaultError("Invalid type for comparison (Only int, float and char allowed).");
            };
        }
        else
        {
            VMError.logvm("Invalid value for comparison.");
        }

        if(isInt(v2)||isFloat(v2))
        {
            b+=Double.valueOf(v2);
        }
        else if(main.contains(v2))
        {
            b+=switch(main.getTypeCode(v2))
            {
                case 1->main.getIntVar(v2);
                case 2->main.getFloatVar(v2);
                case 3->main.getCharVar(v2);
                default->getDefaultError("Invalid type for comparison (Only int, float and char allowed).");
            };
        }
        else if(temp.contains(v2))
        {
            b+=switch(temp.getTypeCode(v2))
            {
                case 1->temp.getIntVar(v2);
                case 2->temp.getFloatVar(v2);
                case 3->temp.getCharVar(v2);
                default->getDefaultError("Invalid type for comparison (Only int, float and char allowed).");
            };
        }
        else
        {
            VMError.logvm("Invalid value for comparison.");
        }

        if(main.contains(target))
        {
            main.setVar(target, (a>=b));
        }
        else if(target.startsWith("$"))
        {
            if(temp.contains(target))
            {
                temp.setVar(target, (a>=b));
            }
            else
            {
                temp.addVar(target, (a>=b));
            }
        }
        else
        {
            VMError.logvm("Target variable not found.");
        }
    }

    /**
     * Interprets the 'cmplt' function in the ArchVM Bytecode.<p>
     * The 'cmplt' function checks if v1 < v2 and stores a boolean value in another variable.<p>
     * A 'cmplt' instruction has the following format: cmplt v1, v2, target<p>
     *
     * @param args Contains the arguments of the instruction.
     */
    public void cmplt(String args)
    {
        StringTokenizer st = new StringTokenizer(args, ", ");
        double a = 0.0,b=0.0;
        String v1=st.nextToken(),v2=st.nextToken(), target=st.nextToken();

        if(isInt(v1)||isFloat(v1))
        {
            a+=Double.valueOf(v1);
        }
        else if(main.contains(v1))
        {
            a+=switch(main.getTypeCode(v1))
            {
                case 1->main.getIntVar(v1);
                case 2->main.getFloatVar(v1);
                case 3->main.getCharVar(v1);
                default->getDefaultError("Invalid type for comparison (Only int, float and char allowed).");
            };
        }
        else if(temp.contains(v1))
        {
            a+=switch(temp.getTypeCode(v1))
            {
                case 1->temp.getIntVar(v1);
                case 2->temp.getFloatVar(v1);
                case 3->temp.getCharVar(v1);
                default->getDefaultError("Invalid type for comparison (Only int, float and char allowed).");
            };
        }
        else
        {
            VMError.logvm("Invalid value for comparison.");
        }

        if(isInt(v2)||isFloat(v2))
        {
            b+=Double.valueOf(v2);
        }
        else if(main.contains(v2))
        {
            b+=switch(main.getTypeCode(v2))
            {
                case 1->main.getIntVar(v2);
                case 2->main.getFloatVar(v2);
                case 3->main.getCharVar(v2);
                default->getDefaultError("Invalid type for comparison (Only int, float and char allowed).");
            };
        }
        else if(temp.contains(v2))
        {
            b+=switch(temp.getTypeCode(v2))
            {
                case 1->temp.getIntVar(v2);
                case 2->temp.getFloatVar(v2);
                case 3->temp.getCharVar(v2);
                default->getDefaultError("Invalid type for comparison (Only int, float and char allowed).");
            };
        }
        else
        {
            VMError.logvm("Invalid value for comparison.");
        }

        if(main.contains(target))
        {
            main.setVar(target, (a<b));
        }
        else if(target.startsWith("$"))
        {
            if(temp.contains(target))
            {
                temp.setVar(target, (a<b));
            }
            else
            {
                temp.addVar(target, (a<b));
            }
        }
        else
        {
            VMError.logvm("Target variable not found.");
        }
    }

    /**
     * Interprets the 'cmplte' function in the ArchVM Bytecode.<p>
     * The 'cmplte' function checks if v1 <= v2 and stores a boolean value in another variable.<p>
     * A 'cmplte' instruction has the following format: cmplte v1, v2, target<p>
     *
     * @param args Contains the arguments of the instruction.
     */
    public void cmplte(String args)
    {
        StringTokenizer st = new StringTokenizer(args, ", ");
        double a = 0.0,b=0.0;
        String v1=st.nextToken(),v2=st.nextToken(), target=st.nextToken();
        if(isInt(v1)||isFloat(v1))
        {
            a+=Double.valueOf(v1);
        }
        else if(main.contains(v1))
        {
            a+=switch(main.getTypeCode(v1))
            {
                case 1->main.getIntVar(v1);
                case 2->main.getFloatVar(v1);
                case 3->main.getCharVar(v1);
                default->getDefaultError("Invalid type for comparison (Only int, float and char allowed).");
            };
        }
        else if(temp.contains(v1))
        {
            a+=switch(temp.getTypeCode(v1))
            {
                case 1->temp.getIntVar(v1);
                case 2->temp.getFloatVar(v1);
                case 3->temp.getCharVar(v1);
                default->getDefaultError("Invalid type for comparison (Only int, float and char allowed).");
            };
        }
        else
        {
            VMError.logvm("Invalid value for comparison.");
        }

        if(isInt(v2)||isFloat(v2))
        {
            b+=Double.valueOf(v2);
        }
        else if(main.contains(v2))
        {
            b+=switch(main.getTypeCode(v2))
            {
                case 1->main.getIntVar(v2);
                case 2->main.getFloatVar(v2);
                case 3->main.getCharVar(v2);
                default->getDefaultError("Invalid type for comparison (Only int, float and char allowed).");
            };
        }
        else if(temp.contains(v2))
        {
            b+=switch(temp.getTypeCode(v2))
            {
                case 1->temp.getIntVar(v2);
                case 2->temp.getFloatVar(v2);
                case 3->temp.getCharVar(v2);
                default->getDefaultError("Invalid type for comparison (Only int, float and char allowed).");
            };
        }
        else
        {
            VMError.logvm("Invalid value for comparison.");
        }

        if(main.contains(target))
        {
            main.setVar(target, (a<=b));
        }
        else if(target.startsWith("$"))
        {
            if(temp.contains(target))
            {
                temp.setVar(target, (a<=b));
            }
            else
            {
                temp.addVar(target, (a<=b));
            }
        }
        else
        {
            VMError.logvm("Target variable not found.");
        }
    }

    /**
     * Interprets the 'cmpe' function in the ArchVM Bytecode.<p>
     * The 'cmpe' function checks if v1 == v2 and stores a boolean value in another variable.<p>
     * A 'cmpe' instruction has the following format: cmpe v1, v2, target<p>
     *
     * @param args Contains the arguments of the instruction.
     */
    public void cmpe(String args)
    {
        StringTokenizer st = new StringTokenizer(args, ", ");
        double a = 0.0,b=0.0;
        String v1=st.nextToken(),v2=st.nextToken(), target=st.nextToken();

        if(isInt(v1)||isFloat(v1))
        {
            a+=Double.valueOf(v1);
        }
        else if(main.contains(v1))
        {
            a+=switch(main.getTypeCode(v1))
            {
                case 1->main.getIntVar(v1);
                case 2->main.getFloatVar(v1);
                case 3->main.getCharVar(v1);
                default->getDefaultError("Invalid type for comparison (Only int, float and char allowed).");
            };
        }
        else if(temp.contains(v1))
        {
            a+=switch(temp.getTypeCode(v1))
            {
                case 1->temp.getIntVar(v1);
                case 2->temp.getFloatVar(v1);
                case 3->temp.getCharVar(v1);
                default->getDefaultError("Invalid type for comparison (Only int, float and char allowed).");
            };
        }
        else
        {
            VMError.logvm("Invalid value for comparison.");
        }

        if(isInt(v2)||isFloat(v2))
        {
            b+=Double.valueOf(v2);
        }
        else if(main.contains(v2))
        {
            b+=switch(main.getTypeCode(v2))
            {
                case 1->main.getIntVar(v2);
                case 2->main.getFloatVar(v2);
                case 3->main.getCharVar(v2);
                default->getDefaultError("Invalid type for comparison (Only int, float and char allowed).");
            };
        }
        else if(temp.contains(v2))
        {
            b+=switch(temp.getTypeCode(v2))
            {
                case 1->temp.getIntVar(v2);
                case 2->temp.getFloatVar(v2);
                case 3->temp.getCharVar(v2);
                default->getDefaultError("Invalid type for comparison (Only int, float and char allowed).");
            };
        }
        else
        {
            VMError.logvm("Invalid value for comparison.");
        }

        if(main.contains(target))
        {
            main.setVar(target, (a==b));
        }
        else if(target.startsWith("$"))
        {
            if(temp.contains(target))
            {
                temp.setVar(target, (a==b));
            }
            else
            {
                temp.addVar(target, (a==b));
            }
        }
        else
        {
            VMError.logvm("Target variable not found.");
        }
    }

    /**
     * Interprets the 'cmpne' function in the ArchVM Bytecode.<p>
     * The 'cmpne' function checks if v1 != v2 and stores a boolean value in another variable.<p>
     * A 'cmpne' instruction has the following format: cmpne v1, v2, target<p>
     *
     * @param args Contains the arguments of the instruction.
     */
    public void cmpne(String args)
    {
        StringTokenizer st = new StringTokenizer(args, ", ");
        double a = 0.0,b=0.0;
        String v1=st.nextToken(),v2=st.nextToken(), target=st.nextToken();

        if(isInt(v1)||isFloat(v1))
        {
            a+=Double.valueOf(v1);
        }
        else if(main.contains(v1))
        {
            a+=switch(main.getTypeCode(v1))
            {
                case 1->main.getIntVar(v1);
                case 2->main.getFloatVar(v1);
                case 3->main.getCharVar(v1);
                default->getDefaultError("Invalid type for comparison (Only int, float and char allowed).");
            };
        }
        else if(temp.contains(v1))
        {
            a+=switch(temp.getTypeCode(v1))
            {
                case 1->temp.getIntVar(v1);
                case 2->temp.getFloatVar(v1);
                case 3->temp.getCharVar(v1);
                default->getDefaultError("Invalid type for comparison (Only int, float and char allowed).");
            };
        }
        else
        {
            VMError.logvm("Invalid value for comparison.");
        }

        if(isInt(v2)||isFloat(v2))
        {
            b+=Double.valueOf(v2);
        }
        else if(main.contains(v2))
        {
            b+=switch(main.getTypeCode(v2))
            {
                case 1->main.getIntVar(v2);
                case 2->main.getFloatVar(v2);
                case 3->main.getCharVar(v2);
                default->getDefaultError("Invalid type for comparison (Only int, float and char allowed).");
            };
        }
        else if(temp.contains(v2))
        {
            b+=switch(temp.getTypeCode(v2))
            {
                case 1->temp.getIntVar(v2);
                case 2->temp.getFloatVar(v2);
                case 3->temp.getCharVar(v2);
                default->getDefaultError("Invalid type for comparison (Only int, float and char allowed).");
            };
        }
        else
        {
            VMError.logvm("Invalid value for comparison.");
        }

        if(main.contains(target))
        {
            main.setVar(target, (a!=b));
        }
        else if(target.startsWith("$"))
        {
            if(temp.contains(target))
            {
                temp.setVar(target, (a!=b));
            }
            else
            {
                temp.addVar(target, (a!=b));
            }
        }
        else
        {
            VMError.logvm("Target variable not found.");
        }
    }

    /**
     * The following methods work with logical operators.
     */

    /**
     * Interprets the 'and' function in the ArchVM Bytecode.<p>
     * The 'and' function stores the value of v1 && v2 in target.<p>
     * A 'and' instruction has the following format: and v1, v2, target<p>
     *
     * @param args Contains the arguments of the instruction.
     */
    public void and(String args)
    {
        StringTokenizer st = new StringTokenizer(args,", ");
        boolean a=searchAndGetBoolean(st.nextToken());
        boolean b=searchAndGetBoolean(st.nextToken());

        boolean c = a && b;

        String target = st.nextToken();

        if(main.contains(target))
        {
            main.setVar(target, c);
        }
        else if(target.startsWith("$"))
        {
            if(temp.contains(target))
            {
                temp.setVar(target, c);
            }
            else
            {
                temp.addVar(target, c);
            }
        }
    }

    /**
     * Interprets the 'or' function in the ArchVM Bytecode.<p>
     * The 'or' function stores the value of v1 || v2 in target.<p>
     * A 'or' instruction has the following format: or v1, v2, target<p>
     *
     * @param args Contains the arguments of the instruction.
     */
    public void or(String args)
    {
        StringTokenizer st = new StringTokenizer(args,", ");
        boolean a=searchAndGetBoolean(st.nextToken());
        boolean b=searchAndGetBoolean(st.nextToken());

        boolean c = a || b;

        String target = st.nextToken();

        if(main.contains(target))
        {
            main.setVar(target, c);
        }
        else if(target.startsWith("$"))
        {
            if(temp.contains(target))
            {
                temp.setVar(target, c);
            }
            else
            {
                temp.addVar(target, c);
            }
        }
    }

    /**
     * Interprets the 'xor' function in the ArchVM Bytecode.<p>
     * The 'xor' function stores the value of v1 && v2 in target.<p>
     * A 'xor' instruction has the following format: xor v1, v2, target<p>
     *
     * @param args Contains the arguments of the instruction.
     */
    public void xor(String args)
    {
        StringTokenizer st = new StringTokenizer(args,", ");
        boolean a=searchAndGetBoolean(st.nextToken());
        boolean b=searchAndGetBoolean(st.nextToken());

        boolean c = a ^ b;

        String target = st.nextToken();

        if(main.contains(target))
        {
            main.setVar(target, c);
        }
        else if(target.startsWith("$"))
        {
            if(temp.contains(target))
            {
                temp.setVar(target, c);
            }
            else
            {
                temp.addVar(target, c);
            }
        }
    }

    /**
     * Interprets the 'not' function in the ArchVM Bytecode.<p>
     * The 'not' function stores the value of !v1 in target.<p>
     * A 'not' instruction has the following format: not v1, v2, target<p>
     *
     * @param args Contains the arguments of the instruction.
     */
    public void not(String args)
    {
        StringTokenizer st = new StringTokenizer(args,", ");
        boolean a=searchAndGetBoolean(st.nextToken());

        String target = st.nextToken();

        if(main.contains(target))
        {
            main.setVar(target, (!a));
        }
        else if(target.startsWith("$"))
        {
            if(temp.contains(target))
            {
                temp.setVar(target, (!a));
            }
            else
            {
                temp.addVar(target, (!a));
            }
        }
    }

    /**
     * Interprets the 'eq' function in the ArchVM Bytecode.<p>
     * The 'eq' function stores the value of v1 == v2 in target.<p>
     * A 'eq' instruction has the following format: eq v1, v2, target<p>
     *
     * @param args Contains the arguments of the instruction.
     */
    public void eq(String args)
    {
        StringTokenizer st = new StringTokenizer(args,", ");
        boolean a=searchAndGetBoolean(st.nextToken());
        boolean b=searchAndGetBoolean(st.nextToken());

        boolean c = a == b;

        String target = st.nextToken();

        if(main.contains(target))
        {
            main.setVar(target, c);
        }
        else if(target.startsWith("$"))
        {
            if(temp.contains(target))
            {
                temp.setVar(target, c);
            }
            else
            {
                temp.addVar(target, c);
            }
        }
    }

    /**
     * Interprets the 'neq' function in the ArchVM Bytecode.<p>
     * The 'neq' function stores the value of v1 != v2 in target.<p>
     * A 'neq' instruction has the following format: neq v1, v2, target<p>
     *
     * @param args Contains the arguments of the instruction.
     */
    public void neq(String args)
    {
        StringTokenizer st = new StringTokenizer(args,", ");
        boolean a=searchAndGetBoolean(st.nextToken());
        boolean b=searchAndGetBoolean(st.nextToken());

        boolean c = a != b;

        String target = st.nextToken();

        if(main.contains(target))
        {
            main.setVar(target, c);
        }
        else if(target.startsWith("$"))
        {
            if(temp.contains(target))
            {
                temp.setVar(target, c);
            }
            else
            {
                temp.addVar(target, c);
            }
        }
    }

    /**
     * The following methods deal with jumps.
     */

     /**
      * Interprets the 'assert' function in the ArchVM Bytecode.<p>
      * The 'assert' function checks if a condition is true and accordingly return a line number to jump to.<p>
      * An 'assert' function has the following format: assert bool_value true_line, false_line<p>
      * This method has been named azzert as assert is a keyword in Java.
      *
      * @param args Contains the arguments of the instruction.
      * @return The line number to be jumped to.
      */
     public int azzert(String args)
     {
        StringTokenizer st = new StringTokenizer(args, ", ");
        boolean b=searchAndGetBoolean(st.nextToken());
        int x=Integer.valueOf(st.nextToken()), y=Integer.valueOf(st.nextToken());
        return (b)?x:y;
     }

   /**
    * Interprets the 'goto' function in the ArchVM Bytecode.<p>
    * The 'goto' function is used to jump to a line in the .jvse file.<p>
    * An 'goto' function has the following format: goto line<p>
    * This method has been named gotu as goto is a reserved word in Java.

    * @param args Contains the arguments of the instruction.
    * @return The line number to be jumped to.
    */
    public int gotu(String args)
    {
        return Integer.valueOf(args);
    }

    /**
     * Interprets the 'print' function in the ArchVM Bytecode.
     * The 'print' function prints literals and variables to screen.
     * A 'print' function has the following format: print args_separated_by_comma_and_space (", ")
     *
     * @param args Contains the arguments of the instruction.
     */
    public void print(String args)
    {
        List<String> parts = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder current = new StringBuilder();
        for (int i = 0; i < args.length(); i++)
        {
            char c = args.charAt(i);
            if (c == '\"')
            {
                inQuotes = !inQuotes;
                current.append(c);
            }
            else if (c == ',' && !inQuotes)
            {
                parts.add(current.toString().trim());
                current.setLength(0);
            }
            else
            {
                current.append(c);
            }
        }
        if (current.length() > 0)
        {
            parts.add(current.toString().trim());
        }

        for (String part : parts)
        {
            if (part.startsWith("\"") && part.endsWith("\""))
            {
                // Remove quotes and interpret escape sequences
                String literal = part.substring(1, part.length() - 1);
                System.out.print(unescapeJavaString(literal));
            }
            else if (main.contains(part))
            {
                if (main.getTypeCode(part) == 1)
                {
                    System.out.print(main.getIntVar(part));
                }
                else if (main.getTypeCode(part) == 2)
                {
                    System.out.print(main.getFloatVar(part));
                }
                else if (main.getTypeCode(part) == 3)
                {
                    System.out.print(main.getCharVar(part));
                }
                else if(main.getTypeCode(part)==4)
                {
                    System.out.print(main.getBoolVar(part));
                }
            }
            else if (temp.contains(part))
            {
                if (temp.getTypeCode(part) == 1)
                {
                    System.out.print(temp.getIntVar(part));
                }
                else if (temp.getTypeCode(part) == 2)
                {
                    System.out.print(temp.getFloatVar(part));
                }
                else if (temp.getTypeCode(part) == 3)
                {
                    System.out.print(temp.getCharVar(part));
                }
                else if(temp.getTypeCode(part)==4)
                {
                    System.out.print(temp.getBoolVar(part));
                }
            }
            else
            {
                VMError.logvm("Invalid arguments.");
            }
        }
    }

    public void printTime()
    {
        System.out.println(new Date().toString());
    }

    public void handleNative(String args)
    {
        try
        {
            // Split input: e.g. NativeTest.java test2(v) y
            String[] parts = args.trim().split("\\s+", 3);
            String classFile = parts[0];  // NativeTest.java
            String methodAndArgs = parts[1];  // test2(v)
            String resultVar = parts.length > 2 ? parts[2] : null;

            // Class name without extension
            String className = classFile.replace(".java", "");

            // Parse method name and argument list
            int openParen = methodAndArgs.indexOf('(');
            int closeParen = methodAndArgs.indexOf(')');
            String methodName = methodAndArgs.substring(0, openParen);
            String argList = methodAndArgs.substring(openParen + 1, closeParen).trim();

            // Split arguments if present
            String[] argNames = argList.isEmpty() ? new String[0] : argList.split("\\s*,\\s*");

            Object[] argValues = new Object[argNames.length];
            Class<?>[] paramTypes = new Class<?>[argNames.length];

            for (int i = 0; i < argNames.length; i++)
            {
                String varName = argNames[i];
                int typeCode = getVariableTypeCode(varName);  // Implement this to get 1,2,3,4
                paramTypes[i] = mapTypeCodeToClass(typeCode);
                argValues[i] = getVariableValueBoxed(varName, typeCode);
            }

            // Load class
            Class<?> clazz = Class.forName(className);

            // Lookup method with exact signature
            Method method = clazz.getMethod(methodName, paramTypes);

            // Invoke static method with args
            Object ret = method.invoke(null, argValues);

            if (method.getReturnType() != void.class && resultVar != null && !resultVar.isEmpty()) {
                if (ret != null) {
                    setVariableValue(resultVar, ret);
                }
            }

        }
        catch (Exception e)
        {
            System.err.println("Error in handleNative: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Map your typeCode to Java primitive Class
    private Class<?> mapTypeCodeToClass(int typeCode)
    {
        switch (typeCode)
        {
            case 1: return long.class;     // jvs int = Java long
            case 2: return double.class;   // jvs float/double
            case 3: return char.class;     // jvs char
            case 4: return boolean.class;  // jvs boolean
            default: return Object.class;
        }
    }

    // Return boxed value for reflection call based on typeCode
    private Object getVariableValueBoxed(String varName, int typeCode)
    {
        if (varName.startsWith("$"))
        {
            switch (typeCode)
            {
                case 1: return Long.valueOf(temp.getIntVar(varName));
                case 2: return Double.valueOf(temp.getFloatVar(varName));
                case 3: return Character.valueOf(temp.getCharVar(varName));
                case 4: return Boolean.valueOf(temp.getBoolVar(varName));
            }
        }
        else
        {
            switch (typeCode)
            {
                case 1: return Long.valueOf(main.getIntVar(varName));
                case 2: return Double.valueOf(main.getFloatVar(varName));
                case 3: return Character.valueOf(main.getCharVar(varName));
                case 4: return Boolean.valueOf(main.getBoolVar(varName));
            }
        }
        return null;
    }

    // Store returned value to main or temp VarManager, handling type conversion if needed
    private void setVariableValue(String varName, Object value)
    {
        boolean isTemp = varName.startsWith("$");
        if (value == null) return;

        if (value instanceof Long)
        {
            if (isTemp) temp.setVar(varName, (Long) value);
            else main.setVar(varName, (Long) value);
        }
        else if (value instanceof Double)
        {
            if (isTemp) temp.setVar(varName, (Double) value);
            else main.setVar(varName, (Double) value);
        }
        else if (value instanceof Character)
        {
            if (isTemp) temp.setVar(varName, (Character) value);
            else main.setVar(varName, (Character) value);
        }
        else if (value instanceof Boolean)
        {
            if (isTemp) temp.setVar(varName, (Boolean) value);
            else main.setVar(varName, (Boolean) value);
        }
        else
        {
            // handle Object or other types if needed
        }
    }

    // Dummy placeholder, you must implement this to return your variable's type code (1-4)
    private int getVariableTypeCode(String varName)
    {
        return ((varName.startsWith("$"))?temp.getTypeCode(varName):main.getTypeCode(varName));
    }


    /**
     * Helper methods.
     */

    /**
     * Checks if the string is a valid JVS integer.
     *
     * @param s The string to be checked.
     * @return True if the string is a valid JVS integer, false otherwise.
     */
    private boolean isInt(String s)
    {
        try
        {
            Long.parseLong(s);
            return true;
        }
        catch(NumberFormatException e)
        {
            return false;
        }
    }

    /**
     * Checks if the string is a valid JVS float.
     *
     * @param s The string to be checked.
     * @return True if the string is a valid JVS float, false otherwise.
     */
    private boolean isFloat(String s)
    {
        try
        {
            Double.parseDouble(s);
            return true;
        }
        catch(NumberFormatException e)
        {
            return false;
        }
    }

    /**
     * Checks if the two types are compatible for casting.<p>
     * The types are compatible if they are either int, float, char or boolean.
     *
     * @param type1 The first type.
     * @param type2 The second type.
     * @return True if they are compatible,otherwise false.
     */
    private boolean isCompatible(String type1, String type2)
    {
        return isIn(type1, new String[]{"int", "float", "char"}) && isIn(type2, new String[]{"int", "float", "char"});
    }

    /**
     * Checks if a String is in an array of Strings.
     * @param a The string to be checked.
     * @param args The array of strings.
     * @return True if the string is in the array, otherwise false.
     */
    private boolean isIn(String a, String args[])
    {
        for(String s: args)
        {
            if(a.equals(s))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * This method logs a VM Error with the message specified by the message parameter and
     * returns a default value.<p>
     *
     * @param msg
     * @return The default value (0).
     */
    private int getDefaultError(String msg)
    {
        VMError.logvm(msg);
        return 0;
    }

    /**
     * This method is a helper method used by methods associated with logical operations to get<p>
     * the required boolean values.
     *
     * @param value The value or name of variable containing a boolean variable.
     * @return The boolean value.
     */
    private boolean searchAndGetBoolean(String value)
    {
        if(value.equals("true"))
        {
            return true;
        }
        else if(value.equals("false"))
        {
            return false;
        }
        else if(main.contains(value))
        {
            return main.getBoolVar(value);
        }
        else if(temp.contains(value))
        {
            return temp.getBoolVar(value);
        }
        else
        {
            VMError.logvm("Invalid value.");
        }

        return false;
    }

    // Helper method to interpret escape sequences in string literals
    private String unescapeJavaString(String str)
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++)
        {
            char ch = str.charAt(i);
            if (ch == '\\' && i + 1 < str.length())
            {
                char next = str.charAt(i + 1);
                switch (next)
                {
                    case 'b': sb.append('\b'); i++; break;
                    case 't': sb.append('\t'); i++; break;
                    case 'n': sb.append('\n'); i++; break;
                    case 'f': sb.append('\f'); i++; break;
                    case 'r': sb.append('\r'); i++; break;
                    case '\"': sb.append('\"'); i++; break;
                    case '\'': sb.append('\''); i++; break;
                    case '\\': sb.append('\\'); i++; break;
                    default: sb.append(ch); break;
                }
            }
            else
            {
                sb.append(ch);
            }
        }
        return sb.toString();
    }
}