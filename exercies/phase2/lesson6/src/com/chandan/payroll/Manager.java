package com.chandan.payroll;

public class Manager extends Employee {
    private final int teamSize;

    public Manager(String name, long baseSalaryInPaise, int teamSize) {
        if (teamSize <= 0) {
            throw new IllegalArgumentException("Team Size should be greater than 0.");
        }
        super(name, baseSalaryInPaise);
        this.teamSize = teamSize;
    }

    @Override
    public long calculateBonus() {
        return (baseSalaryInPaise / 10) + (teamSize * 50000L);
    }

    public int getTeamSize() {
        return teamSize;
    }

    public static void main(String[] args) {
        Employee manager = new Manager("Ravi", 8_000_000L, 5);

        Employee intern = new Intern("Chandan", 1_500_000L);

        System.out.println(manager.describePay());
        System.out.println(intern.describePay());

        System.out.println(Payable.minimumWage());
    }
}
