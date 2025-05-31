package com.jvs.archvm;

import com.jvs.bytecode.*;

import java.util.Map;
import java.util.HashMap;

public class ArchVM
{
    private int line;
    private VarManager main, temp;
    private VMInterpreter vmi;
    private Context context;
    private JVSEInstructions instructions;
    private Map<String, Integer> returnLines;
    private Map<String, String> methodLinks;
    private String currentMethod, previousMethod;

    public ArchVM(String file)
    {
        line=0;
        main=new VarManager();
        temp=new VarManager();
        vmi=new VMInterpreter(main, temp);
        context=new Context(file);
        instructions=JVSELoader.load(file);
        returnLines=new HashMap<>();
        methodLinks=new HashMap<>();
        currentMethod="<main>";
        previousMethod="";
        context.addCall(currentMethod, line+1);
        methodLinks.put(currentMethod, previousMethod);
    }

    public void start()
    {
        for(line=1;line<=instructions.getInstructionCount();line++)
        {
            int prevLine = line;
            interpret(instructions.getInstruction(line));
            // If a jump occurred, adjust for the for-loop increment
            if (line != prevLine)
            {
                line--; // Compensate for the for-loop's increment
            }
        }
    }

    private void interpret(Instruction instruction)
    {
        Opcode o=instruction.getOpcode();
        String args=instruction.getArgs();

        switch (o)
        {
            case PUT->vmi.put(args);
            case SET->vmi.set(args);
            case CAST->vmi.cast(args);
            case IADD->vmi.iadd(args);
            case FADD->vmi.fadd(args);
            case ISUB->vmi.isub(args);
            case FSUB->vmi.fsub(args);
            case IMUL->vmi.imult(args);
            case FMUL->vmi.fmult(args);
            case IDIV->vmi.idiv(args);
            case FDIV->vmi.fdiv(args);
            case IMOD->vmi.imod(args);
            case FMOD->vmi.fmod(args);
            case IPOW->vmi.ipow(args);
            case FPOW->vmi.fpow(args);
            case INCR->vmi.incr(args);
            case DECR->vmi.decr(args);
            case CLRTLIST->vmi.clrtlist();
            case CLRMAINL->vmi.clrmainl();
            case CMPGT->vmi.cmpgt(args);
            case CMPGTE->vmi.cmpgte(args);
            case CMPLT->vmi.cmplt(args);
            case CMPLTE->vmi.cmplte(args);
            case CMPE->vmi.cmpe(args);
            case CMPNE->vmi.cmpne(args);
            case AND->vmi.and(args);
            case OR->vmi.or(args);
            case XOR->vmi.xor(args);
            case NOT->vmi.not(args);
            case EQ->vmi.eq(args);
            case NEQ->vmi.neq(args);
            case ASSERT->line=vmi.azzert(args);
            case GOTO->line=vmi.gotu(args);
            case CALL->registerCall(args, instructions.getMethodIndex(args));
            case PRINT->vmi.print(args);
            case RETURN->returnToLine();
            default->doNothing();
        }
    }

    private void doNothing()
    {
        return;
    }

    private void registerCall(String name, int newLine)
    {
        int returnLine=line+1;
        line=newLine;
        context.addCall(name, newLine);
        returnLines.put(name, returnLine);
        previousMethod=currentMethod;
        currentMethod=name;
        methodLinks.put(currentMethod, previousMethod);
    }

    private void returnToLine()
    {
        line=returnLines.get(previousMethod);
        currentMethod=methodLinks.get(currentMethod);
        previousMethod=methodLinks.get(previousMethod);
        context.addReturn(currentMethod, line);
    }
}
