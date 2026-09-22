public class OrderStatusReport {

    private  static String normalizeStatus(String status) {
        return status.strip().toLowerCase();
    }

    private  static double percentage(int part, int total) {
        if (total == 0) {
            return 0.0;
        }
        double percentage = (double) part / total * 100;
        return percentage;
    }

    private  static String formatSummary(int total, int paid, double paidPercentage, int unknown) {
        String formattedSummarySrt = total + " orders processed, " + paid + " paid ("
                + String.format("%.2f", paidPercentage) + "%), " + unknown + " unrecognized";

        return formattedSummarySrt;

    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please provide some arguments");
            return;
        }

        int noOfPaidOrders = 0;
        int noOfPendingOrders = 0;
        int noOfShippedOrders = 0;
        int noOfCancelledOrders = 0;
        int noOfUnknownOrderStatus = 0;

        for (String orderStatusString : args) {
            switch (normalizeStatus(orderStatusString)) {
                case "paid" -> noOfPaidOrders++;
                case "pending" -> noOfPendingOrders++;
                case "cancelled" -> noOfCancelledOrders++;
                case "shipped" -> noOfShippedOrders++;
                default -> noOfUnknownOrderStatus++;
            }
        }

        int totalStatus = args.length;

        double paidOrdersInPer = percentage(noOfPaidOrders, totalStatus);

        String summary = formatSummary(totalStatus, noOfPaidOrders, paidOrdersInPer, noOfUnknownOrderStatus);

        System.out.println(summary);
    }
}
