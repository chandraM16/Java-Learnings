package com.chandan.orders;

public class FakeNotifier implements Notifier {
    private String lastTo;
    private String lastMessage;

    public String getLastTo() {
        return this.lastTo;
    }

    public String getLastMessage() {
        return this.lastMessage;
    }

    @Override
    public void send(String to, String message) {
        this.lastTo = to;
        this.lastMessage = message;
    }

}
