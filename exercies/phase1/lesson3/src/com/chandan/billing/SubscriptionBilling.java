package com.chandan.billing;

import java.math.BigDecimal;

public class SubscriptionBilling {
    public static void main(String[] args) {
        int subscribers = 2_500_000;
        int yearlyPlanSubscribers = 412_345;
        long priceInPaisePerMonth = 99_999L;

        // Total monthly revenue in Paise
        long totalMonthlyRevenueInPaise = priceInPaisePerMonth * subscribers;

        // Total monthly revenue in Rupee
        BigDecimal totalMonthlyRevenueInRupee = BigDecimal.valueOf(totalMonthlyRevenueInPaise, 2);

        double yearlyPlanPercentage = ((double) yearlyPlanSubscribers / subscribers) * 100;

        // Average revenue per day over a 30-day month, in rupees, with decimals
        double avgDailyRevenueInRupees = totalMonthlyRevenueInPaise / 100.00 / 30;

        System.out.println("Monthly Revenue (in Paise) -> " + totalMonthlyRevenueInPaise);

        System.out.println("Monthly Revenue (in Rupee) -> " + totalMonthlyRevenueInRupee);

        System.out.println(
                "The percentage of subscribers who are on a yearly plan -> " + yearlyPlanPercentage
                        + " %");

        System.out.println("Average revenue per day over a 30-day month, in rupees, with decimals > "
                + String.format("%,.2f", avgDailyRevenueInRupees));
    }
}
