package com.jvs.compiler;

import java.util.List;

public interface Backend
{
    public List<String> compile(String sourceCode);
    public void write(String outputFile, List<String> compiledCode) throws Exception;
}
