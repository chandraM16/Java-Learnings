# Phase 3 — Collections & Generics — Notes

Concise revision notes for this phase only. Updated as topics are learned. See `notes.md` for the index of all phases.

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

## `List` / `ArrayList`

- Declare as the **interface**, create the **implementation**: `List<String> names = new ArrayList<>();` (same idea as composition in 2.7 — depend on the interface).
- **Why declare as `List`, not `ArrayList`** — the payoff is at API boundaries (parameters, return types, fields), not local variables:
  - Parameter `List<T>` accepts any list: `ArrayList`, `List.of(...)`, `LinkedList`. A parameter typed `ArrayList<T>` rejects `List.of(...)` at compile time.
  - Return type `List<T>` lets you change what you return without touching callers — e.g. `return List.copyOf(items);` (a read-only copy) compiles only if the return type is `List`, because `List.copyOf` doesn't return an `ArrayList`.
  - Same idea as `private final Notifier notifier` (2.7): callers depend on the contract, not the implementation.
  - Use `ArrayList` as a type only when you need an `ArrayList`-only method (e.g. `ensureCapacity`) — almost never.
- `<String>` = **generics**: the list only holds Strings, checked at compile time (`names.add(42)` → compile error). `<>` (diamond) infers the type on the right.
- Core methods:
```java
list.add("PAID");          // append
list.add(0, "NEW");        // insert at index (shifts the rest right)
list.get(1);               // read by index — IndexOutOfBoundsException if out of range
list.set(1, "SHIPPED");    // replace at index
list.size();               // NOT .length (arrays) and NOT .length() (String)
list.contains("PAID");     // uses equals() — your own classes need equals() (2.3)
list.indexOf("PAID");      // first index, or -1
list.remove("NEW");        // by value → returns boolean
list.remove(0);            // by index → returns the removed element
list.isEmpty();
System.out.println(list);  // [NEW, PAID] — readable, unlike arrays' [I@1b6d3586
```

| Array | `ArrayList` |
|---|---|
| fixed size | grows automatically |
| `arr.length` | `list.size()` |
| `arr[i]`, `arr[i] = x` | `list.get(i)`, `list.set(i, x)` |
| primitives allowed (`int[]`) | objects only (`List<Integer>`) |
| prints `[I@...` | prints `[a, b, c]` |

## Primitives → wrapper classes, autoboxing

- Generics only take objects: `List<int>` → compile error `unexpected type`. Use the wrapper: `int→Integer`, `long→Long`, `double→Double`, `boolean→Boolean`, `char→Character`.
- **Autoboxing**: `nums.add(5)` silently becomes `nums.add(Integer.valueOf(5))`. **Unboxing**: `int x = nums.get(0)`.
- **Trap 1 — `==` on wrappers**: `Integer a = 127, b = 127; a == b` → `true`, but `128 == 128` → `false`. Java caches `Integer` objects only for −128..127. Same accident as the String pool → always `.equals()`.
- **Trap 2 — `remove` overloads on `List<Integer>`**: `ids.remove(1)` removes **index** 1 (exact `int` match wins). To remove the value 1: `ids.remove(Integer.valueOf(1))`.
- **Trap 3**: unboxing `null` → `NullPointerException` (`Integer x = null; int y = x;`).

## Immutable lists

- `List.of("UPI", "CARD")` — fixed, unmodifiable. `add`/`remove`/`set` → `UnsupportedOperationException` at **run time** (compiles fine). Its `getMessage()` is `null`.
- Mutable copy: `new ArrayList<>(List.of(...))`.
- Defensive copy for a getter (2.2): `return List.copyOf(items);` — caller can't modify your internal list.

## Iterating

```java
for (String s : list) { ... }                       // for-each
for (int i = 0; i < list.size(); i++) { list.get(i); }
```
- Don't add/remove from a list while for-each-ing over it → `ConcurrentModificationException` (details 3.5).

## `ArrayList` vs `LinkedList` (3.2)

