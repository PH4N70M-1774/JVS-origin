package com.jvs.compiler;

import java.io.File;
import java.util.Scanner;

public class FileScanner {
    public static String getSource(String file) throws Exception {
        Scanner sc = new Scanner(new File(file));
        String src = "";

        while (sc.hasNextLine()) {
            src += sc.nextLine()+"\n";
        }

        sc.close();

        return src;
    }
}
