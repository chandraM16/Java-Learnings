# Phase 3 — Collections & Generics — Notes

Revision notes for this phase. Every concept: **theory first**, **then a code snippet**, then an **example / gotcha** with real output (`// →`) or the exact error message. All outputs verified on JDK 25. See `notes.md` for the index of all phases.

---

## Collections framework map

```
Collection (interface)
├── List   — ordered, duplicates allowed, access by index   → ArrayList, LinkedList
├── Set    — no duplicates                                   → HashSet, LinkedHashSet, TreeSet
└── Queue  — add at one end, take from the other            → ArrayDeque, PriorityQueue
Map (separate family) — key → value                          → HashMap, LinkedHashMap, TreeMap
```
- JS rough equivalents: `List` ≈ `Array`, `Set` ≈ `Set`, `Map` ≈ `Map`. Java gives several implementations of each, with different performance trade-offs.

```java
List<String> orders = new ArrayList<>();          // ordered, duplicates OK
Set<String> tags = new HashSet<>();               // unique
Map<String, Integer> stock = new HashMap<>();     // key → value
Deque<String> jobs = new ArrayDeque<>();          // queue / stack
```

---

## `List` / `ArrayList`

- Declare as the **interface**, create the **implementation**: `List<String> names = new ArrayList<>();` (same idea as composition in 2.7 — depend on the interface).
- **Why declare as `List`, not `ArrayList`** — the payoff is at API boundaries (parameters, return types, fields), not local variables:
  - Parameter `List<T>` accepts any list: `ArrayList`, `List.of(...)`, `LinkedList`. A parameter typed `ArrayList<T>` rejects `List.of(...)` at compile time.
  - Return type `List<T>` lets you change what you return without touching callers — e.g. `return List.copyOf(items);` (a read-only copy) compiles only if the return type is `List`, because `List.copyOf` doesn't return an `ArrayList`.
  - Same idea as `private final Notifier notifier` (2.7): callers depend on the contract, not the implementation.
  - Use `ArrayList` as a type only when you need an `ArrayList`-only method (e.g. `ensureCapacity`) — almost never.

```java
static long total(ArrayList<Long> amounts) { ... }
total(List.of(100L, 200L));           // ❌ compile error — List.of isn't an ArrayList

static long total(List<Long> amounts) { ... }
total(List.of(100L, 200L));           // ✅ any List works

public List<CartItem> items() {
    return List.copyOf(items);        // ✅ only possible because the return type is List
}
```

- `<String>` = **generics**: the list only holds Strings, checked at compile time. `<>` (diamond) infers the type on the right.

```java
List<String> names = new ArrayList<>();
names.add(42);        // ❌ error: incompatible types: int cannot be converted to String
```

- Core methods:

```java
List<String> statuses = new ArrayList<>();
statuses.add("PAID");           // [PAID]                    append
statuses.add("PENDING");        // [PAID, PENDING]
statuses.add(0, "NEW");         // [NEW, PAID, PENDING]      insert at index (shifts the rest right)
statuses.get(1);                // → PAID                    IndexOutOfBoundsException if out of range
statuses.set(1, "SHIPPED");     // [NEW, SHIPPED, PENDING]   replace at index
statuses.size();                // → 3      NOT .length (arrays) and NOT .length() (String)
statuses.contains("PENDING");   // → true   uses equals() — your own classes need equals() (2.3)
statuses.indexOf("PENDING");    // → 2      first index, or -1
statuses.remove("NEW");         // [SHIPPED, PENDING]        by value → returns boolean
statuses.remove(0);             // [PENDING]                 by index → returns the removed element
statuses.isEmpty();             // → false
System.out.println(statuses);   // → [PENDING]   readable, unlike arrays' [I@1b6d3586
```

| Array | `ArrayList` |
|---|---|
| fixed size | grows automatically |
| `arr.length` | `list.size()` |
| `arr[i]`, `arr[i] = x` | `list.get(i)`, `list.set(i, x)` |
| primitives allowed (`int[]`) | objects only (`List<Integer>`) |
| prints `[I@...` | prints `[a, b, c]` |

---

## Primitives → wrapper classes, autoboxing

- Generics only take objects. Use the wrapper: `int→Integer`, `long→Long`, `double→Double`, `boolean→Boolean`, `char→Character`.
- **Autoboxing**: `nums.add(5)` silently becomes `nums.add(Integer.valueOf(5))`. **Unboxing**: `int x = nums.get(0)`.

```java
List<int> nums;                       // ❌ error: unexpected type
List<Integer> nums = new ArrayList<>();
nums.add(5);                          // boxing
int first = nums.get(0);              // unboxing → 5
```

- **Trap 1 — `==` on wrappers**: Java caches `Integer` objects only for −128..127. Same accident as the String pool → always `.equals()`.

```java
Integer a = 127, b = 127;
a == b          // → true    (cached — luck)
Integer c = 128, d = 128;
c == d          // → false   ❌
c.equals(d)     // → true    ✅
```

- **Trap 2 — `remove` overloads on `List<Integer>`**: `ids.remove(1)` removes **index** 1 (the exact `int` match wins). To remove the value: `ids.remove(Integer.valueOf(1))`.

```java
List<Integer> ids = new ArrayList<>(List.of(1, 2, 3));
ids.remove(1);                     // → [1, 3]   index 1 (the value 2) removed!
ids.remove(Integer.valueOf(1));    // → [3]      value 1 removed
```

- **Trap 3**: unboxing `null` → `NullPointerException`.

```java
Integer x = null;
int y = x;        // → NullPointerException
```

---

## Immutable lists

- `List.of("UPI", "CARD")` — fixed, unmodifiable. `add`/`remove`/`set` → `UnsupportedOperationException` at **run time** (compiles fine). Its `getMessage()` is `null`.
- Mutable copy: `new ArrayList<>(List.of(...))`.
- Defensive copy for a getter (2.2): `return List.copyOf(items);` — the caller can't modify your internal list.

```java
List<String> methods = List.of("UPI", "CARD");
methods.add("WALLET");     // → java.lang.UnsupportedOperationException   (getMessage() → null)

List<String> mutable = new ArrayList<>(methods);
mutable.add("WALLET");     // → [UPI, CARD, WALLET]
```

---

## Iterating

- for-each when you just need the elements; index loop when you need the position.
- Don't add/remove from a list while for-each-ing over it → `ConcurrentModificationException` (details in 3.5).

```java
for (String s : list) { ... }                          // for-each
for (int i = 0; i < list.size(); i++) { list.get(i); } // index
```

---

## `ArrayList` vs `LinkedList` (3.2)

- `ArrayList` = growable `Object[]` + a `size` counter. Full hone pe `newCapacity = old + (old >> 1)` (~1.5x), naya array allocate, `System.arraycopy`, purana GC ko.
- `LinkedList` = doubly-linked `Node` chain (value + prev + next), sirf `first`/`last` pakde hue.
- **size ≠ capacity.** `get` index ko **size** se check karta hai, capacity se nahi. Khaali slots "null values" nahi hain.

```java
List<String> list = new ArrayList<>(3);   // capacity 3, size 0
list.size();     // → 0
list.get(0);     // → java.lang.IndexOutOfBoundsException: Index 0 out of bounds for length 0
```

