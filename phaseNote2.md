# Phase 2 — Object-Oriented Java — Notes

Concise revision notes for this phase only. Updated as topics are learned. See `notes.md` for the index of all phases.

---

## Classes, objects, fields, constructors, `this`, object lifecycle

- **Class** = compile-time blueprint. **Object/instance** = a `new`-created run-time thing on the **heap**, with its own copy of every field. Two objects from the same class never share field state.
- **Field** = a variable declared in a class holding part of an object's state. Uninitialized fields get **default values** the instant the object is allocated (`0`/`false`/`null` — same defaults as array elements), *before* the constructor body runs.
- **Constructor**: same name as the class, no return type, runs on `new`. Job: get every field into a valid state.
  - **Overloading**: multiple constructors, different parameter lists — compiler picks by call-site arguments, same rule as method overloading.
  - **Chaining**: `this(...)` as the constructor's first statement calls another overload on the same class, to avoid duplicating init logic. Only one such call, only as the first line.
  - Adding **any** constructor removes the free no-arg constructor Java would otherwise give you.
- **`this`** — reference to the current object inside an instance method/constructor. Unlike JS, always bound to the instance the method was called on — no losing `this` in callbacks, no `.bind()`.
  - Needed when a parameter name **shadows** a field: `this.name = name;` (field = parameter). Without `this.`, `name = name;` just reassigns the parameter to itself — the field silently stays at its default. No error, no crash.
  - A **new local variable** with the same name as a field also shadows it silently (e.g. `double balance = this.balance + amount;` inside a method — creates a local, never touches the field).
- **Object lifecycle**: `new` → allocate on heap → fields set to defaults → constructor body runs → reference returned → object stays alive while reachable → eligible for **GC** once nothing references it. No manual `delete`/`free`, no deterministic destructor moment (deep GC mechanics: Phase 8).

### JS/TS comparison
| | Java | JS |
|---|---|---|
| Fields | Fixed set, declared with types at compile time | Dynamic — add any property, any time |
| Constructors | Can overload freely | Exactly one `constructor()`; simulate variants with default params/factories |
| `this` | Always the instance the method was called on | Depends on call site — can be lost, needs binding |

## Encapsulation, access modifiers, invariants, immutability

- **Encapsulation** = bundle data with the operations that keep it valid, hide the data (`private` fields) so nothing outside those operations can touch it. The class is the only gatekeeper of its own state.
- **Invariant** = a fact about an object that must always hold (`balance >= 0`, `memberId` never blank). Constructor establishes it; every mutating method must re-check before allowing the change. Private fields + validated construction + validated mutation = the invariant literally can't break from outside.
- **Access modifiers**, most → least restrictive: `private` (class only) → *(none, package-private)* (same package) → `protected` (package + subclasses, real meaning: inheritance, 2.5) → `public` (everyone). Enforced by the **JVM at runtime** — unlike TS `private`, which is compile-time only and erasable with a cast. Matches real JS `#field` more than TS `private`.
- **Getters/setters are not a language feature** — just regular methods. `get`/`set`/`is` naming is a convention Spring/Jackson/JavaBeans recognize by reflection, not syntax Java itself understands (no `get`/`set` keyword like JS).
- **Throwing to reject bad input/state**: `throw new IllegalArgumentException("...")` for a bad *argument*, `throw new IllegalStateException("...")` for a call that's valid in general but not given the object's *current* state (e.g. redeeming more points than available). One-line refusal; formal `try`/`catch`/custom exceptions = Phase 5.
- **`final` on a field** locks only the field's own reference/value, not the object it points to: `final int[] arr` can't be reassigned, but `arr[0] = 5` still works. Not the same as immutability.

