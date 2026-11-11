package com.chandan.ledger;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
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

    public boolean delete(long id) {
        return expenseMap.remove(id) != null;
    }

    public Map<YearMonth, BigDecimal> totalsByMonth() {
        Map<YearMonth, BigDecimal> monthTotals = new TreeMap<>();
        for(Expense e : expenseMap.values()){
            monthTotals.merge(YearMonth.from(e.getDate()), e.getAmount(), BigDecimal::add);
        }
        return monthTotals;
    }

}
