package edu.salleurl.g6.asi;

import edu.salleurl.g6.alex.Alex;
import edu.salleurl.g6.ase.Ase;
import edu.salleurl.g6.ase.Semantic;
import edu.salleurl.g6.gc.MIPSFactory;
import edu.salleurl.g6.gc.OffsetFactory;
import edu.salleurl.g6.model.*;
import taulasimbols.*;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;

public class Asi {
    private Ase ase;
    private Token lat;
    private Semantic attr;
    private Funcio actFunc = null;


    public Asi(String filename) {
        Alex.init(filename);
        MIPSFactory.initAssemblyOutFile(filename);
        lat = Alex.getToken();
        ase = new Ase();
        attr = new Semantic();
    }


    private String consume(TokenType t) throws SyntacticException {
        String lexem = lat.getLexem();

        if (t == lat.getType()) {
            lat = Alex.getToken();
            return lexem;
        }

        throw (ExceptionFactory.consume(t, lat.getType()));
    }

    public void programa() throws SyntacticException {
        ase.addNewBlock();
        OffsetFactory.reset();
        llistaDecVar();
        llistaDecFunc();
        MIPSFactory.init();
        consume(TokenType.INICI);
        llistaInst();
        consume(TokenType.FI);
        consume(TokenType.EOF);
        MIPSFactory.finish();

    }

