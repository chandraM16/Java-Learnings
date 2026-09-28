# Checkpoint 2: Object-Oriented Java (Lessons 2.1–2.8)

Issued: 2026-09-28 · Gate for Phase 3 (Collections & Generics) and Project 1 (Ledger CLI)

## Rules

- **Part A:** write every prediction down **before** running anything. Then run it and report both your prediction and the actual output.
- **Parts B, C, E:** compile and run everything. Paste the real output.
- **Part D:** your own words, 2–3 sentences each. Don't copy from the notes.
- "I don't know" is a fine answer for any item.
- Folder for everything: `exercies/phase2/checkpoint2/`

## Scoring (40 points)


| Part                       | Points                             |
| -------------------------- | ---------------------------------- |
| A: Predict                 | 8 (1 each)                         |
| B: Debug                   | 14 (7 bugs × [category 1 + fix 1]) |
| C: Build                   | 10 (checklist items)               |
| D: Explain                 | 8 (4 × 2)                          |
| E: Checkpoint 1 carry-over | not scored, **required**           |


**To pass:** at least 32/40, **and** at least 5/8 on Part D, **and** every Part C checklist item ticked, **and** Part E done (this also closes Checkpoint 1).
If you don't pass, we do a short review of the weak areas and you retake only that part.

---



## Part A: Predict (8 points)

Save as `exercies/phase2/checkpoint2/src/PartA.java`.

```java
interface Priced {
    long pricePaise();

    default String tag() {
        return "[" + pricePaise() / 100 + " Rs]";
    }
}

abstract class Item implements Priced {
    private static int created = 0;
    protected final String name;

    Item(String name) {
        this.name = name;
        created++;
    }

    static int count() {
        return created;
    }

    public String describe() {
        return name + " " + tag();
    }
}

class Book extends Item {
    private final long paise;

    Book(String name, long paise) {
        super(name);
        this.paise = paise;
    }

    @Override
    public long pricePaise() {
        return paise;
    }
}

class Ebook extends Book {
    Ebook(String name, long paise) {
        super(name, paise);
    }

    @Override
    public long pricePaise() {
        return super.pricePaise() / 2;
    }

    @Override
    public String describe() {
        return "e:" + super.describe();
    }
}

class Point {
    final int x, y;

    Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point p = (Point) o;
        return x == p.x && y == p.y;
    }
}

enum Level { LOW, MID, HIGH }

class Box {
    private final int[] data;

    Box(int[] data) {
        this.data = data;
    }

    int first() {
        return data[0];
    }
}

public class PartA {
    public static void main(String[] args) {
        Item a = new Book("Java", 49900);
        Item b = new Ebook("Spring", 60000);
        System.out.println("1: " + a.describe());
        System.out.println("2: " + b.describe());
        System.out.println("3: " + Item.count());

        Point p = new Point(1, 2);
        Point q = new Point(1, 2);
        System.out.println("4: " + (p == q) + ", " + p.equals(q));

        Level l = Level.valueOf("MID");
        System.out.println("5: " + (l == Level.MID) + " " + l.ordinal() + " " + Level.values()[2]);

        int[] raw = {7, 8};
        Box box = new Box(raw);
        raw[0] = 99;
        System.out.println("6: " + box.first());

        Item c = b;
        System.out.println("7: " + (c instanceof Book) + " " + (c.getClass() == Book.class));

        // 8: if this line were uncommented, what happens?
        // long total = c.pricePaise() + ((Ebook) a).pricePaise();
    }
}
```

- Lines 1–7: predict the **exact** output, character by character (brackets, spaces, commas).
- Line 8: does it compile? If yes, what happens when it runs? Name the error category.

---



## Part B: Debug `LibraryApp` (14 points)

Save as `exercies/phase2/checkpoint2/src/com/chandan/library/LibraryApp.java`.

There are **7 bugs**. For each one, give:

- the line,
- its category: **compile time**, **run time**, or **silently wrong**,
- the fixed line.

Fixed, the program must print **exactly**:

```
STUDENT S1 (0 loans)
same member: true
S1 loans: 3, T1 loans: 0
GUEST G1 (0 loans)
Parsed: STAFF, max loans 10
```

```java
package com.chandan.library;

import java.util.Objects;

enum MemberType {
    STUDENT(3), STAFF(10), GUEST(1);

    private final int maxLoans;

    MemberType(int maxLoans) {
        this.maxLoans = maxLoans;
    }

    int maxLoans() {
        return maxLoans;
    }
}

abstract class Member {
    protected final String id;
    protected static int activeLoans;

    Member(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id is required");
        }
        id = id;
    }

    abstract MemberType type();

    boolean canBorrow() {
        return activeLoans <= type().maxLoans();
    }

    void borrow() {
        if (!canBorrow()) {
            throw new IllegalStateException(id + " has reached the loan limit");
        }
        activeLoans++;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Member other = (Member) o;
        return id == other.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public String toSting() {
        return type() + " " + id + " (" + activeLoans + " loans)";
    }
}

class Student extends Member {
    Student(String id) {
        super(id);
    }

    @Override
    MemberType type() {
        return MemberType.STUDENT;
    }
}

class Staff extends Member {
    Staff(String id) {
        super(id);
    }

    @Override
    MemberType type() {
        return MemberType.STAFF;
    }
}

class Guest extends Member {
    Guest(String id) {
        super(id);
    }
}

public class LibraryApp {
    public static void main(String[] args) {
        int number = 1;
        Member s1 = new Student("S" + number);
        Member s2 = new Student("S1");
        Member staff = new Staff("T1");
        Member guest = new Guest("G1");

        System.out.println(s1);
        System.out.println("same member: " + s1.equals(s2));

        for (int i = 0; i < 4; i++) {
            if (s1.canBorrow()) {
                s1.borrow();
            }
        }
        System.out.println("S1 loans: " + s1.activeLoans + ", T1 loans: " + staff.activeLoans);
        System.out.println(guest);

        MemberType parsed = MemberType.valueOf(" staff ".strip());
        System.out.println("Parsed: " + parsed + ", max loans " + parsed.maxLoans());
    }
}
```

