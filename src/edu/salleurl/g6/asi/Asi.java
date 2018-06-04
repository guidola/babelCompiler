package edu.salleurl.g6.asi;

import edu.salleurl.g6.alex.Alex;
import edu.salleurl.g6.ase.Ase;
import edu.salleurl.g6.ase.Semantic;
import edu.salleurl.g6.model.*;
import taulasimbols.*;

import javax.net.ssl.SSLEngineResult;
import java.lang.invoke.SwitchPoint;
import java.util.Hashtable;
import java.util.Vector;

public class Asi {
    private Ase ase;
    private Token lat;
    private Semantic attr;

    public Asi(String filename) {
        Alex.init(filename);
        lat = Alex.getToken();
        ase = new Ase();
        attr = new Semantic();
    }


    private void consume(TokenType t) throws SyntacticException {
        if (t == lat.getType()) {
            lat = Alex.getToken();
            return;
        }

        throw (ExceptionFactory.consume(t, lat.getType()));
    }

    public void programa() throws SyntacticException {
        ase.addNewBlock();
        attr = llistaDecVar(attr);//Done
        attr = llistaDecFunc(attr);//Done
        consume(TokenType.INICI);
        attr = llistaInst(attr);
        consume(TokenType.FI);
        consume(TokenType.EOF);

    }

    private Semantic llistaDecVar(Semantic attr) throws SyntacticException {
        switch (lat.getType()) {
            case CONST:
                Constant constant = new Constant();
                attr.setAttributes(new Hashtable<String, Constant>());
                attr.setValue(TokenType.CONST, constant);
                attr = decVar(attr);
                //ase.addNewConstant((Constant) attr.getValue(TokenType.CONST));
                attr = llistaDecVar(attr);
                break;
            case SIMPLE_TYPE:
                Variable var1 = new Variable();
                attr.setAttributes(new Hashtable<String, Variable>());
                attr.setValue(TokenType.SIMPLE_TYPE, var1);
                attr = decVar(attr);
                //ase.addNewVar((Variable) attr.getValue(TokenType.SIMPLE_TYPE));
                attr = llistaDecVar(attr);
                break;
            case VECTOR:
                Variable var2 = new Variable();
                attr.setAttributes(new Hashtable<String, Variable>());
                attr.setValue(TokenType.VECTOR, var2);
                attr = decVar(attr);
                //ase.addNewVar((Variable) attr.getValue(TokenType.VECTOR));
                attr = llistaDecVar(attr);
                break;
            default:
                break;
        }
        return attr;
    }

    private Semantic decVar(Semantic attr) throws SyntacticException {
        switch (lat.getType()) {
            case CONST:
                Constant constant = (Constant) attr.getValue(TokenType.CONST);
                consume(TokenType.CONST);
                constant.setNom(lat.getLexem());
                consume(TokenType.IDENTIFIER);
                consume(TokenType.ASSIGNMENT);

                //attr.setAttributes(new Hashtable<String,ITipus>());
                attr = exp(attr);

                if(attr.getValue("RESULT")!=null){
                    ITipus aux = (ITipus) attr.getValue("RESULT");
                    constant.setValor(aux.getNom());
                    constant.setTipus((ITipus) attr.getValue("RESULT"));
                }else if(attr.getValue("VARIABLE")!=null){
                    ITipus aux = (ITipus) attr.getValue("VARIABLE");
                    constant.setValor(aux.getNom());
                    constant.setTipus((ITipus) attr.getValue("VARIABLE"));
                }

                consume(TokenType.STATEMENT_SEPARATOR);
                ase.addNewConstant(constant);
                attr.setValue(TokenType.CONST, constant);
                break;
            case SIMPLE_TYPE:
                Variable var1 = (Variable) attr.getValue(TokenType.SIMPLE_TYPE);

                //attr.setAttributes(new Hashtable<String, TipusSimple>());
                attr = tipus(attr);
                var1.setTipus((TipusSimple) attr.getValue(TokenType.SIMPLE_TYPE));
                var1.setNom(lat.getLexem());
                consume(TokenType.IDENTIFIER);
                consume(TokenType.STATEMENT_SEPARATOR);
                ase.addNewVar(var1);
                attr.setValue(TokenType.SIMPLE_TYPE, var1);
                break;
            case VECTOR:
                Variable var2 = new Variable();
                attr = tipus(attr);
                var2.setTipus((TipusArray) attr.getValue(TokenType.VECTOR));
                var2.setNom(lat.getLexem());

                consume(TokenType.IDENTIFIER);
                consume(TokenType.STATEMENT_SEPARATOR);
                ase.addNewVar(var2);
                attr.setValue(TokenType.VECTOR, var2);
                break;
            default:
                throw (ExceptionFactory.decVar(lat.getType()));
        }
        return attr;
    }