| Operation | `ArrayList` | `LinkedList` |
|---|---|---|
| `get(i)` / `set(i, x)` | **O(1)** | O(n) |
| `add(x)` (end) | **O(1)** amortized | **O(1)** |
| `add(0, x)` / `remove(0)` | O(n) shift | **O(1)** |
| `add(i, x)` / `remove(i)` middle | O(n) shift | O(n) walk |
| `remove(last)` | **O(1)** | **O(1)** |
| `contains` / `indexOf` | O(n) | O(n) |
| memory per element | 1 reference | pura `Node` object (~40 bytes) |

- **"Amortized O(1)"** — har `add` constant nahi (resize wala O(n) hai), par n adds ki *total* copy work bhi O(n) hai (geometric series ~2n), to total/n = constant.
- `LinkedList.get(i)` aadhe se aage ho to `last` se peeche chalta hai (`for (Node x = last; i > index; i--)`), phir bhi O(n).

### Measured (2026-10-12, M-series Mac, JDK 25) — `exercies/phase3/lesson2/ListBench.java`
```
test  arraylist(ms)  linkedlist(ms)
A  200k appends       2              3
B  20k get(mid)       0              2587      ← <1ms vs 2587ms
C  20k add(0, x)      14             0
```
- B: `LinkedList` ne ~2×10⁹ pointer hops kiye (20k gets × ~100k nodes). `ArrayList` ne 20k address calculations.
- C: `ArrayList` ne ~2×10⁸ element moves **14 ms** mein kiye — `System.arraycopy` contiguous block move hai (SIMD, cache-friendly). "O(n) shift" theory mein scary, hardware pe lagbhag free.
- **Decision rule — asymmetry, not who wins:** galat jagah `LinkedList` = +2587 ms (disaster); galat jagah `ArrayList` = +14 ms (nobody notices). Isliye **default `ArrayList`**, hamesha. `LinkedList` sirf pure-both-ends access pe, aur wahan bhi `ArrayDeque` better (3.5).
- **#1 Java perf bug:** `LinkedList` + `for (int i...) list.get(i)` → O(n²). `for-each` (Iterator, pichla node yaad rakhta hai) O(n) hai. Same loop, same output, 1000x slower.

```java
List<Order> orders = new LinkedList<>(...);
for (int i = 0; i < orders.size(); i++) {
    process(orders.get(i));        // ❌ each get(i) walks from the start → O(n²)
}
for (Order o : orders) {
    process(o);                    // ✅ iterator remembers its position → O(n)
}
```

---

## Benchmarking Java (seekha 3.2 mein)

- **Dead-code elimination**: loop ka result use na ho to JIT poora loop hata deta hai → 0 ms naapte reh jaoge. Result ek static field (`blackhole`) mein jama karo.
- **JIT warm-up**: JVM pehle interpreted chalta hai, kuch hazaar calls ke baad machine code compile karta hai. Warm-up round ke bina jo test *pehle* chalta hai wo apne aap slow dikhta hai.
- Hand-rolled benchmarks sirf **bade** farak ke liye (1 ms vs 2587 ms). 10%-type differences ke liye **JMH** (Java Microbenchmark Harness) use hota hai — warm-up, DCE aur statistics khud handle karta hai.
- Timed loop ke andar `"TRK-" + i` jaisa kaam mat karo — wo string-building naapega, list nahi. Constant use karo.

```java
static long blackhole;

static long timeGets(List<Integer> list) {
    long start = System.nanoTime();
    long sum = 0;
    for (int i = 0; i < 20_000; i++) {
        sum += list.get(list.size() / 2);        // the thing being measured
    }
    blackhole += sum;                            // ✅ result used → JIT can't delete the loop
    return (System.nanoTime() - start) / 1_000_000;
}

timeGets(arrayList); timeGets(linkedList);       // warm-up round, ignore results
long a = timeGets(arrayList);                    // measured round
long l = timeGets(linkedList);
```

---

## `HashMap` / `HashSet` internals (3.3)

### Mechanism
- `HashMap` andar ek array rakhta hai: `Node<K,V>[] table`, default length **16**, hamesha **power of 2**.
- Bucket index: `index = hash & (table.length - 1)` — power-of-2 length isliye, ki `& (n-1)` modulo ka kaam karta hai aur division se tezz hai.
- Spreading: `static final int hash(Object key) { int h = key.hashCode(); return h ^ (h >>> 16); }` — `& 15` sirf neeche ke 4 bits dekhta hai, to upar ke bits ki information neeche laayi jaati hai.
- **Collision** = do keys ka same bucket. Bucket mein ek chain (linked list) banti hai. Java 8+: ek bucket mein >8 nodes aur table ≥64 → chain **red-black tree** ban jaati hai → worst case O(n) ki jagah **O(log n)**.
- `get(k)` ka flow: `hashCode()` → bucket index → us bucket ki chain pe chalo → har node pe `equals()` → match ya `null`.

```java
int h = "apple".hashCode();          // → 93029210
int spread = h ^ (h >>> 16);
int bucket = spread & (16 - 1);      // → 1   "apple" lives in bucket 1 of 16

Integer.valueOf(5).hashCode() & 15   // → 5   small Integers land in "their own" bucket
```

> **`hashCode()` batata hai kis bucket mein jaana hai. `equals()` batata hai us bucket mein kaunsa element sahi hai.** Dono chahiye, dono ka kaam alag.

### Load factor & resize
- `threshold = capacity × loadFactor` → default `16 × 0.75 = 12`.
- `if (++size > threshold) resize();` → **13vi** entry pe resize: capacity 16 → **32**, naya threshold **24**, aur saari entries ka index dobara nikalta hai (`& 31`) = **rehash**, O(n) (amortized, `ArrayList` jaisa).
- `new HashMap<>(1000)` se rehashes kam.
- **Natija:** iteration order = bucket order, aur resize pe **badal jaata hai**. Us order pe kabhi depend mat karo.
- Trap: chhote `Integer` keys ke saath order *sorted dikhta* hai (`Integer.hashCode()` wahi number hai, `5 & 15 = 5`). Sanyog hai, guarantee nahi.

```java
Map<Integer, String> small = new HashMap<>();
for (int i : new int[] {9, 3, 7, 1}) small.put(i, "x");
small.keySet()     // → [1, 3, 7, 9]   looks sorted — coincidence, NOT a guarantee

Map<String, Integer> big = new HashMap<>(1000);   // pre-sized → fewer resizes
```

### equals/hashCode contract (2.3 ka asli kaaran)
- `equals` se equal objects ka `hashCode` **bhi** equal hona chahiye. Warna → alag bucket → `equals` ka mauka hi nahi aata → **object map mein hai par pahunch se bahar**, bina kisi error ke.
- `equals` likha par `hashCode` nahi → `Object` ka identity-based default milta hai → har naya object alag bucket.

```java
final class K {
    final int id;
    K(int id) { this.id = id; }
    @Override public boolean equals(Object o) { return o instanceof K k && k.id == id; }
    // no hashCode()!
}

Map<K, String> m = new HashMap<>();
m.put(new K(42), "answer");
m.get(new K(42))     // → null   ❌ looked in a different bucket
m.size()             // → 1      it IS in there
```

