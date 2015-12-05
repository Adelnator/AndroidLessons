package ru.ifmo.md.lesson4.expression;

public class Subtraction extends BinaryOperation {
    public Subtraction(Expression first, Expression second) {
        super(first, second);
    }

    @Override
    public double evaluate() {
        return first.evaluate() - second.evaluate();
    }
}
