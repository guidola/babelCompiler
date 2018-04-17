package edu.salleurl.g6.alex;

import edu.salleurl.g6.model.*;

import java.io.*;

public class Alex {

    public static final int STAY                = 0x01;
    public static final int STEP_AND_GOT_TOKEN   = 0x02;
    public static final int STEP                = 0x03;
    public static final int STAY_AND_GOT_TOKEN   = 0x04;
    public static final int EOF   = 0x05;

    private static final String sre_relationalOperators = "[><!]";
    private static final String sre_specialCharacters = "[;:,()\\[\\]{}&]";
    private static final String sre_numbers = "[0-9]";
    private static final String sre_letters = "[a-z]";
    private static final String sre_simpleArithmeticOperator = "[+\\-]";
    private static final String sre_whitespacesTabsAndLinejumps = "[ \t\n\r]";
    private static final String re_Linejumps = "[\n\r]";
    private static final String re_validIdChars = "[a-z_0-9]";


    private static final int MAX_STR_LEN = 32;

    private static Reader in;
    private static int r;
    private static int line;

    private static int state;
    private static Token lastToken;
    private static String actualLexem;
    private static LexOutputGenerator log;

    private static Alex alex;

    private Alex(String filename) {
        state = 0x00;
        actualLexem = "";
        line = 1;
        log = new LexOutputGenerator(filename);
        try {
            InputStream is = new FileInputStream(new File(filename));
            Reader reader = new InputStreamReader(is);
            in = new BufferedReader(reader);
        } catch (IOException e) {
            System.err.println("Invalid filename. Abort.");
            System.exit(1);
        }
        try {
            r = in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void init(String filename) {
        alex = new Alex(filename);
    }

    public static void commit() {
        log.close();
    }

    public static Alex getInstance() throws Exception {
        if (alex == null) {
            throw new Exception("Failed to get an alex instance since it has not been initialized");
        }

        return alex;
    }

    public static int getLine() {return line;}

    private static Token __handleLexInput() {

        try {

            while (true) {

                if( r == -1 ) {
                    nextChar(-1, '\n');
                    return lastToken;
                }

                char ch = (char) r;

                switch(nextChar(line, Character.toLowerCase(ch))) {
                    case STEP_AND_GOT_TOKEN:
                        if(Character.toString((char)r).equals("\n")){line++;}
                        r = in.read();
                        return lastToken;
                    case STEP:
                        if(Character.toString((char)r).equals("\n")){line++;}
                        r = in.read();
                        break;
                    case STAY_AND_GOT_TOKEN:
                        return lastToken;
                    
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
    
    public static Token getToken() {
        
        return __handleLexInput();
        
    }

    public static LexOutputGenerator getLog() {
        return log;
    }

    public static int nextChar(int line, char c) {

        switch(state) {

            case 0x00:
                actualLexem = Character.toString(c);
                if(Character.toString(c).matches(sre_whitespacesTabsAndLinejumps)) {
                    if (line == -1 ) {
                        lastToken = log.writeToken(new Token(TokenType.EOF, ""));
                        return EOF;
                    }
                    return STEP;
                } else if(c == '='){
                    state = 0x01;
                    return STEP;

                } else if(c == '"') {
                    state = 0x02;
                    actualLexem = "";
                    return STEP;

                } else if (c == '*') {
                    lastToken = log.writeToken(new Token(TokenType.COMPLEX_ARITHMETIC_OPERATOR, Character.toString(c)));
                    return STEP_AND_GOT_TOKEN;

                } else if (c == '/') {
                    state = 0x03;
                    return STEP;

                } else if(Character.toString(c).matches(sre_relationalOperators)) {
                    state = 0x05;
                    return STEP;

                } else if(Character.toString(c).matches(sre_letters)) {
                    state = 0x06;
                    return STEP;

                } else if(Character.toString(c).matches(sre_numbers)) {
                    state = 0x07;
                    return STEP;

                } else if(Character.toString(c).matches(sre_simpleArithmeticOperator)) {
                    lastToken = log.writeToken(new Token(TokenType.SIMPLE_ARITHMETIC_OPERATOR, Character.toString(c)));
                    return STEP_AND_GOT_TOKEN;

                } else if(Character.toString(c).matches(sre_specialCharacters)) {
                    state = 0x00;
                    lastToken = log.writeToken(ReservedWordsDictionary.getInstance().check(actualLexem));
                    return STEP_AND_GOT_TOKEN;
                } else {
                    log.writeError(ErrorFactory.error(ErrorTypes.LEX_UNKNOWN_CHAR, line, c));
                    return STEP;
                }

            case 0x01:
                if (c == '=') {
                    actualLexem = actualLexem + Character.toString(c);
                    lastToken = log.writeToken(new Token(TokenType.RELATIONAL_OPERATOR, actualLexem));
                    state = 0x00;
                    return STEP_AND_GOT_TOKEN;
                } else {
                    lastToken = log.writeToken(new Token(TokenType.ASSIGNMENT, actualLexem));
                    state = 0x00;
                    return STAY_AND_GOT_TOKEN;
                }


            case 0x02:
                if(c == '"') {
                    state = 0x00;
                    lastToken = log.writeToken(new Token(TokenType.STRING, actualLexem));
                    return STEP_AND_GOT_TOKEN;
                } else if(Character.toString(c).matches(re_Linejumps)) {
                    log.writeError(ErrorFactory.error(ErrorTypes.LEX_UNTERMINATED_STRING, line, c));
                    state = 0x00;
                } else {
                    actualLexem = actualLexem + Character.toString(c);
                }
                return STEP;

            case 0x03:
                if(c == '/') {
                    state = 0x04;
                    return STEP;
                } else {
                    lastToken = log.writeToken(new Token(TokenType.COMPLEX_ARITHMETIC_OPERATOR, actualLexem));
                    state = 0x00;
                    return STAY_AND_GOT_TOKEN;
                }

            case 0x04:
                if(Character.toString(c).matches(re_Linejumps)) {
                    state = 0x00;
                }
                return STEP;

            case 0x05:
                if(c == '=') {
                    actualLexem = actualLexem + Character.toString(c);
                    lastToken = log.writeToken(new Token(TokenType.RELATIONAL_OPERATOR, actualLexem));
                    state = 0x00;
                    return STEP_AND_GOT_TOKEN;
                } else if (actualLexem.matches("[><]")) {
                    lastToken = log.writeToken(new Token(TokenType.RELATIONAL_OPERATOR, actualLexem));
                    state = 0x00;
                    return STAY_AND_GOT_TOKEN;
                } else {
                    log.writeError(ErrorFactory.error(ErrorTypes.LEX_UNKNOWN_CHAR, line, actualLexem.charAt(0)));
                    state = 0x00;
                    return STAY;
                }

            case 0x06:
                if(!Character.toString(c).matches(re_validIdChars)) {
                    state = 0x00;
                    if(actualLexem.length() <= MAX_STR_LEN){
                        lastToken = log.writeToken(ReservedWordsDictionary.getInstance().check(actualLexem));
                    }else{
                        log.writeError(ErrorFactory.warning(line,"Identifier is too long"));
                        lastToken = log.writeToken(ReservedWordsDictionary.getInstance().check(actualLexem.substring(0, MAX_STR_LEN)));
                    }

                    return STAY_AND_GOT_TOKEN;
                }
                actualLexem = actualLexem + Character.toString(c);
                return STEP;

            case 0x07:
                if(!Character.toString(c).matches(sre_numbers)) {
                    state = 0x00;
                    lastToken = log.writeToken(new Token(TokenType.INTEGER_CONSTANT, actualLexem));
                    return STAY_AND_GOT_TOKEN;
                }
                actualLexem = actualLexem + Character.toString(c);
                return STEP;

        }

        return STEP;
    }



}
