package edu.salleurl.g6.alex;

import edu.salleurl.g6.model.Token;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

public class LexOutputGenerator {

    private PrintWriter lexFile;
    private PrintWriter errFile;

    public LexOutputGenerator(String filename) {
        String fn = filename.split("\\.bab")[0];

        initLexOutFile(fn);
        initErrOutFile(fn);
    }


    private void initLexOutFile(String filename) {

        try {
            lexFile = new PrintWriter(filename + ".lex");
        } catch (FileNotFoundException e) {
            File  f = new File(filename + ".lex");
            f.getParentFile().mkdirs();
            try {
                f.createNewFile();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            initLexOutFile(filename);
        }
    }

    private void initErrOutFile(String filename) {

        try {
            errFile = new PrintWriter(filename + ".err");
        } catch (FileNotFoundException e) {
            File  f = new File(filename + ".err");
            f.getParentFile().mkdirs();
            try {
                f.createNewFile();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            initLexOutFile(filename);
        }
    }

    public Token writeToken(Token t) {
        lexFile.println(t.toString());
        return t;
    }

    public void writeError(String e) {
        errFile.println(e);
    }

    public void close() {
        lexFile.close();
        errFile.close();
    }


}
