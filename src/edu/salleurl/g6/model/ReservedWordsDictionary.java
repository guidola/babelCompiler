package edu.salleurl.g6.model;

import java.util.HashMap;

public class ReservedWordsDictionary {

    private static HashMap<String, Token> reservedWords;
    private static ReservedWordsDictionary dict;

    private ReservedWordsDictionary(){
        super();
        reservedWords = new HashMap<>();

        reservedWords.put(ReservedWords.CERT, new Token(TokenType.LOGIC_CONSTANT, ReservedWords.CERT));
        reservedWords.put(ReservedWords.FALS, new Token(TokenType.LOGIC_CONSTANT , ReservedWords.FALS ));

        reservedWords.put(ReservedWords.AND, new Token(TokenType.AND , ReservedWords.AND ));
        reservedWords.put(ReservedWords.OR, new Token(TokenType.OR , ReservedWords.OR ));
        reservedWords.put(ReservedWords.NOT, new Token(TokenType.NOT , ReservedWords.NOT ));

        reservedWords.put(ReservedWords.SENCER, new Token(TokenType.SIMPLE_TYPE , ReservedWords.SENCER ));
        reservedWords.put(ReservedWords.LOGIC, new Token(TokenType.SIMPLE_TYPE , ReservedWords.LOGIC ));

        reservedWords.put(ReservedWords.INICI, new Token(TokenType.INICI , ReservedWords.INICI ));
        reservedWords.put(ReservedWords.FI, new Token(TokenType.FI , ReservedWords.FI ));
        reservedWords.put(ReservedWords.CONST, new Token(TokenType.CONST , ReservedWords.CONST ));
        reservedWords.put(ReservedWords.FUNCIO, new Token(TokenType.FUNCIO , ReservedWords.FUNCIO ));
        reservedWords.put(ReservedWords.VECTOR, new Token(TokenType.VECTOR , ReservedWords.VECTOR ));
        reservedWords.put(ReservedWords.DE, new Token(TokenType.DE , ReservedWords.DE ));
        reservedWords.put(ReservedWords.ESCRIURE, new Token(TokenType.ESCRIURE , ReservedWords.ESCRIURE ));
        reservedWords.put(ReservedWords.LLEGIR, new Token(TokenType.LLEGIR , ReservedWords.LLEGIR ));
        reservedWords.put(ReservedWords.REPETIR, new Token(TokenType.REPETIR , ReservedWords.REPETIR ));
        reservedWords.put(ReservedWords.FINS, new Token(TokenType.FINS , ReservedWords.FINS ));
        reservedWords.put(ReservedWords.MENTRE, new Token(TokenType.MENTRE , ReservedWords.MENTRE ));
        reservedWords.put(ReservedWords.FER, new Token(TokenType.FER , ReservedWords.FER ));
        reservedWords.put(ReservedWords.FIMENTRE, new Token(TokenType.FIMENTRE , ReservedWords.FIMENTRE ));
        reservedWords.put(ReservedWords.SI, new Token(TokenType.SI , ReservedWords.SI ));
        reservedWords.put(ReservedWords.LLAVORS, new Token(TokenType.LLAVORS , ReservedWords.LLAVORS ));
        reservedWords.put(ReservedWords.SINO, new Token(TokenType.SINO , ReservedWords.SINO ));
        reservedWords.put(ReservedWords.FISI, new Token(TokenType.FISI , ReservedWords.FISI ));
        reservedWords.put(ReservedWords.RETORNAR, new Token(TokenType.RETORNAR , ReservedWords.RETORNAR ));


        reservedWords.put(SpecialChars.STATEMENT_SEPARATOR, new Token(TokenType.STATEMENT_SEPARATOR , SpecialChars.STATEMENT_SEPARATOR));
        reservedWords.put(SpecialChars.RETURN_TYPE_PREFIX, new Token(TokenType.RETURN_TYPE_PREFIX, SpecialChars.RETURN_TYPE_PREFIX));
        reservedWords.put(SpecialChars.DECIMAL, new Token(TokenType.DECIMAL, SpecialChars.DECIMAL));
        reservedWords.put(SpecialChars.ARGUMENT_SEPARATOR, new Token(TokenType.ARGUMENT_SEPARATOR, SpecialChars.ARGUMENT_SEPARATOR));
        reservedWords.put(SpecialChars.PARENTHESIS_OPEN, new Token(TokenType.PARENTHESIS_OPEN, SpecialChars.PARENTHESIS_OPEN));
        reservedWords.put(SpecialChars.PARENTHESIS_CLOSE, new Token(TokenType.PARENTHESIS_CLOSE, SpecialChars.PARENTHESIS_CLOSE));
        reservedWords.put(SpecialChars.SQUARE_BRACKETS_OPEN, new Token(TokenType.SQUARE_BRACKETS_OPEN, SpecialChars.SQUARE_BRACKETS_OPEN));
        reservedWords.put(SpecialChars.SQUARE_BRACKETS_CLOSE, new Token(TokenType.SQUARE_BRACKETS_CLOSE, SpecialChars.SQUARE_BRACKETS_CLOSE));
        reservedWords.put(SpecialChars.BRACKETS_OPEN, new Token(TokenType.BRACKETS_OPEN, SpecialChars.BRACKETS_OPEN));
        reservedWords.put(SpecialChars.BRACKETS_CLOSE, new Token(TokenType.BRACKETS_CLOSE, SpecialChars.BRACKETS_CLOSE));
        reservedWords.put(SpecialChars.AMPERSAND, new Token(TokenType.AMPERSAND, SpecialChars.AMPERSAND));
    }

    public static ReservedWordsDictionary getInstance() {
        return dict == null ? new ReservedWordsDictionary() : dict;
    }
    
    public Token check(String word) {
        Token t = reservedWords.get(word);
        return t == null ? new Token(TokenType.IDENTIFIER, word) : t;
    }

}
