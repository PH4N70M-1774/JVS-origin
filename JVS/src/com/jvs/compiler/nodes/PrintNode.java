package com.jvs.compiler.nodes;

import java.util.ArrayList;
import java.util.List;

public class PrintNode implements Node {
    public List<Node> nodesToPrint;

    public PrintNode() {
        nodesToPrint = new ArrayList<>();
    }

    public void addNode(Node node) {
        nodesToPrint.add(node);
    }

    @Override
    public String toString() {
        return "PrintNode";
    }
}
