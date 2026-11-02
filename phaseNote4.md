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

---

## 4.3 Streams: pipelines, laziness, `map`/`filter`/`reduce`/`flatMap`

### Shape of a pipeline
```java
long total = orders.stream()                  // source (collection, array, Stream.of, IntStream.range…)
        .filter(o -> o.status().equals("PAID"))   // intermediate — lazy, returns a new Stream
        .mapToLong(Order::amountPaise)            // intermediate
        .sum();                                   // terminal — runs the whole pipeline, returns a result
```
- A **stream** is not a data structure. It holds no data of its own, and it **never modifies the source**.
- **Intermediate ops** (`filter`, `map`, `flatMap`, `sorted`, `distinct`, `limit`, `peek`) only build the pipeline.
- **Terminal ops** (`toList`, `count`, `sum`, `reduce`, `forEach`, `findFirst`, `anyMatch`, `collect`) trigger execution.

### Laziness
- No terminal op → **nothing runs at all** (a `peek`/`map` with side effects silently does nothing).
- Elements flow **one at a time through the whole pipeline** (A1 filter → A1 map → …), not stage by stage like JS array methods.
- **Short-circuiting** terminals (`findFirst`, `anyMatch`, `limit`) stop as soon as the answer is known — later elements are never touched.
- JS comparison: `arr.filter().map()` is eager and builds a full intermediate array per step. Java streams behave like a lazy iterator.

### Single-use
- A stream can be consumed once. Second terminal op → `IllegalStateException: stream has already been operated upon or closed` (run time). Need it twice → call `.stream()` twice (or collect to a list first).

### Core ops
| Op | Shape | Example |
|---|---|---|
| `filter(Predicate)` | keep some | `.filter(o -> o.amountPaise() > 0)` |
| `map(Function)` | 1 → 1 | `.map(Order::id)` |
| `flatMap(Function<T, Stream<R>>)` | 1 → 0..n, flattened | `.flatMap(o -> o.items().stream())` |
| `reduce(identity, BinaryOperator)` | many → 1 | `.reduce(0, Integer::sum)` |
| `mapToLong`/`mapToInt` + `sum`/`average`/`max` | primitive stream, no boxing | `.mapToLong(Order::amountPaise).sum()` |

- `map(Order::items)` gives a `Stream<List<String>>` (lists as elements). Wanted the items → `flatMap`.
- `reduce` identity must be neutral for the op (`0` for sum, `1` for product). A wrong identity is a silent bug.
- `.toList()` (Java 16+) returns an **unmodifiable** list → `add` throws `UnsupportedOperationException`.

### Rule: no side effects in the pipeline
Don't `add` to an outside list from `peek`/`map`/`forEach`. Build the result *with* the stream (`.map(...).toList()`, `sum`, collectors in 4.4). Side effects + laziness = code that silently doesn't run, and breaks under `parallel()`.

---

## Stream cheat sheet — most-used operations (with use cases)

All examples run against this data (outputs verified on JDK 25, 2026-11-02):
```java
record Order(String id, String customer, long amountPaise, String status, List<String> items) {}
// A1 meera  49900 PAID      [pen, book]
// A2 zoya  150000 CANCELLED [laptop]
// A3 meera 275000 PAID      [book, bag]
// A4 arjun   9900 PAID      [pen]
```

### Sources — where a stream comes from
| Call | Use case |
|---|---|
| `list.stream()` / `set.stream()` | The 95% case: process a collection. |
| `map.entrySet().stream()` | Process a map's key/value pairs (e.g. rank customers by count). |
| `Stream.of(a, b, c)` | Few fixed values, quick tests. |
| `Arrays.stream(arr)` | Arrays (`int[]` gives an `IntStream`). `Arrays.stream(new int[]{3, 4}).sum()` → `7` |
| `IntStream.range(0, n)` / `rangeClosed(1, n)` | Index-based loops without `for (int i…)`. `rangeClosed(1, 3).boxed().toList()` → `[1, 2, 3]` |

