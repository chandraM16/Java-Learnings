# Phase 2 — Object-Oriented Java — Notes

Revision notes for this phase. Every concept: **theory first** (what it is, why, how it works), **then a code snippet**, then an **example / gotcha** with real output (`// →`). See `notes.md` for the index of all phases.

---

## 1. Classes, objects, fields, constructors, `this`, object lifecycle

### Class vs object
- **Class** = compile-time blueprint. It declares which fields and methods every object of that type will have. It exists once (as a `.class` file).
- **Object / instance** = a run-time thing created with `new`. It lives on the **heap** and has its **own copy of every field**. Two objects from the same class never share field state.
- A variable like `Customer a` doesn't hold the object. It holds a **reference** (address) to it, exactly like `int[] arr` in Phase 1.

```java
public class Customer {
    private String name;
    private int points;
    // ...
}

Customer a = new Customer("Asha");
Customer b = new Customer("Ravi");
a.addPoints(10);
// a.points → 10, b.points → 0   (separate objects, separate fields)
```

### Fields and default values
- **Field** = a variable declared in a class, holding part of an object's state.
- Uninitialized fields get **default values** the instant the object is allocated: `0` / `0.0` / `false` / `null` — the same defaults as array elements. This happens *before* the constructor body runs.
- (Local variables are different: they have no default, and reading one before assigning is a compile error.)

```java
public class Demo {
    private int count;       // → 0
    private boolean active;  // → false
    private String label;    // → null
}
```

### Constructors
- Same name as the class, **no return type** (not even `void`), runs on `new`. Its job: get every field into a valid state.
- **Overloading**: several constructors with different parameter lists. The compiler picks one by the call-site arguments — same rule as method overloading.
- **Chaining**: `this(...)` calls another constructor of the same class, to avoid duplicating init logic. Only one such call, as the first statement (see the JDK 25 exception in section 7).
- Adding **any** constructor removes the free no-arg constructor Java would otherwise give you.

```java
public class Customer {
    private final String name;
    private int points;

    public Customer(String name) {             // overload 1
        this(name, 0);                         // chaining → delegates to overload 2
    }

    public Customer(String name, int points) { // overload 2 — the "real" one
        this.name = name;
        this.points = points;
    }
}

new Customer("Asha");        // → overload 1 → overload 2 with points = 0
new Customer("Ravi", 50);    // → overload 2
new Customer();              // ❌ compile error — no no-arg constructor exists any more
```

### `this` and shadowing
- **`this`** = reference to the current object inside an instance method or constructor. Unlike JS, it's always bound to the object the method was called on — no losing `this` in callbacks, no `.bind()`.
- Needed when a parameter name **shadows** a field: `this.name = name;` means field = parameter.
- Without `this.`, `name = name;` just reassigns the parameter to itself. The field silently stays at its default. No error, no crash.
- A **new local variable** with the same name as a field also shadows it silently.

```java
public Customer(String name) {
    name = name;                               // ❌ parameter = itself → field stays null
    this.name = name;                          // ✅
}

public void addPoints(int amount) {
    int points = this.points + amount;         // ❌ declares a NEW local, field unchanged
    this.points = this.points + amount;        // ✅
}
```

### Object lifecycle
- `new` → allocate on heap → fields set to defaults → constructor body runs → reference returned → object stays alive while reachable → eligible for **GC** once nothing references it.
- No manual `delete`/`free`, and no deterministic "destructor" moment (deep GC mechanics: Phase 8).

```java
Customer c = new Customer("Asha");   // allocated, reachable through c
c = null;                            // nothing references it now → GC may reclaim it later
```

### JS/TS comparison
| | Java | JS |
|---|---|---|
| Fields | Fixed set, declared with types at compile time | Dynamic — add any property, any time |
| Constructors | Can overload freely | Exactly one `constructor()`; simulate variants with default params/factories |
| `this` | Always the instance the method was called on | Depends on call site — can be lost, needs binding |

---

## 2. Encapsulation, access modifiers, invariants, immutability

### Encapsulation and invariants
- **Encapsulation** = bundle data with the operations that keep it valid, and hide the data (`private` fields) so nothing outside those operations can touch it. The class is the only gatekeeper of its own state.
- **Invariant** = a fact about an object that must always hold (`balance >= 0`, `memberId` never blank).
- The constructor establishes the invariant; every mutating method must re-check before allowing the change.
- Private fields + validated construction + validated mutation = the invariant literally can't break from outside.

