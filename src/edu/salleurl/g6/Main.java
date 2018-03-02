package edu.salleurl.g6;

public class Main {

    public static void main(String[] args) {

        Compiler c = new Compiler();

        if (args.length != 1) {
            System.exit(2);
        }

        c.analyze(args[0]);


    }
}
