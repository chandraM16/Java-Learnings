import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Lesson 3.3 — REQUIRED debug exercise.
 *
 * Teen problems hain + ek purani aadat jo aap pehle bhi kar chuke ho.
 * Pehle main ka output line-by-line predict karo, PHIR chalao.
 */
public class OrderIndex {

    /** Map ki key. */
    static final class CustomerId {
        private final String region;
        private final int number;

        CustomerId(String region, int number) {
            this.region = region;
            this.number = number;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (!(o instanceof CustomerId other))
                return false;
            return number == other.number && Objects.equals(region, other.region);
        }

        @Override
        public int hashCode() {
            return Objects.hash(region, number);
        }

        @Override
        public String toString() {
            return region + "-" + number;
        }
    }

    private final Map<CustomerId, String> latestOrder = new HashMap<>();

    private final Map<String, Integer> statusCounts = new HashMap<>();

    public void addOrder(CustomerId customer, String orderId) {
        latestOrder.put(customer, orderId);
    }

    public void countStatus(String status) {
        statusCounts.put(status, statusCounts.getOrDefault(status, 0) + 1);
    }

    public String findLatestOrder(CustomerId customer) {
        return latestOrder.get(customer);
    }

    public Map<String, Integer> getStatusCounts() {
        return Map.copyOf(statusCounts);
    }

    public static void main(String[] args) {
        OrderIndex index = new OrderIndex();
        index.addOrder(new CustomerId("IN", 42), "ORD-1001");

        // Wahi customer, par naya object
        CustomerId sameCustomer = new CustomerId("IN", 42);

        System.out.println(index.latestOrder.get(sameCustomer)); // (1)
        System.out.println(index.latestOrder.containsKey(sameCustomer)); // (2)
        System.out.println(index.findLatestOrder(sameCustomer)); // (3)
        System.out.println(index.latestOrder.size()); // (4)

        index.countStatus("PAID"); // (5)
        System.out.println(index.getStatusCounts()); // (6)
    }
}
