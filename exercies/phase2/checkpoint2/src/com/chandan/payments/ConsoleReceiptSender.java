package com.chandan.payments;

public class ConsoleReceiptSender implements ReceiptSender {

    @Override
    public void issue(Payment payment) {
        System.out.println("Receipt: " + payment);
    }
}
