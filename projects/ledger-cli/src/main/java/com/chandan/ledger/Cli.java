package com.chandan.ledger;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

public class Cli {
    private final Ledger ledger;

    public Cli(Ledger ledger) {
        this.ledger = ledger;
    }

    public void run() {
        IO.println("Ledger CLI. Type 'help' for commands.");
        while (true) {
            String line = IO.readln("> ");

            if (line == null) {
                break;
            }

            line = line.strip();

            if (line.isEmpty()) {
                continue;
            }

            boolean keepGoing = handle(line);

            if (!keepGoing) {
                break;
            }
        }
        IO.println("Bye.");
    }

    private boolean handle(String line) {
        String[] parts = line.split("\\s+", 2);
        String command = parts[0].toLowerCase();
        String args = parts.length > 1 ? parts[1] : "";
        return switch (command) {
            case "help" -> {
                printHelp();
                yield true;
            }
            case "quit" -> false;

            case "add" -> {
                addExpense(args);
                yield true;
            }
            case "delete" -> {
                deleteExpense(args);
                yield true;
            }
            case "months" -> {
                printMonths();
                yield true;
            }
            case "summary" -> {
                printSummary();
                yield true;
            }
            case "budget" -> {
                printBudget(args);
                yield true;
            }
            case "list" -> {
                printList();
                yield true;
            }
            default -> {
                IO.println("Error: unknown command '" + parts[0] + "'. Type 'help'.");
                yield true;
            }
        };
    }

    private void addExpense(String args) {
        String[] parts = args.split("\\s+", 4);

        if (parts.length < 4) {
            IO.println("Error: usage: add <yyyy-mm-dd> <amount> <category> <description>");
            return;
        }

        try {
            LocalDate date = parseDate(parts[0]);
            BigDecimal amount = parseAmount(parts[1]);
            Category category = parseCategory(parts[2]);
            String description = parts[3];

            Expense expense = ledger.add(date, amount, category, description);
            IO.println("Added #" + expense.getId() + ": " + expense.toLine());
        } catch (IllegalArgumentException e) {
            IO.println("Error: " + e.getMessage());
        }

    }

    private void deleteExpense(String args) {
        if (args.isBlank() || args.contains(" ")) {
            IO.println("Error: usage: delete <id>");
            return;
        }

        try {
            long id = parseId(args); // taking 1st id and ignoring rest of args
            boolean isRemoved = ledger.delete(id);
            if (isRemoved) {
                IO.println("Deleted #" + id);
            } else {
                IO.println("Error: no expense with id " + id);
            }
        } catch (IllegalArgumentException e) {
            IO.println("Error: " + e.getMessage());
        }
    }

    private void printList() {
        List<Expense> expenseList = ledger.listSorted();
        if (expenseList.isEmpty()) {
            IO.println("No expenses yet.");
            return;
        }
        BigDecimal total = BigDecimal.ZERO.setScale(2);
        for (Expense exp : expenseList) {
            IO.println(String.format("#%-3d %s", exp.getId(), exp.toLine()));
            total = total.add(exp.getAmount());
        }
        String noun = expenseList.size() == 1 ? "expense" : "expenses";
        IO.println("Total: " + total + " (" + expenseList.size() + " " + noun + ")");
    }

    private void printMonths() {
        Map<YearMonth, BigDecimal> yearMonthTotoalAmountMap = ledger.totalsByMonth();
        if (yearMonthTotoalAmountMap.isEmpty()) {
            IO.println("No expenses yet.");
            return;
        }
        for (Map.Entry<YearMonth, BigDecimal> entry : yearMonthTotoalAmountMap.entrySet()) {
            IO.println(String.format("%s %12s", entry.getKey(), entry.getValue()));
        }

    }

    private void printSummary() {
        List<CategoryStat> categoryStatSummary = ledger.summary();
        if (categoryStatSummary.isEmpty()) {
            IO.println("No expenses yet.");
            return;
        }
        for (CategoryStat categoryStat : categoryStatSummary) {
            IO.println(String.format("%-8s %3d %10s", categoryStat.getCategory(), categoryStat.getCount(), categoryStat.getTotal()));
        }
    }

    private void printHelp() {
        IO.println("Commands:");
        IO.println("  add <yyyy-mm-dd> <amount> <category> <description>");
        IO.println("  list");
        IO.println("  delete <id>");
        IO.println("  months");
        IO.println("  summary");
        IO.println("  budget <yyyy-mm>");
        IO.println("  help");
        IO.println("  quit");
        IO.println("Categories: FOOD, RENT, TRAVEL, SHOPPING, BILLS, OTHER");

    }

    private void printBudget(String args) {
        if (args.isBlank() || args.contains(" ")) {
            IO.println("Error: usage: budget <yyyy-mm>");
            return;
        }

        YearMonth yearMonth;

        try {
            yearMonth = YearMonth.parse(args);
        } catch (DateTimeParseException e) {
            IO.println("Error: invalid month '" + args + "' (expected yyyy-mm)");
            return;
        }

        Map<Category, BigDecimal> monthlySpent = ledger.spentInMonth(yearMonth);
        Map<Category, BigDecimal> monthlyBudget = ledger.budgets();

        for (Category category : Category.values()) {
            BigDecimal spent = monthlySpent.getOrDefault(category, BigDecimal.ZERO.setScale(2));
            BigDecimal budget = monthlyBudget.get(category);

            if (budget == null) {
                continue; // OTHER Category
            }

            String line = String.format("%-8s %10s / %10s", category, spent, budget);

            if (spent.compareTo(budget) > 0) {
                BigDecimal overSpent = spent.subtract(budget);
                line += "  OVER by " + overSpent;
            }
            IO.println(line);
        }
    }

    private LocalDate parseDate(String dateStr) {
        LocalDate date;
        try {
            date = LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("invalid date '" + dateStr + "' (expected yyyy-mm-dd)");
        }
        if (date.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("date is in the future: " + date);
        }
        return date;
    }

    private BigDecimal parseAmount(String amountStr) {
        BigDecimal amount;
        try {
            amount = new BigDecimal(amountStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("invalid amount '" + amountStr + "'");
        }
        if (amount.signum() <= 0 || amount.stripTrailingZeros().scale() > 2) {
            throw new IllegalArgumentException("amount must be positive with at most 2 decimals: '" + amountStr + "'");
        }
        return amount;
    }

    private Category parseCategory(String categoryString) {
        Category category;
        try {
            category = Category.valueOf(categoryString.strip().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("unknown category '" + categoryString + "' (valid: FOOD, RENT, TRAVEL, SHOPPING, BILLS, OTHER)");
        }
        return category;
    }

    private long parseId(String idString) {
        long id;
        try {
            id = Long.parseLong(idString);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("invalid id '" + idString + "'");
        }
        return id;
    }
}
