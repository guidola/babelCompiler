package edu.salleurl.g6.model;

import java.util.HashMap;

public class ReservedWordsDictionary {

    private HashMap<String, Token> reservedWords;

    public ReservedWordsDictionary() {
        super();

        this.reservedWords.put(ReservedWords.CERT, new Token(TokenType.LOGIC_CONSTANT, ReservedWords.CERT));
        this.reservedWords.put(ReservedWords.FALS, new Token(TokenType.LOGIC_CONSTANT , ReservedWords.FALS ));

        this.reservedWords.put(ReservedWords.AND, new Token(TokenType.RELATIONAL_OPERATOR , ReservedWords.AND ));
        this.reservedWords.put(ReservedWords.OR, new Token(TokenType.RELATIONAL_OPERATOR , ReservedWords.OR ));
        this.reservedWords.put(ReservedWords.NOT, new Token(TokenType.RELATIONAL_OPERATOR , ReservedWords.NOT ));

        this.reservedWords.put(ReservedWords.SENCER, new Token(TokenType.SIMPLE_TYPE , ReservedWords.SENCER ));
        this.reservedWords.put(ReservedWords.LOGIC, new Token(TokenType.SIMPLE_TYPE , ReservedWords.LOGIC ));

        this.reservedWords.put(ReservedWords.INICI, new Token(TokenType.KEYWORD , ReservedWords.INICI ));
        this.reservedWords.put(ReservedWords.FI, new Token(TokenType.KEYWORD , ReservedWords.FI ));
        this.reservedWords.put(ReservedWords.CONST, new Token(TokenType.KEYWORD , ReservedWords.CONST ));
        this.reservedWords.put(ReservedWords.FUNCIO, new Token(TokenType.KEYWORD , ReservedWords.FUNCIO ));
        this.reservedWords.put(ReservedWords.VECTOR, new Token(TokenType.KEYWORD , ReservedWords.VECTOR ));
        this.reservedWords.put(ReservedWords.DE, new Token(TokenType.KEYWORD , ReservedWords.DE ));
        this.reservedWords.put(ReservedWords.ESCRIURE, new Token(TokenType.KEYWORD , ReservedWords.ESCRIURE ));
        this.reservedWords.put(ReservedWords.LLEGIR, new Token(TokenType.KEYWORD , ReservedWords.LLEGIR ));
        this.reservedWords.put(ReservedWords.REPETIR, new Token(TokenType.KEYWORD , ReservedWords.REPETIR ));
        this.reservedWords.put(ReservedWords.FINS, new Token(TokenType.KEYWORD , ReservedWords.FINS ));
        this.reservedWords.put(ReservedWords.MENTRE, new Token(TokenType.KEYWORD , ReservedWords.MENTRE ));
        this.reservedWords.put(ReservedWords.FER, new Token(TokenType.KEYWORD , ReservedWords.FER ));
        this.reservedWords.put(ReservedWords.FIMENTRE, new Token(TokenType.KEYWORD , ReservedWords.FIMENTRE ));
        this.reservedWords.put(ReservedWords.SI, new Token(TokenType.KEYWORD , ReservedWords.SI ));
        this.reservedWords.put(ReservedWords.LLAVORS, new Token(TokenType.KEYWORD , ReservedWords.LLAVORS ));
        this.reservedWords.put(ReservedWords.SINO, new Token(TokenType.KEYWORD , ReservedWords.SINO ));
        this.reservedWords.put(ReservedWords.FISI, new Token(TokenType.KEYWORD , ReservedWords.FISI ));
        this.reservedWords.put(ReservedWords.RETORNAR, new Token(TokenType.KEYWORD , ReservedWords.RETORNAR ));
        
    }
    
    public Token check(String word) {
        return this.reservedWords.get(word);
    }

}
