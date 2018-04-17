package edu.salleurl.g6.asi;

import com.sun.corba.se.impl.orbutil.concurrent.Sync;
import edu.salleurl.g6.alex.Alex;
import edu.salleurl.g6.model.*;


public class Asi {

    private Token lat;

    public Asi(String filename) {

        Alex.init(filename);
        lat = Alex.getToken();
    }


    private void consume(TokenType t) throws SyntacticException {
        if(t == lat.getType()){
            lat = Alex.getToken();
            return;
        }

        throw(ExceptionFactory.consume(t, lat.getType()));
    }

    private void consumeUntilSync(TokenType[] ts) {

        while(true) {
            for(TokenType t : ts) {

                if (t == lat.getType()) {
                    return;
                }


            }
            lat = Alex.getToken();
        }

    }

    private void waitForTokenToBeReady(TokenType tt) {
        while(true) {
            if (tt == lat.getType() || TokenType.EOF == lat.getType()) {
                return;
            }
            lat = Alex.getToken();
        }
    }

    private boolean waitForTokenToBeReadyOrSync(TokenType tt, TokenType[] ts) {
        boolean iterated = false;
        while(true) {

            if (tt == lat.getType() || TokenType.EOF == lat.getType()) {
                return iterated;
            }
            for(TokenType t : ts) {

                if (t == lat.getType()) {
                    return iterated;
                }
            }
            lat = Alex.getToken();
            iterated = true;
        }
    }



    private void log(String text) {
        System.err.println("[ERROR] line " + Alex.getLine() + ". " + text);
        Alex.getLog().writeError("[ERROR_SIN] line " + Alex.getLine() + ", " + text);
    }

    public void programa() throws SyntacticException {
        llistaDecVar();
        llistaDecFunc();

        waitForTokenToBeReadyOrSync(TokenType.INICI, SyncVectors.inici);
        try {
            consume(TokenType.INICI);
        } catch(SyntacticException se) {
            log("Can't find main routine beginning (INICI)");
            consumeUntilSync(SyncVectors.inici);
        }

        llistaInst();

        try {
            consume(TokenType.FI);
        } catch(SyntacticException se) {
            log("Main routine does not end. Expected FI.");
            consumeUntilSync(SyncVectors.fi);
        }

        try {
            consume(TokenType.EOF);
        } catch(SyntacticException se) {
            log("Instructions not allowed after program body. There shall not be any code after FI found code " +
                    "segment beginning with " + lat.getLexem());
            consumeUntilSync(SyncVectors.eof);
        }


    }

    private void llistaDecVar() throws SyntacticException {
        switch (lat.getType()){
            case CONST:
            case SIMPLE_TYPE:
            case VECTOR:
                decVar();
                llistaDecVar();
                break;
            default:
                break;
        }
    }

    private void decVar() throws SyntacticException {
        switch (lat.getType()){
            case CONST:
                consume(TokenType.CONST);
                try {
                    consume(TokenType.IDENTIFIER);
                } catch (SyntacticException se) {
                    log("Missing identifier in left side of constant declaration");
                    consumeUntilSync(SyncVectors.decVarLeftSide);
                }
                try {
                    consume(TokenType.ASSIGNMENT);
                } catch(SyntacticException se) {
                    log("Missing assignment in constant declaration");
                    consumeUntilSync(SyncVectors.decVarRightSide);
                }
                exp();
                try {
                    consume(TokenType.STATEMENT_SEPARATOR);
                } catch(SyntacticException se) {
                    log("Unterminated statement in constant declaration");
                    consumeUntilSync(SyncVectors.decVarTerminating);
                }
                break;
            case SIMPLE_TYPE:
            case VECTOR:
                tipus();
                try {
                    consume(TokenType.IDENTIFIER);
                } catch (SyntacticException se) {
                    log("Missing variable name in variable declaration");
                    consumeUntilSync(SyncVectors.decVarRightSide);
                }
                try {
                    consume(TokenType.STATEMENT_SEPARATOR);
                } catch(SyntacticException se) {
                    log("Unterminated statement in variable declaration");
                    consumeUntilSync(SyncVectors.decVarTerminating);
                }
                break;
            default:
                //instead of throwing an exception we will automatically sync against follows of possible usages
                log("Invalid statement in declaration");
                consumeUntilSync(SyncVectors.decVarTerminating);
                //throw(ExceptionFactory.decVar(lat.getType()));
        }
    }

