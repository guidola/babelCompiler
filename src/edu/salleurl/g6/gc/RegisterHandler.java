package edu.salleurl.g6.gc;

import java.util.LinkedList;

public class RegisterHandler {

    private LinkedList<String> registers;
    public static final String FP = "$fp";
    public static final String GP = "$gp";
    public static final String V0 = "$v0";


    public RegisterHandler() {

        registers = new LinkedList<>();
        registers.add("$8");
        registers.add("$9");
        registers.add("$10");
        registers.add("$11");
        registers.add("$12");
        registers.add("$13");
        registers.add("$14");
        registers.add("$15");
        registers.add("$16");
        registers.add("$17");
        registers.add("$18");
        registers.add("$19");
        registers.add("$20");
        registers.add("$21");
        registers.add("$22");
        registers.add("$23");
        registers.add("$24");
        registers.add("$25");
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
