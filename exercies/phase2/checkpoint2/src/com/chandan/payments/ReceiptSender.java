package com.chandan.payments;

public interface ReceiptSender {
    void issue(Payment payment);
}