```java
public class Account {
    private final String owner;     // invariant: never blank
    private long balancePaise;      // invariant: never negative

    public Account(String owner) {
        if (owner == null || owner.isBlank()) {
            throw new IllegalArgumentException("owner is required");
        }
        this.owner = owner;
    }

    public void withdraw(long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        if (amount > balancePaise) {
            throw new IllegalStateException("insufficient funds");
        }
        balancePaise -= amount;
    }

    public long getBalancePaise() {
        return balancePaise;
    }
}
```

### Access modifiers
- Most → least restrictive: `private` (this class only) → *(none, package-private)* (same package) → `protected` (package + subclasses; real use in section 7) → `public` (everyone).
- Enforced by the **JVM at runtime** — unlike TS `private`, which is compile-time only and erasable with a cast. Java's `private` behaves like a real JS `#field`.

| Modifier | Visible from |
|---|---|
| `private` | this class only |
| *(none)* package-private | same package |
| `protected` | same package + subclasses |
| `public` | everywhere |

```java
// from another class:
account.balancePaise = -999;   // ❌ compile error: balancePaise has private access in Account
account.withdraw(500);         // ✅ the only way in — and it checks the rules
```

### Getters / setters
- Not a language feature — just regular methods.
- The `get`/`set`/`is` naming is a **convention** that Spring, Jackson and JavaBeans recognise by reflection. Java itself has no `get`/`set` keyword (unlike JS).
- Don't add a setter "just because": every setter is a way around your invariants.

```java
public long getBalancePaise() { return balancePaise; }   // convention-named plain method
public boolean isActive()     { return active; }         // "is" for booleans
```

### Throwing to reject bad input or state
- `IllegalArgumentException` → the **argument** itself is bad (negative amount, blank name).
- `IllegalStateException` → the call is fine in general, but not given the object's **current state** (redeeming more points than available).
- A one-line refusal. Formal `try`/`catch`/custom exceptions come in Phase 5.

```java
card.redeemPoints(-5);    // → IllegalArgumentException: amount must be positive
card.redeemPoints(999);   // → IllegalStateException: insufficient points   (only has 70)
```

### `final` on a field ≠ immutable
- `final` locks only the field's own reference/value, not the object it points to.

```java
private final int[] ids = {1, 2, 3};
ids = new int[5];    // ❌ compile error — final reference can't be reassigned
ids[0] = 99;         // ✅ compiles — the array contents are still mutable
```

### The defensive-copy trap (two leaks, not one)
- A `private` field can still be corrupted from outside through a **shared array reference**, through *two* separate entry points:
  1. **Constructor leak**: `this.balances = balances;` doesn't copy — the field and the caller's variable are now the same array object.
  2. **Getter leak**: `return balances;` doesn't copy either — the caller gets a live reference to internal state.
- Both are the Phase 1 rule "assignment copies the reference, not the object", reached through a constructor parameter and a return value instead of `int[] b = a;`.
- Fix: copy on the way **in** and the way **out**.

```java
public class Wallet {
    private final int[] balances;

    public Wallet(int[] balances) {
        this.balances = balances;              // ❌ leak 1
    }
    public int[] getBalances() {
        return balances;                       // ❌ leak 2
    }
}

int[] initial = {100, 200};
Wallet w = new Wallet(initial);
w.getBalances()[0] = 999999;                   // through the getter
initial[1] = -50;                              // through the caller's own array
w.getBalances()                                // → [999999, -50]   "private" data changed twice
```
```java
this.balances = balances.clone();              // ✅ in   (or Arrays.copyOf)
return balances.clone();                       // ✅ out
return List.copyOf(items);                     // ✅ same idea for a List (Phase 3)
```

### Common mistake (seen twice: `Product`, `MembershipCard`)
- Methods made `private` when other classes need to call them. It compiles and works **only** because `main` happens to live in the same class.

```java
private void restock(int qty) { ... }   // ❌ any other class → compile error
public void restock(int qty) { ... }    // ✅
```

---

## 3. `equals`, `hashCode`, `toString`

### Where they come from
- Every class silently extends `Object` (section 7), so every object already has `equals()`, `hashCode()` and `toString()` before you write anything.
- Default `equals()` = identity (`this == o`).
- Default `hashCode()` = identity-based (different for essentially every object).
- Default `toString()` = `ClassName@hexHashCode` — useless for debugging.

