import java.util.ArrayList;
import java.util.List;

/**
 * Lesson 3.2 — REQUIRED debug exercise.
 * Teen problems hain is class mein. Dhoondho, fix karo, aur neeche ke 2 sawaal
 * answer karo.
 */
public class ShipmentQueue {

    private final List<String> pending = new ArrayList<>();

    public void enqueue(String trackingId) {
        pending.add(trackingId);
    }

    public String longestTrackingId() {
        String longest = "";
        for (int i = 0; i < pending.size(); i++) {
            String id = pending.get(i);
            if (id.length() > longest.length()) {
                longest = id;
            }
        }
        return longest;
    }

    public void dropCancelled(List<String> cancelled) {
        for (int i = 0; i < cancelled.size(); i++) {
            String idToBeCancelled = cancelled.get(i);
            int idx = pending.indexOf(idToBeCancelled);
            if (idx != -1) {
                pending.remove(idx);
            }

        }
    }

    public List<String> getPending() {
        return List.copyOf(pending);
    }

    public static void main(String[] args) {
        ShipmentQueue q = new ShipmentQueue();
        q.enqueue("TRK-1");
        q.enqueue("TRK-2");
        q.enqueue("TRK-3");
        q.enqueue("TRK-4");

        q.dropCancelled(List.of("TRK-2", "TRK-3"));
        System.out.println(q.getPending()); // [TRK-1, TRK-3, TRK-4]

        q.getPending().clear();
        System.out.println(q.getPending());
    }
}
