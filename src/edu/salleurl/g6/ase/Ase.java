package edu.salleurl.g6.ase;

import edu.salleurl.g6.gc.MIPSFactory;
import edu.salleurl.g6.model.TokenType;
import taulasimbols.*;

import java.util.ArrayList;
import java.util.LinkedList;

public class Ase {

    private static final int CONTEXT_GLOBAL = 0;
    public static final String TIPUS_SIMPLE = "sencer";
    public static final String TIPUS_LOGIC = "logic";
    public static final String TIPUS_CADENA = "cadena";
    public static final String CERT = "cert";
    public static final String FALS = "fals";
    public static final boolean STORE = true;
    public static final boolean LOAD = false;

    private TaulaSimbols ts;

    public Ase() {
        this.ts = new TaulaSimbols();
    }


    public static String parseType(String lexem) {

        if (lexem.equals("sencer")) return TIPUS_SIMPLE;
        if (lexem.equals("logic")) return TIPUS_LOGIC;

        return null;
    }

    public void addNewBlock() {
        ts.inserirBloc(new Bloc());
        ts.setBlocActual(ts.getNumeroBlocs() - 1);
    }

    public void addNewConstant(Constant var) {
        if (ts.obtenirBloc(ts.getBlocActual()).obtenirConstant(var.getNom()) == null &&
                ts.obtenirBloc(ts.getBlocActual()).obtenirVariable(var.getNom()) == null)
            ts.obtenirBloc(ts.getBlocActual()).inserirConstant(var);
        else
            System.err.println("[ERR_SEM_1] Constant " + var.getNom() + "  doblement definida");

    }

    public void addNewVar(Variable var) {
        if (ts.obtenirBloc(ts.getBlocActual()).obtenirConstant(var.getNom()) == null &&
                ts.obtenirBloc(ts.getBlocActual()).obtenirVariable(var.getNom()) == null)
            ts.obtenirBloc(ts.getBlocActual()).inserirVariable(var);
        else
            System.err.println("[ERR_SEM_2] Variable " + var.getNom() + "  doblement definida");


    }

    public boolean isInteger(Object var, String value) {
        if (isSimpleType(var)) {
            if (!isLogic(var) && !((TipusSimple) var).getNom().equals("sencer")) {
                if (Integer.parseInt(value) >= 0) {
                    return true;
                } else {
                    //ERROR INCORRECT VALUE FOR INTEGER
                    return false;
                }
            }
        }
        return false;
    }

    public Semantic addNewVarValue(Semantic attr) {
        String name = (String) attr.getValue("ID_ASSIGMENT");
        Object newTipus = attr.getValue("VARIABLE");
        if (newTipus instanceof Parametre) newTipus = ((Parametre) newTipus).getTipus();
        if (newTipus == null) newTipus = (ITipus) attr.getValue("RESULT");
        Variable newVar = ts.obtenirBloc(ts.getBlocActual()).obtenirVariable(name);
        int blockActual = 1;
        if (newVar == null) {
            blockActual = 0;
            newVar = ts.obtenirBloc(blockActual).obtenirVariable(name);
        }

        if (newVar != null) {
            if (newVar.getTipus() instanceof TipusArray) {
                if (attr.getValue("INDEX_VECTOR") != null) {
                    int index = Integer.parseInt((String) attr.getValue("INDEX_VECTOR"));
                    ((TipusArray) newVar.getTipus()).obtenirDimensio(index).setTipusLimit(((ITipus) newTipus));
                    ts.obtenirBloc(blockActual).inserirVariable(newVar);
                }
            } else {

                if (isInteger(newVar.getTipus(), newVar.getTipus().getNom()) && isInteger(newTipus, ((ITipus) newTipus).getNom()) || (newVar.getTipus().getNom().equals("sencer"))) {
                    newVar.setTipus(((ITipus) newTipus));
                    ts.obtenirBloc(blockActual).inserirVariable(newVar);
                } else {
                    if (isLogic(newVar.getTipus()) && isLogic(newTipus)) {
                        newVar.setTipus(((ITipus) newTipus));
                        ts.obtenirBloc(blockActual).inserirVariable(newVar);
                    }
                }
            }
        }
        return attr;
    }


