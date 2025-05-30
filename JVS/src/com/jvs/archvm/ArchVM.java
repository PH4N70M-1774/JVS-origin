package com.jvs.archvm;

import com.jvs.bytecode.*;

public class ArchVM
{
    private int line, returnLine;
    private VarManager main, temp;
    private VMInterpreter vmi;
    private Context ct;
    private JVSEInstructions instructions;

    public ArchVM(String file)
    {
        line=0;
        returnLine=0;
        main=new VarManager();
        temp=new VarManager();
        vmi=new VMInterpreter(main, temp);
        ct=new Context(file);
        instructions=JVSELoader.load(file);
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
        returnLine=line;
        line=newLine;
        ct.addCall(name, newLine);
    }

    private void returnToLine()
    {
        line=returnLine+1;
    }
}
