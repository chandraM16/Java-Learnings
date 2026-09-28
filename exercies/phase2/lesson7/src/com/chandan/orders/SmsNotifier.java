package com.chandan.orders;

public class SmsNotifier implements Notifier {
    @Override
    public void send(String to, String messageString) {
        System.out.println("SMS Service -> " + to + " and " + messageString);
    }
}
