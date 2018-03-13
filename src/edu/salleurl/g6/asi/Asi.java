package edu.salleurl.g6.asi;

import com.sun.org.apache.bcel.internal.generic.SWITCH;
import edu.salleurl.g6.alex.Alex;
import edu.salleurl.g6.model.Token;
import edu.salleurl.g6.model.TokenType;

public class Asi {

    private Token lat;
    private Alex alex;

    public Asi(String filename) {
        alex = new Alex(filename);
        lat = alex.getToken();
    }


    private  void consume(TokenType t){
        if(t == lat.getType()){

        }

    }

    public void programa(){
        llistaDecVar();
        llistaDecFunc();
        consume(TokenType.INICI);
        llistaInst();
        consume(TokenType.FI);

    }

    public void llistaDecVar(){
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

    public void decVar(){
        switch (lat.getType()){
            case CONST:
                consume(TokenType.CONST);
                consume(TokenType.IDENTIFIER);
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
                //ERROR
        }
    }

    public void exp(){
        expSimple();
        llistaExpSimple();
    }

    public void expSimple(){
        opu();
        terme();
        llistaTermes();
    }

    public void opu(){
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

    public void terme(){
        factor();
        factorAux();
    }
    public void factor(){
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
                //ERROR
        }
    }

    public void factorIdSufix(){
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

    public void isVector(){
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

    public void llistaExp(){
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

    public void llistaExpNonEmpty(){
        exp();
        llistaExpAux();
    }

    public void llistaExpAux(){
        switch (lat.getType()){
            case ARGUMENT_SEPARATOR:
                consume(TokenType.ARGUMENT_SEPARATOR);
                llistaExpNonEmpty();
                break;
            default:
                break;
        }
    }

    public void factorAux(){
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

    public void opb(){
        switch (lat.getType()){
            case COMPLEX_ARITHMETIC_OPERATOR:
                consume(TokenType.COMPLEX_ARITHMETIC_OPERATOR);
                break;
            case AND:
                consume(TokenType.AND);
                break;
            default:
                //ERROR
        }
    }

    public void llistaTermes(){
        ops();
        terme();
        llistaTermes();
    }
    public void ops(){
        switch (lat.getType()){
            case SIMPLE_ARITHMETIC_OPERATOR:
                consume(TokenType.SIMPLE_ARITHMETIC_OPERATOR);
                break;
            case OR:
                consume(TokenType.OR);
                break;
            default:
                //ERROR
        }
    }

    public void llistaExpSimple(){
        switch(lat.getType()){
            case RELATIONAL_OPERATOR:
                consume(TokenType.RELATIONAL_OPERATOR);
                expSimple();
                break;
            default:
                break;
        }
    }

    public void tipus(){
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
                //ERRROR
        }
    }

    public void llistaDecFunc(){
        switch (lat.getType()){
            case FUNCIO:
                decFunc();
                llistaDecFunc();
            default:
                    break;
        }
    }

    public void decFunc(){
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

    public void llistaParam(){
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

    public void isRef(){
        switch (lat.getType()){
            case AMPERSAND:
                consume(TokenType.AMPERSAND);
                break;
            default:
                break;
        }
    }

    public void llistaParamAux(){
        switch (lat.getType()){
            case ARGUMENT_SEPARATOR:
                consume(TokenType.ARGUMENT_SEPARATOR);
                llistaParam();
                break;
            default:
                break;
        }
    }

    public void llistaInst(){
        inst();
        consume(TokenType.STATEMENT_SEPARATOR);
        llistaInstAux();
    }

    public void inst(){
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
                consume(TokenType.RELATIONAL_OPERATOR);
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
                //ERROR
        }
    }

    public void llistaInstAux() {
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
    public void hasSino(){
        switch (lat.getType()){
            case SINO:
                consume(TokenType.SINO);
                llistaInst();
                break;
            default:
                break;
        }
    }

    public void variable(){
        consume(TokenType.IDENTIFIER);
        isVector();
    }

    public void llistaVar(){
        variable();
        llistaVarAux();
    }

    public void llistaVarAux(){
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