    private void exp() throws SyntacticException {
        expSimple();
        llistaExpSimple();
    }

    private void expSimple() throws SyntacticException {
        opu();
        terme();
        llistaTermes();
    }

    private void opu() throws SyntacticException {
        switch (lat.getType()){
            case SIMPLE_ARITHMETIC_OPERATOR:
                consume(TokenType.SIMPLE_ARITHMETIC_OPERATOR);
                break;
            case NOT:
                consume(TokenType.NOT);
            default:
                break;
        }
    }

    private void terme() throws SyntacticException {
        factor();
        factorAux();
    }
    private void factor() throws SyntacticException {
        switch (lat.getType()){
            case INTEGER_CONSTANT:
                consume(TokenType.INTEGER_CONSTANT);
                break;
            case LOGIC_CONSTANT:
                consume(TokenType.LOGIC_CONSTANT);
                break;
            case STRING:
                consume(TokenType.STRING);
                break;
            case IDENTIFIER:
                consume(TokenType.IDENTIFIER);
                factorIdSufix();
                break;
            case PARENTHESIS_OPEN:
                consume(TokenType.PARENTHESIS_OPEN);
                exp();
                try {
                    consume(TokenType.PARENTHESIS_CLOSE);
                } catch(SyntacticException se) {
                    log("Unbalanced parenthesis in expression");
                    consumeUntilSync(SyncVectors.factor);
                }
                break;
            default:
                log("Can't use " + lat.getLexem() + " as operand in expression");
                consumeUntilSync(SyncVectors.factor);
                //throw(ExceptionFactory.factor(lat.getType()));
        }
    }

    private void factorIdSufix() throws SyntacticException {
        switch (lat.getType()){
            case PARENTHESIS_OPEN:
                consume(TokenType.PARENTHESIS_OPEN);
                llistaExp();
                try {
                    consume(TokenType.PARENTHESIS_CLOSE);
                } catch(SyntacticException se) {
                    log("Missing closing parenthesis in function call");
                    consumeUntilSync(SyncVectors.factor);
                }
                break;
            default:
                isVector();
        }
    }

    private void isVector() throws SyntacticException {
        switch (lat.getType()){
            case SQUARE_BRACKETS_OPEN:
                consume(TokenType.SQUARE_BRACKETS_OPEN);
                if (lat.getType() != TokenType.SQUARE_BRACKETS_CLOSE) {
                    exp();
                } else {
                    log("No expression provided as vector index");
                }
                try {
                    consume(TokenType.SQUARE_BRACKETS_CLOSE);
                } catch(SyntacticException se) {
                    log("Missing closing bracket in subscript");
                    consumeUntilSync(SyncVectors.is_vector);
                }
                break;
            default:
                break;
        }
    }

    private void llistaExp() throws SyntacticException {
        switch (lat.getType()){
            case SIMPLE_ARITHMETIC_OPERATOR:
            case NOT:
            case INTEGER_CONSTANT:
            case LOGIC_CONSTANT:
            case STRING:
            case IDENTIFIER:
            case PARENTHESIS_OPEN:
                llistaExpNonEmpty();
                break;
            default:
                break;
        }
    }

    private void llistaExpNonEmpty() throws SyntacticException {
        exp();
        llistaExpAux();
    }

    private void llistaExpAux() throws SyntacticException {
        switch (lat.getType()){
            case ARGUMENT_SEPARATOR:
                consume(TokenType.ARGUMENT_SEPARATOR);
                llistaExpNonEmpty();
                break;
            default:
                break;
        }
    }

    private void factorAux() throws SyntacticException {
        switch(lat.getType()){
            case COMPLEX_ARITHMETIC_OPERATOR:
            case AND:
                opb();
                terme();
                break;
            default:
                break;
        }
    }

