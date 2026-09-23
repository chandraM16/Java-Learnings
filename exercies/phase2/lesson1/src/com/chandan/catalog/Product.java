package com.chandan.catalog;

public class Product {
    private String name;
    private long priceInPaise;
    private int stock;

    public Product(String name, long priceInPaise) {
        this(name, priceInPaise, 0);
    }

    public Product(String name, long priceInPaise, int stock) {
        this.name = name;
        this.priceInPaise = priceInPaise;
        this.stock = stock;
    }

    public void restock(int newStock) {
        this.stock = this.stock + newStock;
    }

    public void describe() {
        double priceInRupee = (double) this.priceInPaise / 100.00;
        System.out.println(this.name + ": Rs " + "%.2f".formatted(priceInRupee) + " (" + this.stock + " in stock)");
    }

    public static void main(String[] args) {
        Product toyBike = new Product("Toy Bike", 200000);
        Product toyCar = new Product("Toy Car", 400000, 50);

        toyBike.restock(30);
        toyCar.restock(50);

        toyBike.describe();
        toyCar.describe();

    }

}
