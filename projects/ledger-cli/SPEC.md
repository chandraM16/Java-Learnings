# Project 1 — Ledger CLI

A personal expense tracker that runs in the terminal. You type commands at a `> ` prompt; it keeps expenses in memory (file persistence comes in Phase 6).

Runs alongside Phases 4–5. Built in milestones — each milestone ends with a review.

| Milestone | Scope | Uses | Folded reqs |
|---|---|---|---|
| **M1** (now) | Domain model, REPL loop, `add` / `list` / `delete` / `help` / `quit`, input validation | classes, enum, `Map`/`Set`, `Comparable`, switch expressions, `BigDecimal`, `LocalDate` | 1, 2, 3, 4, 7, 10 |
| M2 | `summary`, `months`, `top`, `budget` reports — written with plain loops + `Map` methods | `computeIfAbsent`, `merge`, `TreeMap`, `Comparator` chains, `Map.copyOf` | 5, 6, 8, 9 |
| M3 (after 4.4–4.6) | Refactor M2 reports to streams + `Collectors`; decide which types become records | streams, collectors, records, `Optional` | — |
| M4 (Phase 5) | Custom exception type, validation layer, narrow catches reviewed | exceptions | — |
| M5 (Phase 6) | `save` / `load` to a CSV file | NIO `Files`, `Path` | — |

---

## M1 — what to build

### Package layout (suggested, you may change it — but justify)

```
src/main/java/com/chandan/ledger/
  Main.java        // ≤ 15 lines. Creates things, starts the loop. No logic.
  Category.java    // enum
  Expense.java     // one expense
  Ledger.java      // holds expenses; add / delete / list-sorted; no printing
  Cli.java         // REPL loop: read line → parse → call Ledger → print
```

