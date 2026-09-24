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