    private Semantic exp(Semantic attr) throws SyntacticException {
        attr = expSimple(attr);
        if(attr.getValue("VARIABLE")!=null){
            attr.setValue("VAR_LEFT",attr.getValue("VARIABLE"));
            //attr.removeAttribute("VARIABLE");
        }

        attr = llistaExpSimple(attr);
        if(attr.getValue(TokenType.RELATIONAL_OPERATOR)!=null){

            if(attr.getValue("VARIABLE")!=null){
                attr.setValue("VAR_RIGHT",attr.getValue("VARIABLE"));
                if(ase.opsValidation(attr)==1){
                    attr = ase.opsOperation(attr);
                }
            }
        }
        return attr;
    }

    private Semantic expSimple(Semantic attr) throws SyntacticException {

        attr = opu(attr);
        String opu = (String) attr.getValue("OPU");
        attr = terme(attr);
        attr = ase.opuOperation(attr);

        //TODO APLICAR OPU AL TERME EN VARIABLE
        attr = llistaTermes(attr);

        //System.out.println("TERME READY!--> "+(TipusSimple)attr.getValue("VAR2").getNom());
        return attr;
    }

    private Semantic opu(Semantic attr) throws SyntacticException {
        switch (lat.getType()) {
            case SIMPLE_ARITHMETIC_OPERATOR:
                attr.setValue("OPU",lat.getLexem());
                consume(TokenType.SIMPLE_ARITHMETIC_OPERATOR);
                break;
            case NOT:
                attr.setValue("OPU",lat.getLexem());
                consume(TokenType.NOT);
            default:
                break;
        }
        return attr;
    }

    private Semantic terme(Semantic attr) throws SyntacticException {
        attr = factor(attr);
        attr = ase.identifyTerm(attr);
        attr =factorAux(attr);
        return attr;
    }

    private Semantic factor(Semantic attr) throws SyntacticException {
        switch (lat.getType()) {
            case INTEGER_CONSTANT:
                attr.setValue(TokenType.INTEGER_CONSTANT, lat.getLexem());
                consume(TokenType.INTEGER_CONSTANT);
                break;
            case LOGIC_CONSTANT:
                attr.setValue(TokenType.LOGIC_CONSTANT, lat.getLexem());
                consume(TokenType.LOGIC_CONSTANT);
                break;
            case STRING:
                attr.setValue(TokenType.STRING, lat.getLexem());
                consume(TokenType.STRING);
                break;
            case IDENTIFIER:
                attr.setValue(TokenType.IDENTIFIER, lat.getLexem());
                attr =  ase.findVariable(attr,lat.getLexem());
                consume(TokenType.IDENTIFIER);
                attr = factorIdSufix(attr);
                break;
            case PARENTHESIS_OPEN:
                consume(TokenType.PARENTHESIS_OPEN);
                attr = exp(attr);
                consume(TokenType.PARENTHESIS_CLOSE);
                break;
            default:
                throw (ExceptionFactory.factor(lat.getType()));
        }
        return attr;
    }

    private Semantic factorIdSufix(Semantic attr) throws SyntacticException {
        switch (lat.getType()) {
            case PARENTHESIS_OPEN:
                consume(TokenType.PARENTHESIS_OPEN);
                attr = llistaExp(attr);
                consume(TokenType.PARENTHESIS_CLOSE);
                break;
            default:
                attr = isVector(attr);
        }
        return  attr;
    }

    private Semantic isVector(Semantic attr) throws SyntacticException {
        switch (lat.getType()) {
            case SQUARE_BRACKETS_OPEN:
                consume(TokenType.SQUARE_BRACKETS_OPEN);
                attr = exp(attr);

                attr = ase.vectorAccesValidation(attr);

                consume(TokenType.SQUARE_BRACKETS_CLOSE);
                break;
            default:
                break;
        }
        return attr;
    }

    private Semantic llistaExp(Semantic attr) throws SyntacticException {
        switch (lat.getType()) {
            case SIMPLE_ARITHMETIC_OPERATOR:
            case NOT:
            case INTEGER_CONSTANT:
            case LOGIC_CONSTANT:
            case STRING:
            case IDENTIFIER:
            case PARENTHESIS_OPEN:
                attr = llistaExpNonEmpty(attr);
                break;
            default:
                break;
        }
        return attr;
    }

