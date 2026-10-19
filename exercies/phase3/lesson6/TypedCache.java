import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Lesson 3.6 — REQUIRED debug exercise.
 *
 * Teen problems hain, teeno "generics ka faayda phenk diya" category ke hain.
 * Pehle (1)-(6) predict karo, PHIR chalao.
 *
 * Note: javac warnings dega. Unhe poora padhne ke liye:
 * javac -Xlint:unchecked TypedCache.java
 */
public class TypedCache<K, V> {

    private final Map<K, V> cache = new HashMap<>();

    public void put(K key, V value) {
        cache.put(key, value);
    }

    public V get(K key) {
        return cache.get(key);
    }

    public static <T extends Comparable<T>> T largest(List<T> items) {
        T best = items.get(0);
        for (T item : items) {
            if (item.compareTo(best) > 0) {
                best = item;
            }
        }
        return best;
    }

    public List<K> keys() {
        return new ArrayList<>(cache.keySet());
    }

    public static void main(String[] args) {
        TypedCache<String, String> c = new TypedCache<>();
        c.put("name", "alice");

        System.out.println("(1) " + c.get("name")); // name
        try {
            System.out.println("(2) " + c.get("age"));
        } catch (RuntimeException e) {
            System.out.println("(2) " + e.getClass().getSimpleName() + ": " + e.getMessage()); // error
        }

        System.out.println("(3) " + largest(List.of(3, 9, 5))); // 9
        System.out.println("(4) " + largest(List.of("a", "c", "b"))); // c

        List<String> keys = c.keys();
        System.out.println("(6) " + keys); // idk
    }
}
