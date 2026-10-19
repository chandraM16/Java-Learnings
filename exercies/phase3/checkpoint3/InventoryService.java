import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

/**
 * Checkpoint 3 — Part C (debug).
 *
 * Is file mein BAARAH bugs hain, Phase 3 ke poore range se (3.1-3.7),
 * aur ek Phase 2 ka purana wala bhi.
 * Kuch compile time pe pakde jaayenge, kuch run time pe, aur kuch
 * CHUP-CHAAP galat jawab denge (koi error nahi).
 *
 * Kaam: baarah bugs dhoondho, fix karo, aur sahi output nikaalo.
 * `main` ko badalne ki zaroorat nahi — bugs baaki code mein hain.
 */
public class InventoryService {

    /** Ek SKU (stock keeping unit). */
    static final class Sku implements Comparable<Sku> {
        private final String code;
        private final int warehouseId;

        Sku(String code, int warehouseId) {
            this.code = code;
            this.warehouseId = warehouseId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Sku other)) return false;
            return warehouseId == other.warehouseId && Objects.equals(code, other.code);
        }

        @Override
        public int compareTo(Sku other) {
            return this.warehouseId - other.warehouseId;
        }

        @Override
        public String toString() {
            return code + "@w" + warehouseId;
        }
    }

    static final class Restock {
        private final String sku;
        private final int urgency;

        Restock(String sku, int urgency) {
            this.sku = sku;
            this.urgency = urgency;
        }

        int getUrgency() { return urgency; }

        @Override
        public String toString() { return sku + "/u" + urgency; }
    }

    private final Map stockBySku = new HashMap();
    private final List<String> auditLog = new LinkedList<>();

    public void receive(String sku, int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("quantity must be > 0");
        }
        stockBySku.put(sku, quantity);
        auditLog.add("received " + quantity + " of " + sku);
    }

    public int stockOf(String sku) {
        return (int) stockBySku.get(sku);
    }

    /** auditLog mein se "received" wali saari lines hata do. */
    public void purgeReceipts() {
        for (String line : auditLog) {
            if (line.startsWith("received")) {
                auditLog.remove(line);
            }
        }
    }

    /** Sabse lambi audit line. */
    public String longestAuditLine() {
        String longest = "";
        for (int i = 0; i < auditLog.size(); i++) {
            if (auditLog.get(i).length() > longest.length()) {
                longest = auditLog.get(i);
            }
        }
        return longest;
    }

    /** Restocks ko urgency order mein print karo (sabse urgent pehle). */
    public void printRestockPlan(Queue<Restock> queue) {
        System.out.println("restock plan:");
        for (Restock r : queue) {
            System.out.println("  " + r);
        }
    }

    /** Kisi bhi number list ka total quantity. */
    public static double totalQuantity(List<Number> quantities) {
        double total = 0;
        for (Number n : quantities) {
            total += n.doubleValue();
        }
        return total;
    }

    public List<String> getAuditLog() {
        return Collections.unmodifiableList(auditLog);
    }

    public static void main(String[] args) {
        InventoryService svc = new InventoryService();

        svc.receive("ABC-1", 10);
        svc.receive("ABC-2", 5);
        svc.receive("ABC-1", 3);
        System.out.println("(1) stock ABC-1 = " + svc.stockOf("ABC-1"));
        System.out.println("(2) audit size  = " + svc.getAuditLog().size());

        Set<Sku> skus = new TreeSet<>();
        skus.add(new Sku("ABC-1", 1));
        skus.add(new Sku("ABC-2", 1));
        skus.add(new Sku("ABC-3", 2));
        System.out.println("(3) skus        = " + skus);

        Set<Sku> hashSkus = new HashSet<>();
        hashSkus.add(new Sku("ABC-1", 1));
        hashSkus.add(new Sku("ABC-1", 1));
        System.out.println("(4) hashSkus    = " + hashSkus.size());

        Queue<Restock> plan = new PriorityQueue<>(
                java.util.Comparator.comparingInt(Restock::getUrgency));
        plan.add(new Restock("ABC-1", 5));
        plan.add(new Restock("ABC-2", 1));
        plan.add(new Restock("ABC-3", 9));
        plan.add(new Restock("ABC-4", 2));
        svc.printRestockPlan(plan);

        List<Integer> intQuantities = List.of(1, 2, 3);
        System.out.println("(5) total ints  = " + totalQuantity(intQuantities));

        svc.purgeReceipts();
        System.out.println("(6) after purge = " + svc.getAuditLog());
    }
}
