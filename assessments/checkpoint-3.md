# Checkpoint 3: Collections & Generics (Lessons 3.1–3.7)

Date issued: 2026-10-19

---

## Rules

- **Part A:** likhne se pehle **predict** karo, phir run karo. Dono bhejo — prediction aur actual.
- **Part B:** sirf padh ke jawab do, run mat karo (yahi point hai).
- **Part C:** compile aur run karo, asli output paste karo.
- **Part D:** ek-ek line. Choice + ek chhoti wajah.
- **Part E:** apne shabdon mein, 2 sentence har ek. Notes se copy nahi.

Jo part ka jawab nahi doge, uske points 0 hain (CP2 ki tarah) — par is baar koi build part nahi hai, wo Project 1 mein hai.

## Scoring (40 points)

| Part | Kya | Points |
|---|---|---|
| A | Predict output | 10 |
| B | javac kaunsi lines reject karega | 6 |
| C | Debug `InventoryService` (12 bugs) | 12 |
| D | Sahi collection chuno | 6 |
| E | Apne shabdon mein samjhao | 6 |

**Pass:** kam se kam **32/40**, **aur** Part C mein kam se kam **8/12**, **aur** Part E poora.

---

## Part A: Predict (10 points, 1 each)

Save as `exercies/phase3/checkpoint3/PartA.java`. Har line ka output predict karo, phir chalao.

```java
import java.util.*;

public class PartA {
    static final class Item {
        final String code;
        Item(String code) { this.code = code; }
        @Override public boolean equals(Object o) {
            return o instanceof Item i && i.code.equals(code);
        }
        @Override public String toString() { return code; }
    }

    public static void main(String[] args) {
        // A1
        Map<String, Integer> m = new HashMap<>();
        m.put("x", 1); m.put("y", 2); m.put("x", 7);
        System.out.println("A1 " + m.size() + " " + m.get("x"));

        // A2
        List<String> sized = new ArrayList<>(5);
        System.out.println("A2 " + sized.size());

        // A3  — do cheezein: pehle toString, phir poll ka order
        Queue<Integer> pq = new PriorityQueue<>();
        pq.add(50); pq.add(10); pq.add(90); pq.add(20);
        System.out.println("A3a " + pq);
        StringBuilder sb = new StringBuilder();
        while (!pq.isEmpty()) sb.append(pq.poll()).append(" ");
        System.out.println("A3b " + sb);

        // A4
        System.out.println("A4 " + (new ArrayList<String>().getClass()
                                 == new ArrayList<Integer>().getClass()));

        // A5 — teen maps, same teen puts, teen outputs
        for (Map<String, Integer> map : List.of(
                new HashMap<String, Integer>(),
                new LinkedHashMap<String, Integer>(),
                new TreeMap<String, Integer>())) {
            map.put("PAID", 1); map.put("SHIPPED", 2);
            map.put("REFUNDED", 3); map.put("CANCELLED", 4);
            System.out.println("A5 " + map.getClass().getSimpleName() + " " + map);
        }

        // A6 — Item ke paas equals hai, hashCode nahi
        Set<Item> set = new HashSet<>();
        set.add(new Item("ABC"));
        set.add(new Item("ABC"));
        System.out.println("A6 " + set.size());

        // A7
        List<Integer> ids = new ArrayList<>(List.of(10, 20, 30));
        ids.remove(1);
        System.out.println("A7a " + ids);
        List<Integer> ids2 = new ArrayList<>(List.of(10, 20, 30));
        ids2.remove(Integer.valueOf(10));
        System.out.println("A7b " + ids2);

        // A8
        List<String> inner = new ArrayList<>(List.of("a"));
        List<String> viewed = Collections.unmodifiableList(inner);
        List<String> copied = List.copyOf(inner);
        inner.add("b");
        System.out.println("A8 " + viewed + " " + copied);

        // A9
        TreeMap<Integer, String> t = new TreeMap<>();
        t.put(10, "a"); t.put(20, "b"); t.put(30, "c");
        System.out.println("A9 " + t.floorKey(25) + " " + t.ceilingKey(35) + " " + t.headMap(20));

        // A10 — dono mein se ek exception dega, ek nahi
        List<String> l1 = new ArrayList<>(List.of("p", "q"));
        try {
            for (String s : l1) l1.set(0, "X");
            System.out.println("A10a ok " + l1);
        } catch (RuntimeException e) {
            System.out.println("A10a " + e.getClass().getSimpleName());
        }
        List<String> l2 = new ArrayList<>(List.of("p", "q", "r", "s"));
        try {
            for (String s : l2) l2.remove(s);
            System.out.println("A10b ok " + l2);
        } catch (RuntimeException e) {
            System.out.println("A10b " + e.getClass().getSimpleName());
        }
    }
}
```

