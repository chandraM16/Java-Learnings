package com.chandan.payments;

public enum PaymentMethod {
    UPI, CARD, NETBANKING;

    public long computeFee(long amountPaise) {
        return switch (this) {
            case UPI -> 0;
            case CARD -> amountPaise * 2 / 100;
            case NETBANKING -> 1000;
        };
    }

    static PaymentMethod parse(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("raw is missing");
        }
        return PaymentMethod.valueOf(raw.strip().toUpperCase());
    }
}
