package com.chandan.ledger;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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
            case "add" -> {
                addExpense(args);
                yield true;
            }
            case "list" -> {
                printList();
                yield true;
            }
            case "quit" -> false;
            default -> {
                IO.println("Error: unknown command '" + parts[0] + "'. Type 'help'.");
                yield true;
            }
        };
    }

    private void addExpense(String args) {
        String[] parts = args.split("\\s+", 4);

        if(parts.length < 4){
            IO.println("Error: usage: add <yyyy-mm-dd> <amount> <category> <description>");
            return;
        }

        try {
            LocalDate date = LocalDate.parse(parts[0]);
            BigDecimal amount = new BigDecimal(parts[1]);
            Category category = Category.valueOf(parts[2].toUpperCase());
            String description = parts[3];

            Expense expense = ledger.add(date, amount, category, description);
            IO.println("Added #" + expense.getId() + ": " + expense.toLine());
        } catch (RuntimeException e) {
            IO.println(e.getMessage());
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

    private void printHelp() {
        IO.println("Commands:");
        IO.println("  add <yyyy-mm-dd> <amount> <category> <description>");
        IO.println("  list");
        IO.println("  delete <id>");
        IO.println("  help");
        IO.println("  quit");
        IO.println("Categories: FOOD, RENT, TRAVEL, SHOPPING, BILLS, OTHER");

    }
}
