import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Lesson 3.5 — REQUIRED debug exercise.
 *
 * Teen problems hain. Pehle (1)-(4) predict karo, PHIR chalao.
 * Task ka priority: chhota number = zyada urgent.
 */
public class TaskRunner {

    static final class Task implements Comparable<Task> {
        private final String name;
        private final int priority;

        Task(String name, int priority) {
            this.name = name;
            this.priority = priority;
        }

        @Override
        public int compareTo(Task other) {
            return Integer.compare(this.priority, other.priority);
        }

        @Override
        public String toString() {
            return name + "/p" + priority;
        }
    }

    private final List<String> pending = new ArrayList<>(List.of("a", "b-skip", "c", "d-skip", "e"));
    private final Queue<Task> queue = new PriorityQueue<>();

    /** Jo tasks skip karne hain unhe hata do. */
    public void dropSkipped() {
        pending.removeIf(t -> t.endsWith("-skip"));
    }

    /** Tasks ko priority order mein print karo (sabse urgent pehle). */
    public void printPlan() {
        queue.add(new Task("email", 5));
        queue.add(new Task("payment", 1));
        queue.add(new Task("report", 9));
        queue.add(new Task("alert", 2));

        System.out.println("plan:");
        while (!queue.isEmpty()) {
            System.out.println("  " + queue.poll());
        }
    }

    /** Caller ko pending tasks do — par read-only. */
    public List<String> getPending() {
        return List.copyOf(pending);
    }

    public static void main(String[] args) {
        TaskRunner runner = new TaskRunner();

        List<String> snapshot = runner.getPending();
        System.out.println(snapshot); // (1) > [a, b-skip, c, d-skip, e]

        runner.pending.add("f-late"); // internal list badal diya
        System.out.println(snapshot); // (2) snapshot pe asar padega? > [a, b-skip, c, d-skip, e, f-late]

        runner.printPlan(); // (3) order kya aayega? > payment, alert, email, report

        runner.dropSkipped(); // (4) > error CME

        System.out.println(runner.getPending());
    }
}
