package com.chandan.payments;

import java.util.Arrays;

public class PaymentsApp {
    public static void main(String[] args) {
        FakeReceiptSender fakeReceiptSender = new FakeReceiptSender();

        PaymentService paymentService = new PaymentService(fakeReceiptSender);

        paymentService.pay("123", 30000, PaymentMethod.UPI);
        paymentService.pay("456", 50000, PaymentMethod.CARD);
        paymentService.pay("789", 90000, PaymentMethod.NETBANKING);

        System.out.println(fakeReceiptSender.getTotalPaymentCount());
        System.out.println(fakeReceiptSender.getLastPayment().getId());

        System.out.println(PaymentMethod.parse(" card "));

        try {
            paymentService.pay("112233", 99, PaymentMethod.CARD);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }

        ConsoleReceiptSender consoleReceiptSender = new ConsoleReceiptSender();
        PaymentService ps2 = new PaymentService(consoleReceiptSender);
        ps2.pay("123", 100, PaymentMethod.CARD);
        Payment[] h = paymentService.history();
        h[0] = null;

        System.out.println(Arrays.toString(paymentService.history()));
    }
}
