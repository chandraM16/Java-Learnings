package com.chandan.payroll;

public interface Payable {
    long calculatePay();

    default String describePay() {
        return "Pay: " + calculatePay();
    }

    static long minimumWage() {
        return 1_500_000L;
    }
}
