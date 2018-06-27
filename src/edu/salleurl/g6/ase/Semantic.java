package edu.salleurl.g6.ase; /**
 * This type was created in VisualAge.
 */

import edu.salleurl.g6.alex.Alex;
import edu.salleurl.g6.gc.MIPSFactory;
import taulasimbols.*;

import java.util.Hashtable;

public class Semantic {
    private Hashtable attributes;

    /**
     * Semantic constructor comment.
     */
    public Semantic() {
        super();
        attributes = new Hashtable();
    }

    public Semantic(Variable var, boolean isGlobal) {

        attributes = new Hashtable();
        setValue("OFFSET", var.getDesplacament());
        setValue("TIPUS", var.getTipus());
        setValue("IS_GLOBAL", isGlobal);
        setValue("ESTATIC", false);

    }

    public Semantic(Constant ct, boolean isGlobal) {

        attributes = new Hashtable();
        if(ct.getTipus() instanceof TipusCadena) {
            setTag((String)ct.getValor());
        } else {
            if(!(ct.getTipus() instanceof TipusIndefinit))
                setValue("VALUE", ct.getValor());
        }
        setValue("TIPUS", ct.getTipus());
        setValue("IS_GLOBAL", isGlobal);
        setValue("ESTATIC", true);

    }

    public Semantic(Funcio func, boolean isGlobal) {

        attributes = new Hashtable();
        setValue("TIPUS", func.getTipus());
        setValue("IS_GLOBAL", isGlobal);
        setValue("ESTATIC", true);

    }

    private void log(String text) {
        System.err.println(text);
        Alex.getLog().writeError(text);
    }

    /**
     * This method was created in VisualAge.
     *
     * @return java.util.Hashtable
     */
    public Hashtable getAttributes() {
        return attributes;
    }

    /**
     * This method was created in VisualAge.
     *
     * @param attributeID java.lang.Object
     * @return java.lang.Object
     */
    public Object getValue(Object attributeID) {
        return getAttributes().get(attributeID);
    }

    /**
     * This method was created in VisualAge.
     */
    public void removeAll() {
        getAttributes().clear();
    }

    /**
     * This method was created in VisualAge.
     *
     * @param attributeID java.lang.Object
     */
    public void removeAttribute(Object attributeID) {
        getAttributes().remove(attributeID);
    }

    /**
     * This method was created in VisualAge.
     *
     * @param newValue java.util.Hashtable
     */
    public void setAttributes(Hashtable newValue) {
        this.attributes = newValue;
    }

    /**
     * This method was created in VisualAge.
     *
     * @param attributeID    java.lang.Object
     * @param attributeValue java.lang.Object
     */
    public void setValue(Object attributeID, Object attributeValue) {
        getAttributes().put(attributeID, attributeValue);
    }

    public void setValue(Object value) {
        getAttributes().put("VALUE", value);
    }

    public void setRegister(String register) {
        getAttributes().put("REG", register);
    }

    public void setType(ITipus type) {
        getAttributes().put("TIPUS", type);
    }

    public void setEstatic(boolean isEstatic) {
        getAttributes().put("ESTATIC", isEstatic);
    }

    public void setTag(String tag) {
        getAttributes().put("TAG", tag);
    }

    public void setBOperator(String operator) {
        getAttributes().put("B_OPERATOR", operator);
    }

    public void isRef(boolean isRef) {
        getAttributes().put("IS_REF", isRef);
    }

    public void setUOperator(String operator) {
        getAttributes().put("U_OPERATOR", operator);
    }

    public void setOpRel(String operator) {
        getAttributes().put("REL_OPERATOR", operator);
    }

    public void setGlobal(boolean isGlobal) {
        getAttributes().put("IS_GLOBAL", isGlobal);
    }

    public void setOffset(int offset) {
        getAttributes().put("OFFSET", offset);
    }
    public void setIsVar(boolean isVar) {
        getAttributes().put("IS_VAR", isVar);
    }


    public void setIsConst(boolean isConst) {
        getAttributes().put("IS_CONST", isConst);
    }

    public void isVectorIndexNonStatic(boolean isVector) {
        getAttributes().put("IS_VECTOR_INDEX_NON_STATIC", isVector);
    }

