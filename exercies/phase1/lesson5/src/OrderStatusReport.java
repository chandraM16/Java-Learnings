public class OrderStatusReport {
    static final int PAID = 0, PENDING = 1, SHIPPED = 2, CANCELLED = 3, UNKNOWN = 4;

    private static void printCounts(int[] counts) {
        System.out.println("paid: " + counts[PAID]);
        System.out.println("pending: " + counts[PENDING]);
        System.out.println("cancelled: " + counts[CANCELLED]);
        System.out.println("shipped: " + counts[SHIPPED]);
        System.out.println("unknown: " + counts[UNKNOWN]);
        
    }

    private static int[] countStatuses(String[] statuses) {
        int[] counts = new int[5];
        for (String raw : statuses) {
            switch (normalizeStatus(raw)) {
                case "paid" -> counts[PAID]++;
                case "pending" -> counts[PENDING]++;
                case "cancelled" -> counts[CANCELLED]++;
                case "shipped" -> counts[SHIPPED]++;
                default -> counts[UNKNOWN]++;
            }
        }
        return counts;
    }

    private static String normalizeStatus(String status) {
        return status.strip().toLowerCase();
    }

    private static double percentage(int part, int total) {
        if (total == 0) {
            return 0.0;
        }
        double percentage = (double) part / total * 100;
        return percentage;
    }

    private static String formatSummary(int total, int paid, int unknown) {
        return "%d orders processed, %d paid (%.2f%%), %d unrecognized"
                .formatted(total, paid, percentage(paid, total), unknown);
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please provide some arguments");
            return;
        }

        int[] statusCount = countStatuses(args);
        printCounts(statusCount);

        int totalStatus = args.length;

        String summary = formatSummary(totalStatus, statusCount[PAID], statusCount[UNKNOWN]);

        System.out.println(summary);
    }
}
