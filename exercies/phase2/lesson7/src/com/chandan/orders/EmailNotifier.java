package com.chandan.orders;

public class EmailNotifier implements Notifier {
    @Override
    public void send(String to, String messageString) {
        System.out.println("Email Service -> " + to + " and " + messageString);
    }
}
