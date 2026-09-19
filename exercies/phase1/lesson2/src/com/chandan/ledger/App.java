package com.chandan.ledger;

import java.time.LocalDate;

public class App {
    public static void main(String[] args) {
        LocalDate today = LocalDate.now();
        System.out.println("Ledger started on " + today);
        System.out.println("Number of arguments: " + args.length);
    }
}
