import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class OrderSummary {

    record Order(String id, String customer, String city, long amountPaise) {}

    // Only edit the lines under (1), (2), (3). Everything else is off-limits.
    public static void main(String[] args) {
        List<Order> orders = List.of(
                new Order("A1", "meera", "Pune", 49900),
                new Order("A2", "zoya", "Nagpur", 150000),
                new Order("A3", "meera", "Pune", 275000),
                new Order("A4", "arjun", "Mumbai", 9900),
                new Order("A5", "zoya", "Delhi", 60000),
                new Order("A6", "arjun", "Bengaluru", 12000));

        // (1) number of orders per customer, alphabetical
        Map<String, Integer> ordersPerCustomer = orders.stream()
                .collect(Collectors.groupingBy(Order::customer, TreeMap::new, Collectors.counting()));
        System.out.println("orders per customer: " + ordersPerCustomer);

        // (2) revenue (paise) per city, alphabetical by city
        Map<String, Long> revenuePerCity = orders.stream()
                .collect(Collectors.groupingBy(Order::city, Collectors.summingLong(Order::amountPaise)));
        System.out.println("revenue per city: " + revenuePerCity);

        // (3) total spent (paise) per customer
        Map<String, Long> spent = orders.stream()
                .collect(Collectors.toMap(Order::customer, Order::amountPaise));
        System.out.println("meera spent: " + spent.get("meera") + ", zoya spent: " + spent.get("zoya"));
    }
}
