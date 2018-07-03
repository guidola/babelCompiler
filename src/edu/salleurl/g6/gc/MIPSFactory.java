package edu.salleurl.g6.gc;

import edu.salleurl.g6.ase.Ase;
import edu.salleurl.g6.ase.Semantic;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;

public class MIPSFactory {

    private static final int REGISTER_SIZE = 0x04;

    public static final int REFERENCE_SIZE = REGISTER_SIZE;
    public static final int TIPUS_LOGIC_SIZE = REGISTER_SIZE;
    public static final int TIPUS_SIMPLE_SIZE = REGISTER_SIZE;

    public static final int CERT = 0xffff;
    public static final int FALS = 0x0000;

    public static final String TAG_CERT = "cert";
    public static final String TAG_FALS = "fals";
    public static final String TAG_LINEJUMP = "linejump";

    // since MIPS instructions refering to comparison understand true as 0x01 and false as 0x00 static true and false
    // for relation operations will follow the same logic
    public static final int CMP_OK = 0x01;
    public static final int CMP_KO = 0x00;

    private static final int OFFSET_OLD_FP = 72;
    private static final int OFFSET_OLD_RA = 76;
    private static final int OFFSET_RET_VAL = 80;

    private static PrintWriter out;
    private static TagGenerator tags = new TagGenerator();
    private static RegisterHandler registers = new RegisterHandler();

