package com.chandan.orders;

public enum OrderStatus {
    PENDING("Awaiting payment"),
    PAID("Paid"),
    SHIPPED("On the way"),
    DELIVERED("Delivered"),
    CANCELLED("Cancelled"),
    RETURNED("Returned");

    private final String label;

    OrderStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }

    public static OrderStatus parse(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("raw string may be null or blank");
        }

        return OrderStatus.valueOf(raw.strip().toUpperCase());
    }

    public boolean canMoveTo(OrderStatus next) {
        return switch (this) {
            case PENDING -> next == PAID || next == CANCELLED;
            case PAID -> next == SHIPPED || next == CANCELLED;
            case SHIPPED -> next == DELIVERED;
            case DELIVERED -> next == RETURNED;
            case RETURNED, CANCELLED -> false;
        };
    }
}
