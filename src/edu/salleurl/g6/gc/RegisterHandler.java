package edu.salleurl.g6.gc;

import java.util.Arrays;
import java.util.LinkedList;

public class RegisterHandler {

    private LinkedList<String> registers;
    public static final String FP = "$fp";
    public static final String GP = "$gp";
    public static final String V0 = "$v0";
    public static final String RA = "$ra";
    public static final String ZERO = "$zero";
    public static final String[] registerPool = {
        "$8", "$9", "$10", "$11", "$12", "$13", "$14", "$15", "$16",
        "$17", "$18", "$19", "$20", "$21", "$22", "$23", "$24", "$25"
    };

    public RegisterHandler() {

        registers = new LinkedList<>();
        registers.addAll(Arrays.asList(registerPool));
    }

    public String getRegister() {
        return registers.pop();
    }

    private boolean isReservedRegister(String register) {
        return register.equals(GP) || register.equals(FP) || register.equals(V0);
    }

    public void returnRegister(String register) {
        if(isReservedRegister(register)) return;
        registers.push(register);
    }

    public void returnRegisters(String r1, String r2) {
        if(!isReservedRegister(r1)) {
            registers.push(r1);
        }
        if(!isReservedRegister(r2)) {
            registers.push(r2);
        }

    }


}