    public Semantic getGlobalVariableOrConstant(String id) {

        Constant ct = ts.obtenirBloc(CONTEXT_GLOBAL).obtenirConstant(id);

        if (ct == null) {
            Variable var = ts.obtenirBloc(CONTEXT_GLOBAL).obtenirVariable(id);
            if (var == null) {
                //TODO error - non declaraded variable or constant

                //TODO change this for the proper error handling return
                return null;
            }

            return new Semantic(var, true);
        }

        return new Semantic(ct, true);
    }

    public Semantic getVariableOrConstant(String id) {

        Constant ct = ts.obtenirBloc(ts.getBlocActual()).obtenirConstant(id);

        if (ct == null) {
            Variable var = ts.obtenirBloc(ts.getBlocActual()).obtenirVariable(id);
            if (var == null) {
                Semantic s = getGlobalVariableOrConstant(id);
                if (s == null) {
                    return notFoundVar(var, id);
                }
                return s;
            }

            return new Semantic(var, ts.getBlocActual() == CONTEXT_GLOBAL);
        }

        return new Semantic(ct, ts.getBlocActual() == CONTEXT_GLOBAL);
    }

    public Semantic getArray(String id) {
        Variable var = ts.obtenirBloc(ts.getBlocActual()).obtenirVariable(id);
        boolean isGlobal = ts.getBlocActual() == CONTEXT_GLOBAL;
        if (var == null) {
            var = ts.obtenirBloc(CONTEXT_GLOBAL).obtenirVariable(id);
            if (var == null) {
                return notFoundVar(var, id);
            }
            isGlobal = true;

        }

        if (var.getTipus() instanceof TipusArray) {
            return new Semantic(var, isGlobal);
        }

        Semantic undefined = new Semantic();
        undefined.setEstatic(true);
        undefined.setType(new TipusIndefinit());
        return undefined;
    }

    public Semantic validateArrayAccessAndGetOffset(String id, Semantic index) {

        //TODO validate it is actually an array
        Semantic vector = getArray(id);

        // if vector is undefined ergo, not found, then return an undefined Semantic
        if(vector.isUndefined()) {
            MIPSFactory.returnRegister(index);
            return undefined();
        }

        Semantic cell = new Semantic();
        cell.setType(vector.arrayType());
        cell.setEstatic(false);
        cell.setGlobal(vector.isGlobal());

        // if index is not int then set index to 0 and static
        if (!index.isInt()) {
            if(!index.isUndefined()) {
                System.err.println("[ERR_SEM_12] El tipus de l'index d'accés del vector no és SENCER");
            }
            index.setValue(0);
            index.setEstatic(true);
        }

        if (index.isEstatic()) {
            // TODO evaluate array bounds ( remember that upper bound is the last working cell of the array, not its dimension )
            cell.isVectorIndexNonStatic(false);
            cell.setOffset(vector.offset() + index.intValue() * vector.arrayType().getTamany());
        } else {
            cell.isVectorIndexNonStatic(true);
            cell.setRegister(MIPSFactory.validateAndGetArrayCellOffset(vector.offset(), index.reg(), vector.arrayLowerBound(),
                    vector.arrayUpperBound()));

        }

        return cell;
    }

    private Semantic undefined() {
        Semantic undefined = new Semantic();
        undefined.setType(new TipusIndefinit());
        undefined.setEstatic(true);
        return undefined;
    }

    public Semantic validateArrayAccessAndLoadCell(String id, Semantic index) {

        Semantic vector = getArray(id);

        if(vector.isUndefined()) {
            MIPSFactory.returnRegister(index);
            return undefined();
        }

        if (!index.isInt()) {
            if(!index.isUndefined()) {
                System.err.println("[ERR_SEM_12] El tipus de l'index d'accés del vector no és SENCER");
            }
            index.setValue(0);
            index.setEstatic(true);
        }

        Semantic cell = new Semantic();
        cell.setType(vector.arrayType());
        cell.setEstatic(false);

        if (index.isEstatic()) {
            // TODO evaluate array bounds ( remember that upper bound is the last working cell of the array, not its dimension
            cell.setRegister(MIPSFactory.loadArrayCell(vector.offset(), index.intValue(), vector.isGlobal()));
        } else {
            cell.setRegister(MIPSFactory.validateAndLoadArrayCell(vector.offset(), index.reg(), vector.arrayLowerBound(),
                    vector.arrayUpperBound(), vector.isGlobal()));

        }

        return cell;
    }