---

## Part B: javac kaunsi lines reject karega? (6 points, 1 each)

**Run mat karo.** Har line ke liye: **reject** ya **ok**. (Jo reject hon, unke liye ek-do shabd mein wajah bhi.)

```java
List<Integer> ints = new ArrayList<>(List.of(1, 2, 3));
List<Number> nums = new ArrayList<>();

List<Number> b1 = ints;                             // B1
List<? extends Number> b2 = ints;                   // B2
b2.add(4);                                          // B3
List<? super Integer> b4 = nums;  b4.add(4);        // B4
Object b5 = (ints instanceof List<Integer> ? 1 : 0);// B5
class Holder<T> { T[] make() { return new T[4]; } } // B6
```

---

## Part C: Debug `InventoryService` (12 points)

File: `exercies/phase3/checkpoint3/InventoryService.java`

Isme **baarah bugs** hain, Phase 3 ke poore range se (aur ek Phase 2 ka purana wala bhi). Teen category ke hain:
- kuch **compile** hone hi nahi denge
- kuch **run time** pe exception denge
- kuch **chup-chaap galat jawab** denge — koi error nahi, bas galat number

`main` badalne ki zaroorat nahi — bugs baaki code mein hain. (Ek jagah `main` mein ek line add karni pad sakti hai agar aap chaaho, par signature fixes se kaam ho jaata hai.)

```bash
cd "exercies/phase3/checkpoint3" && javac -Xlint:unchecked InventoryService.java && java InventoryService
```

Bhejo:
1. Fixed file.
2. Asli output.
3. Har bug ke liye **ek line**: `line N — kya galat tha`. (Category ka naam likhne ki zaroorat nahi — bas kya galat tha.)

Scoring: **har bug 1 point (12 total)**. Sahi final output khud saboot hai ki fixes kaam kar gaye.

**Hint, kyunki kuch bugs chup-chaap hain:** output dekh ke ye chaar sawaal poochho —
- `ABC-1` ka stock 10 aur phir 3 aaya; final kya **hona** chahiye?
- `skus` mein teen SKU daale; kitne dikhne chahiye?
- `hashSkus` mein ek hi SKU do baar daala; size kya hona chahiye?
- restock plan ka order urgency ke hisaab se badhta hua hai ya nahi?

---

## Part D: Sahi collection chuno (6 points, 1 each)

Har scenario ke liye ek collection + ek chhoti wajah (ek line).

| # | Scenario |
|---|---|
| D1 | 50,000 user IDs; bas ye poochhna hai "ye ID list mein hai?" — lakhon baar |
| D2 | HTTP request ke headers, aur response mein wahi order chahiye jisme aaye the |
| D3 | Pricing tiers (0, 1000, 5000) aur poochhna hai "₹3000 kharch pe kaunsa tier?" |
| D4 | Background jobs, sabse urgent pehle chalana hai |
| D5 | Ek sliding window — naye events peeche add, purane aage se nikaalo, 100k/sec |
| D6 | Ek config map jo service start pe bharti hai aur uske baad kabhi nahi badalti; galti se badalne ki koshish pakdi jaani chahiye |

---

## Part E: Apne shabdon mein (6 points, 2 each)

Do sentence har ek. Notes se copy nahi — apne shabdon mein.

**E1.** `new ArrayList<String>()` aur `new ArrayList<Integer>()` ka `getClass()` same aata hai. Iska naam kya hai, aur iski wajah se Java mein `new T[10]` kyun nahi likh sakte?

**E2.** `HashSet` aur `TreeSet` duplicate pehchaanne ke liye **alag** methods use karte hain. Dono kaunse, aur `TreeSet` wala contract tootne pe kya hota hai (exception aati hai ya nahi)?

**E3.** `list.sort(...)` ki signature `Comparator<? super E>` hai, `Comparator<E>` nahi. PECS ke hisaab se `super` kyun — aur isse aapko kya **extra** karne ko milta hai jo `Comparator<E>` se nahi milta?

---

## Submission

Part A ke 10 jawab (prediction + actual), Part B ke 6, Part C ka fixed file + output + 12 lines, Part D ke 6, Part E ke 3.

Ek saath bhejo ya part-by-part — jaise theek lage.
