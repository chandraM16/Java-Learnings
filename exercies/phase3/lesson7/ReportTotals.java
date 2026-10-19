import java.util.ArrayList;
import java.util.List;

/**
 * Lesson 3.7 — REQUIRED debug exercise.
 *
 * Ye file COMPILE NAHI HOTI. Teen problems hain, teeno wildcards ke hain.
 *
 * Kaam: javac ko khush karo — par `main` ko BADLE BINA.
 * Yaani methods ke signatures theek karo, call sites nahi.
 *
 * javac ke errors hi lesson hain — har error padho, phir fix karo.
 */
public class ReportTotals {

    /** Kisi bhi number list ka total. */
    public static double sum(List<Number> numbers) {
        double total = 0;
        for (Number n : numbers) {
            total += n.doubleValue();
        }
        return total;
    }

    /** src ke saare items dest mein daal do. */
    public static <T> void copyAll(List<? extends T> dest, List<? extends T> src) {
        for (T item : src) {
            dest.add(item);
        }
    }

    /** Agar list khaali ho to ek 0 daal do. */
    public static void addZeroIfEmpty(List<? extends Number> target) {
        if (target.isEmpty()) {
            target.add(0);
        }
    }

    public static void main(String[] args) {
        List<Integer> ints = new ArrayList<>(List.of(1, 2, 3));
        List<Double> doubles = new ArrayList<>(List.of(1.5, 2.5));

        System.out.println("(1) " + sum(ints));
        System.out.println("(2) " + sum(doubles));

        List<Number> all = new ArrayList<>();
        copyAll(all, ints);
        copyAll(all, doubles);
        System.out.println("(3) " + all);

        List<Integer> empty = new ArrayList<>();
        addZeroIfEmpty(empty);
        System.out.println("(4) " + empty);
    }
}
