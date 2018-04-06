package edu.salleurl.g6.asi;

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
            lat = Alex.getToken();
            for(TokenType t : ts) {

                if (t == lat.getType()) {
                    return;
                }
            }
        }

    }

    public void programa() throws SyntacticException {
        llistaDecVar();
        llistaDecFunc();
        consume(TokenType.INICI);
        llistaInst();
        consume(TokenType.FI);

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
                    consumeUntilSync();
                }
                consume(TokenType.ASSIGNMENT);
                exp();
                consume(TokenType.STATEMENT_SEPARATOR);
                break;
            case SIMPLE_TYPE:
            case VECTOR:
                tipus();
                consume(TokenType.IDENTIFIER);
                consume(TokenType.STATEMENT_SEPARATOR);
                break;
            default:
                throw(ExceptionFactory.decVar(lat.getType()));
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
                consume(TokenType.PARENTHESIS_CLOSE);
                break;
            default:
                throw(ExceptionFactory.factor(lat.getType()));
        }
    }

    private void factorIdSufix() throws SyntacticException {
        switch (lat.getType()){
            case PARENTHESIS_OPEN:
                consume(TokenType.PARENTHESIS_OPEN);
                llistaExp();
                consume(TokenType.PARENTHESIS_CLOSE);
                break;
            default:
                isVector();
        }
    }

    private void isVector() throws SyntacticException {
        switch (lat.getType()){
            case BRACKETS_OPEN:
                consume(TokenType.BRACKETS_OPEN);
                exp();
                consume(TokenType.BRACKETS_CLOSE);
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
                throw(ExceptionFactory.opb(lat.getType()));
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
                throw(ExceptionFactory.ops(lat.getType()));
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
                consume(TokenType.BRACKETS_OPEN);
                consume(TokenType.INTEGER_CONSTANT);
                consume(TokenType.BRACKETS_CLOSE);
                consume(TokenType.DE);
                consume(TokenType.SIMPLE_TYPE);
                break;
            default:
                throw(ExceptionFactory.tipus(lat.getType()));
        }
    }

    private void llistaDecFunc() throws SyntacticException {
        switch (lat.getType()){
            case FUNCIO:
                decFunc();
                llistaDecFunc();
            default:
                    break;
        }
    }

    private void decFunc() throws SyntacticException {
        consume(TokenType.FUNCIO);
        consume(TokenType.IDENTIFIER);
        consume(TokenType.PARENTHESIS_OPEN);
        llistaParam();
        consume(TokenType.PARENTHESIS_CLOSE);
        consume(TokenType.RETURN_TYPE_PREFIX);
        consume(TokenType.SIMPLE_TYPE);
        consume(TokenType.BRACKETS_OPEN);
        llistaDecVar();
        llistaInst();
        consume(TokenType.BRACKETS_CLOSE);
        consume(TokenType.STATEMENT_SEPARATOR);
    }

    private void llistaParam() throws SyntacticException {
        switch(lat.getType()){
            case SIMPLE_TYPE:
            case VECTOR:
                tipus();
                isRef();
                consume(TokenType.IDENTIFIER);
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
        inst();
        consume(TokenType.STATEMENT_SEPARATOR);
        llistaInstAux();
    }

    private void inst() throws SyntacticException {
        switch(lat.getType()) {
            case REPETIR:
                consume(TokenType.REPETIR);
                llistaInst();
                consume(TokenType.FINS);
                exp();
                break;
            case MENTRE:
                consume(TokenType.MENTRE);
                exp();
                consume(TokenType.FER);
                llistaInst();
                consume(TokenType.FIMENTRE);
                break;
            case SI:
                consume(TokenType.SI);
                exp();
                consume(TokenType.LLAVORS);
                llistaInst();
                hasSino();
                consume(TokenType.FISI);
                break;
            case IDENTIFIER:
                variable();
                consume(TokenType.ASSIGNMENT);
                exp();
                break;
            case ESCRIURE:
                consume(TokenType.ESCRIURE);
                consume(TokenType.PARENTHESIS_OPEN);
                llistaExpNonEmpty();
                consume(TokenType.PARENTHESIS_CLOSE);
                break;
            case LLEGIR:
                consume(TokenType.LLEGIR);
                consume(TokenType.PARENTHESIS_OPEN);
                llistaVar();
                consume(TokenType.PARENTHESIS_CLOSE);
                break;
            case RETORNAR:
                consume(TokenType.RETORNAR);
                llistaInst();
                break;
            default:
                throw(ExceptionFactory.inst(lat.getType()));
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
