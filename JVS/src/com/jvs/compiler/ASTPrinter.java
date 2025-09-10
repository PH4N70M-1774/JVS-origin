package com.jvs.compiler;

import com.jvs.compiler.nodes.*;

//┣┗ ╸┃

public class ASTPrinter {
    private ProgramNode node;

    private ASTPrinter(ProgramNode node) {
        this.node = node;
    }

    public static ASTPrinter forNode(ProgramNode node) {
        return new ASTPrinter(node);
    }

    public void printAST() {
        System.out.println(node.name);
        for (int i = 0; i < node.subnodes.size(); i++) {
            if (i == node.subnodes.size() - 1) {
                System.out.println("┗╸" + node.subnodes.get(i));
            } else {
                System.out.println("┣╸");
            }

            if (node.subnodes.get(i) instanceof BinaryOpNode) {
                binaryNode((BinaryOpNode) node.subnodes.get(i));
            }
        }
    }

    private void binaryNode(BinaryOpNode binOp) {
        System.out.println(":[");
        System.out.println(binOp.left);
        System.out.println(binOp.right);
        System.out.println("]");
    }
}