### Intermediate — lazy, return a new stream
| Op | What it does | Typical backend use case | Example → result |
|---|---|---|---|
| `filter(Predicate)` | keep only matching elements | only PAID orders, only active users, drop blank lines | `.filter(o -> o.status().equals("PAID")).count()` → `3` |
| `map(Function)` | transform each element 1 → 1 | entity → DTO, order → id, trim/normalise strings | `.map(Order::id)` → `A1, A2, …` |
| `mapToInt` / `mapToLong` / `mapToDouble` | map to a primitive stream (no boxing) | anything you'll `sum`/`average`/`max` next | `.mapToLong(Order::amountPaise).sum()` → `484800` |
| `flatMap(Function<T, Stream<R>>)` | 1 → many, flattened | all line items across orders, all roles across users, all words across lines | `.flatMap(o -> o.items().stream()).distinct()` → `[pen, book, laptop, bag]` |
| `distinct()` | remove duplicates (uses `equals`/`hashCode`) | unique customers, unique tags | `.map(Order::customer).distinct()` → `[meera, zoya, arjun]` |
| `sorted()` | natural order (`Comparable`) | alphabetical names | `.map(Order::customer).distinct().sorted()` → `[arjun, meera, zoya]` |
| `sorted(Comparator)` | custom order | biggest orders first, newest first | `.sorted(Comparator.comparingLong(Order::amountPaise).reversed())` |
| `limit(n)` | first n only (short-circuits) | top-N report, preview of results | `…reversed()).limit(2).map(Order::id)` → `[A3, A2]` |
| `skip(n)` | drop first n | offset-style pagination, skip a CSV header | `.skip(2).map(Order::id)` → `[A3, A4]` |
| `takeWhile(Predicate)` | take while true, stop at first false | read a sorted log until a cutoff time | `Stream.of(5, 8, 12, 3, 20).takeWhile(n -> n < 10)` → `[5, 8]` |
| `dropWhile(Predicate)` | drop while true, keep the rest | skip leading comment lines | same data `.dropWhile(n -> n < 10)` → `[12, 3, 20]` |
| `boxed()` | `IntStream` → `Stream<Integer>` | when you need `toList()` / collectors on a primitive stream | see `rangeClosed` above |
| `peek(Consumer)` | look at elements as they pass | **debugging only** — never for real side effects (may not run) | `.peek(o -> System.out.println(o))` |

### Terminal — run the pipeline, produce a result
| Op | Returns | Typical use case | Example → result |
|---|---|---|---|
| `toList()` | unmodifiable `List` | the default way to get a list back | `.map(Order::id).toList()` |
| `count()` | `long` | how many match | `.filter(isPaid).count()` → `3` |
| `sum()` / `average()` / `max()` / `min()` (primitive streams) | number / `OptionalDouble` / `OptionalLong` | totals, averages, peaks | `.average()` → `OptionalDouble[121200.0]` (empty stream has no average → Optional) |
| `max(Comparator)` / `min(Comparator)` | `Optional<T>` | the biggest *object*, not just the number | `.max(Comparator.comparingLong(Order::amountPaise)).map(Order::id)` → `Optional[A3]` |
| `findFirst()` | `Optional<T>` | first match, e.g. first failed payment (short-circuits) | `.filter(o -> o.amountPaise() > 100000).findFirst().map(Order::id).orElse("none")` → `A2` |
| `anyMatch(Predicate)` | `boolean` | "is there any cancelled order?" — validation | `.anyMatch(o -> o.status().equals("CANCELLED"))` → `true` |
| `allMatch(Predicate)` | `boolean` | "are all amounts positive?" — invariants | `.allMatch(o -> o.amountPaise() > 0)` → `true` |
| `noneMatch(Predicate)` | `boolean` | "no blank customer names?" | `.noneMatch(o -> o.customer().isBlank())` → `true` |
| `reduce(identity, BinaryOperator)` | `T` | custom fold when there's no ready-made op | `.map(Order::amountPaise).reduce(0L, Long::sum)` → `484800` |
| `forEach(Consumer)` | `void` | final side effect: print, send, log | `.forEach(System.out::println)` |
| `toArray(String[]::new)` | array | APIs that still need arrays | `.map(Order::id).toArray(String[]::new)` |
| `collect(Collector)` | anything | grouping, maps, joining — **4.4** | `.collect(Collectors.joining(", ", "[", "]"))` → `[A1, A2, A3, A4]`; `groupingBy(Order::customer, counting())` → `{meera=2, zoya=1, arjun=1}` |

