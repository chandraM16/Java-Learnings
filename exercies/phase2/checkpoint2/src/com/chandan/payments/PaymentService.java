package com.chandan.payments;

import java.util.ArrayList;
import java.util.List;

public class PaymentService {
    private final ReceiptSender receiptSender;

    private final List<Payment> payments;

    public PaymentService(ReceiptSender receiptSender) {
        if (receiptSender == null) {
            throw new IllegalArgumentException("ReceiptSender is messing!");
        }
        this.receiptSender = receiptSender;
        this.payments = new ArrayList<>();
    }

    public Payment pay(String id, long amountPaise, PaymentMethod method) {
        Payment p = new Payment(id, amountPaise, method);
        this.payments.add(p);
        receiptSender.issue(p);
        return p;
    }

    public Payment[] history() {
        return payments.toArray(new Payment[0]);
    }

}
