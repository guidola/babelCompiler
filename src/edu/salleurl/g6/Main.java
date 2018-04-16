package edu.salleurl.g6;

import edu.salleurl.g6.alex.Alex;

public class Main {

    public static void main(String[] args) {

        Compiler c = new Compiler();

        if (args.length != 1) {
            System.exit(2);
        }

        c.analyze(args[0]);
        Alex.commit();

    }
}
