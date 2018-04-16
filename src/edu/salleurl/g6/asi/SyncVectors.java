package edu.salleurl.g6.asi;

import edu.salleurl.g6.model.Token;
import edu.salleurl.g6.model.TokenType;

public interface SyncVectors {
    TokenType[] firstExp = {TokenType.SIMPLE_ARITHMETIC_OPERATOR, TokenType.NOT, TokenType.INTEGER_CONSTANT, 
            TokenType.LOGIC_CONSTANT, TokenType.STRING, TokenType.IDENTIFIER, TokenType.PARENTHESIS_OPEN};

    //--> 1 [EOF]
    TokenType[] inici = {TokenType.FI, TokenType.EOF};

    //--> 1 [EOF]
    TokenType[] fi = {TokenType.EOF};

    //--> 7-8 [= FIRST(exp) ; ] [CONST TIPUS FIRST(dec_func) INICI EOF]
    TokenType[] decVarLeftSide = {TokenType.ASSIGNMENT, TokenType.STATEMENT_SEPARATOR, TokenType.SIMPLE_ARITHMETIC_OPERATOR,
            TokenType.NOT,TokenType.INTEGER_CONSTANT,TokenType.LOGIC_CONSTANT, TokenType.STRING, TokenType.IDENTIFIER,
            TokenType.PARENTHESIS_OPEN, TokenType.FUNCIO, TokenType.INICI, TokenType.EOF, TokenType.FI};
    TokenType[] decVarRightSide = {TokenType.STATEMENT_SEPARATOR, TokenType.SIMPLE_ARITHMETIC_OPERATOR, TokenType.NOT,
            TokenType.INTEGER_CONSTANT,TokenType.LOGIC_CONSTANT, TokenType.STRING, TokenType.IDENTIFIER, TokenType.PARENTHESIS_OPEN,
            TokenType.FUNCIO, TokenType.INICI, TokenType.EOF, TokenType.FI};
    TokenType[] decVarTerminating = {TokenType.SIMPLE_ARITHMETIC_OPERATOR, TokenType.NOT, TokenType.INTEGER_CONSTANT,
            TokenType.LOGIC_CONSTANT, TokenType.STRING, TokenType.IDENTIFIER, TokenType.PARENTHESIS_OPEN, TokenType.FUNCIO,
            TokenType.INICI, TokenType.EOF, TokenType.FI};
    
    
    //--> 9 [( ) : TIPUS_SIMPLE{ };] [FUNCIO INICI EOF]
    TokenType[] decFunc_id = {TokenType.PARENTHESIS_OPEN, TokenType.SIMPLE_TYPE, TokenType.VECTOR,
            TokenType.PARENTHESIS_CLOSE, TokenType.RETURN_TYPE_PREFIX, TokenType.SIMPLE_TYPE,
            TokenType.BRACKETS_OPEN,TokenType.BRACKETS_CLOSE,TokenType.STATEMENT_SEPARATOR, TokenType.FUNCIO, 
            TokenType.INICI, TokenType.EOF, TokenType.FI};
    TokenType[] decFunc_parenthesis_open = {TokenType.SIMPLE_TYPE, TokenType.VECTOR,
            TokenType.PARENTHESIS_CLOSE, TokenType.RETURN_TYPE_PREFIX, TokenType.SIMPLE_TYPE,
            TokenType.BRACKETS_OPEN,TokenType.BRACKETS_CLOSE,TokenType.STATEMENT_SEPARATOR, TokenType.FUNCIO,
            TokenType.INICI, TokenType.EOF, TokenType.FI};
    TokenType[] decFunc_params = {TokenType.ARGUMENT_SEPARATOR, TokenType.VECTOR, TokenType.SIMPLE_TYPE,
            TokenType.AMPERSAND, TokenType.IDENTIFIER, TokenType.PARENTHESIS_CLOSE, TokenType.RETURN_TYPE_PREFIX,
            TokenType.SIMPLE_TYPE, TokenType.BRACKETS_OPEN,TokenType.BRACKETS_CLOSE,TokenType.STATEMENT_SEPARATOR,
            TokenType.FUNCIO, TokenType.INICI, TokenType.EOF, TokenType.FI};
    TokenType[] decFunc_parenthesis_close = {TokenType.RETURN_TYPE_PREFIX, TokenType.SIMPLE_TYPE,
            TokenType.BRACKETS_OPEN,TokenType.BRACKETS_CLOSE,TokenType.STATEMENT_SEPARATOR, TokenType.FUNCIO,
            TokenType.INICI, TokenType.EOF, TokenType.FI};
    TokenType[] decFunc_ret_pref = {TokenType.SIMPLE_TYPE, TokenType.BRACKETS_OPEN,TokenType.BRACKETS_CLOSE,
            TokenType.STATEMENT_SEPARATOR, TokenType.FUNCIO, TokenType.INICI, TokenType.EOF, TokenType.FI};
    TokenType[] decFunc_ret_type = {TokenType.BRACKETS_OPEN,TokenType.BRACKETS_CLOSE,
            TokenType.STATEMENT_SEPARATOR, TokenType.FUNCIO, TokenType.INICI, TokenType.EOF, TokenType.FI};
    TokenType[] decFunc_open_brackets = {TokenType.BRACKETS_CLOSE, TokenType.STATEMENT_SEPARATOR, TokenType.FUNCIO,
            TokenType.INICI, TokenType.EOF, TokenType.FI};
    TokenType[] decFunc_close_brackets = {TokenType.STATEMENT_SEPARATOR, TokenType.FUNCIO, TokenType.INICI, TokenType.EOF, TokenType.FI};
    TokenType[] decFunc = {TokenType.FUNCIO, TokenType.INICI, TokenType.EOF, TokenType.FI};

