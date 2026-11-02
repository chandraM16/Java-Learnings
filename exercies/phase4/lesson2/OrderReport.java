import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class OrderReport {

    record Order(String id, long amountPaise, String status) {
    }

    // Only edit the lines under (1), (2), (3). Everything else is off-limits.
    public static void main(String[] args) {
        List<Order> orders = List.of(
                new Order("A1", 49900, "PAID"),
                new Order("A2", 150000, "CANCELLED"),
                new Order("A3", 275000, "PAID"),
                new Order("A4", 9900, "PAID"));

        List<String> quantities = List.of("2", "5");

        // (1)
        Function<String, Integer> parseQty = Integer::parseInt;

        int qtyTotal = 0;
        for (String q : quantities) {
            qtyTotal += parseQty.apply(q);
        }
        System.out.println("qty total: " + qtyTotal);

        // (2) biggest order first
        Comparator<Order> byAmountDesc = Comparator.comparing(Order::amountPaise).reversed();

        List<Order> sorted = new ArrayList<>(orders);
        sorted.sort(byAmountDesc);
        for (Order o : sorted) {
            System.out.println(o.id() + " " + o.amountPaise());
        }

        // (3)
        List<String> paidIds = new ArrayList<>();
        Consumer<String> collect = paidIds::add;
        

        for (Order o : orders) {
            if (o.status().equals("PAID")) {
                collect.accept(o.id());
            }
        }
        System.out.println("paid ids: " + paidIds);
    }
}