```java
Coordinate a = new Coordinate(3, 4);
Coordinate b = new Coordinate(3, 4);
a == b                          // → false
a.equals(b)                     // → false   (default = identity)
a.hashCode() == b.hashCode()    // → false
System.out.println(a);          // → Coordinate@1b6d3586
```

### What `hashCode()` is for
- An `int` "bucket / locker number" for an object. `HashMap`/`HashSet` (Phase 3) use it to jump straight to the right bucket instead of checking every element.
- **The contract**: if `a.equals(b)` is `true`, then `a.hashCode() == b.hashCode()` **must** be true. Otherwise a hash-based lookup searches the wrong bucket and can't find an object that's actually there. Nothing crashes — the object is just unreachable.

```java
// equals overridden, hashCode NOT:
a.equals(b)                    // → true
a.hashCode() == b.hashCode()   // → false  (identity-based default)
set.add(a);
set.contains(b)                // → false  — wrong bucket, no error
```

### Writing them correctly
- **`@Override`** (first annotation seen): tells the compiler "this must match a method on the superclass". A typo in the signature becomes a compile error instead of a silent, unrelated new method.
- **`getClass()`** returns the exact runtime class. `getClass() != o.getClass()` bails out (`return false`) before casting, so comparing with an unrelated type never throws `ClassCastException`.
- **`Objects.equals(a, b)`** — null-safe (`Objects.equals(null, "x")` → `false`, no NPE). Use it for any field that could be `null`.
- **`Objects.hash(a, b, ...)`** — combines several fields into one hash code.
- Standard `equals()` shape: same-reference shortcut → null / class check → cast → compare fields.
- Use the **same fields** in `equals` and `hashCode`.

```java
import java.util.Objects;

public final class SeatAssignment {
    private final char row;
    private final int seatNumber;

    public SeatAssignment(char row, int seatNumber) { ... }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;                                   // 1. same object
        if (o == null || getClass() != o.getClass()) return false;    // 2. null / other type
        SeatAssignment other = (SeatAssignment) o;                    // 3. cast
        return row == other.row && seatNumber == other.seatNumber;    // 4. compare fields
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, seatNumber);                         // same fields as equals
    }

    @Override
    public String toString() {
        return row + "" + seatNumber;
    }
}

SeatAssignment s1 = new SeatAssignment('A', 34);
SeatAssignment s2 = new SeatAssignment('A', 34);
s1 == s2          // → false
s1.equals(s2)     // → true
s1.hashCode()     // → 3010
s2.hashCode()     // → 3010
s1                // → A34
```
```java
field.equals(other.field)          // ❌ NPE if field is null
Objects.equals(field, other.field) // ✅

public String toSting() { ... }    // ❌ typo, no @Override → println(obj) still prints Seat@1b6d...
@Override
public String toString() { ... }   // ✅ a typo here would be a compile error
```

### Mutable fields + `hashCode()` = footgun
- If a hash-based collection put an object in a bucket based on a field, and that field later changes, the object is stuck in its old bucket: "in the set", but `contains()` returns `false`. That's why `final` fields matter here too (section 2, immutability).

```java
ticket.status = "PENDING";
tickets.add(ticket);              // bucket chosen from "PENDING"
ticket.status = "CONFIRMED";      // hash changes
tickets.contains(ticket)          // → false — lost in its own set
```

### Why `println(obj)` calls `toString()`
- Same Phase 1 string-concatenation rule: if either side of `+` is a String, the other side is converted to text — for objects, through `toString()`. No new mechanism.

```java
System.out.println(seat);         // → A34
String s = "Seat: " + seat;       // → "Seat: A34"
```

---

## 4. Static members, `final` classes/methods

### Static vs instance
- **Instance field** = one copy per object (section 1).
- **Static field** = one copy total, shared by every object of the class. Accessed as `ClassName.field` (or unqualified inside the class).
- **Static method** = belongs to the class, has no `this`, so it can't touch instance fields/methods directly. Trying is a **compile-time** error: `non-static variable X cannot be referenced from a static context`.
- `main` is `static` because the JVM calls it before any object exists — there's nothing for an instance method to belong to yet.
- `static final` = a constant (the Phase 1 `PAISE_PER_RUPEE` pattern): `static` → one shared copy, `final` → never reassigned.