- `ArrayList` = growable `Object[]` + a `size` counter. Full hone pe `newCapacity = old + (old >> 1)` (~1.5x), naya array allocate, `System.arraycopy`, purana GC ko.
- `LinkedList` = doubly-linked `Node` chain (value + prev + next), sirf `first`/`last` pakde hue.
- **size ≠ capacity.** `new ArrayList<>(3)` → `size()` is 0. `get(0)` → `IndexOutOfBoundsException: Index 0 out of bounds for length 0`, kyunki `get` index ko **size** se check karta hai, capacity se nahi. Khaali slots "null values" nahi hain.

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

## Benchmarking Java (seekha 3.2 mein)
- **Dead-code elimination**: loop ka result use na ho to JIT poora loop hata deta hai → 0 ms naapte reh jaoge. Result ek static field (`blackhole`) mein jama karo.
- **JIT warm-up**: JVM pehle interpreted chalta hai, kuch hazaar calls ke baad machine code compile karta hai. Warm-up round ke bina jo test *pehle* chalta hai wo apne aap slow dikhta hai.
- Hand-rolled benchmarks sirf **bade** farak ke liye (1 ms vs 2587 ms). 10%-type differences ke liye **JMH** (Java Microbenchmark Harness) use hota hai — warm-up, DCE aur statistics khud handle karta hai.
- Timed loop ke andar `"TRK-" + i` jaisa kaam mat karo — wo string-building naapega, list nahi. Constant use karo.

## `HashMap` / `HashSet` internals (3.3)

### Mechanism
- `HashMap` andar ek array rakhta hai: `Node<K,V>[] table`, default length **16**, hamesha **power of 2**.
- Bucket index: `index = hash & (table.length - 1)` — power-of-2 length isliye, ki `& (n-1)` modulo ka kaam karta hai aur division se tezz hai.
- Spreading: `static final int hash(Object key) { int h = key.hashCode(); return h ^ (h >>> 16); }` — `& 15` sirf neeche ke 4 bits dekhta hai, to upar ke bits ki information neeche laayi jaati hai.
- **Collision** = do keys ka same bucket. Bucket mein ek chain (linked list) banti hai. Java 8+: ek bucket mein >8 nodes aur table ≥64 → chain **red-black tree** ban jaati hai → worst case O(n) ki jagah **O(log n)**.
- `get(k)` ka flow: `hashCode()` → bucket index → us bucket ki chain pe chalo → har node pe `equals()` → match ya `null`.

> **`hashCode()` batata hai kis bucket mein jaana hai. `equals()` batata hai us bucket mein kaunsa element sahi hai.** Dono chahiye, dono ka kaam alag.

### Load factor & resize
- `threshold = capacity × loadFactor` → default `16 × 0.75 = 12`.
- `if (++size > threshold) resize();` → **13vi** entry pe resize: capacity 16 → **32**, naya threshold **24**, aur saari entries ka index dobara nikalta hai (`& 31`) = **rehash**, O(n) (amortized, `ArrayList` jaisa).
- `new HashMap<>(1000)` se rehashes kam.
- **Natija:** iteration order = bucket order, aur resize pe **badal jaata hai**. Us order pe kabhi depend mat karo.
- Trap: chhote `Integer` keys ke saath order *sorted dikhta* hai (`Integer.hashCode()` wahi number hai, `5 & 15 = 5`). Sanyog hai, guarantee nahi.

### equals/hashCode contract (2.3 ka asli kaaran)
- `equals` se equal objects ka `hashCode` **bhi** equal hona chahiye. Warna → alag bucket → `equals` ka mauka hi nahi aata → **object map mein hai par pahunch se bahar**, bina kisi error ke.
- `equals` likha par `hashCode` nahi → `Object` ka identity-based default milta hai → har naya object alag bucket. `m.put(new K(42), v); m.get(new K(42))` → `null`, par `size()` → 1.
- Ulta safe hai: same `hashCode` + `equals` false → sirf collision (slow, galat nahi). Isliye `hashCode` "approximate" ho sakta hai, `equals` ke saath **inconsistent** nahi.
- Dono mein **wahi** fields use karo. `Objects.hash(a, b)` / `Objects.equals(x, y)`.
- **Mutable key trap:** key daal ke uska hash-wala field badal diya → entry purane bucket mein, lookup naye bucket mein → `get` `null`, `containsKey` `false`, nikaalna bhi mushkil. **Map keys immutable rakho** (`String`, `Integer`, `enum`, `final`-fields class).

