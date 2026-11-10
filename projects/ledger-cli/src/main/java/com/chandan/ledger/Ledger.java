package com.chandan.ledger;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

public class Ledger {
    private final Map<Long, Expense> expenseMap = new HashMap<>();
    private long nextId = 1;

    private static final Comparator<Expense> BY_DATE_THEN_ID = Comparator.comparing(Expense::getDate).thenComparingLong(Expense::getId);

    public Expense add(LocalDate date, BigDecimal amount, Category category, String description) {
        Expense expense = new Expense(nextId, date, amount, category, description);
        expenseMap.put(nextId, expense);
        nextId++;
        return expense;
    }

    public List<Expense> listSorted() {
        List<Expense> expenseList = new ArrayList<>(expenseMap.values());
        expenseList.sort(BY_DATE_THEN_ID);
        return expenseList;
    }

    public boolean delete(Long id) {
        if (expenseMap.containsKey(id)) {
            expenseMap.remove(id);
            return true;
        } else {
            return false;
        }
    }

}