```java
public final class Invoice {
    private static final String PREFIX = "INV-";   // constant
    private static int nextNumber = 1;             // ONE shared counter
    private final String invoiceNumber;            // one per object

    public Invoice(long amountInPaise) {
        this.invoiceNumber = PREFIX + "%04d".formatted(nextNumber);   // read, then
        nextNumber++;                                                  // increment
    }

    public static int totalInvoicesCreated() {     // no object needed
        return nextNumber - 1;
    }
}

new Invoice(10000); new Invoice(40000); new Invoice(70000);
// → INV-0001, INV-0002, INV-0003
Invoice.totalInvoicesCreated()   // → 3   (called on the class)
```
```java
public static int totalInvoicesCreated() {
    return invoiceNumber.length();   // ❌ non-static variable invoiceNumber cannot be referenced from a static context
}
```

### Subtle failure: static field turned into an instance field
- This doesn't always fail to compile. A constructor always has `this`, so it can still read/write the field — each object just silently gets its **own fresh copy** instead of sharing one.
- A shared incrementing counter would compile cleanly and give the same starting value to every object, forever. Only code that is itself `static` and touches the field fails to compile.

```java
private int nextNumber = 1;      // ❌ static removed
// constructor still compiles → every invoice is INV-0001
// totalInvoicesCreated()    → compile error (the only visible symptom)
```

### `final` classes and methods
- `final class` — blocks all subclassing. `String` itself is `final`, so nobody can override how it behaves.
- `final` method — blocks overriding that one method in a subclass that's otherwise still extendable.
- `static final` array/collection field: `final` still only locks the reference, not the contents.

```java
public final class Money { }
class MyMoney extends Money { }      // ❌ cannot inherit from final Money

static final int[] IDS = {1, 2, 3};
IDS = new int[3];                    // ❌ compile error
IDS[0] = 99;                         // ✅ compiles — contents still mutable
```

---

## 5. Packages & multiple classes per file

### A package is a named group of classes
- The package name is the folder path written with dots. Every class in the same folder has the **same** `package` line — that line says "I belong to this group".
- The package becomes part of the class's real name: `com.chandan.payment.Money` (its FQN).
- Folders must mirror the package for IDEs, Maven and javac's `-sourcepath` lookups. (`javac -d out OneFile.java` alone tolerates a mismatch, but as soon as another class must be found by name it fails with `cannot find symbol`. Always mirror.)

```
src/com/chandan/payment/Money.java             package com.chandan.payment;
src/com/chandan/payment/CardValidator.java     package com.chandan.payment;
src/com/chandan/payment/PaymentProcessor.java  package com.chandan.payment;
src/com/chandan/orders/Checkout.java           package com.chandan.orders;
```

### A `.java` file = a compilation unit
- Order: `package` line (at most one) → `import` lines → one or more **top-level classes**.
- **At most one `public` top-level class per file**, and its name must equal the file name.
- Extra non-public classes in the same file are **package-private helpers**. Each still compiles to its **own** `.class` file — the JVM has no idea several classes shared a file.
- When to put a helper in the same file: tiny, only used by that class. Otherwise one class per file (what teams expect; IDE/Git friendly).

```java
// OrderApp.java
package com.chandan.orders;

import com.chandan.payment.Money;

public class OrderApp { ... }      // the one public class, named like the file
class OrderLabel { ... }           // package-private helper → OrderLabel.class

public class Three { }             // ❌ class Three is public, should be declared in a file named Three.java
```

### Using another class
- **Same package → no import.** Different package → `import` (or write the FQN inline), **and** the class must be `public`.
- `import` is only a naming shortcut. It never controls compilation — `javac` finds classes through the classpath/source path.

| Where it lives | What you need |
|---|---|
| Same package | Nothing. Just use the name. |
| Different package | `import com.chandan.payment.Money;` **and** the class must be `public` |
| `java.lang` (`String`, `Math`…) | Nothing, imported automatically |

```java
import com.chandan.payment.Money;                              // import, or:
java.time.LocalDate today = java.time.LocalDate.now();         // FQN inline, no import
```
```
error: cannot find symbol  symbol: class Money  location: class Checkout              ← missing import
error: CardValidator is not public in com.chandan.payment; cannot be accessed from outside package
```

### Package boundary = visibility boundary
- A class or member with no modifier is *package-private*: usable inside its own package, invisible outside. That's how you keep helpers (`CardValidator`) internal while exposing an API (`PaymentProcessor`, `Money`). First real use of the 4th access level from section 2.

