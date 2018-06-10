package edu.salleurl.g6.gc;

public class TagGenerator {

    public static final String ERROR_IOB = "iob";

    private int nextTag = 0;


    public String getFuncTag() {
        return "func" + nextTag++ + ": ";
    }

    public String getConversionTag() {
        return "conv" + nextTag++ + ": ";
    }

    public String getLoopTag() {
        return "loop" + nextTag++ + ": ";
    }

    public String getStringTag() {
        return "str" + nextTag++;
    }

}
