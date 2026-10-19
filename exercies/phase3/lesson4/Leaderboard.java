import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Lesson 3.4 — REQUIRED debug exercise.
 *
 * Teen problems hain. Pehle (1)-(4) predict karo, PHIR chalao.
 * Leaderboard ka matlab: sabse zyada score pehle (descending).
 */
public class Leaderboard {

    static final class Player implements Comparable<Player> {
        private final String name;
        private final int score;

        Player(String name, int score) {
            this.name = name;
            this.score = score;
        }

        @Override
        public int compareTo(Player other) {
            int compare = Integer.compare(other.score, this.score);
            return compare != 0 ? compare : this.name.compareTo(other.name);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || !(o instanceof Player other))
                return false;
            Player p = (Player) o;
            return score == other.score && this.name.equals(p.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, score);
        }

        @Override
        public String toString() {
            return name + "(" + score + ")";
        }
    }

    public static void main(String[] args) {
        Player alice = new Player("alice", 50);
        Player bob = new Player("bob", 50);

        System.out.println(alice.equals(bob)); // (1) > false

        Set<Player> board = new TreeSet<>();
        board.add(alice);
        board.add(bob);
        board.add(new Player("carol", 90));
        System.out.println(board); // (2) > [carol(90), alice(50)]
        System.out.println(board.size()); // (3) > 2

        Player dave = new Player("dave", Integer.MAX_VALUE);
        Player eve = new Player("eve", -10);
        System.out.println(dave.compareTo(eve)); // (4) sign kya hona chahiye? > +ve

        Map<Player, String> ranks = new TreeMap<>();
        System.out.println(ranks);
    }
}
