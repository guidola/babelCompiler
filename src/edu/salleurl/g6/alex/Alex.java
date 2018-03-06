package edu.salleurl.g6.alex;

import edu.salleurl.g6.model.*;

public class Alex {

    public static final int STAY                = 0x01;
    public static final int STAY_AND_NEW_LINE   = 0x02;
    public static final int STEP                = 0x03;
    public static final int STEP_AND_NEW_LINE   = 0x04;
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


    private int state;
    private String actualLexem;
    private LexOutputGenerator log;

    public Alex(String filename) {
        state = 0x00;
        actualLexem = "";
        log = new LexOutputGenerator(filename);

    }

    public int nextChar(int line, char c) {

        switch(state) {

            case 0x00:
                actualLexem = Character.toString(c);
                if(Character.toString(c).matches(sre_whitespacesTabsAndLinejumps)) {
                    if (line == -1 ) {
                        log.writeToken(new Token(TokenType.EOF, ""));
                        log.close();
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
                    log.writeToken(new Token(TokenType.COMPLEX_ARITHMETIC_OPERATOR, Character.toString(c)));
                    return STEP;

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
                    log.writeToken(new Token(TokenType.SIMPLE_ARITHMETIC_OPERATOR, Character.toString(c)));
                    return STEP;

                } else if(Character.toString(c).matches(sre_specialCharacters)) {
                    state = 0x00;
                    log.writeToken(ReservedWordsDictionary.getInstance().check(actualLexem));
                    return STEP;
                } else {
                    log.writeError(ErrorFactory.error(ErrorTypes.LEX_UNKNOWN_CHAR, line, c));
                    return STEP;
                }

            case 0x01:
                if (c == '=') {
                    actualLexem = actualLexem + Character.toString(c);
                    log.writeToken(new Token(TokenType.RELATIONAL_OPERATOR, actualLexem));
                    state = 0x00;
                    return STEP;
                } else {
                    log.writeToken(new Token(TokenType.ASSIGNMENT, actualLexem));
                    state = 0x00;
                    return STAY;
                }


            case 0x02:
                if(c == '"') {
                    state = 0x00;
                    log.writeToken(new Token(TokenType.STRING, actualLexem));

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
                    log.writeToken(new Token(TokenType.COMPLEX_ARITHMETIC_OPERATOR, actualLexem));
                    state = 0x00;
                    return STAY;
                }

            case 0x04:
                if(Character.toString(c).matches(re_Linejumps)) {
                    state = 0x00;
                }
                return STEP;

            case 0x05:
                if(c == '=') {
                    actualLexem = actualLexem + Character.toString(c);
                    log.writeToken(new Token(TokenType.RELATIONAL_OPERATOR, actualLexem));
                    state = 0x00;
                    return STEP;
                } else if (actualLexem.matches("[><]")) {
                    log.writeToken(new Token(TokenType.RELATIONAL_OPERATOR, actualLexem));
                    state = 0x00;
                    return STAY;
                } else {
                    return STAY;
                }

            case 0x06:
                if(!Character.toString(c).matches(re_validIdChars)) {
                    state = 0x00;
                    if(actualLexem.length() <= MAX_STR_LEN){
                        log.writeToken(ReservedWordsDictionary.getInstance().check(actualLexem));
                    }else{
                        log.writeError(ErrorFactory.warning(line,"Identifier is too long"));
                        log.writeToken(ReservedWordsDictionary.getInstance().check(actualLexem.substring(0, MAX_STR_LEN)));
                    }

                    return STAY;
                }
                actualLexem = actualLexem + Character.toString(c);
                return STEP;

            case 0x07:
                if(!Character.toString(c).matches(sre_numbers)) {
                    state = 0x00;
                    log.writeToken(new Token(TokenType.INTEGER_CONSTANT, actualLexem));
                    return STAY;
                }
                actualLexem = actualLexem + Character.toString(c);
                return STEP;

        }

        return STEP;
    }



}
