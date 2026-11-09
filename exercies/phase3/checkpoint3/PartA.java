import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;

public class PartA {
    static final class Item {
        final String code;

        Item(String code) {
            this.code = code;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof Item i && i.code.equals(code);
        }

        @Override
        public String toString() {
            return code;
        }
    }

    public static void main(String[] args) {
        // A1
        Map<String, Integer> m = new HashMap<>();
        m.put("x", 1);
        m.put("y", 2);
        m.put("x", 7);
        System.out.println("A1 " + m.size() + " " + m.get("x"));

        // A2
        List<String> sized = new ArrayList<>(5);
        System.out.println("A2 " + sized.size());

        // A3 — do cheezein: pehle toString, phir poll ka order
        Queue<Integer> pq = new PriorityQueue<>();
        pq.add(50);
        pq.add(10);
        pq.add(90);
        pq.add(20);
        System.out.println("A3a " + pq);
        StringBuilder sb = new StringBuilder();
        while (!pq.isEmpty())
            sb.append(pq.poll()).append(" ");
        System.out.println("A3b " + sb);

        // A4
        System.out.println("A4 " + (new ArrayList<String>().getClass() == new ArrayList<Integer>().getClass()));

        // A5 — teen maps, same teen puts, teen outputs
        for (Map<String, Integer> map : List.of(
                new HashMap<String, Integer>(),
                new LinkedHashMap<String, Integer>(),
                new TreeMap<String, Integer>())) {
            map.put("PAID", 1);
            map.put("SHIPPED", 2);
            map.put("REFUNDED", 3);
            map.put("CANCELLED", 4);
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
        t.put(10, "a");
        t.put(20, "b");
        t.put(30, "c");
        System.out.println("A9 " + t.floorKey(25) + " " + t.ceilingKey(35) + " " + t.headMap(20));

        // A10 — dono mein se ek exception dega, ek nahi
        List<String> l1 = new ArrayList<>(List.of("p", "q"));
        try {
            for (String s : l1)
                l1.set(0, "X");
            System.out.println("A10a ok " + l1);
        } catch (RuntimeException e) {
            System.out.println("A10a " + e.getClass().getSimpleName());
        }
        List<String> l2 = new ArrayList<>(List.of("p", "q", "r", "s"));
        try {
            for (String s : l2)
                l2.remove(s);
            System.out.println("A10b ok " + l2);
        } catch (RuntimeException e) {
            System.out.println("A10b " + e.getClass().getSimpleName());
        }
    }
}
// A1 2 7
// A2 0
// A3a [10, 20, 90, 50]
// A3b 10 20 50 90
// A4 true
// A5 HashMap -> Random order
// A5 LinkedHashMap {PAID=1, SHIPPED=2, REFUNDED=3, CANCELLED=4}
// A5 TreeMap {CANCELLED=4, PAID=1, REFUNDED=3, SHIPPED=2}
// A6 2
// A7a [10, 30]
// A7b [20, 30]
// A8 [a, b] [a]
// A9 20 null {10=a}
// A10a ok [X, q]
// A10b ConcurrentModificationException