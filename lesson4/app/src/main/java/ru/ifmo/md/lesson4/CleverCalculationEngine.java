package ru.ifmo.md.lesson4;

import java.util.StringTokenizer;

import ru.ifmo.md.lesson4.expression.Addition;
import ru.ifmo.md.lesson4.expression.Const;
import ru.ifmo.md.lesson4.expression.Division;
import ru.ifmo.md.lesson4.expression.Expression;
import ru.ifmo.md.lesson4.expression.Multiplication;
import ru.ifmo.md.lesson4.expression.Subtraction;
import ru.ifmo.md.lesson4.expression.UnaryMinus;

public class CleverCalculationEngine implements CalculationEngine {
    private static final String DELIMITERS = "+-*/()";

    private StringTokenizer tokenizer;
    private String currentToken;

    @Override
    public double calculate(String expression) throws CalculationException {
        return parse(expression);
    }

    private double parse(String expression) throws CalculationException {
        tokenizer = new StringTokenizer(expression, DELIMITERS, true);
        next();
        Expression exp = getExpr();
        if (!currentToken.equals("$")) {
            throw new CalculationException("Unexpected end of expression (at '" + currentToken + "'");
        }
        return exp.evaluate();
    }

    private void next() {
        if (tokenizer.hasMoreElements()) {
            currentToken = tokenizer.nextToken();
        } else {
            currentToken = "$";
        }
    }

    private Expression getExpr() throws CalculationException {
        Expression result = getItem();
        while (currentToken.equals("+") || currentToken.equals("-")) {
            String tok = currentToken;
            next();
            Expression right = getItem();
            if (tok.equals("+")) {
                result = new Addition(result, right);
            } else if (tok.equals("-")) {
                result = new Subtraction(result, right);
            }
        }
        return result;
    }

    private Expression getItem() throws CalculationException {
        Expression result = getMul();
        while (currentToken.equals("*") || currentToken.equals("/")) {
            String tok = currentToken;
            next();
            if (tok.equals("*")) {
                result = new Multiplication(result, getMul());
            } else if (tok.equals("/")) {
                result = new Division(result, getMul());
            }
        }
        return result;
    }

    private Expression getMul() throws CalculationException {
        Expression result;
        if (currentToken.equals("(")) {
            next();
            result = getExpr();
            if (!currentToken.equals(")")) {
                throw new CalculationException("Unexpected token: " + currentToken + ". ')' expected.");
            }
            next();

        } else if (currentToken.equals("+")) {
            next();
            result = getMul();

        } else if (currentToken.equals("-")) {
            next();
            double d;
            try {
                d = Double.parseDouble(currentToken);
                result = new Const(d * -1);
                next();
            } catch (NumberFormatException e) {
                result = new UnaryMinus(getMul());
            }
        } else if (currentToken.equals("$")) {
            throw new CalculationException("Unexpected end of expression");
        } else {
            double d;
            try {
                d = Double.parseDouble(currentToken);
                result = new Const(d);
            } catch (NumberFormatException e) {
                if (currentToken.equals("/")) currentToken = "÷";
                if (currentToken.equals("*")) currentToken = "×";
                throw new CalculationException("Unexpected operation: " + currentToken);
            }
            next();
        }
        return result;
    }

}