    private void opb() throws SyntacticException {
        switch (lat.getType()){
            case COMPLEX_ARITHMETIC_OPERATOR:
                consume(TokenType.COMPLEX_ARITHMETIC_OPERATOR);
                break;
            case AND:
                consume(TokenType.AND);
                break;
            default:
                log("Invalid binary operator " + lat.getLexem());
                consumeUntilSync(SyncVectors.op);
                //throw(ExceptionFactory.opb(lat.getType()));
        }
    }

    private void llistaTermes() throws SyntacticException {
        switch(lat.getType()) {
            case SIMPLE_ARITHMETIC_OPERATOR:
            case OR:
                ops();
                terme();
                llistaTermes();
            default:
                break;
        }


    }
    private void ops() throws SyntacticException {
        switch (lat.getType()){
            case SIMPLE_ARITHMETIC_OPERATOR:
                consume(TokenType.SIMPLE_ARITHMETIC_OPERATOR);
                break;
            case OR:
                consume(TokenType.OR);
                break;
            default:
                log("Invalid simple operator " + lat.getLexem());
                consumeUntilSync(SyncVectors.op);
                //throw(ExceptionFactory.ops(lat.getType()));

        }
    }

    private void llistaExpSimple() throws SyntacticException {
        switch(lat.getType()){
            case RELATIONAL_OPERATOR:
                consume(TokenType.RELATIONAL_OPERATOR);
                expSimple();
                break;
            default:
                break;
        }
    }

    private void tipus() throws SyntacticException {
        switch(lat.getType()){
            case SIMPLE_TYPE:
                consume(TokenType.SIMPLE_TYPE);
                break;
            case VECTOR:
                consume(TokenType.VECTOR);
                try {
                    consume(TokenType.SQUARE_BRACKETS_OPEN);
                } catch(SyntacticException se) {
                    log("Unbalanced brackets in vector declaration");
                    consumeUntilSync(SyncVectors.vector_brackets_open);
                }
                try {
                    consume(TokenType.INTEGER_CONSTANT);
                } catch(SyntacticException se) {
                    log("Non integer '" + lat.getLexem() + "' provided as vector subscript");
                    consumeUntilSync(SyncVectors.vector_subscript);
                }
                try {
                    consume(TokenType.SQUARE_BRACKETS_CLOSE);
                } catch(SyntacticException se) {
                    log("Unbalanced brackets in vector declaration");
                    consumeUntilSync(SyncVectors.vector_brackets_close);
                }

                try {
                    consume(TokenType.DE);
                } catch(SyntacticException se) {
                    log("Missing 'de' prefix before type in vector declaration");
                    consumeUntilSync(SyncVectors.tipus_prefix);
                }
                try {
                    consume(TokenType.SIMPLE_TYPE);
                } catch(SyntacticException se) {
                    log("Invalid type for vector");
                    consumeUntilSync(SyncVectors.tipus);
                }
                break;
            default:
                log("Invalid type " + lat.getLexem());
                consumeUntilSync(SyncVectors.tipus);
                //throw(ExceptionFactory.tipus(lat.getType()));
        }
    }

    private void llistaDecFunc() throws SyntacticException {
        switch (lat.getType()){
            case IDENTIFIER:
                //probably is a function declaration badly typed if it is not inici
            case FUNCIO:
                decFunc();
                llistaDecFunc();
                break;
            default:
                    break;
        }
    }