    public void merge(Semantic additional_params) {
        getAttributes().putAll(additional_params.getAttributes());
    }

    public void setOffsetRegister(String register) {
        getAttributes().put("OFFSET_REG", register);
    }

    public boolean isTipusIndefinit() {
        return this.type() instanceof TipusIndefinit;

    }
    public boolean isVar(){
        return (boolean)this.getValue("IS_VAR");
    }
    public Object isConst(){
        return this.getValue("IS_CONST");
    }

    public void setAddressOffset(int offset) {
        getAttributes().put("ADDRESS_OFFSET", offset);
    }

    public boolean isArray() {
        return this.type() instanceof TipusArray;
    }
    public boolean isCadena() {
        return this.type() instanceof TipusCadena;
    }
    public void copy(Semantic exp) {

        if (exp.getAttributes().containsKey("VALUE"))
            setValue("VALUE", exp.getValue("VALUE"));
        if (exp.getAttributes().containsKey("ESTATIC"))
            setValue("ESTATIC", exp.getValue("ESTATIC"));
        if (exp.getAttributes().containsKey("TIPUS"))
            setValue("TIPUS", exp.getValue("TIPUS"));
        if (exp.getAttributes().containsKey("OPERADOR"))
            setValue("OPERADOR", exp.getValue("OPERADOR"));
        if (exp.getAttributes().containsKey("COMPARACIO"))
            setValue("COMPARACIO", exp.getValue("COMPARACIO"));
        if (exp.getAttributes().containsKey("LINEA"))
            setValue("LINEA", exp.getValue("LINEA"));
        if (exp.getAttributes().containsKey("COLUMNA"))
            setValue("COLUMNA", exp.getValue("COLUMNA"));
        if (exp.getAttributes().containsKey("REG"))
            setValue("REG", exp.getValue("REG"));
        if (exp.getAttributes().containsKey("TOKEN"))
            setValue("TOKEN", exp.getValue("TOKEN"));

    }

    private int performComparison(int left_side, String operator, int right_side) {
        switch (operator) {
            case "==":
                return left_side == right_side ? MIPSFactory.CMP_OK : MIPSFactory.CMP_KO;
            case "!=":
                return left_side != right_side ? MIPSFactory.CMP_OK : MIPSFactory.CMP_KO;
            case ">":
                return left_side > right_side ? MIPSFactory.CMP_OK : MIPSFactory.CMP_KO;
            case "<":
                return left_side < right_side ? MIPSFactory.CMP_OK : MIPSFactory.CMP_KO;
            case ">=":
                return left_side >= right_side ? MIPSFactory.CMP_OK : MIPSFactory.CMP_KO;
            case "<=":
                return left_side <= right_side ? MIPSFactory.CMP_OK : MIPSFactory.CMP_KO;
            default:
                return MIPSFactory.CMP_KO;
        }
    }

	public Semantic handleOperationWithUndefined(Semantic op1, Semantic op2) {

        if (op1.isUndefined() && op2.isUndefined()) {
            Semantic result = new Semantic();
            result.setEstatic(true);
            result.setType(this.type());
            return result;
        } else if(op1.isUndefined()) {
            MIPSFactory.returnRegister(op1);
            return op2;
        } else if(op2.isUndefined()) {
            MIPSFactory.returnRegister(op1);
            return op1;
        }

        // THIS SHOULD NEVER HAPPEN
        return null;
    }
    /*
    if (!left_side_exp.type().getNom().equals(TIPUS_SIMPLE) || !right_side_exp.type().getNom().equals(TIPUS_SIMPLE)) {
        log("[ERR_SEM_5] El tipus de l'expressió no és SENCER");
        return false;
    }
    */
    public Semantic performRelationalOperation(Semantic right_side_exp) {

        Semantic result = new Semantic();

        result.setType(new TipusSimple(Ase.TIPUS_LOGIC, MIPSFactory.TIPUS_LOGIC_SIZE));

        if(this.isUndefined() || right_side_exp.isUndefined()) {
            return handleOperationWithUndefined(this, right_side_exp);
        }
        if(this.isSameTypeTo(right_side_exp)) {


            result.setEstatic(this.isEstatic() && right_side_exp.isEstatic());

            if (this.isEstatic()) {
                if (right_side_exp.isEstatic()) {
                    result.setValue(performComparison(this.intValue(), this.opRel(), right_side_exp.intValue()));
                } else {
                    result.setRegister(MIPSFactory.performComparison(this.intValue(), this.opRel(), right_side_exp.reg()));
                }
            } else {
                if (right_side_exp.isEstatic()) {
                    result.setRegister(MIPSFactory.performComparison(this.reg(), this.opRel(), right_side_exp.intValue()));
                } else {
                    result.setRegister(MIPSFactory.performComparison(this.reg(), this.opRel(), right_side_exp.reg()));
                }
            }
        }else{
            log("[ERR_SEM_33] "+ Alex.getLine()+", Les dos parts de l'operació relacional no son del mateix tipus");
            result.setType(new TipusIndefinit());
            result.setEstatic(true);
            result.setIsVar(false);
            return result;
        }
        return result;
    }

