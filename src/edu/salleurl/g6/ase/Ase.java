package edu.salleurl.g6.ase;

import edu.salleurl.g6.alex.Alex;
import edu.salleurl.g6.asi.Asi;
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
    public static final String TIPUS_ARRAY = "array";
    public static final String TIPUS_INDEFINIT = "TipusIndefinit";
    public static final String CERT = "cert";
    public static final String FALS = "fals";
    public static final boolean STORE = true;
    public static final boolean LOAD = false;

    private TaulaSimbols ts;

    public Ase() {
        this.ts = new TaulaSimbols();
    }

    private void log(String text) {
        System.err.println(text);
        Alex.getLog().writeError(text);
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
        if (!existConst(var.getNom()))
            ts.obtenirBloc(ts.getBlocActual()).inserirConstant(var);
        else
            log("[ERR_SEM_1] "+Alex.getLine()+", Constant [" + var.getNom() + "] doblement definida");

    }

    public Boolean existConst(String id) {
        /*boolean find = false;
        if (ts.getBlocActual() == 1) {
            find = ts.obtenirBloc(ts.getBlocActual()).obtenirConstant(id) != null;
        }
        if(!find){
            find = ts.obtenirBloc(CONTEXT_GLOBAL).obtenirConstant(id) != null;
        }
        return find;*/
        return ts.obtenirBloc(ts.getBlocActual()).obtenirConstant(id) != null;
    }

    public Boolean existVar(String id) {
        /*boolean find = false;
        if (ts.getBlocActual() == 1) {
            find = ts.obtenirBloc(ts.getBlocActual()).obtenirVariable(id) != null ;
        }
        if(!find){
            find = ts.obtenirBloc(CONTEXT_GLOBAL).obtenirVariable(id) != null;
        }
        return find;*/
        return ts.obtenirBloc(ts.getBlocActual()).obtenirVariable(id) != null;
    }

    public Boolean existFuncio(String id) {
        return ts.obtenirBloc(CONTEXT_GLOBAL).obtenirProcediment(id) != null;
    }


    public void addNewVar(Variable var) {
        if (!existConst(var.getNom()) ){
            if(!existVar(var.getNom())){
                //if(!existFuncio(var.getNom())){
                    ts.obtenirBloc(ts.getBlocActual()).inserirVariable(var);
                //}else
                  //  log("[ERR_SEM_X] "+Alex.getLine()+", Identificador [" + var.getNom() + "] utilitzat previament per una funcio");
            }else
                log("[ERR_SEM_2] "+Alex.getLine()+ ", Variable [" + var.getNom() + "] doblement definida");
        } else
            log("[ERR_SEM_20] "+Alex.getLine()+", Identificador [" + var.getNom() + "] utilitzat previament per una constant");


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

            Semantic getVar =new Semantic(var, ts.getBlocActual() == CONTEXT_GLOBAL);
            getVar.setIsVar(true);
            return getVar;
        }

        Semantic getConst =new Semantic(ct, ts.getBlocActual() == CONTEXT_GLOBAL);
        getConst.setIsConst(true);
        return getConst;
    }

    public Semantic getVariableOrConstant(String id) {

        Constant ct = ts.obtenirBloc(ts.getBlocActual()).obtenirConstant(id);

        if (ct == null) {
            Variable var = ts.obtenirBloc(ts.getBlocActual()).obtenirVariable(id);
            if (var == null) {
                Semantic s = getGlobalVariableOrConstant(id);
                if (s == null) {
                    return notFoundVar(id);
                }
                return s;
            }
            Semantic getVar =new Semantic(var, ts.getBlocActual() == CONTEXT_GLOBAL);
            getVar.setIsVar(true);
            return getVar;
        }

        Semantic getConst =new Semantic(ct, ts.getBlocActual() == CONTEXT_GLOBAL);
        getConst.setIsConst(true);
        return getConst;
    }

    public Semantic getArray(String id) {
        Variable var = ts.obtenirBloc(ts.getBlocActual()).obtenirVariable(id);
        boolean isGlobal = ts.getBlocActual() == CONTEXT_GLOBAL;
        if (var == null) {
            var = ts.obtenirBloc(CONTEXT_GLOBAL).obtenirVariable(id);
            if (var == null) {
                return notFoundVar(id);
            }
            isGlobal = true;
        }
        if (var.getTipus() instanceof TipusArray) {
            return new Semantic(var, isGlobal);
        }
        log("[ERR_SEM_21] "+Alex.getLine()+", La variable " + id + " no es de tipus array");
        var = new Variable(id, new TipusIndefinit(), 4);
        return new Semantic(var, isGlobal);
    }

    public boolean validateArrayBounds(Semantic var, Semantic index) {
        if (index.intValue() <= var.arrayUpperBound() &&
                index.intValue() >= var.arrayLowerBound()) {
            return true;
        }
        log("[ERR_SEM_22] "+Alex.getLine()+", L'index ["+index.intValue()+"] esta fora dels límits del vector, entre ["+var.arrayLowerBound()+","+var.arrayUpperBound()+"]");
        return false;
    }

    public Semantic validateArrayAccessAndGetOffset(String id, Semantic index) {

        //TODO validate it is actually an array
        Semantic vector = getArray(id);

        // if vector is undefined ergo, not found, then return an undefined Semantic
        if (vector.isUndefined()) {
            MIPSFactory.returnRegister(index);
            return undefined();
        }

        Semantic cell = new Semantic();
        cell.setType(vector.arrayType());
        cell.setEstatic(false);
        cell.setGlobal(vector.isGlobal());

        //if index is not int then set index to 0 and static
        if (!index.isInt()) {
            if (!index.isUndefined()) {
                log("[ERR_SEM_12] "+Alex.getLine()+", El tipus de l'index d'accés del vector no és SENCER");
            }

            index.setValue(0);
            index.setEstatic(true);
        }

        if (index.isEstatic()) {
            // TODO evaluate array bounds ( remember that upper bound is the last working cell of the array, not its dimension )
            cell.isVectorIndexNonStatic(false);
            cell.setOffset(validateArrayBounds(vector, index) ?
                    vector.offset() + index.intValue() * vector.arrayType().getTamany() : vector.offset());

        } else {
            cell.isVectorIndexNonStatic(true);
            cell.setRegister(MIPSFactory.validateAndGetArrayCellOffset(vector.offset(), index.reg(), vector.arrayLowerBound(),
                    vector.arrayUpperBound()));

        }
        cell.setIsVar(true);
        return cell;

    }

    public int computeParametersTotalSize(Funcio func) {

        int size = 0;
        for(int i = 0 ; i < func.getNumeroParametres() ; i++) {
            size += func.obtenirParametre(i).getTipus().getTamany();
        }

        return size;
    }

    public void stackParameters(LinkedList<Semantic> parameter_values) {

        for (Semantic parameter : parameter_values) {

            switch(parameter.typeId()) {
                case Ase.TIPUS_SIMPLE:
                case Ase.TIPUS_LOGIC:
                    if(parameter.isEstatic()) {
                        //TODO validate it is not reference :D
                        MIPSFactory.stackParameter(parameter.intValue(), parameter.addressOffset());
                    } else {
                        if(parameter.isRef()){
                            if(parameter.isVectorIndexNonStatic()) {
                                MIPSFactory.stackParameter(parameter.offsetRegister(), parameter.addressOffset());
                            } else {
                                MIPSFactory.stackParameter(parameter.offset(), parameter.addressOffset());
                            }
                        } else {
                            MIPSFactory.stackParameter(parameter.reg(), parameter.addressOffset());
                        }
                    }
                    break;

                case Ase.TIPUS_ARRAY:
                    if(parameter.isRef()) {
                        MIPSFactory.stackParameter(parameter.offset(), parameter.addressOffset());
                    } else {
                        for(int i = 0; i <= parameter.arrayUpperBound(); i++) {
                            MIPSFactory.stackArrayCell(parameter.offset(), i, parameter.isGlobal(), parameter.addressOffset());
                        }
                    }
                    break;
            }
        }
    }

    public Semantic undefined() {
        Semantic undefined = new Semantic();
        undefined.setType(new TipusIndefinit());
        undefined.setEstatic(true);
        undefined.setIsVar(false);
        return undefined;
    }

    public Semantic validateArrayAccessAndLoadCellAndOffset(String id, Semantic index) {

        Semantic vector = getArray(id);
        if (vector.isUndefined()) {
            MIPSFactory.returnRegister(index);
            return undefined();
        }

        Semantic cell = new Semantic();
        cell.setType(vector.arrayType());
        cell.setEstatic(false);

        if (!index.isInt()) {
            if (!index.isUndefined()) {
                log("[ERR_SEM_12] "+Alex.getLine()+", El tipus de l'index d'accés del vector no és SENCER");
            }
            index.setValue(0);
            index.setEstatic(true);
        }

        if (index.isEstatic()) {
            cell.isVectorIndexNonStatic(false);
            cell.setOffset(validateArrayBounds(vector, index) ?
                    vector.offset() + index.intValue() * vector.arrayType().getTamany() : vector.offset());
            cell.setRegister(MIPSFactory.loadArrayCell(vector.offset(), validateArrayBounds(vector, index) ? index.intValue() : 0, vector.isGlobal()));
        } else {
            cell.isVectorIndexNonStatic(true);
            cell.merge(MIPSFactory.validateAndGetOffsetPlusLoadArrayCell(vector.offset(), index.reg(), vector.arrayLowerBound(),
                    vector.arrayUpperBound(), vector.isGlobal()));
        }
        return cell;
    }

    public Funcio addNewFuncio(Funcio var) {
        //System.out.println("FUNCIO: " + var.getNom());

        if (!existFuncio(var.getNom())) {
            if (!existVar(var.getNom()) && !existConst(var.getNom())) {
                ts.obtenirBloc(CONTEXT_GLOBAL).inserirProcediment(var);
                ts.obtenirBloc(ts.getBlocActual()).inserirProcediment(var);
                return var;
            } else
                log("[ERR_SEM_23] "+Alex.getLine()+", Identificador utilitzat previament per una variable o constant");

        } else
            log("[ERR_SEM_3] "+Alex.getLine()+", Funció doblement definida");

        int i = 0;
        while (existFuncio(var.getNom() + "_" + i) && existVar(var.getNom() + "_" + i)
                && existConst(var.getNom() + "_" + i))
            i++;
        var.setNom(var.getNom() + "_" + i);
        return var;
    }

    public void deleteActualBlock() {
        ts.esborrarBloc(ts.getBlocActual());
        //System.out.println("Deleted block: " + ts.getBlocActual());
        ts.setBlocActual(ts.getBlocActual() - 1);
        //System.out.println("Blocks availble: " + ts.getBlocActual());
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

        for (Semantic argument : arguments) {
            if(argument.isConst() == null ) {
                switch (argument.typeId()) {
                    case TIPUS_SIMPLE:
                        if (argument.isVectorIndexNonStatic()) {
                            MIPSFactory.readInt(argument.reg(), argument.isGlobal());
                        } else {
                            MIPSFactory.readInt(argument.offset(), argument.isGlobal());
                        }
                        break;

                    case TIPUS_LOGIC:
                        if (argument.isVectorIndexNonStatic()) {
                            MIPSFactory.readBool(argument.reg(), argument.isGlobal());
                        } else {
                            MIPSFactory.readBool(argument.offset(), argument.isGlobal());
                        }
                        break;
                    default:
                        log("[ERR_SEM_24] "+Alex.getLine()+", El tipus de la expressió en LLEGIR no és simple o no és logic");
                }
            }else{
                log("[ERR_SEM_25] "+Alex.getLine()+", El tipus de la expressió en LLEGIR es una constant");
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
                //System.out.print("Tipus logic");
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


    public Semantic notFoundVar(String id) {

        log("[ERR_SEM_8] "+Alex.getLine()+", L'identificador " + id + " no ha estat declarat");
        Alex.getLog().writeError("[ERR_SEM_8] "+Alex.getLine()+", L'identificador " + id + " no ha estat declarat");
        Variable var = new Variable(id, new TipusIndefinit(), 4);
        ts.obtenirBloc(CONTEXT_GLOBAL).inserirVariable(var);

        Semantic getVar =new Semantic(var, ts.getBlocActual() == CONTEXT_GLOBAL);
        getVar.setIsVar(false);
        getVar.setEstatic(true);
        return getVar;
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

    public void isLogicExp(Semantic condition) {
        //TODO WARNINGS PER BUCLES INFINITS
        if(!condition.isUndefined()) {
            if (!condition.type().getNom().equals(TIPUS_LOGIC)) {
                //log("[ERR_SEM_6] El tipus de l'expressió no és LOGIC");
                log("[ERR_SEM_7] " + Alex.getLine() + ", La condició no es de tipus LOGIC");
            }
        }else{
            log("[ERR_SEM_7] " + Alex.getLine() + ", La condició no es de tipus LOGIC");
        }

    }


    public Semantic updateParamTrace(Semantic attr, Semantic param) {
        ArrayList<ITipus> paramList = (ArrayList) attr.getValue("auxList");
        if (paramList == null) return attr;
        paramList.add(param.type());
        attr.setValue("auxList", paramList);
        return attr;
    }

    public boolean validateRelationalOp(Semantic left_side_exp, Semantic right_side_exp) {

        if (!left_side_exp.type().getNom().equals(TIPUS_SIMPLE) || !right_side_exp.type().getNom().equals(TIPUS_SIMPLE)) {
            log("[ERR_SEM_5] "+Alex.getLine()+", El tipus de l'expressió no és SENCER");
            return false;
        }
        return true;
    }

    /*public void addParamVars(Funcio var) {
        for (int i = 0; i < var.getNumeroParametres(); i++) {
            if (var.obtenirParametre(i) != null) {
                ts.obtenirBloc(ts.getBlocActual()).inserirVariable(var.obtenirParametre(i));
            }
        }
    }*/

    public void addParam(Parametre param, int n_param) {

        //TODO check if current parameter is already a defined symbol in currrent context
        if (false) {
            System.err.println("[ERR_SEM_4] Paràmetre " + param.getNom() + " doblement definit.");
        } else {
            if (!validateTipusPas(param, n_param)) {
                param.setTipusPasParametre(TipusPasParametre.VALOR);
            }

            addNewVar(param); //TODO SAMU he fet que aquesta funcio faci el que feia addparamvars aixi que he cridat aixo. verify
        }
    }


    public Boolean isVar(String id) {

        Variable var = ts.obtenirBloc(ts.getBlocActual()).obtenirVariable(id);
        if (var == null) {
            var = ts.obtenirBloc(CONTEXT_GLOBAL).obtenirVariable(id);
            if (var == null) {
                Funcio func =(Funcio) ts.obtenirBloc(CONTEXT_GLOBAL).obtenirProcediment(id);
                if(func == null){
                    log("[ERR_SEM_10] "+Alex.getLine()+", L'identificador [" + id + "] en part esquerra d'assignació no és una variable");
                }else{
                    log("[ERR_SEM_26] "+Alex.getLine()+", L'identificador [" + id + "] en part esquerra d'assignació és una funcio");
                }

                return false;
            }
        }
        return true;
    }

    public boolean validateAssigment(Semantic leftPart, Semantic rightPart) {
        //leftPart.setIsVar(isVar(leftPart.varName()));
        if(leftPart.isTipusIndefinit() || rightPart.isTipusIndefinit()) return  false;
            leftPart.setIsVar(isVar(leftPart.varName()));
            if(leftPart.isVar()) {

                if (!leftPart.isSameTypeTo(rightPart)) {
                    log("[ERR_SEM_11] "+Alex.getLine()+", La variable [" + leftPart.varName() +
                            "] i l'expressió de assignació tenen tipus diferents. El tipus de la variable és [" + leftPart.typeName() +
                            "] i el de l'expressió és [" + rightPart.typeName() + "]");
                    return false;
                }
                return true;
            }
        return false;
    }

    public void performWriteOperation(LinkedList<Semantic> arguments) {

        for (Semantic argument : arguments) {
            switch (argument.typeId()) {
                case TIPUS_SIMPLE:
                    if (argument.isEstatic()) {
                        MIPSFactory.writeInt(argument.intValue());
                    } else {
                        MIPSFactory.writeInt(argument.reg());
                    }
                    break;
                case TIPUS_LOGIC:
                    if (argument.isEstatic()) {
                        MIPSFactory.writeString(argument.intValue() == MIPSFactory.FALS ? MIPSFactory.TAG_FALS : MIPSFactory.TAG_CERT);
                    } else {
                        MIPSFactory.writeBoolean(argument.reg());
                    }
                    break;
                case TIPUS_CADENA:
                    MIPSFactory.writeString(argument.tag());
                    break;
                case Ase.TIPUS_INDEFINIT:
                    break;
                default:
                    log("[ERR_SEM_13] "+Alex.getLine()+", El tipus de la expressió en ESCRIURE no és simple o no és una constant cadena");
            }
        }

        MIPSFactory.writeString(MIPSFactory.TAG_LINEJUMP);

    }

    public void validateReturn(Semantic exp_result, Funcio actFunc) {
        if (actFunc == null) {
            log("[ERR_SEM_18] "+Alex.getLine()+", Retornar fora de funció");
        } else {
            if (!actFunc.getTipus().getNom().equals(exp_result.type().getNom())) {
                log("[ERR_SEM_17] "+Alex.getLine()+", La funcio [" + actFunc.getNom() + "] ha de ser del tipus [" + actFunc.getTipus().getNom() +
                        "] però en l'expressió del seu valor el tpus és [" + exp_result.type().getNom() + "]");
            }

        }

    }

    public boolean validateFuncio(LinkedList<Semantic> parameters, Funcio actFunc) {
        if (!parameters.isEmpty() && actFunc != null) {

            if (parameters.size() != actFunc.getNumeroParametres()) {
                log("[ERR_SEM_14] "+Alex.getLine()+", La funció en declaració té " + actFunc.getNumeroParametres() +
                        " paràmetres mentre que en ús té " + parameters.size());
                return false;
            } else {
                int i = 0;
                for (Semantic param : parameters) {
                    String type =  param.typeName();
                    String  tipusNom = getTypeName(actFunc.obtenirParametre(i).getTipus());
                    if(param.isArray() && actFunc.obtenirParametre(i).getTipus() instanceof  TipusArray){
                        if(param.arrayUpperBound()!= (int) ((TipusArray) actFunc.obtenirParametre(i).getTipus()).obtenirDimensio(0).getLimitSuperior()){
                            log("[ERR_SEM_30] "+Alex.getLine()+", La mida del vector, paramàtre " + (i+1) +
                                    " de la funció no coincideix amb la dimensió de la seva declaració " +
                                    (int) ((TipusArray) actFunc.obtenirParametre(i).getTipus()).obtenirDimensio(0).getLimitSuperior());
                            return false;
                        }
                    }else{
                        if(!type.equals(tipusNom)){
                            log("[ERR_SEM_15] "+Alex.getLine()+", El tipus de paràmetre " + (i+1) +
                                    " de la funció no coincideix amb el tipus en la seva declaració "+ actFunc.obtenirParametre(i).getTipus().getNom());
                            return false;
                        }

                    }
                    /*if (!type.equals(tipusNom)) {

                        System.err.println("[ERR_SEM_15] El tipus de paràmetre " + (i+1) +
                                " de la funció no coincideix amb el tipus en la seva declaració " +
                                tipusNom);
                    }
                    if(param.isArray() && actFunc.obtenirParametre(i).getTipus() instanceof TipusArray){
                        if(param.arrayUpperBound()!= (int) ((TipusArray) actFunc.obtenirParametre(i).getTipus()).obtenirDimensio(0).getLimitSuperior()){
                            System.err.println("[ERR_SEM_X] La mida del vector, paramàtre " + (i+1) +
                                    " de la funció no coincideix amb la dimensió de la seva declaració " +
                                    tipusNom);
                        }
                    }else{
                        System.err.println("[ERR_SEM_15] El tipus de paràmetre " + (i+1) +
                                " de la funció no coincideix amb el tipus en la seva declaració "+ actFunc.obtenirParametre(i).getTipus().getNom());
                    }*/
                    i--;
                }
            }
        }
        return true;
    }

    public Semantic validationConst(Semantic exp_result) {
        if(!exp_result.isTipusIndefinit()) {
            if (!exp_result.isEstatic()) {
                //ERR_SEM_19
                log("[ERR_SEM_19]" + Alex.getLine() + ", Expressio no és estàtica");
                exp_result.setType(new TipusIndefinit());
                exp_result.setEstatic(true);
            } else {
                if (exp_result.isArray()) {
                    log("[ERR_SEM_27] " + Alex.getLine() + ", Constant no pot ser tipus vector");
                    exp_result.setType(new TipusIndefinit());
                    exp_result.setEstatic(true);
                } else if (exp_result.type() instanceof TipusCadena) {

                }
            }
        }else{
            log("[ERR_SEM_28]" + Alex.getLine() + ", Constant mal definida");
        }
        return exp_result;
    }

    public boolean validateTipusPas(Parametre param, int n_param) {
        if (param.getTipusPasParametre() == TipusPasParametre.REFERENCIA) {
            if (!(param.getTipus() instanceof TipusCadena)) {
                log("[ERR_SEM_16] "+Alex.getLine()+", El paràmetre " + n_param + " de la funció no es pot passar per referència");
                return false;
            }
        }
        return true;
    }

    public Funcio getFuncio(String id) {
        Funcio func = (Funcio) ts.obtenirBloc(CONTEXT_GLOBAL).obtenirProcediment(id);
        if (func == null) {
            func = new Funcio(id, new TipusIndefinit());
            log("[ERR_SEM_29] "+Alex.getLine()+", Funció no declarada previament");
        }
        return func;
    }


    public String getTypeName(ITipus tipus){
        if (tipus instanceof TipusSimple) {
            return tipus.getNom();
        } else if (tipus instanceof TipusCadena) {
            return tipus.getNom();
        }else if(tipus instanceof TipusArray){
            return ((TipusArray) tipus).getTipusElements().getNom();
        }
        return "THIS_SHOULD_NEVER_HAPPEN";
    }
}