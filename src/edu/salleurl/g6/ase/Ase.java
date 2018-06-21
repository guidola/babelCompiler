package edu.salleurl.g6.ase;

import edu.salleurl.g6.gc.MIPSFactory;
import edu.salleurl.g6.model.TokenType;
import taulasimbols.*;

public class Ase {

    private static final int CONTEXT_GLOBAL = 0;
    public static final String TIPUS_SIMPLE = "SENCER";
    public static final String TIPUS_LOGIC = "LOGIC";
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

        //TODO handle case where trying to access variable which is not a vector
        return new Semantic();
    }

    public Semantic validateArrayAccessAndGetOffset(String id, Semantic attr) {

        //TODO validate it is actually an array
        Semantic vector = getArray(id);

        //TODO evaluate index type is SIMPLE

        Semantic cell = new Semantic();
        cell.setType(vector.arrayType());
        cell.setEstatic(false);
        cell.setGlobal(vector.isGlobal());

        if (!attr.type().getNom().equals(TIPUS_SIMPLE)) {
            System.err.println("[ERR_SEM_12] El tipus de l'index d'accés del vector no és SENCER");
        }
        if (attr.isEstatic()) {
            // TODO evaluate array bounds ( remember that upper bound is the last working cell of the array, not its dimension )
            cell.isVectorIndexNonStatic(false);
            cell.setOffset(vector.offset() + attr.intValue() * vector.arrayType().getTamany());
        } else {
            cell.isVectorIndexNonStatic(true);
            cell.setRegister(MIPSFactory.validateAndGetArrayCellOffset(vector.offset(), attr.reg(), vector.arrayLowerBound(),
                    vector.arrayUpperBound()));

        }

        return cell;
    }


    public Semantic validateArrayAccessAndLoadCell(String id, Semantic expression) {

        //TODO validate it is actually an array
        Semantic vector = getArray(id);

        //TODO evaluate index type is SIMPLE

        Semantic cell = new Semantic();
        cell.setType(vector.arrayType());
        cell.setEstatic(false);

        if (expression.isEstatic()) {
            // TODO evaluate array bounds ( remember that upper bound is the last working cell of the array, not its dimension

            cell.setRegister(MIPSFactory.loadArrayCell(vector.offset(), expression.intValue(), vector.isGlobal()));
        } else {
            cell.setRegister(MIPSFactory.validateAndLoadArrayCell(vector.offset(), expression.reg(), vector.arrayLowerBound(),
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

    }

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
}