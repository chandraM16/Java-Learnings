package com.chandan.ledger;

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
        return switch (command) {
            case "help" -> {
                printHelp();
                yield true;
            }
            case "quit" -> false;
            default -> {
                IO.println("Error: unknown command '" + parts[0] + "'. Type 'help'.");
                yield true;
            }
        };
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
