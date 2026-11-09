# Phase 4 — Modern Java — Notes

Revision notes for this phase. Every concept: **theory first**, **then a code snippet**, then an **example / gotcha** with real output (`// →`) or the exact error message. All outputs verified on JDK 25. See `notes.md` for the index of all phases.

Data used in the examples below (unless a section says otherwise):
```java
record Order(String id, String customer, long amountPaise, String status, List<String> items) {}

List<Order> orders = List.of(
        new Order("A1", "meera",  49900, "PAID",      List.of("pen", "book")),
        new Order("A2", "zoya",  150000, "CANCELLED", List.of("laptop")),
        new Order("A3", "meera", 275000, "PAID",      List.of("book", "bag")),
        new Order("A4", "arjun",   9900, "PAID",      List.of("pen")));
```

---

## 4.1 Lambdas & functional interfaces

### Functional interface
- **Functional interface** = an interface with **exactly one abstract method** (default/static methods don't count). `@FunctionalInterface` is optional; it just makes javac enforce the rule.
- A **lambda** is a short way to write an object that implements that one method.
- The **target type** (what the lambda is assigned to / passed as) decides *which* interface it implements. A lambda has no type of its own — unlike a JS arrow function, which is just a `Function` object.
- Phase 3 already used them: `Comparator<T>` is a functional interface (`compare`), so `(a, b) -> ...` was a lambda all along.

```java
@FunctionalInterface
interface DiscountRule {
    long apply(long amountPaise);          // the ONE abstract method
}

DiscountRule tenOff = amt -> amt * 90 / 100;   // lambda = an object implementing apply()
tenOff.apply(50000)                            // → 45000

List<String> words = new ArrayList<>(List.of("banana", "fig", "apple"));
words.sort((a, b) -> Integer.compare(a.length(), b.length()));   // a Comparator lambda
words                                                            // → [fig, apple, banana]
```
```java
@FunctionalInterface
interface TwoAbs { long apply(long a); long other(long b); }
// ❌ error: Unexpected @FunctionalInterface annotation
//      TwoAbs is not a functional interface

var f = (String s) -> s.length();
// ❌ error: cannot infer type for local variable f
//      (lambda expression needs an explicit target-type)      ← a lambda has no type of its own

Function<String, Integer> f = s -> s.length();   // ✅ the target type tells javac what it is
```

### Syntax
```java
(a, b) -> Integer.compare(a.length(), b.length())   // expression body, value is returned
s -> s.length()                                     // one param: parentheses optional
() -> "hello"                                       // no params
s -> { System.out.println(s); }                     // block body: use `return` yourself if a value is needed
(String s) -> s.length()                            // explicit parameter type (rarely needed)
```

### The four workhorses (`java.util.function`)
| Interface | Shape | Method | Use |
|---|---|---|---|
| `Function<T,R>` | T → R | `apply` | transform |
| `Predicate<T>` | T → boolean | `test` | yes/no check |
| `Supplier<T>` | () → T | `get` | produce / lazy value |
| `Consumer<T>` | T → void | `accept` | side effect |

```java
Function<String, Integer> len = s -> s.length();
Predicate<Order> isPaid = o -> o.status().equals("PAID");
Supplier<String> greeting = () -> "hello";
Consumer<String> log = s -> System.out.println("LOG " + s);

len.apply("pen")              // → 3
isPaid.test(orders.get(1))    // → false   (A2 is CANCELLED)
greeting.get()                // → hello
log.accept("order placed");   // → LOG order placed
```

- Also: `BiFunction<T,U,R>`, `UnaryOperator<T>` (= `Function<T,T>`), `BinaryOperator<T>`, `BiConsumer`, `BiPredicate`. Primitive versions (`IntPredicate`, `ToIntFunction`, …) avoid boxing.

```java
BiFunction<Long, Long, Long> add = (a, b) -> a + b;
UnaryOperator<String> upper = s -> s.toUpperCase();
BinaryOperator<Long> bigger = (a, b) -> Math.max(a, b);
IntPredicate isEven = n -> n % 2 == 0;            // int, no Integer boxing

add.apply(2L, 3L)        // → 5
upper.apply("paid")      // → PAID
bigger.apply(4L, 9L)     // → 9
isEven.test(7)           // → false
```

- `Predicate` can be combined with `and`, `or`, `negate`.

```java
Predicate<Order> isBig = o -> o.amountPaise() >= 100000;
orders.stream().filter(isPaid.and(isBig)).map(Order::id).toList()   // → [A3]
orders.stream().filter(isPaid.negate()).map(Order::id).toList()     // → [A2]
orders.stream().filter(isPaid.or(isBig)).map(Order::id).toList()    // → [A1, A2, A3, A4]
```

- Common library entry points: `list.removeIf(Predicate)`, `list.forEach(Consumer)`, `list.sort(Comparator)`, `map.computeIfAbsent(key, Function)`.

```java
List<String> tags = new ArrayList<>(List.of("ok", "", "urgent", " "));
tags.removeIf(t -> t.isBlank());            // Predicate
tags                                        // → [ok, urgent]
tags.forEach(t -> System.out.print("[" + t + "]"));   // Consumer → [ok][urgent]

Map<String, List<String>> byCustomer = new TreeMap<>();
for (Order o : orders) {
    byCustomer.computeIfAbsent(o.customer(), k -> new ArrayList<>()).add(o.id());   // Function
}
byCustomer                                  // → {arjun=[A4], meera=[A1, A3], zoya=[A2]}
```

### Capturing variables
- A lambda may **read** a local variable of the enclosing method only if it is **effectively final** (never reassigned after initialisation). Reassigning it — inside the lambda *or* anywhere else in the method — is a **compile error**.
- javac flags the **lambda's use** of the variable (the caret sits under the variable inside the lambda), not the reassignment line.
- Why: the lambda may run later / on another thread, and it gets a **copy** of the value, not the variable. Letting both sides mutate would silently diverge. (JS closures capture the *variable*, so `count++` inside a callback works there; in Java it doesn't.)
- Fields (`this.count`, static fields) are not captured locals — those can be mutated. Usually a smell; prefer a reduction (`sum`, `reduce`, collectors — 4.3/4.4).

```java
long total = 0;
orders.forEach(o -> total += o.amountPaise());
// ❌ error: local variables referenced from a lambda expression must be final or effectively final

int base = 10;
Function<Integer, Integer> g = x -> x + base;   // ❌ flagged HERE (caret under base)...
base = 20;                                      //    ...because of this later reassignment
```
```java
// ✅ fix 1 — no lambda for the mutation, then copy for any lambda that needs it
long total = 0;
for (Order o : orders) total += o.amountPaise();
long finalTotal = total;                         // effectively final (no `final` keyword needed)
Supplier<String> footer = () -> "TOTAL " + finalTotal;
footer.get()                                     // → TOTAL 484800

// ✅ fix 2 — let the stream do the adding (4.3)
long total2 = orders.stream().mapToLong(Order::amountPaise).sum();   // → 484800

// works, but a smell — mutating a static field from a lambda
static int counter = 0;
orders.forEach(o -> counter += 1);               // counter → 4
```

### Gotchas
- Wrong method name on the interface → `cannot find symbol`.
- Wrong arity / shape for the target type → `incompatible parameter types in lambda expression`.
- javac reports errors in waves: fixing the first batch can reveal more.

```java
Predicate<String> p = s -> s.isEmpty();
p.apply("x");             // ❌ error: cannot find symbol   symbol: method apply(String)   → p.test("x")

Supplier<String> s = x -> "a";
// ❌ error: incompatible types: incompatible parameter types in lambda expression
Supplier<String> s = () -> "a";   // ✅ Supplier takes no parameters
```

---

## 4.2 Method references

- A method reference is a shorter lambda for when the lambda **only calls one existing method**. `Class::method` / `obj::method` — no parentheses, no arguments.

| Kind | Form | Same as lambda |
|---|---|---|
| Static | `Integer::parseInt` | `s -> Integer.parseInt(s)` |
| Bound instance (specific object) | `prefix::concat`, `System.out::println` | `s -> prefix.concat(s)` |
| Unbound instance (any object of the type) | `String::length`, `String::compareTo` | `s -> s.length()`, `(a, b) -> a.compareTo(b)` |
| Constructor | `ArrayList::new`, `Item::new` | `() -> new ArrayList<>()`, `(n, p) -> new Item(n, p)` |

```java
record Item(String name, long pricePaise) {}

Function<String, Integer> parse = Integer::parseInt;      // static
String prefix = "INV-";
Function<String, String> withPrefix = prefix::concat;     // bound — always this `prefix` object
Function<String, Integer> length = String::length;        // unbound — the argument is the receiver
Comparator<String> natural = String::compareTo;           // unbound, 2 params: (a, b) -> a.compareTo(b)
Supplier<List<String>> fresh = ArrayList::new;            // constructor, no args
BiFunction<String, Long, Item> makeItem = Item::new;      // constructor, 2 args

parse.apply("42")                    // → 42
withPrefix.apply("7")                // → INV-7
length.apply("laptop")               // → 6
natural.compare("apple", "banana")   // → -1
fresh.get()                          // → []
makeItem.apply("pen", 1000L)         // → Item[name=pen, pricePaise=1000]

orders.stream().map(Order::id).forEach(System.out::print);   // → A1A2A3A4
```

- **Unbound rule:** the **first parameter becomes the receiver** (the object the method is called on), the rest become arguments. That's why `String::compareTo` fits `BiFunction<String,String,Integer>` / `Comparator<String>`.
- Which overload / constructor gets picked is decided by the **target type**, same as lambdas.
- `Comparator.comparing(Order::amountPaise)` — `comparing` takes a key-extractor `Function`; chain `.reversed()`, `.thenComparing(...)`. `comparingLong` avoids boxing.

```java
List<String> names = new ArrayList<>(List.of("zoya", "arjun", "meera"));
names.sort(String::compareTo);          // → [arjun, meera, zoya]

orders.stream().sorted(Comparator.comparing(Order::amountPaise)).map(Order::id).toList()
// → [A4, A1, A2, A3]   smallest first
orders.stream().sorted(Comparator.comparingLong(Order::amountPaise).reversed()).map(Order::id).toList()
// → [A3, A2, A1, A4]   biggest first
```

### Bound receiver is evaluated ONCE, when the reference is created (≠ lambda)
- The object before `::` is captured **at creation**. Reassigning the variable afterwards compiles (no effectively-final rule for the receiver) and the reference keeps using the **old** object.
- A `null` receiver throws **at creation** (`Objects.requireNonNull` inside), while a lambda only throws when it actually runs.

```java
String p = "X-";
Function<String, String> a = p::concat;   // receiver "X-" captured now
p = "Y-";                                 // compiles! no effectively-final rule for the receiver
a.apply("2");                             // → X-2   still the old object

String none = null;
Supplier<String> g = () -> none.concat("x");  // fine: nothing evaluated until g.get()
Function<String, String> f = none::concat;    // → NullPointerException right HERE, at creation
```
- Same trap with collections — a silent bug:

```java
List<String> list = new ArrayList<>();
Consumer<String> adder = list::add;       // bound to THIS list object
list = new ArrayList<>();                 // variable now points to a new list
adder.accept("a");                        // goes into the OLD list
list                                      // → []   the new one stays empty
```

### When to use which
- Use a method reference when it's *just* forwarding to a method: `System.out::println`, `Order::id`.
- Keep the lambda when there's any extra logic (`o -> o.amountPaise() / 100`), or when the reference would be less readable than the lambda.
- Wrong class → `invalid method reference ... cannot find symbol`.

```java
Function<String, Integer> p = String::parseInt;
// ❌ error: invalid method reference
//      cannot find symbol                       ← parseInt lives on Integer
Function<String, Integer> p = Integer::parseInt;  // ✅

.map(Order::id)                        // ✅ pure forwarding → method ref
.map(o -> o.amountPaise() / 100)       // ✅ extra logic → lambda
```

---

## 4.3 Streams: pipelines, laziness, `map`/`filter`/`reduce`/`flatMap`

### Shape of a pipeline
- **Source** → zero or more **intermediate ops** → one **terminal op**.
- A **stream** is not a data structure. It holds no data of its own, and it **never modifies the source**.
- **Intermediate ops** (`filter`, `map`, `flatMap`, `sorted`, `distinct`, `limit`, `peek`) only build the pipeline.
- **Terminal ops** (`toList`, `count`, `sum`, `reduce`, `forEach`, `findFirst`, `anyMatch`, `collect`) trigger execution.

```java
long total = orders.stream()                      // source (collection, array, Stream.of, IntStream.range…)
        .filter(o -> o.status().equals("PAID"))   // intermediate — lazy, returns a new Stream
        .mapToLong(Order::amountPaise)            // intermediate
        .sum();                                   // terminal — runs the whole pipeline
// total → 334800   (49900 + 275000 + 9900)
orders.size()                                     // → 4   source untouched
```

### Laziness
- No terminal op → **nothing runs at all** (a `peek`/`map` with side effects silently does nothing).
- Elements flow **one at a time through the whole pipeline** (A1 filter → A1 map → …), not stage by stage like JS array methods.
- **Short-circuiting** terminals (`findFirst`, `anyMatch`, `limit`) stop as soon as the answer is known — later elements are never touched.
- JS comparison: `arr.filter().map()` is eager and builds a full intermediate array per step. Java streams behave like a lazy iterator.

```java
Stream.of("a", "b").peek(s -> System.out.println("never printed " + s));
// → (nothing — no terminal op)
```
```java
Stream.of("a", "b")
        .filter(s -> { System.out.println("filter " + s); return true; })
        .map(s -> { System.out.println("map " + s); return s.toUpperCase(); })
        .toList();
// → filter a
// → map a        ← "a" goes all the way through first
// → filter b
// → map b
// result → [A, B]
```
```java
Stream.of("apple", "kiwi", "banana", "fig")
        .peek(s -> System.out.println("check " + s))
        .filter(s -> s.length() > 5)
        .findFirst();
// → check apple
// → check kiwi
// → check banana
// result → Optional[banana]     "fig" is never checked
```

### Single-use
- A stream can be consumed once. A second terminal op → `IllegalStateException` (run time). Need it twice → call `.stream()` twice (or collect to a list first).

```java
Stream<Order> once = orders.stream();
once.count();
once.count();
// → java.lang.IllegalStateException: stream has already been operated upon or closed

orders.stream().count();     // ✅ a fresh stream each time
orders.stream().count();
```

### Core ops
| Op | Shape | Example |
|---|---|---|
| `filter(Predicate)` | keep some | `.filter(o -> o.amountPaise() > 0)` |
| `map(Function)` | 1 → 1 | `.map(Order::id)` |
| `flatMap(Function<T, Stream<R>>)` | 1 → 0..n, flattened | `.flatMap(o -> o.items().stream())` |
| `reduce(identity, BinaryOperator)` | many → 1 | `.reduce(0, Integer::sum)` |
| `mapToLong`/`mapToInt` + `sum`/`average`/`max` | primitive stream, no boxing | `.mapToLong(Order::amountPaise).sum()` |

- `map(Order::items)` gives a `Stream<List<String>>` (lists as elements). Wanted the items → `flatMap`.

```java
orders.stream().map(Order::items).toList()
// → [[pen, book], [laptop], [book, bag], [pen]]      ❌ a list of lists
orders.stream().flatMap(o -> o.items().stream()).toList()
// → [pen, book, laptop, book, bag, pen]               ✅ flattened
```

- `reduce` identity must be neutral for the op (`0` for sum, `1` for product). A wrong identity is a silent bug.

```java
Stream.of(1, 2, 3).reduce(0, Integer::sum)            // → 6    ✅
Stream.of(1, 2, 3).reduce(1, Integer::sum)            // → 7    ❌ silently off by one
Stream.of(2, 3, 4).reduce(1, (x, y) -> x * y)         // → 24   ✅
Stream.of(2, 3, 4).reduce(0, (x, y) -> x * y)         // → 0    ❌
```

- `.toList()` (Java 16+) returns an **unmodifiable** list.
- A `Stream<Long>` has no `sum()` — map to a primitive stream first.

```java
List<String> ids = orders.stream().map(Order::id).toList();
ids.add("A9");           // → UnsupportedOperationException

List<Long> amounts = ...;
amounts.stream().sum();  // ❌ error: cannot find symbol   symbol: method sum()
amounts.stream().mapToLong(Long::longValue).sum();   // ✅
```

### Rule: no side effects in the pipeline
- Don't `add` to an outside list from `peek`/`map`/`forEach`. Build the result *with* the stream (`.map(...).toList()`, `sum`, collectors in 4.4).
- Side effects + laziness = code that silently doesn't run, and breaks under `parallel()`.

```java
List<String> paidIds = new ArrayList<>();
orders.stream().filter(isPaid).peek(o -> paidIds.add(o.id()));   // ❌ no terminal op
paidIds                                                          // → []   silently empty

List<String> paidIds = orders.stream().filter(isPaid).map(Order::id).toList();   // ✅
paidIds                                                                          // → [A1, A3, A4]
```

---

## Stream cheat sheet — most-used operations (with use cases)

All examples run against the `orders` data at the top of this file (outputs verified on JDK 25, 2026-11-02):
```
A1 meera  49900 PAID      [pen, book]
A2 zoya  150000 CANCELLED [laptop]
A3 meera 275000 PAID      [book, bag]
A4 arjun   9900 PAID      [pen]
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

> ⚠️ **4.4 exercises are still pending.** The examples below use the same mechanisms as the `OrderSummary` debug exercise — do Ex1 and Ex2 first, then read this section.

- `stream.collect(Collector)` — a **collector** is a recipe for building a result container (map, list, string) from the elements. `Collectors` holds the ready-made ones.
- Replaces the Phase 3 hand-written `computeIfAbsent` / `merge` loops. (Project 1 still requires writing those by hand once — know both.)
- `import static java.util.stream.Collectors.*;` lets you write `groupingBy(...)` without the `Collectors.` prefix (used below).

```java
// Phase 3 way
Map<String, List<String>> byHand = new TreeMap<>();
for (Order o : orders) {
    byHand.computeIfAbsent(o.customer(), k -> new ArrayList<>()).add(o.id());
}

// Phase 4 way — same result
Map<String, List<String>> byStream = orders.stream()
        .collect(groupingBy(Order::customer, TreeMap::new, mapping(Order::id, toList())));
// both → {arjun=[A4], meera=[A1, A3], zoya=[A2]}
```

### `groupingBy` — many groups, `Map<K, …>`
- 1-arg form = `List` of the whole elements per key. 2nd arg = **downstream collector**: what to do *inside* each group.
- Default map is a **`HashMap` → no key order**. Need sorted output → pass `TreeMap::new` as the middle argument (3-arg form).
- Groups only exist for keys that actually appeared — no empty groups.

```java
groupingBy(Order::customer)                                   // Map<String, List<Order>>
groupingBy(Order::customer, counting())                       // Map<String, Long>   ← Long, not Integer!
groupingBy(Order::status, summingLong(Order::amountPaise))    // Map<String, Long>
groupingBy(Order::customer, mapping(Order::id, toList()))     // Map<String, List<String>>
groupingBy(Order::customer, TreeMap::new, counting())         // sorted keys
```
```java
orders.stream().collect(groupingBy(Order::customer)).keySet()
// → [meera, zoya, arjun]          HashMap — no promised order

orders.stream().collect(groupingBy(Order::customer, TreeMap::new, counting()))
// → {arjun=1, meera=2, zoya=1}

orders.stream().collect(groupingBy(Order::status, TreeMap::new, summingLong(Order::amountPaise)))
// → {CANCELLED=150000, PAID=334800}
```

### Downstream collectors (used inside `groupingBy` / `partitioningBy`)
| Collector | Per group gives | Note |
|---|---|---|
| `counting()` | `Long` | assigning to `Map<K, Integer>` → compile error |
| `summingLong(f)` / `summingInt(f)` | sum | |
| `averagingLong(f)` | `Double` | |
| `mapping(f, toList())` | the mapped values | "ids per customer" instead of whole orders |
| `toSet()` | unique values | |
| `maxBy(Comparator)` / `minBy` | `Optional<T>` | a group is never empty here, but the type is still `Optional` |

```java
groupingBy(Order::customer, TreeMap::new, averagingLong(Order::amountPaise))
// → {arjun=9900.0, meera=162450.0, zoya=150000.0}

groupingBy(Order::customer, TreeMap::new, mapping(Order::status, toSet()))
// → {arjun=[PAID], meera=[PAID], zoya=[CANCELLED]}

groupingBy(Order::customer, TreeMap::new, maxBy(Comparator.comparingLong(Order::amountPaise)))
        .get("meera").map(Order::id)
// → Optional[A3]     meera's biggest order
```
```java
Map<String, Integer> m = xs.stream().collect(groupingBy(s -> s, counting()));
// ❌ error: incompatible types: inference variable D has incompatible equality constraints Integer,Long
Map<String, Long> m = ...;     // ✅ counting() always gives Long
```

### `partitioningBy` — exactly two groups
- **Always has both `false` and `true` keys**, even if one list is empty. `groupingBy(o -> bool)` only has the keys that occurred.

```java
partitioningBy(o -> o.amountPaise() >= 100000)               // Map<Boolean, List<Order>>
partitioningBy(pred, mapping(Order::id, toList()))           // with downstream
```
```java
orders.stream().collect(partitioningBy(o -> o.amountPaise() >= 100000, mapping(Order::id, toList())))
// → {false=[A1, A4], true=[A2, A3]}

orders.stream().collect(partitioningBy(o -> o.amountPaise() > 1_000_000, mapping(Order::id, toList())))
// → {false=[A1, A2, A3, A4], true=[]}       ← empty true group still present
orders.stream().collect(groupingBy(o -> o.amountPaise() > 1_000_000, mapping(Order::id, toList())))
// → {false=[A1, A2, A3, A4]}                ← no true key at all
```

### `toMap` — one key, one value
- Keys must be unique. Duplicate key without a merge function → **run time** `IllegalStateException`. Not caught by javac. Classic production bug: works in testing with unique data, crashes on real data.
- 3rd argument = **merge function** for duplicate keys. 4th = map type.
- A `null` value → `NullPointerException` (unlike `HashMap.put`).
- `toMap` vs `groupingBy`: unique key → `toMap`; many elements per key → `groupingBy` (or `toMap` with a merge fn when you're combining numbers).

```java
toMap(Order::id, Order::amountPaise)                         // keys must be unique!
toMap(Order::customer, Order::amountPaise, Long::sum)        // 3rd arg = merge function for duplicate keys
toMap(k, v, Long::sum, TreeMap::new)                         // + map type
```
```java
orders.stream().collect(toMap(Order::customer, Order::amountPaise));
// → java.lang.IllegalStateException: Duplicate key meera (attempted merging values 49900 and 275000)

orders.stream().collect(toMap(Order::customer, Order::amountPaise, Long::sum, TreeMap::new))
// → {arjun=9900, meera=324900, zoya=150000}     meera = 49900 + 275000

Stream.of("a").collect(toMap(s -> s, s -> (String) null));
// → NullPointerException
```

### Other
- `joining(", ")`, `joining("|", "<", ">")`.
- `toList()` on the stream (16+) is shorter than `collect(Collectors.toList())`; the stream method's list is unmodifiable.
- Generic inference errors (`inference variable D has incompatible equality constraints Integer,Long`) look scary — read the end: it lists the two types that disagree (`Integer` vs `Long`).

```java
orders.stream().map(Order::id).collect(joining(", "))               // → A1, A2, A3, A4
Stream.of("apple", "avocado", "banana").collect(joining("|", "<", ">"))   // → <apple|avocado|banana>
```

---

## Mistakes I keep making (check these first)

```java
predicate.apply(x)                       // ❌ Predicate's method is test → predicate.test(x)
Supplier<String> s = x -> "a";           // ❌ Supplier takes no params → () -> "a"
xs.forEach(x -> total += x);             // ❌ captured local mutated → for loop, or mapToLong(...).sum()
// "javac flags base = 20"               // ❌ it flags the LAMBDA's use of base (caret inside the lambda)
String::parseInt                         // ❌ wrong class → Integer::parseInt
Comparator.comparing(...)  // biggest first  // ❌ forgot .reversed()
list = new ArrayList<>();  // after list::add // ❌ bound ref still adds to the OLD list
stream.peek(o -> out.add(o))             // ❌ no terminal op → nothing runs → .map(...).toList()
map(Order::items)  // wanted the items   // ❌ list of lists → flatMap(o -> o.items().stream())
"check apple" vs "Checking apple"        // when predicting: copy literals EXACTLY, keep the brackets
```
