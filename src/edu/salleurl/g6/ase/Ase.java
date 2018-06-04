package edu.salleurl.g6.ase;

import edu.salleurl.g6.model.TokenType;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;
import taulasimbols.*;

public class Ase {
    private TaulaSimbols ts;

    public Ase() {
        this.ts = new TaulaSimbols();
    }

    public void addNewBlock() {
        ts.inserirBloc(new Bloc());
        ts.setBlocActual(ts.getNumeroBlocs() - 1);
    }

    public void addNewConstant(Constant var) {
        if (ts.obtenirBloc(ts.getBlocActual()).obtenirConstant(var.getNom()) == null)
            ts.obtenirBloc(ts.getBlocActual()).inserirConstant(var);
        else
            System.err.println("Existent constant! Value: " + var.getValor() + " Name: " + var.getNom());
    }

    public void addNewVar(Variable var) {
        if (ts.obtenirBloc(ts.getBlocActual()).obtenirConstant(var.getNom()) == null){
            //ts.obtenirBloc(ts.getBlocActual()).inserirVariable(var);
            if(ts.obtenirBloc(ts.getBlocActual()).obtenirVariable(var.getNom()) == null){
                ts.obtenirBloc(ts.getBlocActual()).inserirVariable(var);
            }else{
                System.err.println("Existent variable! Type: " + var.getTipus().getNom() + " Name: " + var.getNom());
            }
        }else{
            System.err.println("Variable already defined like constant! Type: " + var.getTipus().getNom() + " Name: " + var.getNom());
        }
    }

    public void addNewFuncio(Funcio var) {
        System.out.println("FUNCIO: " + var.getNom());
        if (ts.obtenirBloc(0).obtenirProcediment(var.getNom()) == null) {
            ts.obtenirBloc(0).inserirProcediment(var);
            ts.obtenirBloc(ts.getBlocActual()).inserirProcediment(var);
        } else
            System.err.println("Existent function! Type: " + var.getTipus().getNom() + " Name: " + var.getNom());

    }

    public void addParamVars(Funcio var) {
        for (int i = 0; i < var.getNumeroParametres(); i++) {
            if (var.obtenirParametre(i) != null) {
                ts.obtenirBloc(ts.getBlocActual()).inserirVariable(var.obtenirParametre(i));
            }
        }
    }

    public void deleteActualBlock() {
        ts.esborrarBloc(ts.getBlocActual());
        System.out.println("Deleted block: " + ts.getBlocActual());
        ts.setBlocActual(ts.getBlocActual() - 1);
        System.out.println("Blocks availble: " + ts.getBlocActual());
    }

    public Semantic identifyTerm(Semantic attr) {
        if (attr.getValue(TokenType.STRING) != null) {
            String aux = (String) attr.getValue(TokenType.STRING);
            attr.removeAttribute(TokenType.STRING);
            TipusCadena string = new TipusCadena(aux, aux.length());
            attr.setValue("VARIABLE", string);

        } else if (attr.getValue(TokenType.INTEGER_CONSTANT) != null) {
            String aux = (String) attr.getValue(TokenType.INTEGER_CONSTANT);
            attr.removeAttribute(TokenType.INTEGER_CONSTANT);
            TipusSimple num = new TipusSimple(aux, 10000);
            attr.setValue("VARIABLE", num);

        } else if (attr.getValue(TokenType.LOGIC_CONSTANT) != null) {
            String aux = (String) attr.getValue(TokenType.LOGIC_CONSTANT);
            attr.removeAttribute(TokenType.LOGIC_CONSTANT);
            TipusSimple num = new TipusSimple(aux, 10000);
            attr.setValue("VARIABLE", num);

        } else if (attr.getValue(TokenType.IDENTIFIER) != null) {
            String aux = (String) attr.getValue(TokenType.IDENTIFIER);
            attr.removeAttribute(TokenType.IDENTIFIER);
            Variable var = (Variable) ts.obtenirBloc(ts.getBlocActual() == 1 ? 1 : 0).obtenirVariable(aux.toLowerCase());
            if (var != null) {
                attr.setValue("VARIABLE", var);
            }

        }

        return attr;
    }

