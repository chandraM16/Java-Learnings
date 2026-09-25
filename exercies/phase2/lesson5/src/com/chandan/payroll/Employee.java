public class Employee {
    protected final String name;
    protected final long baseSalaryInPaise;

    public Employee(String name, long baseSalaryInPaise) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name id Required!");
        }
        if (baseSalaryInPaise <= 0) {
            throw new IllegalArgumentException("baseSalaryInPaise should be greater than 0!");

        }
        this.name = name;
        this.baseSalaryInPaise = baseSalaryInPaise;
    }

    public long calculateBonus() {
        return baseSalaryInPaise / 10; // flat 10%
    }

    @Override
    public String toString() {
        return this.name + ": bonus=" + calculateBonus();
    }

}