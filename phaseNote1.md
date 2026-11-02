# Phase 1 — Fundamentals — Notes

Revision notes for this phase. Every concept: **theory first**, **then a code snippet**, then an **example / gotcha** with real output (`// →`) or the exact error message. All outputs verified on JDK 25. See `notes.md` for the index of all phases.

---

## Anatomy of a Java program

- Everything lives inside a type (class/interface/enum/record). No free-floating functions or variables.
- Blocks use `{}`; statements end with `;` (no ASI like JS — a missing `;` is a compile error).
- One file may contain several classes, but **at most one** `public` **top-level class**, named like the file.

```java
package com.chandan.ledger;          // 1. package (optional, but always used in real code) — must be first statement

import java.time.LocalDate;          // 2. imports

public class App {                   // 3. type declaration; public class name == file name
    public static void main(String[] args) {   // 4. entry point
        System.out.println("Today: " + LocalDate.now());   // 5. statements end with ;
    }
}
```
```java
int x = 5                            // ❌ error: ';' expected
```

---

## `main` method, piece by piece

- `public` — the launcher can call it from outside. `static` — runs without creating an object. `void` — returns nothing (exit code via `System.exit(n)`). `String[] args` — CLI arguments.
- Canonical form: `public static void main(String[] args)` — use this in real code.
- The Java 25 launcher also accepts a non-`private`, non-static, no-arg `main` (e.g. `void main()`), mainly for scripts/learning.
- A `private main` **compiles fine** but fails at **launch** time.

```java
public class Greet {
    public static void main(String[] args) {
        System.out.println("Hello, " + args[0]);
    }
}
// java Greet Asha   → Hello, Asha
// java Greet        → ArrayIndexOutOfBoundsException (no args)
```
```java
private static void main(String[] args) { ... }
// javac: OK
// java:  Error: Main method not found in class PrivMain, please define the main method as:
//           public static void main(String[] args)
```

---

## Packages