```java
package com.chandan.payment;

public class PaymentProcessor { ... }   // the API — visible everywhere
class CardValidator { ... }             // internal helper — invisible to com.chandan.orders
```

### Name clashes and static imports
- **Same simple name in two packages** (`java.util.List` vs `java.awt.List`, or `payment.Status` vs `orders.Status`): import one, use the FQN for the other. There's no `import X as Y` in Java.
- **Static import** brings in a *member*, not a type. Use sparingly — it hides where the method came from. Common in tests (`assertEquals`).

```java
import java.util.List;
java.awt.List awtList = new java.awt.List();    // the other one, by FQN

import static java.lang.Math.max;
int bigger = max(3, 9);                         // → 9, no "Math." needed
```

### Nested folders = separate packages
- `com/chandan/orders/f1/Cart.java` **must** declare `package com.chandan.orders.f1;` — one folder, one package.
- `com.chandan.orders` and `com.chandan.orders.f1` are two **unrelated** packages that only share a name prefix. A class in `…orders.f1` needs an `import` to use a `public` class in `…orders`, and **cannot** see its package-private members. Siblings (`f1` ↔ `f2`) are the same.
- A mismatched package (file in `f1/`, says `package com.chandan.orders;`) may still compile when every file is passed to javac, but IntelliJ flags it, `-sourcepath` lookups fail, and same-named classes collide: `error: duplicate class: com.chandan.orders.Report`.
- Package depth is just naming. Name sub-packages by feature (`orders.pricing`, `orders.shipping`), never `f1`/`f2`/`util` dumping grounds.

```
error: OrderHelper is not public in com.chandan.orders; cannot be accessed from outside package
```

### Compile & run with packages
```bash
javac -d out $(find src -name '*.java')     # every file together; output mirrors packages under out/
java -cp out com.chandan.orders.Checkout    # run by FQN
```

---

## 6. Inheritance, overriding, `super`

### `extends`, `protected`, `super`
- `class Manager extends Employee` — `Manager` gets every field and method of `Employee` for free, then can add or override.
- `protected` is exactly for this: fields/methods a subclass needs, but the outside world shouldn't touch directly.
- `super(...)` calls the superclass constructor. `super.method()` calls the superclass's version of a method from inside an override — used to *extend* behavior rather than replace it.

```java
public class Employee {
    protected final String name;
    protected final long baseSalaryInPaise;

    public Employee(String name, long baseSalaryInPaise) {
        this.name = name;
        this.baseSalaryInPaise = baseSalaryInPaise;
    }

    public long calculateBonus() {
        return baseSalaryInPaise / 10;                      // 10%
    }
}

public class Manager extends Employee {
    private final int teamSize;

    public Manager(String name, long baseSalaryInPaise, int teamSize) {
        super(name, baseSalaryInPaise);                     // parent's constructor
        this.teamSize = teamSize;
    }

    @Override
    public long calculateBonus() {
        return super.calculateBonus() + teamSize * 50000L;  // parent's 10% + team bonus
    }

    public int getTeamSize() {
        return teamSize;
    }
}
```

### Statements before `super(...)` — JDK 25 Flexible Constructor Bodies
- The old rule "`super(...)`/`this(...)` must be the literal first statement" was relaxed when JDK 25 finalized **Flexible Constructor Bodies**.
- Statements before `super(...)`/`this(...)` are legal as long as they don't touch `this`/instance state yet. Validating constructor arguments before delegating is the intended use, and it works: the validation can throw before the superclass is ever constructed.
- Older Java (and most StackOverflow answers) say this is illegal — it was, before the feature finalized.

```java
public Manager(String name, long base, int teamSize) {
    if (teamSize <= 0) {
        throw new IllegalArgumentException("Team Size should be greater than 0.");   // ✅ JDK 25
    }
    super(name, base);
    this.teamSize = teamSize;
}

new Manager("Bad", 100000L, -1);   // → IllegalArgumentException, Employee never constructed
```

### Multi-file compile reminder
- `javac` only compiles the file(s) you give it plus whatever it finds on its source path. Compiling `Manager.java` alone, when it extends `Employee` in another file, fails even with both files in the same folder.

```bash
javac -d out src/com/chandan/payroll/Manager.java     # ❌ cannot find symbol: class Employee
javac -d out src/com/chandan/payroll/*.java           # ✅ both together
```

---

## 7. Polymorphism & dynamic dispatch

