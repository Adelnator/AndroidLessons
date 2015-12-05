package ru.ifmo.md.lesson4.expression;

public class UnaryMinus extends UnaryOperation {
    public UnaryMinus(Expression first) {
        super(first);
    }

    @Override
    public double evaluate() {
        return -first.evaluate();
    }
}
