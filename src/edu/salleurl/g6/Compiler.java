package edu.salleurl.g6;

import edu.salleurl.g6.alex.Alex;
import edu.salleurl.g6.model.Token;

import java.io.*;

public class Compiler {


    private Alex alex;

    public Compiler() {
    }


    public void analyze(String filename) {

        alex = new Alex(filename);

        Token t = null;

        while( t == null || !t.isEOF() ) {

            t = alex.getToken();

        }

    }

}