### API
```java
map.get(k);                       // nahi mila → null, exception NAHI
map.getOrDefault(k, 0);           // null handling ka saaf tarika
map.put(k, v);                    // purani key pe REPLACE karta hai, size nahi badhta;
                                  // return = purani value (ya null agar key nayi thi)
map.putIfAbsent(k, v);
map.merge(k, 1, Integer::sum);                              // counting
map.computeIfAbsent(k, x -> new ArrayList<>()).add(item);    // grouping
for (Map.Entry<K,V> e : map.entrySet()) { e.getKey(); e.getValue(); }   // iterate — yahi use karo
```
- `keySet()` pe loop + andar `get(key)` = **do** lookups. `entrySet()` ek mein dono deta hai.
- `HashMap`: ek `null` key + kitni bhi `null` values allowed. (`ConcurrentHashMap` nahi.)
- Counting ka classic bug: `map.put(k, map.get(k) + 1)` → pehli baar `get` `null` → unboxing NPE (3.1 Trap 3). `getOrDefault(k, 0)` ya `merge` use karo.

### `HashSet` = `HashMap` with dummy values (literally)
```java
private transient HashMap<E,Object> map;
private static final Object PRESENT = new Object();
public boolean add(E e) { return map.put(e, PRESENT) == null; }
```
- Isliye: duplicates nahi (= key uniqueness), `contains` **O(1)** (vs `List` ki O(n)), koi order nahi, aur `equals`/`hashCode` ki wahi zaroorat.
- **`Map` ki keys ek `Set` hai** — isliye method ka naam `keySet()` hai. Set ka duplicate-reject karna aur Map ka key pe replace karna **ek hi** rule hai.

### Order: teen cheezein alag hain
| | order | lookup | kab |
|---|---|---|---|
| `HashMap` / `HashSet` | **koi nahi** (resize pe badalta hai) | O(1) | default |
| `LinkedHashMap` / `LinkedHashSet` | **insertion** | O(1) | deterministic output, caches/LRU |
| `TreeMap` / `TreeSet` | **sorted** | O(log n) | ranges, sorted reports (3.4) |

- `LinkedHashMap` `HashMap` ko extend karta hai + entries ke beech ek extra doubly-linked list (`before`/`after`) rakhta hai. Lookup abhi bhi O(1), thodi zyada memory, iteration us chain pe (buckets pe nahi).
- **`LinkedHashMap` ≠ sorted.** Insertion order aur sorted order do alag cheezein hain — randomness hatana sorting nahi hai.
- `LinkedHashSet` = duplicate-free **aur** insertion-ordered. Map ki value ke roop mein grouping ke liye perfect.

### Manual sorting (3.4 tak)
```java
List<Integer> keys = new ArrayList<>(map.keySet());
Collections.sort(keys);
for (int k : keys) { System.out.println(k + " -> " + map.get(k)); }
```

### Parsing note (Ex 3 se)
- `line.split(" ")` multiple spaces pe khaali strings deta hai (`"GET  /users"` → `["GET", "", "/users"]`). **`split("\\s+")`** use karo.
- Sabak: test data jaan-boojh ke ganda rakho (extra spaces, mixed casing) — chhote saaf dataset ne is bug ko chhupa diya tha.

## `TreeMap`/`TreeSet`, `Comparable`/`Comparator` (3.4)

### `TreeMap` / `TreeSet`
- Red-black tree (self-balancing BST). Left mein chhoti keys, right mein badi. **Hamesha sorted.**
- `get`/`put`/`contains` **O(log n)** (vs `HashMap` ki O(1)) — to `TreeMap` tabhi lo jab order ya range queries ki asli zaroorat ho.
- `null` key → **NPE** (compare kis se karega?). `HashMap` ek null key allow karta hai. Fark chubhta hai.
  - Exception: `new TreeMap<>(Comparator.nullsFirst(Comparator.naturalOrder()))` null key allow kar deta hai. Sirf tab jab `null` ka business matlab ho (jaise "no expiry"), galti chhupane ke liye nahi.
