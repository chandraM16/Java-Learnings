package com.chandan.ledger;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class Expense implements Comparable<Expense> {
    private final long id;
    private final LocalDate date;
    private final BigDecimal amount;
    private final Category category;
    private final String description;

    public Expense(long id, LocalDate date, BigDecimal amount, Category category, String description) {
        this.id = id;
        this.date = date;
        this.amount = amount.setScale(2);
        this.category = category;
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public Category getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public String toLine() {
        return String.format("%s %-8s %10s  %s", date, category, amount, description);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Expense e = (Expense) obj;
        return e.id == this.id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public String toString() {
        return toLine();
    }

    @Override
    public int compareTo(Expense o) {
        return Long.compare(this.id, o.id);
    }
}