### Picking the right one — quick rules
- Want **some** elements → `filter`. Want **changed** elements → `map`. Want **inner** elements of a collection field → `flatMap`.
- Going to add numbers → `mapToLong`/`mapToInt` first, then `sum`/`average`. `Stream<Long>` has no `sum()`.
- Want a yes/no → `anyMatch`/`allMatch`/`noneMatch`, not `filter(...).count() > 0` (the match ops stop early).
- Want the biggest *object* → `max(Comparator)`; want just the biggest *number* → `mapToLong(...).max()`.
- Anything that may find nothing (`findFirst`, `max`, `min`, `average`) returns an **`Optional`** — handled properly in 4.5.
- Want a `Map` back → `collect(Collectors.groupingBy/toMap/…)` — 4.4.
- `distinct()` only works as expected if the element type has proper `equals`/`hashCode` (Phase 2.3). Records get them automatically.

---

## 4.4 Collectors: `groupingBy`, `partitioningBy`, `toMap`, downstream collectors

- `stream.collect(Collector)` — a **collector** is a recipe for building a result container (map, list, string) from the elements. `Collectors` holds the ready-made ones.
- Replaces the Phase 3 hand-written `computeIfAbsent` / `merge` loops. (Project 1 still requires writing those by hand once — know both.)

### `groupingBy` — many groups, `Map<K, …>`
```java
groupingBy(Order::customer)                                   // Map<String, List<Order>>
groupingBy(Order::customer, counting())                       // Map<String, Long>   ← Long, not Integer!
groupingBy(Order::city, summingLong(Order::amountPaise))      // Map<String, Long>
groupingBy(Order::customer, mapping(Order::id, toList()))     // Map<String, List<String>>
groupingBy(Order::customer, TreeMap::new, counting())         // sorted keys
```
- 1-arg form = `List` of the whole elements per key. 2nd arg = **downstream collector**: what to do *inside* each group.
- Default map is a **`HashMap` → no key order**. Need sorted output → pass `TreeMap::new` as the middle argument (3-arg form).
- Groups only exist for keys that actually appeared — no empty groups.

### Downstream collectors (used inside `groupingBy` / `partitioningBy`)
| Collector | Per group gives | Note |
|---|---|---|
| `counting()` | `Long` | assigning to `Map<K, Integer>` → compile error (`incompatible equality constraints Integer,Long`) |
| `summingLong(f)` / `summingInt(f)` | sum | |
| `averagingLong(f)` | `Double` | |
| `mapping(f, toList())` | the mapped values | "ids per customer" instead of whole orders |
| `toSet()` | unique values | |
| `maxBy(Comparator)` / `minBy` | `Optional<T>` | group may be... never empty here, but the type is still `Optional` |

### `partitioningBy` — exactly two groups
```java
partitioningBy(o -> o.amountPaise() >= 100000)               // Map<Boolean, List<Order>>
partitioningBy(pred, mapping(Order::id, toList()))           // with downstream
```
- **Always has both `false` and `true` keys**, even if one list is empty. `groupingBy(w -> bool)` only has the keys that occurred.

### `toMap` — one key, one value
```java
toMap(Order::id, Order::amountPaise)                         // keys must be unique!
toMap(Order::customer, Order::amountPaise, Long::sum)        // 3rd arg = merge function for duplicate keys
toMap(k, v, Long::sum, TreeMap::new)                         // + map type
```
- Duplicate key without a merge function → **run time** `IllegalStateException: Duplicate key meera (attempted merging values 49900 and 275000)`. Not caught by javac. Classic production bug: works in testing with unique data, crashes on real data.
- A `null` value → `NullPointerException` (unlike `HashMap.put`).
- `toMap` vs `groupingBy`: unique key → `toMap`; many elements per key → `groupingBy` (or `toMap` with a merge fn when you're combining numbers).

### Other
- `joining(", ")`, `joining("|", "<", ">")` → `"<apple|avocado|banana>"`.
- `toList()` on the stream (16+) is shorter than `collect(Collectors.toList())`; the stream method's list is unmodifiable.
- Generic inference errors (`inference variable V has incompatible equality constraints Integer,D,Long`) look scary — read the last line: it lists the two types that disagree (`Integer` vs `Long`).
- `import static java.util.stream.Collectors.*;` lets you write `groupingBy(...)` without the `Collectors.` prefix.
