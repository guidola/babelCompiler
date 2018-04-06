package edu.salleurl.g6.asi;

import edu.salleurl.g6.model.TokenType;

public interface SyncVectors {

    TokenType[] decVarId = {TokenType.ASSIGNMENT, TokenType.STATEMENT_SEPARATOR, TokenType.INICI,
            TokenType.BRACKETS_CLOSE, TokenType.FI, TokenType.EOF};




    TokenType[] template = {TokenType.STATEMENT_SEPARATOR, TokenType.FI, TokenType.EOF};

}
