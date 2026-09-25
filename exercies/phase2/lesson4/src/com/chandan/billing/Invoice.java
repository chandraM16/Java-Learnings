package com.chandan.billing;

public final class Invoice {
    private static final String PREFIX = "INV-";
    private static int nextNumber = 1;
    private final String invoiceNumber;
    private final long amountInPaise;

    public Invoice(long amountInPaise) {
        if (amountInPaise <= 0) {
            throw new IllegalArgumentException("amount should be grater than 0.");
        }
        this.invoiceNumber = PREFIX + "%04d".formatted(nextNumber);
        nextNumber++;
        this.amountInPaise = amountInPaise;
    }

    @Override
    public String toString() {
        double amountInRupee = (double) this.amountInPaise / 100;
        return this.invoiceNumber + ": Rs " + "%.2f".formatted(amountInRupee);
    }

    public static int totalInvoicesCreated() {
        return nextNumber - 1;
    }

    public static void main(String[] args) {
        Invoice i1 = new Invoice(10000);
        Invoice i2 = new Invoice(40000);
        Invoice i3 = new Invoice(70000);

        System.out.println(i1.toString());
        System.out.println(i2.toString());
        System.out.println(i3.toString());

        System.out.println(Invoice.totalInvoicesCreated());
    }
}
