package edu.salleurl.g6.ase;

import edu.salleurl.g6.gc.MIPSFactory;
import edu.salleurl.g6.model.TokenType;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;
import taulasimbols.*;

public class Ase {

    private static final int CONTEXT_GLOBAL = 0;
    public static final String TIPUS_SIMPLE = "SENCER";
    public static final String TIPUS_LOGIC = "LOGIC";
    public static final String CERT = "cert";
    public static final String FALS = "false";
    public static final boolean STORE = true;
    public static final boolean LOAD = false;

    private TaulaSimbols ts;

    public Ase() {
        this.ts = new TaulaSimbols();
    }


    public static String parseType(String lexem) {

        if(lexem.equals("sencer")) return TIPUS_SIMPLE;
        if(lexem.equals("logic")) return TIPUS_LOGIC;

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
            System.err.println("Existent constant! Value: " + var.getValor() + " Name: " + var.getNom());

    }

    public void addNewVar(Variable var) {
        if (ts.obtenirBloc(ts.getBlocActual()).obtenirConstant(var.getNom()) == null &&
                ts.obtenirBloc(ts.getBlocActual()).obtenirVariable(var.getNom()) == null)
            ts.obtenirBloc(ts.getBlocActual()).inserirVariable(var);
        else
            System.err.println("Existent variable! Type: " + var.getTipus().getNom() + " Name: " + var.getNom());


    }

    public Semantic getGlobalVariableOrConstant(String id) {

        Constant ct = ts.obtenirBloc(CONTEXT_GLOBAL).obtenirConstant(id);

        if(ct == null) {
            Variable var = ts.obtenirBloc(CONTEXT_GLOBAL).obtenirVariable(id);
            if(var == null) {
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

        if(ct == null) {
            Variable var = ts.obtenirBloc(ts.getBlocActual()).obtenirVariable(id);
            if(var == null) {
                Semantic s = getGlobalVariableOrConstant(id);
                if(s == null) {
                    //TODO error - non declaraded variable or constant

                    //TODO change this for the proper error handling return
                    return new Semantic();
                }

                return s;
            }

            return new Semantic(var, ts.getBlocActual() == CONTEXT_GLOBAL);
        }

        return new Semantic(ct, ts.getBlocActual() == CONTEXT_GLOBAL);
    }

    public Semantic getArray(String id) {
        Variable var = ts.obtenirBloc(ts.getBlocActual()).obtenirVariable(id);
        boolean isGlobal = false;
        if(var == null) {
            var = ts.obtenirBloc(CONTEXT_GLOBAL).obtenirVariable(id);
            if(var == null) {
                //TODO error - non declaraded variable or constant

                //TODO change this for the proper error handling return
                return new Semantic();
            }
            isGlobal = true;

        }

        if(var.getTipus() instanceof TipusArray) {
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
        cell.isVector(true);

        if(attr.isEstatic()) {
            // TODO evaluate array bounds ( remember that upper bound is the last working cell of the array, not its dimension )

            cell.setOffset(vector.offset() + attr.intValue() * vector.arrayType().getTamany());
        } else {
            cell.setRegister(MIPSFactory.validateAndGetArrayCellOffset(vector.offset(), attr.reg(), vector.arrayLowerBound(),
                    vector.arrayUpperBound()));

        }

        return cell;
    }


    public Semantic validateArrayAccessAndLoadCell(String id, Semantic attr) {

        //TODO validate it is actually an array
        Semantic vector = getArray(id);

        //TODO evaluate index type is SIMPLE

        Semantic cell = new Semantic();
        cell.setType(vector.arrayType());
        cell.setEstatic(false);

        if(attr.isEstatic()) {
            // TODO evaluate array bounds ( remember that upper bound is the last working cell of the array, not its dimension

            cell.setRegister(MIPSFactory.loadArrayCell(vector.offset(), attr.intValue(), vector.isGlobal()));
        } else {
            cell.setRegister(MIPSFactory.validateAndLoadArrayCell(vector.offset(), attr.reg(), vector.arrayLowerBound(),
                    vector.arrayUpperBound(), vector.isGlobal()));

        }

        return cell;
    }

    public void addNewFuncio(Funcio var) {
        System.out.println("FUNCIO: " + var.getNom());
        if (ts.obtenirBloc(ts.getBlocActual()).obtenirProcediment(var.getNom()) == null)
            ts.obtenirBloc(ts.getBlocActual()).inserirProcediment(var);
        else
            System.err.println("Existent function! Type: " + var.getTipus().getNom() + " Name: " + var.getNom());

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

        return attr;
    }*/

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
        }else if(attr.getValue(TokenType.SIMPLE_ARITHMETIC_OPERATOR) != null) {
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
                if(opb!=null) {
                    if (opb.equals("*")) {
                        op = Integer.parseInt(((TipusSimple) var1).getNom()) * Integer.parseInt(((TipusSimple) var2).getNom());

                    } else if (opb.equals("/")) {
                        if (Integer.parseInt(((TipusSimple) var2).getNom()) != 0) {
                            op = Integer.parseInt(((TipusSimple) var1).getNom()) / Integer.parseInt(((TipusSimple) var2).getNom());
                        } else {
                            //ERROR AL DIVIDIR
                        }
                    }
                }else if(ops!=null){
                    if(ops.equals("+")){
                        op = Integer.parseInt(((TipusSimple) var1).getNom()) + Integer.parseInt(((TipusSimple) var2).getNom());
                    }else if(ops.equals("-")){
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