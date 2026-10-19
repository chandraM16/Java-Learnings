
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Set;

public class ArrayDequePractice {
    public static void main(String[] args) {
        Deque<String> dq = new ArrayDeque<>();
        System.out.println(dq);
        dq.add("a");
        dq.add("b");
        dq.addAll(List.of("w", "e"));
        dq.add("e");
        System.out.println(dq.pollFirst());
        System.out.println(dq);
        List<String> l = Arrays.asList("a", "b");
        l.set(0, "z");
        System.out.println(l);
        // l.add("c");
        System.out.println(l);
        Set.of("a","b","a");
    }
}