- Comparable class na ho aur comparator bhi na do → **doosre** `add` pe `ClassCastException` (pehle pe compare karne ki zaroorat nahi hoti).

### Range queries — `TreeMap` ki asli wajah
```java
tiers.firstKey(); tiers.lastKey();
tiers.floorKey(3000);      // 3000 se chhoti ya barabar sabse BADI key
tiers.ceilingKey(3000);    // 3000 se badi ya barabar sabse CHHOTI key
tiers.higherKey(k); tiers.lowerKey(k);     // strictly bada / chhota
tiers.headMap(20);         // 20 se pehle sab — upper bound EXCLUSIVE
tiers.tailMap(20);         // 20 se aage sab — lower bound INCLUSIVE
tiers.subMap(10, 30);      // 10 <= k < 30
tiers.descendingMap(); tiers.pollFirstEntry();
```
- **Asymmetric bounds** (Java ka har jagah ka pattern, jaise `substring`/`subList`/`i < n`): shuruaat shaamil, ant nahi. Override: `headMap(20, true)`.
- `floorKey`/`ceilingKey` **`null` de sakte hain** jab kuch qualify na kare → `floorEntry(x).getValue()` pe NPE. Hamesha check karo.
- `TreeSet` ke paas wahi: `first()`, `last()`, `floor()`, `ceiling()`, `higher()`, `lower()`, `headSet()`, `tailSet()`, `subSet()`, `descendingSet()`, `pollFirst()`.
- Backend use cases: price/rate tiers, time-series ("is timestamp se pehle ka last event"), rate-limit windows, version ranges.

### `Comparable` — natural order, class ke andar
```java
class Student implements Comparable<Student> {
    public int compareTo(Student other) { return Integer.compare(this.marks, other.marks); }
}
```
- Return value ka **number matter nahi karta, sirf sign**: negative → `this` pehle; zero → barabar; positive → `this` baad mein.
- Padhne ka trick: `a.compareTo(b)` ko `a - b` padho. (Likhne ka tarika **nahi** — neeche dekho.)
- Ek baar likha → `Collections.sort`, `TreeSet`, `TreeMap`, `list.sort(null)`, `PriorityQueue` sab chal gaye.
- Already `Comparable`: `String`, `Integer`/`Long`/`Double`, `Character`, `LocalDate`/`LocalDateTime`, aur **`enum`** (declaration order).
- **KABHI subtraction se compare mat karo.** `Integer.MAX_VALUE - (-10)` → overflow → `-2147483639` → sign ulta → pura order ulta, bina error. `Integer.compare(a, b)` andar `(a<b)?-1:((a==b)?0:1)` hai — koi arithmetic nahi, overflow ka sawaal nahi. `double` ke liye `Double.compare` (`NaN`/`-0.0` bhi handle karta hai).

### `Comparator` — order bahar se, kai ho sakte hain
```java
Comparator.comparing(Player::getName)          // object field
Comparator.comparingInt(Player::getScore)      // int — boxing bachata hai
.reversed()                                    // ulta
.thenComparing(...)                            // tie-breaker
Comparator.naturalOrder() / nullsFirst(...) / nullsLast(...)
list.sort(cmp);  new TreeSet<>(cmp);  new TreeMap<>(cmp);
```
- `TreeSet`/`TreeMap` ko comparator de do → class ka `compareTo` **bilkul ignore** ho jaata hai.
- Comparators ko `private static final` constants banao — naam se code padha jaata hai.
- **Kab kaunsa:** ek obvious default order → `Comparable`. Kai orders / class aapki nahi / order business rule hai → `Comparator`. Dono saath ho sakte hain.
- **Tie-breaker kab zaroori hai:** `List.sort` Java mein **stable** hai, to same input order pe output deterministic hota hai bina breaker ke bhi. Breaker tab chahiye jab (a) `TreeSet`/`TreeMap` ho — wahan barabar element **gir** jaata hai, ya (b) **input order hi random ho** — jaise `HashMap.entrySet()` se aaya data.

