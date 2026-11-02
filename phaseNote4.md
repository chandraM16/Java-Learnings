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

---

## 4.2 Method references

A method reference is a shorter lambda for when the lambda **only calls one existing method**. `Class::method` / `obj::method` — no parentheses, no arguments.

| Kind | Form | Same as lambda |
|---|---|---|
| Static | `Integer::parseInt` | `s -> Integer.parseInt(s)` |
| Bound instance (specific object) | `prefix::concat`, `System.out::println` | `s -> prefix.concat(s)` |
| Unbound instance (any object of the type) | `String::length`, `String::compareTo` | `s -> s.length()`, `(a, b) -> a.compareTo(b)` |
| Constructor | `ArrayList::new`, `Order::new` | `() -> new ArrayList<>()`, `(id, amt) -> new Order(id, amt)` |

- **Unbound rule:** the **first parameter becomes the receiver** (the object the method is called on), the rest become arguments. That's why `String::compareTo` fits `BiFunction<String,String,Integer>` / `Comparator<String>`.
- Which overload / constructor gets picked is decided by the **target type**, same as lambdas.
- `Comparator.comparing(Order::amountPaise)` — `comparing` takes a key-extractor `Function`; chain `.reversed()`, `.thenComparing(...)`.

### Bound receiver is evaluated ONCE, when the reference is created (≠ lambda)
```java
String p = "X-";
Function<String,String> a = p::concat;   // receiver "X-" captured now
p = "Y-";                                 // compiles! no effectively-final rule for the receiver
a.apply("2");                             // "X-2" — still the old object

String none = null;
Supplier<String> g = () -> none.concat("x");  // fine: nothing evaluated until g.get()
Function<String,String> f = none::concat;      // NPE right HERE, at creation (Objects.requireNonNull)
```
- Same trap with collections: `Consumer<String> c = list::add; list = new ArrayList<>();` → `c` keeps adding to the **old** list; the new one stays empty. Silent bug.

### When to use which
- Use a method reference when it's *just* forwarding to a method: `System.out::println`, `Order::id`.
- Keep the lambda when there's any extra logic (`o -> o.amountPaise() / 100`), or when the reference would be less readable than the lambda.
- Wrong class → `invalid method reference ... cannot find symbol` (e.g. `String::parseInt` — `parseInt` lives on `Integer`).
