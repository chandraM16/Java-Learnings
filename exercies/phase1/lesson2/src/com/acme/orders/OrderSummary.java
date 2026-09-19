package com.acme.orders;

import java.time.LocalDate;

public class OrderSummary {
    public static void main(String[] args) {
        String customer = "Asha";
        int items = 3;
        System.out.println("Customer: " + customer);
        System.out.println("Placed on " + LocalDate.now());
    }
}