### The defensive-copy trap (two leaks, not one)
A `private` field can still be corrupted from outside via a **shared array reference**, through *two separate* entry points:
1. **Constructor leak**: `this.balances = balances;` doesn't copy — the field and the caller's variable are now the same array object.
2. **Getter leak**: `return balances;` doesn't copy either — the caller gets a live reference to internal state, not a read-only view.
Both are the same "assignment copies the reference, not the object" rule from Phase 1 arrays — just reached through a constructor param and a return value instead of a plain `int[] b = a;`. Real fix: `balances.clone()` (or `Arrays.copyOf`) on the way in **and** the way out.

### Common mistake, recurring
- Methods default to `private` when they actually need to be called from outside the class. Compiles fine and looks correct **only** because `main`/the caller happens to live in the same class — breaks the moment another class needs to use the object. Seen twice already (L2.1 `Product`, L2.2 `MembershipCard`) — watch for a third.

## `equals`, `hashCode`, `toString`

- Every class silently extends `Object` (full inheritance mechanics: 2.5), so every object already has `equals()`, `hashCode()`, `toString()` before you write anything.
  - Default `equals()` = identity (`this == o`). Default `hashCode()` = identity-based (different for essentially every object). Default `toString()` = `ClassName@hexHashCode` — not useful.
- **`hashCode()` in plain terms**: an `int` "bucket/locker number" for an object, used by `HashMap`/`HashSet` (Phase 3) to jump straight to the right spot instead of checking every element. The **contract**: if `a.equals(b)` is `true`, `a.hashCode() == b.hashCode()` MUST also be true — otherwise a hash-based lookup can search the wrong bucket and fail to find an object that's actually there. Nothing crashes; the object is just unreachable from where Java looked.
- **`@Override`** — first annotation seen. Tells the compiler "this must match a method on the superclass" — typo the signature and you get a compile error instead of a silently-created unrelated method. Always use it on `equals`/`hashCode`/`toString` overrides.
- **`getClass()`** — returns an object's exact runtime class. Used in `equals()` as `getClass() != o.getClass()` to bail out (`return false`) before casting, so comparing against an unrelated type never throws `ClassCastException`.
- **`Objects.equals(a, b)`** — null-safe equality check (`Objects.equals(null, "x")` → `false`, no NPE). Use instead of `field.equals(other.field)` for any field that could be `null`.
- **`Objects.hash(a, b, ...)`** — combines several fields into one `hashCode()` value; standard way to implement it without hand-rolling.
- **Standard `equals()` shape**: `this == o` shortcut → `o == null || getClass() != o.getClass()` → cast → compare fields (`Objects.equals(...)` for any nullable ones).
- **Mutable fields + `hashCode()` = footgun**: if a hash-based collection computes an object's bucket from a field, then that field changes, the object becomes unreachable at its old bucket — "in the set" but `contains()` returns `false`. Reason `final` fields matter here too (ties back to 2.2 immutability).
- Same string-concatenation rule from Phase 1 (`+` converts the other operand to text if either side is a String) is what makes `println(obj)`/`"" + obj` call `toString()` automatically — no new mechanism, same rule landing on custom objects now.

## Static members, `final` classes/methods, packages revisited

- **Instance field** = one copy per object (2.1). **Static field** = one copy total, shared by every object of the class, accessed via `ClassName.field` (or unqualified from inside the class). Same idea for methods: a `static` method has no `this` and can't touch instance fields/methods directly — "non-static variable X cannot be referenced from a static context" is a **compile-time** error, not a runtime one.
- `main` is `static` because the JVM calls it before any object exists — there's nothing to be an instance method *of* yet.
- `static final` = the Phase 1 constant pattern (`PAISE_PER_RUPEE`), now understood: `static` = one shared copy, `final` = never reassigned.
- **Subtle failure mode**: making a field that should be `static` into a regular instance field instead doesn't always fail to compile. A constructor (which always has `this`) can still read/write it fine — so each object silently gets its **own fresh copy** instead of sharing one. A shared incrementing counter turned instance-field would compile clean and just produce the same starting value for every object, forever — no error anywhere. Only code that's *itself* `static` (like a static utility method) and tries to touch that field will fail to compile.
- `final class` — blocks all subclassing (`String` itself is `final`). `final` method — blocks overriding just that one method in a subclass that's otherwise still extendable. Full override mechanics: 2.5.
- `static final` array/collection field: `final` still only locks the reference, not the contents — `static final int[] IDS = {1,2,3};` can't be reassigned, but `IDS[0] = 99` still works (same rule as 2.1/2.2, just on a static field this time).
- Packages/imports: no new mechanics beyond Phase 1 — the useful new context is that classes in the **same package** can see each other's package-private members without an `import`; classes in **different** packages always need one, even within the same project.