    public Semantic performUnaryOperation(Semantic operand) {

        if (this.opu() == null || operand.isUndefined()) return operand;

        Semantic result = new Semantic();
        result.setType(operand.type());
        result.setEstatic(operand.isEstatic());

        //TODO type check compatibility between operator and operand

        // by checking that theoperandis of simpletypewe are protecting against trying to operate on undefined
        if(operand.isOfSimpleType()) {

            if (operand.isEstatic()) {
                if (this.opu().equals("not")) {
                    if (operand.isBool()) {
                        result.setValue(operand.intValue() == MIPSFactory.CERT ? MIPSFactory.FALS : MIPSFactory.CERT);
                    } else  if(operand.isInt()){
                        result.setValue(operand.intValue() == 0 ? 1 : 0);
                    }else{
                        log("[ERR_SEM_34] "+Alex.getLine()+",El operand no es de tipus sencer o logic");
                    }
                } else {
                    if (this.opu().equals("-")) {
                        if(operand.isInt()) {
                            result.setValue(operand.intValue() * (-1));
                        } else {
                            log("[ERR_SEM_35] "+Alex.getLine()+", La operació "+ this.opu()+" requereix un operand sencer" );
                        }
                    }
                }
            } else {
                if(this.opu().equals("-") && operand.isBool()) {
                    log("[ERR_SEM_36] "+Alex.getLine()+", La operació "+ this.opu()+" requereix un operand sencer i ha obtingut un operand logic" );
                } else {
                    result.setRegister(MIPSFactory.performOpu(this.opu(), operand.reg()));
                }
            }
        }else{

        }

        return result;
    }

