import java.util.List;
import java.util.stream.Stream;

public class StreamDemo {

    record Order(String id, long amountPaise, String status) {
    }

    public static void main(String[] args) {
        List<Order> orders = List.of(
                new Order("A1", 49900, "PAID"),
                new Order("A2", 150000, "CANCELLED"),
                new Order("A3", 275000, "PAID"),
                new Order("A4", 9900, "PAID"));

        // 1. Loop vs stream: total of paid orders
        long loopTotal = 0;
        for (Order o : orders) {
            if (o.status().equals("PAID")) {
                loopTotal += o.amountPaise();
            }
        }
        long streamTotal = orders.stream() // source
                .filter(o -> o.status().equals("PAID")) // intermediate (lazy)
                .mapToLong(Order::amountPaise) // intermediate (lazy)
                .sum(); // terminal -> runs everything
        System.out.println(loopTotal + " " + streamTotal);

        // 2. map + toList: transform each element
        List<String> ids = orders.stream().map(Order::id).toList();
        System.out.println(ids);

        // 3. Laziness: watch the order of the prints
        Stream<Order> pipeline = orders.stream()
                .filter(o -> {
                    System.out.println("  filter " + o.id());
                    return o.status().equals("PAID");
                })
                .map(o -> {
                    System.out.println("  map    " + o.id());
                    return o;
                });
        System.out.println("pipeline built, nothing ran yet");
        Order first = pipeline.findFirst().get();
        System.out.println("first paid: " + first.id());

        // 4. reduce: fold everything into one value
        long max = orders.stream().map(Order::amountPaise).reduce(0L, Long::max);
        System.out.println("max: " + max);

        // 5. flatMap: one element -> many elements, flattened
        List<List<String>> carts = List.of(List.of("pen", "book"), List.of("bag"), List.of());
        List<String> allItems = carts.stream().flatMap(List::stream).toList();
        System.out.println(allItems);

        // 6. A stream is single-use
        Stream<String> once = ids.stream();
        System.out.println(once.count());
        try {
            once.count();
        } catch (IllegalStateException e) {
            System.out.println("second use: " + e.getMessage());
        }

        List<String> words = List.of("apple", "kiwi", "banana", "fig");
        System.out.println(words.stream().filter(s -> s.length() > 3).map(String::toUpperCase).toList());

        System.out.println(words.stream().map(String::length).reduce(0, Integer::sum));

        System.out.println(words.stream().sorted().limit(2).toList());

        words.stream().peek(w -> {
            System.out.println("saw " + w);
        }).map(String::length);

        System.out.println("After 4");

        Stream<String> s = words.stream().filter((w) -> {
            System.out.println("Checking " + w);
            return w.startsWith("b");
        });
        System.out.println("Before");
        System.out.println(s.findFirst().get());

        System.out.println(List.of(List.of(1, 2), List.of(3), List.<Integer>of())
                .stream().flatMap(List::stream).map(n -> n * 10).toList()); // (6)

        System.out.println(words);
        // s.count();
        words.stream().toList().add("x");
    }
}
