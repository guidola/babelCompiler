package edu.salleurl.g6.model;

public class Token {

    private TokenType   type;
    private String      lexem;


    public Token(TokenType type, String lexem) {
        this.type = type;
        this.lexem = lexem;
    }

    public TokenType getType() {
        return type;
    }

    public void setType(TokenType type) {
        this.type = type;
    }

    public String getLexem() {
        return lexem;
    }

    public void setLexem(String lexem) {
        this.lexem = lexem;
    }

    @Override
    public String toString() {
        return "[" + type + "] " + lexem;
    }
}
