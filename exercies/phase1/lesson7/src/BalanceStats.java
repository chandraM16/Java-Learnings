
import java.util.Arrays;

public class BalanceStats {
    public static void main(String[] args) {
        int[] dailyChanges = { -120, -45, -300, -80, -16 };

        int total = 0;
        for (int i = 0; i < dailyChanges.length; i++) {
            total += dailyChanges[i];
        }

        double average = (double) total / dailyChanges.length;

        int best = Integer.MIN_VALUE;
        for (int change : dailyChanges) {
            if (change > best) {
                best = change;
            }
        }

        int[] copyForSorting = Arrays.copyOf(dailyChanges, dailyChanges.length);
        int[] sortedArr = copyForSorting;
        Arrays.sort(sortedArr);

        System.out.println("Changes: " + Arrays.toString(dailyChanges));
        System.out.println("Total: " + total + ", average: " + String.format("%.1f", average));
        System.out.println("Best day: " + best);
        System.out.println("Sorted: " + Arrays.toString(sortedArr));
        System.out.println("Original order: " + Arrays.toString(dailyChanges));
    }
}
