package com.chandan.cart;

public class CartApp {
    public static void main(String[] args) {

        Cart cart = new Cart();
        cart.add(new CartItem("Pen", 1000, 2));
        cart.add(new CartItem("Notebook", 6000, 1));
        cart.add(new CartItem("Pen", 1000, 3));

        System.out.println(cart.items());
        System.out.println("Total: Rs " + "%.2f".formatted(cart.totalPaise() / 100.0));
        System.out.println("Removed Notebook: " + cart.remove("Notebook"));
        System.out.println("Removed Eraser: " + cart.remove("Eraser"));
        System.out.println(cart.items());
        try {
            cart.items().add(new CartItem("Hack", 1, 1));
        } catch (UnsupportedOperationException e) {
            System.out.println("Blocked: " + e.getClass().getSimpleName());
        }
        System.out.println("Items: " + cart.items().size());
    }
}
