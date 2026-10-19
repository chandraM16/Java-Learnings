import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Lesson 3.2 — Ex 3. ArrayList vs LinkedList, naap ke dekho.
 *
 * Chalane ka tarika:
 * javac ListBench.java && java ListBench
 *
 * Note: ~10-20 second lagenge. Sabse slow hissa test B ka LinkedList column hai
 * —
 * aur wahi is poore lesson ka point hai.
 */
public class ListBench {

    private static final int APPEND_COUNT = 200_000;
    private static final int GET_COUNT = 20_000;
    private static final int FRONT_COUNT = 20_000;

    /**
     * Ek hi constant string, taaki hum list ki cost naapein — string banane ki
     * nahi.
     */
    private static final String ITEM = "TRK-000000";

    /**
     * Isme har test ka result jama hota hai. Kyun? Agar hum loop ka result kahin
     * use na karein, to JIT compiler poora loop "dead code" samajh ke hata sakta
     * hai
     * aur hum 0 ms naapte reh jayenge. Static field mein likhna usse rokta hai.
     */
    private static long blackhole = 0;

    public static void main(String[] args) {
        warmUp();
        System.out.println("test  arraylist(ms)  linkedlist(ms)");
        printRow("A", appendTest(new ArrayList<>(), APPEND_COUNT),
                appendTest(new LinkedList<>(), APPEND_COUNT));
        printRow("B", getTest(new ArrayList<>(), APPEND_COUNT, GET_COUNT),
                getTest(new LinkedList<>(), APPEND_COUNT, GET_COUNT));
        printRow("C", frontInsertTest(new ArrayList<>(), FRONT_COUNT),
                frontInsertTest(new LinkedList<>(), FRONT_COUNT));
    }

    // ---- tests: dhyaan do, parameter `List<String>` hai, `ArrayList` nahi ----
    // Isi wajah se neeche ka ek hi method dono implementations pe chal jata hai.
    // Agar parameter `ArrayList<String>` hota, to LinkedList pass hi nahi hoti.

    /** Test A — end mein append. */
    private static long appendTest(List<String> list, int count) {
        long start = System.nanoTime();
        for (int i = 0; i < count; i++) {
            list.add(ITEM);
        }
        long elapsed = System.nanoTime() - start;
        blackhole += list.size();
        return toMillis(elapsed);
    }

    /** Test B — beech wale index ko baar-baar padho. (fill timed nahi hai) */
    private static long getTest(List<String> list, int fillSize, int getCount) {
        fill(list, fillSize);
        int mid = list.size() / 2;
        long start = System.nanoTime();
        for (int i = 0; i < getCount; i++) {
            blackhole += list.get(mid).length();
        }
        long elapsed = System.nanoTime() - start;
        return toMillis(elapsed);
    }

    /** Test C — index 0 pe insert. */
    private static long frontInsertTest(List<String> list, int count) {
        long start = System.nanoTime();
        for (int i = 0; i < count; i++) {
            list.add(0, ITEM);
        }
        long elapsed = System.nanoTime() - start;
        blackhole += list.size();
        return toMillis(elapsed);
    }

    // ---- helpers ----

    /**
     * Pehli baar jab JVM koi method chalata hai, wo interpreted mode mein hota hai;
     * kuch hazaar calls ke baad JIT usse machine code mein compile karta hai.
     * Bina warm-up, pehla test apne aap slow dikhta hai. Isliye chhote scale pe
     * sab kuch ek baar chala lete hain aur result phenk dete hain.
     */
    private static void warmUp() {
        for (int round = 0; round < 3; round++) {
            appendTest(new ArrayList<>(), APPEND_COUNT / 40);
            appendTest(new LinkedList<>(), APPEND_COUNT / 40);
            getTest(new ArrayList<>(), APPEND_COUNT / 40, GET_COUNT / 40);
            getTest(new LinkedList<>(), APPEND_COUNT / 40, GET_COUNT / 40);
            frontInsertTest(new ArrayList<>(), FRONT_COUNT / 40);
            frontInsertTest(new LinkedList<>(), FRONT_COUNT / 40);
        }
    }

    private static void fill(List<String> list, int count) {
        for (int i = 0; i < count; i++) {
            list.add(ITEM);
        }
    }

    private static long toMillis(long nanos) {
        return nanos / 1_000_000;
    }

    private static void printRow(String test, long arrayListMs, long linkedListMs) {
        System.out.printf("%-6s%-15d%d%n", test, arrayListMs, linkedListMs);
    }
}

/*
 * Ex 3, requirement 5 — ye teen lines AAP bharo (numbers dekhne ke baad):
 *
 * A winner: ____ kyunki ____
 * B winner: ____ kyunki ____
 * C winner: ____ kyunki ____
 */
