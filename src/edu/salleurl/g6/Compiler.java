package edu.salleurl.g6;

import edu.salleurl.g6.asi.Asi;
import edu.salleurl.g6.model.SyntacticException;


public class Compiler {


    private Asi asi;

    public Compiler() {
    }


    public void analyze(String filename) {

        asi = new Asi(filename);

        try {
            asi.programa();
        } catch (SyntacticException e) {
            e.printStackTrace();
        }


    }

}
