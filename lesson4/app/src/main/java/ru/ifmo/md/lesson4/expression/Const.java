package ru.ifmo.md.lesson4.expression;

public class Const implements Expression {
    private final double value;

    public Const(double value) {
        this.value = value;
    }

    @Override
    public double evaluate() {
        return value;
    }
}
