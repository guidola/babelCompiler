package edu.salleurl.g6.asi;

import edu.salleurl.g6.model.Token;
import edu.salleurl.g6.model.TokenType;

public interface SyncVectors {
    TokenType[] firstExp = {TokenType.SIMPLE_ARITHMETIC_OPERATOR,TokenType.NOT,TokenType.INTEGER_CONSTANT,TokenType.LOGIC_CONSTANT,TokenType.IDENTIFIER, TokenType.PARENTHESIS_OPEN};
    //--> 7-8 [= FIRST(exp) ; ] [CONST TIPUS FIRST(dec_func) INICI EOF]
    TokenType[] decVarId = {TokenType.ASSIGNMENT, TokenType.STATEMENT_SEPARATOR,TokenType.SIMPLE_ARITHMETIC_OPERATOR,TokenType.NOT,TokenType.INTEGER_CONSTANT,TokenType.LOGIC_CONSTANT,
            TokenType.IDENTIFIER, TokenType.PARENTHESIS_OPEN, TokenType.FUNCIO, TokenType.INICI, TokenType.EOF};
    //--> 9 [( ) : TIPUS_SIMPLE{ };] [FUNCIO INICI EOF]
    TokenType[] decFunc= {TokenType.PARENTHESIS_OPEN, TokenType.PARENTHESIS_CLOSE,TokenType.SIMPLE_TYPE,TokenType.BRACKETS_OPEN,TokenType.BRACKETS_CLOSE,TokenType.STATEMENT_SEPARATOR,
            TokenType.FUNCIO, TokenType.INICI, TokenType.EOF};

    //--> 11 [FIRST(llista_param_aux)] [EOF] | FIRST(llista_param_aux)={, vector, tipus_simple, amps, id}
    TokenType[] llistaParam = {TokenType.ARGUMENT_SEPARATOR, TokenType.VECTOR, TokenType.SIMPLE_TYPE,TokenType.AMPERSAND,TokenType.IDENTIFIER, TokenType.EOF};

    //--> 17 [CTE_ENTERA ] DE TIPUS_SIMPLE] [ EOF ]
    TokenType[] tipus = {TokenType.INTEGER_CONSTANT, TokenType.BRACKETS_CLOSE, TokenType.DE, TokenType.SIMPLE_TYPE, TokenType.EOF};

    //--> 21 [FIRST(llista_termes)] [EOF]| FIRST(llista_termes)={SAO OR CTE_ENTERA CTE_LOGICA CTE_CADENA ID (}
    TokenType[] exp_simple = {TokenType.SIMPLE_ARITHMETIC_OPERATOR, TokenType.OR, TokenType.INTEGER_CONSTANT, TokenType.LOGIC_CONSTANT, TokenType.STRING,TokenType.EOF};

    //--> 25 [FIRST(llista_termes)] [EOF]| FIRST(llista_termes)={SAO OR CTE_ENTERA CTE_LOGICA CTE_CADENA ID (}
    TokenType[] llista_termes = {TokenType.SIMPLE_ARITHMETIC_OPERATOR, TokenType.OR, TokenType.INTEGER_CONSTANT, TokenType.LOGIC_CONSTANT, TokenType.STRING,TokenType.EOF};

    //--> 38 [)] [EOF]
    TokenType[] factor = {TokenType.PARENTHESIS_OPEN, TokenType.EOF};

    //--> 39 [)] [EOF]
    TokenType[] factor_id_sufix = {TokenType.PARENTHESIS_OPEN, TokenType.EOF};

    //--> 42 [ ] ] [EOF]
    TokenType[] is_vector= {TokenType.SQUARE_BRACKETS_CLOSE, TokenType.EOF};

    //--> 49 [FIRST(llista_inst_aux)] [ ; EOF] | FIRST(llista_inst_aux)={ ID ESCRIURE LLEGIR REPETIR MENTRE SI RETORNAR}
    TokenType[] llista_inst_aux = {TokenType.IDENTIFIER, TokenType.ESCRIURE, TokenType.LLEGIR, TokenType.REPETIR,
            TokenType.MENTRE, TokenType.SI, TokenType.RETORNAR, TokenType.STATEMENT_SEPARATOR,TokenType.EOF};

    //--> 52 [FIRST(EXP)] [ ; } EOF] | FIRST(exp)={ DEFINITS PREVIAMENT }
    TokenType[] inst_op_rel = {TokenType.SIMPLE_ARITHMETIC_OPERATOR,TokenType.NOT,TokenType.INTEGER_CONSTANT,TokenType.LOGIC_CONSTANT,TokenType.IDENTIFIER, TokenType.PARENTHESIS_OPEN,
            TokenType.STATEMENT_SEPARATOR, TokenType.BRACKETS_CLOSE, TokenType.EOF};

    //--> 53 [FIRST(llista_exp_non_empty) ) ] [ ;  EOF] | FIRST(llista_exp_non_empty)=FIRST(exp)={ DEFINITS PREVIAMENT }
    TokenType[] inst_escriure = {TokenType.SIMPLE_ARITHMETIC_OPERATOR,TokenType.NOT,TokenType.INTEGER_CONSTANT,TokenType.LOGIC_CONSTANT,TokenType.IDENTIFIER, TokenType.PARENTHESIS_OPEN, TokenType.PARENTHESIS_CLOSE,
            TokenType.STATEMENT_SEPARATOR, TokenType.EOF};

    //--> 54 [FIRST(llista_variable) ) ] [ ;  EOF] | FIRST(llista_variable)={ ID , }
    TokenType[] inst_llegir = {TokenType.IDENTIFIER, TokenType.ARGUMENT_SEPARATOR, TokenType.PARENTHESIS_CLOSE,
            TokenType.STATEMENT_SEPARATOR, TokenType.EOF};

    //--> 58 [FIRST(exp) ] [ ;  EOF] |
    TokenType[] inst_repetir = {TokenType.SIMPLE_ARITHMETIC_OPERATOR,TokenType.NOT,TokenType.INTEGER_CONSTANT,TokenType.LOGIC_CONSTANT,TokenType.IDENTIFIER, TokenType.PARENTHESIS_OPEN,
            TokenType.STATEMENT_SEPARATOR, TokenType.EOF};

    //--> 59 [FIRST(llista_inst) fimentre ] [ ;  EOF] |
    TokenType[] inst_mentre = {TokenType.IDENTIFIER, TokenType.ESCRIURE, TokenType.LLEGIR, TokenType.REPETIR,
            TokenType.MENTRE, TokenType.SI, TokenType.RETORNAR, TokenType.FIMENTRE, TokenType.STATEMENT_SEPARATOR,TokenType.EOF};
    //--> 60 [ FIRST(llista_inst & has_sino) fisi] [; EOF]
    TokenType[] inst_si = {TokenType.IDENTIFIER, TokenType.ESCRIURE, TokenType.LLEGIR, TokenType.REPETIR,
            TokenType.MENTRE, TokenType.SI, TokenType.RETORNAR, TokenType.FIMENTRE, TokenType.SINO, TokenType.STATEMENT_SEPARATOR,TokenType.EOF};

}
