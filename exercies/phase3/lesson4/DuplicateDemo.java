import java.util.Comparator;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/** Concept 3 — HashSet aur TreeSet duplicate ALAG tarike se pehchante hain. */
public class DuplicateDemo {

    static final class Player implements Comparable<Player> {
        final String name;
        final int score;

        Player(String name, int score) {
            this.name = name;
            this.score = score;
        }

        /** equals: naam AUR score dono. */
        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (!(o instanceof Player p))
                return false;
            return score == p.score && name.equals(p.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, score);
        }

        /** compareTo: SIRF score. <-- yahi inconsistency hai */
        @Override
        public int compareTo(Player other) {
            return Integer.compare(this.score, other.score);
        }

        @Override
        public String toString() {
            return name + "(" + score + ")";
        }
    }

    public static void main(String[] args) {
        Player alice = new Player("alice", 50);
        Player bob = new Player("bob", 50); // same score, alag naam

        System.out.println("Do ALAG log, same score:");
        System.out.println("  alice.equals(bob)    = " + alice.equals(bob) + "   <- ALAG hain");
        System.out.println("  alice.compareTo(bob) = " + alice.compareTo(bob) + "   <- BARABAR hain");
        System.out.println("  ye dono ek doosre se ULTA keh rahe hain. Yahi bug hai.");
        System.out.println();

        Set<Player> hash = new HashSet<>();
        hash.add(alice);
        hash.add(bob);
        System.out.println("HashSet  (equals + hashCode dekhta hai)");
        System.out.println("  content = " + hash + "  size = " + hash.size());
        System.out.println();

        Set<Player> tree = new TreeSet<>();
        tree.add(alice);
        tree.add(bob);
        System.out.println("TreeSet  (SIRF compareTo dekhta hai)");
        System.out.println("  content = " + tree + "  size = " + tree.size() + "   <- bob GAYAB");
        System.out.println("  tree.contains(bob) = " + tree.contains(bob)
                + "   <- 'haan hai' bola, par andar alice hai!");
        System.out.println("  tree.remove(bob)   = " + tree.remove(bob));
        System.out.println("  remove ke baad     = " + tree + "   <- alice mar gayi, bob ke naam pe");
        System.out.println();

        Comparator<Player> consistent = Comparator.comparingInt((Player p) -> p.score)
                .thenComparing(p -> p.name);
        Comparator<Player> consistent1 = Comparator.comparingInt((Player p) -> p.score);

        Set<Player> fixed = new TreeSet<>(consistent);
        fixed.add(alice);
        fixed.add(bob);
        System.out.println("FIX: compareTo mein tie-breaker (score, phir name)");
        System.out.println("  content = " + fixed + "  size = " + fixed.size());
    }
}
