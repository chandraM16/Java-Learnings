import java.util.List;
import java.util.function.Predicate;

public class OrderStats {

    record Order(String id, long amountPaise, String status, List<String> items) {
    }

    // Only edit the lines under (1), (2), (3). Everything else is off-limits.
    public static void main(String[] args) {
        List<Order> orders = List.of(
                new Order("A1", 49900, "PAID", List.of("pen", "book")),
                new Order("A2", 150000, "CANCELLED", List.of("laptop")),
                new Order("A3", 275000, "PAID", List.of("book", "bag", "pen")),
                new Order("A4", 9900, "PAID", List.of("pen", "book")));
        Predicate<Order> isPaid = o -> o.status().equals("PAID");

        // (1) count and total of paid orders
        long count = orders.stream().filter(isPaid).count();
        long totalPaise = orders.stream().filter(isPaid).mapToLong(Order::amountPaise).sum();
        System.out.println("paid: " + count + ", total Rs " + totalPaise / 100);

        // (2) ids of paid orders
        List<String> paidIds = orders.stream().filter(isPaid).map(Order::id).toList();
        System.out.println("ids: " + paidIds);

        // (3) how many different items appear across ALL orders
        long distinctItems = orders.stream().map(Order::items).flatMap(List::stream).distinct().count();
        System.out.println("distinct items: " + distinctItems);
    }
}
