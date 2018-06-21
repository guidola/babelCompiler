package edu.salleurl.g6.ase; /**
 * This type was created in VisualAge.
 */
 
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
    setValue("VALUE", ct.getValor());
    setValue("TIPUS", ct.getTipus());
    setValue("IS_GLOBAL", isGlobal);
    setValue("ESTATIC", true);

}

/**
 * This method was created in VisualAge.
 * @return java.util.Hashtable
 */
public Hashtable getAttributes() {
	return attributes;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param attributeID java.lang.Object
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
 * @param attributeID java.lang.Object
 */
public void removeAttribute(Object attributeID) {
	getAttributes().remove(attributeID);
}
/**
 * This method was created in VisualAge.
 * @param newValue java.util.Hashtable
 */
public void setAttributes(Hashtable newValue) {
	this.attributes = newValue;
}
    /**
     * This method was created in VisualAge.
     * @param attributeID java.lang.Object
     * @param attributeValue java.lang.Object
     */
    public void setValue(Object attributeID, Object attributeValue) {
        getAttributes().put(attributeID,attributeValue);
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

    public void isVectorIndexNonStatic(boolean isVector) {
        getAttributes().put("IS_VECTOR_INDEX_NON_STATIC", isVector);
    }

public void copy(Semantic exp) {

	if (exp.getAttributes().containsKey("VALUE"))
		setValue("VALUE",exp.getValue("VALUE"));
	if (exp.getAttributes().containsKey("ESTATIC"))
		setValue("ESTATIC",exp.getValue("ESTATIC"));
	if (exp.getAttributes().containsKey("TIPUS"))
		setValue("TIPUS",exp.getValue("TIPUS"));
	if (exp.getAttributes().containsKey("OPERADOR"))
		setValue("OPERADOR",exp.getValue("OPERADOR"));
	if (exp.getAttributes().containsKey("COMPARACIO"))
		setValue("COMPARACIO",exp.getValue("COMPARACIO"));
	if (exp.getAttributes().containsKey("LINEA"))
		setValue("LINEA",exp.getValue("LINEA"));
	if (exp.getAttributes().containsKey("COLUMNA"))
		setValue("COLUMNA",exp.getValue("COLUMNA"));
	if (exp.getAttributes().containsKey("REG"))
		setValue("REG",exp.getValue("REG"));
	if (exp.getAttributes().containsKey("TOKEN"))
		setValue("TOKEN",exp.getValue("TOKEN"));

	}
	
	private int performComparison(int left_side, String operator, int right_side) {
        switch(operator) {
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

	public Semantic performRelationalOperation(Semantic right_side_exp) {

        //TODO perform type checks

        Semantic result = new Semantic();
        result.setEstatic(this.isEstatic() && right_side_exp.isEstatic());
        result.setType(new TipusSimple(Ase.TIPUS_LOGIC, MIPSFactory.TIPUS_LOGIC_SIZE));

        if(this.isEstatic()) {
            if(right_side_exp.isEstatic()) {
                result.setValue(performComparison(this.intValue(), this.opRel(), right_side_exp.intValue()));
            } else {
                result.setRegister(MIPSFactory.performComparison(this.intValue(), this.opRel(), right_side_exp.reg()));
            }
        } else {
            if(right_side_exp.isEstatic()) {
                result.setRegister(MIPSFactory.performComparison(this.reg(), this.opRel(), right_side_exp.intValue()));
            } else {
                result.setRegister(MIPSFactory.performComparison(this.reg(), this.opRel(), right_side_exp.reg()));
            }
        }

        return result;
    }

	public Semantic performUnaryOperation(Semantic operand) {

        if(this.opu() == null) return operand;

        Semantic result = new Semantic();
        result.setType(operand.type());
        result.setEstatic(operand.isEstatic());

        //TODO type check compatibility between operator and operand

        if(operand.type() instanceof TipusSimple && this.type() instanceof TipusSimple) {


            if (operand.isEstatic()) {
                if (this.opu().equals("not")) {
                    if (operand.intValue() == MIPSFactory.CERT || operand.intValue() == MIPSFactory.FALS) {
                        result.setValue(operand.intValue() == MIPSFactory.CERT ? MIPSFactory.FALS : MIPSFactory.CERT);
                    } else {
                        if (operand.intValue() == 0 || operand.intValue() == 1) {
                            result.setValue(operand.intValue() == 1 ? 0 : 1);
                        }
                    }
                } else {
                    if (this.opu().equals("-")) {
                        result.setValue(operand.intValue() * (-1));
                    }
                }
            } else {
                result.setRegister(MIPSFactory.performOpu(this.opu(), operand.reg()));
            }
        }else{
            //System.err.
        }
        return result;
    }

	public Semantic performBinaryOperation(Semantic operand2) {

        if(this.isNotAnOperation()) return operand2;

        Semantic result = new Semantic();

        //TODO perform type checks and operator compatibility checks
        //if(this.intValue())
        result.setEstatic(this.isEstatic() && operand2.isEstatic());
        result.setType(this.type());

        // if it gets to this point everything is semantically correct
        if(result.isEstatic()) {
            switch(this.binaryOperator()) {
                case "+":
                    result.setValue(this.intValue() + operand2.intValue());
                    break;

                case "-":
                    result.setValue(this.intValue() - operand2.intValue());
                    break;

                case "*":
                    result.setValue(this.intValue() * operand2.intValue());
                    break;

                case "/":
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
            switch(this.binaryOperator()) {
                case "+":
                    if(this.isEstatic()){
                        result.setRegister(MIPSFactory.performAdd(this.intValue(), operand2.reg()));
                    } else {
                        if(operand2.isEstatic()) {
                            result.setRegister(MIPSFactory.performAdd(this.reg(), operand2.intValue()));
                        } else {
                            result.setRegister(MIPSFactory.performAdd(this.reg(), operand2.reg()));
                        }
                    }
                    break;

                case "-":
                    if(this.isEstatic()){
                        result.setRegister(MIPSFactory.performSub(this.intValue(), operand2.reg()));
                    } else {
                        if(operand2.isEstatic()) {
                            result.setRegister(MIPSFactory.performSub(this.reg(), operand2.intValue()));
                        } else {
                            result.setRegister(MIPSFactory.performSub(this.reg(), operand2.reg()));
                        }
                    }
                    break;

                case "*":
                    if(this.isEstatic()){
                        result.setRegister(MIPSFactory.performMul(this.intValue(), operand2.reg()));
                    } else {
                        if(operand2.isEstatic()) {
                            result.setRegister(MIPSFactory.performMul(this.reg(), operand2.intValue()));
                        } else {
                            result.setRegister(MIPSFactory.performMul(this.reg(), operand2.reg()));
                        }
                    }
                    break;

                case "/":
                    if(this.isEstatic()){
                        result.setRegister(MIPSFactory.performDiv(this.intValue(), operand2.reg()));
                    } else {
                        if(operand2.isEstatic()) {
                            result.setRegister(MIPSFactory.performDiv(this.reg(), operand2.intValue()));
                        } else {
                            result.setRegister(MIPSFactory.performDiv(this.reg(), operand2.reg()));
                        }
                    }
                    break;

                case "or":
                    if(this.isEstatic()){
                        result.setRegister(MIPSFactory.performOr(this.intValue(), operand2.reg(), this.isInt()));
                    } else {
                        if(operand2.isEstatic()) {
                            result.setRegister(MIPSFactory.performOr(this.reg(), operand2.intValue(), this.isInt()));
                        } else {
                            result.setRegister(MIPSFactory.performOr(this.reg(), operand2.reg(), this.isInt()));
                        }
                    }
                    break;

                case "and":
                    if(this.isEstatic()){
                        result.setRegister(MIPSFactory.performAnd(this.intValue(), operand2.reg(), this.isInt()));
                    } else {
                        if(operand2.isEstatic()) {
                            result.setRegister(MIPSFactory.performAnd(this.reg(), operand2.intValue(), this.isInt()));
                        } else {
                            result.setRegister(MIPSFactory.performAnd(this.reg(), operand2.reg(), this.isInt()));
                        }
                    }
                    break;

            }   
        }


        return result;
    }

    public void store(Semantic expression) {

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

    public boolean isString() {
        return type() instanceof TipusCadena;
    }

    public boolean isInt() {
        return type() instanceof TipusSimple && type().getNom().equals(Ase.TIPUS_SIMPLE);
    }

    public boolean isBool() {
        return type() instanceof TipusSimple && type().getNom().equals(Ase.TIPUS_LOGIC);
    }

    public String opRel() {
        return (String) attributes.get("REL_OPERATOR");
    }

    public boolean isVectorIndexNonStatic() {
        boolean b = (boolean) attributes.get("IS_VECTOR_INDEX_NON_STATIC");
        return (boolean) attributes.get("IS_VECTOR_INDEX_NON_STATIC");
    }


	public String toString() {

		return "VALUE: "+getValue("VALUE")+
				   "; Estatic: "+getValue("ESTATIC")+
				   "; Tipus: "+getValue("TIPUS")+
                   "; Registre: "+getValue("REG")+
				   "; Oper: "+getValue("OPERADOR")+
				   "; Comp: "+getValue("COMPARACIO")+
				   "; Linea: "+getValue("LINEA")+
				   "; Columna: "+getValue("COLUMNA")
				   ;
	}

}
