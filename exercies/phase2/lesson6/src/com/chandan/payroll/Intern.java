package com.chandan.payroll;

public class Intern extends Employee {

    public Intern(String name, long baseSalaryInPaise) {
        super(name, baseSalaryInPaise);
    }

    @Override
    public long calculateBonus() {
        return 0L;
    }
}