    private Semantic llistaExpNonEmpty(Semantic attr) throws SyntacticException {
        attr = exp(attr);
        attr = llistaExpAux(attr);
        return attr;
    }

    private Semantic llistaExpAux(Semantic attr) throws SyntacticException {
        switch (lat.getType()) {
            case ARGUMENT_SEPARATOR:
                consume(TokenType.ARGUMENT_SEPARATOR);
                attr = llistaExpNonEmpty(attr);
                break;
            default:
                break;
        }
        return attr;
    }

    private Semantic factorAux(Semantic attr) throws SyntacticException {
        switch (lat.getType()) {
            case COMPLEX_ARITHMETIC_OPERATOR:
            case AND:
                attr = opb(attr);
                if(ase.opsValidation(attr)==1) {
                    Object var1;
                    if(attr.getValue("RESULT")==null){
                        var1 = attr.getValue("VARIABLE");
                    }else{
                        var1 = attr.getValue("RESULT");
                    }
                    attr.setValue("VAR_LEFT", var1);
                }
                attr =terme(attr);
                if(ase.opsValidation(attr)==1) {
                    Object var2 = attr.getValue("VARIABLE");
                    attr.setValue("VAR_RIGHT", var2);
                    attr = ase.opsOperation(attr);
                }
                break;
            default:
                break;
        }
        return attr;
    }

    private Semantic opb(Semantic attr) throws SyntacticException {
        switch (lat.getType()) {
            case COMPLEX_ARITHMETIC_OPERATOR:
                attr.setValue(TokenType.COMPLEX_ARITHMETIC_OPERATOR,lat.getLexem());
                consume(TokenType.COMPLEX_ARITHMETIC_OPERATOR);
                break;
            case AND:
                attr.setValue(TokenType.AND,lat.getLexem());
                consume(TokenType.AND);
                break;
            default:
                throw (ExceptionFactory.opb(lat.getType()));
        }
        return  attr;
    }


    private Semantic llistaTermes(Semantic attr) throws SyntacticException {
        switch (lat.getType()) {
            case SIMPLE_ARITHMETIC_OPERATOR:
            case OR:
                attr = ops(attr);
                if(ase.opsValidation(attr)==1) {
                    Object var1 = attr.getValue("VARIABLE");
                    attr.setValue("VAR_LEFT", var1);
                }
                attr =terme(attr);
                if(ase.opsValidation(attr)==1) {
                    Object var2 = attr.getValue("VARIABLE");
                    attr.setValue("VAR_RIGHT", var2);
                   attr = ase.opsOperation(attr);
                }
                attr = llistaTermes(attr);
            default:
                break;
        }
        return attr;
    }

    private Semantic ops(Semantic attr) throws SyntacticException {
        switch (lat.getType()) {
            case SIMPLE_ARITHMETIC_OPERATOR:
                attr.setValue( TokenType.SIMPLE_ARITHMETIC_OPERATOR,lat.getLexem());
                consume(TokenType.SIMPLE_ARITHMETIC_OPERATOR);
                break;
            case OR:
                attr.setValue( TokenType.OR,lat.getLexem());
                consume(TokenType.OR);
                break;
            default:
                throw (ExceptionFactory.ops(lat.getType()));
        }
        return attr;
    }

    private Semantic llistaExpSimple(Semantic attr) throws SyntacticException {
        switch (lat.getType()) {
            case RELATIONAL_OPERATOR:
                attr.setValue(TokenType.RELATIONAL_OPERATOR,lat.getLexem());
                consume(TokenType.RELATIONAL_OPERATOR);
                attr = expSimple(attr);
                break;
            default:
                break;
        }
        return attr;
    }

    private Semantic tipus(Semantic attr) throws SyntacticException {
        switch (lat.getType()) {
            case SIMPLE_TYPE:
                //attr.setValue("tipus",lat.getLexem());
                attr.setValue(TokenType.SIMPLE_TYPE, new TipusSimple(lat.getLexem(), 10000));
                consume(TokenType.SIMPLE_TYPE);
                break;
            case VECTOR:
                TipusArray vec = new TipusArray();

                consume(TokenType.VECTOR);
                consume(TokenType.SQUARE_BRACKETS_OPEN);
                attr.setValue("VECTOR_SIZE",lat.getLexem());

                attr.setValue("VECTOR_2",vec);
                attr = ase.addVectorSize(attr);

                attr.removeAttribute("VECTOR_SIZE");
                vec = (TipusArray) attr.getValue("VECTOR_2");

                consume(TokenType.INTEGER_CONSTANT);
                consume(TokenType.SQUARE_BRACKETS_CLOSE);
                consume(TokenType.DE);

                vec.setTipusElements(new TipusSimple(lat.getLexem(), 10000));

                consume(TokenType.SIMPLE_TYPE);

                attr.setValue(TokenType.VECTOR, vec);
                break;
            default:
                throw (ExceptionFactory.tipus(lat.getType()));
        }
        return attr;
    }

