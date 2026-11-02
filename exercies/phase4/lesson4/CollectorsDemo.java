import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class CollectorsDemo {

    record Order(String id, String customer, String city, long amountPaise, String status) {}

    public static void main(String[] args) {
        List<Order> orders = List.of(
                new Order("A1", "meera", "Pune", 49900, "PAID"),
                new Order("A2", "zoya", "Delhi", 150000, "CANCELLED"),
                new Order("A3", "meera", "Pune", 275000, "PAID"),
                new Order("A4", "arjun", "Mumbai", 9900, "PAID"),
                new Order("A5", "zoya", "Delhi", 60000, "PAID"));

        // 0. Phase 3 way: group by hand
        Map<String, List<Order>> byHand = new HashMap<>();
        for (Order o : orders) {
            byHand.computeIfAbsent(o.customer(), k -> new java.util.ArrayList<>()).add(o);
        }

        // 1. groupingBy: same thing, one line -> Map<K, List<T>>
        Map<String, List<Order>> byCustomer = orders.stream()
                .collect(Collectors.groupingBy(Order::customer));
        System.out.println(byCustomer.get("meera").size());                  // 2

        // 2. groupingBy + downstream collector: what to do with each group
        Map<String, Long> countPerCustomer = orders.stream()
                .collect(Collectors.groupingBy(Order::customer, TreeMap::new, Collectors.counting()));
        System.out.println(countPerCustomer);                                // {arjun=1, meera=2, zoya=2}

        Map<String, Long> revenuePerCity = orders.stream()
                .collect(Collectors.groupingBy(Order::city, TreeMap::new, Collectors.summingLong(Order::amountPaise)));
        System.out.println(revenuePerCity);                                  // {Delhi=210000, Mumbai=9900, Pune=324900}

        Map<String, List<String>> idsPerCustomer = orders.stream()
                .collect(Collectors.groupingBy(Order::customer, TreeMap::new,
                        Collectors.mapping(Order::id, Collectors.toList())));
        System.out.println(idsPerCustomer);                                  // {arjun=[A4], meera=[A1, A3], zoya=[A2, A5]}

        // 3. partitioningBy: split into exactly two groups, true/false
        Map<Boolean, List<String>> bigOrSmall = orders.stream()
                .collect(Collectors.partitioningBy(o -> o.amountPaise() >= 100000,
                        Collectors.mapping(Order::id, Collectors.toList())));
        System.out.println(bigOrSmall);                                      // {false=[A1, A4, A5], true=[A2, A3]}

        // 4. toMap: one key -> one value
        Map<String, Long> amountById = orders.stream()
                .collect(Collectors.toMap(Order::id, Order::amountPaise));
        System.out.println(amountById.get("A3"));                            // 275000

        // 5. toMap with a duplicate key -> crash, unless you say how to merge
        try {
            orders.stream().collect(Collectors.toMap(Order::customer, Order::amountPaise));
        } catch (IllegalStateException e) {
            System.out.println("crash: " + e.getMessage());
        }
        Map<String, Long> spentPerCustomer = orders.stream()
                .collect(Collectors.toMap(Order::customer, Order::amountPaise, Long::sum));
        System.out.println(spentPerCustomer.get("zoya"));                    // 210000

        // 6. joining
        String ids = orders.stream().map(Order::id).collect(Collectors.joining(", "));
        System.out.println(ids);                                             // A1, A2, A3, A4, A5
    }
}