Compile:

```bash
javac -d out src/com/chandan/library/LibraryApp.java
```

Remember: javac reports errors in stages. Fix, recompile, and repeat until it runs.

---



## Part C: Build a payments module (10 points)

Package `com.chandan.payments`, folder `exercies/phase2/checkpoint2/src/com/chandan/payments/`.

**Fee rules** (all money in `long` paise):


| Method       | Fee                            |
| ------------ | ------------------------------ |
| `UPI`        | 0                              |
| `CARD`       | 2% of the amount, rounded down |
| `NETBANKING` | flat ₹10 (1000 paise)          |


**What to build:**

- `PaymentMethod`: enum with the 3 methods above.
- `Payment`: `id`, `amountPaise`, `method`. The amount must be **at least ₹1.00 (100 paise)**, and exactly 100 is allowed. `toString()` prints exactly:
  ```
  PAY-1: Rs 500.00 via CARD (fee Rs 10.00)
  ```
- `ReceiptSender`: interface with `void issue(Payment payment);`
- `ConsoleReceiptSender`: prints `Receipt:`  followed by the payment.
- `FakeReceiptSender`: a test double. It records the last payment it received and how many it has received.
- `PaymentService`: `Payment pay(String id, long amountPaise, PaymentMethod method)` creates the payment, calls the sender, and remembers it. `Payment[] history()` returns the payments made so far.



### Requirements checklist: tick every item before submitting

- [ ] 1. Package `com.chandan.payments`, one public type per file, compiled with `javac -d out src/com/chandan/payments/*.java` and run by its fully qualified name
- [ ] 2. `PaymentMethod` computes the fee in a method that uses `switch (this)` with **no** `default`, and has `static PaymentMethod parse(String raw)` that cleans the input first
- [ ] 3. `Payment` is a `final` class; all its fields are `private final`; the constructor rejects a blank id and an amount below 100 paise (**100 itself is accepted**)
- [ ] 4. `Payment` overrides `equals`, `hashCode` and `toString`, all with `@Override`; two payments are equal when their **ids** are equal
- [ ] 5. `FakeReceiptSender` only records (no printing, no validation) and exposes what it recorded through getters
- [ ] 6. `PaymentService` receives its `ReceiptSender` through the constructor and keeps it in a `private final` field typed as the **interface**
- [ ] 7. `history()` returns a **copy** holding only the payments made so far; `main` proves that changing the returned array doesn't change the service
- [ ] 8. Invalid input is caught with the **narrowest** exception type, and only `getMessage()` is printed
- [ ] 9. `main`, using `FakeReceiptSender`, shows all of these:
  - 3 payments, one per method, each printed
  - the fake's count and last payment id, read through its getters
  - `PaymentMethod.parse(" card ")`
  - a 99-paise payment rejected, and a 100-paise payment accepted
- [ ] 10. The full output is pasted

---



## Part D: Explain it (8 points, 2–3 sentences each, your own words)

1. `Employee e = new Manager(...)`. `e.calculateBonus()` runs `Manager`'s version, but `e.getTeamSize()` doesn't compile. Explain both, using the words **declared type** and **runtime type**.
2. `Member` overrides `equals()` to compare by id, but doesn't override `hashCode()`. Nothing crashes. What breaks, where would you notice it, and why?
3. A teammate writes `class AuditedCounter extends LibraryCounter` and overrides both `add` and `addAll`. The counts come out doubled. Why does that happen, and what design avoids it?
4. Why model order status as an enum instead of a `String`? Give one thing the enum catches at **compile time** and one it catches at **run time**, which a `String` would let through.

---



## Part E: Close Checkpoint 1 (required, not scored)

Refactor your existing `exercies/phase1/checkpoint1/src/com/chandan/delivery/DeliveryStats.java`. These are the 4 checklist items still open from Checkpoint 1, plus two exact-output details:

- [ ] Item 2: add one `private static int[] validMinutes(String[] args)` that parses the arguments **once** and returns only the valid minutes. Every other method takes that `int[]`, not `String[]`. Today the arguments are parsed in 4 different methods.
- [ ] Item 3: decide fast/normal/late with an **arrow switch** on `minutes / 15`.
- [ ] Item 7: with no valid deliveries, print two separate lines:
  ```
  Deliveries: 0 valid, 2 invalid
  No valid deliveries
  ```
- [ ] Item 9: `main` is 15 lines or fewer.
- [ ] The index constants (`FAST`, `NORMAL`, …) are `private static final`.
- [ ] The usage line is exactly `Usage: DeliveryStats <minutes>...` (with the three dots), and there's no extra blank line at the end of the normal output.

Paste the output of all four commands from Checkpoint 1 Part C:

```bash
java -cp out com.chandan.delivery.DeliveryStats 35 50 20 -5 95 44 12
java -cp out com.chandan.delivery.DeliveryStats
java -cp out com.chandan.delivery.DeliveryStats -1 -2
java -cp out com.chandan.delivery.DeliveryStats 29 30 44 45
```

