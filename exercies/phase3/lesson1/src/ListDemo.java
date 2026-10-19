import java.util.ArrayList;
import java.util.List;

class Tag {
    private final String name;

    Tag(String name) {
        this.name = name;
    }
}

public class ListDemo {
    public static void main(String[] args) {
        List<String> queue = new ArrayList<>(List.of("A", "B", "C"));
        queue.add(1, "X");
        queue.set(0, "Z");
        System.out.println("1: " + queue); // Z, X, B, C
        System.out.println("2: " + queue.size() + " " + queue.indexOf("C")); // 4 3

        List<Integer> nums = new ArrayList<>(List.of(5, 6, 7));
        nums.remove(1);
        System.out.println("3: " + nums); // 5, 7
        nums.remove(Integer.valueOf(5));
        System.out.println("4: " + nums); // 7

        Integer x = 1000, y = 1000;
        System.out.println("5: " + (x == y) + " " + x.equals(y)); // false, true

        List<Tag> tags = new ArrayList<>();
        tags.add(new Tag("java"));
        System.out.println("6: " + tags.contains(new Tag("java"))); // false

        List<String> fixed = List.of("UPI");
        fixed.add("CARD"); // error 'UnsupportedOperationException'
        System.out.println("7: " + fixed);
    }
}