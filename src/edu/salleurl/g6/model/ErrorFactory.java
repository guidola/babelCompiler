package edu.salleurl.g6.model;

public class ErrorFactory {


    public static String error(ErrorTypes type, int line, char c) {

        switch(type) {
            case LEX_UNKNOWN_CHAR:
                return "[" + type + "] " + line + ", character [" + c + "] unknown";

            case LEX_UNTERMINATED_STRING:
                return "[" + type + "] " + line + ", invalid character in string [\\n\\t\\r] ";

            default:
                return "";
        }

    }

    public static String warning(int line, String msg) {
        return "[WARNING] " + line + ", " + msg;
    }

}