    private Semantic llistaDecFunc(Semantic attr) throws SyntacticException {
        switch (lat.getType()) {
            //case IDENTIFIER:
            //probably is a function declaration badly typed if it is not inici
            case FUNCIO:
                Funcio func = new Funcio();
                //attr.setAttributes(new Hashtable<String, Funcio>());
                attr.setValue(TokenType.FUNCIO, func);
                attr = decFunc(attr);
               // ase.addNewFuncio((Funcio) attr.getValue(TokenType.FUNCIO));
                attr = llistaDecFunc(attr);
                break;
            default:
                break;
        }
        return attr;
    }

    private Semantic decFunc(Semantic attr) throws SyntacticException {
        ase.addNewBlock();
        consume(TokenType.FUNCIO);
        Funcio func = (Funcio) attr.getValue(TokenType.FUNCIO);
        func.setNom(lat.getLexem());
        attr.setValue("FUNC_NAME",lat.getLexem());
        consume(TokenType.IDENTIFIER);
        consume(TokenType.PARENTHESIS_OPEN);


        attr.setValue(TokenType.FUNCIO, func);

        attr = llistaParam(attr);

        func = (Funcio) attr.getValue(TokenType.FUNCIO);

        consume(TokenType.PARENTHESIS_CLOSE);
        consume(TokenType.RETURN_TYPE_PREFIX);
        func.setTipus(new TipusSimple(lat.getLexem(), 10000));
        consume(TokenType.SIMPLE_TYPE);
        consume(TokenType.BRACKETS_OPEN);
        ase.addParamVars(func);
        ase.addNewFuncio(func);
        //TODO SEMANTIC
        attr = llistaDecVar(attr);
        attr = llistaInst(attr);
        consume(TokenType.BRACKETS_CLOSE);
        consume(TokenType.STATEMENT_SEPARATOR);
        ase.deleteActualBlock();
        attr.setValue(TokenType.FUNCIO, func);
        return attr;
    }

    private Semantic llistaParam(Semantic attr) throws SyntacticException {
        Parametre param = new Parametre();
        Funcio aux = (Funcio) attr.getValue(TokenType.FUNCIO);
        switch (lat.getType()) {
            case SIMPLE_TYPE:
                attr.setAttributes(new Hashtable<String, TipusSimple>());
                attr = tipus(attr);
                param.setTipus((TipusSimple) attr.getValue(TokenType.SIMPLE_TYPE));

                //tipus();
                attr.setAttributes(new Hashtable<String, TipusPasParametre>());
                attr = isRef(attr);
                param.setTipusPasParametre((TipusPasParametre) attr.getValue(TokenType.AMPERSAND));
                param.setNom(lat.getLexem());
                consume(TokenType.IDENTIFIER);
                aux.inserirParametre(param);

                attr.setValue(TokenType.FUNCIO, aux);
                attr = llistaParamAux(attr);
                break;
            case VECTOR:
                attr.setAttributes(new Hashtable<String, TipusArray>());
                attr = tipus(attr);
                param.setTipus((TipusArray) attr.getValue(TokenType.VECTOR));

                attr.setAttributes(new Hashtable<String, TipusPasParametre>());
                attr = isRef(attr);
                param.setTipusPasParametre((TipusPasParametre) attr.getValue(TokenType.AMPERSAND));
                param.setNom(lat.getLexem());
                consume(TokenType.IDENTIFIER);
                aux.inserirParametre(param);
                attr.setValue(TokenType.FUNCIO, aux);
                attr = llistaParamAux(attr);
                break;
            default:
                break;
        }
        attr.setValue(TokenType.FUNCIO, aux);
        return attr;
    }

    private Semantic isRef(Semantic attr) throws SyntacticException {
        switch (lat.getType()) {
            case AMPERSAND:
                attr.setValue(TokenType.AMPERSAND, TipusPasParametre.REFERENCIA);
                consume(TokenType.AMPERSAND);
                break;
            default:
                attr.setValue(TokenType.AMPERSAND, TipusPasParametre.VALOR);
                break;
        }
        return attr;
    }

