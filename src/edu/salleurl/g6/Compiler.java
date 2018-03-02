package edu.salleurl.g6;

import edu.salleurl.g6.alex.Alex;
import java.io.*;

public class Compiler {

    private Reader in;
    private int r;
    private int line;
    private Alex alex;

    public Compiler() {
        line = 1;
    }

    private void __handleLexInput() {

        try {
            r = in.read();

            while (true) {

                if( r == -1 ) {
                    alex.nextChar(-1, ' ');
                    break;
                }

                char ch = (char) r;

                switch(alex.nextChar(line, Character.toLowerCase(ch))) {
                    case Alex.STEP:
                        if(Character.toString((char)r).equals("\n")){line++;}
                        r = in.read();
                        break;
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void analyze(String filename) {

        alex = new Alex(filename);

        try {
            InputStream is = new FileInputStream(new File(filename));
            Reader reader = new InputStreamReader(is);
            in = new BufferedReader(reader);
        } catch (IOException e) {
            System.err.println("Invalid filename. Abort.");
            System.exit(1);
        }

        __handleLexInput();

    }

}