    public void addNewFuncio(Funcio var) {
        System.out.println("FUNCIO: " + var.getNom());
        if (ts.obtenirBloc(CONTEXT_GLOBAL).obtenirProcediment(var.getNom()) == null) {
            ts.obtenirBloc(CONTEXT_GLOBAL).inserirProcediment(var);
            ts.obtenirBloc(ts.getBlocActual()).inserirProcediment(var);
        } else
            System.err.println("[ERR_SEM_3] Funció doblement definida");

    }

    public void deleteActualBlock() {
        ts.esborrarBloc(ts.getBlocActual());
        System.out.println("Deleted block: " + ts.getBlocActual());
        ts.setBlocActual(ts.getBlocActual() - 1);
        System.out.println("Blocks availble: " + ts.getBlocActual());
    }

    //TODO permanently remove this when validated the current logic does not need it.
    /*public Semantic identifyTerm(Semantic attr) {
        if (attr.getValue(TokenType.STRING) != null) {
            String aux = (String) attr.getValue(TokenType.STRING);
            TipusCadena string = new TipusCadena(aux, aux.length());
            attr.setValue("VARIABLE", string);
        } else if (attr.getValue(TokenType.INTEGER_CONSTANT) != null) {
            String aux = (String) attr.getValue(TokenType.INTEGER_CONSTANT);
            TipusSimple num = new TipusSimple(aux, 10000);
            attr.setValue("VARIABLE", num);
        } else if (attr.getValue(TokenType.LOGIC_CONSTANT) != null) {
            String aux = (String) attr.getValue(TokenType.LOGIC_CONSTANT);
            TipusSimple num = new TipusSimple(aux, 10000);
            attr.setValue("VARIABLE", num);
        }else if(attr.getValue(TokenType.IDENTIFIER) != null){
            String aux = (String) attr.getValue(TokenType.IDENTIFIER);
            Variable var =(Variable) ts.obtenirBloc(ts.getBlocActual()).obtenirVariable(aux);
            if(var!=null){
                attr.setValue("VARIABLE", var);
            }
        }

        MIPSFactory.writeString(MIPSFactory.TAG_LINEJUMP);

    }*/

    public void performReadOperation(LinkedList<Semantic> arguments) {

        for(Semantic argument : arguments) {
            switch(argument.typeId()) {
                case TIPUS_SIMPLE:
                    if(argument.isVectorIndexNonStatic()){
                        MIPSFactory.readInt(argument.reg(), argument.isGlobal());
                    } else {
                        MIPSFactory.readInt(argument.offset(), argument.isGlobal());
                    }
                    break;

                case TIPUS_LOGIC:
                    if(argument.isVectorIndexNonStatic()){
                        MIPSFactory.readBool(argument.reg(), argument.isGlobal());
                    } else {
                        MIPSFactory.readBool(argument.offset(), argument.isGlobal());
                    }
                    break;
            }
        }

    }

    public int opsValidation(Semantic attr) {
        if (attr.getValue(TokenType.COMPLEX_ARITHMETIC_OPERATOR) != null) {
            String aux = (String) attr.getValue(TokenType.COMPLEX_ARITHMETIC_OPERATOR);
            if (aux.equals("*")) {
                Object aTipus = attr.getValue("VARIABLE");
                if (aTipus instanceof TipusSimple)
                    return 1;

            } else if (aux.equals("/")) {
                Object aTipus = attr.getValue("VARIABLE");
                if (aTipus instanceof TipusSimple) {
                    return 1;
                }
            }
        } else if (attr.getValue(TokenType.SIMPLE_ARITHMETIC_OPERATOR) != null) {
            String aux = (String) attr.getValue(TokenType.SIMPLE_ARITHMETIC_OPERATOR);
            if (aux.equals("+")) {
                Object aTipus = attr.getValue("VARIABLE");
                if (aTipus instanceof TipusSimple)
                    return 1;

            } else if (aux.equals("-")) {
                Object aTipus = attr.getValue("VARIABLE");
                if (aTipus instanceof TipusSimple) {
                    return 1;
                }
            }
        }

        return 0;
    }

