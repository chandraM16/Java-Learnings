package com.chandan.ledger;

public class Main {
    static void main() {
        Ledger ledger = new Ledger();
        Cli cli = new Cli(ledger);
        cli.run();
    }
}
