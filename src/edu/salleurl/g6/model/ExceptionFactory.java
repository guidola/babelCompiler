package edu.salleurl.g6.model;

import edu.salleurl.g6.alex.Alex;

public class ExceptionFactory {


    private static final String firstBit = "Syntactic error found at line ";
    private static final String secondBit = ". Expected one of ";
    private static final String thirdBit = " and got ";

    private static final TokenType[] expected_ops = {
            TokenType.SIMPLE_ARITHMETIC_OPERATOR,
            TokenType.OR
    };

    private static final TokenType[] expected_tipus = {
            TokenType.SIMPLE_TYPE,
            TokenType.VECTOR
    };

    private static final TokenType[] expected_inst = {
            TokenType.REPETIR,
            TokenType.MENTRE,
            TokenType.SI,
            TokenType.IDENTIFIER,
            TokenType.ESCRIURE,
            TokenType.LLEGIR,
            TokenType.RETORNAR
    };

    private static final TokenType[] expected_dec_var = {
            TokenType.SIMPLE_TYPE,
            TokenType.VECTOR,
            TokenType.CONST
    };

    private static final TokenType[] expected_factor = {
            TokenType.INTEGER_CONSTANT,
            TokenType.LOGIC_CONSTANT,
            TokenType.STRING,
            TokenType.IDENTIFIER,
            TokenType.PARENTHESIS_OPEN
    };

    private static final TokenType[] expected_opb = {
            TokenType.COMPLEX_ARITHMETIC_OPERATOR,
            TokenType.AND
    };

    private static String generateExpectedTypesString(TokenType[] expected) {
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < expected.length; i++) {
            if (i < expected.length - 1) {
                sb.append(expected[i]);
                sb.append(", ");
                continue;
            }
            sb.append(expected[i]);
        }

        return sb.toString();
    }

    public static SyntacticException ops(TokenType consumed) {
        return new SyntacticException(SyntacticExceptions.OPS, expected_ops, consumed,
                firstBit + Alex.getLine() + secondBit + generateExpectedTypesString(expected_ops) + thirdBit + consumed
        );
    }

    public static SyntacticException tipus(TokenType consumed) {
        return new SyntacticException(SyntacticExceptions.TIPUS, expected_tipus, consumed,
                firstBit + Alex.getLine() + secondBit + generateExpectedTypesString(expected_tipus) + thirdBit + consumed
        );
    }

    public static SyntacticException inst(TokenType consumed) {
        return new SyntacticException(SyntacticExceptions.INST, expected_inst, consumed,
                firstBit + Alex.getLine() + secondBit + generateExpectedTypesString(expected_inst) + thirdBit + consumed
        );
    }

    public static SyntacticException decVar(TokenType consumed) {
        return new SyntacticException(SyntacticExceptions.DEC_VAR, expected_dec_var, consumed,
                firstBit + Alex.getLine() + secondBit + generateExpectedTypesString(expected_dec_var) + thirdBit + consumed
        );
    }

    public static SyntacticException factor(TokenType consumed) {
        return new SyntacticException(SyntacticExceptions.FACTOR, expected_factor, consumed,
                firstBit + Alex.getLine() + secondBit + generateExpectedTypesString(expected_factor) + thirdBit + consumed
        );
    }

    public static SyntacticException opb(TokenType consumed) {
        return new SyntacticException(SyntacticExceptions.OPB, expected_opb, consumed,
                firstBit + Alex.getLine() + secondBit + generateExpectedTypesString(expected_opb) + thirdBit + consumed
        );
    }

    public static SyntacticException consume(TokenType expected, TokenType consumed) {
        return new SyntacticException(SyntacticExceptions.CONSUME, new TokenType[]{expected}, consumed,
                firstBit + Alex.getLine() +  ". Expected " + generateExpectedTypesString(new TokenType[]{expected})
                        + thirdBit + consumed
        );
    }

}