    public Semantic opsOperation(Semantic attr) {
        Object var1 = attr.getValue("VAR_LEFT");
        Object var2 = attr.getValue("VAR_RIGHT");
        if (var1 instanceof TipusSimple && var2 instanceof TipusSimple) {
            if ((((TipusSimple) var1).getNom().equals("cert") || ((TipusSimple) var1).getNom().equals("fals")) && (((TipusSimple) var2).getNom().equals("cert") || ((TipusSimple) var2).getNom().equals("fals"))) {
                System.out.print("Tipus logic");
            } else {
                String opb = (String) attr.getValue(TokenType.COMPLEX_ARITHMETIC_OPERATOR);
                String ops = (String) attr.getValue(TokenType.SIMPLE_ARITHMETIC_OPERATOR);
                Integer op = 0;
                if (opb != null) {
                    if (opb.equals("*")) {
                        op = Integer.parseInt(((TipusSimple) var1).getNom()) * Integer.parseInt(((TipusSimple) var2).getNom());

                    } else if (opb.equals("/")) {
                        if (Integer.parseInt(((TipusSimple) var2).getNom()) != 0) {
                            op = Integer.parseInt(((TipusSimple) var1).getNom()) / Integer.parseInt(((TipusSimple) var2).getNom());
                        } else {
                            //ERROR AL DIVIDIR
                        }
                    }
                } else if (ops != null) {
                    if (ops.equals("+")) {
                        op = Integer.parseInt(((TipusSimple) var1).getNom()) + Integer.parseInt(((TipusSimple) var2).getNom());
                    } else if (ops.equals("-")) {
                        op = Integer.parseInt(((TipusSimple) var1).getNom()) - Integer.parseInt(((TipusSimple) var2).getNom());
                    }
                }

                ((TipusSimple) var2).setNom(op.toString());
                attr.setValue("RESULT", var2);
            }
        }
        return attr;
    }


    public Semantic notFoundVar(Variable var, String id) {

        System.err.println("[ERR_SEM_8] L'identificador " + id + " no ha estat declarat");
        var.setNom(id);
        var.setTipus(new TipusIndefinit());
        ts.obtenirBloc(CONTEXT_GLOBAL).inserirVariable(var);
        //TODO change this for the proper error handling return
        //TODO make this return an undefined semantic if the varialbe is not found
        return new Semantic(var, ts.getBlocActual() == CONTEXT_GLOBAL);
    }

    public boolean isSimpleType(Object var) {
        return var instanceof TipusSimple;
    }

    public boolean isLogic(Object var) {
        if (var != null) {
            if (isSimpleType(var)) {
                if (((TipusSimple) var).getNom().equals("cert") || ((TipusSimple) var).getNom().equals("fals") || ((TipusSimple) var).getNom().equals("logic")) {
                    return true;
                }
            }
        }
        return false;
    }

    public Semantic updateParamTrace(Semantic attr, Semantic param){
        ArrayList<ITipus> paramList = (ArrayList) attr.getValue("auxList");
        if(paramList == null )return attr;
        paramList.add(param.type());
        attr.setValue("auxList",paramList);
        return attr;
    }

    public boolean validateRelationalOp(Semantic left_side_exp,Semantic right_side_exp){
        if(!left_side_exp.type().getNom().equals(TIPUS_SIMPLE) || !right_side_exp.type().getNom().equals(TIPUS_SIMPLE)){
            System.err.println("[ERR_SEM_5] El tipus de l'expressió no és SENCER");
            return false;
        }
        return true;
    }

    public void addParamVars(Funcio var) {
        for (int i = 0; i < var.getNumeroParametres(); i++) {
            if (var.obtenirParametre(i) != null) {
                ts.obtenirBloc(ts.getBlocActual()).inserirVariable(var.obtenirParametre(i));
            }
        }
    }