## Multiple classes per file & packages (revisit, 2026-09-26)
- A `.java` file = a **compilation unit**: `package` line (≤1) → `import` lines → one or more **top-level classes**.
- **At most one `public` top-level class per file**, and its name must equal the file name. A second public class → `error: class Three is public, should be declared in a file named Three.java`.
- Extra non-public classes in the same file are **package-private helpers**. Each still compiles to its **own** `.class` file (`OrderApp.class`, `OrderLabel.class`) — the JVM has no concept of "several classes in one file".
- When to put a helper in the same file: tiny, only used by that class, not worth its own file. Otherwise one class per file (what teams expect; IDE/Git friendly).
- **Same package → no import.** Different package → `import`, or write the FQN inline (`java.time.LocalDate today = java.time.LocalDate.now();`).
- Package-private class used from another package → `error: TaxTable is not public in com.shop.billing; cannot be accessed from outside package`. Missing import → `error: cannot find symbol / symbol: class Invoice`.
- **Two classes with the same simple name** (`java.util.List` vs `java.awt.List`): import one, use the FQN for the other. There's no `import X as Y` in Java.
- **Static import** brings in a *member*, not a type: `import static java.lang.Math.max;` → `max(3, 9)`. Use sparingly (hides where the method came from); common in tests (`assertEquals`).
- `import` never controls compilation — `javac` finds classes via the **classpath / source paths**, not via imports.
- Compile a whole tree at once: `javac -d out $(find src -name '*.java')`. Output mirrors the package folders under `out/`.

## Packages — plain-language recap (revisited 2026-09-26)

**A package is a named group of classes. The name is the folder path, written with dots.**
- Every class in the same folder gets the **same** `package` line — that line says "I belong to this group".
- The package name becomes part of the class's real name: `com.chandan.payment.Money` (its FQN).
- Folders must mirror the package for IDEs/Maven and for javac's `-sourcepath` lookups.
  - Nuance: `javac -d out OneFile.java` alone tolerates a mismatch (it writes output by package), but as soon as another class has to be found by name, the mismatch fails with `cannot find symbol`. Always mirror.

```
src/com/chandan/payment/Money.java             package com.chandan.payment;
src/com/chandan/payment/CardValidator.java     package com.chandan.payment;
src/com/chandan/payment/PaymentProcessor.java  package com.chandan.payment;
src/com/chandan/orders/Checkout.java           package com.chandan.orders;
```

**Using another class:**
| Where it lives | What you need |
|---|---|
| Same package | Nothing. Just use the name. |
| Different package | `import com.chandan.payment.Money;` **and** the class must be `public` |
| `java.lang` (String, Math…) | Nothing, imported automatically |

Real errors from the demo:
- Missing import → `error: cannot find symbol  symbol: class Money  location: class Checkout`
- Class not public, used from another package → `error: CardValidator is not public in com.chandan.payment; cannot be accessed from outside package`

**Package boundary = a visibility boundary.** A class (or member) with no modifier is *package-private*: usable inside its own package, invisible outside. That's how you keep helpers (`CardValidator`) internal while exposing an API (`PaymentProcessor`, `Money`). This is the first real use of the 4th access level from 2.2.

**Same simple name, different packages** is fine: `com.chandan.payment.Status` and `com.chandan.orders.Status` are different classes. Import one and use the FQN for the other.

**Compile & run with packages:**
```bash
javac -d out $(find src -name '*.java')
java -cp out com.chandan.orders.Checkout
```

