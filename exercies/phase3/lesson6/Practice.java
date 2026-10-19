
import java.util.List;

public class Practice {
    public class Pair<K, V> {
        private final K key;
        private final V value;

        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKay() {
            return key;
        }

        public V getValue() {
            return value;
        }

        static <U> U ident(U u) {
            return u;
        }

        public static <T extends Number & Comparable<T>> T largest(List<T> items) {
            T best = items.get(0);
            for (T t : items) {
                if (t.compareTo(best) > 0) {
                    best = t;
                }
            }
            return best;
        }
    }

    public static void main(String[] args) {

    }
}