    //--> 17 [CTE_ENTERA ] DE TIPUS_SIMPLE] [ EOF ]
    TokenType[] vector_brackets_open = {TokenType.INTEGER_CONSTANT, TokenType.BRACKETS_CLOSE, TokenType.DE, TokenType.SIMPLE_TYPE,
            TokenType.STATEMENT_SEPARATOR, TokenType.PARENTHESIS_CLOSE, TokenType.EOF, TokenType.FI};
    TokenType[] vector_subscript = {TokenType.BRACKETS_CLOSE, TokenType.DE, TokenType.SIMPLE_TYPE,
            TokenType.STATEMENT_SEPARATOR, TokenType.PARENTHESIS_CLOSE, TokenType.EOF, TokenType.FI};
    TokenType[] vector_brackets_close = {TokenType.DE, TokenType.SIMPLE_TYPE,
            TokenType.STATEMENT_SEPARATOR, TokenType.EOF, TokenType.FI};
    TokenType[] tipus = {TokenType.STATEMENT_SEPARATOR, TokenType.PARENTHESIS_CLOSE, TokenType.EOF, TokenType.FI};
    TokenType[] tipus_prefix = {TokenType.SIMPLE_TYPE, TokenType.STATEMENT_SEPARATOR, TokenType.PARENTHESIS_CLOSE, TokenType.EOF, TokenType.FI};

    //--> 21 [FIRST(llista_termes)] [EOF]| FIRST(llista_termes)={SAO OR CTE_ENTERA CTE_LOGICA CTE_CADENA ID (}
    TokenType[] exp = {TokenType.SIMPLE_ARITHMETIC_OPERATOR, TokenType.NOT, TokenType.INTEGER_CONSTANT,
            TokenType.LOGIC_CONSTANT, TokenType.STRING, TokenType.IDENTIFIER, TokenType.PARENTHESIS_OPEN,
            TokenType.EOF, TokenType.FI};

    //--> 25 [FIRST(llista_termes)] [EOF]| FIRST(llista_termes)={SAO OR CTE_ENTERA CTE_LOGICA CTE_CADENA ID (}
    TokenType[] llista_termes = {TokenType.SIMPLE_ARITHMETIC_OPERATOR, TokenType.OR, TokenType.INTEGER_CONSTANT, 
            TokenType.LOGIC_CONSTANT, TokenType.STRING, TokenType.EOF, TokenType.FI};

