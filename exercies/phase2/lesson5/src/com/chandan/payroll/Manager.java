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
        return super.calculateBonus() + (teamSize * 50000L);
    }

    public int getTeamSize() {
        return teamSize;
    }

    public static void main(String[] args) {
        Employee e1 = new Employee("Asha", 5_000_000L);
        Employee e2 = new Manager("Ravi", 8_000_000L, 4); // a Manager, stored in an Employee-typed variable

        System.out.println(e1.calculateBonus()); // 500000 — Employee's version
        System.out.println(e2.calculateBonus()); // Manager's version runs — even though e2's TYPE is Employee
        System.out.println(((Manager) e2).getTeamSize());
    }
}