## Inheritance, overriding, `super`, polymorphism, dynamic dispatch

- `class Manager extends Employee` — `Manager` gets every field/method `Employee` has, for free, then can add/override.
- `protected` (2.2's least-used access level) is for exactly this: fields/methods a subclass needs, but the outside world shouldn't touch directly.
- `super(...)` — calls the superclass constructor; same first-line rule as `this(...)` chaining from 2.1, **with one modern exception** (below). `super.method()` — calls the superclass's version of a method from inside an override, used to *extend* behavior instead of fully replacing it.
- **Polymorphism / dynamic dispatch, the actual mechanism**: two separate questions, answered two different ways —
  - *What can you call?* → decided by the variable's **declared type**, checked at **compile time**. `Employee e2 = new Manager(...); e2.getTeamSize();` fails to compile — `getTeamSize()` isn't on `Employee` — even though `e2` really holds a `Manager`. The error names the declared type explicitly: `location: variable e2 of type Employee`.
  - *Which version actually runs?* → decided by the object's **real runtime type**. `e2.calculateBonus()` compiles (declared type `Employee` has it) and runs `Manager`'s override — even inside code written entirely in `Employee` itself (e.g. `Employee.toString()` calling `calculateBonus()` internally still dispatches to `Manager`'s version when the real object is a `Manager`). Overriding isn't "replace the method in this file" — it's "the JVM looks up the real object's class at the call site, every time."
  - To call a subclass-only method through a superclass-typed variable, cast: `((Manager) e2).getTeamSize();` — same cast syntax as Phase 1's numeric casts, now on your own class hierarchy. Risks `ClassCastException` if the cast is wrong (same idea as `getClass()` checks in 2.3's `equals()`).
- **Correction to a rule taught earlier this phase**: `super(...)`/`this(...)` no longer has to be *literally* the first statement. JDK 25 finalized **Flexible Constructor Bodies**: statements before `super(...)`/`this(...)` are legal as long as they don't touch `this`/instance state yet — pure argument validation (throwing on a bad constructor argument *before* delegating) is the sanctioned use case, and it behaves correctly (the validation runs and can throw before the superclass is ever constructed). Older Java (and most existing teaching material/StackOverflow answers) will say this is illegal — it was, before this feature finalized.
- Multi-class compile reminder (2.1-era, resurfaced): `javac` only compiles the file(s) you give it plus anything on its sourcepath — pointing it at `Manager.java` alone when `Manager extends Employee` in a separate file fails with `cannot find symbol: class Employee`, even with both files sitting in the same folder. Compile both together: `javac -d out src/.../*.java`.

## Abstract classes vs interfaces

- **Abstract class**: can't be instantiated (`new Employee(...)` → `error: Employee is abstract; cannot be instantiated`, compile time). Can hold real instance state and constructors. Constructor is usually `protected` (only subclasses can call it anyway).
- **Abstract method**: signature only, ends in `;`, no body. A class with any abstract method must be `abstract`. Every concrete subclass must implement every inherited abstract method, or javac names exactly which one is missing.
- **Interface**: a contract — "implementers can do these things." No constructors, no instance state.
  - Abstract method: every implementer must define it.
  - `default` method (Java 8+): has a body, inherited for free, can be overridden. Added so library interfaces (e.g. `List`) could gain methods without breaking every existing implementer.
  - `static` method: belongs to the interface, called as `Payable.minimumWage()`.
- **Deciding factor**: `extends` only one class; `implements` as many interfaces as you like. Shared real state/code across a tight family → abstract class. A capability many unrelated classes might have → interface.
- Dispatch works through default methods too: a `default` method calling an abstract method runs the real object's implementation, same mechanism as 2.5. The *method* is shared; the *data* it reads (`this`'s fields) is per object.

## Composition vs inheritance