    //--> 38,39 [)] [EOF + follows(factor)]
    TokenType[] factor = {TokenType.COMPLEX_ARITHMETIC_OPERATOR, TokenType.AND,
            TokenType.SIMPLE_ARITHMETIC_OPERATOR, TokenType.OR, TokenType.RELATIONAL_OPERATOR,
            TokenType.STATEMENT_SEPARATOR, TokenType.PARENTHESIS_CLOSE, TokenType.SQUARE_BRACKETS_CLOSE,
            TokenType.ARGUMENT_SEPARATOR, TokenType.FER, TokenType.LLAVORS, TokenType.EOF, TokenType.FI};

    //--> 31 first(terme)=first(factor) + follows(factor)
    TokenType[] op = {TokenType.INTEGER_CONSTANT, TokenType.LOGIC_CONSTANT, TokenType.STRING, TokenType.IDENTIFIER,
            TokenType.PARENTHESIS_OPEN, TokenType.COMPLEX_ARITHMETIC_OPERATOR, TokenType.AND,
            TokenType.SIMPLE_ARITHMETIC_OPERATOR, TokenType.OR, TokenType.RELATIONAL_OPERATOR,
            TokenType.STATEMENT_SEPARATOR, TokenType.PARENTHESIS_CLOSE, TokenType.SQUARE_BRACKETS_CLOSE,
            TokenType.ARGUMENT_SEPARATOR, TokenType.FER, TokenType.LLAVORS, TokenType.EOF, TokenType.FI};

    //--> 42 [ ] ] [EOF]
    TokenType[] is_vector= {TokenType.ASSIGNMENT, TokenType.COMPLEX_ARITHMETIC_OPERATOR, TokenType.AND,
            TokenType.SIMPLE_ARITHMETIC_OPERATOR, TokenType.OR, TokenType.RELATIONAL_OPERATOR,
            TokenType.STATEMENT_SEPARATOR, TokenType.PARENTHESIS_CLOSE, TokenType.SQUARE_BRACKETS_CLOSE,
            TokenType.ARGUMENT_SEPARATOR, TokenType.FER, TokenType.LLAVORS, TokenType.EOF, TokenType.FI};

    //--> 49 [FIRST(llista_inst_aux)] [ ; EOF] | FIRST(llista_inst_aux)={ ID ESCRIURE LLEGIR REPETIR MENTRE SI RETORNAR}
    TokenType[] llista_inst = {TokenType.IDENTIFIER, TokenType.ESCRIURE, TokenType.LLEGIR, TokenType.REPETIR,
            TokenType.MENTRE, TokenType.SINO, TokenType.FISI, TokenType.FIMENTRE, TokenType.FINS,
            TokenType.BRACKETS_CLOSE, TokenType.SI, TokenType.RETORNAR, TokenType.STATEMENT_SEPARATOR, TokenType.EOF,
            TokenType.FI};

    //--> 52 [FIRST(EXP)] [ ; } EOF] | FIRST(exp)={ DEFINITS PREVIAMENT }
    TokenType[] inst_id = {TokenType.SIMPLE_ARITHMETIC_OPERATOR, TokenType.NOT, TokenType.INTEGER_CONSTANT,
            TokenType.LOGIC_CONSTANT, TokenType.STRING, TokenType.IDENTIFIER, TokenType.ESCRIURE, TokenType.LLEGIR, TokenType.REPETIR,
            TokenType.MENTRE, TokenType.SI, TokenType.RETORNAR, TokenType.PARENTHESIS_OPEN, TokenType.STATEMENT_SEPARATOR,
            TokenType.BRACKETS_CLOSE, TokenType.EOF, TokenType.FI};

    //--> 53 [FIRST(llista_exp_non_empty) ) ] [ ;  EOF] | FIRST(llista_exp_non_empty)=FIRST(exp)={ DEFINITS PREVIAMENT }
    TokenType[] inst_escriure_par_open = {TokenType.SIMPLE_ARITHMETIC_OPERATOR, TokenType.NOT, TokenType.INTEGER_CONSTANT,
            TokenType.LOGIC_CONSTANT, TokenType.STRING, TokenType.IDENTIFIER, TokenType.ESCRIURE, TokenType.LLEGIR, TokenType.REPETIR,
            TokenType.MENTRE, TokenType.SI, TokenType.RETORNAR, TokenType.PARENTHESIS_OPEN, TokenType.PARENTHESIS_CLOSE,
            TokenType.STATEMENT_SEPARATOR, TokenType.ARGUMENT_SEPARATOR, TokenType.EOF, TokenType.FI};
    TokenType[] inst_escriure = {TokenType.ESCRIURE, TokenType.LLEGIR, TokenType.REPETIR,
            TokenType.MENTRE, TokenType.SI, TokenType.RETORNAR,
            TokenType.STATEMENT_SEPARATOR, TokenType.EOF, TokenType.FI};

