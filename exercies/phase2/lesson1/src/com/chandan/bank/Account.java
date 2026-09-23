package com.chandan.bank;

public class Account {
    private String owner;
    private double balance;

    public Account(String owner, double balance) {
        this.owner = owner;
        this.balance = balance;
    }

    public Account(String owner) {
        this(owner, 0.0);

    }

    public void deposit(double amount) {
        this.balance = this.balance + amount;
    }

    public static void main(String[] args) {
        Account a1 = new Account("Meera", 500.0);
        Account a2 = new Account("Zoe");
        a1.deposit(100.0);

        System.out.println(a1.owner + ": " + a1.balance);
        System.out.println(a2.owner + ": " + a2.balance);
    }
}