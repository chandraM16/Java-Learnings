package com.chandan.loyalty;

public class MembershipCard {
    private final String memberId;
    private int points;

    public MembershipCard(String memberId, int points) {
        if (memberId == null || memberId.isBlank()) {
            throw new IllegalArgumentException("MemberId is required");
        }
        if (points < 0) {
            throw new IllegalArgumentException("Amount should be greater than zero");

        }
        this.memberId = memberId;
        this.points = points;
    }

    public void earnPoints(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount should be greater than zero");
        }
        this.points += amount;
    }

    public void redeemPoints(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount should be greater than zero");
        }
        if (amount > this.points) {
            throw new IllegalStateException("Insufficient points");
        }
        this.points -= amount;
    }

    public String getMemberId() {
        return this.memberId;
    }

    public int getPoints() {
        return this.points;
    }

    public static void main(String[] args) {
        MembershipCard mc = new MembershipCard("123", 0);
        mc.earnPoints(100);
        mc.redeemPoints(30);
        System.out.println(mc.getPoints());
        try {
            mc.redeemPoints(90);
        } catch (Exception e) {
            System.out.println(e);

        }
    }

}
