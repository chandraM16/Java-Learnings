import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class OrderRules {

    record Order(String id, long amountPaise, String status) {
    }

    // Bug zone: only edit the marked lines. main() is off-limits.
    public static void main(String[] args) {
        List<Order> orders = List.of(
                new Order("A1", 49900, "PAID"),
                new Order("A2", 150000, "CANCELLED"),
                new Order("A3", 275000, "PAID"),
                new Order("A4", 9900, "PAID"));

        Predicate<Order> isPaid = o -> o.status().equals("PAID");
        Function<Order, Long> toRupees = o -> o.amountPaise() / 100;
        Supplier<String> header = () -> "== Paid orders ==";
        Consumer<Order> printLine = o -> System.out.println(o.id() + " Rs " + toRupees.apply(o));

        // (1)
        System.out.println(header.get());
        // (2)
        int paidCount = 0;
        for (Order o : orders) {
            if (isPaid.test(o)) {
                printLine.accept(o);
            }
        }

        // (3)

        long total = 0;
        for (Order o : orders) {
            if (isPaid.test(o)) {
                total += o.amountPaise();
            }
        }

        // (4)
        final long total1 = total;
        Supplier<String> footer = () -> "Total: Rs " + total1 / 100;
        System.out.println(footer.get());
    }
}
