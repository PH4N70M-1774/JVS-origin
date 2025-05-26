package com.jvs.compiler;

public class JVSError
{
    public static void log(String file, int line, String message, String stmt, int pos, int end, boolean isRed)
    {
        String red="\u001B[31m";
        String reset="\u001B[0m";

        String e1='|'+((file.endsWith(".jvs"))?file:file+".jvs")+": Error at line "+line+" - "+message+"\n|";
        String e2="|   "+stmt;
        String e3="|   ";
        String e4="|(Line "+line+", Column" + ((pos==end)?"":"s") + " "+pos+((pos==end)?"":(pos+1==end)?" and "+end:" to "+end)+")";

        for(int i=0;i<=end;i++)
        {
            if(i>=pos)
            {
                e3+="^";
            }
            else
            {
                e3+=" ";
            }
        }

        System.out.println(((isRed)?red: "")+e1);
        System.out.println(e2);
        System.out.println(e3);
        System.out.println(e4+'\n'+reset);
    }
}