    private void decFunc() throws SyntacticException {
        try {
            consume(TokenType.FUNCIO);
        } catch(SyntacticException se) {
            log("FUNCIO keyword may be missing or misspelled on function declaration");
            consumeUntilSync(SyncVectors.decFuncFUNCIO);
        }
        if (!(lat.getType() == TokenType.IDENTIFIER || lat.getType() == TokenType.PARENTHESIS_OPEN ||
                lat.getType() == TokenType.BRACKETS_OPEN)) {
            return;
        }
        try {
            consume(TokenType.IDENTIFIER);
        } catch(SyntacticException se) {
            log("Missing name in function declaration");
            consumeUntilSync(SyncVectors.decFunc_id);
        }
        if (lat.getType() == TokenType.IDENTIFIER){
            // funcio was badly typed so the funcioo was taken as id
            // hence we gotta skip this id since it would be the real one
            lat = Alex.getToken();
        }
        try {
            consume(TokenType.PARENTHESIS_OPEN);
        } catch(SyntacticException se) {
            log("Unbalanced parenthesis in function declaration");
            consumeUntilSync(SyncVectors.decFunc_parenthesis_open);
        }
        llistaParam();
        try {
            consume(TokenType.PARENTHESIS_CLOSE);
        } catch(SyntacticException se) {
            log("Unbalanced parenthesis in function declaration");
            consumeUntilSync(SyncVectors.decFunc_parenthesis_close);
        }
        try {
            consume(TokenType.RETURN_TYPE_PREFIX);
        } catch(SyntacticException se) {
            log("Missing ':' before function return type");
            consumeUntilSync(SyncVectors.decFunc_ret_pref);
        }
        try {
            consume(TokenType.SIMPLE_TYPE);
        } catch(SyntacticException se) {
            log("Unrecognised return type in function declaration");
            consumeUntilSync(SyncVectors.decFunc_ret_type);
        }
        try {
            consume(TokenType.BRACKETS_OPEN);
        } catch(SyntacticException se) {
            log("Unbalanced brackets around function body");
            consumeUntilSync(SyncVectors.decFunc_open_brackets);
        }
        llistaDecVar();
        llistaInst();
        try {
            consume(TokenType.BRACKETS_CLOSE);
        } catch(SyntacticException se) {
            log("Unbalanced brackets around function body");
            consumeUntilSync(SyncVectors.decFunc_close_brackets);
        }
        try {
            consume(TokenType.STATEMENT_SEPARATOR);
        } catch(SyntacticException se) {
            log("Missing ';' at the end of function declaration");
            consumeUntilSync(SyncVectors.decFunc);
        }

    }

    private void llistaParam() throws SyntacticException {
        switch(lat.getType()){
            case SIMPLE_TYPE:
            case VECTOR:
                tipus();
                isRef();
                try {
                    consume(TokenType.IDENTIFIER);
                } catch(SyntacticException se) {
                    log("Missing identifier for function parameter");
                    consumeUntilSync(SyncVectors.decFunc_params);
                }
                llistaParamAux();
                break;
            default:
                break;
        }
    }

    private void isRef() throws SyntacticException {
        switch (lat.getType()){
            case AMPERSAND:
                consume(TokenType.AMPERSAND);
                break;
            default:
                break;
        }
    }

    private void llistaParamAux() throws SyntacticException {
        switch (lat.getType()){
            case ARGUMENT_SEPARATOR:
                consume(TokenType.ARGUMENT_SEPARATOR);
                llistaParam();
                break;
            default:
                break;
        }
    }

    private void llistaInst() throws SyntacticException {

        switch(lat.getType()) {
            case REPETIR:
            case MENTRE:
            case SI:
            case IDENTIFIER:
            case ESCRIURE:
            case LLEGIR:
            case RETORNAR:
                inst();
                try {
                    consume(TokenType.STATEMENT_SEPARATOR);
                } catch(SyntacticException se) {
                    log("Missing ; at the end of instruction");
                    consumeUntilSync(SyncVectors.llista_inst);
                }
                llistaInstAux();
                break;
            default:
                log("Body cannot be empty. There must be at least one instruction in the scope");
        }


    }