### Two questions, answered two different ways
- **What can you call?** → decided by the variable's **declared type**, checked at **compile time**.
- **Which version actually runs?** → decided by the object's **real runtime type**, at run time. This is **dynamic dispatch**.

```java
Employee e2 = new Manager("Ravi", 8_000_000L, 4);
//  ^ declared type          ^ runtime type

e2.calculateBonus();     // ✅ compiles (Employee has it) → runs MANAGER's version → 1000000
e2.getTeamSize();        // ❌ compile error — Employee has no getTeamSize():
                         //    error: cannot find symbol
                         //      symbol:   method getTeamSize()
                         //      location: variable e2 of type Employee
```

### Dispatch happens even inside the parent's own code
- Overriding isn't "replace the method in this file". The JVM looks up the real object's class **at every call**, so even code written entirely inside the parent ends up calling the child's override.

```java
class Shape {
    public double area() { return 0.0; }
    public String describe() { return "area=" + area(); }   // really this.area()
}

class Circle extends Shape {
    private final double radius;
    Circle(double radius) { this.radius = radius; }
    @Override public double area() { return Math.PI * radius * radius; }
}

Shape s = new Circle(2.0);
s.area()       // → 12.566370614359172
s.describe()   // → area=12.566370614359172   (Shape's code, Circle's area)
```

### Casting to reach a subclass-only method
- Cast to call a subclass-only method through a superclass-typed variable. Same syntax as Phase 1 numeric casts — but a **reference cast never converts the object**, it only asks the JVM to confirm the object already is that type. Wrong guess → `ClassCastException` at **run time**.
- Safe version: `instanceof` with a pattern variable.

```java
((Manager) e2).getTeamSize();         // ✅ → 4   (e2 really is a Manager)

Employee plain = new Employee("Asha", 5_000_000L);
((Manager) plain).getTeamSize();      // compiles → ClassCastException at run time

if (plain instanceof Manager m) {     // ✅ only runs when it really is a Manager
    m.getTeamSize();
}
```

### `instanceof` vs `getClass()`
```java
Item c = new Ebook("Spring", 60000);   // Ebook extends Book
c instanceof Book                      // → true   ("a Book or any subclass?")
c.getClass() == Book.class             // → false  ("exactly Book?" — it's Ebook)
```

---

## 8. Abstract classes vs interfaces

### Abstract class
- Can't be instantiated: `new Employee(...)` → compile error `Employee is abstract; cannot be instantiated`.
- **Can** hold real instance state and constructors. Its constructor is usually `protected` (only subclasses can call it anyway).
- **Abstract method**: signature only, ends in `;`, no body. A class with any abstract method must itself be `abstract`. Every concrete subclass must implement every inherited abstract method, or javac names exactly which one is missing.

```java
public abstract class Employee implements Payable {
    protected final String name;
    protected final long baseSalaryInPaise;

    protected Employee(String name, long baseSalaryInPaise) { ... }

    @Override
    public long calculatePay() {
        return baseSalaryInPaise;
    }

    public abstract long calculateBonus();     // no body — each subclass decides
}

public class Intern extends Employee {
    public Intern(String name, long base) { super(name, base); }

    @Override
    public long calculateBonus() { return 0; }
}

new Employee("X", 100000L);   // ❌ error: Employee is abstract; cannot be instantiated
class Guest extends Member { } // ❌ Guest is not abstract and does not override abstract method type() in Member
```

### Interface
- A contract: "implementers can do these things". No constructors, no instance state.
- **Abstract method**: every implementer must define it.
- **`default` method** (Java 8+): has a body, inherited for free, can be overridden. Added so library interfaces (e.g. `List`) could gain methods without breaking every existing implementer.
- **`static` method**: belongs to the interface, called as `Payable.minimumWage()`.

```java
public interface Payable {
    long calculatePay();                       // abstract

    default String describePay() {             // default
        return "Pay: " + calculatePay();
    }

    static long minimumWage() {                // static
        return 1_500_000L;
    }
}

Employee m = new Manager("Ravi", 8_000_000L, 5);
m.describePay()          // → Pay: 8000000
Payable.minimumWage()    // → 1500000
```

### Choosing between them
- **Deciding factor**: a class can `extends` only **one** class, but `implements` as many interfaces as it likes.
- Shared real state/code across a tight family → abstract class. A capability many unrelated classes might have → interface.
- Dispatch works through default methods too: a `default` method calling an abstract method runs the real object's implementation (section 7). The *method* is shared; the *data* it reads (`this`'s fields) is per object.