    public static void initAssemblyOutFile(String filename) {

        try {
            out = new PrintWriter(filename + ".s");
        } catch (FileNotFoundException e) {
            File f = new File(filename + ".s");
            f.getParentFile().mkdirs();
            try {
                f.createNewFile();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            initAssemblyOutFile(filename);
        }
    }

    public static void finish(){
        // exit
        out.println("li $v0, 0x0A");
        out.println("syscall");
        out.close();
    }

    public static void init() {
        out.println(".data");
        out.println(TAG_CERT + ": .asciiz \"" + Ase.CERT + "\"");
        out.println(TAG_FALS + ": .asciiz \"" + Ase.FALS + "\"");
        out.println(TAG_LINEJUMP + ": .asciiz \"\\n\"");
        out.println(".text");
        defineErrorRoutine();
        out.print("main: ");
        moveFpToSp();
    }

    /** GLOBAL MEMORY OPERATIONS **/

    private static String g_lw(int offset) {
        String rDest = registers.getRegister();
        out.println("lw " + rDest + ", -" + offset + "($gp)");
        return rDest;
    }

    private static String g_lw(String r_offset) {
        String rDest = registers.getRegister();
        String offset_result = sub(RegisterHandler.GP, r_offset);
        out.println("lw " + rDest + ", (" + offset_result + ")");
        registers.returnRegister(offset_result);
        return rDest;
    }

    private static void g_sw(String rSrc, int offset) {
        out.println("sw " + rSrc + ", -" + offset + "($gp)");
        registers.returnRegister(rSrc);
    }

    private static void g_sw(String rSrc, String r_offset) {
        String offset_result = sub(RegisterHandler.GP, r_offset);
        out.println("sw " + rSrc + ", (" + offset_result + ")");
        registers.returnRegisters(rSrc, offset_result);
    }

    /** REFERENCE MEMORY OPERATIONS **/

    private static String r_lw(int offset) {
        String rDest = registers.getRegister();
        out.println("lw " + rDest + ", " + offset);
        return rDest;
    }

    private static String r_lw(String r_offset) {
        String rDest = registers.getRegister();
        out.println("lw " + rDest + ", (" + r_offset + ")");
        registers.returnRegister(r_offset);
        return rDest;
    }

    private static void r_sw(String rSrc, int offset) {
        out.println("sw " + rSrc + ", -" + offset);
        registers.returnRegister(rSrc);
    }

    private static void r_sw(String rSrc, String r_offset) {

        out.println("sw " + rSrc + ", (" + r_offset + ")");
        registers.returnRegisters(rSrc, r_offset);
    }

    /** LOCAL MEMORY OPERATIONS **/

    private static String l_lw(int offset) {
        String rDest = registers.getRegister();
        out.println("lw " + rDest + ", -" + offset + "($fp)");
        return rDest;
    }

    private static String l_lw(String r_offset) {
        String rDest = registers.getRegister();
        String offset_result = sub(RegisterHandler.FP, r_offset);
        out.println("lw " + rDest + ", (" + offset_result + ")");
        registers.returnRegister(offset_result);
        return rDest;
    }

    private static void l_sw(String rSrc, int offset) {
        out.println("sw " + rSrc + ", -" + offset + "($fp)");
        registers.returnRegister(rSrc);
    }

    private static void l_sw(String rSrc, String r_offset) {
        String offset_result = sub(RegisterHandler.FP, r_offset);
        out.println("sw " + rSrc + ", (" + offset_result + ")");
        registers.returnRegisters(rSrc, offset_result);
    }

    /** MEMORY OPERATIONS **/

    private static String lw(int offset, boolean isGlobal, boolean isRef) {
        if(isRef) {
            return r_lw(offset);
        } else if(isGlobal){
            return g_lw(offset);
        } else {
            return l_lw(offset);
        }
    }

    private static String lw(String r_offset, boolean isGlobal, boolean isRef) {
        if(isRef) {
            return r_lw(r_offset);
        } else if(isGlobal){
            return g_lw(r_offset);
        } else {
            return l_lw(r_offset);
        }
    }

    private static void sw(String rSrc, int offset, boolean isGlobal, boolean isRef) {
        if(isRef) {
            r_sw(rSrc, offset);
        } else if(isGlobal){
            g_sw(rSrc, offset);
        } else {
            l_sw(rSrc, offset);
        }
    }

    private static void sw(String rSrc, String r_offset, boolean isGlobal, boolean isRef) {
        if(isRef) {
            r_sw(rSrc, r_offset);
        } else if(isGlobal){
            g_sw(rSrc, r_offset);
        } else {
            l_sw(rSrc, r_offset);
        }
    }

    private static  void validateVectorAccess(String r_index, int min_pos, int max_pos){

        String r_max = li(max_pos);
        out.println("bgt " + r_index + ", " + r_max + ", " + TagGenerator.ERROR_IOB);
        registers.returnRegister(r_max);

        String r_min = li(min_pos);
        out.println("bgt " + r_min + ", " + r_index + ", " + TagGenerator.ERROR_IOB);
        registers.returnRegister(r_min);

    }

    public static String duplicate(String r1) {
        String rDest = registers.getRegister();
        out.println("move " + rDest + ", " + r1);
        return rDest;
    }

    private static Semantic loadVectorCellAndGetOffset(int base_offset, String index_reg, boolean isGlobal, boolean isRef) {
        Semantic result = new Semantic();
        result.setOffsetRegister(addi(muli(index_reg, REGISTER_SIZE), base_offset));
        result.setRegister(lw(duplicate(result.offsetRegister()), isGlobal, isRef));
        return result;
    }

    private static Semantic loadVectorCellAndGetOffset(String r_base_offset, String index_reg, boolean isGlobal, boolean isRef) {
        Semantic result = new Semantic();
        result.setOffsetRegister(sub(r_base_offset, muli(index_reg, REGISTER_SIZE)));
        result.setRegister(lw(duplicate(result.offsetRegister()), isGlobal, isRef));
        return result;
    }

    private static String loadVectorCell(int base_offset, int index, boolean isGlobal, boolean isRef) {
        return lw(index * REGISTER_SIZE + base_offset, isGlobal, isRef);
    }

    private static String loadVectorCell(String r_base_offset, int index, boolean isGlobal, boolean isRef) {
        return lw(addi(r_base_offset, index * REGISTER_SIZE) , isGlobal, isRef);
    }

    /** LOAD LITERAL **/

    private static String li(int literal) {
        String rDest = registers.getRegister();
        out.println("li " + rDest + ", " + literal);
        return rDest;
    }

    /** ARITHMETIC OPERATIONS **/

    private static String neg(String r1){
        String rDest = registers.getRegister();
        out.println("negu " + rDest + ", " + r1);
        registers.returnRegister(r1);
        return rDest;
    }

    private static String add(String r1, String r2){
        String rDest = registers.getRegister();
        out.println("addu " + rDest + ", " + r1 + ", " + r2);
        registers.returnRegisters(r1, r2);
        return rDest;
    }

    private static String addi(String r1, int literal){
        String rDest = registers.getRegister();
        out.println("addiu " + rDest + ", " + r1 + ", " + literal);
        registers.returnRegister(r1);
        return rDest;
    }

    private static String sub(String r1, String r2){
        String rDest = registers.getRegister();
        out.println("subu " + rDest + ", " + r1 + ", " + r2);
        registers.returnRegisters(r1, r2);
        return rDest;
    }

    private static String subi(String r1, int literal){
        String rDest = registers.getRegister();
        out.println("addiu " + rDest + ", " + r1 + ", " + -literal);
        registers.returnRegister(r1);
        return rDest;
    }

    private static String mul(String r1, String r2){
        String rDest = registers.getRegister();
        out.println("mul " + rDest + ", " + r1 + ", " + r2);
        registers.returnRegisters(r1, r2);
        return rDest;
    }

    private static String muli(String r1, int literal){
        String rDest = registers.getRegister();
        String r2 = li(literal);
        out.println("mul " + rDest + ", " + r1 + ", " + r2);
        registers.returnRegisters(r1, r2);
        return rDest;
    }

    private static String div(String r1, String r2){
        String rDest = registers.getRegister();
        out.println("div " + rDest + ", " + r1 + ", " + r2);
        registers.returnRegisters(r1, r2);
        return rDest;
    }

    private static String divi(String r1, int literal){
        String rDest = registers.getRegister();
        String r2 = li(literal);
        out.println("div " + rDest + ", " + r1 + ", " + r2);
        registers.returnRegisters(r1, r2);
        return rDest;
    }

    private static String divi(int literal, String r2){
        String rDest = registers.getRegister();
        String r1 = li(literal);
        out.println("div " + rDest + ", " + r1 + ", " + r2);
        registers.returnRegisters(r1, r2);
        return rDest;
    }

    /** DIRECT LOGICAL OPERATIONS (assumes that r1 and r2 contain a value of type logic) **/

    private static String l_or(String r1, String r2){
        String rDest = registers.getRegister();
        out.println("or " + rDest + ", " + r1 + ", " + r2);
        registers.returnRegisters(r1, r2);
        return rDest;
    }

    private static String l_ori(String r1, int literal){
        String rDest = registers.getRegister();
        out.println("ori " + rDest + ", " + r1 + ", " + literal);
        registers.returnRegister(r1);
        return rDest;
    }

    private static String l_and(String r1, String r2){
        String rDest = registers.getRegister();
        out.println("and " + rDest + ", " + r1 + ", " + r2);
        registers.returnRegisters(r1, r2);
        return rDest;
    }

    private static String l_andi(String r1, int literal){
        String rDest = registers.getRegister();
        out.println("andi " + rDest + ", " + r1 + ", " + literal);
        registers.returnRegister(r1);
        return rDest;
    }

    private static String l_not(String r1){
        String rDest = registers.getRegister();
        out.println("not " + rDest + ", " + r1);
        registers.returnRegister(r1);
        return rDest;
    }

    /** CONVERSIONS **/

    private static String simpleToLogic(String r1) {
        String rDest = registers.getRegister();
        out.println("li " + rDest + ", 0");
        String tag = tags.getConversionTag();
        out.println("beq " + r1 + ", $zero, " + tag);
        out.println("li " + rDest + ", 0xffff");
        out.print(tag + ": ");
        registers.returnRegister(r1);
        return rDest;
    }

    /**  INDIRECT LOGICAL OPERATIONS (assumes that r1 and r2 contain a value of type simple which has to be converted) **/

    private static String s_or(String r1, String r2){
        return l_or(simpleToLogic(r1), simpleToLogic(r2));
    }

    private static String s_ori(String r1, int literal){
        return l_ori(simpleToLogic(r1), literal == 0 ? FALS : CERT);
    }

    private static String s_and(String r1, String r2){
        return l_and(simpleToLogic(r1), simpleToLogic(r2));
    }

    private static String s_andi(String r1, int literal){
        return l_andi(simpleToLogic(r1), literal == 0 ? FALS : CERT);
    }

    private static String s_not(String r1){
        return l_not(simpleToLogic(r1));
    }

    /** RELATIONAL OPERATIONS **/
    // Relational operation results are either 0x01 if true or 0x00 if false

    private static String seq(String r1, String r2) {
        String rDest = registers.getRegister();
        out.println("seq " +  rDest + ", " + r1 + ", " + r2);
        registers.returnRegisters(r1, r2);
        return rDest;
    }

    private static String sge(String r1, String r2) {
        String rDest = registers.getRegister();
        out.println("sge " +  rDest + ", " + r1 + ", " + r2);
        registers.returnRegisters(r1, r2);
        return rDest;
    }

    private static String sgt(String r1, String r2) {
        String rDest = registers.getRegister();
        out.println("sgt " +  rDest + ", " + r1 + ", " + r2);
        registers.returnRegisters(r1, r2);
        return rDest;
    }

    private static String sle(String r1, String r2) {
        String rDest = registers.getRegister();
        out.println("sle " +  rDest + ", " + r1 + ", " + r2);
        registers.returnRegisters(r1, r2);
        return rDest;
    }

    private static String slt(String r1, String r2) {
        String rDest = registers.getRegister();
        out.println("slt " +  rDest + ", " + r1 + ", " + r2);
        registers.returnRegisters(r1, r2);
        return rDest;
    }

    private static String sne(String r1, String r2) {
        String rDest = registers.getRegister();
        out.println("sne " +  rDest + ", " + r1 + ", " + r2);
        registers.returnRegisters(r1, r2);
        return rDest;
    }

    private static String cmp(String r1, String op, String r2){
        switch(op) {
            case "==":
                return seq(r1, r2);
            case "!=":
                return sne(r1, r2);
            case ">":
                return sgt(r1, r2);
            case "<":
                return slt(r1, r2);
            case ">=":
                return sge(r1, r2);
            case "<=":
                return sle(r1, r2);
            default:
                return null; // TODO check if this should never happen since syntax would throw an error first or it would get here with an incorrect operator and then crash
        }
    }

    /** INSTRUCTIONS **/

    private static void bnez(String label, String r1) {

        out.println("bnez " + r1 + ", " + label);
        registers.returnRegister(r1);
    }

    private static void beqz(String label, String r1) {

        out.println("beqz " + r1 + ", " + label);
        registers.returnRegister(r1);
    }

    private static String beqz(String r1) {

        String label = tags.getCondTag();
        out.println("beqz " + r1 + ", " + label);
        registers.returnRegister(r1);
        return label;

    }

    private static void writeJumpTag(String label) {
        out.println(label + ":");
    }



    /** DATA DECLARATIONS **/

    private static String registerStringInDataArea(String str) {
        out.println(".data");
        String tag = tags.getStringTag();
        out.println(tag + ": .asciiz \"" + str + "\"");
        out.println(".text");
        return tag;
    }

    /** ERROR ROUTINES **/

    private static void defineErrorRoutine() {

        //register string
        String tag = registerStringInDataArea("[ERR_GC_1] Index de vector fora de limits");

        // print_str
        out.println(TagGenerator.ERROR_IOB + ": ");
        out.println("li $v0, 0x04");
        out.println("la $a0, " + tag);
        out.println("syscall");

        // exit
        out.println("li $v0, 0x0A");
        out.println("syscall");

    }

    /** FUNCTION CALLS AND RETURNS **/

    public static void moveSp(int offset_size) {
        out.println("addiu $sp, $sp, " + (-offset_size));
    }

    private static void storeAtStack(String register, int addr_offset) {
        out.println("sw " + register + ", " + (-addr_offset) + "($sp)");
        registers.returnRegister(register);

    }

    private static void storeAtStack(String register) {
        out.println("sw " + register + ", ($sp)");
    }

    private static void stackRegister(String register) {
        storeAtStack(register);
        moveSp(REGISTER_SIZE);
    }

    private static void stackGPRs() {
        for (String register : RegisterHandler.registerPool) {
            stackRegister(register);
        }

    }

    private static void restoreSp() {
        out.println("addiu $sp, $fp, 84");
    }

    private static void loadFromStack(String register, int offset) {
        out.println("lw " + register + ", " + (-offset) + "($sp)");
    }

    private static void restoreRegister(String register, int offset) {
        loadFromStack(register, offset);
    }

    private static void restoreGPRs() {
        int offset = 0;
        for (String register : RegisterHandler.registerPool) {
            restoreRegister(register, offset);
            offset += REGISTER_SIZE;
        }

    }

    private static void ret(String r1) {
        out.println("sw " + r1 + ", 4($fp)");
        registers.returnRegister(r1);
        out.println("jr $ra");
    }

    private static void ret(int literal) {
        String r1 = li(literal);
        out.println("sw " + r1 + ", 4($fp)");
        registers.returnRegister(r1);
        out.println("jr $ra");
    }

    /* ---------------------------------------------------------------------------------------------------- */
    /** INTERFACE FOR ASI **/

    private static final String ARITHM_NEGATE = "-";
    private static final String LOGIC_NEGATE = "NOT";

    public static String performOpu(String opu, String target_reg) {

        switch(opu) {
            case ARITHM_NEGATE:
                return neg(target_reg);

            case LOGIC_NEGATE:
                return l_not(target_reg);
            default:
                return target_reg;
        }

    }

    public static String loadVariable(int offset, boolean isGlobal, boolean isRef) {
        return lw(offset, isGlobal, isRef);
    }

    public static String loadVariable(String r_offset, boolean isGlobal, boolean isRef) {
        return lw(duplicate(r_offset), isGlobal, isRef);
    }

    public static String loadArrayCell(int base_offset, int index, boolean isGlobal, boolean isRef) {
        return loadVectorCell(base_offset, index, isGlobal, isRef);
    }

    public static String loadArrayCell(String r_base_offset, int index, boolean isGlobal, boolean isRef) {
        return loadVectorCell(r_base_offset, index, isGlobal, isRef);
    }

    public static Semantic validateAndGetOffsetPlusLoadArrayCell(int base_offset, String index_reg, int min_position, int max_position, boolean isGlobal, boolean isRef) {

        validateVectorAccess(index_reg, min_position, max_position);
        return loadVectorCellAndGetOffset(base_offset, index_reg, isGlobal, isRef);
    }

    public static Semantic validateAndGetOffsetPlusLoadArrayCell(String r_base_offset, String index_reg, int min_position, int max_position, boolean isGlobal, boolean isRef) {

        validateVectorAccess(index_reg, min_position, max_position);
        return loadVectorCellAndGetOffset(r_base_offset, index_reg, isGlobal, isRef);
    }

    public static String defineString(String str) {
        return registerStringInDataArea(str);
    }

    public static String performAdd(String r1, int literal){
        return addi(r1, literal);
    }

    public static String performAdd(int literal, String r1){
        return addi(r1, literal);
    }

    public static String performAdd(String r1, String r2){
        return add(r1, r2);
    }

    public static String performSub(String r1, int literal){
        return subi(r1, literal);
    }

    public static String performSub(int literal, String r1){
        return sub(li(literal), r1);
    }

    public static String performSub(String r1, String r2){
        return sub(r1, r2);
    }

    public static String performMul(String r1, int literal){
        return muli(r1, literal);
    }

    public static String performMul(int literal, String r1){
        return muli(r1, literal);
    }

    public static String performMul(String r1, String r2){
        return mul(r1, r2);
    }

    public static String performDiv(String r1, int literal){
        return divi(r1, literal);
    }

    public static String performDiv(int literal, String r1){
        return divi(literal, r1);
    }

    public static String performDiv(String r1, String r2){
        return div(r1, r2);
    }

    public static String performOr(String r1, int literal, boolean isInt){
        if(isInt) {
            return s_ori(r1, literal);
        } else {
            return l_ori(r1, literal);
        }
    }

    public static String performOr(String r1, String r2, boolean isInt){
        if(isInt) {
            return s_or(r1, r2);
        } else {
            return l_or(r1, r2);
        }
    }

    public static String performOr(int literal, String r2, boolean isInt){
        if(isInt) {
            return s_ori(r2, literal);
        } else {
            return l_ori(r2, literal);
        }
    }

    public static String performAnd(String r1, int literal, boolean isInt){
        if(isInt) {
            return s_andi(r1, literal);
        } else {
            return l_andi(r1, literal);
        }
    }

    public static String performAnd(String r1, String r2, boolean isInt){
        if(isInt) {
            return s_and(r1, r2);
        } else {
            return l_and(r1, r2);
        }
    }

    public static String performAnd(int literal, String r2, boolean isInt){
        if(isInt) {
            return s_andi(r2, literal);
        } else {
            return l_andi(r2, literal);
        }
    }

    public static String performComparison(int literal, String operation, String r2) {
        return cmp(li(literal), operation, r2);
    }

    public static String performComparison(String r1, String operation, int literal) {
        return cmp(r1, operation, li(literal));
    }

    public static String performComparison(String r1, String operation, String r2) {
        return cmp(r1, operation, r2);
    }

    public static String validateAndGetArrayCellOffset(String r_base_offset, String r_index, int min_position, int max_position) {
        validateVectorAccess(r_index, min_position, max_position);
        return sub(r_base_offset, muli(r_index, REGISTER_SIZE));
    }

    public static String validateAndGetArrayCellOffset(int base_offset, String r_index, int min_position, int max_position) {
        validateVectorAccess(r_index, min_position, max_position);
        return addi(muli(r_index, REGISTER_SIZE), base_offset);
    }

    public static void performAssignment(String r_offset, boolean isGlobal, int value, boolean isRef) {
        sw(li(value), r_offset, isGlobal, isRef);
    }

    public static void performAssignment(String r_offset, boolean isGlobal, String r_value, boolean isRef) {
        sw(r_value, r_offset, isGlobal, isRef);
    }

    public static void performAssignment(int offset, boolean isGlobal, int value, boolean isRef) {
        sw(li(value), offset, isGlobal, isRef);
    }

    public static void performAssignment(int offset, boolean isGlobal, String value, boolean isRef) {
        sw(value, offset, isGlobal, isRef);
    }

    public static void jumpIfTrue(String label, int literal) {
        bnez(label, li(literal));
    }

    public static void jumpIfTrue(String label, String r1) {
        bnez(label, r1);
    }

    public static String jumpIfFalse(int literal) {
        return beqz(li(literal));
    }

    public static String jumpIfFalse(String r1) {
        return beqz(r1);
    }

    public static void jumpIfFalse(String label, int literal) {
        beqz(label, li(literal));
    }

    public static void jumpIfFalse(String label, String r1) {
        beqz(label, r1);
    }

    public static void setJumpPoint(String label) {
        writeJumpTag(label);
    }

    public static String setJumpPoint() {
        String label = tags.getCondTag();
        writeJumpTag(label);
        return label;
    }

    public static String unconditionalJump() {
        String label = tags.getCondTag();
        out.println("j " + label);
        return label;
    }

    public static void unconditionalJump(String label) {
        out.println("j " + label);
    }

    public static void writeString(String tag_to_print) {
        out.println("li $v0, 0x04");
        out.println("la $a0, " + tag_to_print);
        out.println("syscall");
    }

    public static void writeInt(int value) {
        out.println("li $v0, 0x01");
        out.println("li $a0, " + value);
        out.println("syscall");
    }

    public static void writeInt(String r1) {
        out.println("li $v0, 0x01");
        out.println("move $a0, " + r1);
        out.println("syscall");
        registers.returnRegister(r1);
    }

    public static void writeBoolean(String r1) {
        String end_of_cert = tags.getCondTag();
        out.println("beqz " + r1 + ", " + end_of_cert);
        writeString(TAG_CERT);
        String end_of_false = unconditionalJump();
        setJumpPoint(end_of_cert);
        writeString(TAG_FALS);
        setJumpPoint(end_of_false);
        registers.returnRegister(r1);
    }

    public static void readInput() {
        out.println("li $v0, 0x05");
        out.println("syscall");
    }

    public static void readInt(String r_offset, boolean isGlobal, boolean isRef) {
        readInput();
        sw(RegisterHandler.V0, r_offset, isGlobal, isRef);
    }

    public static void readInt(int offset, boolean isGlobal, boolean isRef) {
        readInput();
        sw(RegisterHandler.V0, offset, isGlobal, isRef);
    }

    public static void readBool(String r_offset, boolean isGlobal, boolean isRef) {
        readInput();
        sw(simpleToLogic(RegisterHandler.V0), r_offset, isGlobal, isRef);
    }

    public static void readBool(int offset, boolean isGlobal, boolean isRef) {
        readInput();
        sw(simpleToLogic(RegisterHandler.V0), offset, isGlobal, isRef);
    }

    public static void retornar(int literal) {
        ret(literal);
    }

    public static void retornar(String r1) {
        ret(r1);
    }

    public static void stackContext() {
        stackGPRs();
        stackRegister(RegisterHandler.FP);
        stackRegister(RegisterHandler.RA);
        stackRegister(RegisterHandler.ZERO); // stack register zero to reserve the area meant for the return value
    }

    public static void moveFpToSp() {
        out.println("move $fp, $sp");
    }

    public static void stackParameter(int literal, int addr_offset) {
        storeAtStack(li(literal), addr_offset);
    }

    public static void stackParameter(String r1, int addr_offset) {
        storeAtStack(r1, addr_offset);
    }

    public static void stackArrayCell(int offset, int index, boolean isGlobal, int addr_offset, boolean isRef) {
        storeAtStack(loadArrayCell(offset, index, isGlobal, isRef), addr_offset + index * REGISTER_SIZE);
    }

    public static void stackArrayCell(String r_offset, int index, boolean isGlobal, int addr_offset, boolean isRef) {
        storeAtStack(loadArrayCell(r_offset, index, isGlobal, isRef), addr_offset + index * REGISTER_SIZE);
    }

    public static void jal(String tag) {
        out.println("jal " + tag);
    }

    public static void restoreContext() {
        restoreSp();
        restoreGPRs();
        restoreRegister(RegisterHandler.FP, OFFSET_OLD_FP);
        restoreRegister(RegisterHandler.RA, OFFSET_OLD_RA);
    }

    public static String obtainReturnValue() {
        String rDest = registers.getRegister();
        restoreRegister(rDest, OFFSET_RET_VAL);
        return rDest;
    }

    public static String deReferenceAddress(int offset) {
        // can never be a reference to a reference so fixing false to isRef
        // can never be a reference in global scope since only way to make em is through function calls
        return lw(offset, false, false);
    }

    public static String computeRealAddress(int offset, boolean isGlobal) {
        return subi(isGlobal ? RegisterHandler.GP : RegisterHandler.FP, offset);
    }

    /** SOME HELPERS THAT DO NOT  GENERATE ASSEMBLY CODE **/

    public static void returnRegister(String register) {
        registers.returnRegister(register);
    }

    public static void returnRegister(Semantic context) {
        if (context.hasRegister()) {
            registers.returnRegister(context.reg());
        }
    }

    public static void returnRegisters(Semantic context1, Semantic context2) {
        if (context1.hasRegister()) {
            registers.returnRegister(context1.reg());
        }

        if (context2.hasRegister()) {
            registers.returnRegister(context2.reg());
        }
    }

}
