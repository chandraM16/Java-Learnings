package com.chandan.payments;

public class FakeReceiptSender implements ReceiptSender {
    private Payment lastPayment;
    private int totalPaymentCount;

    @Override
    public void issue(Payment payment) {
        this.lastPayment = payment;
        this.totalPaymentCount++;
    }

    public Payment getLastPayment() {
        return lastPayment;
    }

    public int getTotalPaymentCount() {
        return totalPaymentCount;
    }
}