```java
class Manager extends Employee implements Payable, Comparable<Manager> { }   // ✅
class X extends A, B { }                                                     // ❌ no multiple class inheritance
```

| | Abstract class | Interface |
|---|---|---|
| Instance fields / state | ✅ | ❌ (constants only) |
| Constructor | ✅ | ❌ |
| Per class | **one** (`extends`) | **many** (`implements`) |
| Use for | a related family sharing code/state | a capability unrelated classes can have |

---

## 9. Composition vs inheritance

### Is-a vs has-a
- **Inheritance = is-a** (`Manager` is an `Employee`). **Composition = has-a**: keep another object in a field and forward calls to it (**delegation**).
- Default to composition. Inherit only for a true is-a where you control both classes (or the parent is designed for extension).
- Test: "Is an `OrderService` an `EmailSender`?" No → don't `extends`.

### Constructor injection
- The standard composition shape (what Spring automates in Phase 11): a `private final` field typed as an **interface**, passed in through the constructor.
- Don't `new` the dependency inside the class — it's hard-wired: you can't swap it, and you can't test without the real thing.

```java
public interface Notifier {
    void send(String to, String message);
}

public class OrderService {
    private final Notifier notifier;

    public OrderService(Notifier notifier) {
        this.notifier = notifier;
    }

    public void placeOrder(String customer, long amountInPaise) {
        if (amountInPaise <= 0) {
            throw new IllegalArgumentException("Amount can't be negative or zero!");
        }
        notifier.send(customer, "Order placed: Rs " + "%.2f".formatted(amountInPaise / 100.0));
    }
}

new OrderService(new EmailNotifier()).placeOrder("Apple", 8000000);
// → Email Service -> Apple and Order placed: Rs 80000.00
new OrderService(new SmsNotifier()).placeOrder("Samsung", 9000000);
// → SMS Service -> Samsung and Order placed: Rs 90000.00

private final Notifier notifier = new EmailNotifier();   // ❌ hard-wired
```

### Test double (fake)
- An implementation of the interface that only **records** what it received — no printing, no validation.
- Declare it as its own type in the test (`FakeNotifier fake = ...`) so you can read its getters afterwards, while `OrderService` still only sees a `Notifier`. Same idea as a Jest `jest.fn()` checked with `toHaveBeenCalledWith`.

```java
public class FakeNotifier implements Notifier {
    private String lastTo;
    private String lastMessage;

    @Override
    public void send(String to, String message) {
        this.lastTo = to;
        this.lastMessage = message;
    }

    public String getLastTo() { return lastTo; }
    public String getLastMessage() { return lastMessage; }
}

FakeNotifier fake = new FakeNotifier();              // NOT "Notifier fake" — getters wouldn't be visible
new OrderService(fake).placeOrder("Google", 7000000);
fake.getLastTo() + " / " + fake.getLastMessage()
// → Google / Order placed: Rs 70000.00
```

### Fragile base class
- Inside a parent, `add(n)` is really `this.add(n)`, and `this` is the real object — so the parent's own code can call **your** override (section 7). If you don't know exactly how the parent calls itself, your subclass breaks silently.
- `super.add(n)` is the one call that skips dispatch.
- Composition fix: hold the parent as a field. Inside it, `this` is the `Counter`, so nothing dispatches back.

```java
class Counter {
    protected int total;
    public void add(int n) { total += n; }
    public void addAll(int[] numbers) {
        for (int n : numbers) add(n);           // this.add(n) → LoggingCounter.add
    }
}

class LoggingCounter extends Counter {
    private int addCalls = 0;
    @Override public void add(int n) { addCalls++; super.add(n); }
    @Override public void addAll(int[] numbers) { addCalls += numbers.length; super.addAll(numbers); }
}

c.add(5);
c.addAll(new int[] {1, 2, 3});
// total → 11, addCalls → 7   (wanted 4: each number counted twice)
```
```java
class LoggingCounter {                           // ✅ composition, no extends
    private final Counter counter = new Counter();
    private int addCalls = 0;

    public void add(int n) { addCalls++; counter.add(n); }
    public void addAll(int[] numbers) {
        addCalls += numbers.length;
        counter.addAll(numbers);                 // inside, this = Counter → Counter.add → 4
    }
}
```

---

## 10. Enums

