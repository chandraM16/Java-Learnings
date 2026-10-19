package com.chandan.cart;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private final List<CartItem> items;

    public Cart() {
        items = new ArrayList<>();
    }

    public void add(CartItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Item Required!");
        }
        int idx = items.indexOf(item);
        if (idx == -1) {
            items.add(item);
        } else {
            CartItem c = items.get(idx);
            CartItem newCartItem = new CartItem(c.getProductName(), c.getUnitPricePaise(),
                    c.getQuantity() + item.getQuantity());
            items.set(idx, newCartItem);
        }
    }

    public boolean remove(String productName) {
        if (productName == null || productName.isBlank()) {
            throw new IllegalArgumentException("Product name Required!");
        }

        for (int idx = 0; idx < items.size(); idx++) {
            CartItem c = items.get(idx);
            if (c.getProductName().equals(productName)) {
                items.remove(idx);
                return true;
            }
        }
        return false;
    }

    public long totalPaise() {
        long total = 0;
        for (CartItem cartItem : items) {
            total += cartItem.lineTotalPaise();
        }
        return total;
    }

    public List<CartItem> items() {
        return List.copyOf(this.items);
    }
}
