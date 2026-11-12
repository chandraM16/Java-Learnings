package com.chandan.ledger;

import java.util.StringJoiner;

public enum Category {
    FOOD, RENT, TRAVEL, SHOPPING, BILLS, OTHER;

    public static String allNames() {
        StringJoiner stringJoiner = new StringJoiner(", ");
        for (Category c : Category.values()) {
            stringJoiner.add(c.name());
        }
        return stringJoiner.toString();
    }
}