    public int opsValidation(Semantic attr) {
        Object aTipus = null;

        if (attr.getValue(TokenType.COMPLEX_ARITHMETIC_OPERATOR) != null) {
            String aux = (String) attr.getValue(TokenType.COMPLEX_ARITHMETIC_OPERATOR);
            if (aux.equals("*") || aux.equals("/")) {
                aTipus = attr.getValue("VARIABLE");

            }
        } else if (attr.getValue(TokenType.SIMPLE_ARITHMETIC_OPERATOR) != null) {
            String aux = (String) attr.getValue(TokenType.SIMPLE_ARITHMETIC_OPERATOR);
            if (aux.equals("+") || aux.equals("-")) {
                aTipus = attr.getValue("VARIABLE");

            }
        } else if (attr.getValue(TokenType.RELATIONAL_OPERATOR) != null) {
            String aux = (String) attr.getValue(TokenType.RELATIONAL_OPERATOR);
            if (aux.equals("==") || aux.equals(">=") || aux.equals("<=") || aux.equals(">") || aux.equals("<")||aux.equals("!=")) {
                aTipus = attr.getValue("VARIABLE");
            }
        } else if (attr.getValue(TokenType.AND) != null) {
            aTipus = attr.getValue("VARIABLE");
            if(aTipus instanceof Variable) aTipus = ((Variable) aTipus).getTipus();

        } else if (attr.getValue(TokenType.OR) != null) {
            aTipus = attr.getValue("VARIABLE");
            if(aTipus instanceof Variable) aTipus = ((Variable) aTipus).getTipus();

        } else if (attr.getValue(TokenType.NOT) != null) {
            aTipus = attr.getValue("VARIABLE");
            if(aTipus instanceof Variable) aTipus = ((Variable) aTipus).getTipus();
        }
        if(aTipus instanceof  Parametre){
            if(((Parametre) aTipus).getTipus() instanceof TipusSimple){
                return 1;
            }
        }else{
            if(aTipus instanceof TipusSimple){
                return 1;
            }
        }
        return 0;
    }
    public TipusSimple initVar(TipusSimple var){
        if(var.getNom().equals("sencer")){
            var.setNom("0");
        }else if(var.getNom().equals("logic")){
            var.setNom("cert");
        }

        return var;
    }

    public Semantic opuOperation(Semantic attr){
        if(attr.getValue("OPU")!=null){
            if(attr.getValue("OPU").equals("not")){

                Object var1 = attr.getValue("VARIABLE");
                if(var1 instanceof Parametre){
                    var1 = ((Parametre) var1).getTipus();
                }
                var1 = initVar((TipusSimple) var1);
                if(isLogic(var1)){
                    if (((TipusSimple) var1).getNom().equals("cert")) {
                        ((TipusSimple) var1).setNom("fals");
                    } else {
                        ((TipusSimple) var1).setNom("cert");
                    }
                }else{
                    if (((TipusSimple) var1).getNom().equals("1")) {
                        ((TipusSimple) var1).setNom("0");
                    } else if(((TipusSimple) var1).getNom().equals("0")){
                        ((TipusSimple) var1).setNom("1");
                    }else{
                        System.err.println("Incorrect use of not. Using invalid integer value");
                        return attr;
                    }
                }
                attr.setValue("VARIABLE",var1);
                attr.setValue("RESULT",var1);
            }else if(attr.getValue("OPU").equals("-")){
                Object var1 = attr.getValue("VARIABLE");
                var1 = initVar((TipusSimple) var1);
                int ops;

                    if (!isLogic(var1)) {
                        ops = 0 - Integer.parseInt(((TipusSimple) var1).getNom());
                        ((TipusSimple) var1).setNom(Integer.toString(ops));
                    }
                attr.setValue("VARIABLE",var1);
                attr.setValue("RESULT",var1);
            }
        }
        return attr;
    }

