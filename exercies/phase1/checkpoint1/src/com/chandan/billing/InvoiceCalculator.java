package com.chandan.billing;

import java.util.Arrays;

public class InvoiceCalculator {
    private static final int GST_PERCENT = 18;

    private static String normalizeTier(String tier) {
        return tier.strip().toLowerCase();
    }

    private static int discountPercent(String tier) {
        return switch (tier) {
            case "gold" -> 15;
            case "silver" -> 5;
            default -> 0;
        };
    }

    private static long lineTotalInPaise(int quantity, long unitPriceInPaise) {
        return quantity * unitPriceInPaise;
    }

    public static void main(String[] args) {
        String tier = "  GOLD ";
        tier = normalizeTier(tier);

        int[] quantities = { 2, 5000, 1 };
        int[] unitPricesInPaise = { 49_900, 999_900, 15_000 };

        long subtotalInPaise = 0;
        for (int i = 0; i < quantities.length; i++) {
            subtotalInPaise += lineTotalInPaise(quantities[i], unitPricesInPaise[i]);
        }

        int discount = discountPercent(tier);
        double discountRate = (double) discount / 100;
        long discountInPaise = (long) (subtotalInPaise * discountRate);
        long gstInPaise = (subtotalInPaise - discountInPaise) * GST_PERCENT / 100;
        long totalInPaise = subtotalInPaise - discountInPaise + gstInPaise;

        System.out.println("Quantities: " + Arrays.toString(quantities));
        System.out.println("Tier: " + tier + ", discount: " + discount + "%");
        System.out.println("Subtotal: " + subtotalInPaise + " paise");
        System.out.println("Total: " + totalInPaise + " paise");
    }
}