    public Semantic performBinaryOperation(Semantic operand2) {

        if (this.isNotAnOperation()) return operand2;

        if(this.isUndefined() || operand2.isUndefined()) {
            return handleOperationWithUndefined(this, operand2);
        }

        Semantic result = new Semantic();

        //TODO perform type checks and operator compatibility checks
        if(this.isSameTypeTo(operand2)){
            if(!this.isArray() && !this.isCadena()) {
                result.setEstatic(this.isEstatic() && operand2.isEstatic());
                result.setType(this.type());

                // if it gets to this point everything is semantically correct
                if (result.isEstatic()) {
                    switch (this.binaryOperator()) {
                        case "+":
                            if (this.isInt() && operand2.isInt())
                                result.setValue(this.intValue() + operand2.intValue());
                            break;

                        case "-":
                            if (this.isInt() && operand2.isInt())
                                result.setValue(this.intValue() - operand2.intValue());
                            break;

                        case "*":
                            if (this.isInt() && operand2.isInt())
                                result.setValue(this.intValue() * operand2.intValue());
                            break;

                        case "/":
                            if (this.isInt() && operand2.isInt())
                                result.setValue(this.intValue() / operand2.intValue());
                            break;

                        case "and":
                            result.setValue(this.intValue() & operand2.intValue());
                            break;

                        case "or":
                            result.setValue(this.intValue() | operand2.intValue());
                            break;

                    }
                } else {
                    switch (this.binaryOperator()) {
                        case "+":
                            if (this.isEstatic()) {
                                result.setRegister(MIPSFactory.performAdd(this.intValue(), operand2.reg()));
                            } else {
                                if (operand2.isEstatic()) {
                                    result.setRegister(MIPSFactory.performAdd(this.reg(), operand2.intValue()));
                                } else {
                                    result.setRegister(MIPSFactory.performAdd(this.reg(), operand2.reg()));
                                }
                            }
                            break;

                        case "-":
                            if (this.isEstatic()) {
                                result.setRegister(MIPSFactory.performSub(this.intValue(), operand2.reg()));
                            } else {
                                if (operand2.isEstatic()) {
                                    result.setRegister(MIPSFactory.performSub(this.reg(), operand2.intValue()));
                                } else {
                                    result.setRegister(MIPSFactory.performSub(this.reg(), operand2.reg()));
                                }
                            }
                            break;

                        case "*":
                            if (this.isEstatic()) {
                                result.setRegister(MIPSFactory.performMul(this.intValue(), operand2.reg()));
                            } else {
                                if (operand2.isEstatic()) {
                                    result.setRegister(MIPSFactory.performMul(this.reg(), operand2.intValue()));
                                } else {
                                    result.setRegister(MIPSFactory.performMul(this.reg(), operand2.reg()));
                                }
                            }
                            break;

                        case "/":
                            if (this.isEstatic()) {
                                result.setRegister(MIPSFactory.performDiv(this.intValue(), operand2.reg()));
                            } else {
                                if (operand2.isEstatic()) {
                                    result.setRegister(MIPSFactory.performDiv(this.reg(), operand2.intValue()));
                                } else {
                                    result.setRegister(MIPSFactory.performDiv(this.reg(), operand2.reg()));
                                }
                            }
                            break;

                        case "or":
                            if (this.isEstatic()) {
                                result.setRegister(MIPSFactory.performOr(this.intValue(), operand2.reg(), this.isInt()));
                            } else {
                                if (operand2.isEstatic()) {
                                    result.setRegister(MIPSFactory.performOr(this.reg(), operand2.intValue(), this.isInt()));
                                } else {
                                    result.setRegister(MIPSFactory.performOr(this.reg(), operand2.reg(), this.isInt()));
                                }
                            }
                            break;

                        case "and":
                            if (this.isEstatic()) {
                                result.setRegister(MIPSFactory.performAnd(this.intValue(), operand2.reg(), this.isInt()));
                            } else {
                                if (operand2.isEstatic()) {
                                    result.setRegister(MIPSFactory.performAnd(this.reg(), operand2.intValue(), this.isInt()));
                                } else {
                                    result.setRegister(MIPSFactory.performAnd(this.reg(), operand2.reg(), this.isInt()));
                                }
                            }
                            break;

                    }
                }
            }else{
                log("[ERR_SEM_31] "+Alex.getLine()+", No es pot operar amb expressions de tipus array o cadena");
            }
        }else{
            log("[ERR_SEM_32] "+Alex.getLine()+", Operands de diferent tipus");
            result.setType(new TipusIndefinit());
            result.setEstatic(true);
            return result;
        }


        return result;
    }

    public void store(Semantic expression) {

        // if receiver or value is undefined return and do nothing
        if(this.isUndefined() && expression.isUndefined()){
            return;
        } else if(this.isUndefined()) {
            MIPSFactory.returnRegister(expression);
            return;
        } else if(expression.isUndefined()) {
            MIPSFactory.returnRegister(this);
            return;
        }

        if(this.isVectorIndexNonStatic()){
            if(expression.isEstatic()){
                MIPSFactory.performAssignment(this.reg(), this.isGlobal(), expression.intValue());
            } else {
                MIPSFactory.performAssignment(this.reg(), this.isGlobal(), expression.reg());
            }
        } else {
            if(expression.isEstatic()){
                MIPSFactory.performAssignment(this.offset(), this.isGlobal(), expression.intValue());
            } else {
                MIPSFactory.performAssignment(this.offset(), this.isGlobal(), expression.reg());
            }
        }

    }



    public String binaryOperator() {
        return (String) attributes.get("B_OPERATOR");
    }

    public String reg() {
        return (String) attributes.get("REG");
    }

    public Boolean isEstatic() {
        return (Boolean) attributes.get("ESTATIC");
    }

    public boolean isNotAnOperation() {
        return getAttributes().get("B_OPERATOR") == null;
    }

