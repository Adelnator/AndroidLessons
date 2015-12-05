package ru.ifmo.md.lesson4.expression;

public abstract class UnaryOperation implements Expression {
    protected final Expression first;

    public UnaryOperation(Expression first) {
        this.first = first;
    }
}
