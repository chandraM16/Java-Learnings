package com.chandan.ledger;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ExpenseCheck {
    static void main() {
        Expense lunch = new Expense(1, LocalDate.parse("2026-11-10"), BigDecimal.valueOf(249.5), Category.FOOD, "Lunch at office");
        Expense rent = new Expense(2, LocalDate.parse("2026-11-04"), BigDecimal.valueOf(18000), Category.RENT, "November rent");

        IO.println(lunch.toLine());
        IO.println(rent.toLine());
        IO.println(rent.getAmount().add(lunch.getAmount()));
        IO.println(0.1 + 0.2);
        IO.println(new BigDecimal("0.1").add(new BigDecimal("0.2")));
        IO.println(lunch);
    }
}
