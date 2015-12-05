package ru.ifmo.md.lesson4.expression;

public class Multiplication extends BinaryOperation {
    public Multiplication(Expression first, Expression second) {
        super(first, second);
    }

    @Override
    public double evaluate() {
        return first.evaluate() * second.evaluate();
    }
}
