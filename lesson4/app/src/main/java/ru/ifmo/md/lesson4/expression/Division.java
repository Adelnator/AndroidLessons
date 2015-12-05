package ru.ifmo.md.lesson4.expression;

public class Division extends BinaryOperation {
    public Division(Expression first, Expression second) {
        super(first, second);
    }

    @Override
    public double evaluate() {
        return first.evaluate() / second.evaluate();
    }
}