    private void inst() throws SyntacticException {
        switch(lat.getType()) {
            case REPETIR:
                consume(TokenType.REPETIR);
                llistaInst();
                if(waitForTokenToBeReadyOrSync(TokenType.FINS, SyncVectors.inst_repetir)) {
                    log("Invalid instruction before FINS");
                }
                try {
                    consume(TokenType.FINS);
                } catch(SyntacticException se) {
                    log("Missing FINS keyword in REPETIR loop");
                    consumeUntilSync(SyncVectors.inst_repetir);
                }
                exp();
                break;
            case MENTRE:
                consume(TokenType.MENTRE);
                exp();
                if(waitForTokenToBeReadyOrSync(TokenType.FER, SyncVectors.inst_mentre_wait)){
                    log("Invalid expression before FER");
                }
                try {
                    consume(TokenType.FER);
                } catch(SyntacticException se) {
                    log("Missing FER keyword in MENTRE loop");
                    consumeUntilSync(SyncVectors.inst_mentre);
                }
                llistaInst();
                try {
                    consume(TokenType.FIMENTRE);
                } catch(SyntacticException se) {
                    log("Missing FIMENTRE keyword in MENTRE loop");
                    consumeUntilSync(SyncVectors.inst_fimentre);
                }
                break;
            case SI:
                consume(TokenType.SI);
                exp();
                if(waitForTokenToBeReadyOrSync(TokenType.LLAVORS, SyncVectors.inst_si_wait)){
                    log("Invalid expression before LLAVORS");
                }
                try {
                    consume(TokenType.LLAVORS);
                } catch(SyntacticException se) {
                    log("Missing LLAVORS preceding conditional clause body");
                    consumeUntilSync(SyncVectors.inst_si);
                }
                llistaInst();
                if(waitForTokenToBeReadyOrSync(TokenType.SINO, SyncVectors.inst_sino)){
                    log("Invalid instruction before SINO");
                }
                hasSino();
                try {
                    consume(TokenType.FISI);
                } catch(SyntacticException se) {
                    log("Miising FISI keyword delimiting conditional clause");
                    consumeUntilSync(SyncVectors.inst_fisi);
                }
                break;
            case IDENTIFIER:
                variable();
                try {
                    consume(TokenType.ASSIGNMENT);
                    exp();
                } catch(SyntacticException se) {
                    log("Variable being used for non assignment instruction");
                    consumeUntilSync(SyncVectors.inst_id);
                }
                break;
            case ESCRIURE:
                consume(TokenType.ESCRIURE);
                try {
                    consume(TokenType.PARENTHESIS_OPEN);
                } catch(SyntacticException se) {
                    log("Unbalanced parenthesis in ESCRIURE");
                    consumeUntilSync(SyncVectors.inst_escriure_par_open);
                }
                llistaExpNonEmpty();
                try {
                    consume(TokenType.PARENTHESIS_CLOSE);
                } catch(SyntacticException se) {
                    log("Unbalanced parenthesis in ESCRIURE");
                    consumeUntilSync(SyncVectors.inst_escriure);
                }
                break;
            case LLEGIR:
                consume(TokenType.LLEGIR);
                try {
                    consume(TokenType.PARENTHESIS_OPEN);
                } catch(SyntacticException se) {
                    log("Unbalanced parenthesis in LLEGIR");
                    consumeUntilSync(SyncVectors.inst_llegir_par_open);
                }
                llistaVar();
                try {
                    consume(TokenType.PARENTHESIS_CLOSE);
                } catch(SyntacticException se) {
                    log("Unbalanced parenthesis in LLEGIR");
                    consumeUntilSync(SyncVectors.inst_llegir);
                }
                break;
            case RETORNAR:
                consume(TokenType.RETORNAR);
                exp();
                break;
            default:
                log("Invalid instruction");
                consumeUntilSync(SyncVectors.llista_inst);
                //throw(ExceptionFactory.inst(lat.getType()));
        }
    }

    private void llistaInstAux() throws SyntacticException {
        switch (lat.getType()) {
            case REPETIR:
            case MENTRE:
            case SI:
            case IDENTIFIER:
            case ESCRIURE:
            case LLEGIR:
            case RETORNAR:
                llistaInst();
                break;
            default:
                break;
        }
    }
    private void hasSino() throws SyntacticException {
        switch (lat.getType()){
            case SINO:
                consume(TokenType.SINO);
                llistaInst();
                break;
            default:
                break;
        }
    }

    private void variable() throws SyntacticException {
        consume(TokenType.IDENTIFIER);
        isVector();
    }

    private void llistaVar() throws SyntacticException {
        variable();
        llistaVarAux();
    }

    private void llistaVarAux() throws SyntacticException {
        switch (lat.getType()){
            case ARGUMENT_SEPARATOR:
                consume(TokenType.ARGUMENT_SEPARATOR);
                llistaVar();
                break;
            default:
                break;
        }
    }
}