- **Inheritance = is-a** (`Manager` is an `Employee`). **Composition = has-a**: keep another object in a field and forward calls to it (**delegation**).
- Default to composition. Inherit only for a true is-a where you control both classes (or the parent is designed for extension).
- Standard shape — **constructor injection** (what Spring automates in Phase 11):
```java
public class OrderService {
    private final Notifier notifier;                 // interface type, private final
    public OrderService(Notifier notifier) { this.notifier = notifier; }
    public void placeOrder(...) { notifier.send(...); }
}
new OrderService(new EmailNotifier());   // swap implementations without touching OrderService
```
- Don't `new` the dependency inside the class (`= new EmailNotifier()`) — hard-wired, can't swap, can't test.
- **Fragile base class**: inside a parent, `add(n)` is really `this.add(n)`, and `this` is the real object — so the parent's own code can call your override. `LoggingCounter extends Counter` overriding both `add` and `addAll` double-counts (7 instead of 4). `super.add(n)` is the one call that skips dispatch.
  - Composition fix: `LoggingCounter` holds `private final Counter counter` and calls `counter.addAll(...)`. Inside, `this` is the `Counter`, so nothing dispatches back → 4.
- **Test double (fake)**: an implementation of the interface that only *records* what it received — no printing, no validation. Declare it as its own type in the test (`FakeNotifier fake = new FakeNotifier();`) so you can read its getters afterwards, while `OrderService` still only sees `Notifier`. Same idea as a Jest `jest.fn()` mock checked with `toHaveBeenCalledWith`.

## Enums

- An enum is a fixed set of values; each value is a **single object**, created once. What javac generates (`javap -p`): `public final class OrderStatus extends java.lang.Enum<OrderStatus>`, one `public static final OrderStatus PAID;` per value, a **private** constructor, plus `values()`/`valueOf(String)`.
- Because there's exactly one `PAID` object, **`==` is correct** for enums (the exception to Phase 1's "use `.equals()` for objects").
- Fields/constructor/methods: `PAID("Paid")` *is* a constructor call. Values come first, then `;`, then fields and methods.
- Behavior in the enum: `canMoveTo(next)` with `switch (this)` — `this` is the value the method was called on. Inside the enum write `case PAID`, not `case OrderStatus.PAID`.
- **Exhaustive switch, no `default`**: add a value and every switch expression over the enum fails to compile — `error: the switch expression does not cover all possible input values`. A `default` would hide this.
- Built-ins: `values()` (array, declared order), `valueOf("PAID")` (exact, case-sensitive — `IllegalArgumentException: No enum constant OrderStatus.paid`), `name()`, `toString()` (= name unless overridden), `ordinal()` (position from 0).
- Normalize input before `valueOf`: `valueOf(raw.strip().toUpperCase())`.
- **Never persist `ordinal()`** — inserting/reordering values silently changes the meaning of stored numbers. Persist `name()`.
- vs TS: a TS union/enum is erased at compile time; a Java enum is a real class, checked at compile time *and* runtime.

### Nested folders = separate packages (2026-09-26)
`com/chandan/orders/f1/Cart.java` **must** declare `package com.chandan.orders.f1;` — one folder, one package. There is no "package containing sub-packages" in Java's access rules:
- `com.chandan.orders` and `com.chandan.orders.f1` are two **unrelated** packages that happen to share a name prefix.
- A class in `…orders.f1` needs an `import` to use a `public` class in `…orders`, and **cannot** see its package-private members: `error: OrderHelper is not public in com.chandan.orders; cannot be accessed from outside package`.
- Sibling packages (`…orders.f1` ↔ `…orders.f2`) likewise need imports + `public`.
- Declaring a mismatched package (file in `f1/`, says `package com.chandan.orders;`) often still compiles when every file is passed to javac explicitly, but: IntelliJ flags it, `-sourcepath` lookups fail (`cannot find symbol`), and two same-named classes in different folders collide → `error: duplicate class: com.chandan.orders.Report`.
- Package depth is only naming. Name sub-packages by feature/role (`orders.pricing`, `orders.shipping`), never `f1`/`f2`/`util`-dumping-grounds.