### THE TRAP — `TreeSet` `equals()` use NAHI karta
`equals` naam+score dekhe, `compareTo` sirf score dekhe → class apne aap se ulta keh rahi hai:
```
alice.equals(bob)    = false   <- ALAG hain
alice.compareTo(bob) = 0       <- BARABAR hain

HashSet : [bob(50), alice(50)]  size 2     <- sahi
TreeSet : [alice(50)]           size 1     <- bob CHUP-CHAAP gir gaya
tree.contains(bob) = true                  <- 'hai' bola, par andar alice hai
tree.remove(bob)   = true  -> []            <- ALICE delete ho gayi, bob ke naam pe
```
- `TreeSet` tree mein chalte hue compare karta hai; `compareTo == 0` hi uske liye "duplicate" ki definition hai. `equals` ko bulata hi nahi.
- **Fix:** `compareTo == 0` sirf tab jab `equals == true`. Practically — `compareTo` mein **wahi fields** jo `equals` mein hain, tie-breakers ke roop mein.

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

## `ArrayDeque`, `PriorityQueue`, CME, immutable collections (3.5)

### `Queue` / `Deque` / `ArrayDeque`
- `Deque` = "Double Ended QUEue" — dono siron se add/remove. Ek hi class se **queue (FIFO) aur stack (LIFO)** dono.
- `ArrayDeque` andar **circular array** hai, `head` aur `tail` index ke saath. Front se nikaalne pe kuch shift **nahi** hota — bas `head` aage badhta hai, aur index array ke end pe wrap ho jaata hai. **Dono sire O(1)**, aur array hone ki wajah se cache-friendly bhi.
- Yahi 3.2 ka jawab: `ArrayList.remove(0)` O(n) tha (shifting), `ArrayDeque.pollFirst()` O(1) hai — aur `LinkedList` se bhi tezz, kyunki pointer chasing nahi.
- **`ArrayDeque` lo; `Stack` aur `LinkedList` mat lo.** `Stack` Java 1.0 legacy hai — har method `synchronized` (single-thread mein bekaar overhead) aur uska iteration order **ulta** (bottom→top). Oracle docs khud `ArrayDeque` recommend karti hain.

**Do method families — har operation ke do version:**
| Kaam | Exception phenkta hai | Special value deta hai |
|---|---|---|
| daalo | `add(x)` → `IllegalStateException` | `offer(x)` → `false` |
| nikaalo | `remove()` → `NoSuchElementException` | `poll()` → `null` |
| dekho | `element()` → `NoSuchElementException` | `peek()` → `null` |
- Khaali hona **normal** hai → `poll`/`peek`. Khaali hona **bug** hai → `remove`/`element`, taaki turant pata chale.
- `push`/`pop` = `addFirst`/`pollFirst` (stack ki tarah).

### `PriorityQueue`
- Sabse **chhota** element pehle nikalta hai (`compareTo`/`Comparator` se decide), chahe kab daala gaya ho. Comparator se reverse: `new PriorityQueue<>(Comparator.reverseOrder())`.
- Andar ek **binary heap** — array mein store kiya tree, jiska **ek hi** guarantee hai: *har parent apne children se chhota hai.* Poora array sorted **nahi** hota.
- `add`/`poll` **O(log n)**, `peek` **O(1)** (sabse chhota hamesha index 0).
- **THE TRAP — iteration/`toString` sorted NAHI hoti.** Sirf `poll()` ka order sorted hai (har poll heap ko dobara arrange karta hai).
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

