package com.chandan.cart;

import java.util.Objects;

public final class CartItem {
    private final String productName;
    private final long unitPricePaise;
    private final int quantity;

    public CartItem(String productName, long unitPricePaise, int quantity) {
        if (productName == null || productName.isBlank()) {
            throw new IllegalArgumentException("Product name is required!");
        }
        if (unitPricePaise < 1) {
            throw new IllegalArgumentException("Unit Price in Paise is required!");
        }
        if (quantity < 1) {
            throw new IllegalArgumentException("Quantity is required!");
        }
        this.productName = productName;
        this.unitPricePaise = unitPricePaise;
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CartItem c = (CartItem) o;
        return this.productName.equals(c.productName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.productName);
    }

    @Override
    public String toString() {
        double priceInRupee = this.unitPricePaise / 100.0;
        return this.productName + " x" + this.quantity + " @ Rs " + "%.2f".formatted(priceInRupee);
    }

    public String getProductName() {
        return this.productName;
    }

    public long getUnitPricePaise() {
        return this.unitPricePaise;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public long lineTotalPaise() {
        return this.unitPricePaise * this.quantity;
    }

}
