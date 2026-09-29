package com.chandan.payments;

import java.util.Objects;

public final class Payment {
    private final String id;
    private final long amountPaise;
    private final PaymentMethod method;

    Payment(String id, long amountPaise, PaymentMethod method) {
        if (amountPaise < 100) {
            throw new IllegalArgumentException("Amount can't be less than 100 Paise!");
        }
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id is messing!");
        }
        this.id = id;
        this.amountPaise = amountPaise;
        this.method = method;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Payment p = (Payment) o;
        return Objects.equals(this.id, p.id);

    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        double amountInRupee = amountPaise / 100.0;
        double fee = method.computeFee(amountPaise);
        return id + ": Rs " + "%.2f".formatted(amountInRupee) + " via " + method + " (fee Rs "
                + "%.2f".formatted(fee / 100.0) + ")";
    }

    public String getId() {
        return id;
    }

}