    public Semantic opsOperation(Semantic attr) {
        Object var1 = attr.getValue("VAR_LEFT");
        Object var2 = attr.getValue("VAR_RIGHT");

        if(var1 instanceof Parametre){
            var1 = ((Parametre) var1).getTipus();
        }else{
            if(var2 instanceof Parametre){
                var2 = ((Parametre) var2).getTipus();
            }
        }
        if (isSimpleType(var1) && isSimpleType(var2)) {
            var1 = initVar((TipusSimple) var1);
            var2 = initVar((TipusSimple)var2);
            if (isLogic(var1) && isLogic(var2)) {
                //if ((((TipusSimple) var1).getNom().equals("cert") || ((TipusSimple) var1).getNom().equals("fals")) && (((TipusSimple) var2).getNom().equals("cert") || ((TipusSimple) var2).getNom().equals("fals"))) {
                if (attr.getValue(TokenType.OR) != null) {
                    if ((((TipusSimple) var1).getNom().equals("fals")) && ((TipusSimple) var2).getNom().equals("fals")) {
                        ((TipusSimple) var2).setNom("fals");
                    } else {
                        ((TipusSimple) var2).setNom("cert");
                    }
                    attr.removeAttribute(TokenType.OR);
                } else if (attr.getValue(TokenType.AND) != null) {
                    if ((((TipusSimple) var1).getNom().equals("cert")) && ((TipusSimple) var2).getNom().equals("cert")) {
                        ((TipusSimple) var2).setNom("cert");
                    } else {
                        ((TipusSimple) var2).setNom("fals");
                    }
                    attr.removeAttribute(TokenType.AND);
                } /*else if (attr.getValue(TokenType.NOT) != null) {
                    //TODO NOMES TE VAR_LEFT
                    if (((TipusSimple) var1).getNom().equals("cert")) {
                        ((TipusSimple) var1).setNom("fals");
                    } else {
                        ((TipusSimple) var1).setNom("cert");
                    }
                }*/

                attr.setValue("RESULT", var2);
                attr.setValue("VAR_LEFT", var2);
                attr.removeAttribute("VARIABLE");
            } else {
                String opb = (String) attr.getValue(TokenType.COMPLEX_ARITHMETIC_OPERATOR);
                String ops = (String) attr.getValue(TokenType.SIMPLE_ARITHMETIC_OPERATOR);
                String opr = (String) attr.getValue(TokenType.RELATIONAL_OPERATOR);
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
                } else if (opr != null) {
                    if (opr.equals("==")) {
                        if (((TipusSimple) var1).getNom().equals(((TipusSimple) var2).getNom())) {
                            ((TipusSimple) var2).setNom("cert");
                        } else {
                            ((TipusSimple) var2).setNom("fals");
                        }
                    } else if (opr.equals(">=")) {

                        if (Integer.parseInt(((TipusSimple) var1).getNom()) >= Integer.parseInt(((TipusSimple) var2).getNom())) {
                            ((TipusSimple) var2).setNom("cert");
                        } else {
                            ((TipusSimple) var2).setNom("fals");
                        }
                    } else if (opr.equals("<=")) {

                        if (Integer.parseInt(((TipusSimple) var1).getNom()) <= Integer.parseInt(((TipusSimple) var2).getNom())) {
                            ((TipusSimple) var2).setNom("cert");
                        } else {
                            ((TipusSimple) var2).setNom("fals");
                        }
                    } else if (opr.equals(">")) {

                        if (Integer.parseInt(((TipusSimple) var1).getNom()) > Integer.parseInt(((TipusSimple) var2).getNom())) {
                            ((TipusSimple) var2).setNom("cert");
                        } else {
                            ((TipusSimple) var2).setNom("fals");
                        }
                    } else if (opr.equals("<")) {

                        if (Integer.parseInt(((TipusSimple) var1).getNom()) < Integer.parseInt(((TipusSimple) var2).getNom())) {
                            ((TipusSimple) var2).setNom("cert");
                        } else {
                            ((TipusSimple) var2).setNom("fals");
                        }
                    } else if (opr.equals("!=")) {

                        if (Integer.parseInt(((TipusSimple) var1).getNom()) != Integer.parseInt(((TipusSimple) var2).getNom())) {
                            ((TipusSimple) var2).setNom("cert");
                        } else {
                            ((TipusSimple) var2).setNom("fals");
                        }
                    }
                }/*else if (attr.getValue(TokenType.NOT) != null) {
                    if (((TipusSimple) var1).getNom().equals("1")) {
                        ((TipusSimple) var1).setNom("0");
                    } else if(((TipusSimple) var1).getNom().equals("0")){
                        ((TipusSimple) var1).setNom("1");
                    }else{
                        System.err.println("Incorrect use of not. Using invalid integer value");
                    }
                }*/
                if (opr == null) ((TipusSimple) var2).setNom(op.toString());
                attr.setValue("RESULT", var2);
                attr.setValue("VAR_LEFT", var2);
                attr.removeAttribute("VARIABLE");
            }
        } else {

            if (var1 == null)
                System.err.println("Left part of operation have different type");

            if (var2 == null) {
                System.err.println("Right part of operation have different type");
            }
        }
        return attr;
    }

    public Semantic findVariable(Semantic attr, String name) {

        System.out.println("VARIABLE--> " + name);
        int find = 0;
        if (ts.getBlocActual() == 1) {
            if (ts.obtenirBloc(ts.getBlocActual()).obtenirVariable(name) != null) {
                attr.setValue("VARIABLE", ts.obtenirBloc(ts.getBlocActual()).obtenirVariable(name).getTipus());
                find = 1;
            } else {
                if (ts.obtenirBloc(ts.getBlocActual()).obtenirConstant(name) != null) {
                    attr.setValue("VARIABLE", ts.obtenirBloc(ts.getBlocActual()).obtenirConstant(name).getTipus());
                    find = 1;
                }
            }

            /*else if (attr.getValue("FUNC_NAME") != null) {
                String nom_func = (String) attr.getValue("FUNC_NAME");
                Procediment proc = ts.obtenirBloc(1).obtenirProcediment(nom_func);
                if (proc != null) {
                    int nParam = proc.getNumeroParametres();
                    for (int i = 0; find == 0 && i < nParam; i++) {
                        if (proc.obtenirParametre(i).getNom().equals(name)) {
                            attr.setValue("VARIABLE", proc.obtenirParametre(i).getTipus());
                            find = 1;
                        }
                    }
                }
            } else if (ts.obtenirBloc(0).obtenirVariable(name) != null) {
                attr.setValue("VARIABLE", ts.obtenirBloc(0).obtenirVariable(name).getTipus());
                find = 1;
            }
            }*/
        } if(find == 0){
            if (ts.obtenirBloc(0).obtenirConstant(name) != null) {
                attr.setValue("VARIABLE", ts.obtenirBloc(0).obtenirConstant(name).getTipus());
                find = 1;
            }

            if (ts.obtenirBloc(0).obtenirVariable(name) != null) {
                attr.setValue("VARIABLE", ts.obtenirBloc(0).obtenirVariable(name).getTipus());
                find = 1;
            }

        }

        if (find == 0) {
            System.err.println("ERROR, Variable with name " + name + " not exists.");
        }
        attr.setValue("FIND_VAR", find);
        return attr;
    }
    //TODO ASSIGN VALUES
    public Semantic addNewVarValue(Semantic attr) {
        String name = (String) attr.getValue("ID_ASSIGMENT");
        ITipus newTipus = (ITipus) attr.getValue("VARIABLE");
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
                    ((TipusArray) newVar.getTipus()).obtenirDimensio(index).setTipusLimit(newTipus);
                    ts.obtenirBloc(blockActual).inserirVariable(newVar);
                }
            } else {

                if (isInteger(newVar.getTipus(), newVar.getTipus().getNom()) && isInteger(newTipus, newTipus.getNom()) || (newVar.getTipus().getNom().equals("sencer"))) {
                    newVar.setTipus(newTipus);
                    ts.obtenirBloc(blockActual).inserirVariable(newVar);
                } else {
                    if (isLogic(newVar.getTipus()) && isLogic(newTipus)) {
                        newVar.setTipus(newTipus);
                        ts.obtenirBloc(blockActual).inserirVariable(newVar);
                    }
                }
            }
        }
        return attr;
    }

    public Semantic returnValidation(Semantic attr) {


        Object func = attr.getValue(TokenType.FUNCIO);

        if ( func != null) {
            ITipus aux1 = ((Funcio) func).getTipus();
            ITipus aux2 = (ITipus) attr.getValue("RESULT");
            if (isSimpleType(aux1) && isSimpleType(aux2)) {
                if ((isLogic(aux1) && isLogic(aux2)) || (isInteger(aux1, aux1.getNom()) && isInteger(aux2, aux2.getNom()))) {
                    //ALL IS FINE
                } else {
                    System.err.println("Return not correspond to the type of function.");
                }
            } else {
                System.err.println("Invalid return type");
            }
        } else {
            if (ts.getBlocActual() == 0) {
                System.err.println("Invalid return between inici and fi");
            } else {
                System.err.println("Return inside Procedure");
            }
        }


    return attr;
    }

    public Semantic vectorAccesValidation(Semantic attr) {
        boolean ok = false;

        String vec_name = (String) attr.getValue("ID_ASSIGMENT");
        TipusSimple acces_var = attr.getValue("VARIABLE") == null ? (TipusSimple) attr.getValue("RESULT") : (TipusSimple) attr.getValue("VARIABLE");

        attr = findVariable(attr, vec_name);
        if ((int) attr.getValue("FIND_VAR") == 1) {
            ok = true;
            TipusArray vec = (TipusArray) attr.getValue("VARIABLE");

            //TODO CONFIRMAR SI NOMES HI HAURA VECTORS D'UNA DIMENSIO
            if ((Integer.parseInt(acces_var.getNom()) > vec.getNumeroDimensions()) || (Integer.parseInt(acces_var.getNom()) <= 0)) {
                System.err.println("The index of vector is out of range.");
                ok = false;
            } else {
                attr.setValue("INDEX_VECTOR", acces_var.getNom());
            }

        }
        attr.setValue("VEC_OK", ok);
        return attr;
    }

    public Semantic addVectorSize(Semantic attr) {
        String value = (String) attr.getValue("VECTOR_SIZE");
        TipusArray vec = (TipusArray) attr.getValue("VECTOR_2");
        if (isInteger(new TipusSimple(value, 10000), value)) {
            int size = Integer.parseInt(value);
            if (size > 0) {
                for (int i = 1; i <= size; i++) {
                    vec.inserirDimensio(new DimensioArray(new TipusSimple()));
                }
            } else {
                System.err.println("Invalid vector size, needs be bigger than 0 and is " + size);
            }
        } else {
            System.err.println("Invalid vector type");
        }
        attr.setValue("VECTOR_SIZE", vec);
        return attr;
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

}