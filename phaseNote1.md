# Phase 1 — Fundamentals — Notes

Concise revision notes for this phase only. Updated as topics are learned. See `notes.md` for the index of all phases.

---

## Anatomy of a Java program

```java
package com.chandan.ledger;          // 1. package (optional, but always used in real code) — must be first statement

import java.time.LocalDate;          // 2. imports

public class App {                   // 3. type declaration; public class name == file name
    public static void main(String[] args) {   // 4. entry point
        System.out.println("Today: " + LocalDate.now());   // 5. statements end with ;
    }
}
```

- Everything lives inside a type (class/interface/enum/record). No free-floating functions or variables.
- Blocks use `{}`; statements end with `;` (no ASI like JS — a missing `;` is a compile error).
- One file may contain several classes, but **at most one** `public` **top-level class**, named like the file.



## `main` method, piece by piece

- `public` — launcher can call it from outside. `static` — runs without creating an object. `void` — returns nothing (exit code via `System.exit(n)`). `String[] args` — CLI arguments.
- Canonical form: `public static void main(String[] args)` — use this in real code.
- Java 25 launcher also accepts non-`private`, non-static, no-arg `main` (e.g. `void main()`), mainly for scripts/learning. A `private main` → "Main method not found" at **launch** time (compiles fine!).



## Packages

- Namespace for classes, like a folder-based module path. Convention: reversed domain, lowercase → `com.acme.orders`.
- Directory must mirror package: `com/acme/orders/OrderSummary.java`.
- **Fully qualified name (FQN)**: `com.acme.orders.OrderSummary` — this is the class's real identity.
- Compile: `javac -d out src/com/acme/orders/OrderSummary.java` → `out/com/acme/orders/OrderSummary.class`.
- Run: `java -cp out com.acme.orders.OrderSummary` (classpath root + FQN). `java -cp out OrderSummary` → `ClassNotFoundException` (no `out/OrderSummary.class` exists).
- The FQN is **stored inside the** `.class` **file**. Running `java App` from inside `out/com/chandan/ledger/` finds `./App.class`, but its real name is `com/chandan/ledger/App` → `NoClassDefFoundError (wrong name: …)`.
  - `ClassNotFoundException` = "no such file found for that name". `NoClassDefFoundError (wrong name)` = "found a file, but it's a different class".
