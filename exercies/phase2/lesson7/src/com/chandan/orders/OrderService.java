package com.chandan.orders;

public class OrderService {
    private final Notifier notifier;

    public OrderService(Notifier notifier) {
        this.notifier = notifier;
    }

    public void placeOrder(String customer, long amountInPaise) {

        if (customer == null || customer.isBlank()) {
            throw new IllegalArgumentException("customer is missing!");
        }

        if (amountInPaise <= 0) {
            throw new IllegalArgumentException("Amount can't be negative or zero!");
        }
        notifier.send(customer, "Order placed: Rs " + "%.2f".formatted(amountInPaise / 100.0));
    }

    public static void main(String[] args) {
        Notifier emNotifier = new EmailNotifier();
        Notifier smsNotifier = new SmsNotifier();
        FakeNotifier fakeNotifier = new FakeNotifier();

        OrderService os1 = new OrderService(emNotifier);
        OrderService os2 = new OrderService(smsNotifier);
        OrderService os3 = new OrderService(fakeNotifier);

        os1.placeOrder("Apple", 8000000);
        os2.placeOrder("Samsung", 9000000);
        os3.placeOrder("Google", 7000000);

        System.out.println("Recorded: " + fakeNotifier.getLastMessage() + " / " + fakeNotifier.getMessageString());
    }
}