    private void llistaDecVar() throws SyntacticException {
        switch (lat.getType()) {
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
        switch (lat.getType()) {
            case CONST:
                consume(TokenType.CONST);

                Constant constant = new Constant();
                constant.setNom(consume(TokenType.IDENTIFIER));
                consume(TokenType.ASSIGNMENT);

                Semantic exp_result = exp();
                //TODO check that expression is estatic and did not fail to get any of the constants that may form it
                //TODO check that the exp is of type _cadena or _simple
                constant.setTipus(exp_result.type());
                constant.setValor(exp_result.constValue());

                consume(TokenType.STATEMENT_SEPARATOR);
                ase.addNewConstant(constant);
                break;

            case SIMPLE_TYPE:
            case VECTOR:

                Variable var = new Variable();
                var.setTipus(tipus());
                var.setNom(consume(TokenType.IDENTIFIER));
                var.setDesplacament(OffsetFactory.nextOffset(var.getTipus().getTamany()));
                consume(TokenType.STATEMENT_SEPARATOR);
                ase.addNewVar(var);
                break;

            default:
                throw (ExceptionFactory.decVar(lat.getType()));
        }
    }

    private Semantic exp() throws SyntacticException {
        Semantic left_side_exp = expSimple();
        return llistaExpSimple(left_side_exp);
    }

    private Semantic expSimple() throws SyntacticException {

        Semantic operator = opu();
        Semantic operand = terme(new Semantic());

        Semantic result = operator.performUnaryOperation(operand);

        //perform the rest of the exp_simple and return the resultant _Semantic
        return llistaTermes(result);
    }

    private Semantic opu() throws SyntacticException {

        Semantic opu = new Semantic();

        switch (lat.getType()) {
            case SIMPLE_ARITHMETIC_OPERATOR:
                opu.setUOperator(consume(TokenType.SIMPLE_ARITHMETIC_OPERATOR));
                break;
            case NOT:
                opu.setUOperator(consume(TokenType.NOT));
                break;
            default:
                break;
        }
        return opu;
    }

    private Semantic terme(Semantic acumm_exp) throws SyntacticException {
        // a term call may receive a semantic class without operator if it is the first call to term on that exp
        // or receive a operator and an accumulated result if it is a recursive call inside the expression.
        // since association is left-wise we operate downwards hence the operation must be performed before
        // the recursive call to obtain the proper result
        Semantic operand = factor(acumm_exp);
        return factorAux(acumm_exp.performBinaryOperation(operand));
        /* RETURN:  result holds a _reg with either the register where operand1 is held
                    or the result of the  operation or if isEstatic then it holds the numeric value of operand1
         */
    }

    private Semantic factor(Semantic attr) throws SyntacticException {

        Semantic factor = new Semantic();

        switch (lat.getType()) {
            case INTEGER_CONSTANT:
                factor.setEstatic(true);
                factor.setType(new TipusSimple(Ase.TIPUS_SIMPLE, MIPSFactory.TIPUS_SIMPLE_SIZE));
                factor.setValue(Integer.parseInt(consume(TokenType.INTEGER_CONSTANT)));
                //TODO validate that size of integer constant fits in 32bit ca2 register
                break;
            case LOGIC_CONSTANT:
                factor.setEstatic(true);
                factor.setType(new TipusSimple(Ase.TIPUS_LOGIC, MIPSFactory.TIPUS_LOGIC_SIZE));
                String logic_ct = consume(TokenType.LOGIC_CONSTANT);
                //TODO validate logic ct is one of the 2 valid options
                factor.setValue( logic_ct.equals(Ase.CERT) ? MIPSFactory.CERT : MIPSFactory.FALS );

                break;
            case STRING:
                // a string cannot be operated with and hence a exp containing a string as factor can only be a single
                // factor expression, ergo define string in assembly and return label to refer to in print operations
                factor.setEstatic(true);
                factor.setType(new TipusCadena());
                attr.setTag(MIPSFactory.defineString(consume(TokenType.STRING)));
                attr.setValue("STRING",factor.type());
                break;
            case IDENTIFIER:
                String id = consume(TokenType.IDENTIFIER);
                factor = factorIdSufix(id);
                break;
            case PARENTHESIS_OPEN:
                consume(TokenType.PARENTHESIS_OPEN);
                factor = exp();
                consume(TokenType.PARENTHESIS_CLOSE);
                break;
            default:
                throw (ExceptionFactory.factor(lat.getType()));
        }

        attr = ase.updateParamTrace(attr, factor);
        return factor;
    }
    //TODO
    private Semantic factorIdSufix(String id) throws SyntacticException {
        switch (lat.getType()) {
            case PARENTHESIS_OPEN:
                consume(TokenType.PARENTHESIS_OPEN);
                //TODO recover and pass to llistaExp the parameter definition for the func with id _id
                LinkedList<Semantic> parameters = llistaExp(); //TODO follow this call line and implement ase & mips stuff
                consume(TokenType.PARENTHESIS_CLOSE);
                //TODO write all operations to call the func with _parameters if not_empty
                return new Semantic(); // TODO return the return value of the func or where the return value is stored (reg)

                /*consume(TokenType.PARENTHESIS_OPEN);
                //PERQUE ES PERDEN ELS ATTR?

                //TODO recover and pass to llistaExp the parameter definition for the func with id _id
                attr.setValue("auxList",new ArrayList<ITipus>());
                Semantic parameters = llistaExp(attr); //TODO follow this call line and implement ase & mips stuff
                ase.validateFuncio(attr);
                this.attr.removeAttribute("auxList");
                consume(TokenType.PARENTHESIS_CLOSE);
                //TODO write all operations to call the func with _parameters if not_empty
                return parameters;*/
            default:
                return isVector(id, Ase.LOAD);
        }
    }

    private Semantic isVector(String id, boolean isStore) throws SyntacticException {

        Semantic variable;

        switch (lat.getType()) {
            case SQUARE_BRACKETS_OPEN:
                consume(TokenType.SQUARE_BRACKETS_OPEN);
                Semantic expression = exp();
                consume(TokenType.SQUARE_BRACKETS_CLOSE);

                if(isStore) {
                    variable = ase.validateArrayAccessAndGetOffset(id, expression);
                } else {
                    variable = ase.validateArrayAccessAndLoadCell(id, expression);
                }
                break;

            default:
                variable = ase.getVariableOrConstant(id);
                variable.isVectorIndexNonStatic(false);
                if(variable.isEstatic()){
                    //TODO prompt error if variable is CONST and we are doing a STORE action
                    if(variable.isString()) {
                        variable.setTag(MIPSFactory.defineString(variable.strValue()));
                    }
                } else {
                    if(!isStore) {
                        variable.setRegister(MIPSFactory.loadVariable(variable.offset(), variable.isGlobal()));
                    }
                }

                break;
        }
        variable.setValue("VAR_NAME",id);
        return variable;
    }

    private LinkedList<Semantic> llistaExp() throws SyntacticException {

        LinkedList<Semantic> arguments = new LinkedList<>();

        switch (lat.getType()) {
            case SIMPLE_ARITHMETIC_OPERATOR:
            case NOT:
            case INTEGER_CONSTANT:
            case LOGIC_CONSTANT:
            case STRING:
            case IDENTIFIER:
            case PARENTHESIS_OPEN:
                return llistaExpNonEmpty(arguments);
            default:
                break;
        }
        return arguments;
    }

    private LinkedList<Semantic> llistaExpNonEmpty(LinkedList<Semantic> arguments) throws SyntacticException {
        arguments.push(exp());
        return llistaExpAux(arguments);
    }

    private LinkedList<Semantic> llistaExpAux(LinkedList<Semantic> arguments) throws SyntacticException {
        switch (lat.getType()) {
            case ARGUMENT_SEPARATOR:
                consume(TokenType.ARGUMENT_SEPARATOR);
                arguments = llistaExpNonEmpty(arguments);
                break;
            default:
                break;
        }
        return arguments;
    }

    private Semantic factorAux(Semantic accum_exp) throws SyntacticException {
        switch (lat.getType()) {
            case COMPLEX_ARITHMETIC_OPERATOR:
            case AND:
                accum_exp.setBOperator(opb());
                return terme(accum_exp);
            default:
                return accum_exp;
        }
    }

    private String opb() throws SyntacticException {
        switch (lat.getType()) {
            case COMPLEX_ARITHMETIC_OPERATOR:
                return consume(TokenType.COMPLEX_ARITHMETIC_OPERATOR);
            case AND:
                return consume(TokenType.AND);
            default:
                throw (ExceptionFactory.opb(lat.getType()));
        }
    }


    private Semantic llistaTermes(Semantic acumm_exp) throws SyntacticException {

        switch (lat.getType()) {
            case SIMPLE_ARITHMETIC_OPERATOR:
            case OR:

                acumm_exp.setBOperator(ops());
                Semantic operand2 =terme(new Semantic());

                return llistaTermes(acumm_exp.performBinaryOperation(operand2));
            default:
                break;
        }
        return acumm_exp;
    }

    private String ops() throws SyntacticException {
        switch (lat.getType()) {
            case SIMPLE_ARITHMETIC_OPERATOR:
                return consume(TokenType.SIMPLE_ARITHMETIC_OPERATOR);
            case OR:
                return consume(TokenType.OR);
            default:
                throw (ExceptionFactory.ops(lat.getType()));
        }
    }

    private Semantic llistaExpSimple(Semantic left_side_exp) throws SyntacticException {
        switch (lat.getType()) {
            case RELATIONAL_OPERATOR:
                left_side_exp.setOpRel(consume(TokenType.RELATIONAL_OPERATOR));
                Semantic right_side_exp = expSimple();

                if(ase.validateRelationalOp(left_side_exp,right_side_exp))left_side_exp = left_side_exp.performRelationalOperation(right_side_exp);
                return left_side_exp;
            default:
                return left_side_exp;
        }
    }

    private ITipus tipus() throws SyntacticException {

        switch (lat.getType()) {
            case SIMPLE_TYPE:
                String type = Ase.parseType(consume(TokenType.SIMPLE_TYPE));
                return new TipusSimple(type, type.equals(Ase.TIPUS_SIMPLE) ?
                        MIPSFactory.TIPUS_SIMPLE_SIZE : MIPSFactory.TIPUS_LOGIC_SIZE);

            case VECTOR:
                TipusArray vec = new TipusArray();

                consume(TokenType.VECTOR);
                consume(TokenType.SQUARE_BRACKETS_OPEN);

                int n_cells = Integer.parseInt(consume(TokenType.INTEGER_CONSTANT));
                vec.inserirDimensio(new DimensioArray(
                        new TipusSimple(Ase.TIPUS_SIMPLE, MIPSFactory.TIPUS_SIMPLE_SIZE),
                        0,
                        n_cells - 1
                ));

                consume(TokenType.SQUARE_BRACKETS_CLOSE);
                consume(TokenType.DE);

                String arrayType = Ase.parseType(consume(TokenType.SIMPLE_TYPE));
                vec.setTipusElements(new TipusSimple(arrayType, arrayType.equals(Ase.TIPUS_SIMPLE) ?
                        MIPSFactory.TIPUS_SIMPLE_SIZE : MIPSFactory.TIPUS_LOGIC_SIZE));
                vec.setTamany(n_cells * vec.getTipusElements().getTamany());
                return vec;

            default:
                throw (ExceptionFactory.tipus(lat.getType()));
        }

    }

    private void llistaDecFunc() throws SyntacticException {
        switch (lat.getType()) {
            //case IDENTIFIER:
            //probably is a function declaration badly typed if it is not inici
            case FUNCIO:
                attr = decFunc(attr);
                llistaDecFunc();
                break;
            default:
                break;
        }
    }

    private Semantic decFunc(Semantic attr) throws SyntacticException {
        ase.addNewBlock();
        consume(TokenType.FUNCIO);
        Funcio func = new Funcio();
        func.setNom(lat.getLexem());
        consume(TokenType.IDENTIFIER);
        consume(TokenType.PARENTHESIS_OPEN);


        attr.setValue(TokenType.FUNCIO, func);
        //TODO VALIDATE IF IS NECESSARY
        ArrayList<String> listParam = new ArrayList<>();
        attr.setValue("listParam",listParam);
        attr = llistaParam(attr);

        func = (Funcio) attr.getValue(TokenType.FUNCIO);

        consume(TokenType.PARENTHESIS_CLOSE);
        consume(TokenType.RETURN_TYPE_PREFIX);
        func.setTipus(new TipusSimple(lat.getLexem(), 4));
        consume(TokenType.SIMPLE_TYPE);
        consume(TokenType.BRACKETS_OPEN);
        ase.addParamVars(func);
        ase.addNewFuncio(func);
        actFunc = func;
        //TODO SEMANTIC
        OffsetFactory.reset();
        llistaDecVar();
        llistaInst();
        consume(TokenType.BRACKETS_CLOSE);
        consume(TokenType.STATEMENT_SEPARATOR);
        ase.deleteActualBlock();
        actFunc = null;
        attr.setValue(TokenType.FUNCIO, func);
        return attr;
    }

    private Semantic llistaParam(Semantic attr) throws SyntacticException {
        Parametre param = new Parametre();
        switch (lat.getType()) {
            case SIMPLE_TYPE:
                param.setTipus(tipus());

                attr = isRef(attr);
                param.setTipusPasParametre((TipusPasParametre) attr.getValue(TokenType.AMPERSAND));
                attr.removeAttribute(TokenType.AMPERSAND);
                param.setNom(lat.getLexem());
                consume(TokenType.IDENTIFIER);
                attr.setValue("param",param);

                attr = ase.addParam(attr);



                attr = llistaParamAux(attr);
                break;
            case VECTOR:

                param.setTipus(tipus());

                attr = isRef(attr);
                param.setTipusPasParametre((TipusPasParametre) attr.getValue(TokenType.AMPERSAND));
                attr.removeAttribute(TokenType.AMPERSAND);
                param.setNom(lat.getLexem());
                consume(TokenType.IDENTIFIER);
                attr.setValue("param",param);
                attr = ase.addParam(attr);
                attr = llistaParamAux(attr);
                break;
            default:
                break;
        }
        //attr.setValue(TokenType.FUNCIO, aux);
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

    private void llistaInst() throws SyntacticException {
        /*inst();
        consume(TokenType.STATEMENT_SEPARATOR);
        llistaInstAux();*/
        switch (lat.getType()) {
            case REPETIR:
            case MENTRE:
            case SI:
            case IDENTIFIER:
            case ESCRIURE:
            case LLEGIR:
            case RETORNAR:
                inst();
                consume(TokenType.STATEMENT_SEPARATOR);
                llistaInstAux();
                break;
            default:
                throw (ExceptionFactory.inst(lat.getType()));
        }
    }

    private void inst() throws SyntacticException {
        Semantic condition;
        switch (lat.getType()) {
            case REPETIR:
                consume(TokenType.REPETIR);
                String tag_repetir = MIPSFactory.setJumpPoint();
                llistaInst();
                consume(TokenType.FINS);

                condition = exp();

                if(condition.isUndefined()) break;

                if(condition.isEstatic()) {
                    MIPSFactory.jumpIfTrue(tag_repetir, condition.intValue());
                } else {
                    MIPSFactory.jumpIfTrue(tag_repetir, condition.reg());
                }
                break;
            case MENTRE:
                consume(TokenType.MENTRE);
                String tag_mentre = MIPSFactory.setJumpPoint();
                condition = exp();

                if(condition.isUndefined()) {
                    consume(TokenType.FER);
                    llistaInst();
                    consume(TokenType.FIMENTRE);
                }

                String tag_fimentre;
                if(condition.isEstatic()) {
                    tag_fimentre = MIPSFactory.jumpIfFalse(condition.intValue());
                } else {
                    tag_fimentre = MIPSFactory.jumpIfFalse(condition.reg());
                }

                consume(TokenType.FER);
                llistaInst();
                MIPSFactory.unconditionalJump(tag_mentre);
                MIPSFactory.setJumpPoint(tag_fimentre);

                consume(TokenType.FIMENTRE);
                break;
            case SI:
                consume(TokenType.SI);
                condition = exp();
                consume(TokenType.LLAVORS);

                // if the condition is undefined process the instructions and the else but do not codegen the condition
                if(condition.isUndefined()) {
                    llistaInst();
                    hasSino();
                    consume(TokenType.FISI);
                }

                String tag_sino;
                if(condition.isEstatic()) {
                    tag_sino = MIPSFactory.jumpIfFalse(condition.intValue());
                } else {
                    tag_sino = MIPSFactory.jumpIfFalse(condition.reg());
                }

                llistaInst();
                String tag_fisi = MIPSFactory.unconditionalJump();

                MIPSFactory.setJumpPoint(tag_sino);
                hasSino();
                MIPSFactory.setJumpPoint(tag_fisi);

                consume(TokenType.FISI);
                break;
            case IDENTIFIER:
                Semantic target = variableStore();
                consume(TokenType.ASSIGNMENT);
                Semantic exp_result = exp();
                ase.validateAssigment(target,exp_result);
                //TODO LEFT PART SAME TYPE OF EXP_RESULT
                target.store(exp_result);
                break;
            case ESCRIURE:
                consume(TokenType.ESCRIURE);
                consume(TokenType.PARENTHESIS_OPEN);
                ase.performWriteOperation(llistaExpNonEmpty(new LinkedList<>()));
                consume(TokenType.PARENTHESIS_CLOSE);
                break;
            case LLEGIR:
                consume(TokenType.LLEGIR);
                consume(TokenType.PARENTHESIS_OPEN);
                ase.performReadOperation(llistaVar(new LinkedList<>()));
                consume(TokenType.PARENTHESIS_CLOSE);
                break;
            case RETORNAR:
                // TODO implement retornar code generation ( stack management and so )
                consume(TokenType.RETORNAR);
                Semantic exp_return = exp();
                ase.validateReturn(exp_return,actFunc);
                break;
            default:
                throw (ExceptionFactory.inst(lat.getType()));
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
        switch (lat.getType()) {
            case SINO:
                consume(TokenType.SINO);
                llistaInst();
                break;
            default:
                break;
        }
    }

    private Semantic variableStore() throws SyntacticException {
        String id = consume(TokenType.IDENTIFIER);
        return isVector(id, Ase.STORE);
    }

    /**
     * POST:    if non-static returns a REG that holds the content of the variable or array position
     *                                  TYPE with the type of the value that the register holds
     *          if static returns STATIC=true and VALUE holds the constant Descriptor
     */
    private Semantic variable() throws SyntacticException {
        String id = consume(TokenType.IDENTIFIER);
        return isVector(id, Ase.LOAD);
    }

    private LinkedList<Semantic> llistaVar(LinkedList<Semantic> arguments) throws SyntacticException {
        arguments.push(variable());
        return llistaVarAux(arguments);
    }

    private LinkedList<Semantic> llistaVarAux(LinkedList<Semantic> arguments) throws SyntacticException {
        switch (lat.getType()) {
            case ARGUMENT_SEPARATOR:
                consume(TokenType.ARGUMENT_SEPARATOR);
                llistaVar(arguments);
                break;
            default:
                break;
        }

        return arguments;
    }
}