    //--> 54 [FIRST(llista_variable) ) ] [ ;  EOF] | FIRST(llista_variable)={ ID , }
    TokenType[] inst_llegir_par_open = {TokenType.IDENTIFIER, TokenType.ESCRIURE, TokenType.LLEGIR, TokenType.REPETIR,
            TokenType.MENTRE, TokenType.SI, TokenType.RETORNAR, TokenType.ARGUMENT_SEPARATOR, TokenType.PARENTHESIS_CLOSE,
            TokenType.STATEMENT_SEPARATOR, TokenType.EOF, TokenType.FI};
    TokenType[] inst_llegir = {TokenType.ESCRIURE, TokenType.LLEGIR, TokenType.REPETIR,
            TokenType.MENTRE, TokenType.SI, TokenType.RETORNAR,
            TokenType.STATEMENT_SEPARATOR, TokenType.EOF, TokenType.FI};

    //--> 58 [FIRST(exp) ] [ ;  EOF] |
    TokenType[] inst_repetir = {TokenType.IDENTIFIER, TokenType.ESCRIURE, TokenType.LLEGIR, TokenType.REPETIR,
            TokenType.MENTRE, TokenType.SI, TokenType.RETORNAR, TokenType.SIMPLE_ARITHMETIC_OPERATOR, TokenType.NOT, TokenType.INTEGER_CONSTANT,
            TokenType.LOGIC_CONSTANT, TokenType.STRING, TokenType.PARENTHESIS_OPEN,
            TokenType.STATEMENT_SEPARATOR, TokenType.EOF, TokenType.FI};

    //--> 59 [FIRST(llista_inst) fimentre first(exp)] [ ;  EOF] |
    TokenType[] inst_mentre = {TokenType.IDENTIFIER, TokenType.ESCRIURE, TokenType.LLEGIR, TokenType.REPETIR,
            TokenType.MENTRE, TokenType.SI, TokenType.RETORNAR, TokenType.FER, TokenType.SIMPLE_ARITHMETIC_OPERATOR, TokenType.NOT, TokenType.INTEGER_CONSTANT,
            TokenType.LOGIC_CONSTANT, TokenType.STRING, TokenType.IDENTIFIER, TokenType.PARENTHESIS_OPEN, TokenType.FIMENTRE, TokenType.STATEMENT_SEPARATOR,
            TokenType.EOF, TokenType.FI};
    TokenType[] inst_fimentre = {TokenType.IDENTIFIER, TokenType.ESCRIURE, TokenType.LLEGIR, TokenType.REPETIR,
            TokenType.MENTRE, TokenType.SI, TokenType.RETORNAR, TokenType.STATEMENT_SEPARATOR,
            TokenType.EOF, TokenType.FI};
    
    //--> 60 [ FIRST(llista_inst & has_sino) fisi] [; EOF]
    TokenType[] inst_si = {TokenType.IDENTIFIER, TokenType.ESCRIURE, TokenType.LLEGIR, TokenType.REPETIR,
            TokenType.MENTRE, TokenType.SI, TokenType.RETORNAR, TokenType.FISI, TokenType.SINO,
            TokenType.STATEMENT_SEPARATOR, TokenType.EOF, TokenType.FI};
    TokenType[] inst_fisi = {TokenType.IDENTIFIER, TokenType.ESCRIURE, TokenType.LLEGIR, TokenType.REPETIR,
            TokenType.MENTRE, TokenType.SI, TokenType.RETORNAR, TokenType.STATEMENT_SEPARATOR, TokenType.EOF, TokenType.FI};

}
