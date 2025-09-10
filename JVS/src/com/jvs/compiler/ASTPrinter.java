package com.jvs.compiler;

import java.util.Deque;
import java.util.LinkedList;

import com.jvs.compiler.nodes.*;

public class ASTPrinter {
    private final ProgramNode root;
    private final Deque<String> tabs = new LinkedList<>();

    private ASTPrinter(ProgramNode node) {
        this.root = node;
    }

    public static ASTPrinter forNode(ProgramNode node) {
        return new ASTPrinter(node);
    }

    public void printAST() {
        System.out.println(root.name);
        for (int i = 0; i < root.subnodes.size(); i++) {
            boolean isLast = (i == root.subnodes.size() - 1);
            printNode(root.subnodes.get(i), isLast);
        }
    }

    private void printNode(Node node, boolean isLast) {
        System.out.print(getTabs());
        System.out.println((isLast ? " ┗╸" : " ┣╸") + node);

        tabs.addLast(isLast ? "   " : " ┃ ");

        if (node instanceof BinaryOpNode) {
            BinaryOpNode binOp = (BinaryOpNode) node;
            printNode(binOp.left, false);
            printNode(binOp.right, true);
        } else if (node instanceof IntAssignNode) {
            IntAssignNode intAssignNode = (IntAssignNode) node;
            printNode(intAssignNode.identifierNode, false);
            printNode(intAssignNode.binaryOpNode, true);
        } else if (node instanceof PrintNode) {
            PrintNode printNode = (PrintNode) node;
            for (int i = 0; i < printNode.nodesToPrint.size(); i++) {
                boolean isLastPrint = (i == printNode.nodesToPrint.size() - 1);
                printNode(printNode.nodesToPrint.get(i), isLastPrint);
            }

        }

        tabs.removeLast();
    }

    private String getTabs() {
        StringBuilder sb = new StringBuilder();
        for (String t : tabs) {
            sb.append(t);
        }
        return sb.toString();
    }
}
