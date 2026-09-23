package com.chandan.loyalty;

public class MembershipCard {
    private final String memberId;
    private int points;

    public MembershipCard(String memberId, int points) {
        if (memberId == null || memberId.isBlank()) {
            throw new IllegalArgumentException("MemberId is required");
        }
        this.memberId = memberId;
        this.points = points;
    }

    private void earnPoints(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount should be greater than zero");
        }
        this.points += amount;
    }

    private void redeemPoints(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount should be greater than zero");
        }
        if (amount > this.points) {
            throw new IllegalStateException("Insufficient points");
        }
        this.points -= amount;
    }

    public static void main(String[] args) {
        MembershipCard mc = new MembershipCard("123", 0);
        mc.earnPoints(100);
        mc.redeemPoints(30);
        mc.redeemPoints(90);

        System.out.println(mc.points);
    }

}
