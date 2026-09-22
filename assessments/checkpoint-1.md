# Checkpoint 1: Java Fundamentals (Lessons 1–7)

Issued: 2026-09-22 · Gate for Phase 2 (Object-Oriented Java)

## Rules
- **Part A:** write every prediction down **before** running anything. Then check your answers in JShell or a file, and report both your prediction and the actual result.
- **Parts B and C:** compile and run everything. Paste the real output.
- **Part D:** your own words, 2–3 sentences each. Don't copy from notes.md.
- It's fine to say "I don't know" for any item.

## Scoring (42 points)
| Part | Points |
|---|---|
| A: Predict | 8 (1 each) |
| B: Debug | 14 (7 bugs × [category 1 + fix 1]) |
| C: Build | 10 (checklist items) |
| D: Explain | 10 (5 × 2) |

**To pass:** at least 32/42 overall, **and** at least 6/10 on Part D, **and** every Part C checklist item ticked.
If you don't pass, we do a short, targeted review of the weak areas and then retake just that part. No restart.

---

## Part A: Predict (8 points)

```java
import java.util.Arrays;

public class PartA {
    static void applyDiscount(int[] prices, int percent) {
        for (int i = 0; i < prices.length; i++) prices[i] = prices[i] * (100 - percent) / 100;
        percent = 0;
    }

    public static void main(String[] args) {
        int items = 7, boxes = 2;
        double perBox = items / boxes;
        System.out.println("1: " + perBox);

        long ms = 24 * 60 * 60 * 1000 * 45;
        System.out.println("2: " + ms);           // exact number not required: say what KIND of value and why

        String prefix = "ORD-";
        String a = prefix + 0;
        String b = "ORD-0";
        System.out.println("3: " + (a == b) + " " + a.equals(b));

        int[] prices = {1000, 250};
        int percent = 10;
        applyDiscount(prices, percent);
        System.out.println("4: " + Arrays.toString(prices) + " " + percent);

        char grade = 'B';
        System.out.println("5: " + (grade + 1) + " " + (char) (grade + 1) + " " + ("" + grade + 1));

        int code = 2;
        String label = "";
        switch (code) {
            case 1: label += "one ";
            case 2: label += "two ";
            case 3: label += "three ";
                break;
            default: label += "other";
        }
        System.out.println("6: [" + label + "]");

        System.out.println("7: " + (int) -7.9 + " " + (-7 / 2) + " " + (-7 % 2));

        int[] x = {5, 3, 9};
        int[] y = x;
        y = new int[] {1};
        y[0] = 7;
        System.out.println("8: " + Arrays.toString(x));
    }
}
```

---

## Part B: Debug `InvoiceCalculator` (14 points)

Save it as `exercies/checkpoint1/src/com/chandan/billing/InvoiceCalculator.java`.

```java
package com.chandan.billing;

public class InvoiceCalculator {
    private static final int GST_PERCENT = 18;

    private static void normalizeTier(String tier) {
        tier = tier.strip().toLowerCase();
    }

    private static int discountPercent(String tier) {
        return switch (tier) {
            case "gold" -> 15;
            case "silver" -> 5;
        };
    }

    private static long lineTotalInPaise(int quantity, int unitPriceInPaise) {
        return quantity * unitPriceInPaise;
    }

    private static void main(String[] args) {
        String tier = "  GOLD ";
        normalizeTier(tier);

        int[] quantities = {2, 5000, 1};
        int[] unitPricesInPaise = {49_900, 999_900, 15_000};

        long subtotalInPaise = 0;
        for (int i = 0; i <= quantities.length; i++) {
            subtotalInPaise += lineTotalInPaise(quantities[i], unitPricesInPaise[i]);
        }

        int discount = discountPercent(tier);
        double discountRate = discount / 100;
        long discountInPaise = (long) (subtotalInPaise * discountRate);
        long gstInPaise = (subtotalInPaise - discountInPaise) * GST_PERCENT / 100;
        long totalInPaise = subtotalInPaise - discountInPaise + gstInPaise;

        System.out.println("Quantities: " + quantities);
        System.out.println("Tier: " + tier + ", discount: " + discount + "%");
        System.out.println("Subtotal: " + subtotalInPaise + " paise");
        System.out.println("Total: " + totalInPaise + " paise");
    }
}
```

**There are 7 bugs: 1 compile time, 1 launch time, 1 run time and 4 silently wrong.** Each stage hides the next one.

Expected output after every fix:
```
Quantities: [2, 5000, 1]
Tier: gold, discount: 15%
Subtotal: 4999614800 paise
Total: 5014613644 paise
```

Send:
1. A table with one row per bug: `# | line | category | why | fix`
2. Your fixed code and its output
3. How many compile → run → fix rounds it took, and what that tells you

---

## Part C: Build `DeliveryStats` (10 points)

Program arguments are delivery times in whole minutes. Assume every argument is a whole number, but it may be **negative**, and negative values are invalid.

```bash
java -cp out com.chandan.delivery.DeliveryStats 35 50 20 -5 95 44 12
```
must print **exactly**:
```
Deliveries: 6 valid, 1 invalid
Fast: 2, Normal: 2, Late: 2
Average: 42.67 min, fastest: 12 min, slowest: 95 min
Late: 33.33%
```
Speed categories: **fast** is under 30 minutes, **normal** is 30–44, **late** is 45 or more.

Other required behavior:
```bash
java -cp out com.chandan.delivery.DeliveryStats
```
→ `Usage: DeliveryStats <minutes>...`
```bash
java -cp out com.chandan.delivery.DeliveryStats -1 -2
```
→ `Deliveries: 0 valid, 2 invalid` then `No valid deliveries` (no crash, no garbage numbers)
```bash
java -cp out com.chandan.delivery.DeliveryStats 29 30 44 45
```
→ you work out the expected output (check the boundaries!)

### Requirements checklist: tick every item before submitting
- [ ] 1. Package `com.chandan.delivery`, in the matching folder, compiled and run with its fully qualified name
- [ ] 2. The valid minutes are collected into an `int[]` by a separate `private static` method
- [ ] 3. The speed category is decided by an **arrow `switch`** (hint: `minutes / 15` is useful here)
- [ ] 4. Category counts use an `int[]` with **named constants** for the indexes
- [ ] 5. The average uses **correct types** (42.67, not 42.00)
- [ ] 6. Fastest and slowest are correct, **including when every value is large**
- [ ] 7. A guard clause for zero arguments, and a guard for "no valid deliveries" (no division by zero, no `Integer.MIN_VALUE` in the output)
- [ ] 8. All output lines built with `formatted(...)` / `String.format`
- [ ] 9. All helper methods are `private`, and `main` is 15 lines or fewer
- [ ] 10. The output for all four commands above is pasted, and matches exactly

---

## Part D: Explain it (10 points, 2–3 sentences each, your own words)

1. A service computes `double conversionRate = signups / visits * 100;` (both `int`). The dashboard shows **0%** all week and **the logs show no errors**. Explain what's happening, why the logs are clean, and the fix.
2. You pass an `int[]` to a method that runs `values[0] = 1;` and then `values = new int[5];`. What does the caller's array look like afterwards? Explain using the words **reference** and **copy**.
3. Why is `role == "admin"` dangerous in production code even when the tests pass? What should you write instead, and why that exact form?
4. Name the **four error categories** we've used, and give one real example of each **from your own exercises**.
5. `long total = quantity * unitPrice;`, where both are `int`. Why can this still overflow even though `total` is a `long`? Give two ways to fix it.
