import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class MethodRefDemo {

    record Order(String id, long amountPaise) {
    }

    public static void main(String[] args) {
        // 1. Static method: Class::staticMethod
        Function<String, Integer> parse1 = s -> Integer.parseInt(s);
        Function<String, Integer> parse2 = Integer::parseInt;
        System.out.println(parse2.apply("42") + 1); // 43

        // 2. Bound instance method: object::method (receiver fixed now)
        String prefix = "ORD-";
        Function<String, String> tag1 = s -> prefix.concat(s);
        Function<String, String> tag2 = prefix::concat;
        System.out.println(tag2.apply("17")); // ORD-17

        Consumer<String> print = System.out::println; // System.out is the object
        print.accept("via System.out::println");

        // 3. Unbound instance method: Class::instanceMethod (first arg becomes the
        // receiver)
        Function<String, Integer> len = String::length; // s -> s.length()
        BiFunction<String, String, Boolean> same = String::equalsIgnoreCase; // (a, b) -> a.equalsIgnoreCase(b)
        System.out.println(len.apply("chandan")); // 7
        System.out.println(same.apply("PAID", "paid")); // true

        // 4. Constructor: Class::new
        Supplier<List<String>> newList = ArrayList::new; // () -> new ArrayList<>()
        BiFunction<String, Long, Order> newOrder = Order::new; // (id, amt) -> new Order(id, amt)
        List<String> ids = newList.get();
        ids.add("A1");
        System.out.println(ids + " " + newOrder.apply("A9", 500L));

        // Phase 3 revision: Comparator.comparing takes a Function (the "key extractor")
        List<Order> orders = new ArrayList<>(List.of(
                new Order("A1", 49900), new Order("A2", 150000), new Order("A3", 9900)));
        orders.sort(Comparator.comparing(Order::amountPaise).reversed());
        orders.forEach(System.out::println);

        String p = "X-";
        Function<String, String> a = p::concat;
        Function<String, Integer> b = String::length;
        BiFunction<String, String, Integer> c = String::compareTo;
        Supplier<StringBuilder> d = StringBuilder::new;

        System.out.println(a.apply("1")); // (1)
        System.out.println(b.apply("hello") * 2); // (2)
        System.out.println(c.apply("b", "a") > 0); // (3)
        System.out.println(d.get() == d.get()); // (4)

        p = "Y-";
        System.out.println(a.apply("2")); // (5)

        // Function<String, Integer> e = Integer::length; // (6)

        String none = null;
        Supplier<String> g = () -> none.concat("x"); // (7) is line par crash hoga?
        Function<String, String> f = none::concat; // (8) is line par crash hoga?
    }
}