- Why: avoids name collisions (your `User` vs a library's `User`), groups related code, enables package-private visibility.



## Imports

- `import` is **only a naming shortcut** — nothing is loaded or executed (unlike JS `import`, which runs the module).
- `java.lang.*` (String, System, Math, Integer…) is imported automatically.
- Without import you can use the FQN: `java.time.LocalDate.now()`.
- Prefer single-type imports over wildcards (`java.util.*`) — clearer, avoids ambiguity.



## Comments

- `// line`, `/* block */`, `/** Javadoc */` (documentation for public APIs, rendered by IDEs/`javadoc` tool).



## Naming conventions (followed by the whole ecosystem — Spring relies on some)


| Thing                             | Style                    | Example                                     |
| --------------------------------- | ------------------------ | ------------------------------------------- |
| Package                           | lowercase, dot-separated | `com.acme.orders`                           |
| Class / interface / enum / record | PascalCase noun          | `OrderService`, `PaymentStatus`             |
| Method                            | camelCase verb           | `calculateTotal()`, `isEligibleForRefund()` |
| Variable / parameter              | camelCase                | `orderId`                                   |
| Constant (`static final`)         | UPPER_SNAKE_CASE         | `MAX_RETRIES`                               |


- Booleans: `isX`/`hasX`/`canX`. Acronyms: `HttpClient`, `userId` (not `HTTPClient`, `userID`) — common modern style.



## How javac reports errors

- Errors come in **stages**: syntax (parse) errors first → fix → then symbol/type errors appear → fix → then launch/run-time errors.
- Fixing "all" errors may reveal new ones. Always fix the **first** error first; later errors can be side effects.
- Three layers seen so far: **compile-time** (javac), **launch-time** (JVM can't find class or main), **run-time** (exceptions while executing).



## Variables & primitive types

- Two kinds of types: **primitives** (the value itself, 8 fixed types, lowercase) and **references** (point to objects: `String`, `LocalDate`, arrays, your classes).

  | Type      | Size   | Range / notes                                      | Use for                                                |
  | --------- | ------ | -------------------------------------------------- | ------------------------------------------------------ |
  | `byte`    | 8 bit  | −128..127                                          | raw binary data                                        |
  | `short`   | 16 bit | ±32k                                               | rarely used                                            |
  | `int`     | 32 bit | ±2.1 billion (`Integer.MAX_VALUE` = 2,147,483,647) | default whole number: counts, indexes                  |
  | `long`    | 64 bit | ±9.2 × 10^18                                       | IDs, timestamps (ms), byte sizes, money in minor units |
  | `float`   | 32 bit | ~7 digits                                          | almost never (graphics/ML)                             |
  | `double`  | 64 bit | ~15–16 digits                                      | measurements, ratios, averages — **never money**       |
  | `char`    | 16 bit | one UTF-16 code unit, numeric (`'A' + 1` = 66)     | single characters                                      |
  | `boolean` | —      | `true`/`false` only, **not a number**              | flags                                                  |

- Declare: `int count = 0;` · constant local: `final int maxRetries = 3;`
- **Definite assignment**: a local variable must be assigned before it's read, or it's a compile error ("might not have been initialized"). (JShell is lenient here — test with javac.)
- Literals: `42` (int), `42L` (long), `3.14` (double), `3.14f` (float), `'A'` (char), `1_000_000` (underscores for readability), `0xFF`, `0b1010`.
- `var`: **compile-time** inference for local variables. Type is fixed forever after (`var n = 10; n = 2.5;` → error). Needs an initializer. Not dynamic typing — not JS `let`.



## Arithmetic rules

- `int op int` → `int`. **Integer division truncates toward zero**: `7 / 2` = 3, `-7 / 2` = −3. `%` takes the sign of the left operand: `-7 % 2` = −1.
- Mixed types → **widened** to the larger type: `7 / 2.0` = 3.5. `byte`/`short`/`char` are promoted to `int` in arithmetic (`short s = s + 1` fails; `s += 1` compiles — compound assignment casts implicitly).
- Order matters: `10 / 3 * 3` = 9. `completed / total * 100` with ints → 0 for any completed < total. Fix: `completed * 100.0 / total`.
- Integer ÷ 0 → `ArithmeticException` (run time). Double ÷ 0 → `Infinity` (no error!).



## Casting

- **Widening** (int → long → double) is automatic and safe.
- **Narrowing** needs an explicit cast and can lose data: `(int) 3.99` = 3 (truncates, doesn't round), `(int) 3_000_000_000L` = garbage (wraps), `(byte) 200` = −56.
- javac error "possible lossy conversion from X to Y" = you're narrowing without a cast. Don't add a cast just to silence it — first ask if the value can actually fit.



## Overflow — silent!

- `Integer.MAX_VALUE + 1` = −2147483648. No error, no warning.
- Classic bug: `long ms = 1000 * 60 * 60 * 24 * 30;` → −1702967296. The multiplication runs in `int` **before** assignment to `long`. Fix: `1000L * 60 * ...` (make the first operand long).
- When overflow must never happen: `Math.addExact`, `Math.multiplyExact` → throw `ArithmeticException` instead of wrapping.
- Overflow **never** throws on its own — it's always "silently wrong", not "run time". And the wrapped value isn't always negative: `1000 * 60 * 60 * 24 * 60` → 889,032,704 (plausible-looking, wrong).
- Declaring the result `long` is not enough: `long m = requestsPerDay * 30;` still overflows. Make an operand long: `requestsPerDay * 30L`.



## Division by zero — don't "fix" it by switching to double

- `x / (double) 0` → `Infinity`: you've turned a loud crash into a silent wrong value.
- Real fix: handle the zero case explicitly (`if (pendingJobs == 0) ...`) and decide what the business wants (0? "N/A"? skip?).



## Floating point & money

- `0.1 + 0.2` = 0.30000000000000004 (binary floating point can't represent 0.1 exactly).
- Money: **never** `double`. Two valid approaches:
  - `long` **minor units** (paise/cents): simple, fast, exact for +, −, × by whole numbers. Many payment APIs (e.g. Stripe) use this. Division needs an explicit rounding decision.
  - `BigDecimal`: exact decimals + explicit `RoundingMode`; use when dividing, applying rates/tax/discount %, or converting currencies.
  - Common combo: store/transport as `long` paise → convert with `BigDecimal.valueOf(paise, 2)` (= rupees, exact) when you need division or display.
  - Create BigDecimal from **String** or `valueOf`, never `new BigDecimal(0.1)` (captures the double's binary error: 0.1000000000000000055…).
- Counts (subscribers, items) are `int`**/**`long`, not BigDecimal or double.
- **Source of truth = smallest unit.** Compute in paise, derive rupees for display. Never go rupees → `longValue()` → paise (truncates fractions silently).
- `(double) a / b` casts `a` **before** dividing → double division ✅. `(double) (a / b)` casts **after** int division → fraction already lost ❌.



## Avoiding unit bugs

- Replace magic numbers with named constants: `static final int PAISE_PER_RUPEE = 100;` → one definition to review, and the name says the intent.
- Tests: an **example test** with a hand-computed expected value (2.5M × ₹999 = 249,750,000,000 paise) catches wrong factors. An **invariant test** (paise / rupees == 100) checks consistency but passes if both are wrong the same way. Use both. (JUnit: Phase 10.)
- In review: estimate the order of magnitude and compare with the output.



## Format strings (`formatted` / `String.format`)

- `"template".formatted(a, b, c)` ≡ `String.format("template", a, b, c)` (`formatted` is Java 15+).
- Each `%…` placeholder is filled **left to right** by the arguments.

  | Spec                    | Meaning                                     | Example                     |
  | ----------------------- | ------------------------------------------- | --------------------------- |
  | `%d`                    | integer (`int`, `long`)                     | `"%d".formatted(42)` → `42` |
  | `%.2f`                  | decimal, 2 places (rounds)                  | `42.857…` → `42.86`         |
  | `%,d` / `%,.2f`         | with grouping separators                    | `2,497,500,000`             |
  | `%s`                    | anything, via its text form                 | `"hi"`, objects             |
  | `%b`                    | boolean                                     | `true`                      |
  | `%5d` / `%-5d` / `%05d` | width 5, right / left aligned / zero-padded | `[ 42]` `[42 ]` `00042`     |
  | `%%`                    | a literal `%` sign                          |                             |
  | `%n`                    | platform line break                         |                             |

- Wrong type (`%d` with a double) → `IllegalFormatConversionException`; too few args → `MissingFormatArgumentException`. Both at **run time** — javac doesn't check format strings (IntelliJ does warn).



## Formatting numbers for output

- `double` prints large values in scientific notation: `83250000.0` → `8.325E7`.
- `String.format("%.2f", x)` or `"%.2f".formatted(x)` → `83250000.00`. `%,.2f` adds grouping → `83,250,000.00`.
- Exact money display from paise: `BigDecimal.valueOf(paise, 2)` → `2497500000.00`.



## Error categories (so far)

1. **Compile time** — javac rejects it (types, syntax, lossy conversion, uninitialized local).
2. **Launch time** — JVM can't find the class or a valid `main`.
3. **Run time** — exception while executing (`ArithmeticException`, `ArrayIndexOutOfBoundsException`).
4. **Silently wrong** — runs fine, wrong answer (overflow, integer division, float rounding). The most dangerous kind.



## Primitives vs references (memory model)

- Primitive variable **holds the value**. Reference variable **holds an address** of an object on the **heap**.
- Assignment copies what the variable holds: for primitives the value, for references the **address** (two variables → same object).
- References can be `null` (points to nothing). Calling a method on `null` → `NullPointerException` (run time). Primitives can never be null.



## Strings

- `String` is a **class** (reference type), not a primitive. `"text"` = String, `'t'` = char.
- **Immutable**: every "modifying" method returns a **new** String. `s.toUpperCase();` alone does nothing — must assign: `s = s.toUpperCase();`.
- Why immutable: safe to share between threads, safe as map keys, can be cached/pooled, can't be changed after a security check.
- Common methods: `length()`, `charAt(i)`, `substring(start, end)` (end exclusive), `indexOf`, `contains`, `startsWith`/`endsWith`, `toLowerCase`/`toUpperCase`, `strip()` (Unicode-aware trim), `isEmpty()`, `isBlank()`, `split(regex)`, `replace`, `repeat(n)`, `String.join`, `formatted`.
- `substring(i)` starts **at** index i — to skip a delimiter found by `indexOf`, use `substring(i + 1)`.
- `split` drops trailing empty strings: `"a,b,,c,,".split(",").length` = 4.
- Parsing: `Integer.parseInt("42")` → 42; bad input → `NumberFormatException` (run time). Other way: `String.valueOf(42)`.



## `==` vs `.equals()`  ⚠️ #1 JS→Java trap

- `==` on references: **same object?** (address comparison). `.equals()`: **same content?**
- String literals are pooled, so `"hello" == "hello"` is `true` — **by accident**. Strings built at run time (input, concatenation with variables, substring, JSON, DB) are new objects → `==` is `false` even with identical text.
- Rule: **always compare strings (and any objects) with** `.equals()`. `==` only for primitives (and `null` checks / enums later).
- Null-safe: `"ADMIN".equals(role)` (constant first) or `Objects.equals(a, b)`. `role.equals("ADMIN")` throws NPE if role is null.
- Case-insensitive: `equalsIgnoreCase`. Ordering: `compareTo` (negative/0/positive).



## String vs StringBuilder — pick by intent

- `String` = immutable value → safe to pass around, read-only work (parsing, slicing, comparing).
- `StringBuilder` = mutable buffer → only when **building** text piece by piece (loops, big reports).
- Mutating a builder in the middle of reading from it makes later reads depend on call order — a common source of bugs.



## String building

- `+` is fine for a few pieces (compiler optimizes it). In **loops**, each `+=` creates a new String → use `StringBuilder` (`append`, then `toString()`).
- **The** `+` **rule:** if **either operand is a String**, `+` concatenates and converts the other operand to text — any type: `true + "!" + 5 + 'c' + 1.5 + null` → `"true!5c1.5null"`. If **neither** is a String, `+` is arithmetic — which is why `char + char` is int: `'J' + 'a'` = 171. Force concatenation by starting with `""`: `"" + 'J' + 'a'` = "Ja".
- `"1" + 2 + 3` = "123" but `1 + 2 + "3"` = "33" (left to right).
- Text blocks (`"""` … `"""`) for multi-line SQL/JSON; indentation is stripped based on the closing delimiter.



# Operators & control flow (Java vs JS differences)



## Operators

- **No** `===`**.** `==` compares primitives by value, objects by reference → `.equals()` for objects.
- **No truthiness:** `if`, `while`, `&&`, `||`, `?:` need a real `boolean`. `if (list)` / `if (str)` don't compile.
- **No** `||` **default idiom and no** `??`**:** use `x != null ? x : fallback` or `Objects.requireNonNullElse(x, fallback)`.
- `&&` / `||` short-circuit (same as JS) — the standard null guard: `if (s != null && !s.isBlank())`.
- `&` / `|` on booleans = non-short-circuiting (both sides evaluated); on ints = bitwise. `^` xor, `<<`, `>>`, `>>>` (unsigned right shift; JS `>>>` coerces to 32-bit, Java's works on int/long).
- Compound assignment has a **hidden cast**: `byte b = 10; b += 5;` compiles, `b = b + 5;` doesn't.
- `instanceof` with **pattern variable**: `if (o instanceof String s && s.length() > 2)` — no cast needed.
- `=` in a condition compiles when the variable is boolean: `if (isPremium = true)` **assigns** and is always true. A classic silent bug.



## if / else

- Condition must be boolean. Otherwise identical to JS.
- Prefer **guard clauses** (validate + `return` early) over deep nesting.



## switch — much stronger than JS

```java
// classic (fall-through, needs break) — legacy style, avoid in new code
switch (tier) { case "gold": x = 20; break; default: x = 0; }

// arrow switch (Java 14+): no fall-through, can be an EXPRESSION
int discount = switch (tier) {
    case "gold", "platinum" -> 20;       // multiple labels
    case "silver" -> 10;
    case null -> 0;                       // switch can handle null (Java 21+)
    default -> { int base = tier.length(); yield Math.min(base, 5); }   // block → yield
};
```

- Works on `int`, `String`, `enum`, and (Java 21+) any type via **pattern matching**:

```java
String describe(Object value) {
    return switch (value) {
        case Integer i when i > 100 -> "big int " + i;   // guard with `when`
        case Integer i -> "int " + i;
        case String s -> "string of length " + s.length();
        case null -> "nothing";
        default -> "something else";
    };
}
```

- A switch **expression** must be exhaustive (hence `default`, unless all enum/sealed cases are covered).
- `switch` on String compares with `.equals()` internally — safe, unlike `==`.
- Classic `switch` without `break` **falls through** to the next case — a common silent bug.



## Loops

- `for (int i = 0; i < n; i++)` — same as JS.
- **Enhanced for** (= JS `for...of`): `for (String arg : args) { ... }`. No index available; use a classic for when you need one.
- `while`, `do { } while (cond);` (runs at least once — JS has this too but it's rarely used).
- `break` / `continue`, plus **labels** for nested loops: `outer: for (...) { for (...) { break outer; } }`.
- There is no `for...in`; iterating a Map's keys comes later (Phase 3).



# Methods



## Anatomy

```java
private static double percentage(int part, int total) { ... }
// modifiers  return-type  name   parameters (typed)     body
```

- Every method lives in a class. `static` = call without an object (`ClassName.method()` or just `method()` inside the class). Instance methods come in Phase 2.
- `void` = returns nothing. Non-void methods must `return` on **every** path → otherwise compile error "missing return statement".
- Access: `private` for helpers used only inside the class, `public` for things other classes call.
- Names: camelCase verbs (`calculateTotal`, `isValid`, `formatRupees`). Small, one job each.



## No default/optional parameters → overloading

- Same name, **different parameter lists** (count or types). Return type alone doesn't count.
- The compiler picks the overload at **compile time** by the argument types: `describe(5)` → int, `describe(5L)` → long, `describe('A')` → int (char widens to int).
- **Varargs**: `static int sum(int... nums)` — `sum()`, `sum(1, 2, 3)`; inside, `nums` is an array. Must be the last parameter.



## Java is ALWAYS pass-by-value

- The method receives a **copy of the variable's value**.
  - Primitive → a copy of the number. Changing it inside doesn't affect the caller.
  - Reference → a copy of the **address**. Both point to the same object:
    - **Mutating the object** (`sb.append("!")`) → caller sees it.
    - **Reassigning the parameter** (`sb = new StringBuilder()`, `status = status.strip()`) → only the local copy changes; caller unaffected.
- Consequence: a method can't "change" a caller's String (immutable + reassignment is local) → **return the new value** instead.
- Same model as JS (objects are shared by reference-copy, rebinding a param is local). Java is *not* pass-by-reference.



## Method design rules of thumb

- `get`/`set`/`is` prefixes are reserved by convention for **accessors of an object's fields** (JavaBeans). Spring/Jackson rely on this. A calculation is `percentage(...)` / `calculateTotal(...)`, not `getPercentage(...)`.
- Don't pass a value **and** something derived from it (`paid`, `total`, `paidPercentage`) — callers can pass inconsistent combinations. Pass the inputs; derive inside.
- Return directly: `return (double) part / total * 100;` — no single-use temp variable needed.
- A method returns **one** value. Need several (e.g. 5 counts)? → array (L7) or an object/record (Phase 2/4).



## ~~Scope~~

- ~~Locals are block-scoped (like~~ `let`~~). Parameters are locals.~~
- ~~Java forbids redeclaring a local in a nested block:~~ `int x = 1; { int x = 2; }` ~~→ compile error (JS allows this shadowing).~~



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



# Arrays



## Basics

- Fixed-size, typed, indexed from 0. **Size can never change** after creation (unlike JS arrays).

```java
int[] counts = new int[5];                    // [0, 0, 0, 0, 0] — elements get DEFAULT values
String[] names = new String[2];               // [null, null]
String[] statuses = {"paid", "pending"};      // literal (only in a declaration)
int[] ids = new int[] {4, 8, 15};             // literal anywhere (e.g. as an argument)
```

- Defaults: numbers `0`/`0.0`, `boolean` `false`, `char` `' '`, references `null`. (Unlike locals, array elements are always initialised.)
- `.length` is a **field**, not a method: `counts.length` (String uses `length()`).
- Index outside `0 .. length-1` → `ArrayIndexOutOfBoundsException` (run time). Classic: `i <= arr.length`.



## Arrays are objects (reference types)

- `int[] b = a;` copies the **reference** → both names, one array. Changes via `b` are visible via `a`.
- Passing an array to a method shares it (pass-by-value of the reference) → the method **can** change its elements.
- Real copy: `Arrays.copyOf(a, a.length)`, `Arrays.copyOfRange(a, from, to)`, or `a.clone()`.
- `a == b` and `a.equals(b)` both compare **identity** for arrays! Content: `Arrays.equals(a, b)` (2D: `Arrays.deepEquals`).
- `System.out.println(arr)` prints `[I@7344699f` (type code + hash). Use `Arrays.toString(arr)` / `Arrays.deepToString(grid)`.



## `java.util.Arrays` utility

`toString`, `deepToString`, `equals`, `sort` (in place! mutates the array), `fill`, `copyOf`, `copyOfRange`, `asList` (preview).

- `Arrays.sort` on Strings is by UTF-16 code: uppercase before lowercase (`"Paid" < "apple"`).



## 2D arrays

- An array of arrays: `int[][] grid = new int[3][4];` → `grid[row][col]`, `grid.length` rows, `grid[r].length` cols.
- Rows can differ in length (**jagged**): `int[][] j = {{1}, {2, 3}, {}};`



## Common algorithms

- Max/min: start from the **first element** (or `Integer.MIN_VALUE`), never from `0` — all-negative data breaks it.
  - Empty array: `MIN_VALUE` start silently returns −2,147,483,648 (silently wrong); `arr[0]` start throws (loud). Either way, guard `arr.length == 0` explicitly.
- Average: `(double) sum / arr.length`, guard `length == 0`.



## Returning several values

- A method returns one value → an `int[]` can carry several (e.g. counts per status).
- Downside: meaning lives in **indexes** (`counts[2]` = ?). Mitigate with named constants; real fix = enum + Map (Phase 2–3) or a record (Phase 4).



## Arrays vs List (preview)

- Arrays: fixed size, primitives allowed, fast, used by varargs/`main`/low-level APIs.
- `List<T>` (Phase 3): grows/shrinks, rich API, objects only — what most application code uses.

