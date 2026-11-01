# Phase 4 — Modern Java (revision notes)

Concise rules and gotchas only. Language: Hinglish prose, English code/terms.

---

## 4.1 Lambdas & functional interfaces

### Functional interface
- **Functional interface** = interface with **exactly one abstract method** (default/static methods don't count). `@FunctionalInterface` is optional; it just makes javac enforce the rule.
- A **lambda** is a short way to write an object that implements that one method. The target type (what it is assigned to / passed as) decides *which* interface it implements — a lambda has no type of its own (unlike a JS arrow function, which is just a `Function` object).
- Phase 3 already used them: `Comparator<T>` is a functional interface (`compare`), so `(a, b) -> ...` was a lambda all along.

### Syntax
```java
(a, b) -> Integer.compare(a.length(), b.length())   // expression body, value is returned
s -> s.length()                                     // one param: parentheses optional
() -> "hello"                                       // no params
s -> { System.out.println(s); }                     // block body: use `return` yourself if a value is needed
```

### The four workhorses (`java.util.function`)
| Interface | Shape | Method | Use |
|---|---|---|---|
| `Function<T,R>` | T → R | `apply` | transform |
| `Predicate<T>` | T → boolean | `test` | yes/no check |
| `Supplier<T>` | () → T | `get` | produce / lazy value |
| `Consumer<T>` | T → void | `accept` | side effect |

Also: `BiFunction<T,U,R>`, `UnaryOperator<T>` (= `Function<T,T>`), `BinaryOperator<T>`, `BiConsumer`, `BiPredicate`. Primitive versions (`IntPredicate`, `ToIntFunction`, …) avoid boxing.

Common library entry points: `list.removeIf(Predicate)`, `list.forEach(Consumer)`, `list.sort(Comparator)`, `map.computeIfAbsent(key, Function)`.

### Capturing variables
- A lambda may **read** a local variable of the enclosing method only if it is **effectively final** (never reassigned after initialisation). Reassigning it — inside the lambda *or* anywhere else in the method — is a **compile error**: `local variables referenced from a lambda expression must be final or effectively final`.
- Why: the lambda may run later / on another thread, and it gets a **copy** of the value, not the variable. Letting both sides mutate would silently diverge. (JS closures capture the *variable*, so `count++` inside a callback works there; in Java it doesn't.)
- Fields (`this.count`, static fields) are not captured locals — those can be mutated. Usually a smell; prefer a reduction (`sum`, `reduce`, collectors — 4.3).

### Gotchas
- Wrong method name on the interface (`predicate.apply(x)` instead of `.test(x)`) → `cannot find symbol`.
- Wrong arity / shape for the target type (`Supplier` given a parameter) → `incompatible parameter types in lambda expression`.
- javac reports errors in waves: fixing the first batch can reveal more.
