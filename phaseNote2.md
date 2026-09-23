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
