package com.chandan.payroll;

public abstract class Employee implements Payable {
    protected final String name;
    protected final long baseSalaryInPaise;

    public Employee(String name, long baseSalaryInPaise) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name id Required!");
        }
        if (baseSalaryInPaise < 0) {
            throw new IllegalArgumentException("baseSalaryInPaise can't be negative!");

        }
        this.name = name;
        this.baseSalaryInPaise = baseSalaryInPaise;
    }

    public abstract long calculateBonus();

    @Override
    public String toString() {
        return this.name + ": bonus=" + calculateBonus();
    }

    @Override
    public long calculatePay() {
        return baseSalaryInPaise;
    }

}