    public Semantic addParam(Semantic attr) {
        ArrayList<String> listParam = (ArrayList) attr.getValue("listParam");
        Parametre param = (Parametre) attr.getValue("param");
        Funcio aux = (Funcio) attr.getValue(TokenType.FUNCIO);
        if (listParam.contains(param.getNom())) {
            System.err.println("[ERR_SEM_4] Paràmetre " + param.getNom() + " doblement definit.");
        } else {
            listParam.add(param.getNom());
            attr.setValue("listParam", listParam);
            aux.inserirParametre(param);
            attr.setValue(TokenType.FUNCIO, aux);
        }
        return attr;
    }


    public Boolean isVar(String id) {

        Variable var = ts.obtenirBloc(ts.getBlocActual()).obtenirVariable(id);
        if (var == null) {
            var = ts.obtenirBloc(CONTEXT_GLOBAL).obtenirVariable(id);
            if (var == null) {
                return false;
            }
        }
        return true;
    }

    public void validateAssigment(Semantic leftPart, Semantic rightPart) {
        if (!isVar(leftPart.varName())) {
            System.err.println("[ERR_SEM_10] L'identificador [" + leftPart.varName() + "] en part esquerra d'assignació no és una variable");
        } else {
            if (!leftPart.type().getNom().equals(rightPart.type().getNom())) {
                System.err.println("[ERR_SEM_11] La variable [" + leftPart.varName() +
                        "] i l'expressió de assignació tenen tipus diferents. El tipus de la variable és [" + leftPart.type().getNom() +
                        "] i el de l'expressió és [" + rightPart.type().getNom() + "]");
            }
        }

    }

    public void performWriteOperation(LinkedList<Semantic> arguments) {

        for(Semantic argument : arguments) {
            switch( argument.typeId() ) {
                case TIPUS_SIMPLE:
                    if(argument.isEstatic()){
                        MIPSFactory.writeInt(argument.intValue());
                    } else {
                        MIPSFactory.writeInt(argument.reg());
                    }
                    break;
                case TIPUS_LOGIC:
                    if(argument.isEstatic()) {
                        MIPSFactory.writeString(argument.intValue() == MIPSFactory.FALS ? MIPSFactory.TAG_FALS : MIPSFactory.TAG_CERT);
                    } else {
                        MIPSFactory.writeBoolean(argument.reg());
                    }

                    break;
                case TIPUS_CADENA:
                    MIPSFactory.writeString(argument.tag());
                    break;
            }
        }

        MIPSFactory.writeString(MIPSFactory.TAG_LINEJUMP);

    }

    public void validateReturn(Semantic exp_result, Funcio actFunc) {
        if (actFunc == null) {
            System.err.println("[ERR_SEM_18] Retornar fora de funció");
        } else {
            if (!actFunc.getTipus().getNom().equals(exp_result.type().getNom())) {
                System.err.println("[ERR_SEM_17] La funcio [" + actFunc.getNom() + "] ha de ser del tipus [" + actFunc.getTipus().getNom() +
                        "] però en l'expressió del seu valor el tpus és [" + exp_result.type().getNom() + "]");
            }

        }

    }

    public void validateFuncio(Semantic attr){
        ArrayList<ITipus> paramList = (ArrayList) attr.getValue("auxList");
        Funcio func = (Funcio) attr.getValue(TokenType.FUNCIO);

        if(paramList.size()!= func.getNumeroParametres()){
            System.err.println("[ERR_SEM_14] La funció en declaració té "+func.getNumeroParametres()+" paràmetres mentre que en ús té "+paramList.size());
        }else{
            int i = 0;
            for(ITipus param:paramList){
                if(!param.getNom().equals(func.obtenirParametre(i).getTipus().getNom())){
                    System.err.println("[ERR_SEM_15] El tipus de paràmetre "+ i +" de la funció no coincideix amb el tipus en la seva declaració "+func.obtenirParametre(i).getTipus().getNom());
                }
                i++;
            }
        }
    }

}