### What an enum really is
- A fixed set of values; each value is a **single object**, created once when the class loads.
- What javac generates (`javap -p`): `public final class OrderStatus extends java.lang.Enum<OrderStatus>`, one `public static final OrderStatus PAID;` per value, a **private** constructor, plus `values()` / `valueOf(String)`.
- Because there's exactly one `PAID` object, **`==` is correct** for enums (the exception to Phase 1's "use `.equals()` for objects").

```java
public enum OrderStatus { PENDING, PAID, SHIPPED, DELIVERED, CANCELLED }

OrderStatus s = OrderStatus.PAID;
s == OrderStatus.PAID     // → true
OrderStatus.PIAD          // ❌ compile error — a String "piad" would compile fine
```

### Fields, constructor, methods
- `PAID("Paid")` **is** a constructor call. Values come first, then `;`, then fields and methods.
- Behavior can live in the enum: in `canMoveTo(next)`, `switch (this)` — `this` is the value the method was called on. Inside the enum write `case PAID`, not `case OrderStatus.PAID`.

```java
public enum OrderStatus {
    PENDING("Awaiting payment"),
    PAID("Paid"),
    SHIPPED("On the way"),
    DELIVERED("Delivered"),
    CANCELLED("Cancelled");                 // ; required

    private final String label;

    OrderStatus(String label) {             // always private
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public boolean canMoveTo(OrderStatus next) {
        return switch (this) {
            case PENDING -> next == PAID || next == CANCELLED;
            case PAID -> next == SHIPPED || next == CANCELLED;
            case SHIPPED -> next == DELIVERED;
            case DELIVERED, CANCELLED -> false;
        };
    }
}

OrderStatus.PAID.getLabel()                         // → Paid
OrderStatus.PENDING.canMoveTo(OrderStatus.PAID)     // → true
OrderStatus.DELIVERED.canMoveTo(OrderStatus.PAID)   // → false
```

### Exhaustive switch — no `default`
- In a switch **expression** over an enum, leave `default` out. Add a value and every such switch fails to compile, giving you the list of places to update. A `default` would hide this.
- A switch **statement** (`switch (this) { case UPI -> fee = 0; ... }`) is NOT checked: a new value compiles and silently falls through.

```java
// add RETURNED("Returned") but don't update canMoveTo:
// error: the switch expression does not cover all possible input values

// switch STATEMENT version with a new WALLET value:
long fee = 0;
switch (this) { case UPI -> fee = 0; case CARD -> fee = amount * 2 / 100; case NETBANKING -> fee = 1000; }
// compiles; WALLET.computeFee(50000) → 0   (silent)
```

### Built-ins and parsing
- `values()` — array of all values, in declared order. `valueOf("PAID")` — exact, case-sensitive. `name()` — `"PAID"`. `toString()` — same as `name()` unless overridden. `ordinal()` — position from 0.
- Normalize input before `valueOf`.
- **Never persist `ordinal()`**: inserting/reordering values silently changes the meaning of stored numbers. Persist `name()`.
- vs TS: a TS union/enum is erased at compile time; a Java enum is a real class, checked at compile time *and* runtime.

```java
OrderStatus.values()           // → [PENDING, PAID, SHIPPED, DELIVERED, CANCELLED]
OrderStatus.valueOf("SHIPPED") // → SHIPPED
OrderStatus.valueOf("shipped") // → IllegalArgumentException: No enum constant OrderStatus.shipped
OrderStatus.PAID.name()        // → "PAID"   ← store this
OrderStatus.PAID.ordinal()     // → 1        ← never store this

public static OrderStatus parse(String raw) {
    return OrderStatus.valueOf(raw.strip().toUpperCase());
}
OrderStatus.parse(" shipped ")  // → SHIPPED
```

---

## Mistakes I keep making (check these first)

```java
private void restock(int qty)        // ❌ helper private, but other classes need it → public
if (amount < 0)                      // ❌ when 0 must be rejected too → amount <= 0
catch (Exception e)                  // ❌ too broad → catch (IllegalStateException e)
System.out.println(e);               // ❌ → System.out.println(e.getMessage());
javac Manager.java                   // ❌ from inside the package folder → javac -d out src/.../*.java
Notifier fake = new FakeNotifier();  // ❌ getters invisible → FakeNotifier fake = ...
switch (this) { case X -> fee = 0; } // ❌ statement, not checked → return switch (this) { ... };
"Hello, " + name                     // when predicting: copy the literal EXACTLY (commas, spaces, brackets)
```
