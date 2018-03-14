package edu.salleurl.g6.model;

public class SyntacticException extends Exception {

    private int type;
    private TokenType[] expected;
    private TokenType consumed;

    public SyntacticException(int type, TokenType []expected, TokenType consumed, String message) {
        super(message);
        this.type = type;
        this.expected = expected;
        this.consumed = consumed;
    }

    public int getType() {
        return type;
    }

}