    private Semantic llistaParamAux(Semantic attr) throws SyntacticException {
        switch (lat.getType()) {
            case ARGUMENT_SEPARATOR:
                consume(TokenType.ARGUMENT_SEPARATOR);
                attr = llistaParam(attr);
                break;
            default:
                break;
        }
        return attr;
    }

    private Semantic llistaInst(Semantic attr) throws SyntacticException {
        switch (lat.getType()) {
            case REPETIR:
            case MENTRE:
            case SI:
            case IDENTIFIER:
            case ESCRIURE:
            case LLEGIR:
            case RETORNAR:
                attr = inst(attr);
                consume(TokenType.STATEMENT_SEPARATOR);
                attr = llistaInstAux(attr);
                break;
            default:
                throw (ExceptionFactory.inst(lat.getType()));
        }
        return attr;
    }

    private Semantic inst(Semantic attr) throws SyntacticException {
        switch (lat.getType()) {
            case REPETIR:
                attr.setValue(TokenType.REPETIR,lat.getLexem());
                consume(TokenType.REPETIR);
                attr = llistaInst(attr);
                consume(TokenType.FINS);
                attr = exp(attr);
                if(!ase.isLogic(attr.getValue("RESULT"))){
                    System.err.println("Condition with non logic value");
                }
                break;
            case MENTRE:
                attr.setValue(TokenType.MENTRE,lat.getLexem());
                consume(TokenType.MENTRE);
                attr = exp(attr);
                if(!ase.isLogic(attr.getValue("RESULT"))){
                    System.err.println("While with non logic value");
                }
                consume(TokenType.FER);
                attr = llistaInst(attr);
                consume(TokenType.FIMENTRE);
                break;
            case SI:
                attr.setValue(TokenType.SI,lat.getLexem());
                consume(TokenType.SI);
                attr = exp(attr);
                if(!ase.isLogic(attr.getValue("RESULT"))){
                    System.err.println("Condition with non logic value");
                }
                consume(TokenType.LLAVORS);
                attr = llistaInst(attr);
                attr = hasSino(attr);
                consume(TokenType.FISI);
                break;
            case IDENTIFIER:
                attr.setValue(TokenType.IDENTIFIER,lat.getLexem());
                attr = variable(attr);
                consume(TokenType.ASSIGNMENT);
                attr = exp(attr);
                attr = ase.addNewVarValue(attr);
                break;
            case ESCRIURE:
                attr.setValue(TokenType.ESCRIURE,lat.getLexem());
                consume(TokenType.ESCRIURE);
                consume(TokenType.PARENTHESIS_OPEN);
                attr = llistaExpNonEmpty(attr);
                consume(TokenType.PARENTHESIS_CLOSE);
                break;
            case LLEGIR:
                attr.setValue(TokenType.LLEGIR,lat.getLexem());
                consume(TokenType.LLEGIR);
                consume(TokenType.PARENTHESIS_OPEN);
                llistaVar();
                consume(TokenType.PARENTHESIS_CLOSE);
                break;
            case RETORNAR:
                attr.setValue(TokenType.RETORNAR,lat.getLexem());
                consume(TokenType.RETORNAR);
                attr = exp(attr);
                attr = ase.returnValidation(attr);
                break;
            default:
                throw (ExceptionFactory.inst(lat.getType()));
        }
        return  attr;
    }

    private Semantic llistaInstAux(Semantic attr) throws SyntacticException {
        switch (lat.getType()) {
            case REPETIR:
            case MENTRE:
            case SI:
            case IDENTIFIER:
            case ESCRIURE:
            case LLEGIR:
            case RETORNAR:
                attr = llistaInst(attr);
                break;
            default:
                break;
        }
        return attr;
    }

    private Semantic hasSino(Semantic attr) throws SyntacticException {
        switch (lat.getType()) {
            case SINO:
                consume(TokenType.SINO);
                attr = llistaInst(attr);
                break;
            default:
                break;
        }
        return attr;
    }

    private Semantic variable(Semantic attr) throws SyntacticException {
        attr.setValue("ID_ASSIGMENT",lat.getLexem());
        consume(TokenType.IDENTIFIER);
        attr = isVector(attr);
        return attr;
    }

    private void llistaVar() throws SyntacticException {
        attr = variable(attr);
        llistaVarAux();
    }

    private void llistaVarAux() throws SyntacticException {
        switch (lat.getType()) {
            case ARGUMENT_SEPARATOR:
                consume(TokenType.ARGUMENT_SEPARATOR);
                llistaVar();
                break;
            default:
                break;
        }
    }
}