Rule: **`Ledger` never prints and never reads input.** `Cli` never stores expenses. (Same reason a Node service layer doesn't call `res.send`.)

### Commands

```
add <yyyy-mm-dd> <amount> <category> <description>
list
delete <id>
help
quit
```

- Commands are case-insensitive (`LIST` works). Category is case-insensitive (`food` → `FOOD`).
- Blank lines are ignored (no output, just the next prompt).
- Description = everything after the category, kept as typed (may contain spaces).
- Ids start at 1 and are **never reused**, even after `delete`.
- `quit` **or end of input** (`IO.readln` returns `null`, e.g. Ctrl+D or end of a piped file) prints `Bye.` and exits.

### Categories

`FOOD, RENT, TRAVEL, SHOPPING, BILLS, OTHER`

### Validation (in this order, stop at the first error)

| Check | Output |
|---|---|
| fewer than 4 parts after `add` | `Error: usage: add <yyyy-mm-dd> <amount> <category> <description>` |
| date doesn't parse | `Error: invalid date '2026-13-01' (expected yyyy-mm-dd)` |
| date after today | `Error: date is in the future: 2099-01-01` |
| amount doesn't parse | `Error: invalid amount 'abc'` |
| amount ≤ 0 or > 2 decimals | `Error: amount must be positive with at most 2 decimals: '99.999'` |
| unknown category | `Error: unknown category 'GROCERY' (valid: FOOD, RENT, TRAVEL, SHOPPING, BILLS, OTHER)` |
| `delete` with no / extra args | `Error: usage: delete <id>` |
| `delete` id not a number | `Error: invalid id 'x'` |
| `delete` id not known | `Error: no expense with id 7` |
| unknown command | `Error: unknown command 'foo'. Type 'help'.` |

Bad input **never crashes** the program and never prints a stack trace.

### Output formats

- Start: `Ledger CLI. Type 'help' for commands.`
- Prompt: `> ` (no newline — `IO.readln("> ")` does this for you)
- `add` success: `Added #1: ` + expense line
- Expense line: `String.format("%s %-8s %10s  %s", date, category, amount, description)` — amount always 2 decimals
- `list`: each row `String.format("#%-3d %s", id, expenseLine)`, sorted by **date ascending, then id ascending**, then
  `Total: 18369.60 (4 expenses)` — `(1 expense)` when singular. Empty ledger → `No expenses yet.`
- `delete` success: `Deleted #4`
- `help`: exactly what `test-data/expected-m1.txt` shows.

### Done = this command prints nothing

```bash
cd projects/ledger-cli && mvn -q compile && java -cp target/classes com.chandan.ledger.Main < test-data/sample-m1.txt | diff - test-data/expected-m1.txt
```

`diff` printing nothing means your output matches byte-for-byte. Any line it prints is a mismatch (`<` = yours, `>` = expected).
Note: the sample uses `2099-01-01` for the future-date check, so it stays valid until 2099.

---

## New things you need for M1

### `BigDecimal` — money is never `double`

`double` is binary floating point; it can't store 0.1 exactly. `BigDecimal` stores an exact decimal number (digits + a "scale" = number of digits after the point).

```java
System.out.println(0.1 + 0.2);                                        // → 0.30000000000000004
System.out.println(new BigDecimal("0.1").add(new BigDecimal("0.2"))); // → 0.3

BigDecimal a = new BigDecimal("249.5");   // ✅ build from a String
a.scale();                                // → 1
a.setScale(2);                            // → 249.50   (adds zeros; throws if it would have to round)
a.signum();                               // → 1  (-1 negative, 0 zero, 1 positive)
new BigDecimal("99.999").stripTrailingZeros().scale(); // → 3   ("too many decimals" check)
new BigDecimal("18000").stripTrailingZeros().scale();  // → -3  (fine: ≤ 2)
new BigDecimal("abc");                    // → NumberFormatException
```

❌/✅ gotcha — `equals` vs `compareTo`:
```java
new BigDecimal("2.0").equals(new BigDecimal("2.00"));    // → false  (different scale!)
new BigDecimal("2.0").compareTo(new BigDecimal("2.00")); // → 0
```
→ This is exactly the "`compareTo` inconsistent with `equals`" situation from CP3 E2, inside the JDK itself. Put a `BigDecimal` into a `HashSet` and a `TreeSet` and they disagree. Keep it in mind for requirement (7).

`BigDecimal` is immutable: `total.add(x)` returns a **new** value — `total = total.add(x);`, not `total.add(x);`.

### `LocalDate`

```java
LocalDate d = LocalDate.parse("2026-11-03");  // ISO yyyy-mm-dd
LocalDate.parse("2026-13-01");                // → DateTimeParseException
d.isAfter(LocalDate.now());                   // → false
d.toString();                                 // → "2026-11-03"
```
`LocalDate` implements `Comparable<LocalDate>` — usable directly in `Comparator.comparing(...)`.

### `String.split` with a limit

```java
"add 2026-11-03 249.50 food Lunch at office".split("\\s+", 2);
// → ["add", "2026-11-03 249.50 food Lunch at office"]
"2026-11-03 249.50 food Lunch at office".split("\\s+", 4);
// → ["2026-11-03", "249.50", "food", "Lunch at office"]   ← description keeps its spaces
```

---

## Requirements checklist (graded in review)

Folded in from CP1 / Phase 3 / CP3:

- [ ] **(1)** Each input line is parsed **once** into typed values (`LocalDate`, `BigDecimal`, `Category`) — no method re-splits or re-parses the raw string later.
- [ ] **(2)** Command dispatch uses an arrow **switch expression** (`yield`s / returns a value), not a switch statement or if/else chain.
- [ ] **(3)** `Main` ≤ 15 lines; logic in small private methods (rule of thumb: no method > ~20 lines).
- [ ] **(4)** Output matches `expected-m1.txt` exactly (the `diff` above is empty).
- [ ] **(7)** `Expense implements Comparable<Expense>`, and its `compareTo` is **consistent with `equals`**. You decide what `equals` means for an expense — write 2 lines in a comment above `equals` saying why.
- [ ] **(10)** "Is this id known?" for `delete` is answered by a hash-based lookup (`Set.contains` or `Map.containsKey`/`remove`), **not** a loop over a `List`. In your submission say: which operation your container makes cheap, and its Big-O.

Production habits:

- [ ] Narrow `catch` only: `DateTimeParseException`, `NumberFormatException`, `IllegalArgumentException` — never `catch (Exception e)`.
- [ ] Boundary checks: empty input, missing args, extra args for `delete`, zero/negative amount, future date.
- [ ] `Ledger`'s internal collection is `private`, and nothing outside can modify it (what does your "get all" method return?).
- [ ] No `double`/`float` anywhere near money.

### Design questions — answer in 1–2 lines each in your submission

1. What does `equals` mean for `Expense` — same id, or same date+amount+category+description? What breaks with the other choice?
2. `list` sorts by date then id. Is that the **natural order** (`compareTo`) or a separate `Comparator`? Why?
3. Where do you keep `nextId`, and why there?

---

## How to submit M1 (in this order)

1. **Paste the `diff` command's output** (empty = pass). If not empty, paste it anyway — we fix from there.
2. Answers to the 3 design questions + the Big-O line for (10).
3. Then the code (just say "done", I'll read the files).

---

## M2 — reports

M1 commands stay exactly as they are. M2 adds three read-only reports. **Ledger computes, Cli prints** — same rule as M1.

### New commands

```
months              total spent per month, oldest month first
summary             per category: count + total, most-used category first
budget <yyyy-mm>    spent vs monthly budget for each budgeted category
```

`help` lists them after `delete <id>` (see `expected-m2.txt`).

### Output formats

- `months`: one row per month, `String.format("%s %12s", yearMonth, total)`, **chronological**. Empty ledger → `No expenses yet.`
- `summary`: one row per category that has expenses, `String.format("%-8s %3d %10s", category, count, total)`, sorted by **count descending, then category name ascending**. Empty → `No expenses yet.`
- `budget <yyyy-mm>`: one row per category **that has a budget**, in enum order (`FOOD, RENT, TRAVEL, SHOPPING, BILLS`), `String.format("%-8s %10s / %10s", category, spent, limit)`; when spent > limit append `"  OVER by " + (spent - limit)`. Spent = 0.00 when nothing that month. Exactly at the limit is not over.
  - no / extra args → `Error: usage: budget <yyyy-mm>`
  - bad month → `Error: invalid month '2026-13' (expected yyyy-mm)`

### Monthly budgets (fixed config)

| FOOD | RENT | TRAVEL | SHOPPING | BILLS | OTHER |
|---|---|---|---|---|---|
| 6000.00 | 18000.00 | 2000.00 | 3000.00 | 2500.00 | — (no budget) |

### Done = empty diff

```bash
mvn -q compile && java -cp target/classes com.chandan.ledger.Main < test-data/sample-m2.txt | diff - test-data/expected-m2.txt
```
(run from inside `projects/ledger-cli`). M1's diff must **still** be empty too.

### Requirements checklist (graded in review)

- [ ] **(5)** `months` is first written with a `HashMap` — run it, see the order — then switched to a `TreeMap`. Say in one line what changed and why.
- [ ] **(6)** `summary` sorts with a `Comparator` **constant** built as `comparingInt(...).reversed().thenComparing(...)`.
- [ ] **(8)** Written with plain loops, no streams: grouping with `computeIfAbsent`, totals with `merge`, a lookup with `getOrDefault`.
- [ ] **(9)** Budgets live in a map published with `Map.copyOf(...)`. Once, add a deliberate `put` on it, run, paste the exception, then delete that line.
- [ ] The category list in `help` and in the unknown-category error is built from `Category.values()`, not typed twice (M1 carry-over).
- [ ] No streams/Collectors yet — M3 rewrites these loops with them, and the diff must stay empty.

### New things you need for M2

```java
YearMonth.from(LocalDate.parse("2026-11-03"))  // → 2026-11
YearMonth.parse("2026-11")                     // → 2026-11
YearMonth.parse("2026-13")                     // → DateTimeParseException
// YearMonth is Comparable → works as a TreeMap key, sorted chronologically
```
