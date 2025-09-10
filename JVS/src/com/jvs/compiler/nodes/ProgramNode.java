package com.jvs.compiler.nodes;

import java.util.ArrayList;
import java.util.List;

public class ProgramNode {
    public String name;
    public List<Node> subnodes;

    public ProgramNode(String name) {
        this.name = name;
        subnodes = new ArrayList<>();
    }

    public void addNode(Node node) {
        subnodes.add(node);
    }
}