- Ulta safe hai: same `hashCode` + `equals` false → sirf collision (slow, galat nahi). Isliye `hashCode` "approximate" ho sakta hai, `equals` ke saath **inconsistent** nahi.
- Dono mein **wahi** fields use karo. `Objects.hash(a, b)` / `Objects.equals(x, y)`.
- **Mutable key trap:** key daal ke uska hash-wala field badal diya → entry purane bucket mein, lookup naye bucket mein → `get` `null`, `containsKey` `false`, nikaalna bhi mushkil. **Map keys immutable rakho** (`String`, `Integer`, `enum`, `final`-fields class).

```java
MutKey key = new MutKey("A1");        // equals/hashCode on a NON-final field `code`
map.put(key, "v");
key.code = "B2";                      // hash changes after insertion
map.get(key)            // → null
map.containsKey(key)    // → false
map.size()              // → 1        stuck in the old bucket
```

### API
- `get` on a missing key returns `null` — no exception.
- `put` on an existing key **replaces** the value (size doesn't change) and returns the old value.
- Counting ka classic bug: `map.put(k, map.get(k) + 1)` → pehli baar `get` `null` → unboxing NPE (3.1 Trap 3). `getOrDefault(k, 0)` ya `merge` use karo.
- `keySet()` pe loop + andar `get(key)` = **do** lookups. `entrySet()` ek mein dono deta hai.
- `HashMap`: ek `null` key + kitni bhi `null` values allowed. (`ConcurrentHashMap` nahi.)

```java
Map<String, Integer> m = new HashMap<>();
m.put("a", 1)                // → null   (key was new)
m.put("a", 2)                // → 1      (old value returned, REPLACED)
m.size()                     // → 1
m                            // → {a=2}
m.get("zzz")                 // → null
m.getOrDefault("zzz", 0)     // → 0
m.putIfAbsent("a", 99);      // no change, "a" exists
```
```java
counts.put("pen", counts.get("pen") + 1);     // ❌ first time → NullPointerException

Map<String, Integer> counts = new TreeMap<>();
for (String w : List.of("pen", "book", "pen", "pen")) {
    counts.merge(w, 1, Integer::sum);         // ✅ counting
}
counts                                        // → {book=1, pen=3}

Map<String, List<String>> byCategory = new TreeMap<>();
byCategory.computeIfAbsent("food", k -> new ArrayList<>()).add("pizza");    // ✅ grouping
byCategory.computeIfAbsent("travel", k -> new ArrayList<>()).add("cab");
byCategory.computeIfAbsent("food", k -> new ArrayList<>()).add("tea");
byCategory                                    // → {food=[pizza, tea], travel=[cab]}

for (Map.Entry<String, Integer> e : counts.entrySet()) {   // ✅ iterate — one lookup
    System.out.println(e.getKey() + "=" + e.getValue());   // → book=1   pen=3
}

Map<String, Integer> withNull = new HashMap<>();
withNull.put(null, 1);                        // → {null=1}   allowed in HashMap
```

### `HashSet` = `HashMap` with dummy values (literally)
```java
private transient HashMap<E,Object> map;
private static final Object PRESENT = new Object();
public boolean add(E e) { return map.put(e, PRESENT) == null; }
```
- Isliye: duplicates nahi (= key uniqueness), `contains` **O(1)** (vs `List` ki O(n)), koi order nahi, aur `equals`/`hashCode` ki wahi zaroorat.
- **`Map` ki keys ek `Set` hai** — isliye method ka naam `keySet()` hai. Set ka duplicate-reject karna aur Map ka key pe replace karna **ek hi** rule hai.

```java
Set<String> set = new HashSet<>();
set.add("PAID")          // → true    (new)
set.add("PAID")          // → false   (duplicate rejected — map.put returned an old value)
set.size()               // → 1
set.contains("PAID")     // → true    O(1)
```

### Order: teen cheezein alag hain
| | order | lookup | kab |
|---|---|---|---|
| `HashMap` / `HashSet` | **koi nahi** (resize pe badalta hai) | O(1) | default |
| `LinkedHashMap` / `LinkedHashSet` | **insertion** | O(1) | deterministic output, caches/LRU |
| `TreeMap` / `TreeSet` | **sorted** | O(log n) | ranges, sorted reports (3.4) |

- `LinkedHashMap` `HashMap` ko extend karta hai + entries ke beech ek extra doubly-linked list (`before`/`after`) rakhta hai. Lookup abhi bhi O(1), thodi zyada memory, iteration us chain pe (buckets pe nahi).
- **`LinkedHashMap` ≠ sorted.** Insertion order aur sorted order do alag cheezein hain — randomness hatana sorting nahi hai.
- `LinkedHashSet` = duplicate-free **aur** insertion-ordered. Map ki value ke roop mein grouping ke liye perfect.

```java
// insert PAID, SHIPPED, CANCELLED into each:
HashMap        → [CANCELLED, PAID, SHIPPED]   no promised order
LinkedHashMap  → [PAID, SHIPPED, CANCELLED]   insertion order
TreeMap        → [CANCELLED, PAID, SHIPPED]   sorted (here it happens to match HashMap — coincidence)
```

### Manual sorting (3.4 tak)
```java
List<Integer> keys = new ArrayList<>(map.keySet());
Collections.sort(keys);
for (int k : keys) { System.out.println(k + " -> " + map.get(k)); }
```

### Parsing note (Ex 3 se)
- `line.split(" ")` multiple spaces pe khaali strings deta hai. **`split("\\s+")`** use karo.
- Sabak: test data jaan-boojh ke ganda rakho (extra spaces, mixed casing) — chhote saaf dataset ne is bug ko chhupa diya tha.

```java
"GET  /users".split(" ")       // → [GET, , /users]   ❌ empty string in the middle
"GET  /users".split("\\s+")    // → [GET, /users]     ✅
```

---

## `TreeMap`/`TreeSet`, `Comparable`/`Comparator` (3.4)

### `TreeMap` / `TreeSet`
- Red-black tree (self-balancing BST). Left mein chhoti keys, right mein badi. **Hamesha sorted.**
- `get`/`put`/`contains` **O(log n)** (vs `HashMap` ki O(1)) — to `TreeMap` tabhi lo jab order ya range queries ki asli zaroorat ho.
- `null` key → **NPE** (compare kis se karega?). `HashMap` ek null key allow karta hai. Fark chubhta hai.
  - Exception: `new TreeMap<>(Comparator.nullsFirst(Comparator.naturalOrder()))` null key allow kar deta hai. Sirf tab jab `null` ka business matlab ho (jaise "no expiry"), galti chhupane ke liye nahi.
- Comparable class na ho aur comparator bhi na do → **pehle hi** `add` pe `ClassCastException`. (Correction 2026-11-03: purane notes mein "doosre add pe" likha tha — JDK 25 mein khaali tree mein pehla element daalte waqt bhi `compare(key, key)` chalta hai, verified.)

```java
new TreeMap<String, Integer>().put(null, 1);   // → NullPointerException
new HashMap<String, Integer>().put(null, 1);   // → fine, {null=1}

class NotComparable { }
Set<NotComparable> bad = new TreeSet<>();
bad.add(new NotComparable());
// → ClassCastException: class NotComparable cannot be cast to class java.lang.Comparable   (FIRST add)
```

### Range queries — `TreeMap` ki asli wajah
- **Asymmetric bounds** (Java ka har jagah ka pattern, jaise `substring`/`subList`/`i < n`): shuruaat shaamil, ant nahi. Override: `headMap(20, true)`.
- `floorKey`/`ceilingKey` **`null` de sakte hain** jab kuch qualify na kare → `floorEntry(x).getValue()` pe NPE. Hamesha check karo.
- `TreeSet` ke paas wahi: `first()`, `last()`, `floor()`, `ceiling()`, `higher()`, `lower()`, `headSet()`, `tailSet()`, `subSet()`, `descendingSet()`, `pollFirst()`.
- Backend use cases: price/rate tiers, time-series ("is timestamp se pehle ka last event"), rate-limit windows, version ranges.

```java
TreeMap<Integer, String> tiers = new TreeMap<>(Map.of(
        0, "Bronze", 1000, "Silver", 5000, "Gold", 20000, "Platinum"));
// → {0=Bronze, 1000=Silver, 5000=Gold, 20000=Platinum}   always sorted

tiers.firstKey()              // → 0
tiers.lastKey()               // → 20000
tiers.floorKey(3000)          // → 1000    biggest key <= 3000
tiers.ceilingKey(3000)        // → 5000    smallest key >= 3000
tiers.floorKey(5000)          // → 5000    (inclusive)
tiers.higherKey(5000)         // → 20000   strictly bigger
tiers.floorKey(-1)            // → null    ⚠️ nothing qualifies
tiers.floorEntry(3000).getValue()   // → Silver   "which tier is 3000 points?"

tiers.headMap(5000)           // → {0=Bronze, 1000=Silver}               upper bound EXCLUSIVE
tiers.headMap(5000, true)     // → {0=Bronze, 1000=Silver, 5000=Gold}
tiers.tailMap(5000)           // → {5000=Gold, 20000=Platinum}           lower bound INCLUSIVE
tiers.subMap(1000, 20000)     // → {1000=Silver, 5000=Gold}              1000 <= k < 20000
tiers.descendingMap(); tiers.pollFirstEntry();
```

### `Comparable` — natural order, class ke andar
- Return value ka **number matter nahi karta, sirf sign**: negative → `this` pehle; zero → barabar; positive → `this` baad mein.
- Padhne ka trick: `a.compareTo(b)` ko `a - b` padho. (Likhne ka tarika **nahi** — neeche dekho.)
- Ek baar likha → `Collections.sort`, `TreeSet`, `TreeMap`, `list.sort(null)`, `PriorityQueue` sab chal gaye.
- Already `Comparable`: `String`, `Integer`/`Long`/`Double`, `Character`, `LocalDate`/`LocalDateTime`, aur **`enum`** (declaration order).

```java
class Student implements Comparable<Student> {
    final String name;
    final int marks;
    // constructor, toString → "Asha(82)"

    @Override
    public int compareTo(Student other) {
        return Integer.compare(this.marks, other.marks);
    }
}

List<Student> st = new ArrayList<>(List.of(
        new Student("Asha", 82), new Student("Ravi", 67), new Student("Meera", 91)));
Collections.sort(st);
st                  // → [Ravi(67), Asha(82), Meera(91)]
```

- **KABHI subtraction se compare mat karo.** Overflow → sign ulta → pura order ulta, bina error. `Integer.compare(a, b)` andar `(a<b)?-1:((a==b)?0:1)` hai — koi arithmetic nahi, overflow ka sawaal nahi. `double` ke liye `Double.compare` (`NaN`/`-0.0` bhi handle karta hai).

```java
int big = Integer.MAX_VALUE, small = -10;
big - small                    // → -2147483639   ❌ says "big < small"
Integer.compare(big, small)    // → 1             ✅
```

### `Comparator` — order bahar se, kai ho sakte hain
- `TreeSet`/`TreeMap` ko comparator de do → class ka `compareTo` **bilkul ignore** ho jaata hai.
- Comparators ko `private static final` constants banao — naam se code padha jaata hai.
- **Kab kaunsa:** ek obvious default order → `Comparable`. Kai orders / class aapki nahi / order business rule hai → `Comparator`. Dono saath ho sakte hain.
- **Tie-breaker kab zaroori hai:** `List.sort` Java mein **stable** hai, to same input order pe output deterministic hota hai bina breaker ke bhi. Breaker tab chahiye jab (a) `TreeSet`/`TreeMap` ho — wahan barabar element **gir** jaata hai, ya (b) **input order hi random ho** — jaise `HashMap.entrySet()` se aaya data.

```java
Comparator.comparing(Player::getName)          // object field
Comparator.comparingInt(Player::getScore)      // int — boxing bachata hai
.reversed()                                    // ulta
.thenComparing(...)                            // tie-breaker
Comparator.naturalOrder() / nullsFirst(...) / nullsLast(...)
list.sort(cmp);  new TreeSet<>(cmp);  new TreeMap<>(cmp);
```
```java
private static final Comparator<Player> LEADERBOARD =
        Comparator.comparingInt(Player::getScore).reversed()
                  .thenComparing(Player::getName);

List<Player> ps = new ArrayList<>(List.of(
        new Player("ravi", 50), new Player("asha", 90), new Player("meera", 50)));
ps.sort(LEADERBOARD);
ps                  // → [asha:90, meera:50, ravi:50]   score desc, then name
ps.sort(Comparator.comparing(Player::getName));
ps                  // → [asha:90, meera:50, ravi:50]   by name
```

### THE TRAP — `TreeSet` `equals()` use NAHI karta
- `equals` naam+score dekhe, `compareTo` sirf score dekhe → class apne aap se ulta keh rahi hai.
- `TreeSet` tree mein chalte hue compare karta hai; `compareTo == 0` hi uske liye "duplicate" ki definition hai. `equals` ko bulata hi nahi.
- **Fix:** `compareTo == 0` sirf tab jab `equals == true`. Practically — `compareTo` mein **wahi fields** jo `equals` mein hain, tie-breakers ke roop mein.

```java
// equals/hashCode: name + score.   compareTo: score only.
P alice = new P("alice", 50), bob = new P("bob", 50);

alice.equals(bob)              // → false   ALAG hain
alice.compareTo(bob)           // → 0       BARABAR hain

new HashSet<>(List.of(alice, bob)).size()   // → 2           sahi
TreeSet<P> tree = new TreeSet<>(); tree.add(alice); tree.add(bob);
tree                           // → [alice(50)]  bob CHUP-CHAAP gir gaya
tree.contains(bob)             // → true         'hai' bola, par andar alice hai
tree.remove(bob);
tree                           // → []           ALICE delete ho gayi, bob ke naam pe
```
```java
@Override
public int compareTo(P o) {                       // ✅ same fields as equals, as tie-breakers
    int byScore = Integer.compare(score, o.score);
    return byScore != 0 ? byScore : name.compareTo(o.name);
}
```

| Collection | duplicate kaise decide | tootne pe |
|---|---|---|
| `HashSet`/`HashMap` | `hashCode()` + `equals()` | object andar hai par **milta nahi** |
| `TreeSet`/`TreeMap` | `compareTo()` / `Comparator` | object **chup-chaap gir** jaata hai, galat object delete ho sakta hai |

Dono mein common: **koi exception nahi aati** — isliye production tak pahunch jaate hain.

> **Poora Phase 3 contract ek line mein:** `hashCode` = kis bucket mein. `equals` = wahi object hai ya nahi. `compareTo` = pehle/baad/**barabar** — aur "barabar" ka matlab tree ke liye "duplicate" hai.

### Decision table — chaar options
| Chahiye | Lo |
|---|---|
| sabse tezz lookup, order ki parwah nahi | `HashMap` / `HashSet` |
| insertion order (deterministic output, cache) | `LinkedHashMap` / `LinkedHashSet` |
| hamesha sorted + range queries | `TreeMap` / `TreeSet` |
| ek baar sort karke print karna hai | `HashMap` + `list.sort(...)` |

### Misc
- Modern Java (14+) **helpful NullPointerException**: message variable ka naam batata hai — `Cannot invoke "Comparable.compareTo(Object)" because "k1" is null`.
- Crash ko chhupao mat, crash ki wajah hatao. `TreeMap` ka NPE bata raha tha ki data mein `null` aa raha hai — use accept karna sikhana usse silent bug bana deta hai. (Phase 1 L3 ke `/ by zero` → `Infinity` wala sabak, dobara.)

---

## `ArrayDeque`, `PriorityQueue`, CME, immutable collections (3.5)

### `Queue` / `Deque` / `ArrayDeque`
- `Deque` = "Double Ended QUEue" — dono siron se add/remove. Ek hi class se **queue (FIFO) aur stack (LIFO)** dono.
- `ArrayDeque` andar **circular array** hai, `head` aur `tail` index ke saath. Front se nikaalne pe kuch shift **nahi** hota — bas `head` aage badhta hai, aur index array ke end pe wrap ho jaata hai. **Dono sire O(1)**, aur array hone ki wajah se cache-friendly bhi.
- Yahi 3.2 ka jawab: `ArrayList.remove(0)` O(n) tha (shifting), `ArrayDeque.pollFirst()` O(1) hai — aur `LinkedList` se bhi tezz, kyunki pointer chasing nahi.
- **`ArrayDeque` lo; `Stack` aur `LinkedList` mat lo.** `Stack` Java 1.0 legacy hai — har method `synchronized` (single-thread mein bekaar overhead) aur uska iteration order **ulta** (bottom→top). Oracle docs khud `ArrayDeque` recommend karti hain.

```java
Deque<String> queue = new ArrayDeque<>();      // FIFO
queue.offer("job1"); queue.offer("job2"); queue.offer("job3");
queue.poll()        // → job1   (first in, first out)
queue.peek()        // → job2   (look, don't remove)
queue               // → [job2, job3]

Deque<String> stack = new ArrayDeque<>();      // LIFO
stack.push("a"); stack.push("b"); stack.push("c");
stack.pop()         // → c      (last in, first out)
stack               // → [b, a]
```

**Do method families — har operation ke do version:**
| Kaam | Exception phenkta hai | Special value deta hai |
|---|---|---|
| daalo | `add(x)` → `IllegalStateException` | `offer(x)` → `false` |
| nikaalo | `remove()` → `NoSuchElementException` | `poll()` → `null` |
| dekho | `element()` → `NoSuchElementException` | `peek()` → `null` |
- Khaali hona **normal** hai → `poll`/`peek`. Khaali hona **bug** hai → `remove`/`element`, taaki turant pata chale.
- `push`/`pop` = `addFirst`/`pollFirst` (stack ki tarah).

```java
Deque<String> empty = new ArrayDeque<>();
empty.poll()        // → null
empty.peek()        // → null
empty.remove()      // → NoSuchElementException
```

### `PriorityQueue`
- Sabse **chhota** element pehle nikalta hai (`compareTo`/`Comparator` se decide), chahe kab daala gaya ho. Comparator se reverse: `new PriorityQueue<>(Comparator.reverseOrder())`.
- Andar ek **binary heap** — array mein store kiya tree, jiska **ek hi** guarantee hai: *har parent apne children se chhota hai.* Poora array sorted **nahi** hota.
- `add`/`poll` **O(log n)**, `peek` **O(1)** (sabse chhota hamesha index 0).
- **THE TRAP — iteration/`toString` sorted NAHI hoti.** Sirf `poll()` ka order sorted hai (har poll heap ko dobara arrange karta hai).

```java
PriorityQueue<Integer> pq = new PriorityQueue<>();
for (int i : new int[] {5, 1, 9, 2}) pq.add(i);
pq                       // → [1, 2, 9, 5]    ❌ NOT sorted (heap layout)
while (!pq.isEmpty()) {
    System.out.print(pq.poll() + " ");   // → 1 2 5 9    ✅ poll order is sorted
}

PriorityQueue<Integer> maxFirst = new PriorityQueue<>(Comparator.reverseOrder());
maxFirst.addAll(List.of(5, 1, 9, 2));
maxFirst.poll()          // → 9
```
```
add: email/5, payment/1, report/9, alert/2
array: [payment/p1, alert/p2, report/p9, email/p5]
tree:        p1
            /  \
          p2    p9
         /
       p5
parent<child saare rules poore hain, par p9 array mein p5 se PEHLE hai —
kyunki p9 aur p5 ka aapas mein koi rishta nahi (alag parents, index 2 vs 3).
for-each index order pe chalta hai → p1, p2, p9, p5
```
- Sorted print: `while (!q.isEmpty()) q.poll();` — par ye queue **khaali kar deta hai**. Bachani ho to `new PriorityQueue<>(q)` ya `new ArrayList<>(q)` + sort.

### `ConcurrentModificationException`
- **Naam dhokha deta hai — "concurrent" ka matlab threads nahi.** Single thread mein hota hai.
- Mechanism: `ArrayList` ek `modCount` field rakhta hai jo har **structural** change (add/remove) pe badhta hai. For-each andar ek `Iterator` banata hai jo shuru mein `expectedModCount` ki copy rakhta hai; har `next()` pe `checkForComodification()` dono ko compare karta hai, mismatch → exception.
- **Ye feature hai, bug nahi** — ise **fail-fast** kehte hain. Check na hota to elements chup-chaap skip hote (3.2 ke `dropCancelled` wala silent bug, jo index loop mein hua tha kyunki wahan koi iterator hi nahi tha).
- **Structural = size badalna** (add/remove). **`set(i, x)` structural nahi** → CME nahi aayega. `Map` mein purani key pe `put` (replace) bhi structural nahi; nayi key daalna hai.
- **Trap (verified 2026-11-03):** **second-last** element hatao to CME **nahi** aata — `remove` ke baad size ghat jaata hai, `hasNext()` false ho jaata hai, aur loop `next()` (jahan check hota hai) se pehle hi khatam. Last element silently skip. Isliye "mere test mein CME nahi aaya" ka matlab "code sahi hai" nahi.

```java
List<String> a = new ArrayList<>(List.of("t1-skip", "t2", "t3"));
for (String t : a) {
    if (t.endsWith("-skip")) a.remove(t);   // → ConcurrentModificationException
}

List<String> b = new ArrayList<>(List.of("t1", "t2-skip", "t3"));
for (String t : b) {
    if (t.endsWith("-skip")) b.remove(t);   // → NO exception (second-last) → [t1, t3], t3 never checked
}

for (String t : list) list.set(0, "z");     // ✅ set is not structural → no CME

List<String> d = new ArrayList<>(List.of("a-skip", "b-skip", "c"));
for (int i = 0; i < d.size(); i++) {
    if (d.get(i).endsWith("-skip")) d.remove(i);   // ❌ forward index loop
}
d                                           // → [b-skip, c]   silently skipped one
```

**Char fixes:**
```java
pending.removeIf(t -> t.endsWith("-skip"));                   // 1. default choice
Iterator<String> it = pending.iterator();                     // 2. remove ke saath kuch aur bhi karna ho
while (it.hasNext()) { if (...) it.remove(); }                //    it.remove() dono counters update karta hai
for (String t : new ArrayList<>(pending)) { pending.remove(t); }  // 3. copy pe iterate
for (int i = pending.size() - 1; i >= 0; i--) { ... }         // 4. index loop ULTA
```
```java
List<String> p = new ArrayList<>(List.of("t1", "t2-skip", "t3", "t4-skip"));
p.removeIf(t -> t.endsWith("-skip"));      // → [t1, t3]

List<String> e = new ArrayList<>(List.of("a-skip", "b-skip", "c"));
for (int i = e.size() - 1; i >= 0; i--) {
    if (e.get(i).endsWith("-skip")) e.remove(i);
}
e                                          // → [c]   ✅ backwards: removals don't shift what's left to check
```

### Immutable collections — **view** vs **copy**
```java
Collections.unmodifiableList(internal)   // VIEW  — khidki
List.copyOf(internal)                    // COPY  — photo
```
| | caller modify kar sakta? | original badla to dikhega? |
|---|---|---|
| `Collections.unmodifiableList(x)` | ❌ | **✅ haan (view)** |
| `List.copyOf(x)` | ❌ | ❌ |
| `new ArrayList<>(x)` | ✅ (apni copy) | ❌ |
- **Getter ke liye `List.copyOf` default.** View tabhi jab live data *chahiye* ho — aur tab document karo.

```java
List<String> internal = new ArrayList<>(List.of("x"));
List<String> view = Collections.unmodifiableList(internal);
List<String> snap = List.copyOf(internal);
internal.add("y");
view     // → [x, y]   window: sees the change
snap     // → [x]      photo: frozen
```

**Factory gotchas:**
- `Arrays.asList(arr)` = **array ke upar wrapper**. Size fixed → `add` UOE; par `set` chalta hai **aur wo write asli array mein jaata hai**. `List.of` nahi hai — legacy code mein milega, naya code mein `List.of`.
- **Compile vs run time test:** *"javac ko ye jaanne ke liye code chalana padega?"* Haan → run time (`Set.of` duplicate, `List.of` null). Sirf types/syntax se pata chal jaata → compile time (`List.of(42)` ko `List<String>` mein daalna).

```java
List.of("a", null)        // → NullPointerException — null allowed nahi
Set.of("a", "a")          // → IllegalArgumentException: duplicate element: a   (RUN time)
Map.of(k,v, ...)          // max 10 jode; zyada ke liye Map.ofEntries(entry(...), ...)
Map.of(...)               // order per-JVM-run randomized, HashMap se bhi zyada random

String[] arr = {"a", "b"};
List<String> asList = Arrays.asList(arr);
asList.set(0, "Z");
arr[0]                    // → Z    the write went into the array
asList.add("c");          // → UnsupportedOperationException
```

---

## Generics (3.6)

### Purpose
- **Ek line:** run-time `ClassCastException` ko **compile-time error** bana dena, aur casts hatana.
- Asli faayda casts bachana nahi — galti **wahan pakdi jaaye jahan ki gayi**, 20 min debugging ke baad doosri file mein nahi.

```java
List names = new ArrayList();            // raw, pre-2004 style
names.add(42);                           // accepted
String s = (String) names.get(0);        // → ClassCastException, far from the add

List<String> names2 = new ArrayList<>();
names2.add(42);                          // ❌ compile error, right where the mistake is
String s2 = names2.get(0);               // no cast needed
```

### Generic class / method
- Conventions: `T` Type, `E` Element, `K`/`V` Key/Value, `R` Return. (Sirf convention, par code inhi se padha jaata hai.)
- Type javac **infer** karta hai argument se — wahi inference jo `new ArrayList<>()` ke diamond mein hai.
- **`static` method class ka `T` use nahi kar sakta** — `T` instance pe decide hota hai (`new Box<String>()`), static instance ke bina chalta hai. Apna parameter declare karo: `static <U> U identity(U x)`.

```java
class Box<T> {
    private T value;
    void set(T v) { value = v; }
    T get() { return value; }
}
class Pair<K, V> { ... }

Box<String> box = new Box<>();
box.set("hi");
box.get().toUpperCase()      // → HI   no cast

public static <T> List<T> firstTwo(List<T> items) { ... }   // <T> RETURN TYPE SE PEHLE
firstTwo(List.of("a", "b", "c"))    // T inferred as String

class Box<T> {
    static T make() { ... }   // ❌ error: non-static type variable T cannot be referenced from a static context
}
```

### Bounded type parameters
- Bina bound `T` javac ke liye **`Object`** hai → `item.compareTo(...)` → `cannot find symbol`. Bound hi wo cheez hai jo `T` pe methods kholti hai.
- Generics mein interface ke liye bhi **`extends`** likha jaata hai (`implements` keyword nahi hota).
- Bound utna tight rakho jitna zaroori — na khaali `<T>` (kaam nahi karega), na bina wajah `<T extends Number>`.

```java
<T extends Comparable<T>>            // apne aap se compare ho sake
<T extends Number>                   // Number ya subclass
<T extends Number & Comparable<T>>   // DONO — class pehle, interfaces baad mein
```
```java
static <T> T largest(List<T> items) {
    ... if (item.compareTo(best) > 0) ...      // ❌ error: cannot find symbol (T is just Object)
}

static <T extends Comparable<T>> T largest(List<T> items) {
    T best = items.get(0);
    for (T item : items) if (item.compareTo(best) > 0) best = item;
    return best;
}
largest(List.of(3, 9, 5))                   // → 9
largest(List.of("pear", "apple", "zebra"))  // → zebra
```

### Type erasure — sabse important
> `<String>` ek **instruction hai javac ke liye**, `.class` file mein ek **property nahi**. javac padhta hai, check karta hai, cast daalta hai, aur **mita deta hai**. JVM ko kabhi pata nahi chalta.

```java
new ArrayList<String>().getClass() == new ArrayList<Integer>().getClass()   // → true
new ArrayList<String>().getClass().getName()                               // → java.util.ArrayList
```
```java
// aapne likha                          // bytecode mein (effectively)
List<String> l = new ArrayList<>();     List l = new ArrayList();
String s = l.get(0);                    String s = (String) l.get(0);   // cast javac ne daala
```
- `Box<T>` mein `T` → **`Object`**. Bounded ho to bound → `<T extends Number>` mein `T` → `Number`.
- **Kyun erasure?** 2004 ki **backward compatibility** — generic code aur purana raw code ek hi `ArrayList` class use karte hain, to dono saath chalte hain. (C# ne ulta chuna — unke generics reified hain.)

**Erasure kya rokta hai** (saare compile errors):
```java
T[] arr = new T[10];                 // error: generic array creation
if (x instanceof List<String>)       // error: Object cannot be safely cast to List<String>
void f(List<String> a); void f(List<Integer> a);   // error: name clash: f(List<Integer>) and f(List<String>) have the same erasure
T.class  /  new T()  /  catch (MyEx<T> e)  /  static field of type T    // sab illegal
```
Ek hi wajah chaaron ke liye: run time pe `T` ka koi nishaan nahi bachta.

### Raw types & heap pollution
- `List list = new ArrayList();` legal hai (purane code ke liye) par saari safety chali jaati hai. Default javac sirf ek **Note** deta hai; `-Xlint:unchecked` se poori warning:
```
Note: Raw.java uses unchecked or unsafe operations.
Note: Recompile with -Xlint:unchecked for details.

$ javac -Xlint:unchecked Raw.java
warning: [unchecked] unchecked call to add(E) as a member of the raw type List
```
  Matlab: *"main is call ki type-safety check nahi kar paaya, tum apne risk pe ho."*
- **Heap pollution** = typed collection mein galat type ka object ghus jaana. Crash **injection site pe nahi**, us jagah hota hai jahan javac ne apna chhipa cast daala tha (jaise for-each ke andar):

```java
List raw = new ArrayList<String>();
raw.add(42);                        // bug injected here — no error
List<String> typed = raw;
typed.size()                        // → 1   still no error
for (String s : typed) { }          // → ClassCastException: class java.lang.Integer cannot be cast to class java.lang.String
```
```
line 8:  keys.add(42);        <- bug yahan inject, koi error nahi
line 16: for (String k : ...) <- crash yahan, 8 line door, aur 2 element process hone ke BAAD
```
- Cast javac se kehna hai *"bharosa karo, main jaanta hoon"*. Raw `Map` se `get` `Object` deta hai; `Object`→`String` cast **ho sakta hai sahi**, to javac maan leta hai aur check run time pe chhod deta hai. Jhoot bola to `ClassCastException`.
- `@SuppressWarnings("unchecked")` sirf sabse chhote scope pe (ek variable/line), kabhi poori class pe, aur comment likho kyun safe hai.

### Bound ka payoff — galti compile time pe shift ho jaati hai
```java
// pehle (raw): run time
largest(List.of(3, "nine", 5))   -> ClassCastException

// baad (<T extends Comparable<T>>): compile time
error: method largest cannot be applied to given types;
  reason: inference variable E has incompatible bounds
    equality constraints: Integer,String
```
```java
// V = Object  -> type-safe, par faayda dikhta nahi
TypedCache<String, Object> c;  c.put("age", 30);    // chal jaata hai
// V = String  -> yahi wo shift hai jo generics kharidte hain
TypedCache<String, String> c;  c.put("age", 30);    // error: int cannot be converted to String
```

### Invariance (3.7 ka pul)
- `String` `Object` ka subtype **hai**, par `List<String>` `List<Object>` ka subtype **NAHI** hai.
- Java pehle step pe rok deta hai taaki teesra kabhi na aaye. Safe hal: `? extends` / `? super` (3.7).

```java
List<Object> b = new ArrayList<String>();   // ❌ error: incompatible types: ArrayList<String> cannot be converted to List<Object>

// why: AGAR allowed hota...
List<Object> objs = names;   // ...ye legal hota
objs.add(42);                // ...ye bhi
String s = names.get(0);     // ...aur yahan CCE
```

### TS comparison
TS generics bhi **erased** hain (`Array<string>`/`Array<number>` runtime pe dono `Array`), to basic model wahi hai. Farak: TS structural + optional, `any` se kahin bhi nikal sakte ho; Java nominal + mandatory, aur raw type (`any` ka kareeb) pe javac **warning** deta hai — chup-chaap jaane nahi deta.

---

## Wildcards, `? extends`/`? super`, PECS (3.7)

### Problem — invariance
`List<Integer>` `List<Number>` ka subtype **nahi** hai, to `sum(List<Number>)` `List<Integer>` accept nahi karega. Teen copies likhne ki jagah wildcard lagao.

```java
static double sum(List<Number> nums) { ... }
List<Integer> ints = new ArrayList<>(List.of(1, 2, 3));
sum(ints);        // ❌ error: incompatible types: List<Integer> cannot be converted to List<Number>

static double sum(List<? extends Number> nums) {
    double total = 0;
    for (Number n : nums) total += n.doubleValue();
    return total;
}
sum(ints)                    // → 6.0   ✅
sum(List.of(1.5, 2.5))       // → 4.0   ✅ same method, Doubles
```

### `? extends T` — PRODUCER (nal 🚰)
- Padho: *"kisi **ek** aise type ki list jo `Number` ya uska subclass hai — par pata nahi kaunsa."*
- **`CAP#1` kya hai:** javac `? extends Number` dekh ke ek **capture** type variable banata hai jiske baare mein use sirf itna pata hai "ye `Number` extend karta hai"; **identity pata nahi**. To `add(7)` pe sochta hai *"CAP#1 `Integer` ho sakta hai, `Double`, ya `BigDecimal` — nahi pata, to reject."* `null` chalta hai kyunki wo har reference type mein fit hota hai.

```java
List<? extends Number> prod = ints;
Number n = prod.get(0);    // ✅ jo bhi ho, Number to hai
prod.add(7);               // ❌ error: incompatible types: int cannot be converted to CAP#1
prod.add((Integer) 7);     // ❌ wahi error — cast se bhi nahi
prod.add(null);            // ✅ sirf null
```

### `? super T` — CONSUMER (dustbin 🗑️)
- Padho: *"`Integer` ya uska superclass — `Integer`, `Number`, ya `Object`."*
- Likhna safe hai kyunki `Integer` teeno mein fit hota hai. Padhna restricted hai kyunki guaranteed common type sirf `Object` hai.

```java
List<? super Integer> cons = new ArrayList<Number>();
cons.add(7);               // ✅ Integer, Number/Object dono mein fit hota hai
Object o = cons.get(0);    // ✅ sirf Object
Integer i = cons.get(0);   // ❌ error: incompatible types: CAP#1 cannot be converted to Integer
```

### PECS
> **P**roducer → **E**xtends, **C**onsumer → **S**uper

Sawaal: *"ye parameter mujhe data **de** raha hai ya **le** raha hai?"*

| Parameter ka kaam | Likho |
|---|---|
| isse **padhunga** (produce karta hai) | `? extends T` |
| isme **daalunga** (consume karta hai) | `? super T` |
| **dono** | koi wildcard nahi — plain `List<T>` |

```java
public static <T> void copyAll(List<? super T> dest, List<? extends T> src) {
    for (T item : src) dest.add(item);     // src produce, dest consume
}

List<Number> dest = new ArrayList<>();
copyAll(dest, List.of(1, 2));        // Integers into a List<Number>
copyAll(dest, List.of(3.5));         // Doubles too
dest                                 // → [1, 2, 3.5]
```
Dono ulte likh do to **kuch bhi** compile nahi hoga.

**JDK signatures — ab padhi ja sakti hain:**
```java
boolean addAll(Collection<? extends E> c)     // c se elements NIKALTE hain → producer → extends
void sort(Comparator<? super E> c)            // c ko elements JAATE hain   → consumer → super
static <T> void copy(List<? super T> dest, List<? extends T> src)
static <T> T max(Collection<? extends T> coll, Comparator<? super T> comp)
```
`sort(Comparator<? super E>)` ka concrete faayda: `List<Integer>` ko `Comparator<Number>` se sort kar sakte ho. `Comparator<E>` hota to sirf `Comparator<Integer>` chalta.

```java
List<Integer> nums = new ArrayList<>(List.of(30, 10, 20));
Comparator<Number> byDouble = Comparator.comparingDouble(Number::doubleValue);
nums.sort(byDouble);       // ✅ a Comparator<Number> sorts a List<Integer>
nums                       // → [10, 20, 30]
```

### `?` akela (unbounded)
- Kab: jab element ka type **chahiye hi nahi** — `size()`, `isEmpty()`, `clear()`, printing.
- **`List<?>` ≠ `List<Object>`**: `List<?> a = new ArrayList<String>();` ✅ par `List<Object> b = new ArrayList<String>();` ❌ (invariance). `List<?>` "koi bhi list", `List<Object>` "specifically Objects ki list".

```java
List<?> any = List.of("x", "y");   // "kisi bhi cheez ki list"
any.size();                // → 2   ✅ element type ki parwah nahi
any.add("x");              // ❌ CAP#1
any.add(null);             // ✅

static void printSize(List<?> list) {        // accepts List<String>, List<Integer>, ...
    System.out.println(list.size());
}
```

### Wildcard vs type parameter
- **Rule:** type ko **ek se zyada jagah** reference karna hai (parameter + return, ya do parameters ko tie karna) → type parameter `<T>`. Sirf ek parameter mein ek baar → wildcard `?`.
- **Return type mein wildcard mat likho** — `public List<? extends Number> getAll()` caller ko dard deta hai (wo usme kuch daal nahi sakta, aur wildcard uske code mein phailta jaata hai). `List<Number>` likho.
- Wildcards **public API** par matter karte hain; private helper mein `List<Integer>` theek hai.

```java
public static double sum(List<? extends Number> nums)              // wildcard — type ka naam nahi chahiye
public static <T extends Number> T first(List<T> nums)             // <T> — naam chahiye (return mein use hua)

public List<? extends Number> getAll()    // ❌ wildcard leaks into every caller
public List<Number> getAll()              // ✅
```

### Correction (2026-10-19)
`HashMap` ke "order nahi hota" ka example chunte waqt dhyaan rakho: chhote key-sets pe wo **sanyog se** insertion order jaisa dikh sakta hai. Verified:
```
insertion [banana, apple, cherry]      -> HashMap [banana, apple, cherry]    <- same, sanyog
insertion [zebra, apple, mango]        -> HashMap [zebra, apple, mango]      <- same, sanyog
insertion [PAID, SHIPPED, CANCELLED]   -> HashMap [CANCELLED, PAID, SHIPPED] <- farak dikhta hai
insertion [alpha, beta, gamma, delta]  -> HashMap [alpha, delta, beta, gamma]
```
Sabak dono taraf ka hai: order ki **koi guarantee nahi** — na insertion, aur na "definitely different". Isliye us order pe depend karna bug hai, aur usse *test* karna bhi bharosemand nahi.

---

## CP3 ke chaar naye sabak (2026-11-09)

### 1. Array runtime pe apna type yaad rakhta hai — generics nahi

- **Kya:** `new T[10]` illegal hai. Wajah "javac ko type nahi pata" **nahi** hai — javac ko pata hota hai, wahi generics ka point hai.
- **Kyun:** array ko **runtime** pe apna component type chahiye (har store pe check karta hai). Erasure runtime se `T` hata deta hai. Do requirements aapas mein ulti hain.
- **Saboot ki array ko runtime pe type pata hai:**

```java
Object[] objs = new String[2];
objs[0] = 42;        // javac khush hai (Object[] mein Object daal rahe hain)
// → Exception in thread "main" java.lang.ArrayStoreException: java.lang.Integer
//    array khud jaanta hai "main String[] hoon"
```

- **Isi ek wajah se teen cheezein illegal hain:**

```java
T[] arr = new T[10];                        // ❌ generic array creation
if (x instanceof List<String>) { }          // ❌ illegal generic type for instanceof
void f(List<String> a) {}                   // ❌
void f(List<Integer> a) {}                  //    same erasure → "name clash"
```

### 2. `final` immutability nahi deta — sirf reference lock karta hai

- **Kya:** `final` ka matlab hai "is variable ko dobara assign nahi kar sakte". Object ke **andar** kya ho raha hai, usse koi lena-dena nahi.
- **Kab fasta hai:** jab "config start pe bhar do, phir kabhi na badle" jaisa requirement ho — aur accidental change **pakda** jaana chahiye.

```java
private final Map<String, String> config = new HashMap<>();
config.put("x", "y");                    // ✅ chal gaya! final ne nahi roka
// config = new HashMap<>();             // ❌ yeh rukta hai — bas itna hi final karta hai

private final Map<String, String> safe = Map.copyOf(loadFromEnv());
safe.put("x", "y");                      // → UnsupportedOperationException ✅ pakda gaya
```

### 3. "Lookup" ke do matlab hote hain — scenario padh ke decide karo

```java
List<Long> ids = ...;      // 50,000 IDs
ids.get(5);                // by INDEX  → O(1)   ← isse "ArrayList fast hai" ka bhram hota hai
ids.contains(id);          // by VALUE  → O(n)   ← poora scan, 50,000 comparisons

Set<Long> idSet = new HashSet<>(ids);
idSet.contains(id);        // by VALUE  → O(1) average  ✅ hash se seedha bucket
```

**Rule:** container ka naam nahi, **operation** dekho. "Ye ID hai ya nahi?" lakhon baar = `HashSet`, hamesha.

### 4. `? super E` ka asli faayda (E3)

```java
static final Comparator<Person> BY_NAME = Comparator.comparing(p -> p.name);

List<Employee> emps = ...;        // Employee extends Person
emps.sort(BY_NAME);               // ✅ chalta hai, kyunki signature Comparator<? super E> hai
// → [alice/70, bob/80, carol/90]
```

- Comparator elements **consume** karta hai (leta hai, deta nahi) → PECS se `super`.
- Agar signature `Comparator<E>` hoti to `Comparator<Person>` reject hota (invariance) — aur har subclass ke liye alag comparator likhna padta.

---

## Mistakes I keep making (check these first)

```java
javac CartApp.java                       // ❌ from inside the package folder → javac -d out src/.../*.java
ids.remove(1)  // meant the value 1      // ❌ removes INDEX 1 → ids.remove(Integer.valueOf(1))
integerA == integerB                     // ❌ cache luck → integerA.equals(integerB)
items.set(idx, newItem)  // merge        // ❌ replaces, loses the old quantity → combine first, then set
return items;                            // ❌ live list leaks → return List.copyOf(items);
map.put(k, map.get(k) + 1);              // ❌ NPE first time → map.merge(k, 1, Integer::sum)
map.put(k, v)  // expecting "add"        // ❌ REPLACES an existing key's value
LinkedHashMap  // expecting sorted       // ❌ insertion order ≠ sorted → TreeMap / list.sort
for (String t : list) list.remove(t);    // ❌ CME (or a silent skip) → list.removeIf(...)
compareTo using only some fields         // ❌ TreeSet silently drops "equal" items → same fields as equals
return a - b;  // in compareTo           // ❌ overflow → Integer.compare(a, b)
list.contains(x)  // 50k ArrayList pe   // ❌ O(n) har call → new HashSet<>(list).contains(x)
private final Map<..> c = new HashMap<>() // ❌ final ≠ immutable → Map.copyOf(...)
"javac ko T ka type nahi pata"           // ❌ javac KO pata hai; JVM ko nahi (erasure)
ek line ka ek bug fix kiya → aage badh gaya // ❌ ek line pe do bug ho sakte hain (CP3 compareTo)
```