**Char fixes:**
```java
pending.removeIf(t -> t.endsWith("-skip"));                   // 1. default choice
Iterator<String> it = pending.iterator();                     // 2. remove ke saath kuch aur bhi karna ho
while (it.hasNext()) { if (...) it.remove(); }                //    it.remove() dono counters update karta hai
for (String t : new ArrayList<>(pending)) { pending.remove(t); }  // 3. copy pe iterate
for (int i = pending.size() - 1; i >= 0; i--) { ... }         // 4. index loop ULTA
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

**Factory gotchas:**
```java
List.of("a", null)        // NPE — null allowed nahi
Set.of("a", "a")          // IllegalArgumentException: duplicate element — RUN time
Map.of(k,v, ...)          // max 10 jode; zyada ke liye Map.ofEntries(entry(...), ...)
Map.of(...)               // order per-JVM-run randomized, HashMap se bhi zyada random
```
- `Arrays.asList(arr)` = **array ke upar wrapper**. Size fixed → `add` UOE; par `set` chalta hai **aur wo write asli array mein jaata hai**. `List.of` nahi hai — legacy code mein milega, naya code mein `List.of`.
- **Compile vs run time test:** *"javac ko ye jaanne ke liye code chalana padega?"* Haan → run time (`Set.of` duplicate, `List.of` null). Sirf types/syntax se pata chal jaata → compile time (`List.of(42)` ko `List<String>` mein daalna).

## Generics (3.6)

### Purpose
- **Ek line:** run-time `ClassCastException` ko **compile-time error** bana dena, aur casts hatana.
- Asli faayda casts bachana nahi — galti **wahan pakdi jaaye jahan ki gayi**, 20 min debugging ke baad doosri file mein nahi.

### Generic class / method
```java
class Box<T> { private T value; void set(T v){} T get(){ return value; } }
class Pair<K, V> { ... }

public static <T> List<T> firstTwo(List<T> items) { ... }   // <T> RETURN TYPE SE PEHLE
```
- Conventions: `T` Type, `E` Element, `K`/`V` Key/Value, `R` Return. (Sirf convention, par code inhi se padha jaata hai.)
- Type javac **infer** karta hai argument se — wahi inference jo `new ArrayList<>()` ke diamond mein hai.
- **`static` method class ka `T` use nahi kar sakta** — `T` instance pe decide hota hai (`new Box<String>()`), static instance ke bina chalta hai. Apna parameter declare karo: `static <U> U identity(U x)`.

### Bounded type parameters
```java
<T extends Comparable<T>>            // apne aap se compare ho sake
<T extends Number>                   // Number ya subclass
<T extends Number & Comparable<T>>   // DONO — class pehle, interfaces baad mein
```
- Bina bound `T` javac ke liye **`Object`** hai → `item.compareTo(...)` → `cannot find symbol`. Bound hi wo cheez hai jo `T` pe methods kholti hai.
- Generics mein interface ke liye bhi **`extends`** likha jaata hai (`implements` keyword nahi hota).
- Bound utna tight rakho jitna zaroori — na khaali `<T>` (kaam nahi karega), na bina wajah `<T extends Number>`.

### Type erasure — sabse important
> `<String>` ek **instruction hai javac ke liye**, `.class` file mein ek **property nahi**. javac padhta hai, check karta hai, cast daalta hai, aur **mita deta hai**. JVM ko kabhi pata nahi chalta.

```java
new ArrayList<String>().getClass() == new ArrayList<Integer>().getClass()   // true
new ArrayList<String>().getClass().getName()                               // java.util.ArrayList
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
void f(List<String>); void f(List<Integer>);   // error: name clash ... same erasure
T.class  /  new T()  /  catch (MyEx<T> e)  /  static field of type T    // sab illegal
```
Ek hi wajah chaaron ke liye: run time pe `T` ka koi nishaan nahi bachta.

### Raw types & heap pollution
- `List list = new ArrayList();` legal hai (purane code ke liye) par saari safety chali jaati hai. javac: `warning: [unchecked] ...` = *"main is call ki type-safety check nahi kar paaya, tum apne risk pe ho."*
- **Heap pollution** = typed collection mein galat type ka object ghus jaana. Crash **injection site pe nahi**, us jagah hota hai jahan javac ne apna chhipa cast daala tha (jaise for-each ke andar):
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
```java
List<Object> objs = names;   // AGAR allowed hota...
objs.add(42);                // ...ye legal hota
String s = names.get(0);     // ...aur yahan CCE
```
Java pehle step pe rok deta hai taaki teesra kabhi na aaye. Safe hal: `? extends` / `? super` (3.7).

### TS comparison
TS generics bhi **erased** hain (`Array<string>`/`Array<number>` runtime pe dono `Array`), to basic model wahi hai. Farak: TS structural + optional, `any` se kahin bhi nikal sakte ho; Java nominal + mandatory, aur raw type (`any` ka kareeb) pe javac **warning** deta hai — chup-chaap jaane nahi deta.

## Wildcards, `? extends`/`? super`, PECS (3.7)

### Problem — invariance
`List<Integer>` `List<Number>` ka subtype **nahi** hai, to `sum(List<Number>)` `List<Integer>` accept nahi karega. Teen copies likhne ki jagah wildcard lagao.

### `? extends T` — PRODUCER (nal 🚰)
```java
List<? extends Number> prod = ints;
Number n = prod.get(0);    // ✅ jo bhi ho, Number to hai
prod.add(7);               // ❌ error: int cannot be converted to CAP#1
prod.add((Integer) 7);     // ❌ wahi error — cast se bhi nahi
prod.add(null);            // ✅ sirf null
```
- Padho: *"kisi **ek** aise type ki list jo `Number` ya uska subclass hai — par pata nahi kaunsa."*
- **`CAP#1` kya hai:** javac `? extends Number` dekh ke ek **capture** type variable banata hai jiske baare mein use sirf itna pata hai "ye `Number` extend karta hai"; **identity pata nahi**. To `add(7)` pe sochta hai *"CAP#1 `Integer` ho sakta hai, `Double`, ya `BigDecimal` — nahi pata, to reject."* `null` chalta hai kyunki wo har reference type mein fit hota hai.