    public Object constValue() {
        return attributes.get("VALUE");
    }

    public int intValue() {
        return (int) attributes.get("VALUE");
    }

    public String strValue() {
        return (String) attributes.get("VALUE");
    }

    public int logicValue() {
        return attributes.get("VALUE").equals("cert") ? 0xFFFF : 0x0000;
    }

    public String opu() {
        return (String) attributes.get("U_OPERATOR");
    }

    public int offset() {
        return (int) attributes.get("OFFSET");
    }

    public boolean isGlobal() {
        return (boolean) attributes.get("IS_GLOBAL");
    }

    public int arrayUpperBound() {
        return (int) ((TipusArray) type()).obtenirDimensio(0).getLimitSuperior();
    }

    public int arrayLowerBound() {
        return (int) ((TipusArray) type()).obtenirDimensio(0).getLimitInferior();
    }

    public ITipus arrayType() {
        return ((TipusArray) type()).getTipusElements();
    }

    public String tag() {
        return ((String) attributes.get("TAG"));
    }

    public ITipus type() {
        return ((ITipus) attributes.get("TIPUS"));
    }

    public String varName() {
        return ((String) attributes.get("VAR_NAME"));
    }

    public boolean hasOffset() {
        return attributes.contains("OFFSET");
    }

    public String typeId() {

        if (attributes.get("TIPUS") instanceof TipusSimple) {
            return ((TipusSimple) attributes.get("TIPUS")).getNom();
        } else if (attributes.get("TIPUS") instanceof TipusCadena) {
            return Ase.TIPUS_CADENA;
        }else if(this.isUndefined()){
            return Ase.TIPUS_INDEFINIT;
        }
        return "THIS_SHOULD_NEVER_HAPPEN";
    }
    public String typeName() {

        if (this.type() instanceof TipusSimple) {
            return this.type().getNom();
        } else if (this.type() instanceof TipusCadena) {
            return this.type().getNom();
        }else if(this.type() instanceof TipusArray){
            return this.type().getNom();
        }
        return "THIS_SHOULD_NEVER_HAPPEN";
    }

    public boolean isSameTypeTo(Semantic right_part) {
        if (!this.isTipusIndefinit() && !right_part.isTipusIndefinit()) {
            if (right_part.type() instanceof TipusSimple) {
                return this.type().getNom().equals(right_part.type().getNom());
            } else if (right_part.isArray() && this.isArray()) {

                return this.arrayType().getNom().equals(right_part.arrayType().getNom());
            }
            return false;
        }
        return true;
    }

    public boolean isString() {
        return type() instanceof TipusCadena;
    }

    public boolean isInt() {
        return type() instanceof TipusSimple && type().getNom().equals(Ase.TIPUS_SIMPLE);
    }

    public boolean isBool() {
        return type() instanceof TipusSimple && type().getNom().equals(Ase.TIPUS_LOGIC);
    }

    public boolean isUndefined() {
        return type() instanceof TipusIndefinit;
    }

    public String opRel() {
        return (String) attributes.get("REL_OPERATOR");
    }

    public boolean isVectorIndexNonStatic() {
        boolean b = (boolean) attributes.get("IS_VECTOR_INDEX_NON_STATIC");
        return (boolean) attributes.get("IS_VECTOR_INDEX_NON_STATIC");
    }

    public boolean hasRegister() {
        return attributes.containsKey("REG");
    }

    public boolean isOfSimpleType() {
        return type() instanceof TipusSimple;
    }

    public String offsetRegister() {
        return (String) attributes.get("OFFSET_REG");
    }

    public boolean isRef() {
        return (boolean) attributes.get("IS_REF");
    }

    public int addressOffset() {
        return (int) attributes.get("ADDRESS_OFFSET");
    }

    public String toString() {

        return "VALUE: " + getValue("VALUE") +
                "; Estatic: " + getValue("ESTATIC") +
                "; Tipus: " + getValue("TIPUS") +
                "; Registre: " + getValue("REG") +
                "; Oper: " + getValue("OPERADOR") +
                "; Comp: " + getValue("COMPARACIO") +
                "; Linea: " + getValue("LINEA") +
                "; Columna: " + getValue("COLUMNA")
                ;
    }

}