- Namespace for classes, like a folder-based module path. Convention: reversed domain, lowercase → `com.acme.orders`.
- Directory must mirror the package: `com/acme/orders/OrderSummary.java`.
- **Fully qualified name (FQN)**: `com.acme.orders.OrderSummary` — this is the class's real identity.
- Why: avoids name collisions (your `User` vs a library's `User`), groups related code, enables package-private visibility.

```java
// src/com/acme/orders/OrderSummary.java
package com.acme.orders;

public class OrderSummary {
    public static void main(String[] args) {
        System.out.println("ok");
    }
}
```
```bash
javac -d out src/com/acme/orders/OrderSummary.java   # → out/com/acme/orders/OrderSummary.class
java -cp out com.acme.orders.OrderSummary            # → ok   (classpath root + FQN)
```

- `java -cp out OrderSummary` → no `out/OrderSummary.class` exists → `ClassNotFoundException`.
- The FQN is **stored inside the `.class` file**. Running `java OrderSummary` from inside `out/com/acme/orders/` finds `./OrderSummary.class`, but its real name is `com/acme/orders/OrderSummary` → `NoClassDefFoundError (wrong name)`.
  - `ClassNotFoundException` = "no file found for that name". `NoClassDefFoundError (wrong name)` = "found a file, but it's a different class".

```
$ java -cp out OrderSummary
Error: Could not find or load main class OrderSummary
Caused by: java.lang.ClassNotFoundException: OrderSummary

$ cd out/com/acme/orders && java OrderSummary
Error: Could not find or load main class OrderSummary
Caused by: java.lang.NoClassDefFoundError: OrderSummary (wrong name: com/acme/orders/OrderSummary)
```

---

## Imports

- `import` is **only a naming shortcut** — nothing is loaded or executed (unlike JS `import`, which runs the module).
- `java.lang.*` (`String`, `System`, `Math`, `Integer`…) is imported automatically.
- Without an import you can use the FQN.
- Prefer single-type imports over wildcards (`java.util.*`) — clearer, avoids ambiguity.

```java
import java.time.LocalDate;                       // shortcut
LocalDate today = LocalDate.now();

java.time.LocalDate today2 = java.time.LocalDate.now();   // same thing, no import

String s = "x";  Math.max(1, 2);                  // java.lang — no import needed
```

---

## Comments

- `// line`, `/* block */`, `/** Javadoc */` (documentation for public APIs, rendered by IDEs / the `javadoc` tool).

```java
// single line

/* block
   comment */

/**
 * Returns the paid share as a percentage.
 * @param part number of paid orders
 * @param total all orders, must be > 0
 */
static double percentage(int part, int total) { ... }
```

---

## Naming conventions (followed by the whole ecosystem — Spring relies on some)

| Thing | Style | Example |
|---|---|---|
| Package | lowercase, dot-separated | `com.acme.orders` |
| Class / interface / enum / record | PascalCase noun | `OrderService`, `PaymentStatus` |
| Method | camelCase verb | `calculateTotal()`, `isEligibleForRefund()` |
| Variable / parameter | camelCase | `orderId` |
| Constant (`static final`) | UPPER_SNAKE_CASE | `MAX_RETRIES` |

- Booleans: `isX`/`hasX`/`canX`. Acronyms: `HttpClient`, `userId` (not `HTTPClient`, `userID`) — common modern style.

```java
package com.chandan.billing;

public class InvoiceService {
    private static final int MAX_RETRIES = 3;
    private final HttpClient httpClient;

    public boolean isOverdue(long invoiceId) { ... }
    public long calculateTotal(String userId) { ... }
}
```

---

## How javac reports errors

- Errors come in **stages**: syntax (parse) errors first → fix → then symbol/type errors appear → fix → then launch/run-time errors.
- Fixing "all" errors may reveal new ones. Always fix the **first** error first; later errors can be side effects.
- Three layers so far: **compile-time** (javac), **launch-time** (JVM can't find class or main), **run-time** (exceptions while executing).

```
round 1:  error: ';' expected                                   ← syntax, fix it
round 2:  error: cannot find symbol  symbol: variable totl      ← now the symbol errors show up
round 3:  compiles → Exception in thread "main" ...              ← now run-time
```

---

## Variables & primitive types

- Two kinds of types: **primitives** (the value itself, 8 fixed types, lowercase) and **references** (point to objects: `String`, `LocalDate`, arrays, your classes).

| Type | Size | Range / notes | Use for |
|---|---|---|---|
| `byte` | 8 bit | −128..127 | raw binary data |
| `short` | 16 bit | ±32k | rarely used |
| `int` | 32 bit | ±2.1 billion (`Integer.MAX_VALUE` = 2,147,483,647) | default whole number: counts, indexes |
| `long` | 64 bit | ±9.2 × 10^18 | IDs, timestamps (ms), byte sizes, money in minor units |
| `float` | 32 bit | ~7 digits | almost never (graphics/ML) |
| `double` | 64 bit | ~15–16 digits | measurements, ratios, averages — **never money** |
| `char` | 16 bit | one UTF-16 code unit, numeric (`'A' + 1` = 66) | single characters |
| `boolean` | — | `true`/`false` only, **not a number** | flags |

```java
int orderCount = 42;
long createdAtMs = 1_727_000_000_000L;
long amountPaise = 99_950L;
double successRate = 0.987;
char grade = 'A';
boolean isPaid = true;
final int maxRetries = 3;          // constant local
String name = "Asha";              // reference type (a class)
```

- **Definite assignment**: a local variable must be assigned before it's read, or it's a compile error. (JShell is lenient here — test with javac.)
- Literals: `42` (int), `42L` (long), `3.14` (double), `3.14f` (float), `'A'` (char), `1_000_000` (underscores for readability), `0xFF`, `0b1010`.

```java
int count;
System.out.println(count);   // ❌ error: variable count might not have been initialized

int x = "a";                 // ❌ error: incompatible types: String cannot be converted to int
```

- `var`: **compile-time** inference for local variables. The type is fixed forever after. Needs an initializer. Not dynamic typing — not JS `let`.

```java
var n = 10;          // n is an int, permanently
n = 2.5;             // ❌ error: incompatible types: possible lossy conversion from double to int
var list = new ArrayList<String>();   // fine — type is ArrayList<String>
var y;               // ❌ no initializer → can't infer
```

---

## Arithmetic rules

- `int op int` → `int`. **Integer division truncates toward zero.** `%` takes the sign of the left operand.
- Mixed types → **widened** to the larger type.
- Order matters: `10 / 3 * 3` = 9.

```java
7 / 2          // → 3
-7 / 2         // → -3     (toward zero, not floor)
-7 % 2         // → -1     (sign of the left operand)
7 / 2.0        // → 3.5    (one double → double division)
10 / 3 * 3     // → 9
```

- Classic bug: `completed / total * 100` with ints → 0 for any `completed < total`. Fix: put a double in **before** dividing.

```java
int completed = 3, total = 4;
completed / total * 100         // → 0      ❌ (3/4 = 0 first)
completed * 100.0 / total       // → 75.0   ✅
```

- `byte`/`short`/`char` are promoted to `int` in arithmetic. Compound assignment (`+=`) has a hidden cast.

```java
short s = 1;
s = s + 1;        // ❌ error: incompatible types: possible lossy conversion from int to short
s += 1;           // ✅ compiles (hidden cast)
```

- Integer ÷ 0 → `ArithmeticException` (run time). Double ÷ 0 → `Infinity` (no error!).

```java
10 / 0         // → java.lang.ArithmeticException: / by zero
10 / 0.0       // → Infinity
```

---

## Casting

- **Widening** (int → long → double) is automatic and safe.
- **Narrowing** needs an explicit cast and can lose data. It **truncates**, it doesn't round; out-of-range values **wrap**.

```java
long big = 42;                  // widening, automatic
double d = big;                 // widening, automatic

(int) 3.99                      // → 3            (truncates)
(int) -7.9                      // → -7           (toward zero)
(int) 3_000_000_000L            // → -1294967296  (wraps — garbage)
(byte) 200                      // → -56
(char) ('A' + 2)                // → C
```

- javac error "possible lossy conversion from X to Y" = you're narrowing without a cast. Don't add a cast just to silence it — first ask if the value can actually fit.

```java
int x = 3.5;   // ❌ error: incompatible types: possible lossy conversion from double to int
```

---

## Overflow — silent!

- `Integer.MAX_VALUE + 1` wraps to the minimum. No error, no warning.
- Classic bug: the multiplication runs in `int` **before** the result is assigned to `long`. Fix: make the first operand `long`.
- Declaring the result `long` is not enough — an **operand** must be long.
- Overflow **never** throws on its own — it's always "silently wrong", never "run time". And the wrapped value isn't always negative.

```java
Integer.MAX_VALUE + 1                    // → -2147483648

long bad  = 1000 * 60 * 60 * 24 * 30;    // → -1702967296   ❌ int math, then widened
long good = 1000L * 60 * 60 * 24 * 30;   // → 2592000000    ✅
1000 * 60 * 60 * 24 * 60                 // → 889032704     plausible-looking, WRONG

int requestsPerDay = 100_000_000;
long m  = requestsPerDay * 30;           // → -1294967296   ❌ still overflows
long m2 = requestsPerDay * 30L;          // → 3000000000    ✅
```

- When overflow must never happen: `Math.addExact`, `Math.multiplyExact` → throw instead of wrapping.

```java
Math.multiplyExact(1_000_000, 1_000_000)   // → java.lang.ArithmeticException: integer overflow
```

---

## Division by zero — don't "fix" it by switching to double

- `x / (double) 0` → `Infinity`: you've turned a loud crash into a silent wrong value.
- Real fix: handle the zero case explicitly and decide what the business wants (0? "N/A"? skip?).

```java
double rate = processed / (double) pendingJobs;   // ❌ pendingJobs == 0 → Infinity, shown on a dashboard

if (pendingJobs == 0) {                            // ✅ decide explicitly
    return "N/A";
}
return "%.2f".formatted((double) processed / pendingJobs);
```

---

## Floating point & money

- `0.1 + 0.2` isn't 0.3 — binary floating point can't represent 0.1 exactly.

```java
0.1 + 0.2                       // → 0.30000000000000004
new BigDecimal(0.1)             // → 0.1000000000000000055511151231257827021181583404541015625
new BigDecimal("0.1")           // → 0.1
BigDecimal.valueOf(0.1)         // → 0.1
```

- Money: **never** `double`. Two valid approaches:
  - **`long` minor units** (paise/cents): simple, fast, exact for +, −, × by whole numbers. Many payment APIs (e.g. Stripe) use this. Division needs an explicit rounding decision.
  - **`BigDecimal`**: exact decimals + explicit `RoundingMode`; use when dividing, applying rates/tax/discount %, or converting currencies.
  - Common combo: store/transport as `long` paise → convert with `BigDecimal.valueOf(paise, 2)` (= rupees, exact) for division or display.
  - Create `BigDecimal` from a **String** or `valueOf`, never `new BigDecimal(0.1)`.
- Counts (subscribers, items) are `int`/`long`, not `BigDecimal` or `double`.
- **Source of truth = smallest unit.** Compute in paise, derive rupees for display. Never go rupees → `longValue()` → paise (truncates fractions silently).

```java
long pricePaise = 99_950;                          // ₹999.50
long monthly = pricePaise * 2_500_000L;            // exact
BigDecimal.valueOf(249750, 2)                      // → 2497.50   (paise → rupees, exact)

new BigDecimal("999.50").longValue()               // → 999   ❌ the .50 silently gone
```

- `(double) a / b` casts `a` **before** dividing → double division ✅. `(double) (a / b)` casts **after** int division → fraction already lost ❌.

```java
(double) 7 / 2        // → 3.5   ✅
(double) (7 / 2)      // → 3.0   ❌
```

---

## Avoiding unit bugs

- Replace magic numbers with named constants → one definition to review, and the name says the intent.
- Tests: an **example test** with a hand-computed expected value (2.5M × ₹999 = 249,750,000,000 paise) catches wrong factors. An **invariant test** (paise / rupees == 100) checks consistency but passes if both are wrong the same way. Use both. (JUnit: Phase 10.)
- In review: estimate the order of magnitude and compare with the output.

```java
long paise = rupees * 1000;                        // ❌ magic number — and wrong (should be 100)

private static final int PAISE_PER_RUPEE = 100;
long paise = rupees * PAISE_PER_RUPEE;             // ✅ the name makes the mistake visible
```

---

## Format strings (`formatted` / `String.format`)

- `"template".formatted(a, b, c)` ≡ `String.format("template", a, b, c)` (`formatted` is Java 15+).
- Each `%…` placeholder is filled **left to right** by the arguments.

| Spec | Meaning | Example |
|---|---|---|
| `%d` | integer (`int`, `long`) | `"%d".formatted(42)` → `42` |
| `%.2f` | decimal, 2 places (rounds) | `42.857142` → `42.86` |
| `%,d` / `%,.2f` | with grouping separators | `2,497,500,000` |
| `%s` | anything, via its text form | `"hi"`, objects |
| `%b` | boolean | `true` |
| `%5d` / `%-5d` / `%05d` | width 5, right / left aligned / zero-padded | `[   42]` `[42   ]` `00042` |
| `%%` | a literal `%` sign | `50%` |
| `%n` | platform line break | |

```java
"%d".formatted(42)                       // → 42
"%.2f".formatted(42.857142)              // → 42.86
"%,d".formatted(2497500000L)             // → 2,497,500,000
"[%5d]".formatted(42)                    // → [   42]
"[%-5d]".formatted(42)                   // → [42   ]
"%05d".formatted(42)                     // → 00042
"%d%%".formatted(50)                     // → 50%
"%s paid %d%n".formatted("Asha", 3)      // → Asha paid 3  + newline
"INV-%04d".formatted(7)                  // → INV-0007
```

- Wrong type or too few arguments fail at **run time** — javac doesn't check format strings (IntelliJ does warn).

```java
"%d".formatted(3.5)       // → java.util.IllegalFormatConversionException: d != java.lang.Double
"%d %d".formatted(1)      // → java.util.MissingFormatArgumentException: Format specifier '%d'
```

---

## Formatting numbers for output

- `double` prints large values in scientific notation.
- `%.2f` fixes that; `%,.2f` adds grouping.
- Exact money display from paise: `BigDecimal.valueOf(paise, 2)`.

```java
System.out.println(83250000.0);          // → 8.325E7
"%.2f".formatted(83250000.0)             // → 83250000.00
"%,.2f".formatted(83250000.0)            // → 83,250,000.00
BigDecimal.valueOf(249750000000L, 2)     // → 2497500000.00
```

---

## Error categories (so far)

1. **Compile time** — javac rejects it (types, syntax, lossy conversion, uninitialized local).
2. **Launch time** — JVM can't find the class or a valid `main`.
3. **Run time** — exception while executing.
4. **Silently wrong** — runs fine, wrong answer. The most dangerous kind.

Test: *"Did javac refuse?" → compile. "Did it start?" → launch. "Did it throw?" → run time. "Did it finish with a wrong answer?" → silently wrong.*

```java
int x = 3.5;                          // 1. compile   — possible lossy conversion
// java -cp out OrderSummary          // 2. launch    — ClassNotFoundException
int[] a = new int[3]; a[3] = 1;       // 3. run time  — ArrayIndexOutOfBoundsException
int pct = 3 / 4 * 100;                // 4. silent    — 0, no error anywhere
long ms = 1000 * 60 * 60 * 24 * 30;   // 4. silent    — negative, no error anywhere
```

---

## Primitives vs references (memory model)

- A primitive variable **holds the value**. A reference variable **holds an address** of an object on the **heap**.
- Assignment copies what the variable holds: for primitives the value, for references the **address** (two variables → same object).
- References can be `null` (points to nothing). Calling a method on `null` → `NullPointerException` (run time). Primitives can never be null.

```java
int a = 5;
int b = a;          // copies the VALUE
b = 99;             // a → 5

StringBuilder x = new StringBuilder("hi");
StringBuilder y = x;    // copies the ADDRESS — one object, two names
y.append("!");          // x → hi!

String role = null;
role.equals("ADMIN");   // → NullPointerException: Cannot invoke "String.equals(Object)" because "role" is null
int n = null;           // ❌ compile error — primitives can't be null
```

---

## Strings

- `String` is a **class** (reference type), not a primitive. `"text"` = String, `'t'` = char.
- **Immutable**: every "modifying" method returns a **new** String. Calling it without assigning does nothing.
- Why immutable: safe to share between threads, safe as map keys, can be cached/pooled, can't be changed after a security check.

```java
String status = "paid";
status.toUpperCase();              // ❌ result thrown away
System.out.println(status);        // → paid
status = status.toUpperCase();     // ✅
System.out.println(status);        // → PAID
```

- Common methods:

```java
String s = "Hello World";
s.length()                // → 11
s.charAt(4)               // → o
s.substring(0, 5)         // → Hello   (end is EXCLUSIVE)
s.substring(6)            // → World
s.indexOf("o")            // → 4       (-1 if not found)
s.contains("World")       // → true
s.startsWith("Hell")      // → true
"  hi  ".strip()          // → hi      (Unicode-aware trim)
"".isEmpty()              // → true
"   ".isEmpty()           // → false
"   ".isBlank()           // → true
"ab".repeat(3)            // → ababab
String.join("-", "a", "b", "c")   // → a-b-c
"Paid".replace("P", "p")  // → paid
```

- `substring(i)` starts **at** index i — to skip a delimiter found by `indexOf`, use `substring(i + 1)`.

```java
String email = "chandan@mail.com";
int at = email.indexOf('@');      // → 7
email.substring(at)               // → @mail.com   ❌ includes the @
email.substring(at + 1)           // → mail.com    ✅
email.substring(0, at)            // → chandan
```

- `split` drops **trailing** empty strings (but keeps empty ones in the middle).

```java
"a,b,,c,,".split(",")             // → [a, b, , c]   length 4
```

- Parsing: bad input → `NumberFormatException` (run time). Other direction: `String.valueOf`.

```java
Integer.parseInt("42")            // → 42
Integer.parseInt("42abc")         // → NumberFormatException: For input string: "42abc"
String.valueOf(42)                // → "42"
```

---

## `==` vs `.equals()` ⚠️ #1 JS→Java trap

- `==` on references: **same object?** (address comparison). `.equals()`: **same content?**
- String literals are pooled, so `"hello" == "hello"` is `true` — **by accident**. Strings built at run time (input, concatenation with variables, substring, JSON, DB) are new objects → `==` is `false` even with identical text.
- Rule: **always compare strings (and any objects) with `.equals()`**. `==` only for primitives (and `null` checks / enums).

```java
"hello" == "hello"              // → true   (pooled literals — luck)

String part = "hel";
String built = part + "lo";     // built at run time → new object
built == "hello"                // → false  ❌
built.equals("hello")           // → true   ✅
```

- Null-safe: put the constant first, or use `Objects.equals(a, b)`. `role.equals("ADMIN")` throws if `role` is null.
- Case-insensitive: `equalsIgnoreCase`. Ordering: `compareTo` (negative / 0 / positive).

```java
String role = null;
"ADMIN".equals(role)            // → false   ✅ no NPE
Objects.equals(role, "ADMIN")   // → false   ✅
role.equals("ADMIN")            // → NullPointerException   ❌

"Paid".equalsIgnoreCase("PAID") // → true
"apple".compareTo("banana")     // → -1   (apple first)
"b".compareTo("a")              // → 1
"a".compareTo("a")              // → 0
```

---

## String vs StringBuilder — pick by intent

- `String` = immutable value → safe to pass around, read-only work (parsing, slicing, comparing).
- `StringBuilder` = mutable buffer → only when **building** text piece by piece (loops, big reports).
- Mutating a builder in the middle of reading from it makes later reads depend on call order — a common source of bugs.

```java
String masked = email.substring(0, 1) + "***" + email.substring(at);   // ✅ read-only work → String

StringBuilder sb = new StringBuilder(email);
sb.replace(1, at, "***");              // ❌ mutating while you still read indexes from it
```

---

## String building

- `+` is fine for a few pieces (the compiler optimizes it). In **loops**, each `+=` creates a new String → use `StringBuilder` (`append`, then `toString()`).

```java
StringBuilder sb = new StringBuilder();
for (int i = 1; i <= 3; i++) {
    sb.append("row").append(i).append(";");
}
sb.toString()                   // → row1;row2;row3;
```

- **The `+` rule:** if **either operand is a String**, `+` concatenates and converts the other operand to text — any type. If **neither** is a String, `+` is arithmetic — which is why `char + char` is an `int`. Force concatenation by starting with `""`.
- Evaluation is **left to right**.

```java
true + "!" + 5 + 'c' + 1.5 + null    // → true!5c1.5null
'J' + 'a'                            // → 171    (char + char = int arithmetic)
"" + 'J' + 'a'                       // → Ja
'A' + 1                              // → 66
"1" + 2 + 3                          // → 123
1 + 2 + "3"                          // → 33     (1+2 first, then "3")
```

- Text blocks (`"""` … `"""`) for multi-line SQL/JSON; indentation is stripped based on the closing delimiter.

```java
String sql = """
        SELECT id, amount
        FROM orders
        WHERE status = 'PAID'
        """;
```

---

# Operators & control flow (Java vs JS differences)

## Operators

- **No `===`.** `==` compares primitives by value, objects by reference → `.equals()` for objects.
- **No truthiness:** `if`, `while`, `&&`, `||`, `?:` need a real `boolean`.

```java
if (list) { }            // ❌ error: incompatible types: List<String> cannot be converted to boolean
if (str) { }             // ❌ same idea
if (!list.isEmpty()) { } // ✅
if (str != null && !str.isBlank()) { }   // ✅
```

- **No `||` default idiom and no `??`.**

```java
String name = input != null ? input : "guest";             // ternary
String name2 = Objects.requireNonNullElse(input, "guest"); // → guest  when input is null
```

- `&&` / `||` short-circuit (same as JS) — the standard null guard.
- `&` / `|` on booleans = non-short-circuiting (both sides evaluated); on ints = bitwise. `^` xor, `<<`, `>>`, `>>>` (unsigned right shift).

```java
if (s != null && s.length() > 2) { }    // s.length() never runs when s is null

5 & 3      // → 1
5 | 3      // → 7
5 ^ 3      // → 6
1 << 4     // → 16
-16 >> 2   // → -4    (keeps the sign)
-16 >>> 28 // → 15    (fills with zeros)
```

- Compound assignment has a **hidden cast** — it can wrap silently.

```java
byte b = 10;
b = b + 5;     // ❌ error: incompatible types: possible lossy conversion from int to byte
b += 5;        // ✅ → 15
b = 10;
b += 300;      // → 54   (310 doesn't fit in a byte, wraps silently)
```

- `instanceof` with a **pattern variable** — no cast needed.

```java
Object o = "hello";
if (o instanceof String s && s.length() > 2) {
    System.out.println(s.toUpperCase());   // → HELLO
}
```

- `=` in a condition compiles when the variable is boolean: it **assigns** and is always true. A classic silent bug.

```java
boolean isPremium = false;
if (isPremium = true) {          // ❌ assignment, not comparison
    System.out.println("premium!");   // → premium!   (always runs, and isPremium is now true)
}
if (isPremium) { }               // ✅ just use the boolean
```

---

## if / else

- Condition must be a boolean. Otherwise identical to JS.
- Prefer **guard clauses** (validate + `return` early) over deep nesting.

```java
// ❌ nested
if (args.length > 0) {
    if (!args[0].isBlank()) {
        process(args[0]);
    }
}

// ✅ guard clauses
if (args.length == 0) {
    System.out.println("Usage: App <name>");
    return;
}
if (args[0].isBlank()) {
    return;
}
process(args[0]);
```

---

## switch — much stronger than JS

- Classic `switch` (`case X:` + `break`) **falls through** to the next case when `break` is missing — a common silent bug. Legacy style; avoid in new code.
- Arrow `switch` (Java 14+): no fall-through, and it can be an **expression** that returns a value. Multiple labels per case. `case null` (Java 21+). A block uses `yield` to produce its value.

```java
// classic — falls through
int code = 2;
String label = "";
switch (code) {
    case 1: label += "one ";
    case 2: label += "two ";       // starts here...
    case 3: label += "three ";     // ...and falls into here
        break;
    default: label += "other";
}
// label → "two three "
```
```java
// arrow switch EXPRESSION
String tier = "platinum";
int discount = switch (tier) {
    case "gold", "platinum" -> 20;       // multiple labels
    case "silver" -> 10;
    case null -> 0;                      // switch can handle null (Java 21+)
    default -> {
        int base = tier.length();
        yield Math.min(base, 5);         // block → yield
    }
};
// discount → 20
```

- Works on `int`, `String`, `enum`, and (Java 21+) any type via **pattern matching**, with `when` guards.

```java
static String describe(Object value) {
    return switch (value) {
        case Integer i when i > 100 -> "big int " + i;
        case Integer i -> "int " + i;
        case String s -> "string of length " + s.length();
        case null -> "nothing";
        default -> "something else";
    };
}

describe(150)    // → big int 150
describe(5)      // → int 5
describe("hey")  // → string of length 3
describe(null)   // → nothing
describe(2.5)    // → something else
```

- A switch **expression** must be exhaustive (hence `default`, unless all enum/sealed cases are covered).
- `switch` on String compares with `.equals()` internally — safe, unlike `==`.

---

## Loops

- `for (int i = 0; i < n; i++)` — same as JS.
- **Enhanced for** (= JS `for...of`): no index available; use a classic for when you need one.
- `while`, `do { } while (cond);` (do-while runs at least once).
- `break` / `continue`, plus **labels** for nested loops.
- There is no `for...in`; iterating a Map's keys comes in Phase 3.

```java
for (String arg : args) {                  // enhanced for
    System.out.println(arg);
}

for (int i = 0; i < args.length; i++) {    // when you need the index
    System.out.println(i + ": " + args[i]);
}

int n = 0;
do { n++; } while (n < 0);                 // condition false, but the body ran once → n = 1

outer:
for (int i = 0; i < 3; i++) {
    for (int j = 0; j < 3; j++) {
        if (j == 1) continue outer;        // next i
        if (i == 2) break outer;           // leave both loops
        System.out.println("i=" + i + " j=" + j);
    }
}
// → i=0 j=0
// → i=1 j=0
```

---

# Methods

## Anatomy

- Every method lives in a class. `static` = call without an object (`ClassName.method()` or just `method()` inside the class). Instance methods: Phase 2.
- `void` = returns nothing. A non-void method must `return` on **every** path → otherwise compile error.
- Access: `private` for helpers used only inside the class, `public` for things other classes call.
- Names: camelCase verbs (`calculateTotal`, `isValid`, `formatRupees`). Small, one job each.

```java
private static double percentage(int part, int total) { ... }
// modifiers     return-type  name      parameters (typed)    body
```
```java
static int sign(int x) {
    if (x > 0) return 1;
}                           // ❌ error: missing return statement (what if x <= 0?)

static int sign(int x) {
    if (x > 0) return 1;
    if (x < 0) return -1;
    return 0;               // ✅ every path returns
}
```

---

## No default/optional parameters → overloading

- Same name, **different parameter lists** (count or types). The return type alone doesn't count.
- The compiler picks the overload at **compile time** from the argument types. A `char` widens to `int`; a `float` widens to `double`.

```java
static String describe(int x)    { return "int"; }
static String describe(long x)   { return "long"; }
static String describe(double x) { return "double"; }

describe(5)      // → int
describe(5L)     // → long
describe('A')    // → int     (char widens to int)
describe(5f)     // → double  (float widens to double)
```

- **Varargs**: `Type... name` — call with zero or more arguments; inside, it's an array. Must be the last parameter.

```java
static int sum(int... nums) {
    int total = 0;
    for (int n : nums) total += n;
    return total;
}

sum()          // → 0
sum(1, 2, 3)   // → 6
```

---

## Java is ALWAYS pass-by-value

- The method receives a **copy of the variable's value**.
  - Primitive → a copy of the number. Changing it inside doesn't affect the caller.
  - Reference → a copy of the **address**. Both point to the same object:
    - **Mutating the object** (`sb.append("!")`) → caller sees it.
    - **Reassigning the parameter** (`sb = new StringBuilder()`, `status = status.strip()`) → only the local copy changes; caller unaffected.
- Consequence: a method can't "change" a caller's String (immutable + reassignment is local) → **return the new value** instead.
- Same model as JS. Java is *not* pass-by-reference.

```java
static void bump(int n)                  { n = n + 1; }
static void shout(StringBuilder sb)      { sb.append("!"); }                 // mutates the shared object
static void replace(StringBuilder sb)    { sb = new StringBuilder("new"); }  // reassigns the local copy
static void normalize(String s)          { s = s.strip(); }                  // reassigns the local copy

int x = 10;                          bump(x);       // x  → 10
StringBuilder sb = new StringBuilder("hi");
shout(sb);                                          // sb → hi!
replace(sb);                                        // sb → hi!   (unchanged)
String status = "  paid ";           normalize(status);   // status → "  paid "  (unchanged)
```
```java
static String normalize(String s) { return s.strip(); }   // ✅ return the new value
status = normalize(status);                               // → "paid"
```

---

## Method design rules of thumb

- `get`/`set`/`is` prefixes are reserved by convention for **accessors of an object's fields** (JavaBeans). Spring/Jackson rely on this. A calculation is `percentage(...)` / `calculateTotal(...)`, not `getPercentage(...)`.
- Don't pass a value **and** something derived from it — callers can pass inconsistent combinations. Pass the inputs; derive inside.
- Return directly — no single-use temp variable needed.
- A method returns **one** value. Need several (e.g. 5 counts)? → array or an object/record (Phase 2/4).

```java
getPaidPercentage(paid, total)                        // ❌ "get" for a calculation
percentage(paid, total)                               // ✅

formatSummary(paid, total, paidPercentage)            // ❌ caller could pass 3, 4, 99.0
formatSummary(paid, total)                            // ✅ derive the percentage inside

static double percentage(int part, int total) {
    return (double) part / total * 100;               // ✅ return directly
}
```

---

## ~~Scope~~

- ~~Locals are block-scoped (like~~ `let`~~). Parameters are locals.~~
- ~~Java forbids redeclaring a local in a nested block:~~ `int x = 1; { int x = 2; }` ~~→ compile error (JS allows this shadowing).~~

---

## ~~Call stack & stack traces~~

- ~~Each call pushes a **frame** (params + locals) onto the thread's stack;~~ `return` ~~pops it.~~
- ~~Stack trace = frames from innermost (top, where it failed) to outermost (bottom, who started it):~~

```
NullPointerException: Cannot invoke "String.strip()" because "status" is null
    at StatusTools.normalize(StatusTools.java:3)   ← failed here
    at StatusTools.main(StatusTools.java:21)       ← called from here
```

- ~~Read top line for *what/where*, then walk down for *who passed the bad value*.~~
- ~~Compile with~~ `javac -g` ~~to get variable names in NPE messages instead of~~ `<parameter1>`~~/~~`<local2>`~~.~~
- ~~Infinite recursion →~~ `StackOverflowError`~~.~~

---

# Arrays

## Basics

- Fixed-size, typed, indexed from 0. **Size can never change** after creation (unlike JS arrays).
- Elements always get **default values**: numbers `0`/`0.0`, `boolean` `false`, `char` `'\u0000'`, references `null`. (Unlike locals, array elements are always initialised.)
- `.length` is a **field**, not a method (String uses `length()`).
- Index outside `0 .. length-1` → `ArrayIndexOutOfBoundsException` (run time). Classic: `i <= arr.length`.

```java
int[] counts = new int[5];                 // → [0, 0, 0, 0, 0]
String[] names = new String[2];            // → [null, null]
boolean[] flags = new boolean[2];          // → [false, false]
String[] statuses = {"paid", "pending"};   // literal (only in a declaration)
int[] ids = new int[] {4, 8, 15};          // literal anywhere (e.g. as an argument)

counts.length                              // → 5   (field, no parentheses)
"text".length()                            // → 4   (method on String)
```
```java
int[] z = new int[3];
for (int i = 0; i <= z.length; i++) {      // ❌ <= runs one step too far
    z[i] = i;
}
// → java.lang.ArrayIndexOutOfBoundsException: Index 3 out of bounds for length 3
```

---

## Arrays are objects (reference types)

- `int[] b = a;` copies the **reference** → both names, one array. Changes via `b` are visible via `a`.
- Passing an array to a method shares it (pass-by-value of the reference) → the method **can** change its elements.
- Real copy: `Arrays.copyOf(a, a.length)`, `Arrays.copyOfRange(a, from, to)`, or `a.clone()`.

```java
int[] a = {1, 2, 3};
int[] c = a;            // same array
c[0] = 99;              // a → [99, 2, 3]

int[] copy = Arrays.copyOf(a, a.length);
copy[1] = -1;           // a → [99, 2, 3], copy → [99, -1, 3]

static void fill(int[] arr) { arr[0] = 99; }
int[] f = {0, 0};
fill(f);                // f → [99, 0]   (method changed the caller's array)
```

- `a == b` and `a.equals(b)` both compare **identity** for arrays! Content: `Arrays.equals(a, b)` (2D: `Arrays.deepEquals`).
- `System.out.println(arr)` prints a type code + hash. Use `Arrays.toString(arr)` / `Arrays.deepToString(grid)`.

```java
int[] p = {1, 2}, q = {1, 2};
p == q                  // → false
p.equals(q)             // → false   ❌ identity, not content
Arrays.equals(p, q)     // → true    ✅

System.out.println(p);  // → [I@7344699f
Arrays.toString(p)      // → [1, 2]
```

---

## `java.util.Arrays` utility

- `toString`, `deepToString`, `equals`, `sort` (in place! mutates the array), `fill`, `copyOf`, `copyOfRange`, `asList` (Phase 3).
- `Arrays.sort` on Strings is by UTF-16 code: **uppercase before lowercase**.

```java
Arrays.copyOf(new int[] {1, 2}, 4)               // → [1, 2, 0, 0]   (pads with defaults)
Arrays.copyOfRange(new int[] {1, 2, 3, 4, 5}, 1, 3)   // → [2, 3]   (end exclusive)

String[] words = {"banana", "apple", "Paid", "Zebra"};
Arrays.sort(words);                              // sorts IN PLACE
// words → [Paid, Zebra, apple, banana]

int[] original = {3, 1, 2};
int[] sorted = original.clone();                 // sort a copy if the original matters
Arrays.sort(sorted);
```

---

## 2D arrays

- An array of arrays: `grid[row][col]`, `grid.length` rows, `grid[r].length` columns.
- Rows can differ in length (**jagged**).

```java
int[][] grid = new int[3][4];
grid.length           // → 3   rows
grid[0].length        // → 4   columns

int[][] jag = {{1}, {2, 3}, {}};
jag[1].length         // → 2
jag[2].length         // → 0
Arrays.deepToString(jag)   // → [[1], [2, 3], []]
```

---

## Common algorithms

- Max/min: start from the **first element** (or `Integer.MIN_VALUE`), never from `0` — all-negative data breaks it.
  - Empty array: a `MIN_VALUE` start silently returns −2,147,483,648 (silently wrong); an `arr[0]` start throws (loud). Either way, guard `arr.length == 0` explicitly.
- Average: `(double) sum / arr.length`, and guard `length == 0`.

```java
int[] negs = {-5, -2, -9};

int wrong = 0;
for (int v : negs) wrong = Math.max(wrong, v);      // → 0    ❌ not even in the data

int right = negs[0];
for (int v : negs) right = Math.max(right, v);      // → -2   ✅

int[] empty = {};
int best = Integer.MIN_VALUE;
for (int v : empty) best = Math.max(best, v);       // → -2147483648   ❌ silently wrong
if (empty.length == 0) { /* decide explicitly */ }  // ✅

int[] scores = {7, 8, 8};
int sum = 23;
sum / scores.length              // → 7                    ❌ int division
(double) sum / scores.length     // → 7.666666666666667    ✅
```

---

## Returning several values

- A method returns one value → an `int[]` can carry several (e.g. counts per status).
- Downside: meaning lives in **indexes** (`counts[2]` = ?). Mitigate with named constants; real fix = enum + Map (Phase 2–3) or a record (Phase 4).

```java
private static final int PAID = 0, PENDING = 1, FAILED = 2;

private static int[] countStatuses(String[] statuses) {
    int[] counts = new int[3];
    for (String s : statuses) {
        switch (s) {
            case "paid" -> counts[PAID]++;
            case "pending" -> counts[PENDING]++;
            default -> counts[FAILED]++;
        }
    }
    return counts;
}

int[] c = countStatuses(new String[] {"paid", "paid", "failed"});
c[PAID]      // → 2   ✅ readable
c[0]         // → 2   ❌ magic index
```

---

## Arrays vs List (preview)

- Arrays: fixed size, primitives allowed, fast, used by varargs / `main` / low-level APIs.
- `List<T>` (Phase 3): grows/shrinks, rich API, objects only — what most application code uses.

```java
int[] fixed = new int[3];               // can never hold a 4th element
List<Integer> grows = new ArrayList<>();
grows.add(1); grows.add(2); grows.add(3); grows.add(4);   // → [1, 2, 3, 4]
```

---

## Mistakes I keep making (check these first)

```java
completed / total * 100              // ❌ int division → 0      → completed * 100.0 / total
long ms = 1000 * 60 * 60 * 24 * 30;  // ❌ int overflow          → 1000L * 60 * ...
x / (double) 0                       // ❌ crash → silent Infinity → guard the zero case
new BigDecimal(999.00)               // ❌ double constructor     → new BigDecimal("999.00")
status.toUpperCase();                // ❌ result thrown away     → status = status.toUpperCase();
role == "admin"                      // ❌ identity               → "admin".equals(role)
if (args[0] ? ... : ...)             // ❌ no truthiness in Java  → a real boolean condition
email.substring(at)                  // ❌ includes the '@'       → email.substring(at + 1)
for (int i = 0; i <= arr.length; i++) // ❌ one past the end     → i < arr.length
int best = 0;                        // ❌ for max on any data    → int best = arr[0];
"%d".formatted(3.5)                  // ❌ run-time exception     → "%.2f"
```