### `? super T` — CONSUMER (dustbin 🗑️)
```java
List<? super Integer> cons = new ArrayList<Number>();
cons.add(7);               // ✅ Integer, Number/Object dono mein fit hota hai
Object o = cons.get(0);    // ✅ sirf Object
Integer i = cons.get(0);   // ❌ error: CAP#1 cannot be converted to Integer
```
- Padho: *"`Integer` ya uska superclass — `Integer`, `Number`, ya `Object`."*
- Likhna safe hai kyunki `Integer` teeno mein fit hota hai. Padhna restricted hai kyunki guaranteed common type sirf `Object` hai.

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

### `?` akela (unbounded)
```java
List<?> any = someList;    // "kisi bhi cheez ki list"
any.size();                // ✅ element type ki parwah nahi
any.add("x");              // ❌ CAP#1
any.add(null);             // ✅
```
- Kab: jab element ka type **chahiye hi nahi** — `size()`, `isEmpty()`, `clear()`, printing.
- **`List<?>` ≠ `List<Object>`**: `List<?> a = new ArrayList<String>();` ✅ par `List<Object> b = new ArrayList<String>();` ❌ (invariance). `List<?>` "koi bhi list", `List<Object>` "specifically Objects ki list".

### Wildcard vs type parameter
```java
public static double sum(List<? extends Number> nums)              // wildcard — type ka naam nahi chahiye
public static <T extends Number> T first(List<T> nums)             // <T> — naam chahiye (return mein use hua)
```
- **Rule:** type ko **ek se zyada jagah** reference karna hai (parameter + return, ya do parameters ko tie karna) → type parameter `<T>`. Sirf ek parameter mein ek baar → wildcard `?`.
- **Return type mein wildcard mat likho** — `public List<? extends Number> getAll()` caller ko dard deta hai (wo usme kuch daal nahi sakta, aur wildcard uske code mein phailta jaata hai). `List<Number>` likho.
- Wildcards **public API** par matter karte hain; private helper mein `List<Integer>` theek hai.

### Correction (2026-10-19)
`HashMap` ke "order nahi hota" ka example chunte waqt dhyaan rakho: chhote key-sets pe wo **sanyog se** insertion order jaisa dikh sakta hai. Verified:
```
insertion [banana, apple, cherry]      -> HashMap [banana, apple, cherry]    <- same, sanyog
insertion [zebra, apple, mango]        -> HashMap [zebra, apple, mango]      <- same, sanyog
insertion [PAID, SHIPPED, CANCELLED]   -> HashMap [CANCELLED, PAID, SHIPPED] <- farak dikhta hai
insertion [alpha, beta, gamma, delta]  -> HashMap [alpha, delta, beta, gamma]
```
Sabak dono taraf ka hai: order ki **koi guarantee nahi** — na insertion, aur na "definitely different". Isliye us order pe depend karna bug hai, aur usse *test* karna bhi bharosemand nahi.
